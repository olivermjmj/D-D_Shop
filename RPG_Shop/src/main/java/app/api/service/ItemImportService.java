package app.api.service;

import app.api.client.Dnd5eClient;
import app.api.dto.EquipmentListDTO;
import app.api.dto.ImportedItemDTO;
import app.config.ThreadPoolConfig;
import app.dao.ItemCategoryDAO;
import app.dao.ItemDAO;
import app.dao.SupplierDAO;
import app.entities.Item;
import app.entities.ItemCategory;
import app.entities.Supplier;
import app.exceptions.ApiImportException;
import app.exceptions.DatabaseException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class ItemImportService {

    private static final String EXTERNAL_SOURCE = "DND5E";
    private static final String DEFAULT_CATEGORY = "Unknown";
    private static final String DEFAULT_SUPPLIER = "DND 5e API";

    private final Dnd5eClient client;
    private final ItemDAO itemDAO;
    private final ItemCategoryDAO itemCategoryDAO;
    private final SupplierDAO supplierDAO;
    private final ExecutorService executorService;

    public ItemImportService(Dnd5eClient client, ItemDAO itemDAO, ItemCategoryDAO itemCategoryDAO, SupplierDAO supplierDAO) {
        this(client, itemDAO, itemCategoryDAO, supplierDAO, ThreadPoolConfig.getExecutor());
    }

    public ItemImportService(
            Dnd5eClient client,
            ItemDAO itemDAO,
            ItemCategoryDAO itemCategoryDAO,
            SupplierDAO supplierDAO,
            ExecutorService executorService
    ) {
        this.client = Objects.requireNonNull(client);
        this.itemDAO = Objects.requireNonNull(itemDAO);
        this.itemCategoryDAO = Objects.requireNonNull(itemCategoryDAO);
        this.supplierDAO = Objects.requireNonNull(supplierDAO);
        this.executorService = Objects.requireNonNull(executorService);
    }

    public CompletableFuture<List<Item>> importEquipment() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                EquipmentListDTO equipmentListDTO = client.fetchEquipmentList();

                if (equipmentListDTO == null ||
                        equipmentListDTO.getResults() == null ||
                        equipmentListDTO.getResults().isEmpty()) {
                    return List.of();
                }

                return equipmentListDTO.getResults()
                        .stream()
                        .filter(itemRef -> itemRef != null)
                        .filter(itemRef -> itemRef.getUrl() != null && !itemRef.getUrl().isBlank())
                        .map(this::importSingleItem)
                        .toList();

            } catch (Exception e) {
                throw new ApiImportException("Failed to import DND equipment list", e);
            }
        }, executorService);
    }

    private Item importSingleItem(EquipmentListDTO.ItemRefDTO itemRef) {
        try {
            ImportedItemDTO importedItemDTO = client.fetchEquipmentDetail(itemRef.getUrl());

            if (importedItemDTO == null) {
                throw new ApiImportException("DND API returned null item");
            }

            if (importedItemDTO.getExternalId() != null) {
                Optional<Item> existingItem = itemDAO.getByExternalIdAndSource(
                        importedItemDTO.getExternalId(),
                        EXTERNAL_SOURCE
                );

                if (existingItem.isPresent()) {
                    return existingItem.get();
                }
            }

            ItemCategory category = getOrCreateCategory(importedItemDTO);
            Supplier supplier = getOrCreateSupplier();

            Item item = new Item();

            item.setName(importedItemDTO.getName());
            item.setDescription(buildDescription(importedItemDTO));
            item.setBasePrice(calculatePrice(importedItemDTO));

            item.setExternalId(importedItemDTO.getExternalId());
            item.setExternalSource(EXTERNAL_SOURCE);

            item.setItemCategory(category);
            item.setSupplier(supplier);

            return itemDAO.create(item);

        } catch (DatabaseException e) {
            throw new ApiImportException("Database error while importing DND item: " + itemRef.getUrl(), e);

        } catch (Exception e) {
            throw new ApiImportException("Failed to import DND item: " + itemRef.getUrl(), e);
        }
    }

    private ItemCategory getOrCreateCategory(ImportedItemDTO importedItemDTO) {

        try {

            String categoryName = DEFAULT_CATEGORY;

            if (importedItemDTO.getCategory() != null &&
                    importedItemDTO.getCategory().getName() != null &&
                    !importedItemDTO.getCategory().getName().isBlank()) {
                categoryName = importedItemDTO.getCategory().getName();
            }

            String finalCategoryName = categoryName;

            Optional<ItemCategory> existingCategory = itemCategoryDAO.getAll()
                    .stream()
                    .filter(category -> finalCategoryName.equalsIgnoreCase(category.getCategoryName()))
                    .findFirst();

            if (existingCategory.isPresent()) {
                return existingCategory.get();
            }

            ItemCategory category = new ItemCategory();
            category.setCategoryName(finalCategoryName);

            return itemCategoryDAO.create(category);

        } catch (DatabaseException e) {
            throw new ApiImportException("Failed to create item category", e);

        } catch (Exception e) {
            throw new ApiImportException("Failed to get or create item category", e);
        }
    }

    private Supplier getOrCreateSupplier() {

        try {
            Optional<Supplier> existingSupplier = supplierDAO.getAll()
                    .stream()
                    .filter(supplier -> supplier.getName().equalsIgnoreCase(DEFAULT_SUPPLIER))
                    .findFirst();

            if (existingSupplier.isPresent()) {
                return existingSupplier.get();
            }

            Supplier supplier = new Supplier();
            supplier.setName(DEFAULT_SUPPLIER);

            return supplierDAO.create(supplier);

        } catch (DatabaseException e) {
            throw new ApiImportException("Failed to create supplier", e);

        } catch (Exception e) {
            throw new ApiImportException("Failed to get or create supplier", e);
        }
    }

    private String buildDescription(ImportedItemDTO importedItemDTO) {
        if (importedItemDTO.getDescriptionLines() == null ||
                importedItemDTO.getDescriptionLines().isEmpty()) {
            return "";
        }

        return String.join("\n", importedItemDTO.getDescriptionLines());
    }

    private BigDecimal calculatePrice(ImportedItemDTO importedItemDTO) {
        if (importedItemDTO.getCost() == null || importedItemDTO.getCost().getUnit() == null) {
            return BigDecimal.ZERO;
        }

        int quantity = importedItemDTO.getCost().getQuantity();

        return switch (importedItemDTO.getCost().getUnit()) {
            case pp -> BigDecimal.valueOf(quantity * 1000L);
            case gp -> BigDecimal.valueOf(quantity * 100L);
            case ep -> BigDecimal.valueOf(quantity * 50L);
            case sp -> BigDecimal.valueOf(quantity * 10L);
            case cp -> BigDecimal.valueOf(quantity);
        };
    }
}
package app.service.impl;

import app.dao.ItemCategoryDAO;
import app.dao.ItemDAO;
import app.dao.SupplierDAO;
import app.dto.item.CreateItemDTO;
import app.dto.item.ItemResponseDTO;
import app.dto.item.UpdateItemDTO;
import app.entities.Item;
import app.exceptions.ApiException;

import java.util.List;

public class ItemServiceImpl extends AbstractService<CreateItemDTO, UpdateItemDTO, ItemResponseDTO, Item, Integer> {

    private final ItemDAO itemDAO;
    private final ItemCategoryDAO itemCategoryDAO = new ItemCategoryDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();

    public ItemServiceImpl() {
        this(new ItemDAO());
    }

    public ItemServiceImpl(ItemDAO itemDAO) {

        super(itemDAO, ItemResponseDTO::fromEntity);
        this.itemDAO = itemDAO;
    }

    @Override
    protected Item createDtoToEntity(CreateItemDTO dto) {

        Item item = new Item();

        item.setName(dto.name());
        item.setDescription(dto.description());

        item.setItemCategory(itemCategoryDAO.getById(dto.itemCategoryId()).orElseThrow(() -> new ApiException(404, "Item category not found")));

        item.setSupplier(supplierDAO.getById(dto.supplierId()).orElseThrow(() -> new ApiException(404, "Supplier not found")));

        item.setBasePrice(dto.basePrice());
        item.setExternalId(dto.externalId());
        item.setExternalSource(dto.externalSource());

        return item;
    }

    @Override
    protected Item updateDtoToEntity(Item item, UpdateItemDTO dto) {

        if (dto.name() != null) {
            item.setName(dto.name());
        }

        if (dto.description() != null) {
            item.setDescription(dto.description());
        }

        if (dto.itemCategoryId() != null) {

            item.setItemCategory(itemCategoryDAO.getById(dto.itemCategoryId()).orElseThrow(() -> new ApiException(404, "Item category not found")));
        }

        if (dto.supplierId() != null) {item.setSupplier(supplierDAO.getById(dto.supplierId()).orElseThrow(() -> new ApiException(404, "Supplier not found")));
        }

        if (dto.basePrice() != null) {
            item.setBasePrice(dto.basePrice());
        }

        if (dto.externalId() != null) {
            item.setExternalId(dto.externalId());
        }

        if (dto.externalSource() != null) {
            item.setExternalSource(dto.externalSource());
        }

        return item;
    }

    public List<ItemResponseDTO> getAllByCategoryId(int categoryId) {

        return itemDAO.getAllByCategoryId(categoryId)
                .stream()
                .map(ItemResponseDTO::fromEntity)
                .toList();
    }

    public List<ItemResponseDTO> getAllBySupplierId(int supplierId) {

        return itemDAO.getAllBySupplierId(supplierId)
                .stream()
                .map(ItemResponseDTO::fromEntity)
                .toList();
    }

    public List<ItemResponseDTO> getAllByExternalSource(String externalSource) {

        return itemDAO.getAllByExternalSource(externalSource)
                .stream()
                .map(ItemResponseDTO::fromEntity)
                .toList();
    }

    public ItemResponseDTO getByExternalId(String externalId) {

        Item item = itemDAO.getByExternalId(externalId).orElseThrow(() -> new ApiException(404, "Item not found"));

        return ItemResponseDTO.fromEntity(item);
    }

    public ItemResponseDTO getByExternalIdAndSource(String externalId, String externalSource) {

        Item item = itemDAO.getByExternalIdAndSource(externalId, externalSource).orElseThrow(() -> new ApiException(404, "Item not found"));

        return ItemResponseDTO.fromEntity(item);
    }
}
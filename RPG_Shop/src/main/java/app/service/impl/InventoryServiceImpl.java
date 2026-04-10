package app.service.impl;

import app.config.ThreadPoolConfig;
import app.dao.InventoryDAO;
import app.dao.ItemDAO;
import app.dto.inventory.CreateInventoryDTO;
import app.dto.inventory.InventoryResponseDTO;
import app.dto.inventory.UpdateInventoryDTO;
import app.entities.Inventory;
import app.exceptions.ApiException;
import app.exceptions.DatabaseException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class InventoryServiceImpl extends AbstractService<CreateInventoryDTO, UpdateInventoryDTO, InventoryResponseDTO, Inventory, Integer> {

    private final InventoryDAO inventoryDAO;
    private final ItemDAO itemDAO = new ItemDAO();

    public InventoryServiceImpl() {
        this(new InventoryDAO(), ThreadPoolConfig.getExecutor());
    }

    public InventoryServiceImpl(InventoryDAO inventoryDAO, ExecutorService executorService) {
        super(inventoryDAO, InventoryResponseDTO::fromEntity, executorService);
        this.inventoryDAO = inventoryDAO;
    }

    @Override
    protected Inventory createDtoToEntity(CreateInventoryDTO dto) {

        if (inventoryDAO.getByItemId(dto.itemId()).isPresent()) {
            throw new ApiException(409, "Inventory already exists for this item");
        }

        Inventory inventory = new Inventory();

        inventory.setItem(itemDAO.getById(dto.itemId()).orElseThrow(() -> new ApiException(404, "Item not found")));

        inventory.setQuantity(dto.quantity());

        return inventory;
    }

    @Override
    protected Inventory updateDtoToEntity(Inventory inventory, UpdateInventoryDTO dto) {

        if (dto.quantity() != null) {
            if (dto.quantity() < 0) {
                throw new ApiException(400, "Quantity cannot be negative");
            }
            inventory.setQuantity(dto.quantity());
        }

        return inventory;
    }

    public CompletableFuture<InventoryResponseDTO> addStock(int itemId, int amount) {

        return CompletableFuture.supplyAsync(() -> {

            try {


                if (amount <= 0) {
                    throw new ApiException(400, "Amount must be greater than 0");
                }

                Inventory inventory = inventoryDAO.getByItemId(itemId).orElseThrow(() -> new ApiException(404, "Inventory not found"));

                inventory.setQuantity(inventory.getQuantity() + amount);

                return InventoryResponseDTO.fromEntity(inventoryDAO.update(inventory));
            } catch (DatabaseException e) {
                throw new ApiException(500, "Failed to update inventory");
            }
        }, executorService);
    }

    public CompletableFuture<InventoryResponseDTO> removeStock(int itemId, int amount) {
        return CompletableFuture.supplyAsync(() -> {

            try {


                if (amount <= 0) {
                    throw new ApiException(400, "Amount must be greater than 0");
                }

                Inventory inventory = inventoryDAO.getByItemId(itemId)
                        .orElseThrow(() -> new ApiException(404, "Inventory not found"));

                if (inventory.getQuantity() < amount) {
                    throw new ApiException(400, "Not enough stock");
                }

                inventory.setQuantity(inventory.getQuantity() - amount);

                return InventoryResponseDTO.fromEntity(inventoryDAO.update(inventory));
            } catch (DatabaseException e) {
                throw new ApiException(500, "Failed to update inventory");
            }
        }, executorService);
    }

    public CompletableFuture<InventoryResponseDTO> getByItemId(int itemId) {

        return CompletableFuture.supplyAsync(() -> {

            Inventory inventory = inventoryDAO.getByItemId(itemId).orElseThrow(() -> new ApiException(404, "Inventory not found"));

            return InventoryResponseDTO.fromEntity(inventory);
        }, executorService);
    }
}
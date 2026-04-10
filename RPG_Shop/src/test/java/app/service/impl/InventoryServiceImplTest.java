package app.service.impl;

import app.config.HibernateConfig;
import app.dao.InventoryDAO;
import app.dao.ItemCategoryDAO;
import app.dao.ItemDAO;
import app.dao.SupplierDAO;
import app.dto.inventory.CreateInventoryDTO;
import app.dto.inventory.InventoryResponseDTO;
import app.entities.Inventory;
import app.entities.Item;
import app.entities.ItemCategory;
import app.entities.Supplier;
import app.exceptions.ApiException;
import app.exceptions.DatabaseException;
import app.utils.EMF;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class InventoryServiceImplTest {

    private static InventoryServiceImpl inventoryService;
    private static ExecutorService executorService;

    private final InventoryDAO inventoryDAO = new InventoryDAO();
    private final ItemDAO itemDAO = new ItemDAO();
    private final ItemCategoryDAO itemCategoryDAO = new ItemCategoryDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();

    @BeforeAll
    static void setUpAll() {
        HibernateConfig.setTest(true);
        executorService = Executors.newSingleThreadExecutor();
        inventoryService = new InventoryServiceImpl(new InventoryDAO(), executorService);
    }

    @BeforeEach
    void setUp() throws DatabaseException {
        inventoryDAO.deleteAll();
        itemDAO.deleteAll();
        supplierDAO.deleteAll();
        itemCategoryDAO.deleteAll();
    }

    @AfterAll
    static void tearDownAll() {
        executorService.shutdown();
        EMF.close();
    }

    @Test
    void create_shouldCreateInventory() throws DatabaseException {
        Item item = createItem();

        CreateInventoryDTO dto = new CreateInventoryDTO(item.getId(), 10);

        InventoryResponseDTO result = inventoryService.create(dto).join();

        assertNotNull(result);
        assertEquals(item.getId(), result.itemId());
        assertEquals(10, result.quantity());
    }

    @Test
    void create_shouldThrow_whenItemDoesNotExist() {
        CreateInventoryDTO dto = new CreateInventoryDTO(999999, 10);

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> inventoryService.create(dto).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Item not found", ex.getCause().getMessage());
    }

    @Test
    void create_shouldThrow_whenInventoryAlreadyExistsForItem() throws DatabaseException {
        Item item = createItem();
        createInventory(item, 10);

        CreateInventoryDTO dto = new CreateInventoryDTO(item.getId(), 5);

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> inventoryService.create(dto).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Inventory already exists for this item", ex.getCause().getMessage());
    }

    @Test
    void addStock_shouldIncreaseQuantity() throws DatabaseException {
        Item item = createItem();
        createInventory(item, 10);

        InventoryResponseDTO result = inventoryService.addStock(item.getId(), 5).join();

        assertEquals(15, result.quantity());
    }

    @Test
    void removeStock_shouldDecreaseQuantity() throws DatabaseException {
        Item item = createItem();
        createInventory(item, 10);

        InventoryResponseDTO result = inventoryService.removeStock(item.getId(), 4).join();

        assertEquals(6, result.quantity());
    }

    @Test
    void removeStock_shouldThrow_whenNotEnoughStock() throws DatabaseException {
        Item item = createItem();
        createInventory(item, 3);

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> inventoryService.removeStock(item.getId(), 5).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Not enough stock", ex.getCause().getMessage());
    }

    @Test
    void getByItemId_shouldReturnInventory() throws DatabaseException {
        Item item = createItem();
        Inventory inventory = createInventory(item, 12);

        InventoryResponseDTO result = inventoryService.getByItemId(item.getId()).join();

        assertEquals(inventory.getId(), result.id());
        assertEquals(item.getId(), result.itemId());
        assertEquals(12, result.quantity());
    }

    private Item createItem() throws DatabaseException {

        ItemCategory category = new ItemCategory();
        category.setCategoryName("Weapons");
        category = itemCategoryDAO.create(category);

        Supplier supplier = new Supplier();
        supplier.setName("Sword Supplier");
        supplier = supplierDAO.create(supplier);

        Item item = new Item();
        item.setName("Sword");
        item.setBasePrice(new BigDecimal("100.00"));
        item.setItemCategory(category);
        item.setSupplier(supplier);

        return itemDAO.create(item);
    }

    private Inventory createInventory(Item item, int quantity) throws DatabaseException {

        Inventory inventory = new Inventory();
        inventory.setItem(item);
        inventory.setQuantity(quantity);
        return inventoryDAO.create(inventory);
    }
}
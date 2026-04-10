package app.dao;

import app.config.HibernateConfig;
import app.entities.Inventory;
import app.entities.Item;
import app.entities.ItemCategory;
import app.entities.Supplier;
import app.exceptions.DatabaseException;
import app.utils.EMF;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InventoryDAOTest {

    private InventoryDAO inventoryDAO;
    private ItemDAO itemDAO;
    private ItemCategoryDAO itemCategoryDAO;
    private SupplierDAO supplierDAO;

    @BeforeAll
    void setUpAll() {

        HibernateConfig.setTest(true);
        inventoryDAO = new InventoryDAO();
        itemDAO = new ItemDAO();
        itemCategoryDAO = new ItemCategoryDAO();
        supplierDAO = new SupplierDAO();
    }

    @BeforeEach
    void setUp() throws DatabaseException {

        inventoryDAO.deleteAll();
        itemDAO.deleteAll();
        itemCategoryDAO.deleteAll();
        supplierDAO.deleteAll();
    }

    @AfterAll
    void tearDownAll() {
        EMF.close();
    }

    @Test
    void create_shouldPersistInventory() throws DatabaseException {

        Item item = createTestItem("Sword", "ext-1");

        Inventory inventory = new Inventory();
        inventory.setItem(item);
        inventory.setQuantity(10);

        Inventory created = inventoryDAO.create(inventory);

        assertNotNull(created);
        assertTrue(created.getId() > 0);
        assertEquals(10, created.getQuantity());
    }

    @Test
    void getById_shouldReturnInventory_whenExists() throws DatabaseException {

        Item item = createTestItem("Sword", "ext-1");

        Inventory inventory = new Inventory();
        inventory.setItem(item);
        inventory.setQuantity(10);
        Inventory created = inventoryDAO.create(inventory);

        Optional<Inventory> found = inventoryDAO.getById(created.getId());

        assertTrue(found.isPresent());
        assertEquals(created.getId(), found.get().getId());
    }

    @Test
    void getAll_shouldReturnAllInventories() throws DatabaseException {

        Item item1 = createTestItem("Sword", "ext-1");
        Item item2 = createTestItem("Shield", "ext-2");

        Inventory i1 = new Inventory();
        i1.setItem(item1);
        i1.setQuantity(10);

        Inventory i2 = new Inventory();
        i2.setItem(item2);
        i2.setQuantity(5);

        inventoryDAO.create(i1);
        inventoryDAO.create(i2);

        List<Inventory> inventories = inventoryDAO.getAll();

        assertEquals(2, inventories.size());
    }

    @Test
    void update_shouldUpdateInventory() throws DatabaseException {

        Item item = createTestItem("Sword", "ext-1");

        Inventory inventory = new Inventory();
        inventory.setItem(item);
        inventory.setQuantity(10);
        Inventory created = inventoryDAO.create(inventory);

        created.setQuantity(25);

        Inventory updated = inventoryDAO.update(created);

        assertEquals(25, updated.getQuantity());
    }

    @Test
    void deleteById_shouldDeleteInventory() throws DatabaseException {

        Item item = createTestItem("Sword", "ext-1");

        Inventory inventory = new Inventory();
        inventory.setItem(item);
        inventory.setQuantity(10);
        Inventory created = inventoryDAO.create(inventory);

        boolean deleted = inventoryDAO.deleteById(created.getId());

        assertTrue(deleted);
        assertTrue(inventoryDAO.getById(created.getId()).isEmpty());
    }

    @Test
    void getByItemId_shouldReturnInventory_whenExists() throws DatabaseException {

        Item item = createTestItem("Sword", "ext-1");

        Inventory inventory = new Inventory();
        inventory.setItem(item);
        inventory.setQuantity(10);
        inventoryDAO.create(inventory);

        Optional<Inventory> found = inventoryDAO.getByItemId(item.getId());

        assertTrue(found.isPresent());
        assertEquals(item.getId(), found.get().getItem().getId());
    }

    private Item createTestItem(String name, String externalId) throws DatabaseException {

        ItemCategory category = new ItemCategory();
        category.setCategoryName("Weapons-" + externalId);
        category = itemCategoryDAO.create(category);

        Supplier supplier = new Supplier();
        supplier.setName("Supplier-" + externalId);
        supplier = supplierDAO.create(supplier);

        Item item = new Item();
        item.setName(name);
        item.setDescription("Test item");
        item.setBasePrice(BigDecimal.valueOf(100));
        item.setItemCategory(category);
        item.setSupplier(supplier);
        item.setExternalId(externalId);
        item.setExternalSource("DND5E");

        return itemDAO.create(item);
    }
}
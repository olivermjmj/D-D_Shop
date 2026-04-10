package app.dao;

import app.config.HibernateConfig;
import app.entities.*;
import app.entities.enums.Role;
import app.exceptions.DatabaseException;
import app.utils.EMF;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StockChangeDAOTest {

    private StockChangeDAO stockChangeDAO;
    private ItemDAO itemDAO;
    private ItemCategoryDAO itemCategoryDAO;
    private SupplierDAO supplierDAO;
    private UserDAO userDAO;

    @BeforeAll
    void setUpAll() {

        HibernateConfig.setTest(true);
        stockChangeDAO = new StockChangeDAO();
        itemDAO = new ItemDAO();
        itemCategoryDAO = new ItemCategoryDAO();
        supplierDAO = new SupplierDAO();
        userDAO = new UserDAO();
    }

    @BeforeEach
    void setUp() throws DatabaseException {

        stockChangeDAO.deleteAll();
        itemDAO.deleteAll();
        itemCategoryDAO.deleteAll();
        supplierDAO.deleteAll();
        userDAO.deleteAll();
    }

    @AfterAll
    void tearDownAll() {
        EMF.close();
    }

    @Test
    void create_shouldPersistStockChange() throws DatabaseException {

        Item item = createTestItem();

        StockChange stockChange = new StockChange();
        stockChange.setItem(item);
        stockChange.setDelta(10);
        stockChange.setReason("Restock");

        StockChange created = stockChangeDAO.create(stockChange);

        assertNotNull(created);
        assertTrue(created.getId() > 0);
        assertEquals(10, created.getDelta());
    }

    @Test
    void getById_shouldReturnStockChange_whenExists() throws DatabaseException {

        Item item = createTestItem();

        StockChange stockChange = new StockChange();
        stockChange.setItem(item);
        stockChange.setDelta(5);
        stockChange.setReason("Added");

        StockChange created = stockChangeDAO.create(stockChange);

        assertTrue(stockChangeDAO.getById(created.getId()).isPresent());
    }

    @Test
    void getAll_shouldReturnAllStockChanges() throws DatabaseException {

        Item item = createTestItem();

        StockChange s1 = new StockChange();
        s1.setItem(item);
        s1.setDelta(5);
        s1.setReason("Added");

        StockChange s2 = new StockChange();
        s2.setItem(item);
        s2.setDelta(-2);
        s2.setReason("Sold");

        stockChangeDAO.create(s1);
        stockChangeDAO.create(s2);

        List<StockChange> changes = stockChangeDAO.getAll();

        assertEquals(2, changes.size());
    }

    @Test
    void update_shouldUpdateStockChange() throws DatabaseException {

        Item item = createTestItem();

        StockChange stockChange = new StockChange();
        stockChange.setItem(item);
        stockChange.setDelta(5);
        stockChange.setReason("Added");

        StockChange created = stockChangeDAO.create(stockChange);

        created.setDelta(12);
        created.setReason("Restock");

        StockChange updated = stockChangeDAO.update(created);

        assertEquals(12, updated.getDelta());
        assertEquals("Restock", updated.getReason());
    }

    @Test
    void deleteById_shouldDeleteStockChange() throws DatabaseException {

        Item item = createTestItem();

        StockChange stockChange = new StockChange();
        stockChange.setItem(item);
        stockChange.setDelta(5);
        stockChange.setReason("Added");

        StockChange created = stockChangeDAO.create(stockChange);

        boolean deleted = stockChangeDAO.deleteById(created.getId());

        assertTrue(deleted);
        assertTrue(stockChangeDAO.getById(created.getId()).isEmpty());
    }

    @Test
    void getAllByItemId_shouldReturnMatchingStockChanges() throws DatabaseException {

        Item item = createTestItem();

        StockChange s1 = new StockChange();
        s1.setItem(item);
        s1.setDelta(5);
        s1.setReason("Added");

        StockChange s2 = new StockChange();
        s2.setItem(item);
        s2.setDelta(-2);
        s2.setReason("Sold");

        stockChangeDAO.create(s1);
        stockChangeDAO.create(s2);

        List<StockChange> changes = stockChangeDAO.getAllByItemId(item.getId());

        assertEquals(2, changes.size());
        assertTrue(changes.stream().allMatch(c -> c.getItem().getId() == item.getId()));
    }

    @Test
    void getAllByAdminId_shouldReturnMatchingStockChanges() throws DatabaseException {

        Item item = createTestItem();
        User admin = userDAO.create(new User("admin@mail.com", "Admin", "admin1", "pw", Role.ADMIN));

        StockChange stockChange = new StockChange();
        stockChange.setItem(item);
        stockChange.setDelta(10);
        stockChange.setReason("Restock");
        stockChange.setPerformedBy(admin);

        stockChangeDAO.create(stockChange);

        List<StockChange> changes = stockChangeDAO.getAllByAdminId(admin.getId());

        assertEquals(1, changes.size());
        assertEquals(admin.getId(), changes.get(0).getPerformedBy().getId());
    }

    private Item createTestItem() throws DatabaseException {

        ItemCategory category = new ItemCategory();
        category.setCategoryName("Weapons");
        category = itemCategoryDAO.create(category);

        Supplier supplier = new Supplier();
        supplier.setName("Test Supplier");
        supplier = supplierDAO.create(supplier);

        Item item = new Item();
        item.setName("Sword");
        item.setDescription("Test item");
        item.setBasePrice(BigDecimal.valueOf(100));
        item.setItemCategory(category);
        item.setSupplier(supplier);

        return itemDAO.create(item);
    }
}
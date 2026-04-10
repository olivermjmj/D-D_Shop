package app.dao;

import app.config.HibernateConfig;
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
class ItemDAOTest {

    private ItemDAO itemDAO;
    private ItemCategoryDAO itemCategoryDAO;
    private SupplierDAO supplierDAO;

    @BeforeAll
    void setUpAll() {

        HibernateConfig.setTest(true);
        itemDAO = new ItemDAO();
        itemCategoryDAO = new ItemCategoryDAO();
        supplierDAO = new SupplierDAO();
    }

    @BeforeEach
    void setUp() throws DatabaseException {

        itemDAO.deleteAll();
        itemCategoryDAO.deleteAll();
        supplierDAO.deleteAll();
    }

    @AfterAll
    void tearDownAll() {
        EMF.close();
    }

    @Test
    void create_shouldPersistItem() throws DatabaseException {

        Item item = createTestItem("Sword", "ext-1", "DND5E");

        Item created = itemDAO.create(item);

        assertNotNull(created);
        assertTrue(created.getId() > 0);
        assertEquals("Sword", created.getName());
    }

    @Test
    void getById_shouldReturnItem_whenExists() throws DatabaseException {

        Item created = itemDAO.create(createTestItem("Sword", "ext-1", "DND5E"));

        Optional<Item> found = itemDAO.getById(created.getId());

        assertTrue(found.isPresent());
        assertEquals(created.getId(), found.get().getId());
    }

    @Test
    void getAll_shouldReturnAllItems() throws DatabaseException {

        itemDAO.create(createTestItem("Sword", "ext-1", "DND5E"));
        itemDAO.create(createTestItem("Shield", "ext-2", "DND5E"));

        List<Item> items = itemDAO.getAll();

        assertEquals(2, items.size());
    }

    @Test
    void update_shouldUpdateItem() throws DatabaseException {

        Item created = itemDAO.create(createTestItem("Sword", "ext-1", "DND5E"));

        created.setName("Axe");

        Item updated = itemDAO.update(created);

        assertEquals("Axe", updated.getName());
    }

    @Test
    void deleteById_shouldDeleteItem() throws DatabaseException {

        Item created = itemDAO.create(createTestItem("Sword", "ext-1", "DND5E"));

        boolean deleted = itemDAO.deleteById(created.getId());

        assertTrue(deleted);
        assertTrue(itemDAO.getById(created.getId()).isEmpty());
    }

    @Test
    void getAllByCategoryId_shouldReturnMatchingItems() throws DatabaseException {

        ItemCategory category = createTestCategory("Weapons");
        Supplier supplier = createTestSupplier("Supplier 1");

        Item item1 = new Item();
        item1.setName("Sword");
        item1.setDescription("Test item");
        item1.setBasePrice(BigDecimal.valueOf(100));
        item1.setItemCategory(category);
        item1.setSupplier(supplier);

        Item item2 = new Item();
        item2.setName("Axe");
        item2.setDescription("Test item");
        item2.setBasePrice(BigDecimal.valueOf(150));
        item2.setItemCategory(category);
        item2.setSupplier(supplier);

        itemDAO.create(item1);
        itemDAO.create(item2);

        List<Item> items = itemDAO.getAllByCategoryId(category.getId());

        assertEquals(2, items.size());
        assertTrue(items.stream().allMatch(i -> i.getItemCategory().getId() == category.getId()));
    }

    @Test
    void getAllBySupplierId_shouldReturnMatchingItems() throws DatabaseException {

        ItemCategory category = createTestCategory("Weapons");
        Supplier supplier = createTestSupplier("Supplier 1");

        Item item1 = new Item();
        item1.setName("Sword");
        item1.setDescription("Test item");
        item1.setBasePrice(BigDecimal.valueOf(100));
        item1.setItemCategory(category);
        item1.setSupplier(supplier);

        Item item2 = new Item();
        item2.setName("Axe");
        item2.setDescription("Test item");
        item2.setBasePrice(BigDecimal.valueOf(150));
        item2.setItemCategory(category);
        item2.setSupplier(supplier);

        itemDAO.create(item1);
        itemDAO.create(item2);

        List<Item> items = itemDAO.getAllBySupplierId(supplier.getId());

        assertEquals(2, items.size());
        assertTrue(items.stream().allMatch(i -> i.getSupplier().getId() == supplier.getId()));
    }

    @Test
    void getAllByExternalSource_shouldReturnMatchingItems() throws DatabaseException {

        itemDAO.create(createTestItem("Sword", "ext-1", "DND5E"));
        itemDAO.create(createTestItem("Shield", "ext-2", "DND5E"));

        List<Item> items = itemDAO.getAllByExternalSource("DND5E");

        assertEquals(2, items.size());
        assertTrue(items.stream().allMatch(i -> "DND5E".equals(i.getExternalSource())));
    }

    @Test
    void getByExternalId_shouldReturnItem_whenExists() throws DatabaseException {

        itemDAO.create(createTestItem("Sword", "ext-1", "DND5E"));

        Optional<Item> found = itemDAO.getByExternalId("ext-1");

        assertTrue(found.isPresent());
        assertEquals("ext-1", found.get().getExternalId());
    }

    @Test
    void getByExternalIdAndSource_shouldReturnItem_whenExists() throws DatabaseException {

        itemDAO.create(createTestItem("Sword", "ext-1", "DND5E"));

        Optional<Item> found = itemDAO.getByExternalIdAndSource("ext-1", "DND5E");

        assertTrue(found.isPresent());
        assertEquals("ext-1", found.get().getExternalId());
        assertEquals("DND5E", found.get().getExternalSource());
    }

    private Item createTestItem(String name, String externalId, String externalSource) throws DatabaseException {

        ItemCategory category = createTestCategory("Weapons-" + externalId);
        Supplier supplier = createTestSupplier("Supplier-" + externalId);

        Item item = new Item();
        item.setName(name);
        item.setDescription("Test item");
        item.setBasePrice(BigDecimal.valueOf(100));
        item.setItemCategory(category);
        item.setSupplier(supplier);
        item.setExternalId(externalId);
        item.setExternalSource(externalSource);

        return item;
    }

    private ItemCategory createTestCategory(String name) throws DatabaseException {

        ItemCategory category = new ItemCategory();
        category.setCategoryName(name);
        return itemCategoryDAO.create(category);
    }

    private Supplier createTestSupplier(String name) throws DatabaseException {

        Supplier supplier = new Supplier();
        supplier.setName(name);
        return supplierDAO.create(supplier);
    }
}
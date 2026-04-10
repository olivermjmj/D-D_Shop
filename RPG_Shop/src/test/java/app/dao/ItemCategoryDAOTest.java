package app.dao;

import app.config.HibernateConfig;
import app.entities.ItemCategory;
import app.exceptions.DatabaseException;
import app.utils.EMF;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemCategoryDAOTest {

    private ItemCategoryDAO itemCategoryDAO;

    @BeforeAll
    void setUpAll() {

        HibernateConfig.setTest(true);
        itemCategoryDAO = new ItemCategoryDAO();
    }

    @BeforeEach
    void setUp() throws DatabaseException {
        itemCategoryDAO.deleteAll();
    }

    @AfterAll
    void tearDownAll() {
        EMF.close();
    }

    @Test
    void create_shouldPersistItemCategory() throws DatabaseException {

        ItemCategory category = new ItemCategory();
        category.setCategoryName("Weapons");

        ItemCategory created = itemCategoryDAO.create(category);

        assertNotNull(created);
        assertTrue(created.getId() > 0);
        assertEquals("Weapons", created.getCategoryName());
    }

    @Test
    void getById_shouldReturnItemCategory_whenExists() throws DatabaseException {

        ItemCategory category = new ItemCategory();
        category.setCategoryName("Weapons");
        ItemCategory created = itemCategoryDAO.create(category);

        Optional<ItemCategory> found = itemCategoryDAO.getById(created.getId());

        assertTrue(found.isPresent());
        assertEquals(created.getId(), found.get().getId());
    }

    @Test
    void getAll_shouldReturnAllItemCategories() throws DatabaseException {

        ItemCategory c1 = new ItemCategory();
        c1.setCategoryName("Weapons");

        ItemCategory c2 = new ItemCategory();
        c2.setCategoryName("Armor");

        itemCategoryDAO.create(c1);
        itemCategoryDAO.create(c2);

        List<ItemCategory> categories = itemCategoryDAO.getAll();

        assertEquals(2, categories.size());
    }

    @Test
    void update_shouldUpdateItemCategory() throws DatabaseException {

        ItemCategory category = new ItemCategory();
        category.setCategoryName("Weapons");
        ItemCategory created = itemCategoryDAO.create(category);

        created.setCategoryName("Armor");

        ItemCategory updated = itemCategoryDAO.update(created);

        assertEquals("Armor", updated.getCategoryName());
    }

    @Test
    void deleteById_shouldDeleteItemCategory() throws DatabaseException {

        ItemCategory category = new ItemCategory();
        category.setCategoryName("Weapons");
        ItemCategory created = itemCategoryDAO.create(category);

        boolean deleted = itemCategoryDAO.deleteById(created.getId());

        assertTrue(deleted);
        assertTrue(itemCategoryDAO.getById(created.getId()).isEmpty());
    }
}
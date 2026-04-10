package app.dao;

import app.config.HibernateConfig;
import app.entities.*;
import app.entities.enums.QualityStatus;
import app.entities.enums.Role;
import app.exceptions.DatabaseException;
import app.utils.EMF;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class QualityCheckDAOTest {

    private QualityCheckDAO qualityCheckDAO;
    private ItemDAO itemDAO;
    private ItemCategoryDAO itemCategoryDAO;
    private SupplierDAO supplierDAO;
    private UserDAO userDAO;

    @BeforeAll
    void setUpAll() {

        HibernateConfig.setTest(true);
        qualityCheckDAO = new QualityCheckDAO();
        itemDAO = new ItemDAO();
        itemCategoryDAO = new ItemCategoryDAO();
        supplierDAO = new SupplierDAO();
        userDAO = new UserDAO();
    }

    @BeforeEach
    void setUp() throws DatabaseException {

        qualityCheckDAO.deleteAll();
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
    void create_shouldPersistQualityCheck() throws DatabaseException {

        Item item = createTestItem();

        QualityCheck check = new QualityCheck();
        check.setItem(item);
        check.setStatus(QualityStatus.APPROVED);

        QualityCheck created = qualityCheckDAO.create(check);

        assertNotNull(created);
        assertTrue(created.getId() > 0);
        assertEquals(QualityStatus.APPROVED, created.getStatus());
    }

    @Test
    void getById_shouldReturnQualityCheck_whenExists() throws DatabaseException {

        Item item = createTestItem();

        QualityCheck check = new QualityCheck();
        check.setItem(item);
        check.setStatus(QualityStatus.APPROVED);

        QualityCheck created = qualityCheckDAO.create(check);

        Optional<QualityCheck> found = qualityCheckDAO.getById(created.getId());

        assertTrue(found.isPresent());
        assertEquals(created.getId(), found.get().getId());
    }

    @Test
    void getAll_shouldReturnAllQualityChecks() throws DatabaseException {

        Item item = createTestItem();

        QualityCheck q1 = new QualityCheck();
        q1.setItem(item);
        q1.setStatus(QualityStatus.APPROVED);

        QualityCheck q2 = new QualityCheck();
        q2.setItem(item);
        q2.setStatus(QualityStatus.REJECTED);

        qualityCheckDAO.create(q1);
        qualityCheckDAO.create(q2);

        List<QualityCheck> checks = qualityCheckDAO.getAll();

        assertEquals(2, checks.size());
    }

    @Test
    void update_shouldUpdateQualityCheck() throws DatabaseException {

        Item item = createTestItem();

        QualityCheck check = new QualityCheck();
        check.setItem(item);
        check.setStatus(QualityStatus.PENDING);

        QualityCheck created = qualityCheckDAO.create(check);

        created.setStatus(QualityStatus.APPROVED);

        QualityCheck updated = qualityCheckDAO.update(created);

        assertEquals(QualityStatus.APPROVED, updated.getStatus());
    }

    @Test
    void deleteById_shouldDeleteQualityCheck() throws DatabaseException {

        Item item = createTestItem();

        QualityCheck check = new QualityCheck();
        check.setItem(item);
        check.setStatus(QualityStatus.APPROVED);

        QualityCheck created = qualityCheckDAO.create(check);

        boolean deleted = qualityCheckDAO.deleteById(created.getId());

        assertTrue(deleted);
        assertTrue(qualityCheckDAO.getById(created.getId()).isEmpty());
    }

    @Test
    void getAllByItemId_shouldReturnMatchingChecks() throws DatabaseException {

        Item item = createTestItem();

        QualityCheck q1 = new QualityCheck();
        q1.setItem(item);
        q1.setStatus(QualityStatus.APPROVED);

        QualityCheck q2 = new QualityCheck();
        q2.setItem(item);
        q2.setStatus(QualityStatus.REJECTED);

        qualityCheckDAO.create(q1);
        qualityCheckDAO.create(q2);

        List<QualityCheck> checks = qualityCheckDAO.getAllByItemId(item.getId());

        assertEquals(2, checks.size());
        assertTrue(checks.stream().allMatch(q -> q.getItem().getId() == item.getId()));
    }

    @Test
    void getAllByStatus_shouldReturnMatchingChecks() throws DatabaseException {

        Item item = createTestItem();

        QualityCheck q1 = new QualityCheck();
        q1.setItem(item);
        q1.setStatus(QualityStatus.APPROVED);

        QualityCheck q2 = new QualityCheck();
        q2.setItem(item);
        q2.setStatus(QualityStatus.REJECTED);

        qualityCheckDAO.create(q1);
        qualityCheckDAO.create(q2);

        List<QualityCheck> checks = qualityCheckDAO.getAllByStatus(QualityStatus.APPROVED);

        assertEquals(1, checks.size());
        assertEquals(QualityStatus.APPROVED, checks.get(0).getStatus());
    }

    @Test
    void getAllByApprovedById_shouldReturnMatchingChecks() throws DatabaseException {

        Item item = createTestItem();
        User admin = userDAO.create(new User("admin@mail.com", "Admin", "admin1", "pw", Role.ADMIN));

        QualityCheck check = new QualityCheck();
        check.setItem(item);
        check.setStatus(QualityStatus.APPROVED);
        check.setApprovedBy(admin);

        qualityCheckDAO.create(check);

        List<QualityCheck> checks = qualityCheckDAO.getAllByApprovedById(admin.getId());

        assertEquals(1, checks.size());
        assertEquals(admin.getId(), checks.get(0).getApprovedBy().getId());
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
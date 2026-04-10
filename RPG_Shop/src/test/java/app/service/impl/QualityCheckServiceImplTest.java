package app.service.impl;

import app.config.HibernateConfig;
import app.dao.ItemCategoryDAO;
import app.dao.ItemDAO;
import app.dao.QualityCheckDAO;
import app.dao.SupplierDAO;
import app.dao.UserDAO;
import app.dto.qualityCheck.CreateQualityCheckDTO;
import app.dto.qualityCheck.QualityCheckResponseDTO;
import app.entities.Item;
import app.entities.ItemCategory;
import app.entities.QualityCheck;
import app.entities.Supplier;
import app.entities.User;
import app.entities.enums.QualityStatus;
import app.entities.enums.Role;
import app.exceptions.ApiException;
import app.exceptions.DatabaseException;
import app.utils.EMF;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class QualityCheckServiceImplTest {

    private static QualityCheckServiceImpl qualityCheckService;
    private static ExecutorService executorService;

    private final QualityCheckDAO qualityCheckDAO = new QualityCheckDAO();
    private final ItemDAO itemDAO = new ItemDAO();
    private final ItemCategoryDAO itemCategoryDAO = new ItemCategoryDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final UserDAO userDAO = new UserDAO();

    @BeforeAll
    static void setUpAll() {
        HibernateConfig.setTest(true);
        executorService = Executors.newSingleThreadExecutor();
        qualityCheckService = new QualityCheckServiceImpl(new QualityCheckDAO(), executorService);
    }

    @BeforeEach
    void setUp() throws DatabaseException {
        qualityCheckDAO.deleteAll();
        itemDAO.deleteAll();
        supplierDAO.deleteAll();
        itemCategoryDAO.deleteAll();
        userDAO.deleteAll();
    }

    @AfterAll
    static void tearDownAll() {
        executorService.shutdown();
        EMF.close();
    }

    @Test
    void create_shouldThrow_whenStatusIsNull() throws DatabaseException {
        Item item = createItem();

        CreateQualityCheckDTO dto = new CreateQualityCheckDTO(
                item.getId(),
                null,
                null
        );

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> qualityCheckService.create(dto).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Quality status is required", ex.getCause().getMessage());
    }

    @Test
    void create_shouldThrow_whenItemDoesNotExist() {
        CreateQualityCheckDTO dto = new CreateQualityCheckDTO(
                999999,
                QualityStatus.APPROVED,
                null
        );

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> qualityCheckService.create(dto).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Item not found", ex.getCause().getMessage());
    }

    @Test
    void create_shouldThrow_whenUserDoesNotExist() throws DatabaseException {
        Item item = createItem();

        CreateQualityCheckDTO dto = new CreateQualityCheckDTO(
                item.getId(),
                QualityStatus.APPROVED,
                999999
        );

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> qualityCheckService.create(dto).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("User not found", ex.getCause().getMessage());
    }

    @Test
    void getAllByItemId_shouldReturnQualityChecksForItem() throws DatabaseException {
        Item item = createItem();
        User user = createUser();

        createQualityCheck(item, user, QualityStatus.APPROVED);
        createQualityCheck(item, user, QualityStatus.REJECTED);

        List<QualityCheckResponseDTO> result = qualityCheckService.getAllByItemId(item.getId()).join();

        assertEquals(2, result.size());
    }

    @Test
    void getAllByStatus_shouldReturnOnlyMatchingStatus() throws DatabaseException {
        Item item = createItem();
        User user = createUser();

        createQualityCheck(item, user, QualityStatus.APPROVED);
        createQualityCheck(item, user, QualityStatus.REJECTED);
        createQualityCheck(item, user, QualityStatus.APPROVED);

        List<QualityCheckResponseDTO> result = qualityCheckService.getAllByStatus(QualityStatus.APPROVED).join();

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(q -> q.status() == QualityStatus.APPROVED));
    }

    @Test
    void getAllByApprovedById_shouldReturnQualityChecksForUser() throws DatabaseException {
        Item item = createItem();
        User user = createUser();

        createQualityCheck(item, user, QualityStatus.APPROVED);
        createQualityCheck(item, user, QualityStatus.REJECTED);

        List<QualityCheckResponseDTO> result = qualityCheckService.getAllByApprovedById(user.getId()).join();

        assertEquals(2, result.size());
    }

    private User createUser() throws DatabaseException {
        User user = new User();
        user.setEmail("admin@test.dk");
        user.setName("Admin");
        user.setUsername("admin");
        user.setPasswordHash("test123");
        user.setWallet(BigDecimal.ZERO);
        user.setRole(Role.ADMIN);
        return userDAO.create(user);
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

    private QualityCheck createQualityCheck(Item item, User user, QualityStatus status) throws DatabaseException {
        QualityCheck qualityCheck = new QualityCheck();
        qualityCheck.setItem(item);
        qualityCheck.setApprovedBy(user);
        qualityCheck.setStatus(status);
        return qualityCheckDAO.create(qualityCheck);
    }
}
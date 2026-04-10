package app.service.impl;

import app.config.HibernateConfig;
import app.dao.ItemCategoryDAO;
import app.dao.ItemDAO;
import app.dao.StockChangeDAO;
import app.dao.SupplierDAO;
import app.dao.UserDAO;
import app.dto.stockChange.CreateStockChangeDTO;
import app.dto.stockChange.StockChangeResponseDTO;
import app.entities.Item;
import app.entities.ItemCategory;
import app.entities.StockChange;
import app.entities.Supplier;
import app.entities.User;
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

class StockChangeServiceImplTest {

    private static StockChangeServiceImpl stockChangeService;
    private static ExecutorService executorService;

    private final StockChangeDAO stockChangeDAO = new StockChangeDAO();
    private final ItemDAO itemDAO = new ItemDAO();
    private final ItemCategoryDAO itemCategoryDAO = new ItemCategoryDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final UserDAO userDAO = new UserDAO();

    @BeforeAll
    static void setUpAll() {

        HibernateConfig.setTest(true);
        executorService = Executors.newSingleThreadExecutor();
        stockChangeService = new StockChangeServiceImpl(new StockChangeDAO(), executorService);
    }

    @BeforeEach
    void setUp() throws DatabaseException {

        stockChangeDAO.deleteAll();
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
    void create_shouldThrow_whenDeltaIsZero() throws DatabaseException {

        Item item = createItem();

        CreateStockChangeDTO dto = new CreateStockChangeDTO(
                item.getId(),
                0,
                "Restock",
                null
        );

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> stockChangeService.create(dto).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Delta cannot be 0", ex.getCause().getMessage());
    }

    @Test
    void create_shouldThrow_whenItemDoesNotExist() {

        CreateStockChangeDTO dto = new CreateStockChangeDTO(
                999999,
                5,
                "Restock",
                null
        );

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> stockChangeService.create(dto).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Item not found", ex.getCause().getMessage());
    }

    @Test
    void create_shouldThrow_whenUserDoesNotExist() throws DatabaseException {

        Item item = createItem();

        CreateStockChangeDTO dto = new CreateStockChangeDTO(
                item.getId(),
                5,
                "Restock",
                999999
        );

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> stockChangeService.create(dto).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("User not found", ex.getCause().getMessage());
    }

    @Test
    void getAllByItemId_shouldReturnStockChangesForItem() throws DatabaseException {

        Item item = createItem();
        User user = createUser();

        createStockChange(item, user, 5, "Restock");
        createStockChange(item, user, -2, "Sold");

        List<StockChangeResponseDTO> result = stockChangeService.getAllByItemId(item.getId()).join();

        assertEquals(2, result.size());
    }

    @Test
    void getAllByAdminId_shouldReturnStockChangesForAdmin() throws DatabaseException {

        Item item = createItem();
        User user = createUser();

        createStockChange(item, user, 10, "Restock");
        createStockChange(item, user, -3, "Adjustment");

        List<StockChangeResponseDTO> result = stockChangeService.getAllByAdminId(user.getId()).join();

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

    private StockChange createStockChange(Item item, User user, int delta, String reason) throws DatabaseException {

        StockChange stockChange = new StockChange();
        stockChange.setItem(item);
        stockChange.setPerformedBy(user);
        stockChange.setDelta(delta);
        stockChange.setReason(reason);
        return stockChangeDAO.create(stockChange);
    }
}
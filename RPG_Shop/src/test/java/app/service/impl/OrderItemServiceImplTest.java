package app.service.impl;

import app.config.HibernateConfig;
import app.dao.AddressDAO;
import app.dao.ItemCategoryDAO;
import app.dao.ItemDAO;
import app.dao.OrderDAO;
import app.dao.OrderItemDAO;
import app.dao.SupplierDAO;
import app.dao.UserDAO;
import app.dto.orderItem.CreateOrderItemDTO;
import app.dto.orderItem.OrderItemResponseDTO;
import app.entities.Address;
import app.entities.Item;
import app.entities.ItemCategory;
import app.entities.Order;
import app.entities.OrderItem;
import app.entities.Supplier;
import app.entities.User;
import app.entities.enums.OrderStatus;
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

class OrderItemServiceImplTest {

    private static OrderItemServiceImpl orderItemService;
    private static ExecutorService executorService;

    private final OrderItemDAO orderItemDAO = new OrderItemDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final ItemDAO itemDAO = new ItemDAO();
    private final UserDAO userDAO = new UserDAO();
    private final AddressDAO addressDAO = new AddressDAO();
    private final ItemCategoryDAO itemCategoryDAO = new ItemCategoryDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();

    @BeforeAll
    static void setUpAll() {

        HibernateConfig.setTest(true);
        executorService = Executors.newSingleThreadExecutor();
        orderItemService = new OrderItemServiceImpl(new OrderItemDAO(), executorService);
    }

    @BeforeEach
    void setUp() throws DatabaseException {

        orderItemDAO.deleteAll();
        orderDAO.deleteAll();
        itemDAO.deleteAll();
        supplierDAO.deleteAll();
        itemCategoryDAO.deleteAll();
        userDAO.deleteAll();
        addressDAO.deleteAll();
    }

    @AfterAll
    static void tearDownAll() {
        executorService.shutdown();
        EMF.close();
    }

    @Test
    void create_shouldThrow_whenQuantityIsZero() throws DatabaseException {

        Order order = createOrder();
        Item item = createItem();

        CreateOrderItemDTO dto = new CreateOrderItemDTO(order.getId(), item.getId(), 0);

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> orderItemService.create(dto).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Quantity must be greater than 0", ex.getCause().getMessage());
    }

    @Test
    void create_shouldThrow_whenOrderDoesNotExist() throws DatabaseException {

        Item item = createItem();

        CreateOrderItemDTO dto = new CreateOrderItemDTO(999999, item.getId(), 2);

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> orderItemService.create(dto).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Order not found", ex.getCause().getMessage());
    }

    @Test
    void create_shouldThrow_whenItemDoesNotExist() throws DatabaseException {

        Order order = createOrder();

        CreateOrderItemDTO dto = new CreateOrderItemDTO(order.getId(), 999999, 2);

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> orderItemService.create(dto).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Item not found", ex.getCause().getMessage());
    }

    @Test
    void getAllByOrderId_shouldReturnOrderItemsForOrder() throws DatabaseException {

        Order order = createOrder();
        Item item = createItem();

        createOrderItem(order, item, 2);
        createOrderItem(order, item, 3);

        List<OrderItemResponseDTO> result = orderItemService.getAllByOrderId(order.getId()).join();

        assertEquals(2, result.size());
    }

    @Test
    void getAllByItemId_shouldReturnOrderItemsForItem() throws DatabaseException {

        Order order = createOrder();
        Item item = createItem();

        createOrderItem(order, item, 2);
        createOrderItem(order, item, 3);

        List<OrderItemResponseDTO> result = orderItemService.getAllByItemId(item.getId()).join();

        assertEquals(2, result.size());
    }

    private User createUser() throws DatabaseException {

        User user = new User();
        user.setEmail("user@test.dk");
        user.setName("User");
        user.setUsername("user");
        user.setPasswordHash("test123");
        user.setWallet(BigDecimal.ZERO);
        user.setRole(Role.USER);
        return userDAO.create(user);
    }

    private Address createAddress() throws DatabaseException {

        Address address = new Address();
        return addressDAO.create(address);
    }

    private Order createOrder() throws DatabaseException {

        User user = createUser();
        Address address = createAddress();

        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setOrderStatus(OrderStatus.CREATED);
        return orderDAO.create(order);
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

    private OrderItem createOrderItem(Order order, Item item, int quantity) throws DatabaseException {

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setItem(item);
        orderItem.setQuantity(quantity);
        orderItem.setPriceAtPurchase(item.getBasePrice());
        return orderItemDAO.create(orderItem);
    }
}
package app.dao;

import app.config.HibernateConfig;
import app.entities.Address;
import app.entities.Order;
import app.entities.OrderItem;
import app.entities.User;
import app.entities.Item;
import app.entities.ItemCategory;
import app.entities.Supplier;
import app.entities.enums.OrderStatus;
import app.entities.enums.Role;
import app.exceptions.DatabaseException;
import app.utils.EMF;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderDAOTest {

    private OrderDAO orderDAO;
    private UserDAO userDAO;
    private AddressDAO addressDAO;
    private OrderItemDAO orderItemDAO;
    private ItemDAO itemDAO;
    private ItemCategoryDAO itemCategoryDAO;
    private SupplierDAO supplierDAO;

    @BeforeAll
    void setUpAll() {

        HibernateConfig.setTest(true);
        orderDAO = new OrderDAO();
        userDAO = new UserDAO();
        addressDAO = new AddressDAO();
        orderItemDAO = new OrderItemDAO();
        itemDAO = new ItemDAO();
        itemCategoryDAO = new ItemCategoryDAO();
        supplierDAO = new SupplierDAO();
    }

    @BeforeEach
    void setUp() throws DatabaseException {

        orderItemDAO.deleteAll();
        orderDAO.deleteAll();
        itemDAO.deleteAll();
        itemCategoryDAO.deleteAll();
        supplierDAO.deleteAll();
        addressDAO.deleteAll();
        userDAO.deleteAll();
    }

    @AfterAll
    void tearDownAll() {
        EMF.close();
    }

    @Test
    void create_shouldPersistOrder() throws DatabaseException {

        User user = createTestUser();
        Address address = createTestAddress();

        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setOrderStatus(OrderStatus.CREATED);

        Order created = orderDAO.create(order);

        assertNotNull(created);
        assertTrue(created.getId() > 0);
        assertEquals(OrderStatus.CREATED, created.getOrderStatus());
    }

    @Test
    void getById_shouldReturnOrder_whenExists() throws DatabaseException {

        Order order = createTestOrder();

        Optional<Order> found = orderDAO.getById(order.getId());

        assertTrue(found.isPresent());
        assertEquals(order.getId(), found.get().getId());
    }

    @Test
    void getAll_shouldReturnAllOrders() throws DatabaseException {

        createTestOrder();
        createTestOrder();

        List<Order> orders = orderDAO.getAll();

        assertEquals(2, orders.size());
    }

    @Test
    void update_shouldUpdateOrder() throws DatabaseException {

        Order order = createTestOrder();

        order.setOrderStatus(OrderStatus.PAID);

        Order updated = orderDAO.update(order);

        assertEquals(OrderStatus.PAID, updated.getOrderStatus());
    }

    @Test
    void deleteById_shouldDeleteOrder() throws DatabaseException {

        Order order = createTestOrder();

        boolean deleted = orderDAO.deleteById(order.getId());

        assertTrue(deleted);
        assertTrue(orderDAO.getById(order.getId()).isEmpty());
    }

    @Test
    void getByIdWithItems_shouldReturnOrderWithItems() throws DatabaseException {

        Order order = createTestOrder();
        Item item = createTestItem();

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setItem(item);
        orderItem.setQuantity(2);
        orderItem.setPriceAtPurchase(BigDecimal.valueOf(100));
        orderItemDAO.create(orderItem);

        Optional<Order> found = orderDAO.getByIdWithItems(order.getId());

        assertTrue(found.isPresent());
        assertNotNull(found.get().getOrderItems());
        assertEquals(1, found.get().getOrderItems().size());
    }

    @Test
    void getAllByUserId_shouldReturnMatchingOrders() throws DatabaseException {

        User user = createTestUser();
        Address address = createTestAddress();

        Order order1 = new Order();
        order1.setUser(user);
        order1.setAddress(address);
        order1.setOrderStatus(OrderStatus.CREATED);
        orderDAO.create(order1);

        Order order2 = new Order();
        order2.setUser(user);
        order2.setAddress(address);
        order2.setOrderStatus(OrderStatus.PAID);
        orderDAO.create(order2);

        List<Order> orders = orderDAO.getAllByUserId(user.getId());

        assertEquals(2, orders.size());
        assertTrue(orders.stream().allMatch(o -> o.getUser().getId() == user.getId()));
    }

    @Test
    void getAllByStatus_shouldReturnMatchingOrders() throws DatabaseException {

        User user = createTestUser();
        Address address = createTestAddress();

        Order order1 = new Order();
        order1.setUser(user);
        order1.setAddress(address);
        order1.setOrderStatus(OrderStatus.CREATED);
        orderDAO.create(order1);

        Order order2 = new Order();
        order2.setUser(user);
        order2.setAddress(address);
        order2.setOrderStatus(OrderStatus.PAID);
        orderDAO.create(order2);

        List<Order> orders = orderDAO.getAllByStatus(OrderStatus.CREATED);

        assertEquals(1, orders.size());
        assertEquals(OrderStatus.CREATED, orders.get(0).getOrderStatus());
    }

    @Test
    void getTotalPriceByOrderId_shouldReturnTotalPrice() throws DatabaseException {

        Order order = createTestOrder();
        Item item = createTestItem();

        OrderItem orderItem1 = new OrderItem();
        orderItem1.setOrder(order);
        orderItem1.setItem(item);
        orderItem1.setQuantity(2);
        orderItem1.setPriceAtPurchase(BigDecimal.valueOf(100));
        orderItemDAO.create(orderItem1);

        OrderItem orderItem2 = new OrderItem();
        orderItem2.setOrder(order);
        orderItem2.setItem(item);
        orderItem2.setQuantity(1);
        orderItem2.setPriceAtPurchase(BigDecimal.valueOf(50));
        orderItemDAO.create(orderItem2);

        BigDecimal total = orderDAO.getTotalPriceByOrderId(order.getId());

        assertEquals(BigDecimal.valueOf(250), total);
    }

    @Test
    void countByUserId_shouldReturnCorrectCount() throws DatabaseException {

        User user = createTestUser();
        Address address = createTestAddress();

        Order order1 = new Order();
        order1.setUser(user);
        order1.setAddress(address);
        order1.setOrderStatus(OrderStatus.CREATED);
        orderDAO.create(order1);

        Order order2 = new Order();
        order2.setUser(user);
        order2.setAddress(address);
        order2.setOrderStatus(OrderStatus.PAID);
        orderDAO.create(order2);

        long count = orderDAO.countByUserId(user.getId());

        assertEquals(2, count);
    }

    private User createTestUser() throws DatabaseException {

        return userDAO.create(new User("user@mail.com", "User", "user1", "pw", Role.USER));
    }

    private Address createTestAddress() throws DatabaseException {
        Address address = new Address();
        address.setStreet("Test Street 1");
        address.setPostalCode("2800");
        address.setCity("Lyngby");
        address.setCountry("Denmark");
        return addressDAO.create(address);
    }

    private Order createTestOrder() throws DatabaseException {

        User user = createTestUser();
        Address address = createTestAddress();

        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setOrderStatus(OrderStatus.CREATED);

        return orderDAO.create(order);
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
package app.dao;

import app.config.HibernateConfig;
import app.entities.*;
import app.entities.Order;
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
class OrderItemDAOTest {

    private OrderItemDAO orderItemDAO;
    private OrderDAO orderDAO;
    private ItemDAO itemDAO;
    private ItemCategoryDAO itemCategoryDAO;
    private SupplierDAO supplierDAO;
    private UserDAO userDAO;
    private AddressDAO addressDAO;

    @BeforeAll
    void setUpAll() {

        HibernateConfig.setTest(true);
        orderItemDAO = new OrderItemDAO();
        orderDAO = new OrderDAO();
        itemDAO = new ItemDAO();
        itemCategoryDAO = new ItemCategoryDAO();
        supplierDAO = new SupplierDAO();
        userDAO = new UserDAO();
        addressDAO = new AddressDAO();
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
    void create_shouldPersistOrderItem() throws DatabaseException {

        Order order = createTestOrder();
        Item item = createTestItem();

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setItem(item);
        orderItem.setQuantity(2);
        orderItem.setPriceAtPurchase(BigDecimal.valueOf(100));

        OrderItem created = orderItemDAO.create(orderItem);

        assertNotNull(created);
        assertTrue(created.getId() > 0);
        assertEquals(2, created.getQuantity());
    }

    @Test
    void getById_shouldReturnOrderItem_whenExists() throws DatabaseException {

        Order order = createTestOrder();
        Item item = createTestItem();

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setItem(item);
        orderItem.setQuantity(2);
        orderItem.setPriceAtPurchase(BigDecimal.valueOf(100));

        OrderItem created = orderItemDAO.create(orderItem);

        Optional<OrderItem> found = orderItemDAO.getById(created.getId());

        assertTrue(found.isPresent());
        assertEquals(created.getId(), found.get().getId());
    }

    @Test
    void getAll_shouldReturnAllOrderItems() throws DatabaseException {

        Order order = createTestOrder();
        Item item = createTestItem();

        OrderItem o1 = new OrderItem();
        o1.setOrder(order);
        o1.setItem(item);
        o1.setQuantity(1);
        o1.setPriceAtPurchase(BigDecimal.valueOf(100));

        OrderItem o2 = new OrderItem();
        o2.setOrder(order);
        o2.setItem(item);
        o2.setQuantity(3);
        o2.setPriceAtPurchase(BigDecimal.valueOf(100));

        orderItemDAO.create(o1);
        orderItemDAO.create(o2);

        List<OrderItem> orderItems = orderItemDAO.getAll();

        assertEquals(2, orderItems.size());
    }

    @Test
    void update_shouldUpdateOrderItem() throws DatabaseException {

        Order order = createTestOrder();
        Item item = createTestItem();

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setItem(item);
        orderItem.setQuantity(2);
        orderItem.setPriceAtPurchase(BigDecimal.valueOf(100));

        OrderItem created = orderItemDAO.create(orderItem);

        created.setQuantity(5);

        OrderItem updated = orderItemDAO.update(created);

        assertEquals(5, updated.getQuantity());
    }

    @Test
    void deleteById_shouldDeleteOrderItem() throws DatabaseException {

        Order order = createTestOrder();
        Item item = createTestItem();

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setItem(item);
        orderItem.setQuantity(2);
        orderItem.setPriceAtPurchase(BigDecimal.valueOf(100));

        OrderItem created = orderItemDAO.create(orderItem);

        boolean deleted = orderItemDAO.deleteById(created.getId());

        assertTrue(deleted);
        assertTrue(orderItemDAO.getById(created.getId()).isEmpty());
    }

    @Test
    void getAllByOrderId_shouldReturnMatchingOrderItems() throws DatabaseException {

        Order order = createTestOrder();
        Item item = createTestItem();

        OrderItem o1 = new OrderItem();
        o1.setOrder(order);
        o1.setItem(item);
        o1.setQuantity(1);
        o1.setPriceAtPurchase(BigDecimal.valueOf(100));

        OrderItem o2 = new OrderItem();
        o2.setOrder(order);
        o2.setItem(item);
        o2.setQuantity(2);
        o2.setPriceAtPurchase(BigDecimal.valueOf(100));

        orderItemDAO.create(o1);
        orderItemDAO.create(o2);

        List<OrderItem> orderItems = orderItemDAO.getAllByOrderId(order.getId());

        assertEquals(2, orderItems.size());
        assertTrue(orderItems.stream().allMatch(oi -> oi.getOrder().getId() == order.getId()));
    }

    @Test
    void getAllByItemId_shouldReturnMatchingOrderItems() throws DatabaseException {

        Order order = createTestOrder();
        Item item = createTestItem();

        OrderItem o1 = new OrderItem();
        o1.setOrder(order);
        o1.setItem(item);
        o1.setQuantity(1);
        o1.setPriceAtPurchase(BigDecimal.valueOf(100));

        OrderItem o2 = new OrderItem();
        o2.setOrder(order);
        o2.setItem(item);
        o2.setQuantity(2);
        o2.setPriceAtPurchase(BigDecimal.valueOf(100));

        orderItemDAO.create(o1);
        orderItemDAO.create(o2);

        List<OrderItem> orderItems = orderItemDAO.getAllByItemId(item.getId());

        assertEquals(2, orderItems.size());
        assertTrue(orderItems.stream().allMatch(oi -> oi.getItem().getId() == item.getId()));
    }

    private Order createTestOrder() throws DatabaseException {

        User user = userDAO.create(new User("user@mail.com", "User", "user1", "pw", Role.USER));

        Address address = new Address();
        address.setStreet("Test Street 1");
        address.setPostalCode("2800");
        address.setCity("Lyngby");
        address.setCountry("Denmark");
        address = addressDAO.create(address);

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
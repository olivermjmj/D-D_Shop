package app.service.impl;

import app.config.HibernateConfig;
import app.dao.AddressDAO;
import app.dao.OrderDAO;
import app.dao.UserDAO;
import app.dto.order.CreateOrderDTO;
import app.dto.order.OrderResponseDTO;
import app.dto.order.UpdateOrderDTO;
import app.dto.order.UpdateOrderStatusDTO;
import app.entities.Address;
import app.entities.Order;
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

class OrderServiceImplTest {

    private static OrderServiceImpl orderService;
    private static ExecutorService executorService;

    private final OrderDAO orderDAO = new OrderDAO();
    private final UserDAO userDAO = new UserDAO();
    private final AddressDAO addressDAO = new AddressDAO();

    @BeforeAll
    static void setUpAll() {
        HibernateConfig.setTest(true);
        executorService = Executors.newSingleThreadExecutor();
        orderService = new OrderServiceImpl(new OrderDAO(), executorService);
    }

    @BeforeEach
    void setUp() throws DatabaseException {
        orderDAO.deleteAll();
        userDAO.deleteAll();
        addressDAO.deleteAll();
    }

    @AfterAll
    static void tearDownAll() {
        executorService.shutdown();
        EMF.close();
    }

    @Test
    void createForUser_shouldCreateOrder() throws DatabaseException {
        User user = createUser();
        Address address = createAddress();

        CreateOrderDTO dto = new CreateOrderDTO(address.getId());

        OrderResponseDTO result = orderService.createForUser(dto, user.getId()).join();

        assertNotNull(result);
        assertEquals(user.getId(), result.userId());
        assertEquals(address.getId(), result.addressId());
        assertEquals(OrderStatus.CREATED, result.orderStatus());
    }

    @Test
    void createForUser_shouldThrow_whenUserDoesNotExist() throws DatabaseException {
        Address address = createAddress();

        CreateOrderDTO dto = new CreateOrderDTO(address.getId());

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> orderService.createForUser(dto, 999999).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("User not found", ex.getCause().getMessage());
    }

    @Test
    void createForUser_shouldThrow_whenAddressDoesNotExist() throws DatabaseException {
        User user = createUser();

        CreateOrderDTO dto = new CreateOrderDTO(999999);

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> orderService.createForUser(dto, user.getId()).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Address not found", ex.getCause().getMessage());
    }

    @Test
    void getMyOrders_shouldReturnOrdersForUser() throws DatabaseException {
        User user = createUser();
        Address address = createAddress();

        createOrder(user, address, OrderStatus.CREATED);
        createOrder(user, address, OrderStatus.SHIPPED);

        List<OrderResponseDTO> result = orderService.getMyOrders(user.getId()).join();

        assertEquals(2, result.size());
    }

    @Test
    void getAllByUserId_shouldReturnOrdersForUser() throws DatabaseException {
        User user = createUser();
        Address address = createAddress();

        createOrder(user, address, OrderStatus.CREATED);
        createOrder(user, address, OrderStatus.SHIPPED);

        List<OrderResponseDTO> result = orderService.getAllByUserId(user.getId()).join();

        assertEquals(2, result.size());
    }

    @Test
    void getAllByUserId_shouldThrow_whenUserDoesNotExist() {
        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> orderService.getAllByUserId(999999).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("User not found", ex.getCause().getMessage());
    }

    @Test
    void getAllByStatus_shouldReturnOnlyMatchingStatus() throws DatabaseException {
        User user = createUser();
        Address address = createAddress();

        createOrder(user, address, OrderStatus.CREATED);
        createOrder(user, address, OrderStatus.SHIPPED);
        createOrder(user, address, OrderStatus.CREATED);

        List<OrderResponseDTO> result = orderService.getAllByStatus(OrderStatus.CREATED).join();

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(o -> o.orderStatus() == OrderStatus.CREATED));
    }

    @Test
    void getTotalPriceForUser_shouldThrow_whenOrderDoesNotExist() {
        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> orderService.getTotalPriceForUser(999999, 1, Role.USER).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Order not found", ex.getCause().getMessage());
    }

    @Test
    void updateAddressForUser_shouldUpdateAddress() throws DatabaseException {
        User user = createUser();
        Address oldAddress = createAddress();
        Address newAddress = createSecondAddress();

        Order order = createOrder(user, oldAddress, OrderStatus.CREATED);

        UpdateOrderDTO dto = new UpdateOrderDTO(newAddress.getId());

        OrderResponseDTO result = orderService
                .updateAddressForUser(order.getId(), dto, user.getId(), Role.USER)
                .join();

        assertEquals(newAddress.getId(), result.addressId());
    }

    @Test
    void updateStatus_shouldUpdateOrderStatus() throws DatabaseException {
        User user = createUser();
        Address address = createAddress();

        Order order = createOrder(user, address, OrderStatus.CREATED);

        UpdateOrderStatusDTO dto = new UpdateOrderStatusDTO(OrderStatus.SHIPPED);

        OrderResponseDTO result = orderService.updateStatus(order.getId(), dto).join();

        assertEquals(OrderStatus.SHIPPED, result.orderStatus());
    }

    @Test
    void getByIdForUser_shouldThrow_whenUserDoesNotOwnOrder() throws DatabaseException {
        User owner = createUser();
        User otherUser = createSecondUser();
        Address address = createAddress();

        Order order = createOrder(owner, address, OrderStatus.CREATED);

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> orderService.getByIdForUser(order.getId(), otherUser.getId(), Role.USER).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Forbidden", ex.getCause().getMessage());
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

    private User createSecondUser() throws DatabaseException {
        User user = new User();
        user.setEmail("other@test.dk");
        user.setName("Other User");
        user.setUsername("otheruser");
        user.setPasswordHash("test123");
        user.setWallet(BigDecimal.ZERO);
        user.setRole(Role.USER);

        return userDAO.create(user);
    }

    private Address createAddress() throws DatabaseException {
        Address address = new Address();
        address.setStreet("Testvej 1");
        address.setPostalCode("2800");
        address.setCity("Lyngby");
        address.setCountry("Denmark");

        return addressDAO.create(address);
    }

    private Address createSecondAddress() throws DatabaseException {
        Address address = new Address();
        address.setStreet("Andenvej 2");
        address.setPostalCode("4000");
        address.setCity("Roskilde");
        address.setCountry("Denmark");

        return addressDAO.create(address);
    }

    private Order createOrder(User user, Address address, OrderStatus status) throws DatabaseException {
        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setOrderStatus(status);

        return orderDAO.create(order);
    }
}
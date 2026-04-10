package app.service.impl;

import app.config.HibernateConfig;
import app.dao.AddressDAO;
import app.dao.OrderDAO;
import app.dao.UserDAO;
import app.dto.order.CreateOrderDTO;
import app.dto.order.OrderResponseDTO;
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
    void create_shouldCreateOrder() throws DatabaseException {

        User user = createUser();
        Address address = createAddress();

        CreateOrderDTO dto = new CreateOrderDTO(user.getId(), address.getId());

        OrderResponseDTO result = orderService.create(dto).join();

        assertNotNull(result);
        assertEquals(user.getId(), result.userId());
        assertEquals(address.getId(), result.addressId());
        assertEquals(OrderStatus.CREATED, result.orderStatus());
    }

    @Test
    void create_shouldThrow_whenUserDoesNotExist() throws DatabaseException {

        Address address = createAddress();

        CreateOrderDTO dto = new CreateOrderDTO(999999, address.getId());

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> orderService.create(dto).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("User not found", ex.getCause().getMessage());
    }

    @Test
    void create_shouldThrow_whenAddressDoesNotExist() throws DatabaseException {

        User user = createUser();

        CreateOrderDTO dto = new CreateOrderDTO(user.getId(), 999999);

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> orderService.create(dto).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Address not found", ex.getCause().getMessage());
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
    void getTotalPriceByOrderId_shouldThrow_whenOrderDoesNotExist() {

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> orderService.getTotalPriceByOrderId(999999).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Order not found", ex.getCause().getMessage());
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

    private Order createOrder(User user, Address address, OrderStatus status) throws DatabaseException {

        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setOrderStatus(status);
        return orderDAO.create(order);
    }
}
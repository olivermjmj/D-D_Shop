package app.service.impl;

import app.config.HibernateConfig;
import app.dao.OrderDAO;
import app.dao.TransactionDAO;
import app.dao.UserDAO;
import app.dto.transaction.TransactionResponseDTO;
import app.entities.Order;
import app.entities.Transaction;
import app.entities.User;
import app.entities.enums.Role;
import app.entities.enums.TransactionType;
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

import static org.junit.jupiter.api.Assertions.*;

class TransactionServiceImplTest {

    private static TransactionServiceImpl service;

    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final UserDAO userDAO = new UserDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    @BeforeAll
    static void setUpAll() {

        HibernateConfig.setTest(true);
        service = new TransactionServiceImpl();
    }

    @BeforeEach
    void setUp() throws DatabaseException {

        transactionDAO.deleteAll();
        orderDAO.deleteAll();
        userDAO.deleteAll();
    }

    @AfterAll
    static void tearDownAll() {
        EMF.close();
    }

    @Test
    void getAllByUserId_shouldReturnTransactionsForUser() throws DatabaseException {

        User user = createUser("user1@test.dk");
        Order order = createOrder(user);

        createTransaction(user, order, new BigDecimal("100.00"), TransactionType.DEPOSIT);
        createTransaction(user, order, new BigDecimal("50.00"), TransactionType.PURCHASE);

        List<TransactionResponseDTO> result = service.getAllByUserId(user.getId()).join();

        assertEquals(2, result.size());
    }

    @Test
    void getAllByUserId_shouldThrow_whenUserDoesNotExist() {

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> service.getAllByUserId(999999).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("User not found", ex.getCause().getMessage());
    }

    @Test
    void getAllByOrderId_shouldReturnTransactionsForOrder() throws DatabaseException {

        User user = createUser("user2@test.dk");
        Order order = createOrder(user);

        createTransaction(user, order, new BigDecimal("120.00"), TransactionType.DEPOSIT);
        createTransaction(user, order, new BigDecimal("30.00"), TransactionType.REFUND);

        List<TransactionResponseDTO> result = service.getAllByOrderId(order.getId()).join();

        assertEquals(2, result.size());
    }

    @Test
    void getAllByOrderId_shouldThrow_whenOrderDoesNotExist() {

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> service.getAllByOrderId(999999).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Order not found", ex.getCause().getMessage());
    }

    @Test
    void getAllByType_shouldReturnOnlyMatchingType() throws DatabaseException {

        User user = createUser("user3@test.dk");
        Order order = createOrder(user);

        createTransaction(user, order, new BigDecimal("200.00"), TransactionType.DEPOSIT);
        createTransaction(user, order, new BigDecimal("70.00"), TransactionType.PURCHASE);
        createTransaction(user, order, new BigDecimal("40.00"), TransactionType.DEPOSIT);

        List<TransactionResponseDTO> result = service.getAllByType(TransactionType.DEPOSIT).join();

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t -> t.type() == TransactionType.DEPOSIT));
    }

    private User createUser(String email) throws DatabaseException {

        User user = new User();
        user.setEmail(email);
        user.setUsername(email.split("@")[0]);
        user.setPasswordHash("test123");
        user.setRole(Role.USER);
        return userDAO.create(user);
    }

    private Order createOrder(User user) throws DatabaseException {

        Order order = new Order();
        order.setUser(user);
        return orderDAO.create(order);
    }

    private Transaction createTransaction(User user, Order order, BigDecimal amount, TransactionType type) throws DatabaseException {

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setOrder(order);
        transaction.setAmount(amount);
        transaction.setType(type);
        return transactionDAO.create(transaction);
    }
}
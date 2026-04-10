package app.dao;

import app.config.HibernateConfig;
import app.entities.Address;
import app.entities.Order;
import app.entities.Transaction;
import app.entities.User;
import app.entities.enums.OrderStatus;
import app.entities.enums.Role;
import app.entities.enums.TransactionType;
import app.exceptions.DatabaseException;
import app.utils.EMF;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionDAOTest {

    private TransactionDAO transactionDAO;
    private UserDAO userDAO;
    private AddressDAO addressDAO;
    private OrderDAO orderDAO;

    @BeforeAll
    void setUpAll() {

        HibernateConfig.setTest(true);
        transactionDAO = new TransactionDAO();
        userDAO = new UserDAO();
        addressDAO = new AddressDAO();
        orderDAO = new OrderDAO();
    }

    @BeforeEach
    void setUp() throws DatabaseException {

        transactionDAO.deleteAll();
        orderDAO.deleteAll();
        addressDAO.deleteAll();
        userDAO.deleteAll();
    }

    @AfterAll
    void tearDownAll() {
        EMF.close();
    }

    @Test
    void create_shouldPersistTransaction() throws DatabaseException {

        User user = userDAO.create(new User("user@mail.com", "User", "user1", "pw", Role.USER));

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setAmount(BigDecimal.valueOf(100));
        transaction.setType(TransactionType.DEPOSIT);

        Transaction created = transactionDAO.create(transaction);

        assertNotNull(created);
        assertTrue(created.getId() > 0);
        assertEquals(TransactionType.DEPOSIT, created.getType());
    }

    @Test
    void getById_shouldReturnTransaction_whenExists() throws DatabaseException {

        User user = userDAO.create(new User("user@mail.com", "User", "user1", "pw", Role.USER));

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setAmount(BigDecimal.valueOf(100));
        transaction.setType(TransactionType.DEPOSIT);

        Transaction created = transactionDAO.create(transaction);

        Optional<Transaction> found = transactionDAO.getById(created.getId());

        assertTrue(found.isPresent());
        assertEquals(created.getId(), found.get().getId());
    }

    @Test
    void getAll_shouldReturnAllTransactions() throws DatabaseException {

        User user = userDAO.create(new User("user@mail.com", "User", "user1", "pw", Role.USER));

        Transaction t1 = new Transaction();
        t1.setUser(user);
        t1.setAmount(BigDecimal.valueOf(100));
        t1.setType(TransactionType.DEPOSIT);

        Transaction t2 = new Transaction();
        t2.setUser(user);
        t2.setAmount(BigDecimal.valueOf(50));
        t2.setType(TransactionType.REFUND);

        transactionDAO.create(t1);
        transactionDAO.create(t2);

        List<Transaction> transactions = transactionDAO.getAll();

        assertEquals(2, transactions.size());
    }

    @Test
    void update_shouldUpdateTransaction() throws DatabaseException {

        User user = userDAO.create(new User("user@mail.com", "User", "user1", "pw", Role.USER));

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setAmount(BigDecimal.valueOf(100));
        transaction.setType(TransactionType.DEPOSIT);

        Transaction created = transactionDAO.create(transaction);

        created.setAmount(BigDecimal.valueOf(200));
        created.setType(TransactionType.REFUND);

        Transaction updated = transactionDAO.update(created);

        assertEquals(BigDecimal.valueOf(200), updated.getAmount());
        assertEquals(TransactionType.REFUND, updated.getType());
    }

    @Test
    void deleteById_shouldDeleteTransaction() throws DatabaseException {

        User user = userDAO.create(new User("user@mail.com", "User", "user1", "pw", Role.USER));

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setAmount(BigDecimal.valueOf(100));
        transaction.setType(TransactionType.DEPOSIT);

        Transaction created = transactionDAO.create(transaction);

        boolean deleted = transactionDAO.deleteById(created.getId());

        assertTrue(deleted);
        assertTrue(transactionDAO.getById(created.getId()).isEmpty());
    }

    @Test
    void getAllByUserId_shouldReturnMatchingTransactions() throws DatabaseException {

        User user = userDAO.create(new User("user@mail.com", "User", "user1", "pw", Role.USER));

        Transaction t1 = new Transaction();
        t1.setUser(user);
        t1.setAmount(BigDecimal.valueOf(100));
        t1.setType(TransactionType.DEPOSIT);

        Transaction t2 = new Transaction();
        t2.setUser(user);
        t2.setAmount(BigDecimal.valueOf(50));
        t2.setType(TransactionType.REFUND);

        transactionDAO.create(t1);
        transactionDAO.create(t2);

        List<Transaction> transactions = transactionDAO.getAllByUserId(user.getId());

        assertEquals(2, transactions.size());
        assertTrue(transactions.stream().allMatch(t -> t.getUser().getId() == user.getId()));
    }

    @Test
    void getAllByOrderId_shouldReturnMatchingTransactions() throws DatabaseException {

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
        order = orderDAO.create(order);

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setOrder(order);
        transaction.setAmount(BigDecimal.valueOf(100));
        transaction.setType(TransactionType.PURCHASE);

        transactionDAO.create(transaction);

        List<Transaction> transactions = transactionDAO.getAllByOrderId(order.getId());

        assertEquals(1, transactions.size());
        assertEquals(order.getId(), transactions.get(0).getOrder().getId());
    }

    @Test
    void getAllByType_shouldReturnMatchingTransactions() throws DatabaseException {

        User user = userDAO.create(new User("user@mail.com", "User", "user1", "pw", Role.USER));

        Transaction t1 = new Transaction();
        t1.setUser(user);
        t1.setAmount(BigDecimal.valueOf(100));
        t1.setType(TransactionType.DEPOSIT);

        Transaction t2 = new Transaction();
        t2.setUser(user);
        t2.setAmount(BigDecimal.valueOf(50));
        t2.setType(TransactionType.REFUND);

        transactionDAO.create(t1);
        transactionDAO.create(t2);

        List<Transaction> transactions = transactionDAO.getAllByType(TransactionType.DEPOSIT);

        assertEquals(1, transactions.size());
        assertEquals(TransactionType.DEPOSIT, transactions.get(0).getType());
    }
}
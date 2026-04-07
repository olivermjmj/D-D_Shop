package app.dao;

import app.entities.User;
import app.entities.enums.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {

    /*

    private static EntityManagerFactory emf;
    private UserDAO userDAO;

    @BeforeAll
    static void init() {
        emf = Persistence.createEntityManagerFactory("test-pu");
    }

    @AfterAll
    static void close() {
        emf.close();
    }

    @BeforeEach
    void setup() {
        userDAO = new UserDAO(emf);
        clearDatabase();
    }

    private void clearDatabase() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createQuery("DELETE FROM User").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    private User createUser(String email, String username, Role role) {
        return userDAO.create(new User(email, "Test", username, "hash", role));
    }

    @Test
    void create() {
        User user = createUser("a@test.dk", "user1", Role.USER);
        assertNotEquals(0, user.getId());
    }

    @Test
    void getAll() {
        createUser("a@test.dk", "u1", Role.USER);
        createUser("b@test.dk", "u2", Role.USER);

        List<User> users = userDAO.getAll();
        assertEquals(2, users.size());
    }

    @Test
    void getById() {
        User user = createUser("c@test.dk", "u3", Role.USER);

        Optional<User> found = userDAO.getById(user.getId());
        assertTrue(found.isPresent());
    }

    @Test
    void update() {
        User user = createUser("d@test.dk", "u4", Role.USER);
        user.setName("Updated");

        userDAO.update(user);

        Optional<User> updated = userDAO.getById(user.getId());
        assertEquals("Updated", updated.get().getName());
    }

    @Test
    void delete() {
        User user = createUser("e@test.dk", "u5", Role.USER);

        userDAO.delete(user);

        Optional<User> deleted = userDAO.getById(user.getId());
        assertTrue(deleted.isEmpty());
    }

    @Test
    void existsByUsername() {
        createUser("f@test.dk", "uniqueUser", Role.USER);

        assertTrue(userDAO.existsByUsername("uniqueUser"));
        assertFalse(userDAO.existsByUsername("noUser"));
    }

    @Test
    void existsByEmail() {
        createUser("g@test.dk", "u7", Role.USER);

        assertTrue(userDAO.existsByEmail("g@test.dk"));
        assertFalse(userDAO.existsByEmail("none@test.dk"));
    }

    @Test
    void getByUsername() {
        createUser("h@test.dk", "lookupUser", Role.USER);

        Optional<User> found = userDAO.getByUsername("lookupUser");
        assertTrue(found.isPresent());
    }

    @Test
    void getByEmail() {
        createUser("i@test.dk", "u9", Role.USER);

        Optional<User> found = userDAO.getByEmail("i@test.dk");
        assertTrue(found.isPresent());
    }

    @Test
    void getAllByRole() {
        createUser("j@test.dk", "u10", Role.USER);
        createUser("k@test.dk", "u11", Role.ADMIN);

        List<User> users = userDAO.getAllByRole(Role.USER);
        assertEquals(1, users.size());
        assertEquals(Role.USER, users.get(0).getRole());
    }

    @Test
    void countByRole() {
        createUser("l@test.dk", "u12", Role.USER);
        createUser("m@test.dk", "u13", Role.USER);
        createUser("n@test.dk", "u14", Role.ADMIN);

        assertEquals(2, userDAO.countByRole(Role.USER));
        assertEquals(1, userDAO.countByRole(Role.ADMIN));
    }

     */
}
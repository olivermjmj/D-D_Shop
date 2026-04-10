package app.dao;

import app.config.HibernateConfig;
import app.entities.User;
import app.entities.enums.Role;
import app.exceptions.DatabaseException;
import app.utils.EMF;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDAOTest {

    private UserDAO userDAO;

    @BeforeAll
    void setUpAll() {

        HibernateConfig.setTest(true);
        userDAO = new UserDAO();
    }

    @BeforeEach
    void setUp() throws DatabaseException {
        userDAO.deleteAll();
    }

    @AfterAll
    void tearDownAll() {
        EMF.close();
    }

    @Test
    void create_shouldPersistUser() throws DatabaseException {

        User user = new User("test@mail.com", "Test User", "testuser", "hashedpw", Role.USER);

        User created = userDAO.create(user);

        assertNotNull(created);
        assertTrue(created.getId() > 0);
        assertEquals("test@mail.com", created.getEmail());
    }

    @Test
    void getById_shouldReturnUser_whenUserExists() throws DatabaseException {

        User user = new User("test@mail.com", "Test User", "testuser", "hashedpw", Role.USER);
        User created = userDAO.create(user);

        Optional<User> found = userDAO.getById(created.getId());

        assertTrue(found.isPresent());
        assertEquals(created.getId(), found.get().getId());
        assertEquals("test@mail.com", found.get().getEmail());
    }

    @Test
    void getById_shouldReturnEmpty_whenUserDoesNotExist() {

        Optional<User> found = userDAO.getById(9999);

        assertTrue(found.isEmpty());
    }

    @Test
    void getAll_shouldReturnAllUsers() throws DatabaseException {

        userDAO.create(new User("a@mail.com", "User A", "usera", "pw1", Role.USER));
        userDAO.create(new User("b@mail.com", "User B", "userb", "pw2", Role.ADMIN));

        List<User> users = userDAO.getAll();

        assertEquals(2, users.size());
    }

    @Test
    void update_shouldUpdateUser() throws DatabaseException {

        User user = new User("test@mail.com", "Test User", "testuser", "hashedpw", Role.USER);
        User created = userDAO.create(user);

        created.setName("Updated Name");
        created.setEmail("updated@mail.com");

        User updated = userDAO.update(created);

        assertEquals("Updated Name", updated.getName());
        assertEquals("updated@mail.com", updated.getEmail());
    }

    @Test
    void deleteById_shouldReturnTrue_whenUserExists() throws DatabaseException {

        User user = new User("test@mail.com", "Test User", "testuser", "hashedpw", Role.USER);
        User created = userDAO.create(user);

        boolean deleted = userDAO.deleteById(created.getId());

        assertTrue(deleted);
        assertTrue(userDAO.getById(created.getId()).isEmpty());
    }

    @Test
    void deleteById_shouldReturnFalse_whenUserDoesNotExist() throws DatabaseException {

        boolean deleted = userDAO.deleteById(9999);

        assertFalse(deleted);
    }

    @Test
    void deleteAll_shouldRemoveAllUsers() throws DatabaseException {

        userDAO.create(new User("a@mail.com", "User A", "usera", "pw1", Role.USER));
        userDAO.create(new User("b@mail.com", "User B", "userb", "pw2", Role.ADMIN));

        userDAO.deleteAll();

        List<User> users = userDAO.getAll();
        assertTrue(users.isEmpty());
    }

    @Test
    void countByRole_shouldReturnCorrectCount() throws DatabaseException {

        userDAO.create(new User("a@mail.com", "User A", "usera", "pw1", Role.USER));
        userDAO.create(new User("b@mail.com", "User B", "userb", "pw2", Role.USER));
        userDAO.create(new User("c@mail.com", "User C", "userc", "pw3", Role.ADMIN));

        long count = userDAO.countByRole(Role.USER);

        assertEquals(2, count);
    }

    @Test
    void getAllByRole_shouldReturnMatchingUsers() throws DatabaseException {

        userDAO.create(new User("a@mail.com", "User A", "usera", "pw1", Role.USER));
        userDAO.create(new User("b@mail.com", "User B", "userb", "pw2", Role.ADMIN));
        userDAO.create(new User("c@mail.com", "User C", "userc", "pw3", Role.USER));

        List<User> customers = userDAO.getAllByRole(Role.USER);

        assertEquals(2, customers.size());
        assertTrue(customers.stream().allMatch(user -> user.getRole() == Role.USER));
    }

    @Test
    void existsByUsername_shouldReturnTrue_whenUsernameExists() throws DatabaseException {

        userDAO.create(new User("test@mail.com", "Test User", "testuser", "hashedpw", Role.USER));

        boolean exists = userDAO.existsByUsername("testuser");

        assertTrue(exists);
    }

    @Test
    void existsByUsername_shouldReturnFalse_whenUsernameDoesNotExist() {

        boolean exists = userDAO.existsByUsername("unknown");

        assertFalse(exists);
    }

    @Test
    void existsByEmail_shouldReturnTrue_whenEmailExists() throws DatabaseException {

        userDAO.create(new User("test@mail.com", "Test User", "testuser", "hashedpw", Role.USER));

        boolean exists = userDAO.existsByEmail("test@mail.com");

        assertTrue(exists);
    }

    @Test
    void existsByEmail_shouldReturnFalse_whenEmailDoesNotExist() {

        boolean exists = userDAO.existsByEmail("unknown@mail.com");

        assertFalse(exists);
    }

    @Test
    void getByUsername_shouldReturnUser_whenUsernameExists() throws DatabaseException {

        userDAO.create(new User("test@mail.com", "Test User", "testuser", "hashedpw", Role.USER));

        Optional<User> found = userDAO.getByUsername("testuser");

        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
    }

    @Test
    void getByUsername_shouldReturnEmpty_whenUsernameDoesNotExist() {

        Optional<User> found = userDAO.getByUsername("unknown");

        assertTrue(found.isEmpty());
    }

    @Test
    void getByEmail_shouldReturnUser_whenEmailExists() throws DatabaseException {

        userDAO.create(new User("test@mail.com", "Test User", "testuser", "hashedpw", Role.USER));

        Optional<User> found = userDAO.getByEmail("test@mail.com");

        assertTrue(found.isPresent());
        assertEquals("test@mail.com", found.get().getEmail());
    }

    @Test
    void getByEmail_shouldReturnEmpty_whenEmailDoesNotExist() {

        Optional<User> found = userDAO.getByEmail("unknown@mail.com");

        assertTrue(found.isEmpty());
    }
}
package app.service.impl;

import app.config.HibernateConfig;
import app.dao.UserDAO;
import app.dto.user.CreateUserDTO;
import app.dto.user.UpdateUserDTO;
import app.dto.user.UserResponseDTO;
import app.entities.User;
import app.entities.enums.Role;
import app.exceptions.ApiException;
import app.exceptions.DatabaseException;
import app.utils.EMF;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceImplTest {

    private UserDAO userDAO;
    private UserServiceImpl userService;
    private ExecutorService executorService;

    @BeforeAll
    void setUpAll() {
        HibernateConfig.setTest(true);
        userDAO = new UserDAO();
        executorService = Executors.newSingleThreadExecutor();
        userService = new UserServiceImpl(userDAO, executorService);
    }

    @BeforeEach
    void setUp() throws DatabaseException {
        userDAO.deleteAll();
    }

    @AfterAll
    void tearDownAll() {
        executorService.shutdown();
        EMF.close();
    }

    @Test
    void create_shouldCreateUser() {
        CreateUserDTO dto = new CreateUserDTO(
                "test@mail.com",
                "Test User",
                "testuser",
                "1234",
                Role.USER
        );

        UserResponseDTO result = userService.create(dto).join();

        assertNotNull(result);
        assertEquals("test@mail.com", result.email());
        assertEquals("Test User", result.name());
        assertEquals("testuser", result.username());
        assertEquals(Role.USER, result.role());
        assertEquals(BigDecimal.ZERO, result.wallet());
    }

    @Test
    void create_shouldThrow_whenEmailAlreadyExists() throws DatabaseException {
        userDAO.create(new User("test@mail.com", "Existing User", "existinguser", "hashedpw", Role.USER));

        CreateUserDTO dto = new CreateUserDTO(
                "test@mail.com",
                "Test User",
                "testuser",
                "1234",
                Role.USER
        );

        CompletionException ex = assertThrows(CompletionException.class,
                () -> userService.create(dto).join());

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Email already exists", ex.getCause().getMessage());
    }

    @Test
    void create_shouldThrow_whenUsernameAlreadyExists() throws DatabaseException {
        userDAO.create(new User("existing@mail.com", "Existing User", "testuser", "hashedpw", Role.USER));

        CreateUserDTO dto = new CreateUserDTO(
                "test@mail.com",
                "Test User",
                "testuser",
                "1234",
                Role.USER
        );

        CompletionException ex = assertThrows(CompletionException.class,
                () -> userService.create(dto).join());

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Username already exists", ex.getCause().getMessage());
    }

    @Test
    void update_shouldUpdateUser() {
        CreateUserDTO createDto = new CreateUserDTO(
                "test@mail.com",
                "Test User",
                "testuser",
                "1234",
                Role.USER
        );

        UserResponseDTO created = userService.create(createDto).join();

        UpdateUserDTO updateDto = new UpdateUserDTO(
                "updated@mail.com",
                "Updated User",
                "updateduser",
                "5678",
                BigDecimal.valueOf(100),
                Role.ADMIN
        );

        UserResponseDTO updated = userService.update(created.id(), updateDto).join();

        assertEquals("updated@mail.com", updated.email());
        assertEquals("Updated User", updated.name());
        assertEquals("updateduser", updated.username());
        assertEquals(BigDecimal.valueOf(100), updated.wallet());
        assertEquals(Role.ADMIN, updated.role());
    }

    @Test
    void update_shouldThrow_whenEmailAlreadyExists() throws DatabaseException {
        User user1 = userDAO.create(new User("user1@mail.com", "User 1", "user1", "pw", Role.USER));
        userDAO.create(new User("user2@mail.com", "User 2", "user2", "pw", Role.USER));

        UpdateUserDTO dto = new UpdateUserDTO(
                "user2@mail.com",
                null,
                null,
                null,
                null,
                null
        );

        CompletionException ex = assertThrows(CompletionException.class,
                () -> userService.update(user1.getId(), dto).join());

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Email already exists", ex.getCause().getMessage());
    }

    @Test
    void update_shouldThrow_whenUsernameAlreadyExists() throws DatabaseException {
        User user1 = userDAO.create(new User("user1@mail.com", "User 1", "user1", "pw", Role.USER));
        userDAO.create(new User("user2@mail.com", "User 2", "user2", "pw", Role.USER));

        UpdateUserDTO dto = new UpdateUserDTO(
                null,
                null,
                "user2",
                null,
                null,
                null
        );

        CompletionException ex = assertThrows(CompletionException.class,
                () -> userService.update(user1.getId(), dto).join());

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Username already exists", ex.getCause().getMessage());
    }
}
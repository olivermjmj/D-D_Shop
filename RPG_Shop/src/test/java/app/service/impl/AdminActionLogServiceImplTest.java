package app.service.impl;

import app.config.HibernateConfig;
import app.dao.AdminActionLogDAO;
import app.dao.UserDAO;
import app.dto.adminActionLog.AdminActionLogResponseDTO;
import app.dto.adminActionLog.CreateAdminActionLogDTO;
import app.entities.AdminActionLog;
import app.entities.User;
import app.entities.enums.AdminActionType;
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

class AdminActionLogServiceImplTest {

    private static AdminActionLogServiceImpl adminActionLogService;
    private static ExecutorService executorService;

    private final AdminActionLogDAO adminActionLogDAO = new AdminActionLogDAO();
    private final UserDAO userDAO = new UserDAO();

    @BeforeAll
    static void setUpAll() {

        HibernateConfig.setTest(true);
        executorService = Executors.newSingleThreadExecutor();
        adminActionLogService = new AdminActionLogServiceImpl(new AdminActionLogDAO(), executorService);
    }

    @BeforeEach
    void setUp() throws DatabaseException {

        adminActionLogDAO.deleteAll();
        userDAO.deleteAll();
    }

    @AfterAll
    static void tearDownAll() {

        executorService.shutdown();
        EMF.close();
    }

    @Test
    void create_shouldThrow_whenActionIsNull() throws DatabaseException {

        User admin = createAdmin();

        CreateAdminActionLogDTO dto = new CreateAdminActionLogDTO(
                admin.getId(),
                null,
                "Item",
                1
        );

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> adminActionLogService.create(dto).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Action is required", ex.getCause().getMessage());
    }

    @Test
    void create_shouldThrow_whenTargetTypeIsBlank() throws DatabaseException {

        User admin = createAdmin();

        CreateAdminActionLogDTO dto = new CreateAdminActionLogDTO(
                admin.getId(),
                AdminActionType.CREATE,
                "   ",
                1
        );

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> adminActionLogService.create(dto).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Target type is required", ex.getCause().getMessage());
    }

    @Test
    void create_shouldThrow_whenAdminUserDoesNotExist() {

        CreateAdminActionLogDTO dto = new CreateAdminActionLogDTO(
                999999,
                AdminActionType.CREATE,
                "Item",
                1
        );

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> adminActionLogService.create(dto).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Admin user not found", ex.getCause().getMessage());
    }

    @Test
    void create_shouldThrow_whenUserIsNotAdmin() throws DatabaseException {

        User user = createUser();

        CreateAdminActionLogDTO dto = new CreateAdminActionLogDTO(
                user.getId(),
                AdminActionType.CREATE,
                "Item",
                1
        );

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> adminActionLogService.create(dto).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("User is not an admin", ex.getCause().getMessage());
    }

    @Test
    void getAllByAdminId_shouldReturnLogsForAdmin() throws DatabaseException {

        User admin = createAdmin();

        createAdminActionLog(admin, AdminActionType.CREATE, "Item", 1);
        createAdminActionLog(admin, AdminActionType.UPDATE, "Item", 2);

        List<AdminActionLogResponseDTO> result = adminActionLogService.getAllByAdminId(admin.getId()).join();

        assertEquals(2, result.size());
    }

    @Test
    void getAllByAdminId_shouldThrow_whenAdminUserDoesNotExist() {

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> adminActionLogService.getAllByAdminId(999999).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Admin user not found", ex.getCause().getMessage());
    }

    @Test
    void getAllByAdminId_shouldThrow_whenUserIsNotAdmin() throws DatabaseException {

        User user = createUser();

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> adminActionLogService.getAllByAdminId(user.getId()).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("User is not an admin", ex.getCause().getMessage());
    }

    private User createAdmin() throws DatabaseException {

        User user = new User();
        user.setEmail("admin@test.dk");
        user.setName("Admin");
        user.setUsername("admin");
        user.setPasswordHash("test123");
        user.setWallet(BigDecimal.ZERO);
        user.setRole(Role.ADMIN);
        return userDAO.create(user);
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

    private AdminActionLog createAdminActionLog(User admin, AdminActionType action, String targetType, Integer targetId) throws DatabaseException {

        AdminActionLog log = new AdminActionLog();
        log.setAdmin(admin);
        log.setAction(action);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        return adminActionLogDAO.create(log);
    }
}
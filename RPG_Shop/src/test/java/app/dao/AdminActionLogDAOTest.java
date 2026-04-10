package app.dao;

import app.config.HibernateConfig;
import app.entities.AdminActionLog;
import app.entities.User;
import app.entities.enums.AdminActionType;
import app.entities.enums.Role;
import app.exceptions.DatabaseException;
import app.utils.EMF;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdminActionLogDAOTest {

    private AdminActionLogDAO adminActionLogDAO;
    private UserDAO userDAO;

    @BeforeAll
    void setUpAll() {

        HibernateConfig.setTest(true);
        adminActionLogDAO = new AdminActionLogDAO();
        userDAO = new UserDAO();
    }

    @BeforeEach
    void setUp() throws DatabaseException {

        adminActionLogDAO.deleteAll();
        userDAO.deleteAll();
    }

    @AfterAll
    void tearDownAll() {
        EMF.close();
    }

    @Test
    void create_shouldPersistAdminActionLog() throws DatabaseException {

        User admin = createTestAdmin();

        AdminActionLog log = new AdminActionLog();
        log.setAdmin(admin);
        log.setAction(AdminActionType.CREATE);
        log.setTargetType("Item");
        log.setTargetId(1);

        AdminActionLog created = adminActionLogDAO.create(log);

        assertNotNull(created);
        assertTrue(created.getId() > 0);
        assertEquals(AdminActionType.CREATE, created.getAction());
    }

    @Test
    void getById_shouldReturnAdminActionLog_whenExists() throws DatabaseException {

        User admin = createTestAdmin();

        AdminActionLog log = new AdminActionLog();
        log.setAdmin(admin);
        log.setAction(AdminActionType.CREATE);
        log.setTargetType("Item");
        log.setTargetId(1);

        AdminActionLog created = adminActionLogDAO.create(log);

        Optional<AdminActionLog> found = adminActionLogDAO.getById(created.getId());

        assertTrue(found.isPresent());
        assertEquals(created.getId(), found.get().getId());
    }

    @Test
    void getAll_shouldReturnAllAdminActionLogs() throws DatabaseException {

        User admin = createTestAdmin();

        AdminActionLog log1 = new AdminActionLog();
        log1.setAdmin(admin);
        log1.setAction(AdminActionType.CREATE);
        log1.setTargetType("Item");
        log1.setTargetId(1);

        AdminActionLog log2 = new AdminActionLog();
        log2.setAdmin(admin);
        log2.setAction(AdminActionType.UPDATE);
        log2.setTargetType("User");
        log2.setTargetId(2);

        adminActionLogDAO.create(log1);
        adminActionLogDAO.create(log2);

        List<AdminActionLog> logs = adminActionLogDAO.getAll();

        assertEquals(2, logs.size());
    }

    @Test
    void update_shouldUpdateAdminActionLog() throws DatabaseException {

        User admin = createTestAdmin();

        AdminActionLog log = new AdminActionLog();
        log.setAdmin(admin);
        log.setAction(AdminActionType.CREATE);
        log.setTargetType("Item");
        log.setTargetId(1);

        AdminActionLog created = adminActionLogDAO.create(log);

        created.setAction(AdminActionType.DELETE);
        created.setTargetType("Order");
        created.setTargetId(3);

        AdminActionLog updated = adminActionLogDAO.update(created);

        assertEquals(AdminActionType.DELETE, updated.getAction());
        assertEquals("Order", updated.getTargetType());
        assertEquals(3, updated.getTargetId());
    }

    @Test
    void deleteById_shouldDeleteAdminActionLog() throws DatabaseException {

        User admin = createTestAdmin();

        AdminActionLog log = new AdminActionLog();
        log.setAdmin(admin);
        log.setAction(AdminActionType.CREATE);
        log.setTargetType("Item");
        log.setTargetId(1);

        AdminActionLog created = adminActionLogDAO.create(log);

        boolean deleted = adminActionLogDAO.deleteById(created.getId());

        assertTrue(deleted);
        assertTrue(adminActionLogDAO.getById(created.getId()).isEmpty());
    }

    @Test
    void getAllByAdminId_shouldReturnMatchingLogs() throws DatabaseException {

        User admin = createTestAdmin();

        AdminActionLog log1 = new AdminActionLog();
        log1.setAdmin(admin);
        log1.setAction(AdminActionType.CREATE);
        log1.setTargetType("Item");
        log1.setTargetId(1);

        AdminActionLog log2 = new AdminActionLog();
        log2.setAdmin(admin);
        log2.setAction(AdminActionType.UPDATE);
        log2.setTargetType("User");
        log2.setTargetId(2);

        adminActionLogDAO.create(log1);
        adminActionLogDAO.create(log2);

        List<AdminActionLog> logs = adminActionLogDAO.getAllByAdminId(admin.getId());

        assertEquals(2, logs.size());
        assertTrue(logs.stream().allMatch(log -> log.getAdmin().getId() == admin.getId()));
    }

    private User createTestAdmin() throws DatabaseException {
        return userDAO.create(new User("admin@mail.com", "Admin", "admin1", "pw", Role.ADMIN));
    }
}
package app.service.impl;

import app.config.HibernateConfig;
import app.dao.StampDAO;
import app.dto.stamp.CreateStampDTO;
import app.dto.stamp.StampResponseDTO;
import app.dto.stamp.UpdateStampDTO;
import app.entities.Stamp;
import app.exceptions.ApiException;
import app.exceptions.DatabaseException;
import app.utils.EMF;
import org.junit.jupiter.api.*;

import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StampServiceImplTest {

    private StampDAO stampDAO;
    private StampServiceImpl stampService;
    private ExecutorService executorService;

    @BeforeAll
    void setUpAll() {

        HibernateConfig.setTest(true);
        stampDAO = new StampDAO();
        executorService = Executors.newSingleThreadExecutor();
        stampService = new StampServiceImpl(stampDAO, executorService);
    }

    @BeforeEach
    void setUp() throws DatabaseException {
        stampDAO.deleteAll();
    }

    @AfterAll
    void tearDownAll() {

        executorService.shutdown();
        EMF.close();
    }

    @Test
    void create_shouldCreateStamp() {

        CreateStampDTO dto = new CreateStampDTO("Rare");

        StampResponseDTO result = stampService.create(dto).join();

        assertNotNull(result);
        assertEquals("Rare", result.name());
    }

    @Test
    void create_shouldThrow_whenNameIsNull() {

        CreateStampDTO dto = new CreateStampDTO(null);

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> stampService.create(dto).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Stamp name cannot be blank", ex.getCause().getMessage());
    }

    @Test
    void create_shouldThrow_whenNameIsBlank() {

        CreateStampDTO dto = new CreateStampDTO("   ");

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> stampService.create(dto).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Stamp name cannot be blank", ex.getCause().getMessage());
    }

    @Test
    void update_shouldUpdateName() throws DatabaseException {

        Stamp stamp = new Stamp();
        stamp.setName("Old");
        stamp = stampDAO.create(stamp);

        UpdateStampDTO dto = new UpdateStampDTO("New");

        StampResponseDTO result = stampService.update(stamp.getId(), dto).join();

        assertEquals("New", result.name());
    }

    @Test
    void update_shouldThrow_whenNameIsBlank() throws DatabaseException {

        Stamp stamp = new Stamp();
        stamp.setName("Old");
        Stamp finalStamp = stampDAO.create(stamp);

        UpdateStampDTO dto = new UpdateStampDTO("   ");

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> stampService.update(finalStamp.getId(), dto).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Stamp name cannot be blank", ex.getCause().getMessage());
    }
}
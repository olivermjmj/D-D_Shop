package app.dao;

import app.config.HibernateConfig;
import app.entities.Stamp;
import app.exceptions.DatabaseException;
import app.utils.EMF;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StampDAOTest {

    private StampDAO stampDAO;

    @BeforeAll
    void setUpAll() {
        HibernateConfig.setTest(true);
        stampDAO = new StampDAO();
    }

    @BeforeEach
    void setUp() throws DatabaseException {
        stampDAO.deleteAll();
    }

    @AfterAll
    void tearDownAll() {
        EMF.close();
    }

    @Test
    void create_shouldPersistStamp() throws DatabaseException {

        Stamp stamp = new Stamp();
        stamp.setName("Rare");

        Stamp created = stampDAO.create(stamp);

        assertNotNull(created);
        assertTrue(created.getId() > 0);
        assertEquals("Rare", created.getName());
    }

    @Test
    void getById_shouldReturnStamp_whenExists() throws DatabaseException {

        Stamp stamp = new Stamp();
        stamp.setName("Rare");
        Stamp created = stampDAO.create(stamp);

        Optional<Stamp> found = stampDAO.getById(created.getId());

        assertTrue(found.isPresent());
        assertEquals(created.getId(), found.get().getId());
    }

    @Test
    void getAll_shouldReturnAllStamps() throws DatabaseException {

        Stamp s1 = new Stamp();
        s1.setName("Rare");

        Stamp s2 = new Stamp();
        s2.setName("Legendary");

        stampDAO.create(s1);
        stampDAO.create(s2);

        List<Stamp> stamps = stampDAO.getAll();

        assertEquals(2, stamps.size());
    }

    @Test
    void update_shouldUpdateStamp() throws DatabaseException {

        Stamp stamp = new Stamp();
        stamp.setName("Rare");
        Stamp created = stampDAO.create(stamp);

        created.setName("Epic");

        Stamp updated = stampDAO.update(created);

        assertEquals("Epic", updated.getName());
    }

    @Test
    void deleteById_shouldDeleteStamp() throws DatabaseException {

        Stamp stamp = new Stamp();
        stamp.setName("Rare");
        Stamp created = stampDAO.create(stamp);

        boolean deleted = stampDAO.deleteById(created.getId());

        assertTrue(deleted);
        assertTrue(stampDAO.getById(created.getId()).isEmpty());
    }
}
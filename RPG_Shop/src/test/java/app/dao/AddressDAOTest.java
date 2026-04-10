package app.dao;

import app.config.HibernateConfig;
import app.entities.Address;
import app.exceptions.DatabaseException;
import app.utils.EMF;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AddressDAOTest {

    private AddressDAO addressDAO;

    @BeforeAll
    void setUpAll() {

        HibernateConfig.setTest(true);
        addressDAO = new AddressDAO();
    }

    @BeforeEach
    void setUp() throws DatabaseException {
        addressDAO.deleteAll();
    }

    @AfterAll
    void tearDownAll() {
        EMF.close();
    }

    @Test
    void create_shouldPersistAddress() throws DatabaseException {

        Address address = new Address();
        address.setStreet("Test Street 1");
        address.setPostalCode("2800");
        address.setCity("Lyngby");
        address.setCountry("Denmark");

        Address created = addressDAO.create(address);

        assertNotNull(created);
        assertTrue(created.getId() > 0);
        assertEquals("Test Street 1", created.getStreet());
    }

    @Test
    void getById_shouldReturnAddress_whenExists() throws DatabaseException {

        Address address = new Address();
        address.setStreet("Test Street 1");
        address.setPostalCode("2800");
        address.setCity("Lyngby");
        address.setCountry("Denmark");
        Address created = addressDAO.create(address);

        Optional<Address> found = addressDAO.getById(created.getId());

        assertTrue(found.isPresent());
        assertEquals(created.getId(), found.get().getId());
    }

    @Test
    void getAll_shouldReturnAllAddresses() throws DatabaseException {

        Address a1 = new Address();
        a1.setStreet("Street 1");
        a1.setPostalCode("2800");
        a1.setCity("Lyngby");
        a1.setCountry("Denmark");

        Address a2 = new Address();
        a2.setStreet("Street 2");
        a2.setPostalCode("2100");
        a2.setCity("Copenhagen");
        a2.setCountry("Denmark");

        addressDAO.create(a1);
        addressDAO.create(a2);

        List<Address> addresses = addressDAO.getAll();

        assertEquals(2, addresses.size());
    }

    @Test
    void update_shouldUpdateAddress() throws DatabaseException {

        Address address = new Address();
        address.setStreet("Old Street");
        address.setPostalCode("2800");
        address.setCity("Lyngby");
        address.setCountry("Denmark");
        Address created = addressDAO.create(address);

        created.setStreet("New Street");

        Address updated = addressDAO.update(created);

        assertEquals("New Street", updated.getStreet());
    }

    @Test
    void deleteById_shouldDeleteAddress() throws DatabaseException {

        Address address = new Address();
        address.setStreet("Test Street 1");
        address.setPostalCode("2800");
        address.setCity("Lyngby");
        address.setCountry("Denmark");
        Address created = addressDAO.create(address);

        boolean deleted = addressDAO.deleteById(created.getId());

        assertTrue(deleted);
        assertTrue(addressDAO.getById(created.getId()).isEmpty());
    }
}
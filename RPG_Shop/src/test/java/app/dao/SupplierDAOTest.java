package app.dao;

import app.config.HibernateConfig;
import app.entities.Address;
import app.entities.Supplier;
import app.exceptions.DatabaseException;
import app.utils.EMF;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SupplierDAOTest {

    private SupplierDAO supplierDAO;
    private AddressDAO addressDAO;

    @BeforeAll
    void setUpAll() {

        HibernateConfig.setTest(true);
        supplierDAO = new SupplierDAO();
        addressDAO = new AddressDAO();
    }

    @BeforeEach
    void setUp() throws DatabaseException {

        supplierDAO.deleteAll();
        addressDAO.deleteAll();
    }

    @AfterAll
    void tearDownAll() {
        EMF.close();
    }

    @Test
    void create_shouldPersistSupplier() throws DatabaseException {

        Supplier supplier = new Supplier();
        supplier.setName("Test Supplier");

        Supplier created = supplierDAO.create(supplier);

        assertNotNull(created);
        assertTrue(created.getId() > 0);
        assertEquals("Test Supplier", created.getName());
    }

    @Test
    void getById_shouldReturnSupplier_whenExists() throws DatabaseException {

        Supplier supplier = new Supplier();
        supplier.setName("Test Supplier");
        Supplier created = supplierDAO.create(supplier);

        Optional<Supplier> found = supplierDAO.getById(created.getId());

        assertTrue(found.isPresent());
        assertEquals(created.getId(), found.get().getId());
    }

    @Test
    void getAll_shouldReturnAllSuppliers() throws DatabaseException {

        Supplier s1 = new Supplier();
        s1.setName("Supplier 1");

        Supplier s2 = new Supplier();
        s2.setName("Supplier 2");

        supplierDAO.create(s1);
        supplierDAO.create(s2);

        List<Supplier> suppliers = supplierDAO.getAll();

        assertEquals(2, suppliers.size());
    }

    @Test
    void update_shouldUpdateSupplier() throws DatabaseException {

        Supplier supplier = new Supplier();
        supplier.setName("Old Name");
        Supplier created = supplierDAO.create(supplier);

        created.setName("New Name");

        Supplier updated = supplierDAO.update(created);

        assertEquals("New Name", updated.getName());
    }

    @Test
    void deleteById_shouldDeleteSupplier() throws DatabaseException {

        Supplier supplier = new Supplier();
        supplier.setName("Test Supplier");
        Supplier created = supplierDAO.create(supplier);

        boolean deleted = supplierDAO.deleteById(created.getId());

        assertTrue(deleted);
        assertTrue(supplierDAO.getById(created.getId()).isEmpty());
    }

    @Test
    void create_shouldPersistSupplierWithAddress() throws DatabaseException {

        Address address = new Address();
        address.setStreet("Street 1");
        address.setPostalCode("2800");
        address.setCity("Lyngby");
        address.setCountry("Denmark");
        address = addressDAO.create(address);

        Supplier supplier = new Supplier();
        supplier.setName("Supplier With Address");
        supplier.setAddress(address);

        Supplier created = supplierDAO.create(supplier);

        assertNotNull(created);
        assertTrue(created.getId() > 0);
        assertNotNull(created.getAddress());
        assertEquals(address.getId(), created.getAddress().getId());
    }
}
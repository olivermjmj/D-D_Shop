package app.service.impl;

import app.config.HibernateConfig;
import app.dao.AddressDAO;
import app.dao.SupplierDAO;
import app.dto.supplier.CreateSupplierDTO;
import app.dto.supplier.SupplierResponseDTO;
import app.dto.supplier.UpdateSupplierDTO;
import app.entities.Address;
import app.entities.Supplier;
import app.exceptions.ApiException;
import app.exceptions.DatabaseException;
import app.utils.EMF;
import org.junit.jupiter.api.*;

import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SupplierServiceImplTest {

    private SupplierDAO supplierDAO;
    private AddressDAO addressDAO;
    private SupplierServiceImpl supplierService;
    private ExecutorService executorService;

    @BeforeAll
    void setUpAll() {

        HibernateConfig.setTest(true);
        supplierDAO = new SupplierDAO();
        addressDAO = new AddressDAO();
        executorService = Executors.newSingleThreadExecutor();
        supplierService = new SupplierServiceImpl(supplierDAO, executorService);
    }

    @BeforeEach
    void setUp() throws DatabaseException {

        supplierDAO.deleteAll();
        addressDAO.deleteAll();
    }

    @AfterAll
    void tearDownAll() {

        executorService.shutdown();
        EMF.close();
    }

    @Test
    void create_shouldCreateSupplierWithoutAddress() {

        CreateSupplierDTO dto = new CreateSupplierDTO("Wizard Supplier", null);

        SupplierResponseDTO result = supplierService.create(dto).join();

        assertNotNull(result);
        assertEquals("Wizard Supplier", result.name());
        assertNull(result.addressId());
    }

    @Test
    void create_shouldThrow_whenAddressDoesNotExist() {

        CreateSupplierDTO dto = new CreateSupplierDTO("Wizard Supplier", 999999);

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> supplierService.create(dto).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Address not found", ex.getCause().getMessage());
    }

    @Test
    void update_shouldUpdateName() throws DatabaseException {

        Supplier supplier = new Supplier();
        supplier.setName("Old Supplier");
        supplier = supplierDAO.create(supplier);

        UpdateSupplierDTO dto = new UpdateSupplierDTO("New Supplier", null);

        SupplierResponseDTO result = supplierService.update(supplier.getId(), dto).join();

        assertEquals("New Supplier", result.name());
    }

    @Test
    void update_shouldSetAddress() throws DatabaseException {

        Supplier supplier = new Supplier();
        supplier.setName("Supplier");
        supplier = supplierDAO.create(supplier);

        Address address = createAddress();

        UpdateSupplierDTO dto = new UpdateSupplierDTO(null, address.getId());

        SupplierResponseDTO result = supplierService.update(supplier.getId(), dto).join();

        assertEquals(address.getId(), result.addressId());
    }

    private Address createAddress() throws DatabaseException {

        Address address = new Address();
        return addressDAO.create(address);
    }
}
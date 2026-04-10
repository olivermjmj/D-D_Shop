package app.service.impl;

import app.config.HibernateConfig;
import app.dao.AddressDAO;
import app.dto.address.AddressResponseDTO;
import app.dto.address.CreateAddressDTO;
import app.dto.address.UpdateAddressDTO;
import app.entities.Address;
import app.exceptions.ApiException;
import app.exceptions.DatabaseException;
import app.utils.EMF;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class AddressServiceImplTest {

    private static AddressServiceImpl addressService;
    private static ExecutorService executorService;

    private final AddressDAO addressDAO = new AddressDAO();

    @BeforeAll
    static void setUpAll() {

        HibernateConfig.setTest(true);
        executorService = Executors.newSingleThreadExecutor();
        addressService = new AddressServiceImpl(new AddressDAO(), executorService);
    }

    @BeforeEach
    void setUp() throws DatabaseException {
        addressDAO.deleteAll();
    }

    @AfterAll
    static void tearDownAll() {

        executorService.shutdown();
        EMF.close();
    }

    @Test
    void create_shouldCreateAddress() {

        CreateAddressDTO dto = new CreateAddressDTO(
                "Main Street 1",
                "2800",
                "Kongens Lyngby",
                "Denmark"
        );

        AddressResponseDTO result = addressService.create(dto).join();

        assertNotNull(result);
        assertEquals("Main Street 1", result.street());
        assertEquals("2800", result.postalCode());
        assertEquals("Kongens Lyngby", result.city());
        assertEquals("Denmark", result.country());
    }

    @Test
    void create_shouldThrow_whenStreetIsBlank() {

        CreateAddressDTO dto = new CreateAddressDTO(
                "   ",
                "2800",
                "Kongens Lyngby",
                "Denmark"
        );

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> addressService.create(dto).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Street is required", ex.getCause().getMessage());
    }

    @Test
    void create_shouldThrow_whenPostalCodeIsBlank() {

        CreateAddressDTO dto = new CreateAddressDTO(
                "Main Street 1",
                "   ",
                "Kongens Lyngby",
                "Denmark"
        );

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> addressService.create(dto).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Postal code is required", ex.getCause().getMessage());
    }

    @Test
    void update_shouldUpdateCity() throws DatabaseException {

        Address address = new Address();
        address.setStreet("Main Street 1");
        address.setPostalCode("2800");
        address.setCity("Lyngby");
        address.setCountry("Denmark");
        Address finalAddress = addressDAO.create(address);

        UpdateAddressDTO dto = new UpdateAddressDTO(
                null,
                null,
                "Copenhagen",
                null
        );

        AddressResponseDTO result = addressService.update(finalAddress.getId(), dto).join();

        assertEquals("Copenhagen", result.city());
    }

    @Test
    void update_shouldThrow_whenCountryIsBlank() throws DatabaseException {

        Address address = new Address();
        address.setStreet("Main Street 1");
        address.setPostalCode("2800");
        address.setCity("Lyngby");
        address.setCountry("Denmark");
        Address finalAddress = addressDAO.create(address);

        UpdateAddressDTO dto = new UpdateAddressDTO(
                null,
                null,
                null,
                "   "
        );

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> addressService.update(finalAddress.getId(), dto).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Country cannot be blank", ex.getCause().getMessage());
    }
}
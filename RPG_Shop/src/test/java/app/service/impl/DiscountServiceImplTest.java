package app.service.impl;

import app.config.HibernateConfig;
import app.dao.DiscountDAO;
import app.dto.discount.CreateDiscountDTO;
import app.dto.discount.DiscountResponseDTO;
import app.dto.discount.UpdateDiscountDTO;
import app.entities.Discount;
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

class DiscountServiceImplTest {

    private static DiscountServiceImpl discountService;
    private static ExecutorService executorService;

    private final DiscountDAO discountDAO = new DiscountDAO();

    @BeforeAll
    static void setUpAll() {

        HibernateConfig.setTest(true);
        executorService = Executors.newSingleThreadExecutor();
        discountService = new DiscountServiceImpl(new DiscountDAO(), executorService);
    }

    @BeforeEach
    void setUp() throws DatabaseException {
        discountDAO.deleteAll();
    }

    @AfterAll
    static void tearDownAll() {

        executorService.shutdown();
        EMF.close();
    }

    @Test
    void create_shouldCreateDiscount() {

        CreateDiscountDTO dto = new CreateDiscountDTO(25.0);

        DiscountResponseDTO result = discountService.create(dto).join();

        assertNotNull(result);
        assertEquals(25.0, result.discountPercentage());
    }

    @Test
    void create_shouldThrow_whenDiscountPercentageIsNegative() {

        CreateDiscountDTO dto = new CreateDiscountDTO(-1.0);

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> discountService.create(dto).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Discount percentage must be between 0 and 100", ex.getCause().getMessage());
    }

    @Test
    void create_shouldThrow_whenDiscountPercentageIsAbove100() {

        CreateDiscountDTO dto = new CreateDiscountDTO(101.0);

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> discountService.create(dto).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Discount percentage must be between 0 and 100", ex.getCause().getMessage());
    }

    @Test
    void update_shouldUpdateDiscountPercentage() throws DatabaseException {

        Discount discount = new Discount();
        discount.setDiscountPercentage(10.0);
        Discount finalDiscount = discountDAO.create(discount);

        UpdateDiscountDTO dto = new UpdateDiscountDTO(35.0);

        DiscountResponseDTO result = discountService.update(finalDiscount.getId(), dto).join();

        assertEquals(35.0, result.discountPercentage());
    }

    @Test
    void update_shouldThrow_whenDiscountPercentageIsAbove100() throws DatabaseException {

        Discount discount = new Discount();
        discount.setDiscountPercentage(10.0);
        Discount finalDiscount = discountDAO.create(discount);

        UpdateDiscountDTO dto = new UpdateDiscountDTO(150.0);

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> discountService.update(finalDiscount.getId(), dto).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Discount percentage must be between 0 and 100", ex.getCause().getMessage());
    }
}
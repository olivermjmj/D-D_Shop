package app.dao;

import app.config.HibernateConfig;
import app.entities.Discount;
import app.exceptions.DatabaseException;
import app.utils.EMF;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DiscountDAOTest {

    private DiscountDAO discountDAO;

    @BeforeAll
    void setUpAll() {

        HibernateConfig.setTest(true);
        discountDAO = new DiscountDAO();
    }

    @BeforeEach
    void setUp() throws DatabaseException {
        discountDAO.deleteAll();
    }

    @AfterAll
    void tearDownAll() {
        EMF.close();
    }

    @Test
    void create_shouldPersistDiscount() throws DatabaseException {

        Discount discount = new Discount();
        discount.setDiscountPercentage(15.0);

        Discount created = discountDAO.create(discount);

        assertNotNull(created);
        assertTrue(created.getId() > 0);
        assertEquals(15.0, created.getDiscountPercentage());
    }

    @Test
    void getById_shouldReturnDiscount_whenExists() throws DatabaseException {

        Discount discount = new Discount();
        discount.setDiscountPercentage(15.0);
        Discount created = discountDAO.create(discount);

        Optional<Discount> found = discountDAO.getById(created.getId());

        assertTrue(found.isPresent());
        assertEquals(created.getId(), found.get().getId());
    }

    @Test
    void getAll_shouldReturnAllDiscounts() throws DatabaseException {

        Discount d1 = new Discount();
        d1.setDiscountPercentage(10.0);

        Discount d2 = new Discount();
        d2.setDiscountPercentage(20.0);

        discountDAO.create(d1);
        discountDAO.create(d2);

        List<Discount> discounts = discountDAO.getAll();

        assertEquals(2, discounts.size());
    }

    @Test
    void update_shouldUpdateDiscount() throws DatabaseException {

        Discount discount = new Discount();
        discount.setDiscountPercentage(15.0);
        Discount created = discountDAO.create(discount);

        created.setDiscountPercentage(25.0);

        Discount updated = discountDAO.update(created);

        assertEquals(25.0, updated.getDiscountPercentage());
    }

    @Test
    void deleteById_shouldDeleteDiscount() throws DatabaseException {

        Discount discount = new Discount();
        discount.setDiscountPercentage(15.0);
        Discount created = discountDAO.create(discount);

        boolean deleted = discountDAO.deleteById(created.getId());

        assertTrue(deleted);
        assertTrue(discountDAO.getById(created.getId()).isEmpty());
    }
}
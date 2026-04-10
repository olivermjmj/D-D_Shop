package app.service.impl;

import app.config.HibernateConfig;
import app.dao.ItemCategoryDAO;
import app.dao.ItemDAO;
import app.dao.SupplierDAO;
import app.dto.item.CreateItemDTO;
import app.dto.item.ItemResponseDTO;
import app.entities.Item;
import app.entities.ItemCategory;
import app.entities.Supplier;
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

class ItemServiceImplTest {

    private static ItemServiceImpl itemService;
    private static ExecutorService executorService;

    private final ItemDAO itemDAO = new ItemDAO();
    private final ItemCategoryDAO itemCategoryDAO = new ItemCategoryDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();

    @BeforeAll
    static void setUpAll() {

        HibernateConfig.setTest(true);
        executorService = Executors.newSingleThreadExecutor();
        itemService = new ItemServiceImpl(new ItemDAO(), executorService);
    }

    @BeforeEach
    void setUp() throws DatabaseException {

        itemDAO.deleteAll();
        supplierDAO.deleteAll();
        itemCategoryDAO.deleteAll();
    }

    @AfterAll
    static void tearDownAll() {

        executorService.shutdown();
        EMF.close();
    }

    @Test
    void create_shouldCreateItem() throws DatabaseException {

        ItemCategory category = createCategory("Weapons");
        Supplier supplier = createSupplier("Sword Supplier");

        CreateItemDTO dto = new CreateItemDTO(
                category.getId(),
                supplier.getId(),
                new BigDecimal("100.00"),
                "ext-1",
                "dnd5e",
                "Sword",
                "A sharp sword"
        );

        ItemResponseDTO result = itemService.create(dto).join();

        assertNotNull(result);
        assertEquals(category.getId(), result.itemCategoryId());
        assertEquals(supplier.getId(), result.supplierId());
        assertEquals(new BigDecimal("100.00"), result.basePrice());
        assertEquals("ext-1", result.externalId());
        assertEquals("dnd5e", result.externalSource());
        assertEquals("Sword", result.name());
        assertEquals("A sharp sword", result.description());
    }

    @Test
    void create_shouldThrow_whenCategoryDoesNotExist() throws DatabaseException {

        Supplier supplier = createSupplier("Sword Supplier");

        CreateItemDTO dto = new CreateItemDTO(
                999999,
                supplier.getId(),
                new BigDecimal("100.00"),
                "ext-1",
                "dnd5e",
                "Sword",
                "A sharp sword"
        );

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> itemService.create(dto).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Item category not found", ex.getCause().getMessage());
    }

    @Test
    void create_shouldThrow_whenSupplierDoesNotExist() throws DatabaseException {

        ItemCategory category = createCategory("Weapons");

        CreateItemDTO dto = new CreateItemDTO(
                category.getId(),
                999999,
                new BigDecimal("100.00"),
                "ext-1",
                "dnd5e",
                "Sword",
                "A sharp sword"
        );

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> itemService.create(dto).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Supplier not found", ex.getCause().getMessage());
    }

    @Test
    void getAllByCategoryId_shouldReturnItemsForCategory() throws DatabaseException {

        ItemCategory category = createCategory("Weapons");
        Supplier supplier = createSupplier("Sword Supplier");

        createItem(category, supplier, "Sword", "ext-1", "dnd5e");
        createItem(category, supplier, "Axe", "ext-2", "dnd5e");

        List<ItemResponseDTO> result = itemService.getAllByCategoryId(category.getId()).join();

        assertEquals(2, result.size());
    }

    @Test
    void getAllBySupplierId_shouldReturnItemsForSupplier() throws DatabaseException {

        ItemCategory category = createCategory("Weapons");
        Supplier supplier = createSupplier("Sword Supplier");

        createItem(category, supplier, "Sword", "ext-1", "dnd5e");
        createItem(category, supplier, "Axe", "ext-2", "dnd5e");

        List<ItemResponseDTO> result = itemService.getAllBySupplierId(supplier.getId()).join();

        assertEquals(2, result.size());
    }

    @Test
    void getAllByExternalSource_shouldReturnItemsForSource() throws DatabaseException {

        ItemCategory category = createCategory("Weapons");
        Supplier supplier = createSupplier("Sword Supplier");

        createItem(category, supplier, "Sword", "ext-1", "dnd5e");
        createItem(category, supplier, "Axe", "ext-2", "dnd5e");
        createItem(category, supplier, "Hammer", "ext-3", "manual");

        List<ItemResponseDTO> result = itemService.getAllByExternalSource("dnd5e").join();

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(i -> "dnd5e".equals(i.externalSource())));
    }

    @Test
    void getByExternalId_shouldReturnItem() throws DatabaseException {

        ItemCategory category = createCategory("Weapons");
        Supplier supplier = createSupplier("Sword Supplier");

        Item item = createItem(category, supplier, "Sword", "ext-1", "dnd5e");

        ItemResponseDTO result = itemService.getByExternalId("ext-1").join();

        assertEquals(item.getId(), result.id());
        assertEquals("ext-1", result.externalId());
    }

    @Test
    void getByExternalId_shouldThrow_whenItemDoesNotExist() {

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> itemService.getByExternalId("missing").join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Item not found", ex.getCause().getMessage());
    }

    @Test
    void getByExternalIdAndSource_shouldReturnItem() throws DatabaseException {

        ItemCategory category = createCategory("Weapons");
        Supplier supplier = createSupplier("Sword Supplier");

        Item item = createItem(category, supplier, "Sword", "ext-1", "dnd5e");

        ItemResponseDTO result = itemService.getByExternalIdAndSource("ext-1", "dnd5e").join();

        assertEquals(item.getId(), result.id());
        assertEquals("ext-1", result.externalId());
        assertEquals("dnd5e", result.externalSource());
    }

    @Test
    void getByExternalIdAndSource_shouldThrow_whenItemDoesNotExist() {

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> itemService.getByExternalIdAndSource("missing", "dnd5e").join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Item not found", ex.getCause().getMessage());
    }

    private ItemCategory createCategory(String name) throws DatabaseException {

        ItemCategory category = new ItemCategory();
        category.setCategoryName(name);
        return itemCategoryDAO.create(category);
    }

    private Supplier createSupplier(String name) throws DatabaseException {

        Supplier supplier = new Supplier();
        supplier.setName(name);
        return supplierDAO.create(supplier);
    }

    private Item createItem(ItemCategory category, Supplier supplier, String name, String externalId, String externalSource) throws DatabaseException {

        Item item = new Item();
        item.setItemCategory(category);
        item.setSupplier(supplier);
        item.setBasePrice(new BigDecimal("100.00"));
        item.setExternalId(externalId);
        item.setExternalSource(externalSource);
        item.setName(name);
        item.setDescription("Test description");
        return itemDAO.create(item);
    }
}
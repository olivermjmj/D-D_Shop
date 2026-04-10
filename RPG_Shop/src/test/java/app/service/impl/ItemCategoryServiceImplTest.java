package app.service.impl;

import app.config.HibernateConfig;
import app.dao.ItemCategoryDAO;
import app.dto.itemCategory.CreateItemCategoryDTO;
import app.dto.itemCategory.ItemCategoryResponseDTO;
import app.dto.itemCategory.UpdateItemCategoryDTO;
import app.entities.ItemCategory;
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

class ItemCategoryServiceImplTest {

    private static ItemCategoryServiceImpl itemCategoryService;
    private static ExecutorService executorService;

    private final ItemCategoryDAO itemCategoryDAO = new ItemCategoryDAO();

    @BeforeAll
    static void setUpAll() {

        HibernateConfig.setTest(true);
        executorService = Executors.newSingleThreadExecutor();
        itemCategoryService = new ItemCategoryServiceImpl(new ItemCategoryDAO(), executorService);
    }

    @BeforeEach
    void setUp() throws DatabaseException {
        itemCategoryDAO.deleteAll();
    }

    @AfterAll
    static void tearDownAll() {

        executorService.shutdown();
        EMF.close();
    }

    @Test
    void create_shouldCreateItemCategory() {

        CreateItemCategoryDTO dto = new CreateItemCategoryDTO("Weapons");

        ItemCategoryResponseDTO result = itemCategoryService.create(dto).join();

        assertNotNull(result);
        assertEquals("Weapons", result.categoryName());
    }

    @Test
    void create_shouldThrow_whenCategoryNameIsNull() {

        CreateItemCategoryDTO dto = new CreateItemCategoryDTO(null);

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> itemCategoryService.create(dto).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Category name cannot be blank", ex.getCause().getMessage());
    }

    @Test
    void create_shouldThrow_whenCategoryNameIsBlank() {

        CreateItemCategoryDTO dto = new CreateItemCategoryDTO("   ");

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> itemCategoryService.create(dto).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Category name cannot be blank", ex.getCause().getMessage());
    }

    @Test
    void update_shouldUpdateCategoryName() throws DatabaseException {

        ItemCategory itemCategory = new ItemCategory();
        itemCategory.setCategoryName("Old");
        ItemCategory finalItemCategory = itemCategoryDAO.create(itemCategory);

        UpdateItemCategoryDTO dto = new UpdateItemCategoryDTO("New");

        ItemCategoryResponseDTO result = itemCategoryService.update(finalItemCategory.getId(), dto).join();

        assertEquals("New", result.categoryName());
    }

    @Test
    void update_shouldThrow_whenCategoryNameIsBlank() throws DatabaseException {

        ItemCategory itemCategory = new ItemCategory();
        itemCategory.setCategoryName("Old");
        ItemCategory finalItemCategory = itemCategoryDAO.create(itemCategory);

        UpdateItemCategoryDTO dto = new UpdateItemCategoryDTO("   ");

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> itemCategoryService.update(finalItemCategory.getId(), dto).join()
        );

        assertInstanceOf(ApiException.class, ex.getCause());
        assertEquals("Category name cannot be blank", ex.getCause().getMessage());
    }
}
package app.service.impl;

import app.config.ThreadPoolConfig;
import app.dao.ItemCategoryDAO;
import app.dto.itemCategory.CreateItemCategoryDTO;
import app.dto.itemCategory.ItemCategoryResponseDTO;
import app.dto.itemCategory.UpdateItemCategoryDTO;
import app.entities.ItemCategory;
import app.exceptions.ApiException;

import java.util.concurrent.ExecutorService;

public class ItemCategoryServiceImpl extends AbstractService<CreateItemCategoryDTO, UpdateItemCategoryDTO, ItemCategoryResponseDTO, ItemCategory, Integer> {

    private final ItemCategoryDAO itemCategoryDAO;

    public ItemCategoryServiceImpl() {
        this(new ItemCategoryDAO(), ThreadPoolConfig.getExecutor());
    }

    public ItemCategoryServiceImpl(ItemCategoryDAO itemCategoryDAO, ExecutorService executorService) {

        super(itemCategoryDAO, ItemCategoryResponseDTO::fromEntity, executorService);
        this.itemCategoryDAO = itemCategoryDAO;
    }

    @Override
    protected ItemCategory createDtoToEntity(CreateItemCategoryDTO dto) {

        validateCategoryName(dto.categoryName());

        ItemCategory itemCategory = new ItemCategory();
        itemCategory.setCategoryName(dto.categoryName());

        return itemCategory;
    }

    @Override
    protected ItemCategory updateDtoToEntity(ItemCategory itemCategory, UpdateItemCategoryDTO dto) {

        if (dto.categoryName() != null) {

            validateCategoryName(dto.categoryName());
            itemCategory.setCategoryName(dto.categoryName());
        }

        return itemCategory;
    }

    private void validateCategoryName(String categoryName) {

        if (categoryName == null || categoryName.isBlank()) {
            throw new ApiException(400, "Category name cannot be blank");
        }
    }
}
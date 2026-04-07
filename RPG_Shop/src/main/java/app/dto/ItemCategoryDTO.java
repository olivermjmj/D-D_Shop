package app.dto;

import app.entities.ItemCategory;

public record ItemCategoryDTO(

        int id,
        String categoryName
) {
    public static ItemCategoryDTO fromEntity(ItemCategory itemCategory) {

        return new ItemCategoryDTO(

                itemCategory.getId(),
                itemCategory.getCategoryName()
        );
    }
}
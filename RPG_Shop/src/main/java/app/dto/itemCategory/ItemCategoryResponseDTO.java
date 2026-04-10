package app.dto.itemCategory;

import app.entities.ItemCategory;

public record ItemCategoryResponseDTO(

        int id,
        String categoryName
) {
    public static ItemCategoryResponseDTO fromEntity(ItemCategory itemCategory) {

        return new ItemCategoryResponseDTO(

                itemCategory.getId(),
                itemCategory.getCategoryName()
        );
    }
}
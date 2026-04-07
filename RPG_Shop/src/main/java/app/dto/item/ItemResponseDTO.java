package app.dto.item;

import app.entities.Item;

import java.math.BigDecimal;

public record ItemResponseDTO(

        int id,
        int itemCategoryId,
        int supplierId,
        BigDecimal basePrice,
        String externalId,
        String externalSource,
        String name,
        String description
) {
    public static ItemResponseDTO fromEntity(Item item) {

        return new ItemResponseDTO(
                item.getId(),
                item.getItemCategory().getId(),
                item.getSupplier().getId(),
                item.getBasePrice(),
                item.getExternalId(),
                item.getExternalSource(),
                item.getName(),
                item.getDescription()
        );
    }
}
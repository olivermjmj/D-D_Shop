package app.dto.item;

import java.math.BigDecimal;

public record UpdateItemDTO(

        Integer itemCategoryId,
        Integer supplierId,
        BigDecimal basePrice,
        String externalId,
        String externalSource,
        String name,
        String description
) {
}
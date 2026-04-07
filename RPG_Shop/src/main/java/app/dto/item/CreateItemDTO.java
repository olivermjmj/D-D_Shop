package app.dto.item;

import java.math.BigDecimal;

public record CreateItemDTO(

        int itemCategoryId,
        int supplierId,
        BigDecimal basePrice,
        String externalId,
        String externalSource,
        String name,
        String description
) {
}
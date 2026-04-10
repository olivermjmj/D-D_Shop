package app.dto.discount;

import app.entities.Discount;

public record DiscountResponseDTO(

        int id,
        double discountPercentage
) {
    public static DiscountResponseDTO fromEntity(Discount discount) {

        return new DiscountResponseDTO(

                discount.getId(),
                discount.getDiscountPercentage()
        );
    }
}
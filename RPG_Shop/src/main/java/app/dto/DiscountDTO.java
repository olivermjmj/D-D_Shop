package app.dto;

import app.entities.Discount;

public record DiscountDTO(

        int id,
        double discountPercentage
) {
    public static DiscountDTO fromEntity(Discount discount) {

        return new DiscountDTO(

                discount.getId(),
                discount.getDiscountPercentage()
        );
    }
}
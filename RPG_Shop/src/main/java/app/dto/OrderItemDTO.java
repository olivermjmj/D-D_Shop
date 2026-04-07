package app.dto;

import app.entities.OrderItem;

import java.math.BigDecimal;

public record OrderItemDTO(

        int id,
        int orderId,
        int itemId,
        int quantity,
        BigDecimal priceAtPurchase
) {
    public static OrderItemDTO fromEntity(OrderItem orderItem) {

        return new OrderItemDTO(

                orderItem.getId(),
                orderItem.getOrder().getId(),
                orderItem.getItem().getId(),
                orderItem.getQuantity(),
                orderItem.getPriceAtPurchase()
        );
    }
}
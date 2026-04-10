package app.dto.orderItem;

import app.entities.OrderItem;

import java.math.BigDecimal;

public record OrderItemResponseDTO(

        int id,
        int orderId,
        int itemId,
        int quantity,
        BigDecimal priceAtPurchase
) {
    public static OrderItemResponseDTO fromEntity(OrderItem orderItem) {
        return new OrderItemResponseDTO(
                orderItem.getId(),
                orderItem.getOrder().getId(),
                orderItem.getItem().getId(),
                orderItem.getQuantity(),
                orderItem.getPriceAtPurchase()
        );
    }
}
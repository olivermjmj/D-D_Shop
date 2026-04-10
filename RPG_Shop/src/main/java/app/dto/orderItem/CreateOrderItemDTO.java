package app.dto.orderItem;

public record CreateOrderItemDTO(

        int orderId,
        int itemId,
        int quantity
) {
}
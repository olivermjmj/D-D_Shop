package app.dto.order;

import app.entities.Order;
import app.entities.enums.OrderStatus;

import java.time.Instant;

public record OrderResponseDTO(
        int id,
        int userId,
        OrderStatus orderStatus,
        Instant createdAt,
        int addressId
) {
    public static OrderResponseDTO fromEntity(Order order) {
        return new OrderResponseDTO(
                order.getId(),
                order.getUser().getId(),
                order.getOrderStatus(),
                order.getCreatedAt(),
                order.getAddress().getId()
        );
    }
}
package app.dto.order;

import app.entities.enums.OrderStatus;

public record UpdateOrderDTO(
        OrderStatus orderStatus,
        Integer addressId
) {
}
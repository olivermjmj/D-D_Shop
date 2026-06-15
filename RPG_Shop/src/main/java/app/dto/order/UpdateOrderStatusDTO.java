package app.dto.order;

import app.entities.enums.OrderStatus;

public record UpdateOrderStatusDTO(

        OrderStatus orderStatus
) {
}
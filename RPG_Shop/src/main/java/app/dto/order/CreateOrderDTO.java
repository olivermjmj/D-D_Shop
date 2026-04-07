package app.dto.order;

public record CreateOrderDTO(
        int userId,
        int addressId
) {
}
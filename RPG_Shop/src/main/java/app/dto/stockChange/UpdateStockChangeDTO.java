package app.dto.stockChange;

public record UpdateStockChangeDTO(

        Integer delta,
        String reason,
        Integer performedByUserId
) {
}
package app.dto.stockChange;

public record CreateStockChangeDTO(

        int itemId,
        int delta,
        String reason,
        Integer performedByUserId
) {
}
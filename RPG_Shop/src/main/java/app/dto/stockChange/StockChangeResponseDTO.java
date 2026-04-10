package app.dto.stockChange;

import app.entities.StockChange;

import java.time.Instant;

public record StockChangeResponseDTO(

        int id,
        int itemId,
        int delta,
        String reason,
        Instant createdAt,
        Integer performedByUserId
) {
    public static StockChangeResponseDTO fromEntity(StockChange stockChange) {

        return new StockChangeResponseDTO(
                stockChange.getId(),
                stockChange.getItem().getId(),
                stockChange.getDelta(),
                stockChange.getReason(),
                stockChange.getCreatedAt(),
                stockChange.getPerformedBy() != null ? stockChange.getPerformedBy().getId() : null
        );
    }
}
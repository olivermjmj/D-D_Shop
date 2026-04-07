package app.dto;

import app.entities.StockChange;

import java.time.Instant;

public record StockChangeDTO(

        int id,
        int itemId,
        int delta,
        String reason,
        Instant createdAt,
        Integer performedByUserId
) {
    public static StockChangeDTO fromEntity(StockChange stockChange) {

        return new StockChangeDTO(

                stockChange.getId(),
                stockChange.getItem().getId(),
                stockChange.getDelta(),
                stockChange.getReason(),
                stockChange.getCreatedAt(),
                stockChange.getPerformedBy() != null ? stockChange.getPerformedBy().getId() : null
        );
    }
}
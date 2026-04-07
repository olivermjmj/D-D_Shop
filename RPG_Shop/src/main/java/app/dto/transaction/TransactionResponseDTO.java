package app.dto.transaction;

import app.entities.Transaction;
import app.entities.enums.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionResponseDTO(

        int id,
        Integer orderId,
        int userId,
        BigDecimal amount,
        TransactionType type,
        Instant createdAt
) {
    public static TransactionResponseDTO fromEntity(Transaction transaction) {

        return new TransactionResponseDTO(
                transaction.getId(),
                transaction.getOrder() != null ? transaction.getOrder().getId() : null,
                transaction.getUser().getId(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getCreatedAt()
        );
    }
}
package app.dto.transaction;

import app.entities.enums.TransactionType;

import java.math.BigDecimal;

public record CreateTransactionDTO(

        Integer orderId,
        int userId,
        BigDecimal amount,
        TransactionType type
) {
}
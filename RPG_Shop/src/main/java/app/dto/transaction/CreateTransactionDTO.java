package app.dto.transaction;

import app.entities.enums.TransactionType;

import java.math.BigDecimal;

public record CreateTransactionDTO(

        int userId,
        BigDecimal amount,
        TransactionType type
) {
}
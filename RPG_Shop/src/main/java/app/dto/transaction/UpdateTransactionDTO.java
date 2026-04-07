package app.dto.transaction;

import app.entities.enums.TransactionType;

import java.math.BigDecimal;

public record UpdateTransactionDTO(

        BigDecimal amount,
        TransactionType type
) {
}
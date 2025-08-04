package com.rgs.wallet.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    private UUID id;
    private Wallet wallet;
    private BigDecimal amount;
    private TransactionType type;
    private Instant createdAt;
    private UUID relatedTransactionId;

    public void linkWithRelatedTransaction(UUID relatedId) {
        if (this.type != TransactionType.TRANSFER_IN && this.type != TransactionType.TRANSFER_OUT) {
            throw new IllegalStateException("Only transfer transactions can be linked");
        }
        this.relatedTransactionId = relatedId;
    }

    private BigDecimal validateAmount(BigDecimal amount, TransactionType type) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        return type.equals(TransactionType.DEPOSIT) ? amount.negate() : amount;
    }

    public BigDecimal getSignedAmount() {
        return this.amount;
    }
}

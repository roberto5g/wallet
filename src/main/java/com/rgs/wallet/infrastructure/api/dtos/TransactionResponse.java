package com.rgs.wallet.infrastructure.api.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rgs.wallet.domain.model.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        UUID walletId,
        BigDecimal amount,
        String type,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        Instant createdAt,
        UUID relatedTransactionId
) {
    public static TransactionResponse fromDomain(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getWallet().getId(),
                transaction.getSignedAmount().abs(),
                transaction.getType().name(),
                transaction.getCreatedAt(),
                transaction.getRelatedTransactionId()
        );
    }
}

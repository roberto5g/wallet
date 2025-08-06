package com.rgs.wallet.fixtures;

import com.rgs.wallet.domain.model.Transaction;
import com.rgs.wallet.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class TransactionFixture {

    public static Transaction defaultTransaction() {
        return depositTransaction();
    }

    public static Transaction depositTransaction() {
        return Transaction.builder()
                .id(UUID.randomUUID())
                .wallet(WalletFixture.defaultWallet())
                .amount(new BigDecimal("100.00"))
                .type(TransactionType.DEPOSIT)
                .createdAt(Instant.now())
                .build();
    }

    public static Transaction withdrawalTransaction() {
        return Transaction.builder()
                .id(UUID.randomUUID())
                .wallet(WalletFixture.defaultWallet())
                .amount(new BigDecimal("50.00"))
                .type(TransactionType.WITHDRAWAL)
                .createdAt(Instant.now())
                .build();
    }

    public static Transaction transferOutTransaction(UUID relatedId) {
        return Transaction.builder()
                .id(UUID.randomUUID())
                .wallet(WalletFixture.defaultWallet())
                .amount(new BigDecimal("75.00"))
                .type(TransactionType.TRANSFER_OUT)
                .createdAt(Instant.now())
                .relatedTransactionId(relatedId)
                .build();
    }

    public static Transaction transferInTransaction(UUID relatedId) {
        return Transaction.builder()
                .id(UUID.randomUUID())
                .wallet(WalletFixture.defaultWallet())
                .amount(new BigDecimal("75.00"))
                .type(TransactionType.TRANSFER_IN)
                .createdAt(Instant.now())
                .relatedTransactionId(relatedId)
                .build();
    }

    public static Transaction transferInUnlinked() {
        return Transaction.builder()
                .id(UUID.randomUUID())
                .wallet(WalletFixture.defaultWallet())
                .amount(new BigDecimal("75.00"))
                .type(TransactionType.TRANSFER_IN)
                .createdAt(Instant.now())
                .build();
    }

    public static Transaction withCustomAmount(BigDecimal amount, TransactionType type) {
        return Transaction.builder()
                .id(UUID.randomUUID())
                .wallet(WalletFixture.defaultWallet())
                .amount(amount)
                .type(type)
                .createdAt(Instant.now())
                .build();
    }
}

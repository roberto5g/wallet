package com.rgs.wallet.domain.model;

import com.rgs.wallet.domain.exceptions.InsufficientFundsException;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class Wallet {
    private UUID id;
    private User user;
    private BigDecimal balance;
    private WalletStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();

    public Transaction deposit(BigDecimal amount) {
        validateAmount(amount);
        this.balance = balance.add(amount);
        this.updatedAt = Instant.now();

        Transaction transaction = Transaction.builder()
                .wallet(this)
                .amount(amount)
                .type(TransactionType.DEPOSIT)
                .createdAt(Instant.now())
                .build();

        this.transactions.add(transaction);
        return transaction;
    }

    public Transaction withdraw(BigDecimal amount) throws InsufficientFundsException {
        validateAmount(amount);
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        }
        this.balance = balance.subtract(amount);
        this.updatedAt = Instant.now();

        Transaction transaction = Transaction.builder()
                .wallet(this)
                .amount(amount)
                .type(TransactionType.WITHDRAWAL)
                .createdAt(Instant.now())
                .build();

        this.transactions.add(transaction);
        return transaction;
    }

    public void activate() {
        this.status = WalletStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.status = WalletStatus.INACTIVE;
        this.updatedAt = Instant.now();
    }

    private void validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}

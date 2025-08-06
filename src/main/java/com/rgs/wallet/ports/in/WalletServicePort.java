package com.rgs.wallet.ports.in;

import com.rgs.wallet.domain.exceptions.InsufficientFundsException;
import com.rgs.wallet.domain.model.Transaction;
import com.rgs.wallet.domain.model.Wallet;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface WalletServicePort {
    Wallet createWallet(UUID userId);

    BigDecimal getBalance(UUID walletId);
    Wallet getWallet(UUID walletId);
    List<Transaction> getTransactions(UUID walletId, Instant startDate, Instant endDate);

    BigDecimal getHistoricalBalance(UUID walletId, Instant timestamp);

    void deposit(UUID walletId, BigDecimal amount, UUID requestId);

    void withdraw(UUID walletId, BigDecimal amount, UUID requestId);

    void transfer(UUID fromWalletId, UUID toWalletId, BigDecimal amount, UUID requestId) throws InsufficientFundsException;
}

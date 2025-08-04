package com.rgs.wallet.application.service;

import com.rgs.wallet.domain.model.Transaction;
import com.rgs.wallet.ports.out.TransactionPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HistoricalBalanceService {

    private final TransactionPersistencePort transactionPersistence;

    public BigDecimal calculateHistoricalBalance(UUID walletId, Instant timestamp) {
        return transactionPersistence.calculateBalanceUpTo(walletId, timestamp);
    }
}

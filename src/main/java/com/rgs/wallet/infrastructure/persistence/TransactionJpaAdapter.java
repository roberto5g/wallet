package com.rgs.wallet.infrastructure.persistence;

import com.rgs.wallet.domain.model.Transaction;
import com.rgs.wallet.infrastructure.persistence.entity.TransactionEntity;
import com.rgs.wallet.infrastructure.persistence.mapper.TransactionMapper;
import com.rgs.wallet.infrastructure.persistence.repository.TransactionJpaRepository;
import com.rgs.wallet.ports.out.TransactionPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TransactionJpaAdapter implements TransactionPersistencePort {

    private final TransactionJpaRepository transactionRepository;
    private final TransactionMapper mapper;

    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity entity = mapper.toEntity(transaction);
        TransactionEntity saved = transactionRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<Transaction> findByWalletIdAndPeriod(UUID walletId, Instant startDate, Instant endDate) {
        return transactionRepository
                .findByWalletIdAndCreatedAtBetween(walletId, startDate, endDate)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }


    @Override
    public BigDecimal calculateBalanceUpTo(UUID walletId, Instant timestamp) {
        return transactionRepository.calculateBalanceUpTo(walletId, timestamp);
    }

}

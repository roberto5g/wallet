package com.rgs.wallet.infrastructure.persistence.repository;

import com.rgs.wallet.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, UUID> {

    @Query("SELECT COALESCE(SUM(CASE WHEN t.type = 'DEPOSIT' OR t.type = 'TRANSFER_IN' THEN t.amount ELSE -t.amount END), 0) " +
            "FROM TransactionEntity t " +
            "WHERE t.wallet.id = :walletId AND t.createdAt <= :timestamp")
    BigDecimal calculateBalanceUpTo(@Param("walletId") UUID walletId,
                                    @Param("timestamp") Instant timestamp);

    List<TransactionEntity> findByWalletIdAndCreatedAtBetween(
            UUID walletId,
            Instant startDate,
            Instant endDate);
}
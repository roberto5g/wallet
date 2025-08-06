package com.rgs.wallet.infrastructure.persistence.mapper;

import com.rgs.wallet.domain.model.Transaction;
import com.rgs.wallet.domain.model.TransactionType;
import com.rgs.wallet.domain.model.Wallet;
import com.rgs.wallet.infrastructure.persistence.entity.TransactionEntity;
import com.rgs.wallet.infrastructure.persistence.entity.WalletEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class TransactionMapperTest {

    private WalletMapper walletMapper;
    private TransactionMapper transactionMapper;

    @BeforeEach
    void setup() {
        walletMapper = mock(WalletMapper.class);
        transactionMapper = new TransactionMapper(walletMapper);
    }

    @Test
    void testToDomain() {
        UUID transactionId = UUID.randomUUID();
        UUID walletId = UUID.randomUUID();
        Instant createdAt = Instant.now();
        UUID relatedId = UUID.randomUUID();

        WalletEntity walletEntity = WalletEntity.builder().id(walletId).build();
        TransactionEntity entity = TransactionEntity.builder()
                .id(transactionId)
                .wallet(walletEntity)
                .amount(BigDecimal.valueOf(100))
                .type(TransactionType.DEPOSIT)
                .createdAt(createdAt)
                .relatedTransactionId(relatedId)
                .build();

        Wallet walletDomain = Wallet.builder().id(walletId).build();
        when(walletMapper.toDomain(walletEntity)).thenReturn(walletDomain);

        Transaction transaction = transactionMapper.toDomain(entity);

        assertNotNull(transaction);
        assertEquals(transactionId, transaction.getId());
        assertEquals(walletDomain, transaction.getWallet());
        assertEquals(BigDecimal.valueOf(100), transaction.getAmount());
        assertEquals(TransactionType.DEPOSIT, transaction.getType());
        assertEquals(createdAt, transaction.getCreatedAt());
        assertEquals(relatedId, transaction.getRelatedTransactionId());

        verify(walletMapper).toDomain(walletEntity);
    }

    @Test
    void testToEntity() {
        UUID transactionId = UUID.randomUUID();
        UUID walletId = UUID.randomUUID();
        Instant createdAt = Instant.now();
        UUID relatedId = UUID.randomUUID();

        Wallet walletDomain = Wallet.builder().id(walletId).build();
        Transaction transaction = Transaction.builder()
                .id(transactionId)
                .wallet(walletDomain)
                .amount(BigDecimal.valueOf(100))
                .type(TransactionType.DEPOSIT)
                .createdAt(createdAt)
                .relatedTransactionId(relatedId)
                .build();

        WalletEntity walletEntity = WalletEntity.builder().id(walletId).build();
        when(walletMapper.toEntity(walletDomain)).thenReturn(walletEntity);

        TransactionEntity entity = transactionMapper.toEntity(transaction);

        assertNotNull(entity);
        assertEquals(transactionId, entity.getId());
        assertEquals(walletEntity, entity.getWallet());
        assertEquals(BigDecimal.valueOf(100), entity.getAmount());
        assertEquals(TransactionType.DEPOSIT, entity.getType());
        assertEquals(createdAt, entity.getCreatedAt());
        assertEquals(relatedId, entity.getRelatedTransactionId());

        verify(walletMapper).toEntity(walletDomain);
    }
}

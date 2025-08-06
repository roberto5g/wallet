package com.rgs.wallet.infrastructure.persistence;

import com.rgs.wallet.domain.model.Transaction;
import com.rgs.wallet.fixtures.TransactionFixture;
import com.rgs.wallet.infrastructure.persistence.entity.TransactionEntity;
import com.rgs.wallet.infrastructure.persistence.mapper.TransactionMapper;
import com.rgs.wallet.infrastructure.persistence.repository.TransactionJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TransactionJpaAdapterTest {

    @Mock
    private TransactionJpaRepository transactionRepository;

    @Mock
    private TransactionMapper mapper;

    @InjectMocks
    private TransactionJpaAdapter adapter;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldSaveTransactionSuccessfully() {
        // Arrange
        Transaction transaction = TransactionFixture.defaultTransaction();
        TransactionEntity entity = new TransactionEntity();
        TransactionEntity savedEntity = new TransactionEntity();

        when(mapper.toEntity(transaction)).thenReturn(entity);
        when(transactionRepository.save(entity)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(transaction);

        // Act
        Transaction result = adapter.save(transaction);

        // Assert
        assertThat(result).isEqualTo(transaction);
        verify(mapper).toEntity(transaction);
        verify(transactionRepository).save(entity);
        verify(mapper).toDomain(savedEntity);
    }

    @Test
    void shouldFindTransactionsByWalletIdAndPeriod() {
        UUID walletId = UUID.randomUUID();
        Instant start = Instant.parse("2024-01-01T00:00:00Z");
        Instant end = Instant.parse("2025-01-01T00:00:00Z");

        TransactionEntity entity = new TransactionEntity();
        Transaction transaction = TransactionFixture.defaultTransaction();

        when(transactionRepository.findByWalletIdAndCreatedAtBetween(walletId, start, end))
                .thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(transaction);

        // Act
        List<Transaction> results = adapter.findByWalletIdAndPeriod(walletId, start, end);

        // Assert
        assertThat(results).hasSize(1).contains(transaction);
        verify(transactionRepository).findByWalletIdAndCreatedAtBetween(walletId, start, end);
        verify(mapper).toDomain(entity);
    }

    @Test
    void shouldCalculateBalanceUpToGivenTimestamp() {
        UUID walletId = UUID.randomUUID();
        Instant timestamp = Instant.now();
        BigDecimal expected = new BigDecimal("123.45");

        when(transactionRepository.calculateBalanceUpTo(walletId, timestamp)).thenReturn(expected);

        BigDecimal result = adapter.calculateBalanceUpTo(walletId, timestamp);

        assertThat(result).isEqualTo(expected);
        verify(transactionRepository).calculateBalanceUpTo(walletId, timestamp);
    }
}

package com.rgs.wallet.application.service;

import com.rgs.wallet.domain.exceptions.WalletNotFoundException;
import com.rgs.wallet.domain.model.Transaction;
import com.rgs.wallet.domain.model.User;
import com.rgs.wallet.domain.model.Wallet;
import com.rgs.wallet.domain.model.WalletStatus;
import com.rgs.wallet.ports.out.TransactionPersistencePort;
import com.rgs.wallet.ports.out.UserPersistencePort;
import com.rgs.wallet.ports.out.WalletPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletPersistencePort walletPersistence;

    @Mock
    private UserPersistencePort userPersistence;

    @Mock
    private TransactionPersistencePort transactionPersistence;

    @InjectMocks
    private WalletService walletService;

    private UUID walletId1;
    private UUID walletId2;
    private Wallet walletFrom;
    private Wallet walletTo;

    @BeforeEach
    void setUp() {
        walletId1 = UUID.randomUUID();
        walletId2 = UUID.randomUUID();

        User user1 = User.builder()
                .id(UUID.randomUUID())
                .name("user-1")
                .taxId("1234567890")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        User user2 = User.builder()
                .id(UUID.randomUUID())
                .name("user-2")
                .taxId("1234567890")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        walletFrom = Wallet.builder()
                .id(walletId1)
                .user(user1)
                .balance(BigDecimal.valueOf(100))
                .status(WalletStatus.ACTIVE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        walletTo = Wallet.builder()
                .id(walletId2)
                .user(user2)
                .balance(BigDecimal.valueOf(50))
                .status(WalletStatus.ACTIVE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    void testTransfer_successful() {
        BigDecimal amount = BigDecimal.valueOf(30);
        var requestId = UUID.randomUUID();
        when(walletPersistence.findById(walletId1)).thenReturn(Optional.of(walletFrom));
        when(walletPersistence.findById(walletId2)).thenReturn(Optional.of(walletTo));
        when(transactionPersistence.save(any(Transaction.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());

        walletService.transfer(walletId1, walletId2, amount, requestId);

        assertEquals(BigDecimal.valueOf(70), walletFrom.getBalance());
        assertEquals(BigDecimal.valueOf(80), walletTo.getBalance());

        verify(walletPersistence, times(1)).save(walletFrom);
        verify(walletPersistence, times(1)).save(walletTo);
        verify(transactionPersistence, times(2)).save(any(Transaction.class));
    }

    @Test
    void testTransfer_walletNotFound() {
        when(walletPersistence.findById(walletId1)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () ->
                walletService.transfer(walletId1, walletId2, BigDecimal.TEN, UUID.randomUUID()));
    }

    @Test
    void testTransfer_sameWallet_shouldThrow() {
        assertThrows(IllegalArgumentException.class, () ->
                walletService.transfer(walletId1, walletId1, BigDecimal.TEN, UUID.randomUUID()));
    }
}
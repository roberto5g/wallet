
package com.rgs.wallet.application.service;

import com.rgs.wallet.domain.enums.ErrorCodeEnum;
import com.rgs.wallet.domain.exceptions.BusinessException;
import com.rgs.wallet.domain.exceptions.InsufficientFundsException;
import com.rgs.wallet.domain.exceptions.WalletNotFoundException;
import com.rgs.wallet.domain.model.*;
import com.rgs.wallet.infrastructure.idempotency.CacheService;
import com.rgs.wallet.infrastructure.idempotency.IdempotencyService;
import com.rgs.wallet.ports.out.TransactionPersistencePort;
import com.rgs.wallet.ports.out.UserPersistencePort;
import com.rgs.wallet.ports.out.WalletPersistencePort;
import com.rgs.wallet.application.service.fallback.WalletFallbackHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class WalletServiceTest {

    @InjectMocks
    private WalletService walletService;

    @Mock
    private WalletPersistencePort walletPersistence;

    @Mock
    private UserPersistencePort userPersistence;

    @Mock
    private TransactionPersistencePort transactionPersistence;

    @Mock
    private IdempotencyService idempotencyService;

    @Mock
    private CacheService cacheService;

    @Mock
    private WalletFallbackHandler fallbackHandler;

    private UUID walletId;
    private UUID requestId;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        walletId = UUID.randomUUID();
        requestId = UUID.randomUUID();
        wallet = Wallet.builder()
                .id(walletId)
                .balance(BigDecimal.valueOf(100))
                .user(User.builder().id(UUID.randomUUID()).build())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .status(WalletStatus.ACTIVE)
                .build();
    }

    @Test
    void shouldDepositSuccessfully() {
        BigDecimal amount = BigDecimal.valueOf(50);

        when(walletPersistence.findById(walletId)).thenReturn(Optional.of(wallet));
        doAnswer(invocation -> {
            Runnable op = invocation.getArgument(1);
            op.run();
            return null;
        }).when(idempotencyService).processWithIdempotency(eq(requestId), any(Runnable.class));

        walletService.deposit(walletId, amount, requestId);

        verify(walletPersistence).findById(walletId);
        verify(walletPersistence).save(wallet);
        verify(transactionPersistence).save(any(Transaction.class));
        verify(cacheService).clearCache(walletId);
        verifyNoInteractions(fallbackHandler);
    }

    @Test
    void shouldInvokeFallbackOnWalletNotFound() {
        BigDecimal amount = BigDecimal.valueOf(50);

        when(walletPersistence.findById(walletId)).thenReturn(Optional.empty());
        doAnswer(invocation -> {
            Runnable op = invocation.getArgument(1);
            op.run();
            return null;
        }).when(idempotencyService).processWithIdempotency(eq(requestId), any(Runnable.class));

        walletService.deposit(walletId, amount, requestId);

        verify(fallbackHandler).handleDepositFallback(eq(walletId), eq(amount), eq(requestId), any(WalletNotFoundException.class));
    }

    @Test
    void shouldInvokeFallbackOnUnexpectedException() {
        BigDecimal amount = BigDecimal.valueOf(50);

        doThrow(new RuntimeException("Unexpected")).when(idempotencyService).processWithIdempotency(eq(requestId), any(Runnable.class));

        walletService.deposit(walletId, amount, requestId);

        verify(fallbackHandler).handleDepositFallback(eq(walletId), eq(amount), eq(requestId), any(RuntimeException.class));
    }

    @Test
    void shouldWithdrawSuccessfully() {
        UUID walletId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("50.00");
        Wallet wallet = mock(Wallet.class);
        Transaction transaction = mock(Transaction.class);

        when(walletPersistence.findById(walletId)).thenReturn(Optional.of(wallet));
        when(wallet.withdraw(amount)).thenReturn(transaction);
        when(walletPersistence.save(wallet)).thenReturn(wallet);
        when(transactionPersistence.save(transaction)).thenReturn(transaction);

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(1);
            runnable.run();
            return null;
        }).when(idempotencyService).processWithIdempotency(eq(requestId), any(Runnable.class));

        walletService.withdraw(walletId, amount, requestId);

        verify(walletPersistence).save(wallet);
        verify(transactionPersistence).save(transaction);
        verify(cacheService).clearCache(walletId);
    }


    @Test
    void shouldInvokeFallbackOnInsufficientFunds() {
        UUID walletId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.00");
        Wallet wallet = mock(Wallet.class);

        when(walletPersistence.findById(walletId)).thenReturn(Optional.of(wallet));

        when(wallet.withdraw(amount)).thenThrow(new InsufficientFundsException());

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(1);
            runnable.run();
            return null;
        }).when(idempotencyService).processWithIdempotency(eq(requestId), any(Runnable.class));

        doThrow(new BusinessException(HttpStatus.SERVICE_UNAVAILABLE, ErrorCodeEnum.WS500001))
                .when(fallbackHandler).handleWithdrawFallback(eq(walletId), eq(amount), eq(requestId), any(Throwable.class));

        assertThrows(BusinessException.class, () -> walletService.withdraw(walletId, amount, requestId));

        verify(fallbackHandler).handleWithdrawFallback(eq(walletId), eq(amount), eq(requestId), any(Throwable.class));
    }


    @Test
    void shouldInvokeFallbackOnUnexpectedError() {
        UUID walletId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.00");

        doThrow(new NullPointerException("Unexpected error"))
                .when(idempotencyService).processWithIdempotency(eq(requestId), any(Runnable.class));

        doThrow(new BusinessException(HttpStatus.SERVICE_UNAVAILABLE, ErrorCodeEnum.WS500001))
                .when(fallbackHandler).handleWithdrawFallback(eq(walletId), eq(amount), eq(requestId), any(Throwable.class));

        assertThrows(BusinessException.class, () -> walletService.withdraw(walletId, amount, requestId));

        verify(fallbackHandler).handleWithdrawFallback(eq(walletId), eq(amount), eq(requestId), any(Throwable.class));
    }


    @Test
    void shouldTransferSuccessfully() {
        UUID fromWalletId = UUID.randomUUID();
        UUID toWalletId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("30.00");

        Wallet fromWallet = mock(Wallet.class);
        Wallet toWallet = mock(Wallet.class);
        Transaction outTransaction = mock(Transaction.class);
        Transaction inTransaction = mock(Transaction.class);

        when(fromWallet.getBalance()).thenReturn(BigDecimal.valueOf(100));
        when(toWallet.getBalance()).thenReturn(BigDecimal.valueOf(50));

        doNothing().when(fromWallet).setBalance(any());
        doNothing().when(toWallet).setBalance(any());

        when(walletPersistence.findById(fromWalletId)).thenReturn(Optional.of(fromWallet));
        when(walletPersistence.findById(toWalletId)).thenReturn(Optional.of(toWallet));
        when(transactionPersistence.save(any()))
                .thenReturn(outTransaction)
                .thenReturn(inTransaction);

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(1);
            runnable.run();
            return null;
        }).when(idempotencyService).processWithIdempotency(eq(requestId), any(Runnable.class));

        walletService.transfer(fromWalletId, toWalletId, amount, requestId);

        verify(walletPersistence).save(fromWallet);
        verify(walletPersistence).save(toWallet);
        verify(cacheService).clearCache(fromWalletId);
        verify(cacheService).clearCache(toWalletId);
    }

    @Test
    void shouldThrowWhenUserAlreadyHasWallet() {
        UUID userId = UUID.randomUUID();
        User existingUser = User.builder().id(userId).build();

        when(userPersistence.findById(userId)).thenReturn(Optional.of(existingUser));
        when(walletPersistence.existsByUser(existingUser)).thenReturn(true);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> walletService.createWallet(userId)
        );

        assertEquals("User already has a wallet", exception.getMessage());
        verify(userPersistence).findById(userId);
        verify(walletPersistence).existsByUser(existingUser);
        verifyNoMoreInteractions(walletPersistence, transactionPersistence);
    }

    @Test
    void shouldInvokeFallbackWhenTransferToSameWallet() {
        UUID walletId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.TEN;
        UUID requestId = UUID.randomUUID();
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(1);
            runnable.run();
            return null;
        }).when(idempotencyService).processWithIdempotency(eq(requestId), any(Runnable.class));

        doThrow(new BusinessException(HttpStatus.SERVICE_UNAVAILABLE, ErrorCodeEnum.WS500001))
                .when(fallbackHandler)
                .handleTransferFallback(eq(walletId), eq(walletId), eq(amount), eq(requestId), any(IllegalArgumentException.class));

        assertThrows(BusinessException.class, () -> walletService.transfer(walletId, walletId, amount, requestId));

        verify(fallbackHandler).handleTransferFallback(
                eq(walletId), eq(walletId), eq(amount), eq(requestId), any(IllegalArgumentException.class)
        );
    }


    @Test
    void shouldInvokeFallbackOnUnexpectedErrorInTransfer() {
        UUID fromWalletId = UUID.randomUUID();
        UUID toWalletId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.00");

        RuntimeException error = new RuntimeException("Unexpected failure");

        doThrow(error)
                .when(idempotencyService)
                .processWithIdempotency(eq(requestId), any(Runnable.class));

        doThrow(new BusinessException(HttpStatus.SERVICE_UNAVAILABLE, ErrorCodeEnum.WS500001))
                .when(fallbackHandler)
                .handleTransferFallback(eq(fromWalletId), eq(toWalletId), eq(amount), eq(requestId), eq(error));

        assertThrows(BusinessException.class, () ->
                walletService.transfer(fromWalletId, toWalletId, amount, requestId));

        verify(fallbackHandler).handleTransferFallback(
                eq(fromWalletId),
                eq(toWalletId),
                eq(amount),
                eq(requestId),
                eq(error)
        );
    }


    @Test
    void shouldCreateWalletSuccessfully() {
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).build();

        when(userPersistence.findById(userId)).thenReturn(Optional.of(user));
        when(walletPersistence.existsByUser(user)).thenReturn(false);
        when(walletPersistence.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Wallet created = walletService.createWallet(userId);

        assert created.getUser().equals(user);
        verify(walletPersistence).save(any(Wallet.class));
    }

    @Test
    void shouldGetBalanceFromCache() {
        when(cacheService.getCachedBalance(walletId)).thenReturn(BigDecimal.TEN);

        BigDecimal result = walletService.getBalance(walletId);

        assert result.equals(BigDecimal.TEN);
        verifyNoInteractions(walletPersistence);
    }

    @Test
    void shouldGetBalanceFromPersistenceWhenNotInCache() {
        when(cacheService.getCachedBalance(walletId)).thenReturn(null);
        when(walletPersistence.findById(walletId)).thenReturn(Optional.of(wallet));

        BigDecimal result = walletService.getBalance(walletId);

        assert result.equals(wallet.getBalance());
        verify(cacheService).cacheBalance(walletId, wallet.getBalance());
    }

    @Test
    void shouldGetWalletFromCache() {
        when(cacheService.getCachedWallet(walletId)).thenReturn(wallet);

        Wallet result = walletService.getWallet(walletId);

        assert result.equals(wallet);
        verifyNoInteractions(walletPersistence);
    }

    @Test
    void shouldGetWalletFromPersistenceWhenNotInCache() {
        when(cacheService.getCachedWallet(walletId)).thenReturn(null);
        when(walletPersistence.findById(walletId)).thenReturn(Optional.of(wallet));

        Wallet result = walletService.getWallet(walletId);

        assert result.equals(wallet);
        verify(cacheService).cacheWallet(walletId, wallet);
    }

    @Test
    void shouldGetHistoricalBalanceFromCache() {
        Instant timestamp = Instant.now();
        when(cacheService.getCachedHistoricalBalance(walletId, timestamp)).thenReturn(BigDecimal.ONE);

        BigDecimal result = walletService.getHistoricalBalance(walletId, timestamp);

        assert result.equals(BigDecimal.ONE);
        verifyNoInteractions(transactionPersistence);
    }

    @Test
    void shouldGetHistoricalBalanceFromPersistenceWhenNotInCache() {
        Instant timestamp = Instant.now();
        when(cacheService.getCachedHistoricalBalance(walletId, timestamp)).thenReturn(null);
        when(walletPersistence.findById(walletId)).thenReturn(Optional.of(wallet));
        when(transactionPersistence.calculateBalanceUpTo(walletId, timestamp)).thenReturn(BigDecimal.TEN);

        BigDecimal result = walletService.getHistoricalBalance(walletId, timestamp);

        assert result.equals(BigDecimal.TEN);
        verify(cacheService).cacheHistoricalBalance(walletId, timestamp, BigDecimal.TEN);
    }

    @Test
    void shouldGetTransactionsWithValidPeriod() {
        Instant start = Instant.parse("2023-01-01T00:00:00Z");
        Instant end = Instant.parse("2023-12-31T23:59:59Z");

        when(walletPersistence.findById(walletId)).thenReturn(Optional.of(wallet));
        when(transactionPersistence.findByWalletIdAndPeriod(walletId, start, end)).thenReturn(List.of());

        List<Transaction> result = walletService.getTransactions(walletId, start, end);

        assert result.isEmpty();
    }

    @Test
    void shouldThrowWhenStartDateAfterEndDate() {
        Instant start = Instant.now();
        Instant end = start.minusSeconds(10);

        when(walletPersistence.findById(walletId)).thenReturn(Optional.of(wallet));

        assertThrows(IllegalArgumentException.class, () -> walletService.getTransactions(walletId, start, end));
    }




}
package com.rgs.wallet.application.service;

import com.rgs.wallet.application.service.fallback.WalletFallbackHandler;
import com.rgs.wallet.domain.exceptions.*;
import com.rgs.wallet.domain.model.*;
import com.rgs.wallet.infrastructure.idempotency.CacheService;
import com.rgs.wallet.infrastructure.idempotency.IdempotencyService;
import com.rgs.wallet.ports.in.WalletServicePort;
import com.rgs.wallet.ports.out.TransactionPersistencePort;
import com.rgs.wallet.ports.out.UserPersistencePort;
import com.rgs.wallet.ports.out.WalletPersistencePort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletService implements WalletServicePort {

    private final WalletPersistencePort walletPersistence;
    private final UserPersistencePort userPersistence;
    private final TransactionPersistencePort transactionPersistence;
    private final IdempotencyService idempotencyService;
    private final CacheService cacheService;
    private final WalletFallbackHandler fallbackHandler;

    @Override
    @Transactional
    public Wallet createWallet(UUID userId) {
        User existingUser = userPersistence.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (walletPersistence.existsByUser(existingUser)) {
            log.warn("User {} already has a wallet", userId);
            throw new UserAlreadyHasWalletException();
        }

        Wallet newWallet = Wallet.builder()
                .user(existingUser)
                .balance(BigDecimal.ZERO)
                .status(WalletStatus.ACTIVE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        return walletPersistence.save(newWallet);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getBalance(UUID walletId) {
        BigDecimal cached = cacheService.getCachedBalance(walletId);
        if (cached != null) return cached;

        Wallet wallet = findWallet(walletId);

        BigDecimal balance = wallet.getBalance();
        cacheService.cacheBalance(walletId, balance);
        return balance;
    }


    @Override
    @Transactional(readOnly = true)
    public Wallet getWallet(UUID walletId) {
        Wallet cached = cacheService.getCachedWallet(walletId);
        if (cached != null) return cached;

        Wallet wallet = findWallet(walletId);

        cacheService.cacheWallet(walletId, wallet);
        return wallet;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getHistoricalBalance(UUID walletId, Instant timestamp) {
        var cached = cacheService.getCachedHistoricalBalance(walletId, timestamp);
        if (cached != null) return cached;
        findWallet(walletId);
        BigDecimal balance = transactionPersistence.calculateBalanceUpTo(walletId, timestamp);
        cacheService.cacheHistoricalBalance(walletId, timestamp, balance);
        return balance;
    }

    @Override
    @Transactional
    @Retry(name = "walletServiceRetry")
    @CircuitBreaker(name = "walletServiceCB")
    public void deposit(UUID walletId, BigDecimal amount, UUID requestId) {
        try {
            idempotencyService.processWithIdempotency(requestId, () -> {
                Wallet wallet = findWallet(walletId);
                Transaction transaction = wallet.deposit(amount);
                walletPersistence.save(wallet);
                transactionPersistence.save(transaction);
                cacheService.clearCache(walletId);
            });
        } catch (BusinessException e) {
            throw e;
        } catch (Throwable t) {
            fallbackHandler.handleDepositFallback(walletId, amount, requestId, t);
        }
    }


    @Override
    @Transactional
    @Retry(name = "walletServiceRetry")
    @CircuitBreaker(name = "walletServiceCB")
    public void withdraw(UUID walletId, BigDecimal amount, UUID requestId) {
        try {
            idempotencyService.processWithIdempotency(requestId, () -> {
                Wallet wallet = findWallet(walletId);

                Transaction withdrawalTransaction = wallet.withdraw(amount);

                walletPersistence.save(wallet);
                transactionPersistence.save(withdrawalTransaction);
                cacheService.clearCache(walletId);
            });
        } catch (BusinessException e) {
            throw e;
        } catch (Throwable t) {
            fallbackHandler.handleWithdrawFallback(walletId, amount, requestId, t);
        }
    }

    @Override
    @Transactional
    @Retry(name = "walletServiceRetry")
    @CircuitBreaker(name = "walletServiceCB")
    public void transfer(UUID fromWalletId, UUID toWalletId, BigDecimal amount, UUID requestId) {
        try {
            idempotencyService.processWithIdempotency(requestId, () -> {
                if (fromWalletId.equals(toWalletId)) {
                    throw new SameWalletTransferException();
                }

                Wallet source = findWallet(fromWalletId);
                if (source.getBalance().compareTo(amount) < 0) {
                    throw new InsufficientFundsException();
                }
                Wallet target = findWallet(toWalletId);

                Transaction transferOut = Transaction.builder()
                        .wallet(source)
                        .amount(amount)
                        .type(TransactionType.TRANSFER_OUT)
                        .createdAt(Instant.now())
                        .build();

                Transaction transferIn = Transaction.builder()
                        .wallet(target)
                        .amount(amount)
                        .type(TransactionType.TRANSFER_IN)
                        .createdAt(Instant.now())
                        .build();

                transferOut = transactionPersistence.save(transferOut);
                transferIn = transactionPersistence.save(transferIn);

                transferOut.linkWithRelatedTransaction(transferIn.getId());
                transferIn.linkWithRelatedTransaction(transferOut.getId());

                source.setBalance(source.getBalance().subtract(amount));
                target.setBalance(target.getBalance().add(amount));

                transactionPersistence.save(transferOut);
                transactionPersistence.save(transferIn);

                walletPersistence.save(source);
                walletPersistence.save(target);
                cacheService.clearCache(fromWalletId);
                cacheService.clearCache(toWalletId);
            });
        } catch (BusinessException e){
            throw e;
        } catch (Throwable t) {
            fallbackHandler.handleTransferFallback(fromWalletId, toWalletId, amount, requestId, t);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactions(UUID walletId, Instant startDate, Instant endDate) {
        findWallet(walletId);
        Instant effectiveStartDate = (startDate != null) ? startDate : Instant.EPOCH;
        Instant effectiveEndDate = (endDate != null) ? endDate : Instant.now();

        if (effectiveStartDate.isAfter(effectiveEndDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        return transactionPersistence.findByWalletIdAndPeriod(walletId, effectiveStartDate, effectiveEndDate);
    }

    private Wallet findWallet(UUID walletId) {
        return walletPersistence.findById(walletId)
                .orElseThrow(WalletNotFoundException::new);
    }

}

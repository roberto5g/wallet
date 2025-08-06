package com.rgs.wallet.fixtures;

import com.rgs.wallet.domain.model.Wallet;
import com.rgs.wallet.domain.model.WalletStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class WalletFixture {

    public static Wallet defaultWallet() {
        return Wallet.builder()
                .id(UUID.randomUUID())
                .user(UserFixture.createUser(UUID.randomUUID()))
                .balance(BigDecimal.valueOf(100.00))
                .status(WalletStatus.ACTIVE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    public static Wallet withParameters(UUID walletId, UUID userId) {
        return Wallet.builder()
                .id(walletId)
                .user(UserFixture.createUser(userId))
                .balance(BigDecimal.valueOf(100.00))
                .status(WalletStatus.ACTIVE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    public static Wallet walletWithBalance(BigDecimal balance) {
        return Wallet.builder()
                .id(UUID.randomUUID())
                .user(UserFixture.createUser(UUID.randomUUID()))
                .balance(balance)
                .status(WalletStatus.ACTIVE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    public static Wallet inactiveWallet() {
        return Wallet.builder()
                .id(UUID.randomUUID())
                .user(UserFixture.createUser(UUID.randomUUID()))
                .balance(BigDecimal.valueOf(50.00))
                .status(WalletStatus.INACTIVE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
}

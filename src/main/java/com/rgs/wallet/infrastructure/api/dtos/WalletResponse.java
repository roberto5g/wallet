package com.rgs.wallet.infrastructure.api.dtos;

import com.rgs.wallet.domain.model.User;
import com.rgs.wallet.domain.model.Wallet;
import com.rgs.wallet.domain.model.WalletStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record WalletResponse(
        UUID id,
        User userId,
        BigDecimal balance,
        WalletStatus status,
        Instant createdAt,
        Instant updatedAt
) {
    public static WalletResponse fromDomain(Wallet wallet) {
        return new WalletResponse(
                wallet.getId(),
                wallet.getUser(),
                wallet.getBalance(),
                wallet.getStatus(),
                wallet.getCreatedAt(),
                wallet.getUpdatedAt()
        );
    }
}

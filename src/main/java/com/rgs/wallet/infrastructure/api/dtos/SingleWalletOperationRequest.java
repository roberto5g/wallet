package com.rgs.wallet.infrastructure.api.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record SingleWalletOperationRequest(
        @NotNull UUID walletId,
        @NotNull @Positive BigDecimal amount
) {}

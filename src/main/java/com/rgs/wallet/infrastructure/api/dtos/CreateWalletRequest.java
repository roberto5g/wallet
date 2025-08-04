package com.rgs.wallet.infrastructure.api.dtos;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateWalletRequest(@NotNull UUID userId) {}

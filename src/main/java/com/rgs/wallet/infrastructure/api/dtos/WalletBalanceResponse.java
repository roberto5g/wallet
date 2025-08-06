package com.rgs.wallet.infrastructure.api.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletBalanceResponse(
        BigDecimal balance,
        UUID walletId
) {
}

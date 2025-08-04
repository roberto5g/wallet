package com.rgs.wallet.infrastructure.api.dtos;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record WalletHistoricalBalanceResponse(
        BigDecimal balance,
        Instant queryTimestamp,
        UUID walletId
) {
}

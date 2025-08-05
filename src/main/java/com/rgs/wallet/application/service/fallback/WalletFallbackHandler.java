package com.rgs.wallet.application.service.fallback;

import com.rgs.wallet.domain.enums.ErrorCodeEnum;
import com.rgs.wallet.domain.exceptions.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
public class WalletFallbackHandler {

    public void handleDepositFallback(UUID walletId, BigDecimal amount, UUID requestId, Throwable t) {
        log.error("Fallback triggered for deposit. Wallet: {}, Reason: {}", walletId, t.getMessage());
        throw new BusinessException(HttpStatus.SERVICE_UNAVAILABLE, ErrorCodeEnum.WS500001);
    }

    public void handleWithdrawFallback(UUID walletId, BigDecimal amount, UUID requestId, Throwable t) {
        log.error("Fallback triggered for withdraw. Wallet: {}, Reason: {}", walletId, t.getMessage());
        throw new BusinessException(HttpStatus.SERVICE_UNAVAILABLE, ErrorCodeEnum.WS500001);
    }

    public void handleTransferFallback(UUID fromWalletId, UUID toWalletId, BigDecimal amount, UUID requestId, Throwable t) {
        log.error("Fallback triggered for transfer. From: {}, To: {}, Reason: {}", fromWalletId, toWalletId, t.getMessage());
        throw new BusinessException(HttpStatus.SERVICE_UNAVAILABLE, ErrorCodeEnum.WS500001);
    }
}

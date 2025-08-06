package com.rgs.wallet.application.service.fallback;

import com.rgs.wallet.domain.enums.ErrorCodeEnum;
import com.rgs.wallet.domain.exceptions.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WalletFallbackHandlerTest {

    private WalletFallbackHandler fallbackHandler;

    @BeforeEach
    void setUp() {
        fallbackHandler = new WalletFallbackHandler();
    }

    @Test
    void shouldThrowBusinessExceptionOnDepositFallback() {
        UUID walletId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.TEN;
        Throwable cause = new RuntimeException("Simulated failure");

        BusinessException ex = assertThrows(BusinessException.class, () ->
                fallbackHandler.handleDepositFallback(walletId, amount, requestId, cause)
        );

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, ex.getStatus());
        assertEquals(ErrorCodeEnum.WS500001, ex.getErrorCode());
    }

    @Test
    void shouldThrowBusinessExceptionOnWithdrawFallback() {
        UUID walletId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.TEN;
        Throwable cause = new RuntimeException("Simulated failure");

        BusinessException ex = assertThrows(BusinessException.class, () ->
                fallbackHandler.handleWithdrawFallback(walletId, amount, requestId, cause)
        );

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, ex.getStatus());
        assertEquals(ErrorCodeEnum.WS500001, ex.getErrorCode());
    }

    @Test
    void shouldThrowBusinessExceptionOnTransferFallback() {
        UUID fromWalletId = UUID.randomUUID();
        UUID toWalletId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.TEN;
        Throwable cause = new RuntimeException("Simulated failure");

        BusinessException ex = assertThrows(BusinessException.class, () ->
                fallbackHandler.handleTransferFallback(fromWalletId, toWalletId, amount, requestId, cause)
        );

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, ex.getStatus());
        assertEquals(ErrorCodeEnum.WS500001, ex.getErrorCode());
    }
}

package com.rgs.wallet.infrastructure.api.controllers;

import com.rgs.wallet.domain.model.Wallet;
import com.rgs.wallet.infrastructure.api.dtos.*;
import com.rgs.wallet.ports.in.WalletServicePort;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletServicePort walletService;

    @PostMapping
    public ResponseEntity<UUID> createWallet(@Valid @RequestBody CreateWalletRequest request) {
        Wallet wallet = walletService.createWallet(request.userId());
        return ResponseEntity.ok(wallet.getId());
    }

    @GetMapping("/{walletId}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable UUID walletId) {
        return ResponseEntity.ok(walletService.getBalance(walletId));
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<WalletResponse> getWallet(@PathVariable UUID walletId) {
        return ResponseEntity.ok(WalletResponse.fromDomain(walletService.getWallet(walletId)));
    }

    @PostMapping("/deposit")
    public ResponseEntity<Void> deposit(
            @RequestBody @Valid SingleWalletOperationRequest request,
            @RequestHeader("X-Request-ID") @NotNull UUID requestId) {
        walletService.deposit(request.walletId(), request.amount(), requestId);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdraw(@RequestBody @Valid SingleWalletOperationRequest request,
                                         @RequestHeader("X-Request-ID") @NotNull UUID requestId) {

        walletService.withdraw(request.walletId(), request.amount(), requestId);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(@RequestBody @Valid TransferBetweenWalletsRequest request,
                                         @RequestHeader("X-Request-ID") @NotNull UUID requestId) {
        walletService.transfer(request.fromWalletId(), request.toWalletId(), request.amount(), requestId);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{walletId}/historical-balance")
    public ResponseEntity<WalletHistoricalBalanceResponse> getHistoricalBalance(
            @PathVariable UUID walletId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant timestamp) {

        BigDecimal balance = walletService.getHistoricalBalance(walletId, timestamp);
        return ResponseEntity.ok(
                new WalletHistoricalBalanceResponse(balance, timestamp, walletId)
        );
    }
}

package com.rgs.wallet.infrastructure.api.controllers;

import com.rgs.wallet.domain.model.Wallet;
import com.rgs.wallet.infrastructure.api.controllers.docs.WalletControllerDocs;
import com.rgs.wallet.infrastructure.api.dtos.*;
import com.rgs.wallet.ports.in.WalletServicePort;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController implements WalletControllerDocs {

    private final WalletServicePort walletService;

    @Override
    @PostMapping
    public ResponseEntity<UUID> createWallet(@Valid @RequestBody CreateWalletRequest request) {
        Wallet wallet = walletService.createWallet(request.userId());
        return ResponseEntity.ok(wallet.getId());
    }

    @Override
    @GetMapping("/{walletId}/balance")
    public ResponseEntity<WalletBalanceResponse> getBalance(@PathVariable UUID walletId) {
        return ResponseEntity.ok(new WalletBalanceResponse(walletService.getBalance(walletId), walletId));
    }

    @Override
    @GetMapping("/{walletId}")
    public ResponseEntity<WalletResponse> getWallet(@PathVariable UUID walletId) {
        return ResponseEntity.ok(WalletResponse.fromDomain(walletService.getWallet(walletId)));
    }

    @Override
    @PostMapping("/deposit")
    public ResponseEntity<Void> deposit(@RequestBody @Valid SingleWalletOperationRequest request,
                                        @RequestHeader("X-Request-ID") @NotNull UUID requestId) {
        walletService.deposit(request.walletId(), request.amount(), requestId);
        return ResponseEntity.accepted().build();
    }

    @Override
    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdraw(@RequestBody @Valid SingleWalletOperationRequest request,
                                         @RequestHeader("X-Request-ID") @NotNull UUID requestId) {
        walletService.withdraw(request.walletId(), request.amount(), requestId);
        return ResponseEntity.accepted().build();
    }

    @Override
    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(@RequestBody @Valid TransferBetweenWalletsRequest request,
                                         @RequestHeader("X-Request-ID") @NotNull UUID requestId) {
        walletService.transfer(request.fromWalletId(), request.toWalletId(), request.amount(), requestId);
        return ResponseEntity.accepted().build();
    }

    @Override
    @GetMapping("/{walletId}/historical-balance")
    public ResponseEntity<WalletHistoricalBalanceResponse> getHistoricalBalance(UUID walletId, Instant timestamp) {
        BigDecimal balance = walletService.getHistoricalBalance(walletId, timestamp);
        return ResponseEntity.ok(new WalletHistoricalBalanceResponse(balance, timestamp, walletId));
    }
}

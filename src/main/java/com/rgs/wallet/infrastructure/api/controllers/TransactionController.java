package com.rgs.wallet.infrastructure.api.controllers;

import com.rgs.wallet.domain.model.Transaction;
import com.rgs.wallet.infrastructure.api.dtos.TransactionResponse;
import com.rgs.wallet.ports.in.WalletServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final WalletServicePort walletService;

    @GetMapping("/wallet/{walletId}")
    public ResponseEntity<List<TransactionResponse>> getWalletTransactions(
            @PathVariable UUID walletId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {

        List<Transaction> transactions = walletService.getTransactions(walletId, from, to);

        return ResponseEntity.ok(
                transactions.stream()
                        .map(TransactionResponse::fromDomain)
                        .toList()
        );
    }

}

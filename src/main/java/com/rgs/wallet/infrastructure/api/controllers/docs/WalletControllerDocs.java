package com.rgs.wallet.infrastructure.api.controllers.docs;

import com.rgs.wallet.infrastructure.api.dtos.*;
import com.rgs.wallet.infrastructure.api.exceptions.ExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.util.UUID;

public interface WalletControllerDocs {

    @Operation(summary = "Create a new wallet",
            description = "Creates a new wallet for a user. If the user already has a wallet, an error is returned.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Wallet created successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UUID.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request data",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(responseCode = "409", description = "User already has a wallet",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
            }
    )
    ResponseEntity<UUID> createWallet(@Valid @RequestBody CreateWalletRequest request);

    @Operation(summary = "Get wallet balance", description = "Retrieves the current balance of a specified wallet.")
    ResponseEntity<WalletBalanceResponse> getBalance(@PathVariable UUID walletId);

    @Operation(summary = "Get wallet details", description = "Retrieves the details of a specified wallet.")
    ResponseEntity<WalletResponse> getWallet(@PathVariable UUID walletId);

    @Operation(summary = "Deposit funds into a wallet", description = "Deposits a specified amount into the wallet.")
    ResponseEntity<Void> deposit(@RequestBody @Valid SingleWalletOperationRequest request,
                                 @RequestHeader("X-Request-ID") @NotNull UUID requestId);

    @Operation(summary = "Withdraw funds from a wallet", description = "Withdraws a specified amount from the wallet.")
    ResponseEntity<Void> withdraw(@RequestBody @Valid SingleWalletOperationRequest request,
                                  @RequestHeader("X-Request-ID") @NotNull UUID requestId);

    @Operation(summary = "Transfer between wallets", description = "Transfers amount between two wallets.")
    ResponseEntity<Void> transfer(@RequestBody @Valid TransferBetweenWalletsRequest request,
                                  @RequestHeader("X-Request-ID") @NotNull UUID requestId);

    @Operation(summary = "Get historical wallet balance", description = "Retrieves balance at specific timestamp.")
    ResponseEntity<WalletHistoricalBalanceResponse> getHistoricalBalance(@PathVariable UUID walletId,
                                                                         @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant timestamp);
}

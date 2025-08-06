
package com.rgs.wallet.infrastructure.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rgs.wallet.domain.model.Wallet;
import com.rgs.wallet.fixtures.WalletFixture;
import com.rgs.wallet.infrastructure.api.dtos.CreateWalletRequest;
import com.rgs.wallet.infrastructure.api.dtos.SingleWalletOperationRequest;
import com.rgs.wallet.infrastructure.api.dtos.TransferBetweenWalletsRequest;
import com.rgs.wallet.ports.in.WalletServicePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WalletController.class)
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WalletServicePort walletService;

    private UUID walletId;
    private UUID userId;
    private UUID requestId;

    @BeforeEach
    void setup() {
        walletId = UUID.randomUUID();
        userId = UUID.randomUUID();
        requestId = UUID.randomUUID();
    }

    @Test
    void shouldCreateWallet() throws Exception {
        when(walletService.createWallet(userId)).thenReturn(WalletFixture.withParameters(walletId, userId));

        CreateWalletRequest request = new CreateWalletRequest(userId);

        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(walletId.toString()));
    }

    @Test
    void shouldGetBalance() throws Exception {
        when(walletService.getBalance(walletId)).thenReturn(BigDecimal.TEN);

        mockMvc.perform(get("/api/v1/wallets/{walletId}/balance", walletId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(10))
                .andExpect(jsonPath("$.walletId").value(walletId.toString()));
    }

    @Test
    void shouldGetWallet() throws Exception {
        Wallet wallet = WalletFixture.withParameters(walletId, userId);
        when(walletService.getWallet(walletId)).thenReturn(wallet);

        mockMvc.perform(get("/api/v1/wallets/{walletId}", walletId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(walletId.toString()))
                .andExpect(jsonPath("$.userId.id").value(userId.toString()));
    }

    @Test
    void shouldDeposit() throws Exception {
        doNothing().when(walletService).deposit(walletId, BigDecimal.TEN, requestId);

        SingleWalletOperationRequest request = new SingleWalletOperationRequest(walletId, BigDecimal.TEN);

        mockMvc.perform(post("/api/v1/wallets/deposit")
                        .header("X-Request-ID", requestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted());
    }

    @Test
    void shouldWithdraw() throws Exception {
        doNothing().when(walletService).withdraw(walletId, BigDecimal.TEN, requestId);

        SingleWalletOperationRequest request = new SingleWalletOperationRequest(walletId, BigDecimal.TEN);

        mockMvc.perform(post("/api/v1/wallets/withdraw")
                        .header("X-Request-ID", requestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted());
    }

    @Test
    void shouldTransfer() throws Exception {
        UUID toWalletId = UUID.randomUUID();
        doNothing().when(walletService).transfer(walletId, toWalletId, BigDecimal.TEN, requestId);

        TransferBetweenWalletsRequest request = new TransferBetweenWalletsRequest(walletId, toWalletId, BigDecimal.TEN);

        mockMvc.perform(post("/api/v1/wallets/transfer")
                        .header("X-Request-ID", requestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted());
    }

    @Test
    void shouldGetHistoricalBalance() throws Exception {
        Instant timestamp = Instant.now();
        when(walletService.getHistoricalBalance(walletId, timestamp)).thenReturn(BigDecimal.TEN);

        mockMvc.perform(get("/api/v1/wallets/{walletId}/historical-balance", walletId)
                        .param("timestamp", timestamp.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(10))
                .andExpect(jsonPath("$.queryTimestamp").value(timestamp.toString()))
                .andExpect(jsonPath("$.walletId").value(walletId.toString()));
    }
}
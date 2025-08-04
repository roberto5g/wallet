package com.rgs.wallet.infrastructure.idempotency;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rgs.wallet.domain.model.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String BALANCE_PREFIX = "wallet-balance:";
    private static final String HISTORICAL_PREFIX = "historical-balance:";
    private static final String WALLET_PREFIX = "wallet:";

    @Value("${app.cache.balance-ttl-seconds}")
    private long balanceTtl;
    @Value("${app.cache.historical-balance-ttl-seconds}")
    private long historicalTtl;
    @Value("${app.cache.wallet-ttl-seconds}")
    private long walletTtl;


    public BigDecimal getCachedBalance(UUID walletId) {
        String value = redisTemplate.opsForValue().get(BALANCE_PREFIX + walletId);
        return value != null ? new BigDecimal(value) : null;
    }

    public void cacheBalance(UUID walletId, BigDecimal balance) {
        redisTemplate.opsForValue().set(BALANCE_PREFIX + walletId, balance.toPlainString(), Duration.ofSeconds(balanceTtl));
    }

    public BigDecimal getCachedHistoricalBalance(UUID walletId, Instant timestamp) {
        String key = HISTORICAL_PREFIX + walletId + ":" + timestamp;
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? new BigDecimal(value) : null;
    }

    public void cacheHistoricalBalance(UUID walletId, Instant timestamp, BigDecimal balance) {
        String key = HISTORICAL_PREFIX + walletId + ":" + timestamp;
        redisTemplate.opsForValue().set(key, balance.toPlainString(), Duration.ofSeconds(historicalTtl));
    }

    public Wallet getCachedWallet(UUID walletId) {
        String key = WALLET_PREFIX + walletId;
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) return null;

        try {
            return objectMapper.readValue(json, Wallet.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public void cacheWallet(UUID walletId, Wallet wallet) {
        try {
            String json = objectMapper.writeValueAsString(wallet);
            redisTemplate.opsForValue().set(WALLET_PREFIX + walletId, json, Duration.ofSeconds(walletTtl));
        } catch (JsonProcessingException ignored) {
        }
    }

    public void clearCache(UUID walletId) {
        redisTemplate.delete(BALANCE_PREFIX + walletId);
        redisTemplate.delete(WALLET_PREFIX + walletId);
    }

}


package com.rgs.wallet.infrastructure.idempotency;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rgs.wallet.domain.exceptions.ConcurrentRequestException;
import com.rgs.wallet.domain.exceptions.DuplicateRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IdempotencyService {
    private static final String IDEMPOTENCY_PREFIX = "idempotency:";
    private static final String LOCK_PREFIX = "lock:";

    @Value("${app.idempotency.ttl-seconds}")
    private long idempotencyTtl;

    @Value("${app.idempotency.lock-timeout}")
    private long lockTimeout;

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Retryable(
            retryFor = ConcurrentRequestException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100, multiplier = 2)
    )
    public void processWithIdempotency(UUID requestId, Runnable operation) {
        checkDuplicate(requestId);
        tryWithLock(requestId, operation);
        registerSuccess(requestId);
    }

    private void checkDuplicate(UUID requestId) {
        if (redisTemplate.hasKey(IDEMPOTENCY_PREFIX + requestId)) {
            throw new DuplicateRequestException();
        }
    }

    private void tryWithLock(UUID requestId, Runnable operation) {
        if (!acquireLock(requestId)) {
            throw new ConcurrentRequestException();
        }
        try {
            operation.run();
        } finally {
            releaseLock(requestId);
        }
    }

    private boolean acquireLock(UUID requestId) {
        return Boolean.TRUE.equals(
                redisTemplate.opsForValue().setIfAbsent(
                        LOCK_PREFIX + requestId,
                        "1",
                        Duration.ofSeconds(lockTimeout)
                ));
    }

    private void releaseLock(UUID requestId) {
        redisTemplate.delete(LOCK_PREFIX + requestId);
    }

    private void registerSuccess(UUID requestId) {
        redisTemplate.opsForValue().set(
                IDEMPOTENCY_PREFIX + requestId,
                "processed",
                Duration.ofSeconds(idempotencyTtl)
        );
    }
}

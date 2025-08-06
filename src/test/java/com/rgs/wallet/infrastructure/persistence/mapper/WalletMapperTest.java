package com.rgs.wallet.infrastructure.persistence.mapper;

import com.rgs.wallet.domain.model.User;
import com.rgs.wallet.domain.model.Wallet;
import com.rgs.wallet.infrastructure.persistence.entity.UserEntity;
import com.rgs.wallet.infrastructure.persistence.entity.WalletEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class WalletMapperTest {

    private UserMapper userMapper;
    private WalletMapper walletMapper;

    @BeforeEach
    void setup() {
        userMapper = mock(UserMapper.class);
        walletMapper = new WalletMapper(userMapper);
    }

    @Test
    void testToDomain() {
        UUID walletId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Instant now = Instant.now();

        UserEntity userEntity = UserEntity.builder().id(userId).build();
        WalletEntity entity = WalletEntity.builder()
                .id(walletId)
                .user(userEntity)
                .balance(BigDecimal.valueOf(100))
                .status(com.rgs.wallet.domain.model.WalletStatus.ACTIVE)
                .createdAt(now)
                .updatedAt(now)
                .build();

        User userDomain = User.builder().id(userId).build();
        when(userMapper.toDomain(userEntity)).thenReturn(userDomain);

        Wallet wallet = walletMapper.toDomain(entity);

        assertNotNull(wallet);
        assertEquals(walletId, wallet.getId());
        assertEquals(userDomain, wallet.getUser());
        assertEquals(BigDecimal.valueOf(100), wallet.getBalance());
        assertEquals(com.rgs.wallet.domain.model.WalletStatus.ACTIVE, wallet.getStatus());
        assertEquals(now, wallet.getCreatedAt());
        assertEquals(now, wallet.getUpdatedAt());

        verify(userMapper).toDomain(userEntity);
    }

    @Test
    void testToEntity() {
        UUID walletId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Instant now = Instant.now();

        User userDomain = User.builder().id(userId).build();
        Wallet wallet = Wallet.builder()
                .id(walletId)
                .user(userDomain)
                .balance(BigDecimal.valueOf(100))
                .status(com.rgs.wallet.domain.model.WalletStatus.ACTIVE)
                .createdAt(now)
                .updatedAt(now)
                .build();

        UserEntity userEntity = UserEntity.builder().id(userId).build();
        when(userMapper.toEntity(userDomain)).thenReturn(userEntity);

        WalletEntity entity = walletMapper.toEntity(wallet);

        assertNotNull(entity);
        assertEquals(walletId, entity.getId());
        assertEquals(userEntity, entity.getUser());
        assertEquals(BigDecimal.valueOf(100), entity.getBalance());
        assertEquals(com.rgs.wallet.domain.model.WalletStatus.ACTIVE, entity.getStatus());
        assertEquals(now, entity.getCreatedAt());
        assertEquals(now, entity.getUpdatedAt());

        verify(userMapper).toEntity(userDomain);
    }
}

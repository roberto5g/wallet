package com.rgs.wallet.infrastructure.persistence.mapper;

import com.rgs.wallet.domain.model.User;
import com.rgs.wallet.domain.model.Wallet;
import com.rgs.wallet.infrastructure.persistence.entity.UserEntity;
import com.rgs.wallet.infrastructure.persistence.entity.WalletEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WalletMapper {

    private final UserMapper userMapper;

    public Wallet toDomain(WalletEntity entity) {
        return Wallet.builder()
                .id(entity.getId())
                .user(userMapper.toDomain(entity.getUser()))
                .balance(entity.getBalance())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public WalletEntity toEntity(Wallet wallet) {
        return WalletEntity.builder()
                .id(wallet.getId())
                .user(userMapper.toEntity(wallet.getUser()))
                .balance(wallet.getBalance())
                .status(wallet.getStatus())
                .createdAt(wallet.getCreatedAt())
                .updatedAt(wallet.getUpdatedAt())
                .build();
    }
}

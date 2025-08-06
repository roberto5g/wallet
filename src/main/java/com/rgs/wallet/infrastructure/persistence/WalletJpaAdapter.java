package com.rgs.wallet.infrastructure.persistence;

import com.rgs.wallet.domain.model.User;
import com.rgs.wallet.domain.model.Wallet;
import com.rgs.wallet.infrastructure.persistence.entity.WalletEntity;
import com.rgs.wallet.infrastructure.persistence.mapper.WalletMapper;
import com.rgs.wallet.infrastructure.persistence.repository.TransactionJpaRepository;
import com.rgs.wallet.infrastructure.persistence.repository.WalletJpaRepository;
import com.rgs.wallet.ports.out.WalletPersistencePort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class WalletJpaAdapter implements WalletPersistencePort {

    private final WalletJpaRepository walletRepository;
    private final WalletMapper mapper;

    @Override
    @Transactional
    public Wallet save(Wallet wallet) {
        WalletEntity entity = mapper.toEntity(wallet);
        WalletEntity saved = walletRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional
    public Optional<Wallet> findById(UUID walletId) {
        return walletRepository.findById(walletId)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByUser(User user) {
        return walletRepository.findByUserId(user.getId())
                .isPresent();
    }

}

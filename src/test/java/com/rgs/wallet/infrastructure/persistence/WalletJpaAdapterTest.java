package com.rgs.wallet.infrastructure.persistence;

import com.rgs.wallet.domain.model.User;
import com.rgs.wallet.domain.model.Wallet;
import com.rgs.wallet.fixtures.UserFixture;
import com.rgs.wallet.fixtures.WalletFixture;
import com.rgs.wallet.infrastructure.persistence.entity.WalletEntity;
import com.rgs.wallet.infrastructure.persistence.mapper.WalletMapper;
import com.rgs.wallet.infrastructure.persistence.repository.WalletJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class WalletJpaAdapterTest {

    @Mock
    private WalletJpaRepository walletRepository;

    @Mock
    private WalletMapper mapper;

    @InjectMocks
    private WalletJpaAdapter adapter;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldSaveWalletSuccessfully() {
        Wallet wallet = WalletFixture.defaultWallet();
        WalletEntity entity = new WalletEntity();
        WalletEntity savedEntity = new WalletEntity();

        when(mapper.toEntity(wallet)).thenReturn(entity);
        when(walletRepository.save(entity)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(wallet);

        Wallet result = adapter.save(wallet);

        assertThat(result).isEqualTo(wallet);
        verify(mapper).toEntity(wallet);
        verify(walletRepository).save(entity);
        verify(mapper).toDomain(savedEntity);
    }

    @Test
    void shouldFindWalletById() {
        UUID walletId = UUID.randomUUID();
        WalletEntity entity = new WalletEntity();
        Wallet wallet = WalletFixture.defaultWallet();

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(wallet);

        Optional<Wallet> result = adapter.findById(walletId);

        assertThat(result).isPresent().contains(wallet);
        verify(walletRepository).findById(walletId);
        verify(mapper).toDomain(entity);
    }

    @Test
    void shouldReturnEmptyWhenWalletNotFound() {
        UUID walletId = UUID.randomUUID();

        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        Optional<Wallet> result = adapter.findById(walletId);

        assertThat(result).isEmpty();
        verify(walletRepository).findById(walletId);
        verify(mapper, never()).toDomain(any());
    }

    @Test
    void shouldReturnTrueIfWalletExistsByUser() {
        User user = UserFixture.createUser(UUID.randomUUID());
        WalletEntity entity = new WalletEntity();

        when(walletRepository.findByUserId(user.getId())).thenReturn(Optional.of(entity));

        boolean exists = adapter.existsByUser(user);

        assertThat(exists).isTrue();
        verify(walletRepository).findByUserId(user.getId());
    }

    @Test
    void shouldReturnFalseIfWalletDoesNotExistByUser() {
        User user = UserFixture.createUser(UUID.randomUUID());

        when(walletRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        boolean exists = adapter.existsByUser(user);

        assertThat(exists).isFalse();
        verify(walletRepository).findByUserId(user.getId());
    }
}

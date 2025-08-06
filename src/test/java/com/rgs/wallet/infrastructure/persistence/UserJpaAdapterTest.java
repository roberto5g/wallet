package com.rgs.wallet.infrastructure.persistence;

import com.rgs.wallet.domain.exceptions.UserNotFoundException;
import com.rgs.wallet.domain.model.User;
import com.rgs.wallet.fixtures.UserFixture;
import com.rgs.wallet.infrastructure.persistence.entity.UserEntity;
import com.rgs.wallet.infrastructure.persistence.mapper.UserMapper;
import com.rgs.wallet.infrastructure.persistence.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserJpaAdapterTest {

    private UserJpaRepository userRepository;
    private UserMapper userMapper;
    private UserJpaAdapter adapter;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserJpaRepository.class);
        userMapper = mock(UserMapper.class);
        adapter = new UserJpaAdapter(userRepository, userMapper);
    }

    @Test
    void shouldReturnUserWhenFound() {
        UUID userId = UUID.randomUUID();
        UserEntity entity = UserEntity.builder().id(userId).taxId("123456789").name("John Doe").build();
        User user = UserFixture.createUser(UUID.randomUUID());

        when(userRepository.findById(userId)).thenReturn(Optional.of(entity));
        when(userMapper.toDomain(entity)).thenReturn(user);

        Optional<User> result = adapter.findById(userId);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(userRepository).findById(userId);
        verify(userMapper).toDomain(entity);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> adapter.findById(userId));
        verify(userRepository).findById(userId);
        verifyNoInteractions(userMapper);
    }
}
package com.rgs.wallet.infrastructure.persistence;

import com.rgs.wallet.domain.exceptions.UserNotFoundException;
import com.rgs.wallet.domain.model.User;
import com.rgs.wallet.infrastructure.persistence.mapper.UserMapper;
import com.rgs.wallet.infrastructure.persistence.repository.UserJpaRepository;
import com.rgs.wallet.ports.out.UserPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserJpaAdapter implements UserPersistencePort {

    private final UserJpaRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Optional<User> findById(UUID userId) {
        return Optional.ofNullable(userRepository.findById(userId)
                .map(userMapper::toDomain)
                .orElseThrow(UserNotFoundException::new));
    }
}

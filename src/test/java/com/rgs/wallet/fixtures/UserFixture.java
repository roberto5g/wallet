package com.rgs.wallet.fixtures;

import com.rgs.wallet.domain.model.User;

import java.time.Instant;
import java.util.UUID;

public class UserFixture {
    public static User createUser(UUID id) {
        return User.builder()
                .id(id)
                .name("John Doe")
                .taxId("123456789")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
}

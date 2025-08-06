package com.rgs.wallet.infrastructure.persistence.mapper;

import com.rgs.wallet.domain.model.User;
import com.rgs.wallet.infrastructure.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserMapperTest {

    private final UserMapper userMapper = new UserMapper();

    @Test
    void testToDomain() {
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.now().minusSeconds(3600);
        Instant updatedAt = Instant.now();

        UserEntity entity = UserEntity.builder()
                .id(id)
                .name("John Doe")
                .taxId("123456789")
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        User user = userMapper.toDomain(entity);

        assertNotNull(user);
        assertEquals(id, user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("123456789", user.getTaxId());
        assertEquals(createdAt, user.getCreatedAt());
        assertEquals(updatedAt, user.getUpdatedAt());
    }

    @Test
    void testToEntity() {
        UUID id = UUID.randomUUID();

        User user = User.builder()
                .id(id)
                .name("Jane Doe")
                .taxId("987654321")
                .build();

        UserEntity entity = userMapper.toEntity(user);

        assertNotNull(entity);
        assertEquals(id, entity.getId());
        assertEquals("Jane Doe", entity.getName());
        assertEquals("987654321", entity.getTaxId());
    }
}

package com.rgs.wallet.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
public class User {
    private UUID id;
    private String name;
    private String taxId;
    private Instant createdAt;
    private Instant updatedAt;
}

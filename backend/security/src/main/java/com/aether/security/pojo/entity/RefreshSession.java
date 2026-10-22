package com.aether.security.pojo.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@Document(collection = "refresh_sessions")
public class RefreshSession {
    @Id
    private String id;

    @Indexed
    private String userId;
    private String tokenHash;
    private boolean revoked;
    private Instant createdAt;
    private Instant lastUsedAt;

    @Indexed(expireAfter = "0s")
    private Instant expiresAt;
}



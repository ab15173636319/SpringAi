package com.aether.security.pojo.vto;

import com.aether.security.pojo.entity.UserRole;

import java.time.Instant;

public record AuthView(
        String id,
        String username,
        String displayName,
        String email,
        String avatarColor,
        UserRole role,
        Instant createdAt
) {
}




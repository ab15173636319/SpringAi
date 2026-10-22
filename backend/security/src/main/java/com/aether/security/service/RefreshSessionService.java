package com.aether.security.service;

import com.aether.security.pojo.entity.RefreshSession;

import java.time.Instant;

public interface RefreshSessionService {
    void create(RefreshSession session);

    boolean rotate(String sessionId, String currentHash, String nextHash, Instant expiresAt);

    void revoke(String sessionId);
}


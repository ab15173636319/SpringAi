package com.aether.security.service;

import java.time.Duration;

public interface TokenSessionService {
    void activate(
            String userId,
            String sessionId,
            String refreshTokenHash,
            Duration refreshTtl,
            String accessTokenId,
            Duration accessTtl
    );

    boolean isActive(String userId, String sessionId);

    String getRefreshTokenHash(String sessionId);

    boolean rotateRefreshToken(
            String sessionId,
            String currentHash,
            String nextHash,
            Duration refreshTtl
    );

    void rotateAccessTokenId(String sessionId, String accessTokenId, Duration accessTtl);

    boolean isAccessTokenBlacklisted(String accessTokenId);

    void blacklistAccessToken(String accessTokenId, Duration accessTtl);

    void revoke(
            String userId,
            String sessionId,
            String accessTokenId,
            Duration accessTtl
    );
}

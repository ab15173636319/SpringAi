package com.aether.security.service.impl;

import com.aether.security.service.TokenSessionService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class TokenSessionServiceImpl implements TokenSessionService {
    private static final String ACTIVE_SESSION_PREFIX = "auth:active-session:";
    private static final String REFRESH_TOKEN_PREFIX = "auth:refresh-token:";
    private static final String ACCESS_TOKEN_PREFIX = "auth:access-token:";
    private static final String BLACKLIST_PREFIX = "auth:blacklist:";

    private static final DefaultRedisScript<Long> ROTATE_REFRESH_SCRIPT =
            new DefaultRedisScript<>("""
                    local current = redis.call('GET', KEYS[1])
                    if current ~= ARGV[1] then
                        return 0
                    end
                    redis.call('SET', KEYS[1], ARGV[2], 'PX', ARGV[3])
                    return 1
                    """, Long.class);

    private final StringRedisTemplate redis;

    public TokenSessionServiceImpl(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public void activate(
            String userId,
            String sessionId,
            String refreshTokenHash,
            Duration refreshTtl,
            String accessTokenId,
            Duration accessTtl
    ) {
        String activeKey = activeSessionKey(userId);
        String previousSessionId = redis.opsForValue().get(activeKey);
        if (previousSessionId != null && !previousSessionId.equals(sessionId)) {
            revoke(previousSessionId, null);
        }

        redis.opsForValue().set(refreshTokenKey(sessionId), refreshTokenHash, refreshTtl);
        redis.opsForValue().set(accessTokenKey(sessionId), accessTokenId, accessTtl);
        redis.opsForValue().set(activeKey, sessionId, refreshTtl);
    }

    @Override
    public boolean isActive(String userId, String sessionId) {
        return sessionId.equals(redis.opsForValue().get(activeSessionKey(userId)));
    }

    @Override
    public String getRefreshTokenHash(String sessionId) {
        return redis.opsForValue().get(refreshTokenKey(sessionId));
    }

    @Override
    public boolean rotateRefreshToken(
            String sessionId,
            String currentHash,
            String nextHash,
            Duration refreshTtl
    ) {
        Long result = redis.execute(
                ROTATE_REFRESH_SCRIPT,
                List.of(refreshTokenKey(sessionId)),
                currentHash,
                nextHash,
                String.valueOf(refreshTtl.toMillis())
        );
        return Long.valueOf(1L).equals(result);
    }

    @Override
    public void rotateAccessTokenId(String sessionId, String accessTokenId, Duration accessTtl) {
        String accessKey = accessTokenKey(sessionId);
        revokeLatestAccessToken(sessionId);
        redis.opsForValue().set(accessKey, accessTokenId, accessTtl);
    }

    @Override
    public boolean isAccessTokenBlacklisted(String accessTokenId) {
        return Boolean.TRUE.equals(redis.hasKey(blacklistKey(accessTokenId)));
    }

    @Override
    public void blacklistAccessToken(String accessTokenId, Duration accessTtl) {
        if (accessTtl.isZero() || accessTtl.isNegative()) {
            return;
        }
        redis.opsForValue().set(blacklistKey(accessTokenId), "1", accessTtl);
    }

    @Override
    public void revoke(
            String userId,
            String sessionId,
            String accessTokenId,
            Duration accessTtl
    ) {
        if (sessionId == null) {
            return;
        }
        String activeKey = userId == null ? null : activeSessionKey(userId);
        if (accessTokenId != null) {
            blacklistAccessToken(accessTokenId, accessTtl);
        } else {
            revokeLatestAccessToken(sessionId);
        }
        if (activeKey != null
                && sessionId.equals(redis.opsForValue().get(activeKey))) {
            redis.delete(activeKey);
        }
        redis.delete(refreshTokenKey(sessionId));
        redis.delete(accessTokenKey(sessionId));
    }

    private void revoke(String sessionId, String accessTokenId) {
        revoke(null, sessionId, accessTokenId, Duration.ZERO);
    }

    private void revokeLatestAccessToken(String sessionId) {
        String accessKey = accessTokenKey(sessionId);
        String latestAccessTokenId = redis.opsForValue().get(accessKey);
        if (latestAccessTokenId == null) {
            return;
        }
        Long remainingMs = redis.getExpire(accessKey, TimeUnit.MILLISECONDS);
        if (remainingMs != null && remainingMs > 0) {
            blacklistAccessToken(latestAccessTokenId, Duration.ofMillis(remainingMs));
        }
    }

    private String activeSessionKey(String userId) {
        return ACTIVE_SESSION_PREFIX + userId;
    }

    private String refreshTokenKey(String sessionId) {
        return REFRESH_TOKEN_PREFIX + sessionId;
    }

    private String accessTokenKey(String sessionId) {
        return ACCESS_TOKEN_PREFIX + sessionId;
    }

    private String blacklistKey(String accessTokenId) {
        return BLACKLIST_PREFIX + accessTokenId;
    }
}

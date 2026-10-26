package com.aether.security.service.impl;

import com.aether.common.error.BusinessException;
import com.aether.common.result.ResponseCode;
import com.aether.security.config.SecurityProperties;
import com.aether.security.pojo.dto.IssuedAccessToken;
import com.aether.security.pojo.entity.UserAccount;
import com.aether.security.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtServiceImpl implements JwtService {
    private final SecurityProperties properties;
    private final SecretKey signingKey;

    public JwtServiceImpl(SecurityProperties properties) {
        this.properties = properties;
        byte[] secret = properties.jwt().secret().getBytes(StandardCharsets.UTF_8);
        if (secret.length < 32) {
            throw new IllegalStateException("JWT secret must contain at least 32 bytes");
        }
        this.signingKey = Keys.hmacShaKeyFor(secret);
    }

    @Override
    public IssuedAccessToken createAccessToken(UserAccount user, String sessionId, Instant now) {
        String tokenId = UUID.randomUUID().toString();
        String token = Jwts.builder()
                .issuer(properties.jwt().issuer())
                .subject(user.getId())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(properties.jwt().accessTtl())))
                .id(tokenId)
                .claim("type", ACCESS)
                .claim("sid", sessionId)
                .claim("username", user.getUsername())
                .claim("role", user.getRole().name())
                .signWith(signingKey)
                .compact();
        return new IssuedAccessToken(token, tokenId, sessionId);
    }

    @Override
    public String createRefreshToken(UserAccount user, String sessionId, Instant now) {
        return Jwts.builder()
                .issuer(properties.jwt().issuer())
                .subject(user.getId())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(properties.jwt().refreshTtl())))
                .id(sessionId)
                .claim("type", REFRESH)
                .claim("sid", sessionId)
                .claim("nonce", UUID.randomUUID().toString())
                .signWith(signingKey)
                .compact();
    }

    @Override
    public Claims parse(String token, String expectedType) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .requireIssuer(properties.jwt().issuer())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            if (!expectedType.equals(claims.get("type", String.class))) {
                throw new BusinessException(ResponseCode.UNAUTHORIZED, "Token 类型不正确");
            }
            return claims;
        } catch (JwtException | IllegalArgumentException exception) {
            throw new BusinessException(
                    ResponseCode.UNAUTHORIZED,
                    "登录凭证无效或已过期",
                    exception
            );
        }
    }

    @Override
    public String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}



package com.aether.security.service;

import com.aether.security.pojo.dto.IssuedAccessToken;
import com.aether.security.pojo.entity.UserAccount;
import io.jsonwebtoken.Claims;

import java.time.Instant;

public interface JwtService {
    String ACCESS = "access";
    String REFRESH = "refresh";

    IssuedAccessToken createAccessToken(UserAccount user, String sessionId, Instant now);

    String createRefreshToken(UserAccount user, String sessionId, Instant now);

    Claims parse(String token, String expectedType);

    String hash(String token);
}

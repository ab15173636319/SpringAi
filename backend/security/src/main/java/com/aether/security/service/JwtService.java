package com.aether.security.service;

import com.aether.security.pojo.entity.UserAccount;
import io.jsonwebtoken.Claims;

import java.time.Instant;

public interface JwtService {
    String ACCESS = "access";
    String REFRESH = "refresh";

    String createAccessToken(UserAccount user, Instant now);

    String createRefreshToken(UserAccount user, String sessionId, Instant now);

    Claims parse(String token, String expectedType);

    String hash(String token);
}


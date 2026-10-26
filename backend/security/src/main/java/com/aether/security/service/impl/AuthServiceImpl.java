package com.aether.security.service.impl;

import com.aether.common.constant.Credit;
import com.aether.common.error.ApiException;
import com.aether.common.error.BusinessException;
import com.aether.common.result.ResponseCode;
import com.aether.security.config.SecurityProperties;
import com.aether.security.mapper.UserMapper;
import com.aether.security.pojo.dto.AuthSession;
import com.aether.security.pojo.dto.IssuedAccessToken;
import com.aether.security.pojo.dto.LoginRequest;
import com.aether.security.pojo.dto.RegisterRequest;
import com.aether.security.pojo.entity.AuthenticatedUser;
import com.aether.security.pojo.entity.UserAccount;
import com.aether.security.pojo.entity.UserRole;
import com.aether.security.pojo.vto.AuthResponse;
import com.aether.security.pojo.vto.AuthView;
import com.aether.security.service.AuthService;
import com.aether.security.service.JwtService;
import com.aether.security.service.TokenSessionService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.Duration;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final String[] AVATAR_COLORS = {
            "#7C3AED", "#0891B2", "#DB2777", "#D97706", "#059669", "#4F46E5"
    };

    private final UserMapper userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenSessionService tokenSessionService;
    private final SecurityProperties properties;
    private final Credit credit;


    public AuthSession register(RegisterRequest request) {
        String username = request.username().trim().toLowerCase(Locale.ROOT);
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByUsername(username)) {
            throw new BusinessException(ResponseCode.CONFLICT, "这个用户名已经被使用");
        }
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ResponseCode.CONFLICT, "这个邮箱已经注册");
        }

        Instant now = Instant.now();
        UserAccount user = UserAccount.builder()
                .username(username)
                .displayName(request.displayName().trim())
                .email(email)
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(UserRole.USER)
                .enabled(true)
                .avatarColor(colorFor(username))
                .createdAt(now)
                .updatedAt(now)
                .credits(BigDecimal.valueOf(credit.getCreditFree()))
                .build();
        try {
            user = userRepository.insert(user);
        } catch (DuplicateKeyException exception) {
            throw new BusinessException(
                    ResponseCode.CONFLICT,
                    "用户名或邮箱已经被使用",
                    exception
            );
        }
        return issueSession(user);
    }

    @Override
    public AuthSession login(LoginRequest request) {
        String identifier = request.identifier().trim().toLowerCase(Locale.ROOT);
        UserAccount user = identifier.contains("@")
                ? userRepository.findByEmail(identifier).orElse(null)
                : userRepository.findByUsername(identifier).orElse(null);
        if (user == null || !user.isEnabled()
                || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException(
                    ResponseCode.UNAUTHORIZED,
                    "用户名、邮箱或密码不正确"
            );
        }

        user.setLastLoginAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
        return issueSession(user);
    }

    @Override
    public AuthSession refresh(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new BusinessException(ResponseCode.UNAUTHORIZED, "缺少刷新凭证");
        }

        Claims claims = jwtService.parse(refreshToken, JwtService.REFRESH);
        String sessionId = claims.get("sid", String.class);
        String userId = claims.getSubject();
        if (sessionId == null || !tokenSessionService.isActive(userId, sessionId)) {
            throw ApiException.unauthorized("账号已在其他设备登录");
        }

        String storedHash = tokenSessionService.getRefreshTokenHash(sessionId);
        String currentHash = jwtService.hash(refreshToken);
        if (storedHash == null || !storedHash.equals(currentHash)) {
            throw ApiException.unauthorized("刷新凭证已失效，请重新登录");
        }

        UserAccount user = userRepository.findById(userId)
                .filter(UserAccount::isEnabled)
                .orElseThrow(() -> new BusinessException(
                        ResponseCode.UNAUTHORIZED,
                        "用户账号不可用"
                ));

        Instant now = Instant.now();
        String nextRefreshToken = jwtService.createRefreshToken(user, sessionId, now);
        if (!tokenSessionService.rotateRefreshToken(
                sessionId,
                currentHash,
                jwtService.hash(nextRefreshToken),
                properties.jwt().refreshTtl()
        )) {
            throw new BusinessException(
                    ResponseCode.UNAUTHORIZED,
                    "会话已被轮换，请重新登录"
            );
        }

        IssuedAccessToken accessToken = jwtService.createAccessToken(user, sessionId, now);
        tokenSessionService.rotateAccessTokenId(
                sessionId,
                accessToken.tokenId(),
                properties.jwt().accessTtl()
        );
        AuthResponse response = new AuthResponse(
                accessToken.token(),
                properties.jwt().accessTtl().toSeconds(),
                userRepository.toView(user)
        );
        return new AuthSession(response, nextRefreshToken);
    }

    public void logout(String refreshToken, String accessToken) {
        Claims refreshClaims = tryParse(refreshToken, JwtService.REFRESH);
        Claims accessClaims = tryParse(accessToken, JwtService.ACCESS);

        if (refreshClaims != null) {
            String sessionId = refreshClaims.get("sid", String.class);
            String userId = refreshClaims.getSubject();
            String accessTokenId = matchingAccessTokenId(accessClaims, sessionId);
            tokenSessionService.revoke(
                    userId,
                    sessionId,
                    accessTokenId,
                    remainingAccessTtl(accessClaims)
            );
            return;
        }

        if (accessClaims != null) {
            tokenSessionService.revoke(
                    accessClaims.getSubject(),
                    accessClaims.get("sid", String.class),
                    accessClaims.getId(),
                    remainingAccessTtl(accessClaims)
            );
        }
    }

    @Override
    public AuthView current(AuthenticatedUser principal) {
        UserAccount user = userRepository.findById(principal.id())
                .filter(UserAccount::isEnabled)
                .orElseThrow(() -> new BusinessException(
                        ResponseCode.UNAUTHORIZED,
                        "用户账号不可用"
                ));
        return userRepository.toView(user);
    }

    private AuthSession issueSession(UserAccount user) {
        Instant now = Instant.now();
        String sessionId = UUID.randomUUID().toString();
        String refreshToken = jwtService.createRefreshToken(user, sessionId, now);
        IssuedAccessToken accessToken = jwtService.createAccessToken(user, sessionId, now);
        tokenSessionService.activate(
                user.getId(),
                sessionId,
                jwtService.hash(refreshToken),
                properties.jwt().refreshTtl(),
                accessToken.tokenId(),
                properties.jwt().accessTtl()
        );

        AuthResponse response = new AuthResponse(
                accessToken.token(),
                properties.jwt().accessTtl().toSeconds(),
                userRepository.toView(user)
        );
        return new AuthSession(response, refreshToken);
    }

    private Claims tryParse(String token, String expectedType) {
        if (!StringUtils.hasText(token)) {
            return null;
        }
        try {
            return jwtService.parse(token, expectedType);
        } catch (ApiException ignored) {
            return null;
        }
    }

    private String matchingAccessTokenId(Claims accessClaims, String sessionId) {
        if (accessClaims == null || sessionId == null) {
            return null;
        }
        return sessionId.equals(accessClaims.get("sid", String.class))
                ? accessClaims.getId()
                : null;
    }

    private Duration remainingAccessTtl(Claims accessClaims) {
        if (accessClaims == null || accessClaims.getExpiration() == null) {
            return properties.jwt().accessTtl();
        }
        Duration remaining = Duration.between(Instant.now(), accessClaims.getExpiration().toInstant());
        return remaining.isNegative() ? Duration.ZERO : remaining;
    }

    private String colorFor(String username) {
        return AVATAR_COLORS[Math.floorMod(username.hashCode(), AVATAR_COLORS.length)];
    }
}


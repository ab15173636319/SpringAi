package com.aether.security.service.impl;

import com.aether.common.error.BusinessException;
import com.aether.common.result.ResponseCode;
import com.aether.security.config.SecurityProperties;
import com.aether.security.mapper.RefreshSessionMapper;
import com.aether.security.mapper.UserMapper;
import com.aether.security.pojo.dto.AuthSession;
import com.aether.security.pojo.dto.LoginRequest;
import com.aether.security.pojo.dto.RegisterRequest;
import com.aether.security.pojo.entity.AuthenticatedUser;
import com.aether.security.pojo.entity.RefreshSession;
import com.aether.security.pojo.entity.UserAccount;
import com.aether.security.pojo.entity.UserRole;
import com.aether.security.pojo.vto.AuthResponse;
import com.aether.security.pojo.vto.AuthView;
import com.aether.security.service.AuthService;
import com.aether.security.service.JwtService;
import com.aether.security.service.RefreshSessionService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
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
    private final RefreshSessionMapper refreshSessionRepository;
    private final RefreshSessionService refreshSessionService;
    private final SecurityProperties properties;

    @Override
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
        String sessionId = claims.getId();
        String userId = claims.getSubject();
        RefreshSession session = refreshSessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(
                        ResponseCode.UNAUTHORIZED,
                        "登录会话不存在"
                ));

        String currentHash = jwtService.hash(refreshToken);
        if (session.isRevoked()
                || !session.getUserId().equals(userId)
                || !session.getTokenHash().equals(currentHash)
                || session.getExpiresAt().isBefore(Instant.now())) {
            throw new BusinessException(
                    ResponseCode.UNAUTHORIZED,
                    "刷新凭证已失效，请重新登录"
            );
        }

        UserAccount user = userRepository.findById(userId)
                .filter(UserAccount::isEnabled)
                .orElseThrow(() -> new BusinessException(
                        ResponseCode.UNAUTHORIZED,
                        "用户账号不可用"
                ));

        Instant now = Instant.now();
        Instant nextExpiry = now.plus(properties.jwt().refreshTtl());
        String nextRefreshToken = jwtService.createRefreshToken(user, sessionId, now);
        if (!refreshSessionService.rotate(
                sessionId,
                currentHash,
                jwtService.hash(nextRefreshToken),
                nextExpiry
        )) {
            throw new BusinessException(
                    ResponseCode.UNAUTHORIZED,
                    "会话已被轮换，请重新登录"
            );
        }

        long expiresIn = properties.jwt().accessTtl().toSeconds();
        AuthResponse response = new AuthResponse(
                jwtService.createAccessToken(user, now),
                expiresIn,
                userRepository.toView(user)
        );
        return new AuthSession(response, nextRefreshToken);
    }

    @Override
    public void logout(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            return;
        }
        try {
            Claims claims = jwtService.parse(refreshToken, JwtService.REFRESH);
            refreshSessionService.revoke(claims.getId());
        } catch (BusinessException ignored) {
            // 已过期或格式错误的凭证本身已不可用。
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
        refreshSessionService.create(RefreshSession.builder()
                .id(sessionId)
                .userId(user.getId())
                .tokenHash(jwtService.hash(refreshToken))
                .revoked(false)
                .createdAt(now)
                .lastUsedAt(now)
                .expiresAt(now.plus(properties.jwt().refreshTtl()))
                .build());

        AuthResponse response = new AuthResponse(
                jwtService.createAccessToken(user, now),
                properties.jwt().accessTtl().toSeconds(),
                userRepository.toView(user)
        );
        return new AuthSession(response, refreshToken);
    }

    private String colorFor(String username) {
        return AVATAR_COLORS[Math.floorMod(username.hashCode(), AVATAR_COLORS.length)];
    }
}




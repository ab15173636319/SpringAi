package com.aether.security.controller;

import com.aether.common.error.BusinessException;
import com.aether.common.result.ResponseCode;
import com.aether.common.result.Result;
import com.aether.security.pojo.dto.AuthSession;
import com.aether.security.pojo.dto.LoginRequest;
import com.aether.security.pojo.dto.RegisterRequest;
import com.aether.security.pojo.vto.AuthResponse;
import com.aether.security.pojo.vto.AuthView;
import com.aether.security.service.AuthService;
import com.aether.security.service.RefreshCookieService;
import com.aether.security.util.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final RefreshCookieService cookieService;

    @PostMapping("/register")
    public ResponseEntity<Result<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return withRefreshCookie(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<Result<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return withRefreshCookie(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Result<AuthResponse>> refresh(HttpServletRequest request) {
        try {
            String refreshToken = cookieService.read(request)
                    .orElseThrow(() -> new BusinessException(
                            ResponseCode.UNAUTHORIZED,
                            "缺少刷新凭证"
                    ));
            return withRefreshCookie(authService.refresh(refreshToken));
        } catch (BusinessException exception) {
            return ResponseEntity.status(ResponseCode.UNAUTHORIZED.httpStatus())
                    .header(HttpHeaders.SET_COOKIE, cookieService.clear().toString())
                    .body(Result.error(ResponseCode.UNAUTHORIZED, exception.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResult<Void>> logout(HttpServletRequest request) {
        authService.logout(
                cookieService.read(request).orElse(null),
                bearerToken(request)
        );
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieService.clear().toString())
                .body(Result.success());
    }

    @GetMapping("/me")
    public Result<AuthView> me() {
        return Result.success(authService.current(SecurityUtils.currentUser()));
    }

    private ResponseEntity<Result<AuthResponse>> withRefreshCookie(AuthSession session) {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieService.create(session.refreshToken()).toString())
                .body(Result.success(session.response()));
    }

    private String bearerToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        return header != null && header.startsWith("Bearer ")
                ? header.substring(7)
                : null;
    }

    private String bearerToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        return header != null && header.startsWith("Bearer ")
                ? header.substring(7)
                : null;
    }
}



package com.aether.security.controller;

import com.aether.common.pojo.vto.ApiResult;
import com.aether.common.error.ApiException;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final RefreshCookieService cookieService;

    public AuthController(AuthService authService, RefreshCookieService cookieService) {
        this.authService = authService;
        this.cookieService = cookieService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResult<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return withRefreshCookie(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResult<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return withRefreshCookie(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResult<AuthResponse>> refresh(HttpServletRequest request) {
        try {
            String refreshToken = cookieService.read(request)
                    .orElseThrow(() -> ApiException.unauthorized("缺少刷新凭证"));
            return withRefreshCookie(authService.refresh(refreshToken));
        } catch (ApiException exception) {
            return ResponseEntity.status(401)
                    .header(HttpHeaders.SET_COOKIE, cookieService.clear().toString())
                    .body(ApiResult.error(401, exception.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResult<Void>> logout(HttpServletRequest request) {
        cookieService.read(request).ifPresent(authService::logout);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieService.clear().toString())
                .body(ApiResult.success());
    }

    @GetMapping("/me")
    public ApiResult<AuthView> me() {
        return ApiResult.success(authService.current(SecurityUtils.currentUser()));
    }

    private ResponseEntity<ApiResult<AuthResponse>> withRefreshCookie(AuthSession session) {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieService.create(session.refreshToken()).toString())
                .body(ApiResult.success(session.response()));
    }
}



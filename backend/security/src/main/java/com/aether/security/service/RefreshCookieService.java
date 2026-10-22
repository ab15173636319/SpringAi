package com.aether.security.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;

import java.util.Optional;

public interface RefreshCookieService {
    ResponseCookie create(String token);

    ResponseCookie clear();

    Optional<String> read(HttpServletRequest request);
}


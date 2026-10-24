package com.aether.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.List;

@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(Jwt jwt, Cookie cookie, Cors cors) {
    public record Jwt(String issuer, String secret, Duration accessTtl, Duration refreshTtl) {
    }

    public record Cookie(String name, boolean secure, String sameSite, String path) {
    }

    public record Cors(List<String> allowedOrigins) {
    }
}




package com.aether.security.pojo.vto;

public record AuthResponse(String accessToken, long expiresIn, AuthView user) {
}




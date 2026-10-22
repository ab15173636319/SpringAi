package com.aether.security.pojo.dto;

import com.aether.security.pojo.vto.AuthResponse;

public record AuthSession(AuthResponse response, String refreshToken) {
}




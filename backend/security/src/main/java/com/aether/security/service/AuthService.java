package com.aether.security.service;

import com.aether.security.pojo.dto.AuthSession;
import com.aether.security.pojo.dto.LoginRequest;
import com.aether.security.pojo.dto.RegisterRequest;
import com.aether.security.pojo.entity.AuthenticatedUser;
import com.aether.security.pojo.vto.AuthView;

public interface AuthService {
    AuthSession register(RegisterRequest request);

    AuthSession login(LoginRequest request);

    AuthSession refresh(String refreshToken);

    void logout(String refreshToken);

    AuthView current(AuthenticatedUser principal);
}


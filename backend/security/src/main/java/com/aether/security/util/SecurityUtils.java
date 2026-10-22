package com.aether.security.util;

import com.aether.common.error.ApiException;
import com.aether.security.pojo.entity.AuthenticatedUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {
    private SecurityUtils() {
    }

    public static AuthenticatedUser currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            throw ApiException.unauthorized("登录状态已失效，请重新登录");
        }
        return user;
    }

    public static String currentUserId() {
        return currentUser().id();
    }
}




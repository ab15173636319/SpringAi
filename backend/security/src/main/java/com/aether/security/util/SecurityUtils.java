package com.aether.security.util;

import com.aether.common.error.BusinessException;
import com.aether.common.result.ResponseCode;
import com.aether.security.pojo.entity.AuthenticatedUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {
    private SecurityUtils() {
    }

    public static AuthenticatedUser currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            throw new BusinessException(
                    ResponseCode.UNAUTHORIZED,
                    "登录状态已失效，请重新登录"
            );
        }
        return user;
    }

    public static String currentUserId() {
        return currentUser().id();
    }
}




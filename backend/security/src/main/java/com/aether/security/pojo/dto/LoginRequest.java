package com.aether.security.pojo.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "请输入用户名或邮箱") String identifier,
        @NotBlank(message = "请输入密码") String password
) {
}




package com.aether.security.pojo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "请输入用户名")
        @Size(min = 3, max = 24, message = "用户名长度应为 3 到 24 个字符")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
        String username,

        @NotBlank(message = "请输入显示名称")
        @Size(max = 32, message = "显示名称不能超过 32 个字符")
        String displayName,

        @Email(message = "请输入有效的邮箱地址")
        @NotBlank(message = "请输入邮箱地址")
        String email,

        @NotBlank(message = "请输入密码")
        @Size(min = 8, max = 72, message = "密码长度应为 8 到 72 个字符")
        String password
) {
}




package com.aether.chat.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateConversationRequest(
        @NotBlank(message = "会话标题不能为空")
        @Size(max = 80, message = "会话标题不能超过 80 个字符")
        String title
) {
}




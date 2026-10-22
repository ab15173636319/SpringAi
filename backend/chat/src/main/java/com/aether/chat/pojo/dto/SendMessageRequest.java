package com.aether.chat.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SendMessageRequest(
        @NotBlank(message = "请输入消息")
        @Size(max = 12000, message = "单条消息不能超过 12000 个字符")
        String message
) {
}




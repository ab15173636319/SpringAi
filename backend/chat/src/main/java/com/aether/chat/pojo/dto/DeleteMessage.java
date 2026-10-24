package com.aether.chat.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record DeleteMessage(
    @NotBlank(message = "对话id不能为空")
    String conversationId,

    @NotEmpty(message = "消息id列表不能为空")
    List<@NotBlank(message = "消息id不能为空") String> messageIds
) {
}

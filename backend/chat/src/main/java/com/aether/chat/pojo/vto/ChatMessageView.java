package com.aether.chat.pojo.vto;

import com.aether.chat.pojo.entity.MessageRole;

import java.time.Instant;

public record ChatMessageView(
        String id,
        String conversationId,
        MessageRole role,
        String content,
        Instant createdAt
) {
}




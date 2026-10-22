package com.aether.chat.pojo.vto;

import java.time.Instant;

public record ConversationView(
        String id,
        String title,
        String model,
        boolean pinned,
        Instant createdAt,
        Instant updatedAt
) {
}




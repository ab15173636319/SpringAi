package com.aether.chat.pojo.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@Document(collection = "chat_messages")
@CompoundIndex(name = "message_conversation_time", def = "{'conversationId': 1, 'createdAt': 1}")
public class ChatMessage {
    @Id
    private String id;
    private String conversationId;
    private String userId;
    private MessageRole role;
    private String content;
    private Instant createdAt;
}




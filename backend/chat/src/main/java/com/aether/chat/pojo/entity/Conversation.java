package com.aether.chat.pojo.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@Document(collection = "conversations")
@CompoundIndex(name = "conversation_owner_order", def = "{'userId': 1, 'pinned': -1, 'updatedAt': -1}")
public class Conversation {
    @Id
    private String id;
    private String userId;
    private String title;
    private String model;
    private boolean pinned;
    private Instant createdAt;
    private Instant updatedAt;
}




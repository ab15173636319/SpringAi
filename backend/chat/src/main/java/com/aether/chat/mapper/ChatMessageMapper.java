package com.aether.chat.mapper;

import com.aether.chat.pojo.entity.ChatMessage;
import com.aether.chat.pojo.vto.ChatMessageView;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageMapper extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findAllByConversationIdAndUserIdOrderByCreatedAtAsc(String conversationId, String userId);

    void deleteAllByConversationIdAndUserId(String conversationId, String userId);

    default ChatMessageView toView(ChatMessage message) {
        return new ChatMessageView(
                message.getId(),
                message.getConversationId(),
                message.getRole(),
                message.getContent(),
                message.getCreatedAt()
        );
    }

    void deleteAllByIdAndConversationIdAndUserId(String id, String conversationId, String userId);
}




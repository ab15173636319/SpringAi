package com.aether.chat.mapper;

import com.aether.chat.pojo.entity.Conversation;
import com.aether.chat.pojo.vto.ConversationView;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationMapper extends MongoRepository<Conversation, String> {
    List<Conversation> findAllByUserIdOrderByPinnedDescUpdatedAtDesc(String userId);

    Optional<Conversation> findByIdAndUserId(String id, String userId);

    long countByUserId(String userId);

    default ConversationView toView(Conversation conversation) {
        return new ConversationView(
                conversation.getId(),
                conversation.getTitle(),
                conversation.getModel(),
                conversation.isPinned(),
                conversation.getCreatedAt(),
                conversation.getUpdatedAt()
        );
    }
}




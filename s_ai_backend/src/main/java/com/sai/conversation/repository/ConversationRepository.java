package com.sai.conversation.repository;

import com.sai.conversation.entity.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface ConversationRepository extends MongoRepository<Conversation, String> { }

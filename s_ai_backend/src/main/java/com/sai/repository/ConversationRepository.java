package com.sai.repository;

import com.sai.entity.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface ConversationRepository extends MongoRepository<Conversation, String> { }

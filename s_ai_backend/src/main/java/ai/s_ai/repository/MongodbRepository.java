package ai.s_ai.repository;

import ai.s_ai.entity.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongodbRepository extends MongoRepository<Conversation, String> { }

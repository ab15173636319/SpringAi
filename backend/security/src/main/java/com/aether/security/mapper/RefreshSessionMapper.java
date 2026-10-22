package com.aether.security.mapper;

import com.aether.security.pojo.entity.RefreshSession;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RefreshSessionMapper extends MongoRepository<RefreshSession, String> {
}




package com.aether.security.service.impl;

import com.aether.security.mapper.RefreshSessionMapper;
import com.aether.security.pojo.entity.RefreshSession;
import com.aether.security.service.RefreshSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefreshSessionServiceImpl implements RefreshSessionService {
    private final RefreshSessionMapper repository;
    private final MongoTemplate mongoTemplate;

    @Override
    public void create(RefreshSession session) {
        repository.insert(session);
    }

    @Override
    public boolean rotate(String sessionId, String currentHash, String nextHash, Instant expiresAt) {
        Query query = Query.query(Criteria.where("_id").is(sessionId)
                .and("tokenHash").is(currentHash)
                .and("revoked").is(false));
        Update update = new Update()
                .set("tokenHash", nextHash)
                .set("lastUsedAt", Instant.now())
                .set("expiresAt", expiresAt);
        return mongoTemplate.updateFirst(query, update, RefreshSession.class).getModifiedCount() == 1;
    }

    @Override
    public void revoke(String sessionId) {
        mongoTemplate.updateFirst(
                Query.query(Criteria.where("_id").is(sessionId)),
                new Update().set("revoked", true),
                RefreshSession.class
        );
    }
}




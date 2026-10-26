package com.aether.chat.service.impl;

import com.aether.chat.config.AiProperties;
import com.aether.chat.mapper.DailyUsageMapper;
import com.aether.chat.pojo.entity.DailyUsage;
import com.aether.chat.pojo.vto.UsageView;
import com.aether.chat.service.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * 用量服务实现：按「用户 + 日期」累计对话次数，基于 MongoDB 原子自增控制并发。
 */
@Service
@RequiredArgsConstructor
public class UsageServiceImpl implements UsageService {
    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");

    private final DailyUsageMapper repository;
    private final MongoTemplate mongoTemplate;
    private final AiProperties properties;

    /**
     * 尝试为当天占用一次额度。
     * <p>先以「count 小于上限」为条件做原子自增；未命中说明当天记录不存在或已达上限：
     * 记录已存在则直接判定超限，否则插入初始记录（并发插入冲突也判定为超限）。</p>
     *
     * @param userId 用户 ID
     * @return 抢占成功返回 {@code true}，额度用尽返回 {@code false}
     */
    @Override
    public boolean tryConsume(String userId) {
        String day = LocalDate.now(ZONE).toString();
        Query query = Query.query(Criteria.where("userId").is(userId)
                .and("day").is(day)
                .and("count").lt(properties.dailyLimit()));
        boolean incremented = mongoTemplate.updateFirst(
                query,
                new Update().inc("count", 1),
                DailyUsage.class
        ).getModifiedCount() == 1;
        if (incremented) {
            return true;
        }

        if (repository.findByUserIdAndDay(userId, day).isPresent()) {
            return false;
        }
        try {
            repository.insert(DailyUsage.builder().userId(userId).day(day).count(1).build());
            return true;
        } catch (DuplicateKeyException ignored) {
            return false;
        }
    }

    /**
     * 查询当天已用次数与上限，当天无记录时按 0 次返回。
     *
     * @param userId 用户 ID
     * @return 用量视图
     */
    @Override
    public UsageView current(String userId) {
        String day = LocalDate.now(ZONE).toString();
        DailyUsage usage = repository.findByUserIdAndDay(userId, day).orElse(null);
        return repository.toView(usage, properties.dailyLimit(), day);
    }
}

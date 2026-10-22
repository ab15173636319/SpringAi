package com.aether.chat.mapper;

import com.aether.chat.pojo.entity.DailyUsage;
import com.aether.chat.pojo.vto.UsageView;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DailyUsageMapper extends MongoRepository<DailyUsage, String> {
    Optional<DailyUsage> findByUserIdAndDay(String userId, String day);

    default UsageView toView(DailyUsage usage, int limit, String day) {
        return new UsageView(
                usage == null ? 0 : usage.getCount(),
                limit,
                day
        );
    }
}




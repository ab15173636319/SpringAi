package com.sai.chat;

import com.sai.device.DeviceCallLimitProperties;
import com.sai.redis.RedisClient;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final DeviceCallLimitProperties deviceCallLimitProperties;
    private final MongoTemplate mongoTemplate;

    private final ChatClient chatClient;
    private final RedisClient redisClient;

    private final ChatMemory chatMemory;

    /**
     * 获取
     */
    public Flux<String> chat(String id, String message, HttpServletRequest request) {
        String deviceId = request.getHeader("deviceId");
        String countKey = deviceCallLimitProperties.getDeviceCallCountPrefix() + deviceId;

        long callCount = redisClient.incrementAndGet(countKey, deviceCallLimitProperties.getCallCountExpireDay(),
                TimeUnit.DAYS);

        if (callCount > deviceCallLimitProperties.getMaxCallCount()) {
            return Flux.just("已超出最大访问次数");
        }

        return chatClient
                .prompt(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, id))
                .stream()
                .content();
    }

    public List<Chat> getChats(String conversationId) {
        if (conversationId == null) {
            throw new IllegalArgumentException("会话 ID不能为空");
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("conversationId").is(conversationId))
                .with(Sort.by(Sort.Direction.ASC, "timestamp"));

        return mongoTemplate.find(query, Chat.class);
    }

}

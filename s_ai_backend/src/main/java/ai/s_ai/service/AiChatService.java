package ai.s_ai.service;

import ai.s_ai.components.DeviceCallCountProperties;
import ai.s_ai.entity.Chat;
import ai.s_ai.utils.RedisUtil;
import ai.s_ai.utils.ResultUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
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
public class AiChatService {

    @Resource
    private final DeviceCallCountProperties deviceCallCountProperties;
    @Resource
    private MongoTemplate mongoTemplate;

    private final ChatClient chatClient;
    private final RedisUtil redisUtil;

    private final ChatMemory chatMemory;

    /**
     * 获取
     */
    public Flux<String> chat(String id, String message, HttpServletRequest request) {
        String deviceId = request.getHeader("deviceId");
        String countKey = deviceCallCountProperties.getDeviceCallCountPrefix() + deviceId;

        long callCount = redisUtil.incrementAndGet(countKey, deviceCallCountProperties.getCallCountExpireDay(),
                TimeUnit.DAYS);

        if (callCount > deviceCallCountProperties.getMaxCallCount()) {
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

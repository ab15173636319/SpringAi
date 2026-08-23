package com.sai.chat.service;

import com.sai.device.config.DeviceCallLimitProperties;
import com.sai.common.exception.BusinessException;
import com.sai.conversation.entity.Conversation;
import com.sai.conversation.repository.ConversationRepository;
import com.sai.redis.util.RedisClient;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatMetaService {

    private final DeviceCallLimitProperties deviceCallLimitProperties;
    private final RedisClient redisClient;
    private final ChatClient setTitle;
    private final ConversationRepository conversationRepository;

    public Map<String, Object> getCurrentDeviceAmount(HttpServletRequest request) {
        String deviceId = request.getHeader("deviceId");
        String countKey = deviceCallLimitProperties.getDeviceCallCountPrefix() + deviceId;
        int currentAmount = redisClient.getInt(countKey);
        int maxCallCount = deviceCallLimitProperties.getMaxCallCount();
        Map<String, Object> map = new HashMap<>();
        map.put("amount", currentAmount);
        map.put("max", maxCallCount);
        return map;
    }

    public void getTitle(String content, String conversationId) {
        String title = setTitle.prompt(content).call().content();
        Conversation conversation = conversationRepository.findById(conversationId).orElseThrow(() -> new BusinessException("对话不存在"));
        //  只有标题为空或为默认标题时才修改
        if (conversation.getTitle() == null || conversation.getTitle().isEmpty() || conversation.getTitle().equals("新对话")) {
            conversation.setTitle(title);
            conversation.setUpdateTime(LocalDateTime.now());
            conversationRepository.save(conversation);
        }
    }

}

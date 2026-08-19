package ai.s_ai.service;

import ai.s_ai.components.DeviceCallCountProperties;
import ai.s_ai.configuration.GlobalException;
import ai.s_ai.entity.Conversation;
import ai.s_ai.repository.MongodbRepository;
import ai.s_ai.utils.RedisUtil;
import ai.s_ai.utils.ResultUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiChatAboutService {

    private final DeviceCallCountProperties deviceCallCountProperties;
    private final RedisUtil redisUtil;
    private final ChatClient setTitle;
    private final MongodbRepository mongodbRepository;

    public Map<String, Object> getCurrentDeviceAmount(HttpServletRequest request) {
        String deviceId = request.getHeader("deviceId");
        String countKey = deviceCallCountProperties.getDeviceCallCountPrefix() + deviceId;
        int currentAmount = redisUtil.get(countKey) != null ? Integer.parseInt(redisUtil.get(countKey)) : 0;
        int maxCallCount = (int) deviceCallCountProperties.getMaxCallCount();
        Map<String, Object> map = new HashMap<>();
        map.put("amount", currentAmount);
        map.put("max", maxCallCount);
        return map;
    }

    public void getTitle(String content, String conversationId) {
        String title = setTitle.prompt(content).call().content();
        Conversation conversation = mongodbRepository.findById(conversationId).orElseThrow(() -> new GlobalException.BusinessException("对话不存在"));
        //  只有标题为空或为默认标题时才修改
        if (conversation.getTitle() == null || conversation.getTitle().isEmpty() || conversation.getTitle().equals("新对话")) {
            conversation.setTitle(title);
            conversation.setUpdateTime(LocalDateTime.now());
            mongodbRepository.save(conversation);
        }
    }

}

package ai.s_ai.service;

import ai.s_ai.components.DeviceCallCountProperties;
import ai.s_ai.utils.RedisUtil;
import ai.s_ai.utils.ResultUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiChatAboutService {

    private final DeviceCallCountProperties deviceCallCountProperties;
    private final RedisUtil redisUtil;

    public ResultUtil<Map<String, Object>> getCurrentDeviceAmount(HttpServletRequest request) {
        String deviceId = request.getHeader("deviceId");
        String countKey = deviceCallCountProperties.getDeviceCallCountPrefix() + deviceId;
        int currentAmount = redisUtil.get(countKey) != null ? Integer.parseInt(redisUtil.get(countKey)) : 0;
        int maxCallCount = (int) deviceCallCountProperties.getMaxCallCount();
        Map<String, Object> map = new HashMap<>();
        map.put("amount", currentAmount);
        map.put("max", maxCallCount);
        return ResultUtil.success(map);
    }

}

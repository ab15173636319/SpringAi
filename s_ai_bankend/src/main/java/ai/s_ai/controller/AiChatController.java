package ai.s_ai.controller;

import ai.s_ai.entity.Device;
import ai.s_ai.utils.RedisUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@Tag(name = "AI 聊天接口", description = "提供 AI 对话聊天、历史记录查询、设备调用统计等功能")
public class AiChatController {
    private static final String DEVICE_CALL_COUNT_PREFIX = "device:call_count:";
    private static final long CALL_COUNT_EXPIRE_DAYS = 1L;
    private static final long MAX_CALL_COUNT = 5L;
    private final ChatClient chatClient;
    private final RedisUtil redisUtil;

    @GetMapping(value = "/chat/{id}", produces = "text/plain;charset=UTF-8")
    @Operation(summary = "AI 流式对话", description = "与 AI 进行流式对话，基于会话 ID 保持上下文记忆，返回流式响应")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "对话成功，返回流式文本响应"),
            @ApiResponse(responseCode = "400", description = "非法访问，缺少 deviceId 请求头")
    })
    public Flux<String> chat(
            @Parameter(description = "会话 ID，用于区分不同对话上下文", required = true, example = "session-123456")
            @PathVariable String id,
            @Parameter(description = "用户发送的消息内容", required = true, example = "你好，请介绍一下自己")
            @RequestParam String message,
            HttpServletRequest request) {
        String deviceId = request.getHeader("deviceId");
        if (deviceId == null || deviceId.isBlank()) {
            return Flux.just("非法访问！");
        }

        String countKey = DEVICE_CALL_COUNT_PREFIX + deviceId;
        // 自增并保存一天
        long callCount = redisUtil.incrementAndGet(countKey, CALL_COUNT_EXPIRE_DAYS, TimeUnit.DAYS);

        System.out.println("当前设备调用次数: " + callCount);

        if (callCount > MAX_CALL_COUNT) {
            return Flux.just("已超出最大访问次数");
        }

        // 构建设备对象
        Device device = Device.builder()
                .deviceId(deviceId)
                .amount(callCount)
                .build();

        return chatClient
                .prompt(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, id))
                .stream()
                .content();
    }

    @GetMapping(value = "/currentHistory/{id}", produces = "text/plain;charset=UTF-8")
    @Operation(summary = "获取当前会话历史", description = "根据会话 ID 查询当前对话的历史消息记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功，返回历史消息列表")
    })
    public List<String> currentHistory(
            @Parameter(description = "会话 ID", required = true, example = "session-123456")
            @PathVariable String id,
            @Parameter(description = "消息内容（预留参数）", example = "")
            @RequestParam String message) {
        //  return chatClient.currentHistory(id);
        return List.of();
    }

    @GetMapping("/getCurrentDeviceAmount")
    @Operation(summary = "获取当前设备调用次数", description = "查询当前设备在 24 小时内调用 AI 接口的累计次数，次数统计周期为 1 天")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功，返回调用次数（未找到设备时返回 0）")
    })
    public Map<String, Object> getCurrentDeviceAmount(
            @Parameter(description = "HTTP 请求（需在请求头中携带 deviceId）", required = true)
            HttpServletRequest request) {
        String deviceId = request.getHeader("deviceId");
        Map<String, Object> map = new HashMap<>();
        if (deviceId == null || deviceId.isBlank()) {
            map.put("amount", "非法访问");
            return map;
        }
        String countKey = DEVICE_CALL_COUNT_PREFIX + deviceId;
        int currentAmount = redisUtil.get(countKey) != null ? Integer.parseInt(redisUtil.get(countKey)) : 0;
        int maxCallCount = (int) MAX_CALL_COUNT;
        map.put("amount", currentAmount);
        map.put("max", maxCallCount);
        return map;
    }

}
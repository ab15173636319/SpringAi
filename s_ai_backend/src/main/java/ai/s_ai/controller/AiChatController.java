package ai.s_ai.controller;

import ai.s_ai.entity.Chat;
import ai.s_ai.entity.Device;
import ai.s_ai.service.AiChatAboutService;
import ai.s_ai.service.AiChatService;
import ai.s_ai.utils.RedisUtil;
import ai.s_ai.utils.ResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
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
    @Resource
    private final AiChatService aiChatService;
    @Resource
    private final AiChatAboutService aiChatAboutService;

    @GetMapping(value = "/chat/{id}", produces = "text/plain;charset=UTF-8")
    @Operation(summary = "AI 流式对话", description = "与 AI 进行流式对话，基于会话 ID 保持上下文记忆，返回流式响应")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "对话成功，返回流式文本响应"),
            @ApiResponse(responseCode = "400", description = "非法访问，缺少 deviceId 请求头")
    })
    public Flux<String> chat(
            @Parameter(description = "会话 ID，用于区分不同对话上下文", required = true, example = "session-123456") @PathVariable String id,
            @Parameter(description = "用户发送的消息内容", required = true, example = "你好，请介绍一下自己") @RequestParam String message,
            HttpServletRequest request) {

        return aiChatService.chat(id, message, request);

    }

    @GetMapping(value = "/currentHistory/{id}")
    @Operation(summary = "获取当前会话历史", description = "根据会话 ID 查询当前对话的历史消息记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功，返回历史消息列表")
    })
    public ResultUtil<List<Message>> currentHistory(
            @Parameter(description = "会话 ID", required = true, example = "session-123456") @PathVariable String id
    ) {
        return aiChatService.getChats(id);
    }

    @GetMapping("/getCurrentDeviceAmount")
    @Operation(summary = "获取当前设备调用次数", description = "查询当前设备在 24 小时内调用 AI 接口的累计次数，次数统计周期为 1 天")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功，返回调用次数（未找到设备时返回 0）")
    })
    public ResultUtil<Map<String, Object>> getCurrentDeviceAmount(
            @Parameter(description = "HTTP 请求（需在请求头中携带 deviceId）", required = true) HttpServletRequest request) {
        return aiChatAboutService.getCurrentDeviceAmount(request);
    }

}
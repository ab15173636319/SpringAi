package com.sai.chat.controller;

import com.sai.chat.entity.Chat;
import com.sai.chat.service.ChatMetaService;
import com.sai.chat.service.ChatService;
import com.sai.web.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@Tag(name = "AI 聊天接口", description = "提供 AI 对话聊天、历史记录查询、设备调用统计等功能")
public class ChatController {
        private final ChatService chatService;
        private final ChatMetaService chatMetaService;

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
                chatMetaService.getTitle(message, id);
            return chatService.chat(id, message, request);
        }

        @GetMapping(value = "/currentHistory/{id}")
        @Operation(summary = "获取当前会话历史", description = "根据会话 ID 查询当前对话的历史消息记录")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "查询成功，返回历史消息列表")
        })
        public Result<List<Chat>> currentHistory(
                        @Parameter(description = "会话 ID", required = true, example = "session-123456") @PathVariable String id) {
                return Result.success(chatService.getChats(id));
        }

        @GetMapping("/getCurrentDeviceAmount")
        @Operation(summary = "获取当前设备调用次数", description = "查询当前设备在 24 小时内调用 AI 接口的累计次数，次数统计周期为 1 天")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "查询成功，返回调用次数（未找到设备时返回 0）")
        })
        public Result<Map<String, Object>> getCurrentDeviceAmount(
                        @Parameter(description = "HTTP 请求（需在请求头中携带 deviceId）", required = true) HttpServletRequest request) {
                return Result.success(chatMetaService.getCurrentDeviceAmount(request));
        }

}

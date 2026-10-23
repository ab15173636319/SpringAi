package com.aether.chat.controller;

import com.aether.chat.pojo.dto.DeleteMessage;
import com.aether.chat.pojo.dto.SendMessageRequest;
import com.aether.chat.service.ChatHistoryService;
import com.aether.chat.service.ChatService;
import com.aether.common.result.Result;
import com.aether.security.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chatService;
    private final ChatHistoryService chatHistoryService;


    @PostMapping(value = "/{conversationId}", produces = "text/plain;charset=UTF-8")
    public Flux<String> stream(
            @PathVariable String conversationId,
            @Valid @RequestBody SendMessageRequest request
    ) {
        return chatService.stream(
                SecurityUtils.currentUserId(),
                conversationId,
                request.message().trim()
        );
    }

    @PostMapping("/delete")
    public Result<?> delete(@Valid @RequestBody DeleteMessage deleteMessage) {
        log.info("删除消息: {}", deleteMessage.getMessageIds().toString());
        chatHistoryService.delete(SecurityUtils.currentUserId(), deleteMessage);
        return Result.success();
    }
}




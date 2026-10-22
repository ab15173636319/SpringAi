package com.aether.chat.controller;

import com.aether.chat.pojo.vto.ChatMessageView;
import com.aether.chat.service.ChatHistoryService;
import com.aether.common.pojo.vto.ApiResult;
import com.aether.security.util.SecurityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ChatHistoryController {
    private final ChatHistoryService historyService;

    public ChatHistoryController(ChatHistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping("/{id}/messages")
    public ApiResult<List<ChatMessageView>> list(@PathVariable String id) {
        return ApiResult.success(historyService.list(SecurityUtils.currentUserId(), id));
    }
}




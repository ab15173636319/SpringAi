package com.aether.chat.controller;

import com.aether.chat.pojo.vto.ChatMessageView;
import com.aether.chat.service.ChatHistoryService;
import com.aether.common.result.Result;
import com.aether.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/conversations")
public class ChatHistoryController {
    private final ChatHistoryService historyService;

    @GetMapping("/{id}/messages")
    public Result<List<ChatMessageView>> list(@PathVariable String id) {
        return Result.success(historyService.list(SecurityUtils.currentUserId(), id));
    }
}




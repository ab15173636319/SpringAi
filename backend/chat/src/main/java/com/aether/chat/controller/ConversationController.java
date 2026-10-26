package com.aether.chat.controller;

import com.aether.chat.pojo.dto.CreateConversationRequest;
import com.aether.chat.pojo.dto.UpdateConversationRequest;
import com.aether.chat.pojo.vto.ConversationView;
import com.aether.chat.service.ConversationService;
import com.aether.common.result.Result;
import com.aether.security.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/conversations")
public class ConversationController {
    private final ConversationService service;

    @GetMapping
    public Result<List<ConversationView>> list() {
        return Result.success(service.list(SecurityUtils.currentUserId()));
    }

    @PostMapping
    public Result<ConversationView> create(
            @Valid @RequestBody(required = false) CreateConversationRequest request
    ) {
        CreateConversationRequest safeRequest = request == null
                ? new CreateConversationRequest(null)
                : request;
        return Result.success(service.create(SecurityUtils.currentUserId(), safeRequest));
    }

    @PatchMapping("/{id}")
    public Result<ConversationView> rename(
            @PathVariable String id,
            @Valid @RequestBody UpdateConversationRequest request
    ) {
        return Result.success(service.rename(SecurityUtils.currentUserId(), id, request));
    }

    @PatchMapping("/{id}/pin")
    public Result<ConversationView> togglePin(@PathVariable String id) {
        return Result.success(service.togglePin(SecurityUtils.currentUserId(), id));
    }

    @PostMapping("/{id}/clear")
    public Result<Void> clear(@PathVariable String id) {
        service.clearMessages(SecurityUtils.currentUserId(), id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable String id) {
        service.delete(SecurityUtils.currentUserId(), id);
        return Result.success();
    }
}




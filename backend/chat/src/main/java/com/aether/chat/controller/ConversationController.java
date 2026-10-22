package com.aether.chat.controller;

import com.aether.chat.pojo.dto.CreateConversationRequest;
import com.aether.chat.pojo.dto.UpdateConversationRequest;
import com.aether.chat.pojo.vto.ConversationView;
import com.aether.chat.service.ConversationService;
import com.aether.common.pojo.vto.ApiResult;
import com.aether.security.util.SecurityUtils;
import jakarta.validation.Valid;
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
@RequestMapping("/api/conversations")
public class ConversationController {
    private final ConversationService service;

    public ConversationController(ConversationService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResult<List<ConversationView>> list() {
        return ApiResult.success(service.list(SecurityUtils.currentUserId()));
    }

    @PostMapping
    public ApiResult<ConversationView> create(@Valid @RequestBody(required = false) CreateConversationRequest request) {
        CreateConversationRequest safeRequest = request == null
                ? new CreateConversationRequest(null)
                : request;
        return ApiResult.success(service.create(SecurityUtils.currentUserId(), safeRequest));
    }

    @PatchMapping("/{id}")
    public ApiResult<ConversationView> rename(
            @PathVariable String id,
            @Valid @RequestBody UpdateConversationRequest request
    ) {
        return ApiResult.success(service.rename(SecurityUtils.currentUserId(), id, request));
    }

    @PatchMapping("/{id}/pin")
    public ApiResult<ConversationView> togglePin(@PathVariable String id) {
        return ApiResult.success(service.togglePin(SecurityUtils.currentUserId(), id));
    }

    @PostMapping("/{id}/clear")
    public ApiResult<Void> clear(@PathVariable String id) {
        service.clearMessages(SecurityUtils.currentUserId(), id);
        return ApiResult.success();
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable String id) {
        service.delete(SecurityUtils.currentUserId(), id);
        return ApiResult.success();
    }
}




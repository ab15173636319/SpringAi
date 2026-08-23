package com.sai.controller;

import com.sai.entity.Conversation;
import com.sai.entity.dto.ConversationDto;
import com.sai.service.ConversationService;
import com.sai.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conversation")
@RequiredArgsConstructor
@Tag(name = "会话管理接口", description = "提供 AI 对话会话的创建、列表查询等管理功能")
public class ConversationController {

    private final ConversationService conversationService;

    @PostMapping("/create")
    @Operation(summary = "创建新会话", description = "创建一个新的 AI 对话会话，返回包含会话 ID 等信息的会话对象")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "会话创建成功，返回新创建的会话对象")
    })
    public Result<Conversation> createConversation() {
        return Result.success(conversationService.createConversation());
    }

    @GetMapping("/list")
    @Operation(summary = "获取会话列表", description = "查询所有已创建的 AI 对话会话列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功，返回会话列表")
    })
    public Result<List<Conversation>> listConversation() {
        return Result.success(conversationService.getConversationList());
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除会话", description = "根据指定 ID 删除 AI 对话会话")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功")
    })
    public Result<Void> deleteConversation(@PathVariable String id) {
        conversationService.deleteConversation(id);
        return Result.success();
    }

    @PostMapping("/modify")
    @Operation(summary = "修改会话", description = "修改指定 ID 的 AI 对话会话的标题")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "修改成功，返回修改后的会话对象")
    })
    public Result<Conversation> modifyConversation(@RequestBody ConversationDto conversationDto) {
        return Result.success(conversationService.modifyConversation(conversationDto));
    }

    // 置顶会话
    @PostMapping("/top/{id}")
    @Operation(summary = "置顶会话", description = "置顶指定 ID 的 AI 对话会话")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "置顶成功，返回置顶后的会话对象")
    })
    public Result<Conversation> topConversation(@PathVariable String id) {
        return Result.success(conversationService.topConversation(id));
    }

}
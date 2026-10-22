package com.aether.chat.service.impl;

import com.aether.chat.mapper.ChatMessageMapper;
import com.aether.chat.pojo.dto.DeleteMessage;
import com.aether.chat.pojo.vto.ChatMessageView;
import com.aether.chat.service.ChatHistoryService;
import com.aether.chat.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 聊天记录服务实现：先校验会话归属，再读取或删除消息。
 */
@RequiredArgsConstructor
@Service
public class ChatHistoryServiceImpl implements ChatHistoryService {
    private final ChatMessageMapper repository;
    private final ConversationService conversationService;

    /**
     * 查询会话的全部消息，按创建时间升序返回。
     *
     * @param userId         用户 ID
     * @param conversationId 会话 ID
     * @return 消息视图列表
     */
    public List<ChatMessageView> list(String userId, String conversationId) {
        conversationService.requireOwned(userId, conversationId);
        return repository.findAllByConversationIdAndUserIdOrderByCreatedAtAsc(conversationId, userId)
                .stream()
                .map(repository::toView)
                .toList();
    }

    /**
     * 按 ID 删除会话内的指定消息（会话本身保留）。
     * <p>删除条件同时包含消息 ID、会话 ID 与用户 ID，避免误删或越权删除其他数据。</p>
     *
     * @param userId        当前登录用户 ID
     * @param deleteMessage 删除参数，包含会话 ID 与待删除的消息 ID 列表
     */
    @Override
    public void delete(String userId, DeleteMessage deleteMessage) {
        conversationService.requireOwned(userId, deleteMessage.getConversationId());
        deleteMessage.getMessageIds().forEach(id -> {
            repository.deleteAllByIdAndConversationIdAndUserId(id, deleteMessage.getConversationId(), userId);
        });
        conversationService.touch(userId, deleteMessage.getConversationId());
    }
}

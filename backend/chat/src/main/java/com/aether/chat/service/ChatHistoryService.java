package com.aether.chat.service;

import com.aether.chat.pojo.dto.DeleteMessage;
import com.aether.chat.pojo.vto.ChatMessageView;

import java.util.List;

/**
 * 聊天记录服务：查询与删除会话内的历史消息。
 */
public interface ChatHistoryService {
    /**
     * 查询指定会话的全部消息，按创建时间升序返回。
     *
     * @param userId         用户 ID
     * @param conversationId 会话 ID
     * @return 消息视图列表，会话不属于该用户时抛出异常
     */
    List<ChatMessageView> list(String userId, String conversationId);

    /**
     * 按 ID 删除指定会话中的消息（会话本身保留）。
     *
     * @param userId        当前登录用户 ID
     * @param deleteMessage 删除参数，包含会话 ID 与待删除的消息 ID 列表
     */
    void delete(String userId, DeleteMessage deleteMessage);
}

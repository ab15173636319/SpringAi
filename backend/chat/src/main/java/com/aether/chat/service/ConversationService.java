package com.aether.chat.service;

import com.aether.chat.pojo.dto.CreateConversationRequest;
import com.aether.chat.pojo.dto.UpdateConversationRequest;
import com.aether.chat.pojo.entity.Conversation;
import com.aether.chat.pojo.vto.ConversationView;

import java.util.List;

/**
 * 会话服务：会话的增删改查、置顶、清空消息，以及标题与更新时间维护。
 */
public interface ConversationService {
    /**
     * 查询当前用户的会话列表，置顶优先，其次按更新时间倒序。
     *
     * @param userId 用户 ID
     * @return 会话视图列表
     */
    List<ConversationView> list(String userId);

    /**
     * 新建会话，标题为空时使用默认标题「新对话」。
     *
     * @param userId  用户 ID
     * @param request 创建请求，可携带标题
     * @return 新建后的会话视图
     */
    ConversationView create(String userId, CreateConversationRequest request);

    /**
     * 重命名会话并刷新更新时间。
     *
     * @param userId  用户 ID
     * @param id      会话 ID
     * @param request 更新请求，title 为新标题
     * @return 更新后的会话视图
     */
    ConversationView rename(String userId, String id, UpdateConversationRequest request);

    /**
     * 切换会话的置顶状态。
     *
     * @param userId 用户 ID
     * @param id     会话 ID
     * @return 更新后的会话视图
     */
    ConversationView togglePin(String userId, String id);

    /**
     * 删除会话，并同时清理该会话下的全部消息。
     *
     * @param userId 用户 ID
     * @param id     会话 ID
     */
    void delete(String userId, String id);

    /**
     * 清空会话消息，并把标题重置为「新对话」。
     *
     * @param userId 用户 ID
     * @param id     会话 ID
     */
    void clearMessages(String userId, String id);

    /**
     * 校验会话存在且属于该用户，否则抛出 404 业务异常。
     *
     * @param userId 用户 ID
     * @param id     会话 ID
     * @return 通过校验的会话实体
     */
    Conversation requireOwned(String userId, String id);

    /**
     * 若会话仍是默认标题，则用首条用户消息生成标题（超过 24 个字符时截断并追加省略号）。
     *
     * @param userId       用户 ID
     * @param id           会话 ID
     * @param firstMessage 首条用户消息内容
     */
    void markTitleFromFirstMessage(String userId, String id, String firstMessage);

    /**
     * 刷新会话的更新时间，使其在列表中前置。
     *
     * @param userId 用户 ID
     * @param id     会话 ID
     */
    void touch(String userId, String id);
}

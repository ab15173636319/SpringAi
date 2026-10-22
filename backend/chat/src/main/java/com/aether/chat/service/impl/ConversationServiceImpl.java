package com.aether.chat.service.impl;

import com.aether.chat.config.AiProperties;
import com.aether.chat.mapper.ChatMessageMapper;
import com.aether.chat.mapper.ConversationMapper;
import com.aether.chat.pojo.dto.CreateConversationRequest;
import com.aether.chat.pojo.dto.UpdateConversationRequest;
import com.aether.chat.pojo.entity.Conversation;
import com.aether.chat.pojo.vto.ConversationView;
import com.aether.chat.service.ConversationService;
import com.aether.common.error.ApiException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;

/**
 * 会话服务实现：基于 {@link ConversationMapper} 与 {@link ChatMessageMapper} 维护会话及其消息。
 */
@Service
public class ConversationServiceImpl implements ConversationService {
    private final ConversationMapper repository;
    private final ChatMessageMapper messageRepository;
    private final AiProperties aiProperties;

    public ConversationServiceImpl(
            ConversationMapper repository,
            ChatMessageMapper messageRepository,
            AiProperties aiProperties
    ) {
        this.repository = repository;
        this.messageRepository = messageRepository;
        this.aiProperties = aiProperties;
    }

    /**
     * 查询用户的会话列表，置顶优先，其次按更新时间倒序。
     *
     * @param userId 用户 ID
     * @return 会话视图列表
     */
    public List<ConversationView> list(String userId) {
        return repository.findAllByUserIdOrderByPinnedDescUpdatedAtDesc(userId)
                .stream()
                .map(repository::toView)
                .toList();
    }

    /**
     * 新建会话，标题为空时使用默认标题「新对话」，模型名取自配置。
     *
     * @param userId  用户 ID
     * @param request 创建请求
     * @return 新建后的会话视图
     */
    public ConversationView create(String userId, CreateConversationRequest request) {
        Instant now = Instant.now();
        String title = StringUtils.hasText(request.title()) ? request.title().trim() : "新对话";
        Conversation conversation = repository.insert(Conversation.builder()
                .userId(userId)
                .title(title)
                .model(aiProperties.model())
                .pinned(false)
                .createdAt(now)
                .updatedAt(now)
                .build());
        return repository.toView(conversation);
    }

    /**
     * 重命名会话并刷新更新时间。
     *
     * @param userId  用户 ID
     * @param id      会话 ID
     * @param request 更新请求，title 为新标题
     * @return 更新后的会话视图
     */
    public ConversationView rename(String userId, String id, UpdateConversationRequest request) {
        Conversation conversation = requireOwned(userId, id);
        conversation.setTitle(request.title().trim());
        conversation.setUpdatedAt(Instant.now());
        return repository.toView(repository.save(conversation));
    }

    /**
     * 切换会话的置顶状态并刷新更新时间。
     *
     * @param userId 用户 ID
     * @param id     会话 ID
     * @return 更新后的会话视图
     */
    public ConversationView togglePin(String userId, String id) {
        Conversation conversation = requireOwned(userId, id);
        conversation.setPinned(!conversation.isPinned());
        conversation.setUpdatedAt(Instant.now());
        return repository.toView(repository.save(conversation));
    }

    /**
     * 删除会话，并级联清理该会话下的全部消息。
     *
     * @param userId 用户 ID
     * @param id     会话 ID
     */
    public void delete(String userId, String id) {
        Conversation conversation = requireOwned(userId, id);
        messageRepository.deleteAllByConversationIdAndUserId(id, userId);
        repository.delete(conversation);
    }

    /**
     * 清空会话消息，并把标题重置为「新对话」、刷新更新时间。
     *
     * @param userId 用户 ID
     * @param id     会话 ID
     */
    public void clearMessages(String userId, String id) {
        Conversation conversation = requireOwned(userId, id);
        messageRepository.deleteAllByConversationIdAndUserId(id, userId);
        conversation.setTitle("新对话");
        conversation.setUpdatedAt(Instant.now());
        repository.save(conversation);
    }

    /**
     * 校验会话存在且属于该用户。
     *
     * @param userId 用户 ID
     * @param id     会话 ID
     * @return 通过校验的会话实体
     * @throws ApiException 会话不存在或不属于该用户时抛出 404
     */
    public Conversation requireOwned(String userId, String id) {
        return repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> ApiException.notFound("会话不存在或无权访问"));
    }

    /**
     * 会话仍为默认标题时，用首条用户消息生成标题：压缩空白，超长则截断为 23 个字符加省略号。
     *
     * @param userId       用户 ID
     * @param id           会话 ID
     * @param firstMessage 首条用户消息
     */
    public void markTitleFromFirstMessage(String userId, String id, String firstMessage) {
        Conversation conversation = requireOwned(userId, id);
        if (!"新对话".equals(conversation.getTitle())) {
            return;
        }
        String normalized = firstMessage.replaceAll("\\s+", " ").trim();
        if (!StringUtils.hasText(normalized)) {
            return;
        }
        conversation.setTitle(normalized.length() > 24
                ? normalized.substring(0, 23).stripTrailing() + "…"
                : normalized);
        conversation.setUpdatedAt(Instant.now());
        repository.save(conversation);
    }

    /**
     * 刷新会话更新时间，使会话在列表中前置。
     *
     * @param userId 用户 ID
     * @param id     会话 ID
     */
    public void touch(String userId, String id) {
        Conversation conversation = requireOwned(userId, id);
        conversation.setUpdatedAt(Instant.now());
        repository.save(conversation);
    }
}

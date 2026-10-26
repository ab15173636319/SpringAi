package com.aether.chat.service.impl;

import com.aether.chat.config.AiProperties;
import com.aether.chat.mapper.ChatMessageMapper;
import com.aether.chat.pojo.entity.ChatMessage;
import com.aether.chat.pojo.entity.MessageRole;
import com.aether.chat.service.ChatService;
import com.aether.chat.service.ConversationService;
import com.aether.chat.service.UsageService;
import com.aether.chat.tool.UtilityTools;
import com.aether.common.error.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * 对话服务实现：校验会话与额度、拼装上下文、调用大模型流式输出，并持久化问答消息。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private static final int CONTEXT_MESSAGE_LIMIT = 20;
    private static final String SYSTEM_PROMPT = """
            你是 Aether，一名可靠、直接的中文 AI 工作伙伴。
            优先回答问题本身，使用清晰的 Markdown 结构；避免寒暄、自我介绍和机械式能力清单。
            不确定时明确说明，不编造链接、数据或执行结果。
            代码和命令必须使用带语言标记的 Markdown 代码块。
            """;

    private final ChatMessageMapper messageRepository;
    private final ConversationService conversationService;
    private final UsageService usageService;
    private final ChatClient chatClient;

    /**
     * 以流式方式完成一轮对话。
     * <p>执行顺序：校验会话归属 → 扣减当日额度 → 读取历史上下文 → 写入用户消息 → 更新会话标题与时间；
     * 随后按配置走真实模型或演示模式，边输出边累积回答，在流正常结束或被取消时把助手回复落库。</p>
     *
     * @param userId         用户 ID
     * @param conversationId 会话 ID
     * @param prompt         用户本轮输入的文本
     * @return 回答内容的分块流；参数非法、额度不足或模型异常时以错误信号结束
     */
    @Override
    public Flux<String> stream(String userId, String conversationId, String prompt) {
        return Flux.defer(() -> doStream(userId, conversationId, prompt));
    }

    /**
     * 实际对话流程：被 {@link #stream} 包在 defer 中，保证校验、扣额度、写库等副作用只在该流被订阅时执行一次。
     */
    private Flux<String> doStream(String userId, String conversationId, String prompt) {
        conversationService.requireOwned(userId, conversationId);
        if (!usageService.tryConsume(userId)) {
            return Flux.error(ApiException.tooManyRequests("今日对话额度已用完，请在明天继续"));
        }

        List<Message> context = loadContext(conversationId, userId);
        saveMessage(conversationId, userId, MessageRole.USER, prompt);
        conversationService.markTitleFromFirstMessage(userId, conversationId, prompt);
        conversationService.touch(userId, conversationId);

        StringBuilder answer = new StringBuilder();
        return
                liveStream(context, prompt)
                        .doOnNext(answer::append) // 累积回答
                        // doFinally 对完成、异常、取消三种信号都只触发一次，无需额外的去重标记
                        .doFinally(signal -> {
                            log.info("对话结束，回答：{}", answer);
                            if (!answer.isEmpty()) {
                                saveMessage(conversationId, userId, MessageRole.ASSISTANT, answer.toString());
                                conversationService.touch(userId, conversationId);
                            }
                        })
                        .onErrorMap(error -> error instanceof ApiException
                                ? error
                                : new ApiException(HttpStatus.BAD_GATEWAY, "AI 服务暂时不可用，请稍后重试"));
    }

    /**
     * 持久化一条消息。
     *
     * @param conversationId 会话 ID
     * @param userId         用户 ID
     * @param role           消息角色
     * @param content        消息内容
     */
    private void saveMessage(String conversationId, String userId, MessageRole role, String content) {
        messageRepository.insert(ChatMessage.builder()
                .conversationId(conversationId)
                .userId(userId)
                .role(role)
                .content(content)
                .createdAt(Instant.now())
                .build());
    }

    /**
     * 调用真实大模型的流式接口，携带系统提示词、历史上下文与工具集。
     *
     * @param context 历史消息上下文
     * @param prompt  用户本轮输入
     * @return 模型逐块输出的文本流
     */
    private Flux<String> liveStream(List<Message> context, String prompt) {
        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .messages(context)
                .user(prompt)
                .advisors()
                .stream()
                .content();
    }

    /**
     * 加载上下文消息
     *
     * @param conversationId 会话 ID
     * @param userId         用户 ID
     * @return 上下文消息列表
     */
    private List<Message> loadContext(String conversationId, String userId) {
        List<ChatMessage> stored = messageRepository
                .findAllByConversationIdAndUserIdOrderByCreatedAtAsc(conversationId, userId);
        // 取最近 CONTEXT_MESSAGE_LIMIT 条消息
        int start = Math.max(0, stored.size() - CONTEXT_MESSAGE_LIMIT);
        List<Message> context = new ArrayList<>(stored.size() - start);
        // 转换为 UserMessage 或 AssistantMessage
        for (ChatMessage message : stored.subList(start, stored.size())) {
            context.add(message.getRole() == MessageRole.USER
                    ? new UserMessage(message.getContent())
                    : new AssistantMessage(message.getContent()));
        }
        return context;
    }
}

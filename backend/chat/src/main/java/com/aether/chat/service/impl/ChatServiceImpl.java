package com.aether.chat.service.impl;

import com.aether.chat.config.AiProperties;
import com.aether.chat.mapper.ChatMessageMapper;
import com.aether.chat.pojo.entity.ChatMessage;
import com.aether.chat.pojo.entity.MessageRole;
import com.aether.chat.service.ChatService;
import com.aether.chat.service.ConversationService;
import com.aether.chat.service.UsageService;
import com.aether.common.error.BusinessException;
import com.aether.common.result.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 对话服务实现：校验会话与额度、拼装上下文、调用大模型流式输出，并持久化问答消息。
 */
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
    private final AiProperties properties;
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
        return Flux.defer(() -> {
            conversationService.requireOwned(userId, conversationId);
            if (!usageService.tryConsume(userId)) {
                return Flux.error(new BusinessException(
                        ResponseCode.TOO_MANY_REQUESTS,
                        "今日对话额度已用完，请在明天继续"
                ));
            }

            List<Message> context = loadContext(conversationId, userId);
            messageRepository.insert(ChatMessage.builder()
                    .conversationId(conversationId)
                    .userId(userId)
                    .role(MessageRole.USER)
                    .content(prompt)
                    .createdAt(Instant.now())
                    .build());

            conversationService.markTitleFromFirstMessage(userId, conversationId, prompt);
            conversationService.touch(userId, conversationId);

            Flux<String> response = properties.isLive() && chatClient != null
                    ? liveStream(context, prompt)
                    : demoStream(prompt);

            StringBuilder answer = new StringBuilder();
            AtomicBoolean persisted = new AtomicBoolean(false);
            Runnable persistResponse = () -> {
                if (persisted.compareAndSet(false, true) && !answer.isEmpty()) {
                    messageRepository.insert(ChatMessage.builder()
                            .conversationId(conversationId)
                            .userId(userId)
                            .role(MessageRole.ASSISTANT)
                            .content(answer.toString())
                            .createdAt(Instant.now())
                            .build());
                    conversationService.touch(userId, conversationId);
                }
            };

            return response
                    .doOnNext(answer::append)
                    .doOnComplete(persistResponse)
                    .doFinally(signal -> {
                        if (signal != SignalType.ON_COMPLETE) {
                            persistResponse.run();
                        }
                    })
                    .onErrorMap(error -> error instanceof BusinessException
                            ? error
                            : new BusinessException(
                                    ResponseCode.BAD_GATEWAY,
                                    "AI 服务暂时不可用，请稍后重试",
                                    error
                            ));
        });
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
                .stream()
                .content();
    }

    /**
     * 本地演示模式：未配置 API Key 时用固定文案分块延时输出，便于前端联调。
     *
     * @param prompt 用户本轮输入，会被回显在回答中
     * @return 模拟的流式回答
     */
    private Flux<String> demoStream(String prompt) {
        List<String> chunks = List.of(
                "当前后端运行在 **本地演示模式**。\n\n",
                "你发送的内容已经写入 MongoDB，会话管理、流式显示和双 Token 鉴权都可以正常验证。\n\n",
                "你刚才的问题是：\n\n> ",
                prompt.replace("\n", "\n> "),
                "\n\n配置 `DEEPSEEK_API_KEY` 并重启后端后，这里会自动切换为 DeepSeek 的真实流式回答。"
        );
        return Flux.fromIterable(chunks).delayElements(Duration.ofMillis(35));
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

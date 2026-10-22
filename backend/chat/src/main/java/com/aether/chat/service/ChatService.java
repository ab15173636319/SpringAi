package com.aether.chat.service;

import reactor.core.publisher.Flux;

/**
 * 对话服务：负责组织上下文、调用大模型，并以流式方式返回回答。
 */
public interface ChatService {
    /**
     * 以流式（SSE）方式完成一轮对话。
     *
     * @param userId         用户 ID，用于会话归属校验与每日额度扣减
     * @param conversationId 会话 ID，必须属于该用户
     * @param prompt         用户本轮输入的文本
     * @return 逐块返回回答内容的流，订阅后才会真正执行
     */
    Flux<String> stream(String userId, String conversationId, String prompt);
}

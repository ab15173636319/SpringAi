package com.sai.chat.advisor;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Component
@Slf4j

public class ResponseAdvisor implements BaseAdvisor {

    // 提示词增强，question_answer_context为上下文信息（历史对话记录、知识库），query为用户问题
    private static final PromptTemplate DEFAULT_PROMPT_TEMPLATE = new PromptTemplate("""
            {query}
            上下文信息如下：被包裹在---------------------之间的内容
            
            ---------------------
            {question_answer_context}
            ---------------------
            
            如果有内容就简述回复给用户，无则客观简介的回答用户。
            """);
    private static final int DEFAULT_ORDER = 0;

    private static final PromptTemplate promptTemplate = DEFAULT_PROMPT_TEMPLATE;

    @NullMarked
    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {

        // 获取上下文信息
        Prompt str = chatClientRequest.prompt();

        UserMessage userMessage = chatClientRequest.prompt().getUserMessage();
        if (userMessage.getText() == null) {
            return chatClientRequest;
        }
        String augmentPrompt = promptTemplate.render(Map.of("query", userMessage.getText(), "question_answer_context", str.toString()));

        return chatClientRequest
                .mutate()
                .prompt(
                        chatClientRequest.prompt().augmentUserMessage(augmentPrompt)
                )
                .build();
    }

    @NullMarked
    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        ChatResponse chatResponse = chatClientResponse.chatResponse();
        if (chatResponse != null && !chatResponse.getResults().isEmpty()) {
            String reasoningContent = String.valueOf(chatResponse.getResults().get(0).getOutput().getMetadata().get("reasoningContent"));
            if (StringUtils.hasText(reasoningContent)) {
                List<Generation> generations = chatResponse
                        .getResults()
                        .stream()
                        .map(generation -> {
                            AssistantMessage assistantMessage = generation.getOutput();
                            String thinkContent = String.format("<think>%s</think>", reasoningContent) + assistantMessage.getText();
                            AssistantMessage thinkMessage = AssistantMessage.builder()
                                    .content(thinkContent)
                                    .properties(assistantMessage.getMetadata())
                                    .toolCalls(assistantMessage.getToolCalls())
                                    .media(assistantMessage.getMedia())
                                    .build();
                            return new Generation(thinkMessage, generation.getMetadata());
                        }).toList();
                chatResponse = new ChatResponse(generations, chatResponse.getMetadata());
            }
        }

        return ChatClientResponse
                .builder()
                .chatResponse(chatResponse)
                .context(chatClientResponse.context())
                .build();
    }

    @NullMarked
    @Override
    public int getOrder() {
        return 0;
    }
}

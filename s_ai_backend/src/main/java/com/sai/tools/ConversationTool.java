package com.sai.tools;

import com.sai.entity.Conversation;
import com.sai.exception.BusinessException;
import com.sai.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ConversationTool {

    private final ConversationRepository conversationRepository;

    @Tool(description = "修改当前对话标题")
    public Conversation modifyConversation(
            @ToolParam(description = "传入对话id") String conversationId,
            @ToolParam(description = "新标题") String title

    ) {
        // 检查对话是否存在
        Conversation conversation = conversationRepository.findById(conversationId).orElseThrow(() -> new BusinessException("对话不存在"));
        conversation.setTitle(title);
        conversation.setUpdateTime(LocalDateTime.now());
        return conversationRepository.save(conversation);


    }


}

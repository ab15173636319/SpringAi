package ai.s_ai.tools;

import ai.s_ai.configuration.GlobalException;
import ai.s_ai.entity.Conversation;
import ai.s_ai.entity.dto.ConversationDto;
import ai.s_ai.repository.MongodbRepository;
import ai.s_ai.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AiTool {

    private final MongodbRepository mongodbRepository;

    @Tool(description = "修改当前对话标题")
    public Conversation modifyConversation(
            @ToolParam(description = "传入对话id") String ConversationId,
            @ToolParam(description = "新标题") String title

    ) {
        // 检查对话是否存在
        Conversation conversation = mongodbRepository.findById(ConversationId).orElseThrow(() -> new GlobalException.BusinessException("对话不存在"));
        conversation.setTitle(title);
        conversation.setUpdateTime(LocalDateTime.now());
        return mongodbRepository.save(conversation);


    }


}

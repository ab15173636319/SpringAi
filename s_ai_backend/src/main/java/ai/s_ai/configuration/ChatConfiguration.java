package ai.s_ai.configuration;

import ai.s_ai.tools.*;
import ai.s_ai.utils.ReadXmlUtil;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.mongo.MongoChatMemoryRepository;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfiguration {

    /*
     * 这是Spring Ai聊天记忆处理
     * 使用mongodb存储聊天记忆
     * 使用MessageWindowChatMemory存储最近20条聊天记录
     */
    @Bean
    public ChatMemory chatMemory(MongoChatMemoryRepository repository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(repository)
                .maxMessages(20)
                .build();
    }

    @Bean
    public ChatClient chatClient(ChatModel chatModel, ChatMemory chatMemory, GaodeTools gaodeTools,
                                 EmotionUtil emotionUtil, TimeTool timeTool, ReadXmlUtil readXmlUtil, AiTool aiTool) {
        return ChatClient.create(chatModel)
                .prompt("你是ai助手，能够根据用户的问题，总结使用工具查到的数据，输出的格式为markdown格式。" +
                        "严禁输出欢迎语、自我介绍、打招呼、能力介绍或功能清单（如“你好，我是您的AI助手”、“我可以帮助您完成以下任务”、“我擅长根据您的需求，调用相应工具查询数据”、“请问有什么我可以帮您的吗”等）。"
                        +
                        "每次直接针对用户的问题给出答案，不要寒暄、不要罗列工具能力、不要罗列工具能力、不要罗列工具能力。")
                .tools(emotionUtil, gaodeTools, timeTool, aiTool)
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .mutate().build();
    }

    @Bean
    public ChatClient setTitle(ChatModel chatModel) {
        return ChatClient.create(chatModel)
                .prompt("根据用户聊天内容，总结一个不超过8个字的标题")
                .mutate()
                .build();
    }

}

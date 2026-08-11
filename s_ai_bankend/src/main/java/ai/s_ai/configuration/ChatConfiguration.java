package ai.s_ai.configuration;

import ai.s_ai.tools.EmotionUtil;
import ai.s_ai.tools.GaodeTools;
import ai.s_ai.tools.JsonBack;
import ai.s_ai.tools.TimeTool;
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

    @Bean
    public ChatMemory chatMemory(MongoChatMemoryRepository repository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(repository)
                .maxMessages(20)
                .build();
    }

    @Bean
    public ChatClient chatClient(ChatModel chatModel, ChatMemory chatMemory, GaodeTools gaodeTools, EmotionUtil emotionUtil, TimeTool timeTool, ReadXmlUtil readXmlUtil) {
        return ChatClient.create(chatModel)
                .prompt("你是ai助手，言简意赅。以markdown格式返回。")
                .tools(emotionUtil, gaodeTools, timeTool)
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .mutate().build();
    }


}

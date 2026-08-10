package ai.s_ai.configuration;

import ai.s_ai.tools.EmotionUtil;
import ai.s_ai.tools.GaodeTools;
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
    public ChatClient chatClient(ChatModel chatModel, ChatMemory chatMemory, GaodeTools gaodeTools) {
        return ChatClient.create(chatModel)
                .prompt("你是一个善解人意的助手，输出内容要多情绪化,以json格式返回{msg:‘回答用户的内容，以markdown格式返回’,qute:[为用户提供三条话题]}")
                .tools(new EmotionUtil(), gaodeTools)
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .mutate().build();
    }


}

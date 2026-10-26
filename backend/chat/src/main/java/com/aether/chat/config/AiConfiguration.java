package com.aether.chat.config;

import com.aether.chat.tool.AMap;
import com.aether.chat.tool.ShowApi;
import com.aether.chat.tool.UtilityTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AiProperties.class)
public class AiConfiguration {

    @Bean
    public ChatClient chatClient(
            ChatModel chatModel,
            AMap aMap,
            UtilityTools utilityTools,
            ShowApi showApi
    ) {
        return ChatClient.builder(chatModel)
                .defaultTools(aMap, utilityTools, showApi)
                .build();
    }
}



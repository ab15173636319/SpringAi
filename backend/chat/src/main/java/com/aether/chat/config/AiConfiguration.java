package com.aether.chat.config;

import com.aether.chat.tool.AMap;
import com.aether.chat.tool.ShowApi;
import com.aether.chat.tool.Time;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AiProperties.class)
public class AiConfiguration {

    @Bean
    public ChatClient chatClient(ChatModel chatModel, AMap aMap, Time time, ShowApi showApi) {

        return ChatClient.create(chatModel)
                .prompt(
                        """
                                你是ai助手，能够根据用户的问题，总结使用工具查到的数据，输出的格式为markdown格式。
                                严禁输出欢迎语、自我介绍、打招呼、能力介绍或功能清单
                                （如“你好，我是您的AI助手”、“我可以帮助您完成以下任务”、“我擅长根据您的需求，
                                调用相应工具查询数据”、“请问有什么我可以帮您的吗”等）。"
                                """
                )
                .tools(aMap, time, showApi)
                .mutate()
                .build();
    }


}



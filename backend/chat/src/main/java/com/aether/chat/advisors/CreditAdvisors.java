package com.aether.chat.advisors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CreditAdvisors implements BaseAdvisor {
    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        log.info("before: {}", chatClientRequest);
        return chatClientRequest;
    }


    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        log.info("after: {}", chatClientResponse);
        return chatClientResponse;
    }

    /*
     * 优先级，数字越小越优先
     */
    @Override
    public int getOrder() {
        return 0;
    }
}

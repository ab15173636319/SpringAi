package com.aether.chat.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.ai")
public record AiProperties(
        String model,
        String deepseekApiKey,
        int dailyLimit
) {
    public boolean isLive() {
        return deepseekApiKey != null
                && !deepseekApiKey.isBlank()
                && !"local-dev-placeholder".equals(deepseekApiKey);
    }
}



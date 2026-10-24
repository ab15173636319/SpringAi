package com.aether;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.mongodb.uri=mongodb://localhost:27017/aether_ai_test",
        "spring.ai.deepseek.api-key=test-key",
        "spring.ai.deepseek.base-url=http://localhost:11434",
        "spring.ai.deepseek.chat.options.model=deepseek-chat",
        "app.ai.model=deepseek-chat",
        "app.ai.deepseek-api-key=",
        "app.ai.daily-limit=100",
        "app.security.jwt.secret=test-secret-with-at-least-thirty-two-characters-for-jwt",
        "app.security.cookie.secure=false",
        "app.security.cookie.path=/api/auth",
        "app.security.cors.allowed-origins[0]=http://localhost:5173"
})
class AetherApplicationTests {
    @Test
    void contextLoads() {
    }
}



package com.aether;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.mongodb.uri=mongodb://root:123456@localhost:27017/aether_ai_test?authSource=admin",
        "app.security.jwt.secret=test-secret-with-at-least-thirty-two-characters-for-jwt"
})
class AetherApplicationTests {
    @Test
    void contextLoads() {
    }
}



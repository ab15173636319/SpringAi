package com.aether;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(
        scanBasePackages = "com.aether",
        exclude = UserDetailsServiceAutoConfiguration.class
)
public class AetherApplication {
    public static void main(String[] args) {
        SpringApplication.run(AetherApplication.class, args);
    }
}



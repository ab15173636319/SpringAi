package com.aether;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.aether")
public class AetherApplication {
    public static void main(String[] args) {
        SpringApplication.run(AetherApplication.class, args);
    }
}



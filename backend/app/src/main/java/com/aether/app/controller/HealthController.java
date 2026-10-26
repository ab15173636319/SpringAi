package com.aether.app.controller;

import com.aether.app.pojo.vto.HealthView;
import com.aether.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api")
public class HealthController {
    @GetMapping("/health")
    public Result<HealthView> health() {
        return Result.success(new HealthView("UP", "aether-ai", Instant.now()));
    }
}




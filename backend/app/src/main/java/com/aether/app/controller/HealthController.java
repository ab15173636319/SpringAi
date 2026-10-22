package com.aether.app.controller;

import com.aether.app.pojo.vto.HealthView;
import com.aether.common.pojo.vto.ApiResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api")
public class HealthController {
    @GetMapping("/health")
    public ApiResult<HealthView> health() {
        return ApiResult.success(new HealthView("UP", "aether-ai", Instant.now()));
    }
}




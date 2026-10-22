package com.aether.chat.controller;

import com.aether.chat.pojo.vto.UsageView;
import com.aether.chat.service.UsageService;
import com.aether.common.pojo.vto.ApiResult;
import com.aether.security.util.SecurityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usage")
public class UsageController {
    private final UsageService usageService;

    public UsageController(UsageService usageService) {
        this.usageService = usageService;
    }

    @GetMapping
    public ApiResult<UsageView> current() {
        return ApiResult.success(usageService.current(SecurityUtils.currentUserId()));
    }
}




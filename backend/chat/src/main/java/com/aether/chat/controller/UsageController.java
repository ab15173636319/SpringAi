package com.aether.chat.controller;

import com.aether.chat.pojo.vto.UsageView;
import com.aether.chat.service.UsageService;
import com.aether.common.result.Result;
import com.aether.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/usage")
public class UsageController {
    private final UsageService usageService;

    @GetMapping
    public Result<UsageView> current() {
        return Result.success(usageService.current(SecurityUtils.currentUserId()));
    }
}




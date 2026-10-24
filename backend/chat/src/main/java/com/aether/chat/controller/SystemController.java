package com.aether.chat.controller;

import com.aether.chat.pojo.vto.SystemInfoView;
import com.aether.chat.service.SystemService;
import com.aether.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/system")
public class SystemController {
    private final SystemService systemService;

    @GetMapping("/info")
    public Result<SystemInfoView> info() {
        return Result.success(systemService.info());
    }
}



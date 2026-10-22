package com.aether.chat.controller;

import com.aether.chat.pojo.vto.SystemInfoView;
import com.aether.chat.service.SystemService;
import com.aether.common.pojo.vto.ApiResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system")
public class SystemController {
    private final SystemService systemService;

    public SystemController(SystemService systemService) {
        this.systemService = systemService;
    }

    @GetMapping("/info")
    public ApiResult<SystemInfoView> info() {
        return ApiResult.success(systemService.info());
    }
}



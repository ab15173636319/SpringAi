package com.aether.chat.service.impl;

import com.aether.chat.config.AiProperties;
import com.aether.chat.pojo.vto.SystemInfoView;
import com.aether.chat.service.SystemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 系统信息服务实现：直接读取 {@link AiProperties} 暴露运行配置。
 */
@Service
@RequiredArgsConstructor
public class SystemServiceImpl implements SystemService {
    private final AiProperties properties;

    /**
     * 汇总当前运行模式（live/demo）、模型名称与每日对话额度。
     *
     * @return 系统信息视图
     */
    @Override
    public SystemInfoView info() {
        return new SystemInfoView(
                properties.isLive() ? "live" : "demo",
                properties.model(),
                properties.dailyLimit()
        );
    }
}

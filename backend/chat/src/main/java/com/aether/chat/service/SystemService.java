package com.aether.chat.service;

import com.aether.chat.pojo.vto.SystemInfoView;

/**
 * 系统信息服务：向前端暴露后端当前的运行配置。
 */
public interface SystemService {
    /**
     * 获取系统信息，包括运行模式（live/demo）、模型名称与每日对话额度。
     *
     * @return 系统信息视图
     */
    SystemInfoView info();
}

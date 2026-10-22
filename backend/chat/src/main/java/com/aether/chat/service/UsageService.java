package com.aether.chat.service;

import com.aether.chat.pojo.vto.UsageView;

/**
 * 用量服务：按自然日统计并限制用户的对话次数。
 */
public interface UsageService {
    /**
     * 尝试为当天占用一次对话额度。
     *
     * @param userId 用户 ID
     * @return 抢占成功返回 {@code true}；当天额度已用尽返回 {@code false}
     */
    boolean tryConsume(String userId);

    /**
     * 查询当天已用额度与上限。
     *
     * @param userId 用户 ID
     * @return 用量视图
     */
    UsageView current(String userId);
}

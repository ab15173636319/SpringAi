package com.aether.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Redis 常用操作封装，统一走 {@link RedisConfig} 里配置的 JSON 序列化。
 */
@Component
@RequiredArgsConstructor
public class RedisUtils {

    /** SCAN 每批返回的 key 数量，同时作为批量删除的批次大小 */
    private static final long SCAN_BATCH = 500L;

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 保存，永久有效。
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 保存并设置过期时间。
     */
    public void set(String key, Object value, Duration timeout) {
        redisTemplate.opsForValue().set(key, value, timeout);
    }

    /**
     * 获取值，key 不存在时返回 null。
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * key 是否存在。
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 获取过期时间，单位秒。
     *
     * @return 剩余秒数；-1 表示永不过期；-2 表示 key 不存在
     */
    public long getExpire(String key) {
        Long seconds = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        return seconds == null ? -2L : seconds;
    }

    /**
     * 获取剩余存活时间。
     *
     * @return 剩余时长；永不过期或 key 不存在时返回 {@link Duration#ZERO}，
     *         需要区分这两种情况请用 {@link #getExpire(String)}
     */
    public Duration getRemainingTtl(String key) {
        Long millis = redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
        return millis == null || millis <= 0 ? Duration.ZERO : Duration.ofMillis(millis);
    }

    /**
     * 删除单个 key。
     *
     * @return true 表示确实删掉了，false 表示 key 本来就不存在
     */
    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    /**
     * 删除指定前缀下的所有 key，例如 {@code deleteByPrefix("chat:session:")}。
     * <p>
     * 使用 SCAN 增量遍历而不是 KEYS，避免大 key 空间下阻塞 Redis；遍历过程中分批删除。
     *
     * @return 实际删除的 key 数量
     */
    public long deleteByPrefix(String prefix) {
        if (!StringUtils.hasText(prefix)) {
            return 0L;
        }
        ScanOptions options = ScanOptions.scanOptions()
                .match(prefix + "*")
                .count(SCAN_BATCH)
                .build();

        long removed = 0L;
        List<String> batch = new ArrayList<>((int) SCAN_BATCH);
        try (Cursor<String> cursor = redisTemplate.scan(options)) {
            while (cursor.hasNext()) {
                batch.add(cursor.next());
                if (batch.size() >= SCAN_BATCH) {
                    removed += deleteBatch(batch);
                }
            }
        }
        return removed + deleteBatch(batch);
    }

    private long deleteBatch(List<String> keys) {
        if (keys.isEmpty()) {
            return 0L;
        }
        Long count = redisTemplate.delete(keys);
        keys.clear();
        return count == null ? 0L : count;
    }
}

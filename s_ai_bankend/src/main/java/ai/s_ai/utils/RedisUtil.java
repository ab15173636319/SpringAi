package ai.s_ai.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

public class RedisUtil {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 设置值
     *
     * @param key   键
     * @param value 值
     */
    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置值
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间
     * @param unit    过期时间单位
     */
    public void set(String key, String value, long timeout, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 获取值
     * @param key 键名
     * @return String
     */
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }





}

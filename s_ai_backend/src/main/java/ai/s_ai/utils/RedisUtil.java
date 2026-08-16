package ai.s_ai.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
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
     *
     * @param key 键名
     * @return String
     */
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 删除值
     *
     * @param key 键名
     */
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 判断键是否存在
     *
     * @param key 键名
     * @return 是否存在
     */
    public boolean exists(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    /**
     * 重置键过期时间
     *
     * @param key     键名
     * @param timeout 过期时间
     * @param unit    过期时间单位
     */
    public void expire(String key, long timeout, TimeUnit unit) {
        stringRedisTemplate.expire(key, timeout, unit);
    }

    /**
     * 获取键过期时间
     *
     * @param key  键名
     * @param unit 时间单位
     * @return 过期时间
     */
    public long getExpire(String key, TimeUnit unit) {
        Long expire = stringRedisTemplate.getExpire(key, unit);
        return expire == null ? 0 : expire;
    }

    /**
     * 设置值 如果键不存在
     *
     * @param key   键名
     * @param value 值
     */
    public void setIfAbsent(String key, String value) {
        stringRedisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * 自增
     *
     * @param key 键名
     * @return 自增后的值
     */
    public long incrementAndGet(String key) {
        Long result = stringRedisTemplate.opsForValue().increment(key);
        return result != null ? result : 0;
    }

    /**
     * 自增并设置过期时间（键不存在时生效）
     *
     * @param key     键名
     * @param timeout 过期时间
     * @param unit    过期时间单位
     * @return 自增后的值
     */
    public long incrementAndGet(String key, long timeout, TimeUnit unit) {
        Long result = stringRedisTemplate.opsForValue().increment(key);
        if (result != null && result == 1) {
            stringRedisTemplate.expire(key, timeout, unit);
        }
        return result != null ? result : 0;
    }

    /**
     * 获取整型值
     *
     * @param key 键名
     * @return 整数值，不存在返回0
     */
    public int getInt(String key) {
        String value = stringRedisTemplate.opsForValue().get(key);
        return value != null ? Integer.parseInt(value) : 0;
    }

}

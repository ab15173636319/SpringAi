package ai.s_ai.components;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.stereotype.Component;

@Component
public class JsonRedisTemplate extends RedisTemplate<String, Object> {

    public JsonRedisTemplate(RedisConnectionFactory redisConnectionFactory){
        super.setConnectionFactory(redisConnectionFactory);

        GenericJacksonJsonRedisSerializer gsjs = GenericJacksonJsonRedisSerializer.builder().build();
    }

}

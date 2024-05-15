package org.example.onmessage.config.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/2/27
 */

@Configuration
public class RedissonConfig {
    @Value("${REDIS_PASSWORD}")
    private String password;

    @Bean
    public RedissonClient redissonClient() {
        // 配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://39.98.41.126:6379")
                .setPassword(password);
        // 创建RedissonClient对象
        return Redisson.create(config);
    }
}

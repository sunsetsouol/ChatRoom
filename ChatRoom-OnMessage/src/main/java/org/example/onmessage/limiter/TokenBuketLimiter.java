package org.example.onmessage.limiter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.onmessage.constants.RedisConstant;
import org.example.onmessage.service.common.RedisCacheService;
import org.example.pojo.dto.WsMessageDTO;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/6/11
 */
@Component(RedisConstant.TOKEN_BUCKET)
@Slf4j
@RequiredArgsConstructor
public class TokenBuketLimiter implements Limiter {
    private final RedisCacheService redisCacheService;

    private static DefaultRedisScript<List> sliding = new DefaultRedisScript<>();

    static {
        sliding.setLocation(new ClassPathResource("token_bucket_limiter.lua"));
        sliding.setResultType(List.class);
    }

    @Override
    public boolean limiter(WsMessageDTO wsMessageDTO) {
        try {
            List<String> keys = Arrays.asList(RedisConstant.TOKEN_BUKET_LIMITER_TOKEN_PREFIX + wsMessageDTO.getFromUserId(),
                    RedisConstant.TOKEN_BUKET_LIMITER_TIMESTAMP_PREFIX + wsMessageDTO.getFromUserId());
            List<Long> execute = redisCacheService.execute(sliding, keys, RedisConstant.TOKEN_BUKET_RATE, RedisConstant.TOKEN_BUKET_CAPACITY, Instant.now().getEpochSecond(), RedisConstant.TOKEN_BUKET_REQUESTED);
            return execute.get(0) == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }
}

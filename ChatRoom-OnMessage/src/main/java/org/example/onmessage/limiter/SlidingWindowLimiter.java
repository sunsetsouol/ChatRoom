package org.example.onmessage.limiter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.onmessage.constants.RedisConstant;
import org.example.onmessage.mq.service.MessageService;
import org.example.onmessage.route.MessageBuffer;
import org.example.onmessage.service.common.RedisCacheService;
import org.example.pojo.dto.WsMessageDTO;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/16
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SlidingWindowLimiter implements Limiter{

    private final MessageService messageService;
    private final MessageBuffer messageBuffer;
    private final RedisCacheService redisCacheService;


    private static DefaultRedisScript<List> sliding = new DefaultRedisScript<>();

    static {
        sliding.setLocation(new ClassPathResource("sliding_window_limiter.lua"));
        sliding.setResultType(List.class);
    }

    // TODO：滑动窗口限流
    @Override
    public boolean limiter(WsMessageDTO wsMessageDTO){
        List<String> keys = Arrays.asList(RedisConstant.SLIDING_WINDOW_LIMITER_PREFIX + wsMessageDTO.getFromUserId(),
                UUID.randomUUID().toString());
        List<Long> execute = redisCacheService.execute(sliding, keys, RedisConstant.SLIDING_WINDOW_RATE, RedisConstant.SLIDING_WINDOW_CAPACITY, String.valueOf(Instant.now().getEpochSecond()));
        return execute.get(0) == 1;
    }

    public void handleMessage(WsMessageDTO wsMessageDTO){
        if (!limiter(wsMessageDTO)){
            log.info("消息推送太频繁，直接丢弃");
            return;
        }
        messageBuffer.handleMsg(wsMessageDTO);
        // 消息正常推送
        log.info("消息正常推送:{}", wsMessageDTO);
//        messageService.push2mq(wsMessageDTO);
//        // ack
//        WsMessageDTO ack1Message = MessageAdapter.getAck1Message(wsMessageDTO);
//        GlobalWsMap.sendText(wsMessageDTO.getFromUserId(), wsMessageDTO.getDevice(), JSON.toJSONString(ack1Message));
    }
}

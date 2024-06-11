package org.example.onmessage.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.onmessage.constants.RedisConstant;
import org.example.onmessage.limiter.Limiter;
import org.example.onmessage.limiter.TokenBuketLimiter;
import org.example.pojo.dto.WsMessageDTO;
import org.example.onmessage.route.DownLinkMessageRoute;
import org.example.onmessage.service.MessageService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/9
 */
@Service
@Slf4j
public class MessageServiceImpl implements MessageService {
    private final DownLinkMessageRoute downLinkMessageRoute;
    private final Limiter limiter;

    public MessageServiceImpl(DownLinkMessageRoute downLinkMessageRoute, @Qualifier(RedisConstant.TOKEN_BUCKET) Limiter limiter) {
        this.downLinkMessageRoute = downLinkMessageRoute;
        this.limiter = limiter;
    }

    @Override
    public void accept(WsMessageDTO wsMessageDTO) {
        // 参数校验
        if (!limiter.limiter(wsMessageDTO)){
            log.info("消息推送太频繁，直接丢弃");
            return;
        }
        wsMessageDTO.validate();

        // 消息下行推送
        downLinkMessageRoute.downLinkMessagePush(wsMessageDTO);

    }
}

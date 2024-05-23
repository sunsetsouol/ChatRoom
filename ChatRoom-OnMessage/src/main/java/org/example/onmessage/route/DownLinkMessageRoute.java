package org.example.onmessage.route;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pojo.AbstractMessage;
import org.example.pojo.dto.WsMessageDTO;
import org.example.onmessage.limiter.SlidingWindowLimiter;
import org.springframework.stereotype.Component;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/9
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DownLinkMessageRoute {
    private final MessageBuffer messageBuffer;
    private final SlidingWindowLimiter slidingWindowLimiter;

    /**
     * 下行消息推送
     *
     * @param wsMessageDTO 消息
     */
    public void downLinkMessagePush(WsMessageDTO wsMessageDTO) {
        log.info("下行消息推送:{}", wsMessageDTO);
        if (wsMessageDTO.getMessageType().equals(AbstractMessage.MessageType.GET_MESSAGE.getCode())){
            messageBuffer.getUnreadMessage(wsMessageDTO);
        }else {
            slidingWindowLimiter.handleMessage(wsMessageDTO);
        }
    }
}

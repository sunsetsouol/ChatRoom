package org.example.onmessage.route;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.onmessage.entity.dto.WsMessageDTO;
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

    /**
     * 下行消息推送
     *
     * @param wsMessageDTO 消息
     */
    public void downLinkMessagePush(WsMessageDTO wsMessageDTO) {
        log.info("下行消息推送:{}", wsMessageDTO);
        messageBuffer.handleMsg(wsMessageDTO);
    }
}

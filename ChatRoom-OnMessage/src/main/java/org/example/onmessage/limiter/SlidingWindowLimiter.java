package org.example.onmessage.limiter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.onmessage.mq.service.MessageService;
import org.example.onmessage.route.MessageBuffer;
import org.example.pojo.dto.WsMessageDTO;
import org.springframework.stereotype.Component;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/16
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SlidingWindowLimiter {

    private final MessageService messageService;
    private final MessageBuffer messageBuffer;
    // TODO：滑动窗口限流
    public boolean limiter(WsMessageDTO wsMessageDTO){
        return true;
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

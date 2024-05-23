package org.example.onmessage.limiter;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.onmessage.adapter.MessageAdapter;
import org.example.pojo.dto.WsMessageDTO;
import org.example.onmessage.handler.ws.GlobalWsMap;
import org.example.onmessage.mq.service.MQService;
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

    private final MQService mqService;
    // TODO：滑动窗口限流
    public boolean limiter(WsMessageDTO wsMessageDTO){
        return true;
    }

    public void handleMessage(WsMessageDTO wsMessageDTO){
        if (!limiter(wsMessageDTO)){
            log.info("消息推送太频繁，直接丢弃");
            return;
        }
        // 消息正常推送
        log.info("消息正常推送:{}", wsMessageDTO);
        mqService.push2mq(wsMessageDTO);
        // ack
        WsMessageDTO ack1Message = MessageAdapter.getAck1Message(wsMessageDTO);
        GlobalWsMap.sendText(wsMessageDTO.getFromUserId(), wsMessageDTO.getDevice(), JSON.toJSONString(ack1Message));
    }
}

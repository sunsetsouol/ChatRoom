package org.example.onmessage.publish.listener;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import org.example.onmessage.dao.MsgReader;
import org.example.onmessage.handler.ws.GlobalWsMap;
import org.example.onmessage.publish.event.MessageAckToUserEvent;
import org.example.pojo.AbstractMessage;
import org.example.pojo.bo.MessageBO;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/6/6
 */
@Component
@RequiredArgsConstructor
public class MessageAckToUserEventListener {
    private final MsgReader msgReader;
    @EventListener(MessageAckToUserEvent.class)
    public void onMessageAckToUserEvent(MessageAckToUserEvent event) {
        MessageBO message = event.getMessage();
        AbstractMessage.MessageType messageType = event.getMessageType();
        if (messageType.equals(AbstractMessage.MessageType.SERVER_ACK)) {
            message.setMessageType(AbstractMessage.MessageType.SERVER_ACK.getCode());
            msgReader.acked(message.getId());
        }
        GlobalWsMap.sendText(event.getAckTargetUserId(), JSON.toJSONString(message));
    }
}

package org.example.onmessage.push;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import org.example.onmessage.dao.MsgWriter;
import org.example.onmessage.entity.AbstractMessage;
import org.example.onmessage.entity.bo.MessageBO;
import org.example.onmessage.handler.ws.GlobalWsMap;
import org.springframework.stereotype.Component;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/13
 */
@Component
@RequiredArgsConstructor
public class PushWorker {
    private final MsgWriter msgWriter;
    public void push2User(MessageBO message){

        msgWriter.saveSingleChatMsg(message);
        GlobalWsMap.sendText(message.getTargetId(), JSON.toJSONString(message));

    }

    public void push2Group(MessageBO message){

    }

    public void push(MessageBO message){
        if (message.getMessageType().equals(AbstractMessage.MessageType.SINGLE.getCode())) {
            push2User(message);
        } else if (message.getMessageType().equals(AbstractMessage.MessageType.GROUP.getCode()) ) {
            push2Group(message);
        }
    }
}

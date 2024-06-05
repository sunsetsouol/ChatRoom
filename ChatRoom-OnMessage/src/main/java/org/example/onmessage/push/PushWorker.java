package org.example.onmessage.push;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import org.example.IdStrategy.IdGen.IdGeneratorStrategyFactory;
import org.example.constant.RedisCacheConstants;
import org.example.onmessage.dao.MsgReader;
import org.example.onmessage.dao.MsgWriter;
import org.example.onmessage.handler.ws.GlobalWsMap;
import org.example.onmessage.publish.PublishEventUtils;
import org.example.onmessage.service.common.RedisCacheService;
import org.example.pojo.AbstractMessage;
import org.example.pojo.bo.MessageSendBo;
import org.example.pojo.bo.MessageBO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/13
 */
@Component
@RequiredArgsConstructor
public class PushWorker {
    private final MsgWriter msgWriter;
    private final MsgReader msgReader;
    private final PublishEventUtils publishEventUtils;
    private final RedisCacheService redisCacheService;
    private final IdGeneratorStrategyFactory idGeneratorStrategyFactory;

    public void push2User(MessageBO message) {

//        msgWriter.saveDurably(message);

        // 推送消息
        GlobalWsMap.sendText(message.getTargetId(), JSON.toJSONString(message));
        // todo：等待用户ack后再返回业务ack，这里测试不等了

        publishEventUtils.pushMessageAck(this, message);

//        if (GlobalWsMap.coreDeviceIsOnline(message.getTargetId())) {
//            publishEventUtils.pushMessageAck(this, message, AbstractMessage.MessageType.SERVER_ACK);
//        }else {
//            publishEventUtils.pushMessageAck(this, message, AbstractMessage.MessageType.DEVICE_ACK);
//        }
//        GlobalWsMap.sendText(message.getFromUserId(), JSON.toJSONString(message));
    }

    public void push2Group(MessageBO message) {

        msgWriter.saveDurably(message);

        Set<Long> memberIds = redisCacheService.gAllSet(RedisCacheConstants.ROOM_MEMBER + message.getTargetId(), Long.class);
        // 推送消息
        // todo：如果人数太多就放在群聊消息表，每次都去查
        memberIds.forEach(memberId -> GlobalWsMap.sendText(memberId, JSON.toJSONString(message)));
        GlobalWsMap.sendText(memberIds, message);

        // todo：如果是私聊，则只有目标设备需要ack，如果是群聊则需要所有群聊都ack才行
//        publishEventUtils.pushMessageAck(this, message, AbstractMessage.MessageType.SERVER_ACK);

    }



    public void push(MessageSendBo messageSendBo) {
        Set<String> receivers = messageSendBo.getReceivers();
        MessageBO message = messageSendBo.getMessage();
        String messageString = JSON.toJSONString(message);

        receivers.forEach(receiver -> {
            String[] split = receiver.split(":");
            Long userId = Long.parseLong(split[0]);
            Integer device = Integer.parseInt(split[1]);
            GlobalWsMap.sendText(userId, device, messageString);
        });

//        if (message.getMessageType().equals(AbstractMessage.MessageType.SINGLE.getCode())) {
//            push2User(message);
//        } else if (message.getMessageType().equals(AbstractMessage.MessageType.GROUP.getCode())) {
//            push2Group(message);
//        }
    }

//    public void push(List<MessageBO> messageList) {
//
//    }
//
//    public void push2Group(MessageSendBo messageSendBo) {
//        Set<String> receivers = messageSendBo.getReceivers();
//        MessageBO message = messageSendBo.getMessage();
//        String messageString = JSON.toJSONString(message);
//
//        receivers.forEach(receiver -> {
//            String[] split = receiver.split(":");
//            Long userId = Long.parseLong(split[0]);
//            Integer device = Integer.parseInt(split[1]);
//            GlobalWsMap.sendText(userId, device, messageString);
//        });
//    }
}

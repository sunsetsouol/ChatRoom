package org.example.onmessage.push;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import org.example.constant.RedisCacheConstants;
import org.example.onmessage.dao.MsgWriter;
import org.example.onmessage.entity.AbstractMessage;
import org.example.onmessage.entity.bo.MessageBO;
import org.example.onmessage.handler.ws.GlobalWsMap;
import org.example.onmessage.publish.PublishEventUtils;
import org.example.onmessage.service.common.RedisCacheService;
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
    private final PublishEventUtils publishEventUtils;
    private final RedisCacheService redisCacheService;
    public void push2User(MessageBO message){

        // TODO：lua脚本保证原子性
        // 保存消息id映射
        msgWriter.saveMessageIdMap(message.getFromUserId(), message.getDevice(), message.getClientMessageId(), message.getId());
        // 保存消息到对话缓存
        msgWriter.saveSingleChatMsg(message);
        // 收件箱更新
        msgWriter.saveSingleInboxMsg(message);
        // 推送消息
        GlobalWsMap.sendText(message.getTargetId(), JSON.toJSONString(message));
        GlobalWsMap.sendText(message.getFromUserId(), JSON.toJSONString(message));
        // TODO：online服务做好后，要等client的acK，如果不online直接返回即可
        // 业务ack
        publishEventUtils.pushMessageAck(this, message);
    }

    public void push2Group(MessageBO message){
        // TODO：lua脚本保证原子性
        // 保存消息id映射
        msgWriter.saveMessageIdMap(message.getFromUserId(), message.getDevice(), message.getClientMessageId(), message.getId());
        // 保存消息到对话缓存
        msgWriter.saveGroupChatMsg(message);

        Set<Long> memberIds = redisCacheService.gAllSet(RedisCacheConstants.ROOM_MEMBER + message.getTargetId(), Long.class);
        // 收件箱更新
        msgWriter.saveGroupInboxMsg(message, memberIds);
        // 推送消息
        memberIds.forEach(memberId -> GlobalWsMap.sendText(memberId, JSON.toJSONString(message)));
        // 业务ack
        publishEventUtils.pushMessageAck(this, message);
    }

    public void push(MessageBO message){
        if (message.getMessageType().equals(AbstractMessage.MessageType.SINGLE.getCode())) {
            push2User(message);
        } else if (message.getMessageType().equals(AbstractMessage.MessageType.GROUP.getCode()) ) {
            push2Group(message);
        }
    }

    public void push(List<MessageBO> messageList) {

    }
}

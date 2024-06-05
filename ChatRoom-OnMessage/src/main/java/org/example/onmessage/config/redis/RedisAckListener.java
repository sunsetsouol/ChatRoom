package org.example.onmessage.config.redis;

import lombok.RequiredArgsConstructor;
import org.example.onmessage.constants.RedisConstant;
import org.example.onmessage.dao.MsgReader;
import org.example.onmessage.mq.service.MessageService;
import org.example.pojo.bo.MessageBO;
import org.example.onmessage.service.common.RedisCacheService;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/13
 */
@Component
@RequiredArgsConstructor
public class RedisAckListener implements MessageListener {
    private final RedisCacheService redisCacheService;
    private final MsgReader msgReader;
    private final MessageService messageService;
//    @Override
//    protected void doRegister(RedisMessageListenerContainer listenerContainer) {
//        listenerContainer.addMessageListener(this, new PatternTopic("__keyevent@2__:del test"));
//    }


    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 只监听过期事件
        String event = new String(message.getBody());

        String[] split = new String(message.getChannel()).split(":");
        String userId = split[split.length - 2];
        String key = split[split.length - 1];
        if (event.equals(RedisConstant.EXPIRED)) {
            // 重发
            long messageId = Long.parseLong(key);
//            List<MessageBO> messageBOS = msgReader.getWindowsMsg(RedisConstant.MESSAGE + userId, messageId, messageId, 0L, 1L, MessageBO.class);
//            for (MessageBO messageBO : messageBOS) {
//                messageService.push2mq(messageBO);
//                // ack
//                redisCacheService.setCacheObject(RedisConstant.ACK + messageBO.getFromUserId() + ":" + messageBO.getId(), "", RedisConstant.ACK_EXPIRE_TIME, TimeUnit.SECONDS);
//
//            }

            MessageBO messageBO = msgReader.getMsgByScore(RedisConstant.MESSAGE + userId, messageId, MessageBO.class);
            messageService.push2mq(messageBO);
            // ack
            redisCacheService.setCacheObject(RedisConstant.ACK + messageBO.getFromUserId() + ":" + messageBO.getId(), "", RedisConstant.ACK_EXPIRE_TIME, TimeUnit.SECONDS);
        } else if (event.equals(RedisConstant.DEL)) {
            // TODO： del就是ack了，给双方ack

        }


    }
}

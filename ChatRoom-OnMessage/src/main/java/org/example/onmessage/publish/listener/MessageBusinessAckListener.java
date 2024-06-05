package org.example.onmessage.publish.listener;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.example.constant.RedisCacheConstants;
import org.example.onmessage.constants.RabbitMQConstant;
import org.example.onmessage.dao.MsgWriter;
import org.example.onmessage.mq.service.MessageService;
import org.example.onmessage.service.MQService;
import org.example.onmessage.service.common.RedisCacheService;
import org.example.pojo.AbstractMessage;
import org.example.pojo.bo.BusinessAckMessage;
import org.example.pojo.bo.MessageBO;
import org.example.onmessage.publish.event.MessageBusinessAckEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/13
 */
@Component
@RequiredArgsConstructor
public class MessageBusinessAckListener {
    private final MQService mqService;
    private final RedisCacheService redisCacheService;
    private final MsgWriter msgWriter;

    @EventListener(MessageBusinessAckEvent.class)
    public void messageBusinessAck(MessageBusinessAckEvent event) {
        MessageBO messageBO = event.getMessageBO();
        AbstractMessage.MessageType messageType = event.getMessageType();

        messageBO.setMessageType(messageType.getCode());

        // 根据类型找到要发送对象的id

        Long receiveUser = null;

        if (event.getMessageType().equals(AbstractMessage.MessageType.USER_RECEIVE_ACK)){
            receiveUser = messageBO.getTargetId();
        }
        // 找到对象的ip
        if (receiveUser != null) {
            Map<String, String> userDeviceIpMap = redisCacheService.getHashMap(RedisCacheConstants.ONLINE + receiveUser, String.class);
            String ip = userDeviceIpMap.getOrDefault(messageBO.getDevice().toString(), null);
            if (StringUtils.hasText(ip)) {
                // 发送ack
//                new BusinessAckMessage(messageBO, receiveUser + ":" )
                mqService.push2mq( messageBO,RabbitMQConstant.MQ_ACK_EXCHANGE, ip);
                return;
            }
        }
        // 如果找不到说明不在线，保存到收件箱
        msgWriter.saveDurably(messageBO, receiveUser);

    }
}

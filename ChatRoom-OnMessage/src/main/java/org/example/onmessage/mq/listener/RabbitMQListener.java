package org.example.onmessage.mq.listener;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.constant.RedisCacheConstants;
import org.example.onmessage.adapter.MessageAdapter;
import org.example.onmessage.constants.RabbitMQConstant;
import org.example.onmessage.handler.ws.GlobalWsMap;
import org.example.onmessage.push.PushWorker;
import org.example.onmessage.service.AckService;
import org.example.onmessage.service.MQService;
import org.example.onmessage.service.common.RedisCacheService;
import org.example.pojo.AbstractMessage;
import org.example.pojo.bo.BusinessAckMessage;
import org.example.pojo.bo.MessageBO;
import org.example.pojo.bo.MessageSendBo;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/10
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class RabbitMQListener {
    private final PushWorker pushWorker;
    private final AckService ackService;
    private final MQService mqService;
    private final RedisCacheService redisCacheService;
    //    @RabbitListener(
//            bindings = @QueueBinding(
//                    value = @Queue(value = RabbitMQConstant.WS_QUEUE),
//                    exchange = @Exchange(value = RabbitMQConstant.WS_EXCHANGE, type = ExchangeTypes.TOPIC),
//                    key = {"ws.#"}
//            )
//    )
    @Value("${spring.rabbitmq.listener.queues}")
    private String[] queueNames;

    @Bean
    public String[] queueNames() {
        return queueNames;
    }

    //    @RabbitListener(queues = {"#{queueNames}", RabbitMQConstant.DEFAULT_QUEUE})
    @RabbitListener(queues = "#{queueNames}")
    public void receive(Message message, Channel channel) throws IOException {
        byte[] body = message.getBody();

        MessageSendBo messageSendBo = JSON.parseObject(body, MessageSendBo.class);

        log.info("收到消息：{}", messageSendBo);
        if (doBusiness(messageSendBo)) {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        } else {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }

    @RabbitListener(queues = RabbitMQConstant.MQ_GROUP_QUEUE + "." + "#{queueNames}")
    public void receiveGroup(Message message, Channel channel) throws IOException {
        byte[] body = message.getBody();

        MessageSendBo messageSendBo = JSON.parseObject(body, MessageSendBo.class);

        log.info("收到消息：{}", messageSendBo);
        if (doBusiness(messageSendBo)) {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        } else {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }

    @RabbitListener(queues = RabbitMQConstant.MQ_ACK_QUEUE + "." + "#{queueNames}")
    public void receiveAck(Message message, Channel channel) throws IOException {
        byte[] body = message.getBody();

        BusinessAckMessage businessAckMessage = JSON.parseObject(body, BusinessAckMessage.class);

        log.info("收到消息：{}", businessAckMessage);
        if (doBusiness(businessAckMessage)) {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        } else {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }

    private boolean doBusiness(BusinessAckMessage businessAckMessage) {
        MessageBO message = businessAckMessage.getMessage();
        try {
            ackService.ack(message, businessAckMessage.getUserIds());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 消息处理
     *
     * @param messageSendBo 群聊消息对象，包括消息内容和需要发送的用户id
     * @return 是否处理成功
     */
    private boolean doBusiness(MessageSendBo messageSendBo) {
        try {
            pushWorker.push(messageSendBo);
            // todo：应该全部用户ack后再ack的，测试先直接ack
            BusinessAckMessage businessAckMessage = new BusinessAckMessage(messageSendBo.getMessage(), messageSendBo.getReceivers());
            String host = redisCacheService.getHashValue(RedisCacheConstants.ONLINE + messageSendBo.getMessage().getFromUserId(), messageSendBo.getMessage().getDevice().toString(), String.class);

            mqService.push2mq(businessAckMessage, RabbitMQConstant.MQ_ACK_EXCHANGE, host);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 私聊消息处理
     *
     * @param messageBO 消息内容
     * @return 是否处理成功
     */
    public Boolean doBusiness(MessageBO messageBO) {
        if (messageBO.getMessageType().equals(AbstractMessage.MessageType.SERVER_ACK.getCode())) {
            // todo：ACK
//            ClientMessageAck ack = ClientMessageAck
//                    .builder()
//                    .clientMessageId(messageBO.getClientMessageId())
//                    .device(messageBO.getDevice())
//                    .ackType(ClientMessageAck.AckType.SERVER_ACK.getCode())
//                    .isAck(true)
//                    .build();
//            GlobalWsMap.sendText(messageBO.getFromUserId(), JSON.toJSONString(ack));

            MessageBO businessAckMessage = MessageAdapter.getBusinessAckMessage(messageBO);
            GlobalWsMap.sendText(messageBO.getFromUserId(), JSON.toJSONString(businessAckMessage));
            return true;
        } else {
            try {
//                messageBuffer.handleMsg(wsMessageDTO)
                pushWorker.push2User(messageBO);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
//        if (messageBO.getMessageType().equals(AbstractMessage.MessageType.SINGLE.getCode())) {
//                // 单聊
//                if (GlobalWsMap.sendText(messageBO.getTargetId(), JSON.toJSONString(messageBO))){
//                    // TODO：离线消息保存
////                redisCacheService.saveOfflineMessage(wsMessageDTO.getTargetId(), wsMessageDTO);
//
//                    publishEventUtils.dealMessage(this, messageBO);
////                redisCacheService.deleteObject(RedisConstant.ACK + messageBO.getFromUserId() + ":" + messageBO.getId());
//                    return true;
//                }
//                return false;
//            } else if (messageBO.getMessageType().equals(AbstractMessage.MessageType.GROUP.getCode())) {
//                // 群聊
//                System.out.println("群聊消息：" + messageBO);
//            }
//
//        return true;
    }
}
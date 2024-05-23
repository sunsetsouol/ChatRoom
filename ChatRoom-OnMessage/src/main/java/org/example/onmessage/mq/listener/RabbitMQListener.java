package org.example.onmessage.mq.listener;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.onmessage.adapter.MessageAdapter;
import org.example.onmessage.constants.RabbitMQConstant;
import org.example.onmessage.handler.ws.GlobalWsMap;
import org.example.onmessage.route.MessageBuffer;
import org.example.pojo.AbstractMessage;
import org.example.pojo.dto.WsMessageDTO;
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
    private final MessageBuffer messageBuffer;
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
    @RabbitListener(queues = {"#{queueNames}", RabbitMQConstant.DEFAULT_QUEUE})
    public void receive(Message message, Channel channel) throws IOException {
        byte[] body = message.getBody();
        WsMessageDTO wsMessageDTO = JSON.parseObject(body, WsMessageDTO.class);

        log.info("收到消息：{}", wsMessageDTO);
        if (doBusiness(wsMessageDTO)) {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        } else {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }

    public Boolean doBusiness(WsMessageDTO wsMessageDTO) {
        if (wsMessageDTO.getMessageType().equals(AbstractMessage.MessageType.BUSINESS_ACK.getCode())){
            // ACK
//            ClientMessageAck ack = ClientMessageAck
//                    .builder()
//                    .clientMessageId(messageBO.getClientMessageId())
//                    .device(messageBO.getDevice())
//                    .ackType(ClientMessageAck.AckType.BUSINESS_ACK.getCode())
//                    .isAck(true)
//                    .build();
//            GlobalWsMap.sendText(messageBO.getFromUserId(), JSON.toJSONString(ack));
            WsMessageDTO businessAckMessage = MessageAdapter.getBusinessAckMessage(wsMessageDTO);
            GlobalWsMap.sendText(wsMessageDTO.getFromUserId(), JSON.toJSONString(businessAckMessage));
            return true;
        } else {
            try {
                messageBuffer.handleMsg(wsMessageDTO);
//                pushWorker.push(wsMessageDTO);
//                publishEventUtils.pushMessageAck(this, wsMessageDTO);
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
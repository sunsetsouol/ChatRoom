package org.example.onmessage.mq.service;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.onmessage.constants.RabbitMQConstant;
import org.example.onmessage.constants.RedisConstant;
import org.example.onmessage.entity.bo.MessageBO;
import org.example.onmessage.entity.dto.WsMessageDTO;
import org.example.onmessage.service.common.RedisCacheService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/10
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class MQService {
    private final RabbitTemplate rabbitTemplate;
    private final String EXCHANGE = RabbitMQConstant.WS_EXCHANGE;
    private final String ROUTINGKEY = RabbitMQConstant.WS_ROUTING_KEY;
    public void push2mq(WsMessageDTO wsMessageDTO) {
        Message message = MessageBuilder
                .withBody(JSON.toJSONString(wsMessageDTO).getBytes(StandardCharsets.UTF_8))
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .build();


        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        correlationData.getFuture().addCallback(
                result ->{
                    if (!result.isAck()){
                        log.warn("消息发送失败，交换机{}，路由键{},消息{}", EXCHANGE, ROUTINGKEY, message);
                        rabbitTemplate.convertAndSend(EXCHANGE, ROUTINGKEY, message, correlationData);
                    }
                },
                ex ->{
                    log.error("消息发送异常，交换机{}，路由键{},消息{}", EXCHANGE, ROUTINGKEY, message);
                }
        );
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTINGKEY, message, correlationData);
    }
}

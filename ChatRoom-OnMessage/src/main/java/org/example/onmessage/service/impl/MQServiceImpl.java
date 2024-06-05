package org.example.onmessage.service.impl;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.onmessage.service.MQService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/6/5
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MQServiceImpl implements MQService {
    private final RabbitTemplate rabbitTemplate;
    @Override
    public void push2mq(Object object, String exchange, String routingKey) {
        if (!StringUtils.hasText(routingKey)){
            return;
        }
        Message message = MessageBuilder
                .withBody(JSON.toJSONString(object).getBytes(StandardCharsets.UTF_8))
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .build();

        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        correlationData.getFuture().addCallback(
                result -> {
                    if (!result.isAck()) {
                        log.warn("消息发送失败，交换机{}，路由键{},消息{}", exchange, routingKey, message);
                        rabbitTemplate.convertAndSend(exchange, routingKey, message, correlationData);
                    }
                },
                ex -> {
                    log.error("消息发送异常，交换机{}，路由键{},消息{}", exchange, routingKey, message);
                }
        );
        rabbitTemplate.convertAndSend(exchange, routingKey, message, correlationData);
    }
}

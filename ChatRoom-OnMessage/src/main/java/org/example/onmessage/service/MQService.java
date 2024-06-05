package org.example.onmessage.service;

import org.springframework.amqp.core.Message;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/6/5
 */
public interface MQService {
    void push2mq(Object object, String exchange, String routingKey);
}

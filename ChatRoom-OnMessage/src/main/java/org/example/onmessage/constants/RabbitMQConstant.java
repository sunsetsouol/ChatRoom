package org.example.onmessage.constants;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/11
 */
public interface RabbitMQConstant {

    String WS_EXCHANGE = "ws_exchange";


    String MQ_GROUP_ROUTING_KEY = "ws.group";
    String IP_QUEUE = "ipQueue";
    String DEFAULT_QUEUE = "defaultQueue";
    String MQ_GROUP_EXCHANGE = "ws.group.exchange";
    String MQ_GROUP_QUEUE = "ws.group.queue";
    String MQ_ACK_EXCHANGE = "mq.ack.exchange";
    String MQ_ACK_QUEUE = "mq.ack.queue";
}

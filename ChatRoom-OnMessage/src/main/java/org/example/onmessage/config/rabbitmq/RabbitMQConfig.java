package org.example.onmessage.config.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.example.onmessage.constants.RabbitMQConstant;
import org.example.onmessage.publish.PublishEventUtils;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/1/27
 */
@Slf4j
@Configuration
public class RabbitMQConfig implements ApplicationContextAware {

    @Value("${spring.rabbitmq.listener.queues}")
    private String[] queueNames;

    @Bean
    public Exchange exchange(){
        return ExchangeBuilder.topicExchange(RabbitMQConstant.WS_EXCHANGE).durable(true).build();
    }

    @Bean(RabbitMQConstant.IP_QUEUE)
    public Queue queue(){
        return QueueBuilder.durable(queueNames[0]).build();
    }

    @Bean(RabbitMQConstant.DEFAULT_QUEUE)
    public Queue defaultQueue(){
        return QueueBuilder.durable(RabbitMQConstant.DEFAULT_QUEUE).build();
    }

    @Bean
    public Binding bindingSingle(@Qualifier(RabbitMQConstant.IP_QUEUE) Queue queue,  Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(queueNames[0]).noargs();
    }

    @Bean
    public Binding bindingGroup(@Qualifier(RabbitMQConstant.IP_QUEUE) Queue queue,  Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(RabbitMQConstant.MQ_GROUP_ROUTING_KEY).noargs();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // 获取RabbitTemplate
        RabbitTemplate rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
        // 设置ReturnCallback
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            // 交换机投递到队列失败，记录日志
            log.error("消息路由到队列失败，应答码{}，原因{}，交换机{}，路由键{},消息{}",
                    replyCode, replyText, exchange, routingKey, message);
            // 重发消息
            if (replyCode == 312){
                log.info("消息发送失败，失败code为312，重新发送消息");
                CompletableFuture.runAsync(() -> rabbitTemplate.convertAndSend(exchange, routingKey, message));
            }else {
                rabbitTemplate.convertAndSend(exchange, routingKey, message);
            }
        });
//        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
//            if (ack) {
//                log.info("消息发送成功:correlationData({}),ack({}),cause({})", correlationData, ack, cause);
//            } else {
//                log.error("消息发送失败:correlationData({}),ack({}),cause({})", correlationData, ack, cause);
//            }
//        });
    }
}

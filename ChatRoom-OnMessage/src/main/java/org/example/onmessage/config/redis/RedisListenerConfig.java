//package org.example.onmessage.config.redis;
//
//import org.example.onmessage.constants.RedisConstant;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnection;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.listener.PatternTopic;
//import org.springframework.data.redis.listener.RedisMessageListenerContainer;
//
///**
// * @author yinjunbiao
// * @version 1.0
// * @date 2024/5/13
// */
//@Configuration
//public class RedisListenerConfig {
//
//    @Autowired
//    private RedisExpireListener redisExpireListener;
//    @Autowired
//    private RedisAckListener redisAckListener;
//    @Bean
//    public RedisMessageListenerContainer container(RedisConnectionFactory redisConnectionFactory) {
//        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//        container.setConnectionFactory(redisConnectionFactory);
//        RedisConnection connection = container.getConnectionFactory().getConnection();
//        connection.setConfig("notify-keyspace-events", "KEA");
//        container.addMessageListener(redisExpireListener, new PatternTopic("__keyevent@2__:expired"));
//        container.addMessageListener(redisAckListener, new PatternTopic("__keyspace@2__:" + RedisConstant.ACK + "*"));
//        return container;
//    }
//}

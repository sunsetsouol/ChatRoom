package org.example.onmessage.config.redis;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/12
 */
@Component
public class RedisExpireListener implements MessageListener {


//    @Override
//    protected void doRegister(RedisMessageListenerContainer listenerContainer) {
//        listenerContainer.addMessageListener(this, new PatternTopic("__keyspace@2__:test expire"));
//    }



    @Override
    public void onMessage(Message message, byte[] pattern) {
        String key = new String(message.getBody());
        System.out.println("================================");
        System.out.println(key);
    }
}

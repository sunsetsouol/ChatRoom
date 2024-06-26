package org.example.onmessage;

import org.example.IdStrategy.IdGen.IdGeneratorStrategyFactory;
import org.example.onmessage.mq.service.MessageService;
import org.example.onmessage.service.common.RedisCacheService;
import org.example.onmessage.utils.JavaCVUtil;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/8
 */
@SpringBootTest
public class OnMessageApplicationTest {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private IdGeneratorStrategyFactory idGeneratorStrategyFactory;
    @Autowired
    private MessageService messageService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisCacheService redisCacheService;
    @Test
    public void contextLoads() throws IOException {
        String convert = JavaCVUtil.convert(new File("E:\\g\\Python\\code\\ftp\\client\\陈奕迅 - 岁月如歌.mp4"));
        System.out.println(convert);
//        DefaultRedisScript<List> sliding = new DefaultRedisScript<>();
//        sliding.setLocation(new ClassPathResource("sliding_window_limiter.lua"));
//        sliding.setResultType(List.class);
//        List<String > keys = new ArrayList<>();
//        keys.add("a");
//        keys.add(String.valueOf(System.currentTimeMillis()));
////        System.out.println(stringRedisTemplate.hasKey("a"));
//        List<?> result = stringRedisTemplate.execute(
//                sliding,
////                Collections.emptyList()
//                keys,
//                "10",
//                "10",
//                String.valueOf(System.currentTimeMillis())
//        );
////        List<?> result1 = (List<?>) result;
//        for (Object o : result) {
//            System.out.println(o);
//        }
//        System.out.println(result);
//        System.out.println(execute);
//        stringRedisTemplate.opsForHash().put("a","k","v");
//        stringRedisTemplate.opsForHash().delete("a","k");

//        Map<String , List<String >> map = new HashMap();
//        map.computeIfAbsent("a", k-> new ArrayList<>());
//        map.forEach((k,v)-> System.out.println("key:"+k+"\nvalue:"+v));
//        for (int i = 0; i < 10; i++) {
//            MessageBO messageBO = new MessageBO();
//            messageBO.setId(((long) i));
//            Message message = MessageBuilder.withBody(JSON.toJSONBytes(messageBO)).build();
//            rabbitTemplate.convertAndSend("ws_exchange", "10.33.48.46:8083", message);
//        }
//        stringRedisTemplate.opsForZSet().add("a","a",1);
//        stringRedisTemplate.opsForZSet().add("a","b",2);
//        stringRedisTemplate.opsForZSet().add("a","c",3);
//        stringRedisTemplate.opsForZSet().add("a","d",4);
//        Set<String> a = stringRedisTemplate.opsForZSet().reverseRange("a", 0, 2);
//        for (String s : a) {
//            System.out.println(a);
//        }
//        FileInputStream fileInputStream = new FileInputStream("D:\\博客\\github.JPEG");
//        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
//        byte[] bytes = new byte[1024];
//        FileOutputStream fileOutputStream = new FileOutputStream("D:\\博客\\github1.JPEG");
//        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
//        int end;
//        List<Byte> byteList = new ArrayList<>();
//        while ((end = bufferedInputStream.read(bytes)) != -1) {
////            bufferedOutputStream.write(bytes, 0 ,end);
//            for (int i = 0; i < end; i++) {
//                byteList.add(bytes[i]);
//            }
//        }
//        int subIndex = byteList.size() / 2;
//        Byte[] byteListArray1 = byteList.subList(0, subIndex + 1).toArray(new Byte[0]);
//        stringRedisTemplate.opsForValue().set("byteList", JSON.toJSONString(byteListArray1));
//        Byte[] byteListArray2 = byteList.subList(subIndex, byteList.size()).toArray(new Byte[0]);
//        stringRedisTemplate.opsForValue().set("byteList2", JSON.toJSONString(byteListArray2));
//        String byteList1 = stringRedisTemplate.opsForValue().get("byteList");
//        String byteList2 = stringRedisTemplate.opsForValue().get("byteList2");
//        Byte[] byteListArray11 = JSON.parseObject(byteList1, Byte[].class);
//        Byte[] byteListArray22 = JSON.parseObject(byteList2, Byte[].class);
//        byte[] bytes1 = new byte[byteListArray22.length + byteListArray11.length];
//        for (int i = 0; i < byteListArray11.length; i++) {
//            bytes1[i] = byteListArray11[i];
//        }
//        for (int i = 0; i < byteListArray22.length; i++) {
//            bytes1[i + byteListArray11.length] = byteListArray22[i];
//        }
//        bufferedOutputStream.write(bytes1);
//        bufferedOutputStream.flush();
//        bufferedOutputStream.close();
    }
}

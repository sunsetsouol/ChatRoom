package org.example.onmessage;

import com.alibaba.fastjson.JSON;
import org.example.IdStrategy.IdGen.IdGeneratorStrategyFactory;
import org.example.onmessage.mq.service.MQService;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    private MQService mqService;

    @Test
    public void contextLoads() throws IOException {
//        stringRedisTemplate.opsForZSet().add("a","a",1);
//        stringRedisTemplate.opsForZSet().add("a","b",2);
//        stringRedisTemplate.opsForZSet().add("a","c",3);
//        stringRedisTemplate.opsForZSet().add("a","d",4);
        Set<String> a = stringRedisTemplate.opsForZSet().reverseRange("a", 0, 2);
        for (String s : a) {
            System.out.println(a);
        }
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

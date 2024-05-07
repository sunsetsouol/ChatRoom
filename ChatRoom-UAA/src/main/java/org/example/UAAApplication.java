package org.example;

import org.example.feign.client.UserClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/4/28
 */
@SpringBootApplication
@EnableFeignClients(clients = {UserClient.class})
public class UAAApplication {
    public static void main(String[] args) {
        SpringApplication.run(UAAApplication.class, args);
    }
}
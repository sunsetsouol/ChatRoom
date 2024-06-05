package org.example.onmessage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/4/29
 */
@SpringBootApplication
@EnableScheduling
public class OnMessageApplication {
    public static void main(String[] args) {
        SpringApplication.run(OnMessageApplication.class, args);
    }
}

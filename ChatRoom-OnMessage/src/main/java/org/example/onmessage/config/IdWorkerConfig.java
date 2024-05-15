package org.example.onmessage.config;

import org.example.IdStrategy.IdGen.IdGeneratorStrategyFactory;
import org.example.IdStrategy.IdGen.Impl.SnowFlake;
import org.example.IdStrategy.IdGen.Impl.UUIDGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/12
 */
@Configuration
public class IdWorkerConfig {
    @Bean
    public IdGeneratorStrategyFactory idGeneratorStrategyFactory(){
        return new IdGeneratorStrategyFactory();
    }
    @Bean
    public SnowFlake snowFlake(){
        return new SnowFlake();
    }

    @Bean
    public UUIDGenerator uuidGenerator(){
        return new UUIDGenerator();
    }
}

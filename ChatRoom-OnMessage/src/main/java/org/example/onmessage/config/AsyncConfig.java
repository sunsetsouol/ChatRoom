package org.example.onmessage.config;

import lombok.extern.slf4j.Slf4j;
import org.example.onmessage.constants.ThreadPoolConstant;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/9
 */
@Configuration
@Slf4j
public class AsyncConfig implements AsyncConfigurer {
    // 核心线程池大小
    private int corePoolSize = 16;

    // 最大可创建的线程数
    private int maxPoolSize = 32;

    // 队列最大长度
    private int queueCapacity = 200;

    // 线程池维护线程所允许的空闲时间
    private int keepAliveSeconds = 30;

    /**
     * 常规业务线程池
     */
    @Bean(name = ThreadPoolConstant.COMMON_THREAD_POOL_NAME)
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(maxPoolSize);
        executor.setCorePoolSize(corePoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setThreadNamePrefix("async-");
        // 线程池对拒绝任务(无线程可用)的处理策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
    @Override
    public Executor getAsyncExecutor() {
        return threadPoolTaskExecutor();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> log.info("执行异步方法：{}错误，参数：{} 错误：{}", method.getName(), Arrays.toString(params), ex.getMessage()); // 在此演示中不是重点，请随意。
    }
}

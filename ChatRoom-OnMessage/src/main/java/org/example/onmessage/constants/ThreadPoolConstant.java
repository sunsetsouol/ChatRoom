package org.example.onmessage.constants;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/9
 */
public interface ThreadPoolConstant {
    /**
     * 线程池名称
     */
    String COMMON_THREAD_POOL_NAME = "threadPoolTaskExecutor";

    /**
     * 保存收件箱信息线程池名称
     */
    String MESSAGE_SAVE_THREAD_POOL_NAME = "messageSaveTaskExecutor";
    String WS_MESSAGE_THREAD_POOL_NAME = "wsMessageHandlerTaskExecutor";
}

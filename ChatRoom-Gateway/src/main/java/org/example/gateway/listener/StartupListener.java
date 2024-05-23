package org.example.gateway.listener;

import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.EventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/20
 */
@Component
@Slf4j
public class StartupListener implements ApplicationRunner {
    @Resource
    private NacosServiceManager nacosServiceManager;
    @Override
    public void run(ApplicationArguments args) throws Exception {

        NamingService namingService = nacosServiceManager.getNamingService();
        try {
            namingService.subscribe("onMessage", new EventListener() {
                @Override
                public void onEvent(com.alibaba.nacos.api.naming.listener.Event event) {
                    log.info("监听nacos的服务实例变化情况: {}", JSON.toJSONString(event));
                }
            });
        } catch (NacosException e) {
            log.error("监听nacos的服务实例变化情况失败", e);
        }
    }
}

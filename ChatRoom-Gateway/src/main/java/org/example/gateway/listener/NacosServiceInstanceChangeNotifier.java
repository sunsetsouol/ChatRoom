package org.example.gateway.listener;

import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.event.InstancesChangeEvent;
import com.alibaba.nacos.common.notify.Event;
import com.alibaba.nacos.common.notify.NotifyCenter;
import com.alibaba.nacos.common.notify.listener.Subscriber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.constant.GlobalConstants;
import org.example.constant.RedisCacheConstants;
import org.example.gateway.hashring.*;
import org.example.gateway.service.common.RedisCacheService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/20
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class NacosServiceInstanceChangeNotifier extends Subscriber<InstancesChangeEvent> {

    private final HashRingUtil hashRingUtil;

    @PostConstruct
    public void init() {
        NotifyCenter.registerSubscriber(this);
    }
    @Override
    public void onEvent(InstancesChangeEvent instancesChangeEvent) {
        if (instancesChangeEvent.getServiceName().equals(GlobalConstants.LISTEN_WS_SERVICE_NAME)) {
            List<Instance> hosts = instancesChangeEvent.getHosts();

            List<RealNode> nodes = getNodes(hosts);
            // 更新哈希环
            hashRingUtil.updateHashRing(nodes);
            hashRingUtil.freshToRedis();
        }

    }

    private List<RealNode> getNodes(List<Instance> hosts) {
        List<RealNode> nodes = new ArrayList<>();
        for (Instance host : hosts) {
            RealNode realNode = new RealNode(host.getIp() + ":" + host.getPort());
            nodes.add(realNode);
        }
        return nodes;
    }

    @Override
    public Class<? extends Event> subscribeType() {
        return InstancesChangeEvent.class;
    }
}

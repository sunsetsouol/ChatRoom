package org.example.gateway.config;


import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import org.example.constant.RedisCacheConstants;
import org.example.gateway.hashring.HashRingUtil;
import org.example.gateway.hashring.Node;
import org.example.gateway.hashring.RealNode;
import org.example.gateway.hashring.VirtualNode;
import org.example.gateway.service.common.RedisCacheService;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/13
 */
@Component
@RequiredArgsConstructor
public class HashRingListener implements MessageListener {

    private final RedisCacheService redisCacheService;
    private final HashRingUtil hashRingUtil;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        Map<Long, JSONObject> ring = redisCacheService.getCacheObject(RedisCacheConstants.HASH_RING, Map.class);
        List<RealNode> serviceNodes = new ArrayList<>();
        if (Objects.nonNull(ring)) {
            for (Map.Entry<Long, JSONObject> ringEntrySet : ring.entrySet()) {
                VirtualNode<RealNode> virtualNode = VirtualNode.fromJSON(ringEntrySet.getValue());
                RealNode physicalNode = virtualNode.getPhysicalNode();
                serviceNodes.add(physicalNode);
            }
        }
        hashRingUtil.updateHashRing(serviceNodes);

    }
}

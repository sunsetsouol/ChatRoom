package org.example.gateway.hashring;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.constant.GlobalConstants;
import org.example.constant.RedisCacheConstants;
import org.example.gateway.service.common.RedisCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/20
 */
@Component
@RequiredArgsConstructor
@Data
public class HashRingUtil {
    private ConsistentHashRouter consistentHashRouter;
    @Autowired
    private RedisCacheService redisCacheService;
    public void updateHashRing(List<RealNode> nodes) {
        // 更新哈希环
        this.consistentHashRouter = new ConsistentHashRouter<>(nodes, GlobalConstants.VIRTUAL_COUNT);
    }


    public void freshToRedis() {
        // todo： lua保证原子性
        redisCacheService.deleteObject(RedisCacheConstants.HASH_RING);
        redisCacheService.setCacheObject(RedisCacheConstants.HASH_RING, consistentHashRouter.getRing());

    }
}

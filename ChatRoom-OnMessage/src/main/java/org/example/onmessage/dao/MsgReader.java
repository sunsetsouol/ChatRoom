package org.example.onmessage.dao;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import org.example.onmessage.entity.bo.MessageBO;
import org.example.onmessage.service.common.RedisCacheService;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/10
 */
@Component
@RequiredArgsConstructor
public class MsgReader {
    private final RedisCacheService redisCacheService;

    // TODO：细分一点，不要全用这个函数
    public <T> List<T> getWindowsMsg(String key, long min, long max, long offset, long bufferSize, Class<T> tclass) {
        Set<ZSetOperations.TypedTuple<String>> zget = redisCacheService.zget(key, min, max, offset, bufferSize, tclass);
        return zget.stream().map(typedTuple -> JSON.parseObject(typedTuple.getValue(), tclass)).collect(Collectors.toList());
    }

    public<T> List<T> getMsg(String key, Long bufferSize, Class<T> tclass) {
        Set<ZSetOperations.TypedTuple<String>> zget = redisCacheService.zget(key, 0, Long.MAX_VALUE, 0, bufferSize, tclass);
        return zget.stream().map(typedTuple -> JSON.parseObject(typedTuple.getValue(), tclass)).collect(Collectors.toList());
    }


    public <T> boolean hasMsg(String key, Long globalMessageId, Class<T> tClass) {
        Set<T> zget = redisCacheService.zget(key,  globalMessageId, tClass);
        return zget != null && zget.size() > 0;
    }
}

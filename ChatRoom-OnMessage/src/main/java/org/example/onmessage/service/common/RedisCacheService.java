package org.example.onmessage.service.common;

import com.alibaba.fastjson.JSON;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/8
 */
@Component
public class RedisCacheService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 设置redis键值对
     *
     * @param key   redis键
     * @param value redis值
     */
    public void setCacheObject(final String key, final Object value) {
        stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(value));
    }

    /**
     * 设置set集合的键值对
     *
     * @param key   redis键
     * @param value redis值
     */
    public void setCacheObjectSet(final String key, final Object value) {
        stringRedisTemplate.opsForSet().add(key, JSON.toJSONString(value));
    }

    /**
     * 设置redis键值对及其过期时间
     *
     * @param key      redis键
     * @param value    redis值
     * @param time     过期时间
     * @param timeUnit 时间单位
     */
    public void setCacheObject(final String key, final Object value, final Integer time, final TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(value), time, timeUnit);
    }

    /**
     * redis中如果不存在该键值对则设置
     *
     * @param key   redis键
     * @param value redis值
     * @return true=设置成功；false=设置失败
     */
    public Boolean setCacheObjectIfAbsent(final String key, final Object value) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, JSON.toJSONString(value));
    }


    /**
     * redis中如果不存在该键值对则设置并设置过期时间
     *
     * @param key   redis键
     * @param value redis值
     * @return true=设置成功；false=设置失败
     */
    public Boolean setCacheObjectIfAbsent(final String key, final Object value, final Integer time, final TimeUnit timeUnit) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, JSON.toJSONString(value), time, timeUnit);
    }

    /**
     * redis中如果不存在该键值对则设置
     *
     * @param key  redis键
     * @param time 过期时间
     * @param unit 时间单位
     * @return true=设置成功；false=设置失败
     */
    public Boolean expire(final String key, final long time, final TimeUnit unit) {
        return stringRedisTemplate.expire(key, time, unit);
    }

    /**
     * 获得缓存的对象
     *
     * @param key redis键
     * @return redis值
     */
    public <T> T getCacheObject(final String key, Class<T> type) {
        String json = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        return JSON.parseObject(json, type);
    }

    /**
     * 删除单个对象
     *
     * @param key redis键
     */
    public void deleteObject(final String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 缓存Set
     *
     * @param key   缓存键值
     * @param value 缓存的数据
     * @return 缓存数据的对象
     */
    public <T> long setCacheSet(final String key, final Object value) {
        Long count = stringRedisTemplate.opsForSet().add(key, JSON.toJSONString(value));
        return count == null ? 0 : count;
    }


    /**
     * 判断key-set中是否存在value
     *
     * @param key   缓存键值
     * @param value 缓存的数据
     * @return true=存在；false=不存在
     */
    public Boolean containsCacheSet(final String key, final Object value) {
        return stringRedisTemplate.opsForSet().isMember(key, JSON.toJSONString(value));
    }

    /**
     * 获取sst大小
     * @param key 键
     * @return  大小
     */
    public Long getSetSize(final String key) {
        return stringRedisTemplate.opsForSet().size(key);
    }

    /**
     * 获取key的过期时间
     *
     * @param key 缓存键值
     * @return key 过期时间
     */
    public Long getExpireTime(final String key) {
        return stringRedisTemplate.opsForValue().getOperations().getExpire(key);
    }


    /**
     * 自增
     *
     * @param key redis键
     */
    public void increment(final String key) {
        stringRedisTemplate.opsForValue().increment(key);
    }

    /**
     * 自增
     *
     * @param key redis键
     */
    public Long incrementAndGet(final String key) {
        return stringRedisTemplate.opsForValue().increment(key);
    }

    /**
     * 增加给定值
     *
     * @param key  redis键
     * @param incr 增量
     */
    public void increment(final String key, final long incr) {
        stringRedisTemplate.opsForValue().increment(key, incr);
    }


    /**
     * 自减
     *
     * @param key redis键
     * @return 自减后的值
     */
    public Long decrement(final String key) {
        return stringRedisTemplate.opsForValue().decrement(key);
    }

    /**
     * lua原子自减（先查后改原子操作）
     *
     * @param key redis键
     * @return 自减后的值
     */
    public long luaAtomicDecr(final String key) {
        RedisScript<Long> redisScript = new DefaultRedisScript<>(LuaAtomicDecrScript(), Long.class);
        Number n = stringRedisTemplate.execute(redisScript, Collections.singletonList(key));
        if (n == null) {
            return -1;
        }
        return n.longValue();
    }


    /**
     * 向key队列左插数据value
     *
     * @param key   队列key
     * @param value 数据
     */
    public void leftPushValue(final String key, final String value) {
        stringRedisTemplate.opsForList().leftPushIfPresent(key, value);
    }


    /**
     * 从key队列右移value
     *
     * @param key 队列key
     */
    public void rightPopValue(final String key) {
        stringRedisTemplate.opsForList().rightPop(key);
    }

    /**
     * 从key队列中移除count个值为value的元素
     *
     * @param key   队列key
     * @param value 值
     * @param count 数量
     * @return 成功移除的数量
     */
    public Long removeListValue(final String key, final String value, final long count) {
        return stringRedisTemplate.opsForList().remove(key, count, value);
    }


    /**
     * 从key哈希表中存hashKey-hashValue
     *
     * @param key       redis键
     * @param hashKey   哈希key
     * @param hashValue 哈希value
     * @return 是否设置成功
     */
    public Boolean putHashKeyIfAbsent(final String key, final String hashKey, final Object hashValue) {
        return stringRedisTemplate.opsForHash().putIfAbsent(key, hashKey, JSON.toJSONString(hashValue));
    }

    /**
     * 从key哈希表中存hashKey-hashValue
     *
     * @param key       redis键
     * @param hashKey   哈希key
     * @param hashValue 哈希value
     */
    public void putHashKey(final String key, final String hashKey, final Object hashValue) {
        stringRedisTemplate.opsForHash().put(key, hashKey, JSON.toJSONString(hashValue));
    }

    /**
     * 从key哈希表中存hashKey-hashValue
     * @param key       redis键
     * @param hashMap   hash map
     */
    public void putAllHash(final String key, Map<String, String > hashMap) {
        stringRedisTemplate.opsForHash().putAll(key, hashMap);
    }

    /**
     * 从key哈希表中存hashKey-hashValue
     * @param key       redis键
     * @param hashKey   hash key
     * @param tClass    hash class
     * @return  hash value
     * @param <T>   hash class
     */
    public<T> T getHashValue(final String key, final String hashKey, Class<T> tClass) {
        String json = (String) stringRedisTemplate.opsForHash().get(key, hashKey);
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        return JSON.parseObject(json, tClass);
    }


    /**
     * 移除hash表中元素
     *
     * @param key     redis键
     * @param hashKey 哈希key
     */
    public void removeHashKey(final String key, final String hashKey) {
        stringRedisTemplate.opsForHash().delete(key, hashKey);
    }

    /**
     * 向有序集合ZSet中插入元素
     *
     * @param key     redis键
     * @param zSetKey zset值
     * @param score   分数
     * @return 是否插入成功
     */
    public Boolean addZSet(final String key, final Object zSetKey, final Long score) {
        return stringRedisTemplate.opsForZSet().add(key, JSON.toJSONString(zSetKey), score);
    }

    /**
     * 移除ZSet中元素
     *
     * @param key     redis键
     * @param zSetKey zset值
     */
    public void removeZSetKey(final String key, final Object zSetKey) {
        Long remove = stringRedisTemplate.opsForZSet().remove(key, JSON.toJSONString(zSetKey));
    }

    /**
     * 根据成绩范围移除ZSet中元素
     * @param key redis键
     * @param min 最小值
     * @param max 最大值
     */
    public void removeZSetByScore(final String key, final double min, final double max) {
        stringRedisTemplate.opsForZSet().removeRangeByScore(key, min, max);
    }


    /**
     * 获取ZSet中排名第一的元素
     *
     * @param key  redis键
     * @param type 类型
     * @return value
     */
    public <T> T getFirstZSetValue(final String key, Class<T> type) {
        Set<String> range = stringRedisTemplate.opsForZSet().range(key, 0, 0);
        if (!CollectionUtils.isEmpty(range)) {
            return JSON.parseObject(range.iterator().next(), type);
        }
        return null;
    }

    /**
     * ZSet中是否存在某个值
     *
     * @param key     redis键
     * @param zSetKey zset值
     * @return 是否存在
     */
    public Boolean zSetContains(final String key, final Object zSetKey) {
        return stringRedisTemplate.opsForZSet().rank(key, JSON.toJSONString(zSetKey)) != null;
    }

    /**
     * 获取ZSet中所有值
     *
     * @param key redis键
     * @return ZSet中所有值
     */
    public List<String> getAllZSetValue(final String key) {
        Set<String> set = stringRedisTemplate.opsForZSet().range(key, 0, Integer.MAX_VALUE);
        if (ObjectUtils.isEmpty(set)) {
            return null;
        }
        return new ArrayList<>(set);
    }


    /**
     * 获取ZSet长度
     *
     * @param key redis键
     * @return 长度
     */
    public Long getZSetSize(final String key) {
        return stringRedisTemplate.opsForZSet().size(key);
    }

    /**
     * 获取ZSet中某个值的排名
     *
     * @param key     redis键
     * @param zSetKey zset值
     * @return 排名
     */
    public Long getZSetRank(final String key, final Object zSetKey) {
        return stringRedisTemplate.opsForZSet().rank(key, JSON.toJSONString(zSetKey));
    }


    /**
     * 获取hash表大小
     *
     * @param key redis键
     * @return hash表大小
     */
    public Long getHashSize(final String key) {
        return stringRedisTemplate.opsForHash().size(key);
    }


    /**
     * lua原子脚本
     */
    private String LuaAtomicDecrScript() {
        return "local n" +
                "\nn = redis.call('get',KEYS[1])" +
                "\nif n and tonumber(n) < 0 then" +
                "\nreturn n;" +
                "\nend" +
                "\nn = redis.call('decr',KEYS[1])" +
                "\nreturn n";
    }

    /**
     * 获取有序集合中指定范围的元素。
     * @param key Redis键。
     * @param startRange 开始索引。
     * @param endRange 结束索引。
     * @return 集合中的元素集。
     */
    public Set<String> getElementsInRange(String key, long startRange, long endRange) {
        ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();
        return zSetOps.reverseRange(key, startRange, endRange);
    }

    public <T> Set<ZSetOperations.TypedTuple<String>> zget(String key, long min, long max, long offset, long count, Class<T> tClass) {
        Set<ZSetOperations.TypedTuple<String>> set = stringRedisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max, offset, count);
        if (CollectionUtils.isEmpty(set)) {
            return new LinkedHashSet<>();
        }
        return set;
    }

    public <T> List<T> hGet(String key, Class<T> tClass) {
        return redisTemplate.opsForHash().values(key);
    }

    public <T> T zget(String key, Long score, Class<T> tClass) {
        Set<ZSetOperations.TypedTuple<String>> set = stringRedisTemplate.opsForZSet().rangeByScoreWithScores(key, score, score);
        if (CollectionUtils.isEmpty(set)) {
            return null;
        }
        return set.stream().findFirst().map(o -> JSON.parseObject(o.getValue(), tClass)).get();
    }

    public <T> Set<T> gAllSet(String key, Class<T> tClass) {
        Set<String> members = stringRedisTemplate.opsForSet().members(key);
        if (CollectionUtils.isEmpty(members)) {
            return new LinkedHashSet<>();
        }
        return members.stream().map(o -> JSON.parseObject(o, tClass)).collect(Collectors.toSet());
    }

    public Double getFirstZSetScore(String key) {
        Set<ZSetOperations.TypedTuple<String>> firstTypedTuple = stringRedisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, 0, Long.MAX_VALUE, 0, 1);
        if (CollectionUtils.isEmpty(firstTypedTuple)) {
            return null;
        }
        return firstTypedTuple.iterator().next().getScore();
    }
}


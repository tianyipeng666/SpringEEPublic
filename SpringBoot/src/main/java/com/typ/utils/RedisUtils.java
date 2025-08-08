package com.typ.utils;


import com.typ.config.RedisConfig;
import com.typ.constants.RedisConstants;
import com.typ.listener.RedisMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static com.typ.constants.GatewayConfConstants.*;
import static com.typ.constants.RedisConstants.LOCK_PREFIX;


@Component
@Slf4j
public final class RedisUtils {

    private RedisUtils() {
    }

    private static String redisKeyPrefix;

    @Value("${spring.redis.prefix}")
    public void setRedisKeyPrefix(String param) {
        this.redisKeyPrefix = param;
    }

    private static Integer redisMaxScanCount;

    @Value("${spring.redis.maxScanCount:256}")
    public void setRedisMaxScanCount(Integer param) {
        this.redisMaxScanCount = param;
    }

    private static Integer redisErrorTime;

    @Value("${error.unlockTime}")
    public void setRedisErrorTime(Integer param) {
        this.redisErrorTime = param;
    }

    private static Integer retrySleepTime;

    @Value("${error.retrySleepTime:1000}")
    public void setRetrySleepTime(Integer param) {
        this.retrySleepTime = param;
    }

    private static Random random = new Random();

    public static void withConnection(BiConsumer<RedisTemplate, String> f, String key) {
        try {
            lockKey(key, false);
            f.accept(RedisConfig.getRedisTemplate(), key);
        } catch (Exception e) {
            log.error(DEFAULT_REDIS_GET_MESSAGE_ERROR_INFO, addKeyPrefix(key), e.getMessage());
            throw new RuntimeException(e);
        } finally {
            unlockKey(key);
        }
    }

    public static <T> T doWithConnection(BiFunction<RedisTemplate, String, T> f, String key) {
        return doWithConnection(f, key, false, true);
    }

    public static <T> T doWithConnectionWithoutLock(BiFunction<RedisTemplate, String, T> f, String key) {
        return doWithConnection(f, key, false, false);
    }

    public static <T> T doWithConnection(BiFunction<RedisTemplate, String, T> f, String key, Boolean forceLock, Boolean needLock) {
        try {
            if (needLock) {
                lockKey(key, forceLock);
            }
            return f.apply(RedisConfig.getRedisTemplate(), key);
        } catch (Exception e) {
            log.error(DEFAULT_REDIS_GET_MESSAGE_ERROR_INFO, addKeyPrefix(key), e.getMessage());
            throw new RuntimeException(e);
        } finally {
            if (needLock) {
                unlockKey(key);
            }
        }
    }

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    public static void expire(String key, long time) {
        withConnection((redisTemplate, k) -> redisTemplate.expire(k, time, TimeUnit.SECONDS), addKeyPrefix(key));
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public static long getExpire(String key) {
        return doWithConnection((redisTemplate, k) -> redisTemplate.getExpire(k, TimeUnit.SECONDS), addKeyPrefix(key));
    }

    public static boolean setnx(String key, String value, long outTime) {
        return doWithConnection((redisTemplate, k) ->
                redisTemplate.opsForValue().setIfAbsent(k, value, outTime, TimeUnit.SECONDS), addKeyPrefix(key));
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public static boolean hasKey(String key) {
        return doWithConnection((redisTemplate, k) -> redisTemplate.hasKey(k), addKeyPrefix(key));
    }

    /**
     * 删除
     *
     * @param keys 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public static void del(String... keys) {
        if (!ObjectUtils.isEmpty(keys)) {
            if (keys.length == 1) {
                withConnection((redisTemplate, k) -> redisTemplate.delete(k), addKeyPrefix(keys[0]));
            } else {
                List<String> delKeys = Arrays.stream(keys)
                        .map(k -> addKeyPrefix(k)).collect(Collectors.toList());
                withConnection((redisTemplate, k) -> redisTemplate.delete(delKeys), null);
            }
        }
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public static Object get(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        return doWithConnectionWithoutLock((redisTemplate, k) -> redisTemplate.opsForValue().get(k), addKeyPrefix(key));
    }

    public static int getToInt(String key) {
        if (StringUtils.isEmpty(key)) {
            return 0;
        }
        return doWithConnectionWithoutLock((redisTemplate, k) -> {
            Object redisValue = redisTemplate.opsForValue().get(k);
            if (ObjectUtils.isEmpty(redisValue)) {
                return 0;
            } else {
                try {
                    return Integer.parseInt(redisValue.toString());
                } catch (NumberFormatException e) {
                    log.warn("redis value is not a number, key: {}, value: {}", k, redisValue);
                }
                return 0;
            }
        }, addKeyPrefix(key));
    }

    public static boolean lockKey(String key) {
        return lockKey(key, false);
    }

    public static boolean lockKey(String key, Boolean forceLock) {
        if (StringUtils.isEmpty(key)) {
            log.trace("key is empty");
            return false;
        }
        Long startTime = System.currentTimeMillis();
        Long currentTime = startTime;
        Long endTime = currentTime + redisErrorTime;
        Integer index = 0;
        Integer sleepTime = 0;
        String lockKey = recoverKeyPrefix(key);
        log.debug("lock key: {}, forceLock: {} come in loop", lockKey, forceLock);
        while (true) {
            // 调整上锁时间为20秒
            if (RedisConfig.getRedisTemplate().opsForValue().setIfAbsent(
                    String.format(DEFAULT_KEY_CHAIN_FORMAT, LOCK_PREFIX, lockKey), "a", NUM_30_CONSTANT, TimeUnit.SECONDS)) {
                log.debug("lock key: {}, forceLock: {}, costTime: {} ms, out of loop", lockKey, forceLock, System.currentTimeMillis() - startTime);
                return true;
            }

            try {
                sleepTime = random.nextInt(NUM_10_CONSTANT);
                Thread.sleep(sleepTime);
                index++;
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }

            currentTime = System.currentTimeMillis();
            if (forceLock) {
                if (endTime - currentTime < NUM_30_CONSTANT) {
                    log.debug("lock timeout, incr lock time, continue wait ...");
                    endTime = currentTime + redisErrorTime;
                    RedisConfig.getRedisTemplate().expire(
                            String.format(DEFAULT_KEY_CHAIN_FORMAT, LOCK_PREFIX, lockKey), NUM_30_CONSTANT, TimeUnit.SECONDS);
                }
            } else {
                if (currentTime >= endTime) {
                    log.debug("lock failed, execute logic, key: {}, forceLock: false, costTime: {} ms, out of loop",
                            lockKey, System.currentTimeMillis() - startTime);
                    return false;
                }
            }
        }
    }

    public static void unlockKey(String key) {
        if (StringUtils.isEmpty(key)) {
            log.trace("key is empty");
            return;
        }
        RedisConfig.getRedisTemplate().delete(String.format(DEFAULT_KEY_CHAIN_FORMAT, LOCK_PREFIX, recoverKeyPrefix(key)));
        log.debug("unlock key {}", recoverKeyPrefix(key));
    }

    public static Long incrQueueRemainExecutors(String mobiusKey) {
        return doWithConnectionWithoutLock((redisTemplate, k) -> {
            boolean setValue = redisTemplate.opsForValue().setIfAbsent(k, 1);
            if (setValue) {
                log.debug("incr after redis key: {}, value: 1", k);
                return 1L;
            }
            Long incr = redisTemplate.opsForValue().increment(k);
            log.debug("incr after redis key: {}, value: {}", k, incr);
            return incr;
        }, addKeyPrefix(mobiusKey));
    }

    public static void decrQueueRemainExecutors(String mobiusKey) {
        decrQueueRemainExecutors(mobiusKey, 1);
    }

    public static void decrQueueRemainExecutors(String mobiusKey, long delta) {
        doWithConnectionWithoutLock((redisTemplate, k) -> {
            Long incr = redisTemplate.opsForValue().decrement(k, delta);
            log.debug("decr after redis key: {}, value: {}", k, incr);
            return incr;
        }, recoverKeyPrefix(mobiusKey));
    }

    /**
     * 获取某个队列或mobius剩余的并发数
     *
     * @param confKey   键, 配置的队列并发数的key
     * @param actualKey 键, 实际运行队列并发数的key
     * @return 队列并发剩余值
     */
    public static int getRemainTaskNum(String confKey, String actualKey) {
        int confValue = getToInt(confKey);
        if (confValue < 1) {
            return 0;
        }
        int actualValue = getToInt(actualKey);
        log.debug("getRemainTaskNum confKey: {}, confNum: {}, actualKey: {}, actualNum: {}",
                confKey, confValue, actualKey, actualValue);
        return confValue - actualValue > 0 ? confValue - actualValue : 0;
    }


    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public static void set(String key, Object value) {
        withConnection((redisTemplate, k) -> redisTemplate.opsForValue().set(k, value), addKeyPrefix(key));
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public static void set(String key, Object value, long time) {
        if (time > 0) {
            withConnection((redisTemplate, k) -> redisTemplate.opsForValue().set(k, value, time, TimeUnit.SECONDS), addKeyPrefix(key));
        } else {
            set(key, value);
        }
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */
    public static long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return doWithConnection((redisTemplate, k) -> {
            boolean setValue = redisTemplate.opsForValue().setIfAbsent(k, 1);
            if (setValue) {
                return 1L;
            }
            return redisTemplate.opsForValue().increment(k, delta);
        }, addKeyPrefix(key));
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return
     */
    public static long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return doWithConnection((redisTemplate, k) -> redisTemplate.opsForValue().increment(k, -delta), addKeyPrefix(key));
    }

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public static Object hget(String key, String item) {
        return doWithConnection((redisTemplate, k) -> redisTemplate.opsForHash().get(k, item), addKeyPrefix(key));
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public static Map<Object, Object> hmget(String key) {
        return doWithConnection((redisTemplate, k) -> redisTemplate.opsForHash().entries(k), addKeyPrefix(key));
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public static void hmset(String key, Map<String, Object> map) {
        withConnection((redisTemplate, k) -> redisTemplate.opsForHash().putAll(k, map), addKeyPrefix(key));
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public static void hmset(String key, Map<String, Object> map, long time) {
        withConnection((redisTemplate, k) -> {
            redisTemplate.opsForHash().putAll(k, map);
            if (time > 0) {
                redisTemplate.expire(k, time, TimeUnit.SECONDS);
            }
        }, addKeyPrefix(key));
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public static void hset(String key, String item, Object value) {
        withConnection((redisTemplate, k) -> redisTemplate.opsForHash().put(k, item, value), addKeyPrefix(key));
    }

    public static void hset(String key, String item, String value) {
        withConnection((redisTemplate, k) -> redisTemplate.opsForHash().put(k, item, value), addKeyPrefix(key));
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public static void hset(String key, String item, Object value, long time) {
        withConnection((redisTemplate, k) -> {
            redisTemplate.opsForHash().put(k, item, value);
            if (time > 0) {
                redisTemplate.expire(k, time, TimeUnit.SECONDS);
            }
        }, addKeyPrefix(key));
    }


    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public static void hdel(String key, Object... item) {
        doWithConnection((redisTemplate, k) -> redisTemplate.opsForHash().delete(k, item), addKeyPrefix(key));
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public static boolean hHasKey(String key, String item) {
        return doWithConnection((redisTemplate, k) -> redisTemplate.opsForHash().hasKey(k, item), addKeyPrefix(key));
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return
     */
    public static double hincr(String key, String item, double by) {
        return doWithConnection((redisTemplate, k) -> redisTemplate.opsForHash().increment(k, item, by), addKeyPrefix(key));
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return
     */
    public static double hdecr(String key, String item, double by) {
        return doWithConnection((redisTemplate, k) -> redisTemplate.opsForHash().increment(k, item, -by), addKeyPrefix(key));
    }

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */
    public static Set<Object> sGet(String key) {
        return doWithConnection((redisTemplate, k) -> redisTemplate.opsForSet().members(k), addKeyPrefix(key));
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public static boolean sHasKey(String key, Object value) {
        return doWithConnection((redisTemplate, k) -> redisTemplate.opsForSet().isMember(k, value), addKeyPrefix(key));
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public static long sSet(String key, Object... values) {
        return doWithConnection((redisTemplate, k) -> redisTemplate.opsForSet().add(k, values), addKeyPrefix(key));
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public static long sSetAndTime(String key, long time, Object... values) {
        return doWithConnection((redisTemplate, k) -> {
            Long count = redisTemplate.opsForSet().add(k, values);
            if (time > 0) {
                redisTemplate.expire(k, time, TimeUnit.SECONDS);
            }
            return count;
        }, addKeyPrefix(key));
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    public static long sGetSetSize(String key) {
        return doWithConnection((redisTemplate, k) -> redisTemplate.opsForSet().size(k), addKeyPrefix(key));
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public static long setRemove(String key, Object... values) {
        return doWithConnection((redisTemplate, k) -> redisTemplate.opsForSet().remove(k, values), addKeyPrefix(key));
    }

    /**
     * 将数据放入zset缓存
     *
     * @param key   键
     * @param value 值
     * @param score 排序号
     */
    public static void zSet(String key, Object value, double score) {
        doWithConnection((redisTemplate, k) -> redisTemplate.opsForZSet().add(k, value, score), addKeyPrefix(key));
    }

    /**
     * 返回zset数据个数
     *
     * @param key
     * @return
     */
    public static Long zCard(String key) {
        return doWithConnection((redisTemplate, k) -> redisTemplate.opsForZSet().zCard(k), addKeyPrefix(key));
    }

    /**
     * 删除键为key   start < score < end的元素 返回删除个数
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public static Long removeRangeByScore(String key, Double start, Double end) {
        return doWithConnection((redisTemplate, k) -> redisTemplate.opsForZSet().removeRangeByScore(k, start, end), addKeyPrefix(key));
    }

    /**
     * 根据score查询集合元素 从小到大排序
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public static Set<DefaultTypedTuple> rangeWithScores(String key, Long start, Long end) {
        return doWithConnection((redisTemplate, k) -> redisTemplate.opsForZSet().rangeWithScores(k, start, end), addKeyPrefix(key));
    }


    /**
     * 根据score查询集合元素与score 从小到大排序
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public static Set<DefaultTypedTuple> zRangeByScoreWithScores(String key, Double start, Double end) {
        return doWithConnection((redisTemplate, k) -> redisTemplate.opsForZSet().rangeByScoreWithScores(k, start, end), addKeyPrefix(key));
    }

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return
     */
    public static List<Object> lGet(String key, long start, long end) {
        return doWithConnection((redisTemplate, k) -> redisTemplate.opsForList().range(k, start, end), addKeyPrefix(key));
    }

    //  从左边获取一个个元素
    public static Object lGet(String key) {
        return doWithConnection((redisTemplate, k) -> redisTemplate.opsForList().leftPop(k), addKeyPrefix(key));
    }

    public static Object lpush(String key, Object value) {
        return doWithConnection((redisTemplate, k) -> redisTemplate.opsForList().leftPush(k, value), addKeyPrefix(key));
    }

    public static Object lpushWithoutPrefixKey(String key, Object value) {
        return doWithConnection((redisTemplate, k) -> redisTemplate.opsForList().leftPush(k, value), key);
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public static long lGetListSize(String key) {
        return doWithConnection((redisTemplate, k) -> redisTemplate.opsForList().size(k), addKeyPrefix(key));
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public static Object lGetIndex(String key, long index) {
        return doWithConnection((redisTemplate, k) -> redisTemplate.opsForList().index(k, index), addKeyPrefix(key));
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static void lSet(String key, Object value) {
        doWithConnection((redisTemplate, k) -> redisTemplate.opsForList().leftPush(k, value), addKeyPrefix(key));
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public static void lSet(String key, Object value, long time) {
        withConnection((redisTemplate, k) -> {
            redisTemplate.opsForList().leftPush(k, value);
            if (time > 0) {
                redisTemplate.expire(k, time, TimeUnit.SECONDS);
            }
        }, addKeyPrefix(key));
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static void lSet(String key, List<Object> value) {
        doWithConnection((redisTemplate, k) -> redisTemplate.opsForList().leftPushAll(k, value), addKeyPrefix(key));
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public static void lSet(String key, List<Object> value, long time) {
        withConnection((redisTemplate, k) -> {
            redisTemplate.opsForList().leftPushAll(k, value);
            if (time > 0) {
                redisTemplate.expire(k, time, TimeUnit.SECONDS);
            }
        }, addKeyPrefix(key));
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    public static void lUpdateIndex(String key, long index, Object value) {
        withConnection((redisTemplate, k) -> redisTemplate.opsForList().set(k, index, value), addKeyPrefix(key));
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public static long lRemove(String key, long count, Object value) {
        if (value == null) {
            return 0L;
        }
        return doWithConnection((redisTemplate, k) -> {
            log.debug("lRemove key: {}, value: {}", k, value);
            return redisTemplate.opsForList().remove(k, count, value);
        }, addKeyPrefix(key));
    }
    /* ===============================list end============================= */

    /**
     * 模糊查询获取key值
     *
     * @param pattern "*"查询所有key
     * @return
     */
    public static Set<String> keys(String pattern) {
        return doWithConnection((redisTemplate, k) -> scanKeys(redisTemplate, addKeyPrefix(pattern)), null);
    }

    private static Set<String> scanKeys(RedisTemplate redisTemplate, String pattern) {
        List<String> keys = new ArrayList<>();
        redisTemplate.execute((RedisCallback<Void>) connection -> {
            ScanOptions options = ScanOptions.scanOptions().match(pattern).count(redisMaxScanCount).build();
            try (Cursor<byte[]> cursor = connection.scan(options)) {
                while (cursor.hasNext()) {
                    keys.add(new String(cursor.next()));
                }
            }
            return null;
        });
        return new HashSet<>(keys);
    }

    /**
     * 使用Redis的消息队列
     *
     * @param channel
     * @param message 消息内容
     */
    public static void convertAndSend(String channel, Object message) {
        withConnection((redisTemplate, k) -> RedisConfig.getRedisTemplate().convertAndSend(addKeyPrefix(channel), message), null);
    }

    /**
     * 将数据添加到Redis的list中
     *
     * @param listKey
     * @param expireEnum 有效期的枚举类
     * @param values     待添加的数据
     */
    public static void addToListLeft(String listKey, RedisConstants.ExpireEnum expireEnum, Object... values) {
        withConnection((redisTemplate, k) -> {
            // 绑定操作
            BoundListOperations<String, Object> boundValueOperations = redisTemplate.boundListOps(k);
            // 插入数据
            boundValueOperations.rightPushAll(values);
            // 设置过期时间
            boundValueOperations.expire(expireEnum.getTime(), expireEnum.getTimeUnit());
        }, addKeyPrefix(listKey));
    }


    /**
     * 根据起始结束序号遍历Redis中的list
     *
     * @param listKey
     * @param start   起始序号
     * @param end     结束序号
     * @return
     */
    public static List<Object> rangeList(String listKey, long start, long end) {
        return doWithConnection((redisTemplate, k) -> {
            // 绑定操作
            BoundListOperations<String, Object> boundValueOperations = redisTemplate.boundListOps(k);
            if (boundValueOperations.size() == 0) {
                return Collections.emptyList();
            }
            // 查询数据
            return boundValueOperations.range(start, end);
        }, addKeyPrefix(listKey));
    }

    /**
     * 弹出右边的值 --- 并且移除这个值
     *
     * @param listKey
     */
    public static Object rifhtPop(String listKey) {
        return doWithConnection((redisTemplate, k) -> redisTemplate.boundListOps(k).rightPop(), addKeyPrefix(listKey));
    }

    // ========= Pub/Sub start============

    /**
     * 向通道发送消息的方法
     *
     * @param message
     */
    public static void sendChannelMess(String channel, String message) {
        RedisConfig.getRedisTemplate().convertAndSend(addKeyPrefix(channel), message);
    }

    public static void subscribe(String channel) {
        RedisMessageListener adapter = new RedisMessageListener();
        RedisConfig.getContainer().addMessageListener(adapter, new ChannelTopic(addKeyPrefix(channel)));
        RedisConfig.getContainer().start();
    }

    // ========= push queue============

    /**
     * 向队列里塞数据
     */
    public static void rpush(String queue, String data) {
        doWithConnection((redisTemplate, k) -> redisTemplate.opsForList().rightPush(k, data), addKeyPrefix(queue));
    }

    public static void removeQueueMessage(String source, String target) {
        removeQueueMessage(source, target, null);
    }

    public static void removeQueueMessage(String source, String target, String cntQueue) {
        if (StringUtils.isEmpty(source) || StringUtils.isEmpty(target)) {
            return;
        }
        List<Object> queueObjects = rangeList(source, 0, -1);
        if (!CollectionUtils.isEmpty(queueObjects)) {
            if (!StringUtils.isEmpty(cntQueue)) {
                decrQueueRemainExecutors(cntQueue, queueObjects.size());
            }
            log.info("move message, from {} to {}, value: {}", source, target, JsonUtils.toJson(queueObjects));
            addToListLeft(target, RedisConstants.ExpireEnum.DEFAULT, queueObjects);
        } else {
            log.info("move message, from {} to {}", source, target);
        }
        del(source);
    }


    public static String addKeyPrefix(String key) {
        if (StringUtils.isEmpty(key)) {
            return key;
        }
        if (key.startsWith(redisKeyPrefix)) {
            return key;
        }
        return String.format(DEFAULT_KEY_CHAIN_FORMAT, redisKeyPrefix, key);
    }

    public static String recoverKeyPrefix(String key) {
        if (StringUtils.isEmpty(key)) {
            return key;
        }
        if (key.startsWith(redisKeyPrefix.toLowerCase())) {
            return key.replaceFirst(redisKeyPrefix.toLowerCase(), redisKeyPrefix);
        } else if (key.startsWith(redisKeyPrefix)) {
            return key;
        }
        return String.format(DEFAULT_KEY_CHAIN_FORMAT, redisKeyPrefix, key);
    }

    public static String removeKeyPrefix(String key) {
        if (StringUtils.isEmpty(key)) {
            return key;
        }
        if (key.startsWith(redisKeyPrefix)) {
            return key.replaceFirst(String.format(DEFAULT_KEY_CHAIN_FORMAT, redisKeyPrefix, ""), "");
        } else if (key.startsWith(redisKeyPrefix.toLowerCase())) {
            return key.replaceFirst(String.format(DEFAULT_KEY_CHAIN_FORMAT, redisKeyPrefix.toLowerCase(), ""), "");
        }
        return key;
    }
}

package com.typ.constants;

import java.util.concurrent.TimeUnit;

public final class RedisConstants {
    private RedisConstants() {
    }

    /**
     * 过期时间相关枚举
     */
    public enum ExpireEnum {
        TEMP(100L, TimeUnit.SECONDS), DEFAULT(8L, TimeUnit.HOURS);

        /**
         * 过期时间
         */
        private Long time;
        /**
         * 时间单位
         */
        private TimeUnit timeUnit;

        ExpireEnum(Long time, TimeUnit timeUnit) {
            this.time = time;
            this.timeUnit = timeUnit;
        }

        public Long getTime() {
            return time;
        }

        public TimeUnit getTimeUnit() {
            return timeUnit;
        }
    }
    // 有效的gateway
    public static final String GATEWAY_VALID_INFO = "gateway:valid:info";

    // pentagon-gateway异步请求队列
    public static final String PENTAGON_GATEWAY_ASYNC_MESSAGE_QUEUE = "gateway:async:info";
    // gateway处理pentagon-gateway异步请求队列的临时队列，防止消息丢失
    public static final String TMP_PENTAGON_GATEWAY_ASYNC_MESSAGE_QUEUE = "tmp:gateway:async:info";

    // 底层mobis信息存储的前缀 mobius:gateway:slave, 完整版 mobius:gateway:slave:info:ip:port -> mobiusinfo
    // 除mobiusInfo,其他redisKey避免用这个前缀 ！todo
//    public static final String MOBIUS_SLAVE_INFO = "m:g:s:i";
    public static final String MOBIUS_SLAVE_INFO = "m:g:s:i";

    // mobius:gateway:engine:domain:function 具体消息队列
    public static final String GATEAY_MESSAGE_QUEUE = "m:g";
    public static final String TMP_GATEAY_MESSAGE_QUEUE = "tmp:m:g";

    // 底层mobiuschannel 发布注册信息channel
//    public static final String APP_REGISTER_CHANNEL = "app:register";
    public static final String APP_REGISTER_CHANNEL = "app:register";

    // 单mobius不同类别任务总数前缀 mobius:gateway:max:run:each, 配置的总数
    public static final String APP_MAX_RUN_LIMIT_PREFIX = "m:g:m:r:e";

    // 所有类别任务总数前缀 mobius:gateway:actual:max:run:each, 实际运行的总数
    public static final String APP_ACTUAL_MAX_RUN_LIMIT_PREFIX = "m:g:a:m:r:e";

    // 所有类别任务总数 mobius:gateway:total:max:run, 配置的总数
    public static final String APP_TOTAL_MAX_RUN_LIMIT = "m:g:t:m:r";

    // 所有类别任务总数 mobius:gateway:actual:total:max:run, 实际运行的总数
    public static final String APP_TOTAL_ACTUAL_MAX_RUN_LIMIT = "m:g:a:t:m:r";

    //底层mobius任务队列临时列表"$redisKeyPrefix:m:async:tmp:task"
//    public static final String ASYNC_REQUEST_TMP_TASK = "m:async:tmp:task";
    public static final String ASYNC_REQUEST_TMP_TASK = "m:async:tmp:task";

    // 当前可用mobius列表 mobius:valid:list:  + GatewayConfConstants.DEFAULT_ENGINE_DOMAIN_FUNCTION_FORMAT
    public static final String MOBIUS_VALID_LIST_PREFIX = "m:v:l";
    // 当前不可用mobius列表
    public static final String MOBIUS_INVALID_LIST_PREFIX = "m:inv:l";

    // 配置的mobius总的最大并发值，配置的单个队列总的并发值，配置的单个队列中某个mobius并发值
    // 实际mobius总的并发值，实际单个队列总的并发值，实际单个队列中某个mobius并发值

    // 配置的mobius总的最大并发值
    public static final String MOBIUS_TOTAL_CONF_MAX_RUNNING_TASK = "m:g:c:t";
    // 实际mobius总的并发值
    public static final String MOBIUS_TOTAL_ACTUAL_MAX_RUNNING_TASK = "m:g:a:t";

    // 更新内存中的mobius信息 gateway:config:memory:update
    public static final String UPDATE_GATEWAY_CONFIG_IN_MEMORY = "g:c:m:u";

    // 清理无效底层mobius上锁标记
    public static final String LOCK_CLEANUP_INVALID_MOBIUS = "lock:c:i:m";

    // 锁的前缀
    public static final String LOCK_PREFIX = "lock";

    // tmp:mobius:g:async:export:complete:task
    public static final String MOBIUS_ASYNC_REQUEST_EXPORT_COMPLETE_TASK = "mobius:g:async:export:complete:task";
    public static final String TMP_MOBIUS_ASYNC_REQUEST_EXPORT_COMPLETE_TASK = "tmp:" + MOBIUS_ASYNC_REQUEST_EXPORT_COMPLETE_TASK;
    public static final String GATEWAY_ASYNC_REQUEST_EXPORT_COMPLETE_TASK = "m:async:export:complete:task";

    //    public static final String MOBIUS_ASYNC_REQUEST_COMPLETE_TASK = "mobius:g:async:complete:task";
    // mobius执行完任务的结果队列
    public static final String MOBIUS_ASYNC_REQUEST_COMPLETE_TASK = "mobius:g:async:complete:task";
    public static final String TMP_MOBIUS_ASYNC_REQUEST_COMPLETE_TASK = "tmp:" + MOBIUS_ASYNC_REQUEST_COMPLETE_TASK;

    // redis中list，用于存储mobius完成的消息，队列名 prefix:gateway:async:info
    public static final String GATEWAY_ASYNC_REQUEST_COMPLETE_TASK = "m:async:complete:task";

    // mobius消费task，通知gateway修改task状态的队列 m:async:task:status
    public static final String ASYNC_REQUEST_TASK_STATUS = "m:async:task:status";
    public static final String TMP_ASYNC_REQUEST_TASK_STATUS = "tmp:" + ASYNC_REQUEST_TASK_STATUS;

    // mobius:g:async:middle:task
    public static final String MOBIUS_ASYNC_REQUEST_MIDDLE_TASK = "mobius:g:async:middle:task";
    public static final String TMP_MOBIUS_ASYNC_REQUEST_MIDDLE_TASK = "tmp:" + MOBIUS_ASYNC_REQUEST_MIDDLE_TASK;
    // pentagon消费的队列
    public static final String GATEWAY_ASYNC_REQUEST_MIDDLE_TASK = "m:async:middle:task";

    // task/changetaskstatus 需要严格的顺序，在事件7 和 事件8 中因为异步修改和scheduler读取信息有一段事件sleep的关系，有可能导致顺序错乱
    public static final String TASK_EVENT_MAP = "t:e:m";

    public static final String[] DEFAULT_TMP_QUEUE_ARRAY = new String[]{TMP_PENTAGON_GATEWAY_ASYNC_MESSAGE_QUEUE,
            TMP_MOBIUS_ASYNC_REQUEST_MIDDLE_TASK, TMP_ASYNC_REQUEST_TASK_STATUS, TMP_MOBIUS_ASYNC_REQUEST_COMPLETE_TASK,
            TMP_MOBIUS_ASYNC_REQUEST_EXPORT_COMPLETE_TASK};

    // pentagon推送的机器学习任务  machineLearning
    public static final String PENTAGON_MACHINE_LEARNING_TASK = "gateway:ml:task";
    public static final String TMP_PENTAGON_MACHINE_LEARNING_TASK = "tmp:" + PENTAGON_MACHINE_LEARNING_TASK;
    // mobius执行完机器学习任务的返回
    public static final String MOBIUS_MACHINE_LEARNING_TASK = "m:ml:t:r";
    public static final String TMP_MOBIUS_MACHINE_LEARNING_TASK = "tmp" + MOBIUS_MACHINE_LEARNING_TASK;
    // mobius执行机器学习的队列
    public static final String MOBIUS_MACHINE_LEARNING_RUN = "m:j:t:q";


    // 大任务资源池剩余可用core
    public static final String BIGTASK_RESOURCE_POOL_REMAIN_CORES = "m:b:r:p:r:c";
    // 大任务资源池剩余可用内存
    public static final String BIGTASK_RESOURCE_POOL_REMAIN_MEMORYS = "m:b:r:p:r:m";
    // 大任务资源池正在运行的任务列表（只用作监控）
    public static final String BIGTASK_RESOURCE_POOL_TASKS = "m:b:r:b:t";
    // 大任务单独提交的mobiuslist
    public static final String MOBIUS_BIGTASK_SLAVE_INFO = "m:g:b:s:i";
    // 大任务资源池更新锁
    public static final String MOBIUS_BIGTASK_RESOURCE_LOCK = "m:g:b:r:l";



    /**
     * gateway应用锁，抢到锁的再开始启动调度任务
     */
    public static final String GATEWAY_APPLICATION_LOCK = "gateway:application:lock";
}


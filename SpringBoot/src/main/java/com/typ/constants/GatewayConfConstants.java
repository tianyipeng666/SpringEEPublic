package com.typ.constants;

public final class GatewayConfConstants {
    private GatewayConfConstants() {
    }

    public static final String TRACE_ID = "trace_id";

    public static final String START_STR = "*";
    // 引擎、企业域、功能默认值
    public static final String[] DEFAULT_VALUE_ARRAY = new String[]{"default"};

    public static final String DEFAULT_VALUE = "default";

    public static final Integer NUM_1000_CONSTANT = 1000;

    public static final Integer NUM_60_CONSTANT = 60;

    public static final Integer NUM_30_CONSTANT = 30;

    public static final Integer NUM_4_CONSTANT = 4;

    public static final Integer NUM_3_CONSTANT = 3;
    public static final Integer NUM_10_CONSTANT = 10;
    public static final Integer NUM_1024_CONSTANT = 1024;

    // 引擎 企业域 功能 用户 key的格式
    public static final String DEFAULT_ENGINE_DOMAIN_FUNCTION_FORMAT = "%s:%s:%s:%s";

    public static final String ACTUAL_QUEUE_FORMAR = "%s:%s:%s";

    public static final String DEFAULT_ROUTE_CONFIG_ERROR_INFO =
            "Could not find routing configuration information,"
                    + " engine: %s to %s, domain: %s to %s, function: %s to %s, userName : %s to %s";

    public static final String DEFAULT_KEY_CHAIN_FORMAT = "%s:%s";

    public static final String LOCK_PREFIX = "lock";

    public static final String DOMAIN_PREFIX = "en_";

    public static final String DEFAULT_CHANNEL_DEBUG_LOG = "channel: {}, message: {}, update info";
    public static final String NO_MATCH_DEFAULT_CHANNEL_LOG = "channel: {}, message: {}, not match, not operator";

    // 返回值格式参数名称
    public static final String RESPONSE_STATUS = "status";
    public static final String RESPONSE_ERROR_MSG = "err_msg";
    public static final String RESPONSE_MSG = "msg";
    public static final String RESPONSE_DEFAULT_MSG = "mobius too busy";

    // 用户角色代码
    //    roleType: "{0: 100, 1: 100, 2: 70, 3: 50, 4: 30}"
    //    triggerByUser: "{true: 100, false: 50}"
    //    priority: "{1: 100, 0: 70, -1: 50}"
    public static final Integer DEFAULT_ROLE_TYPE_CODE = 3;
    public static final Integer DEFAULT_ROLE_TYPE_WEIGHT = 50;

    // 用户触发代码
    public static final Boolean DEFAULT_TRIGGER_BY_USER_CODE = false;
    public static final Integer DEFAULT_TRIGGER_BY_USER_WEIGHT = 50;

    // 用户优先级
    public static final String DEFAULT_PRIORITY_BASE_WEIGHT_NAME = "base";
    public static final Integer DEFAULT_PRIORITY_BASE_WEIGHT_VALUE = 10;

    // 时间权重配置

    public static final String DEFAULT_WEIGHT_NAME = "weight";
    public static final Integer DEFAULT_WEIGHT_VALUE = 1;
    public static final String DEFAULT_START_TIME_NAME = "start";
    public static final Integer DEFAULT_START_TIME_VALUE = 10;

    // 权重因素
    public static final Float DEFAULT_RATIO_WEIGHT = 0.25f;
    public static final String ROLE_TYPE_WEIGHT = "roleType";
    public static final String USER_PRIORITY_WEIGHT = "priority";
    public static final String TRIGGER_BY_USER_WEIGHT = "triggerByUser";
    public static final String TIME_WEIGHT = "time";

    public static final String HTTP_PREFIX = "http://";

    // 时间常量
    public static final Integer DEFAULT_MINUTE_MILLI_SECONDS_VALUE = 60000;


    // redis 取值信息异常的信息
    public static final String DEFAULT_REDIS_GET_MESSAGE_ERROR_INFO = "redis operator error, key: {}, errmsg: {}";

    public static final String CONSTANT_TB_NAME = "tbName";
    public static final String CONSTANT_STORAGE_ID = "storageId";
    public static final String JOB_ID = "jobId";

    // storage_id的占位符
    public static final String STORAGE_PLACE_HOLDER = "##";

    // 默认ip
    public static final String DEFAULT_IP = "0.0.0.0";

    public static final String ENGINE_IGNORE_DEFAULT_VALUE = "null";

    // 消费线程池名
    public static final String MOBIUS_QUEUE = "mobius-queue";
    public static final String MOBIUS_TASK_QUEUE = "mobius-task-queue";
    public static final String MOBIUS_EXPORT_QUEUE = "mobius-export-queue";
    public static final String MOBIUS_MIDDLE_QUEUE = "mobius-middle-queue";
    public static final String PENTAGON_QUEUE = "pentagon-queue";

    public static final String MACHINE_LEARNING_QUEUE = "machine-learning-queue";
    public static final String MACHINE_LEARNING_TASK = "machine-learning-task";

    //校验URI正则
    public static final String REG_URI_STARTWITH_V2 = "api/v2/[view|tag|table]/(\\w{32})(/\\w+)/";
    public static final String REG_TB_NAME = "\\b[a-z0-9]{32}\\b";

    // 时间
    public static final long TEN_SECONDS = 10000;
    public static final long ONE_MINUTE = 60000;
    public static final Integer NUM_0_CONSTANT = 0;
    // 最大重试次数
    public static final int MAX_RETRIES = 6;
    // 重试间隔时间（毫秒）
    public static final int RETRY_DELAY = 10000;

}

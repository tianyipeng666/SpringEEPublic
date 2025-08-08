package com.typ.listener;

import com.typ.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import static com.typ.constants.GatewayConfConstants.*;
import static com.typ.constants.RedisConstants.APP_REGISTER_CHANNEL;
import static com.typ.constants.RedisConstants.UPDATE_GATEWAY_CONFIG_IN_MEMORY;

// 根据key获取所有的key，然后对key的值进行刷新

@Component
@Slf4j
public class RedisMessageListener implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] pattern) {
        switch (RedisUtils.removeKeyPrefix(new String(pattern))) {
            case UPDATE_GATEWAY_CONFIG_IN_MEMORY:
                MDC.put(TRACE_ID, UPDATE_GATEWAY_CONFIG_IN_MEMORY);
                log.info(DEFAULT_CHANNEL_DEBUG_LOG, APP_REGISTER_CHANNEL, message);
                break;
            case APP_REGISTER_CHANNEL:
                MDC.put(TRACE_ID, APP_REGISTER_CHANNEL);
                log.info(DEFAULT_CHANNEL_DEBUG_LOG, APP_REGISTER_CHANNEL, message);
                break;
            default:
                log.warn(NO_MATCH_DEFAULT_CHANNEL_LOG, new String(pattern), message);
                break;
        }
    }
}

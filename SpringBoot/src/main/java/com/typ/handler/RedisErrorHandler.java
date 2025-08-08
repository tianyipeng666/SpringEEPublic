package com.typ.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ErrorHandler;

@Slf4j
public class RedisErrorHandler implements ErrorHandler {

    @Override
    public void handleError(Throwable t) {
        // 记录异常信息
        log.error("Redis message listener container error.", t);
    }
}

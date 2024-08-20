package com.typ.bean;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RefreshScope
@Setter
@Getter
public class ConfigPojo {

    @Value("${bean.key:}")
    private String key;

    @Value("${bean.value:}")
    private Boolean value;
}

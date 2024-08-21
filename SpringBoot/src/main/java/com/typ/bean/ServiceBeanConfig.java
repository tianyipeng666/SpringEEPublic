package com.typ.bean;


import com.typ.client.converter.TemplateMappingJackson2HttpMessageConverter;
import com.typ.service.HttpService;
import com.typ.service.impl.HttpServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Configuration
public class ServiceBeanConfig {

    @Bean(name = "FtpHttpService")
    public HttpService FtpHttpService() {
        return new HttpServiceImpl();
    }

    // 若不通过bean构建，RestTemplate无法在Springboot中自动注入，可通过builder来构建
    // RestTemplate restTemplate = restTemplateBuilder.build();
    @Bean(name = "RestTemplateWithOtherType")
    public RestTemplate RestTemplateWithOtherType() {
        RestTemplate restTemplate = new RestTemplate();
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Arrays.asList(
                MediaType.APPLICATION_JSON_UTF8,
                MediaType.TEXT_HTML,
                MediaType.TEXT_PLAIN));
        restTemplate.getMessageConverters().add(mappingJackson2HttpMessageConverter);
        // 自定义转换器，支持text/json;charset=UTF-8
        restTemplate.getMessageConverters().add(new TemplateMappingJackson2HttpMessageConverter());
        return restTemplate;
    }
}

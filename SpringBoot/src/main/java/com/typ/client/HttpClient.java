package com.typ.client;

import com.typ.bean.ConfigPojo;
import com.typ.client.request.HttpRequest;
import com.typ.client.response.CommonResponse;
import com.typ.client.response.HttpResponse;
import com.typ.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Slf4j
// 指示某个类被Spring管理，包含@Repository、@Service、@Controller注解，同@Autowired组合使用实现自动注入
@Component
public class HttpClient {

    @Autowired
    private ConfigPojo config;

    @Autowired
    @Qualifier("RestTemplateWithOtherType")
    private RestTemplate restTemplate;

    public HttpResponse executeRequest(HttpRequest request, String requestPath) throws InterruptedException {
        // http请求
        String url = String.format("http://%s%s", config.getUrl(), requestPath);
        String requestBody = JsonUtils.toJSONString(request);
        log.info(url + "==>" + requestBody);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);

        // 执行
        ResponseEntity<HttpResponse> responseEntity = restTemplate.postForEntity(url, httpEntity, HttpResponse.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            if (Objects.isNull(responseEntity.getBody())) {
                throw new RuntimeException("服务端响应为空");
            }
            HttpResponse body = responseEntity.getBody();
            if (!body.getErrorMsg().isEmpty()) {
                throw new RuntimeException(body.getErrorMsg());
            } else {
                return body;
            }
        } else {
            if (responseEntity.getBody() != null) {
                log.error("服务端响应:{}", responseEntity.getBody());
            }
            throw new RuntimeException("请求错误");
        }
    }

}

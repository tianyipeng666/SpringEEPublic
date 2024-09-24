package com.typ.client;

import com.typ.bean.ConfigPojo;
import com.typ.client.request.CommonRequest;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Objects;

@Slf4j
// 指示某个类被Spring管理，包含@Repository、@Service、@Controller注解，同@Autowired组合使用实现自动注入
public class HttpClient {

    @Autowired
    private ConfigPojo config;

    @Autowired
    @Qualifier("RestTemplateWithOtherType")
    private RestTemplate restTemplate;

    public String executeRestFulRequest(CommonRequest request, String requestPath, Class clazz) throws InterruptedException {
        // http请求
        String url = String.format("http://%s%s", config.getUrl(), requestPath);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestBody = JsonUtils.toJSONString(request);
        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);

        // 执行
        ResponseEntity<CommonResponse> responseEntity = restTemplate.postForEntity(url, httpEntity, clazz);

        // 返回
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            if (Objects.isNull(responseEntity.getBody())) {
                throw new RuntimeException("服务端响应为空");
            }
            CommonResponse body = responseEntity.getBody();
            if (!body.getStatus().equals(0)) {
                throw new RuntimeException("The some error happen, please refer to the log for details");
            } else {
                return JsonUtils.toJSONString(body);
            }
        } else {
            if (responseEntity.getBody() != null) {
                log.error("服务端响应:{}", responseEntity.getBody());
            }
            throw new RuntimeException("请求错误");
        }
    }

    public String executeRequest(MultiValueMap map, String requestPath, Class clazz) throws InterruptedException {
        // http请求
        String url = String.format("http://%s%s", config.getUrl(), requestPath);
        HttpHeaders headers = new HttpHeaders();
        // 请求接收端的HttpServletRequest若是以getParameter获取请求中的参数，那么contentType需为application/x-www-form-urlencoded
        // 若是以getReader来解析请求中的参数，则可以使用application/json
        // 必须使用MultiValueMap来添加参数
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(map, headers);

        // 执行
        ResponseEntity<CommonResponse> responseEntity = restTemplate.postForEntity(url, httpEntity, clazz);

        // 返回
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            if (Objects.isNull(responseEntity.getBody())) {
                throw new RuntimeException("服务端响应为空");
            }
            // 可获取到status
            CommonResponse body = responseEntity.getBody();
            log.info("[executeRequest MobiusCommonResponse]==>" + body.getStatus());
            if (!body.getStatus().equals(0)) {
                throw new RuntimeException("The some error happen, please refer to the log for details");
            } else {
                // 子类也可获取到status，但是toString由于@Data注解不能输出父类的属性
                log.info("[executeRequest]==>" + responseEntity.getBody().toString());
                return JsonUtils.toJSONString(body);
            }
        } else {
            if (responseEntity.getBody() != null) {
                log.error("服务端响应:{}", responseEntity.getBody());
            }
            throw new RuntimeException("请求错误");
        }
    }
}

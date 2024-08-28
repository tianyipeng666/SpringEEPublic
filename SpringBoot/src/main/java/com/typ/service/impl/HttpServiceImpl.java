package com.typ.service.impl;

import com.typ.bean.ConnectPojo;
import com.typ.client.HttpClient;
import com.typ.client.request.CommonRequest;
import com.typ.client.request.HttpRequest;
import com.typ.client.request.QueryRequest;
import com.typ.client.response.HttpResponse;
import com.typ.client.response.QueryResponse;
import com.typ.service.HttpService;
import com.typ.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Component
public class HttpServiceImpl implements HttpService {

    @Autowired
    private HttpClient client;

    @Override
    public String listFiles(ConnectPojo conn, String path, String requestPath) {
        try {
            CommonRequest request = HttpRequest.builder()
                    .connInfo(conn)
                    .path(path)
                    .build();
            HttpResponse httpResponse = JsonUtils.parseObject(client.executeRestFulRequest(request, requestPath, HttpResponse.class), HttpResponse.class);
            log.info(httpResponse.getStatus().toString());
            return httpResponse.toString();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String dbQuery(String sql, String requestPath) {
        try {
            LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
            map.add("sql", sql);
            QueryResponse queryResponse = JsonUtils.parseObject(client.executeRequest(map, requestPath, QueryResponse.class), QueryResponse.class);
            Object[][] data = queryResponse.getData();
            log.info("The return data length:" + data.length + ", and example data like:" + Arrays.stream(data[0]).collect(Collectors.toList()));
            log.info(queryResponse.getStatus().toString());
            return queryResponse.toString();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

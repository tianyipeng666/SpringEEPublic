package com.typ.service.impl;

import com.typ.bean.ConnectPojo;
import com.typ.client.HttpClient;
import com.typ.client.request.CommonRequest;
import com.typ.client.request.HttpRequest;
import com.typ.client.request.QueryRequest;
import com.typ.client.response.HttpResponse;
import com.typ.client.response.QueryResponse;
import com.typ.service.HttpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;

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
            return client.executeRestFulRequest(request, requestPath, HttpResponse.class).toString();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String dbQuery(String sql, String requestPath) {
        try {
            LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
            map.add("sql", sql);
            return client.executeRequest(map, requestPath, QueryResponse.class).toString();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

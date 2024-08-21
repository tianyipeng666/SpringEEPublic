package com.typ.service.impl;

import com.typ.bean.ConnectPojo;
import com.typ.client.HttpClient;
import com.typ.client.request.HttpRequest;
import com.typ.service.HttpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HttpServiceImpl implements HttpService {

    @Autowired
    private HttpClient client;

    @Override
    public String listFiles(ConnectPojo conn, String path, String requestPath) {
        try {
        HttpRequest request = HttpRequest.builder()
                .connInfo(conn)
                .path(path)
                .build();
            return client.executeRequest(request, requestPath).toString();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

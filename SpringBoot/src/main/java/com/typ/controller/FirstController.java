package com.typ.controller;


import com.typ.bean.ConfigPojo;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/first")
public class FirstController {

    private static OkHttpClient client;

    @Autowired
    private ConfigPojo config;

    static {
         client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    @ResponseBody
    @GetMapping("/firstGet")
    public String firstGet() {
        log.info(config.getKey() + "--" + config.getValue());
        return config.getKey();
    }

    @ResponseBody
    @GetMapping("/okhttp")
    public String okHttp3Check() {
        Request request = new Request.Builder()
                .url("http://localhost:8080/first/firstGet")
                .build();
        try {
            Response res = client.newCall(request).execute();
            return res.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

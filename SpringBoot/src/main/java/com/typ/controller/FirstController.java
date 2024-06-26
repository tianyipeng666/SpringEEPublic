package com.typ.controller;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Controller
public class FirstController {

    private static OkHttpClient client;

    static {
         client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    @ResponseBody
    @RequestMapping("firstGet")
    public String firstGet() {
        return "check";
    }

    @ResponseBody
    @RequestMapping("okhttp")
    public String okHttp3Check() {
        Request request = new Request.Builder()
                .url("http://localhost:8080/firstGet")
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

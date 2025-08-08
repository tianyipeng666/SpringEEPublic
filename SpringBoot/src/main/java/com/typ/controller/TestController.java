package com.typ.controller;


import com.typ.bean.ConfigPojo;
import com.typ.bean.ConnectPojo;
import com.typ.service.HttpService;
import com.typ.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    @Qualifier("FtpHttpService")
    HttpService ftpHttpService;

    @ResponseBody
    @GetMapping("/listFiles")
    public String listFiles() {
        ConnectPojo conn = ConnectPojo.builder()
                .host("123.126.105.70")
                .port(21)
                .userName("share")
                .password("haizhi1234")
                .protocol(1)
                .build();
        return ftpHttpService.listFiles(conn, "/typ/excelUpload", "/api/v2/ftp/listFile");
    }

    @ResponseBody
    @GetMapping("/query")
    public String queryData() {
        return ftpHttpService.dbQuery("show tables in `bdp_test` like '*'", "/db/query");
    }

    @ResponseBody
    @GetMapping("/count")
    public String countRows() {
        return ftpHttpService.countRows("bdp", "typtest4", 1726649479522L, "/taurus/tableRows");
    }

    @ResponseBody
    @GetMapping("/redis")
    public String redisTest() {
        RedisUtils.setnx("springRedis", "1", 60);
        RedisUtils.expire("springRedis", 80);
        System.out.println(RedisUtils.getToInt("springRedis"));
        RedisUtils.set("springRedis2", "2");
        RedisUtils.del((RedisUtils.hasKey("springRedis")) ? "springRedis" : "springRedis2");
        RedisUtils.rpush("springRedisQueueSource", "1");
        RedisUtils.rpush("springRedisQueueSource", "2");
        RedisUtils.rpush("springRedisQueueSource", "3");
        RedisUtils.removeQueueMessage("springRedisQueueSource", "springRedisQueueTarget");
        return "complete";
    }
}

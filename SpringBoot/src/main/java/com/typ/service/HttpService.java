package com.typ.service;

import com.typ.bean.ConnectPojo;

public interface HttpService {

    String listFiles(ConnectPojo conn, String path, String requestPath);
}

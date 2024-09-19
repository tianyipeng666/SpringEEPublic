package com.typ.service;

import com.typ.bean.ConnectPojo;

public interface HttpService {

    String listFiles(ConnectPojo conn, String path, String requestPath);

    String dbQuery(String sql, String requestPath);

    String countRows(String database, String tableName, Long lastUpdateTime, String requestPath);
}

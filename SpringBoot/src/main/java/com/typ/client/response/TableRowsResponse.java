package com.typ.client.response;

import lombok.Data;

@Data
public class TableRowsResponse extends CommonResponse {
    private String database;
    private String tableName;
    private Long lastUpdateTime;
    private Long newestUpdateTime;
    private Long totalSize;
    private Long rows;
    private Boolean isUpdate;
}

package com.typ.bean;

import lombok.Data;

@Data
public class FileInfo {
    private String path;
    private String fileName;
    private Long modifiedTime;
    private Long size;
    private Integer status;
    private Boolean isDir;
}

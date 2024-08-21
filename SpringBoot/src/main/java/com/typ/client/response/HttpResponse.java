package com.typ.client.response;

import com.typ.bean.FileInfo;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;

@Data
@ToString
//@NoArgsConstructor
//@Builder, 带上该注解会导致序列化失败
public class HttpResponse{
    private Integer status;
    private String errorMsg;
    private ArrayList<FileInfo> files;
}

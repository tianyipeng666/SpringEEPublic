package com.typ.client.request;


import com.typ.bean.ConnectPojo;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class HttpRequest{
    private String path;
    private ConnectPojo connInfo;
}

package com.typ.client.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public abstract class  CommonResponse {
    private Integer status;
    private String errMsg;
}

package com.typ.client.response;

import com.typ.bean.FieldInfo;
import com.typ.bean.FileInfo;
import lombok.Data;

import java.util.ArrayList;

@Data
public class QueryResponse extends CommonResponse{
    private Integer status;
    private ArrayList<String[]> data;
    private Integer df_length;
    private ArrayList<FieldInfo> schema;
}

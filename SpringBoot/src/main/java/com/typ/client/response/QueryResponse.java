package com.typ.client.response;

import com.typ.bean.FieldInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;

@Data
public class QueryResponse extends CommonResponse{
    private Object[][] data;
    private Integer df_length;
    private ArrayList<FieldInfo> schema;
}

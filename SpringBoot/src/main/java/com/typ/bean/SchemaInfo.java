package com.typ.bean;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Data
public class SchemaInfo {
    private String type = "struct";
    private List<FieldInfo> fields;
}

package com.typ.bean;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class FieldInfo {
    private String name;
    private String type;
    private Boolean nullable = true;
    private Map<String, Object> metadata = new HashMap<>();
}

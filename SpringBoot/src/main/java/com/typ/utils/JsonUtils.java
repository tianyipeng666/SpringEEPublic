package com.typ.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public final class JsonUtils {

    private static Gson gson = new Gson();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


    private JsonUtils() {

    }

    public static String toJSONString(Object obj) {
        return gson.toJson(obj);
    }


    public static <T> T parseObject(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    public static <T> List<T> parseList(Object obj, Class<T> clazz) {
        return gson.fromJson(gson.toJson(obj), new TypeToken<List<T>>() {
        }.getType());
    }

    public static String toJson(Object obj) {
        String jsonStr = "";

        try {
            jsonStr = OBJECT_MAPPER.writeValueAsString(obj);
            return jsonStr;
        } catch (JsonProcessingException var3) {
            throw new RuntimeException(var3);
        }
    }

}

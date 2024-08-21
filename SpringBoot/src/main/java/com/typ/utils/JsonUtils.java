package com.typ.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public final class JsonUtils {

    private static Gson gson = new Gson();

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

}

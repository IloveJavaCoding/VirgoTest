package com.harine.virgotest.util;

import com.google.gson.Gson;

/**
 * @author nepalese on 2021/3/13 11:22
 * @usage
 */
public class GsonUtil {

    public static <T> Object getObject(String json, Class<T> tClass){
        Gson gson = new Gson();
        return gson.fromJson(json, tClass);
    }

    public static String toJson(Object obj){
        return new Gson().toJson(obj);
    }
}

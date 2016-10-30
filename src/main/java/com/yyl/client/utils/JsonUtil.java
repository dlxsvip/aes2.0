package com.yyl.client.utils;

import org.codehaus.jackson.map.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yl on 2016/10/24.
 */
public class JsonUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String obj2str(Object obj) {
        String result = "";
        try {
            result = mapper.writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static <T> T json2bean(String json, Class<T> valueType) {
        try {
            return mapper.readValue(json, valueType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String success() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("code", "0");
        result.put("msg", "success");
        return obj2str(result);
    }

    public static String success(Object obj) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("code", "0");
        result.put("msg", "success");
        result.put("data", obj);
        return obj2str(result);
    }

    public static String failure() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("code", "-1");
        result.put("msg", "failure");
        return obj2str(result);
    }

    public static String failure(String msg) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("code", "-1");
        result.put("msg", msg);
        return obj2str(result);
    }


//    public static String failure(FailType eErr) {
//        Map<String, Object> result = new HashMap<String, Object>();
//        result.put("code", eErr.getIndex());
//        result.put("msg", eErr.getType());
//        return obj2str(result);
//    }
}

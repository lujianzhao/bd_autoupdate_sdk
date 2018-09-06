//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk.utils;

import com.baidu.autoupdatesdk.protocol.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.text.ParseException;

public class JsonUtils {
    public JsonUtils() {
    }

    public static Object parseResponse(byte[] responseBody, String charset) throws JSONException {
        if (null == responseBody) {
            return null;
        } else {
            Object result = null;
            String jsonString = getResponseString(responseBody, charset);
            if (jsonString != null) {
                jsonString = jsonString.trim();
                if (jsonString.startsWith("{") || jsonString.startsWith("[")) {
                    result = (new JSONTokener(jsonString)).nextValue();
                }
            }

            if (result == null) {
                result = jsonString;
            }

            return result;
        }
    }

    public static String getResponseString(byte[] stringBytes, String charset) {
        try {
            return stringBytes == null ? null : new String(stringBytes, charset);
        } catch (UnsupportedEncodingException var3) {
            return null;
        }
    }

    public static String stringTypeValue(JSONObject object, String name) {
        Object value = object.opt(name);
        if (value == null) {
            return null;
        } else {
            return JSONObject.NULL.equals(value) ? null : value.toString();
        }
    }

    public static Number numberTypeValue(JSONObject object, String name) {
        Object value = object.opt(name);
        if (value == null) {
            return null;
        } else if (JSONObject.NULL.equals(value)) {
            return null;
        } else {
            try {
                return NumberFormat.getInstance(Constant.default_Locale).parse(value.toString().trim());
            } catch (ParseException var4) {
//                LogUtils.printE(var4.getMessage());
                return null;
            }
        }
    }

    public static JSONArray arrayTypeValue(JSONObject object, String name) {
        Object value = object.opt(name);
        if (value == null) {
            return null;
        } else if (JSONObject.NULL.equals(value)) {
            return null;
        } else {
            return value instanceof JSONArray ? (JSONArray)value : null;
        }
    }

    public static JSONObject jsonObjectTypeValue(JSONObject object, String name) {
        Object value = object.opt(name);
        if (value == null) {
            return null;
        } else if (JSONObject.NULL.equals(value)) {
            return null;
        } else {
            return value instanceof JSONObject ? (JSONObject)value : null;
        }
    }
}

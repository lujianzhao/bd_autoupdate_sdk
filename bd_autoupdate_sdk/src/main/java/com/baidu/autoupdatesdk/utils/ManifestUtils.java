//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;

public class ManifestUtils {
    private static final String APP_ID = "BDAPPID";
    private static final String APP_KEY = "BDAPPKEY";

    private ManifestUtils() {
    }

    public static int intMetaDataType(Context context, String key, int defaultValue) {
        int value = defaultValue;

        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), 128);
            value = ai.metaData.getInt(key, defaultValue);
        } catch (NameNotFoundException var5) {
            ;
        } catch (Exception var6) {
            ;
        }

        return value;
    }

    public static String stringMetaDataType(Context context, String key, String defaultValue) {
        String value = defaultValue;

        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), 128);
            value = ai.metaData.getString(key, defaultValue);
        } catch (Exception var5) {
            ;
        }

        return value;
    }

    public static int getAppID(Context context) {
        int id = intMetaDataType(context, "BDAPPID", -1);
        if (id == -1) {
            throw new RuntimeException("app id is illegal!");
        } else {
            return id;
        }
    }

    public static String getAppKey(Context context) {
        String key = stringMetaDataType(context, "BDAPPKEY", (String)null);
        if (TextUtils.isEmpty(key)) {
            throw new RuntimeException("app key can not be empty!");
        } else {
            return key;
        }
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import com.baidu.autoupdatesdk.AppUpdateInfo;
import com.baidu.autoupdatesdk.AppUpdateInfoForInstall;

public final class PreferenceUtils {
    private static final String PREFERENCE = "bdp_pref";
    private static final String IGNORE_VERSION_CODE = "ignore_version_code";
    private static final String INSTALL_INFO = "install_info";
    private static final String INSTALLED_LAST_MODIFIED = "installed_last_modified";
    private static final String INSTALLED_MD5 = "installed_md5";

    private PreferenceUtils() {
    }

    public static int getIgnoreVersionCode(Context context) {
        SharedPreferences settings = context.getSharedPreferences("bdp_pref", 0);
        return settings.getInt("ignore_version_code", -1);
    }

    public static void setIgnoreVersionCode(Context context, int versionCode) {
        if (versionCode > 0) {
            SharedPreferences settings = context.getSharedPreferences("bdp_pref", 0);
            Editor editor = settings.edit();
            editor.putInt("ignore_version_code", versionCode);
            editor.commit();
        }
    }

    public static AppUpdateInfoForInstall getInstallInfo(Context context) {
        SharedPreferences settings = context.getSharedPreferences("bdp_pref", 0);
        return AppUpdateInfoForInstall.build(settings.getString("install_info", (String)null));
    }

    public static void setInstallInfo(Context context, AppUpdateInfo info) {
        String backup = AppUpdateInfoForInstall.toJson(info);
        if (!TextUtils.isEmpty(backup)) {
            SharedPreferences settings = context.getSharedPreferences("bdp_pref", 0);
            Editor editor = settings.edit();
            editor.putString("install_info", backup);
            editor.commit();
        }

    }

    public static long getInstalledLastModified(Context context) {
        SharedPreferences settings = context.getSharedPreferences("bdp_pref", 0);
        return settings.getLong("installed_last_modified", 0L);
    }

    public static void setInstalledLastModified(Context context, long lastModified) {
        SharedPreferences settings = context.getSharedPreferences("bdp_pref", 0);
        Editor editor = settings.edit();
        editor.putLong("installed_last_modified", lastModified);
        editor.commit();
    }

    public static String getInstalledMD5(Context context) {
        SharedPreferences settings = context.getSharedPreferences("bdp_pref", 0);
        return settings.getString("installed_md5", "");
    }

    public static void setInstalledMD5(Context context, String md5) {
        SharedPreferences settings = context.getSharedPreferences("bdp_pref", 0);
        Editor editor = settings.edit();
        editor.putString("installed_md5", md5);
        editor.commit();
    }
}

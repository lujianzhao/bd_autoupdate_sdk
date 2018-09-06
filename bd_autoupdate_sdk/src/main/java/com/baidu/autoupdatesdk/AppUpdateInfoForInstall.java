//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk;

import android.text.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class AppUpdateInfoForInstall {
    private String appSName;
    private String appVersionName;
    private String appChangeLog;
    private String installPath;

    private AppUpdateInfoForInstall() {
    }

    public static String toJson(AppUpdateInfo info) {
        String result = null;
        JSONObject obj = new JSONObject();

        try {
            obj.put("appSName", info.getAppSname());
            obj.put("appVersionName", info.getAppVersionName());
            obj.put("appChangeLog", info.getAppChangeLog());
            if (obj != null) {
                result = obj.toString();
            }
        } catch (JSONException var4) {
//            LogUtils.printE(var4.getMessage());
        }

        return result;
    }

    public static AppUpdateInfoForInstall build(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        } else {
            AppUpdateInfoForInstall a;
            try {
                JSONObject obj = new JSONObject(json);
                a = new AppUpdateInfoForInstall();
                a.appSName = obj.optString("appSName");
                a.appVersionName = obj.optString("appVersionName");
                a.appChangeLog = obj.optString("appChangeLog");
            } catch (JSONException var3) {
                a = null;
            }

            return a;
        }
    }

    public String getAppSName() {
        return this.appSName;
    }

    public String getAppVersionName() {
        return this.appVersionName;
    }

    public String getAppChangeLog() {
        return this.appChangeLog;
    }

    public String getInstallPath() {
        return this.installPath;
    }

    public void setInstallPath(String installPath) {
        this.installPath = installPath;
    }
}

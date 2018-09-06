//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import com.baidu.autoupdatesdk.analytics.Tag;
import com.baidu.autoupdatesdk.analytics.TagRecorder;

public class AppSearchUtils {
    public AppSearchUtils() {
    }

    @SuppressLint({"InlinedApi"})
    public static void invokeAppSearch(Context context) {
        TagRecorder.onTag(context, Tag.newInstance(30));
        Intent intent = new Intent("com.baidu.appsearch.extinvoker.LAUNCH");
        intent.putExtra("backop", "0");
        intent.putExtra("id", context.getPackageName());
        intent.putExtra("func", "10");
        intent.putExtra("pkg", context.getPackageName());
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static boolean canAsUpdate(Context context) {
        try {
            int versioncode = context.getPackageManager().getPackageInfo("com.baidu.appsearch", PackageManager.GET_SIGNATURES).versionCode;
            if (versioncode > 16782394) {
                return true;
            }
        } catch (NameNotFoundException var2) {
//            LogUtils.printE(var2.getMessage());
        }

        return false;
    }
}

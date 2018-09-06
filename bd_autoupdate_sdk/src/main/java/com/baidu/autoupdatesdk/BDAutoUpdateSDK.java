//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import com.baidu.autoupdatesdk.flow.AsUpdateFlow;
import com.baidu.autoupdatesdk.flow.NoUIUpdateFlow;
import com.baidu.autoupdatesdk.flow.SilenceUpdateFlow;
import com.baidu.autoupdatesdk.flow.UIUpdateFlow;
import com.baidu.autoupdatesdk.utils.ApkUtils;

public class BDAutoUpdateSDK {
    public static final int RESULT_CODE_OK = 0;
    public static final int RESULT_CODE_NET_ERROR = -1;
    public static final int RESULT_CODE_PARSE_ERROR = -2;
    public static final int RESULT_CODE_ERROR_UNKNOWN = -2147483648;
    private static long lastUpdate = 0L;

    public BDAutoUpdateSDK() {
    }

    public static void cpUpdateCheck(Context context, CPCheckUpdateCallback callback, boolean useHttps) {
        (new NoUIUpdateFlow()).start(context, callback, useHttps);
    }

    public static void cpUpdateDownload(Context context, AppUpdateInfo info, CPUpdateDownloadCallback callback) {
        (new NoUIUpdateFlow()).download(context, info, callback);
    }

    public static void cpUpdateDownloadByAs(Context context) {
        if (canContinue()) {
            (new AsUpdateFlow()).start(context, false);
        }

    }

    public static void cpUpdateInstall(Context context, String apkPath) {
        if (!TextUtils.isEmpty(apkPath)) {
            ApkUtils.installSimple(context, apkPath);
        }

    }

    public static void uiUpdateAction(Context context, UICheckUpdateCallback callback, boolean useHttps) {
        if (canContinue()) {
            (new UIUpdateFlow()).start(context, callback, useHttps);
        } else if (callback != null) {
            callback.onCheckComplete();
        }

    }

    public static void silenceUpdateAction(Context context, boolean useHttps) {
        if (canContinue()) {
            (new SilenceUpdateFlow()).start(context, useHttps);
        }

    }

    private static boolean canContinue() {
        long now = SystemClock.elapsedRealtime();
        if (now - lastUpdate < 1000L) {
//            LogUtils.printI("invoke too often");
            return false;
        } else {
            lastUpdate = now;
            return true;
        }
    }
}

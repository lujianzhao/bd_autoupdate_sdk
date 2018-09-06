//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import com.baidu.autoupdatesdk.protocol.Constant;

public class DeviceUtils {
    public static final int SCREEN_PORTRAIT = 0;
    public static final int SCREEN_LANDSCAPE = 1;
    public static String MCC_INVALID = "";
    public static String MNC_INVALID = "";

    public DeviceUtils() {
    }

    public static String getIMSI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService("phone");
        String imsi = telephonyManager.getSubscriberId();
        return imsi;
    }

    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService("phone");
        return telephonyManager.getDeviceId();
    }

    public static String getAndroidID(Context context) {
        String androidId = Secure.getString(context.getContentResolver(), "android_id");
        return androidId;
    }

    public static String getMAC(Context context) {
        WifiManager wifi = (WifiManager)((WifiManager)context.getSystemService("wifi"));
        if (wifi == null) {
            return "";
        } else {
            WifiInfo info = wifi.getConnectionInfo();
            return info == null ? "" : info.getMacAddress();
        }
    }

    public static int getScreenOrientation(Context context) {
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        int w = display.widthPixels;
        int h = display.heightPixels;
        return h > w ? 0 : 1;
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        return display.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        return display.heightPixels;
    }

    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5F);
    }

    public static int px2dip(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5F);
    }

    public static boolean isMountSim(Context context) {
        TelephonyManager manager = (TelephonyManager)context.getSystemService("phone");
        int absent = manager.getSimState();
        return absent == 5;
    }

    public static String getCountryIso(Context context) {
        TelephonyManager telManager = (TelephonyManager)context.getSystemService("phone");
        String countryIso = telManager.getSimCountryIso();
        if (TextUtils.isEmpty(countryIso)) {
            countryIso = telManager.getNetworkCountryIso();
        }

        if (countryIso == null) {
            countryIso = "";
        }

        return countryIso.toUpperCase(Constant.default_Locale);
    }

    public static String getSimMCC(Context context) {
        TelephonyManager tel = (TelephonyManager)context.getSystemService("phone");
        String simOperator = tel.getSimOperator();
        if (!TextUtils.isEmpty(simOperator)) {
            try {
                return simOperator.substring(0, 3);
            } catch (Exception var4) {
//                LogUtils.printE(var4.getMessage());
            }
        }

        return MCC_INVALID;
    }

    public static String getSimMNC(Context context) {
        TelephonyManager tel = (TelephonyManager)context.getSystemService("phone");
        String simOperator = tel.getSimOperator();
        if (!TextUtils.isEmpty(simOperator)) {
            try {
                String mnc = simOperator.substring(3);
                if (mnc.length() == 3 || mnc.length() == 2) {
                    return mnc;
                }
            } catch (Exception var4) {
//                LogUtils.printE(var4.getMessage());
            }
        }

        return MNC_INVALID;
    }

    public static String getPhoneType(Context context) {
        return Build.MODEL;
    }
}

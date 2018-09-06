//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import com.baidu.autoupdatesdk.protocol.Constant;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;

public class BDUtils {
    public BDUtils() {
    }

    public static String getVersionName(Context context) {
        String ver = null;
        PackageManager pm = context.getPackageManager();

        try {
            ver = pm.getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception var4) {
//            LogUtils.printE(var4.getMessage());
        }

        return ver;
    }

    public static int getVersionCode(Context context) {
        int code = -1;
        PackageManager pm = context.getPackageManager();

        try {
            code = pm.getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (Exception var4) {
//            LogUtils.printE(var4.getMessage());
        }

        return code;
    }

    public static Drawable getAppIcon(Context context) {
        PackageManager pm = context.getPackageManager();

        try {
            return pm.getApplicationIcon(context.getPackageName());
        } catch (NameNotFoundException var3) {
//            LogUtils.printE(var3.getMessage());
            return null;
        }
    }

    public static String getAppSignMd5(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 64);
            return creatSignInt(getMd5(packageInfo));
        } catch (Exception var2) {
//            LogUtils.printE(var2.getMessage());
            return null;
        }
    }

    private static String getMd5(PackageInfo packageinfo) {
        if (packageinfo == null) {
            return null;
        } else {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(packageinfo.signatures[0].toCharsString().getBytes());
                byte[] b = md.digest();
                char[] hexChar = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
                StringBuilder sb = new StringBuilder(b.length * 2);

                for(int i = 0; i < b.length; ++i) {
                    sb.append(hexChar[(b[i] & 240) >>> 4]);
                    sb.append(hexChar[b[i] & 15]);
                }

                return sb.toString();
            } catch (NoSuchAlgorithmException var6) {
                return null;
            }
        }
    }

    private static String creatSignInt(String md5) {
        if (md5 != null && md5.length() >= 32) {
            String sign = md5.substring(8, 24);
            long id1 = 0L;
            long id2 = 0L;
            String s = "";

            int i;
            for(i = 0; i < 8; ++i) {
                id2 *= 16L;
                s = sign.substring(i, i + 1);
                id2 += (long)Integer.parseInt(s, 16);
            }

            for(i = 8; i < sign.length(); ++i) {
                id1 *= 16L;
                s = sign.substring(i, i + 1);
                id1 += (long)Integer.parseInt(s, 16);
            }

            long id = id1 + id2 & 4294967295L;
            return String.valueOf(id);
        } else {
            return "-1";
        }
    }

    public static int getCellID(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager)context.getSystemService("phone");
            CellLocation location = tm.getCellLocation();
            if (location instanceof CdmaCellLocation) {
                return ((CdmaCellLocation)location).getBaseStationId();
            }

            if (location instanceof GsmCellLocation) {
                return ((GsmCellLocation)location).getCid();
            }
        } catch (Exception var3) {
//            LogUtils.printE(var3.getMessage());
        }

        return 0;
    }

    public static boolean isAppActive(Context ctx) {
        ActivityManager activityManager = (ActivityManager)ctx.getSystemService("activity");
        List<RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
        Iterator var3 = processInfos.iterator();

        while(true) {
            RunningAppProcessInfo processInfo;
            do {
                if (!var3.hasNext()) {
                    return false;
                }

                processInfo = (RunningAppProcessInfo)var3.next();
            } while(processInfo.importance != 100);

            String[] var5 = processInfo.pkgList;
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                String pkgName = var5[var7];
                if (ctx.getPackageName().equals(pkgName)) {
                    return true;
                }
            }
        }
    }

    public static String byteToMb(long fileSize) {
        float size = (float)fileSize / 1048576.0F;
        return String.format("%.2fMB", size);
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString != null && !hexString.equals("")) {
            hexString = hexString.toUpperCase(Constant.default_Locale);
            int length = hexString.length() / 2;
            char[] hexChars = hexString.toCharArray();
            byte[] d = new byte[length];

            for(int i = 0; i < length; ++i) {
                int pos = i * 2;
                d[i] = (byte)(charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
            }

            return d;
        } else {
            return null;
        }
    }

    private static byte charToByte(char c) {
        return (byte)"0123456789ABCDEF".indexOf(c);
    }
}

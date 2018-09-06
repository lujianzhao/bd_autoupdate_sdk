//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.os.Build.VERSION;

public class NdPackageParser {
    private String mPkgName;
    private Context mContext;
    public NdPackageParser.PkgItem pkg = new NdPackageParser.PkgItem();

    public NdPackageParser(Context context, String pkgName) {
        this.mContext = context;
        this.mPkgName = pkgName;
    }

    public NdPackageParser parse() {
        try {
            PackageInfo pi = this.mContext.getPackageManager().getPackageInfo(this.mPkgName, 0);
            this.pkg.pkgName = this.mPkgName;
            this.pkg.versionName = pi.versionName;
            this.pkg.versionCode = pi.versionCode;
            this.pkg.installPath = pi.applicationInfo.sourceDir;
            if (VERSION.SDK_INT < 8) {
                String intalledPath = pi.applicationInfo.sourceDir;
                String sdcardPath = Environment.getExternalStorageState();
                if (intalledPath.startsWith(sdcardPath)) {
                    this.pkg.storedInternal = false;
                } else {
                    this.pkg.storedInternal = true;
                }
            } else {
                int FLAG_EXTERNAL_STORAGE = 262144;
                this.pkg.storedInternal = (pi.applicationInfo.flags & 262144) == 0;
            }
        } catch (NameNotFoundException var4) {
//            LogUtils.printE(var4.getMessage());
        }

        return this;
    }

    public static class PkgItem {
        public String pkgName = "unknown";
        public String versionName = "unknown";
        public int versionCode = 0;
        public boolean storedInternal = true;
        public String installPath = "";

        public PkgItem() {
        }

        public boolean isInvalid() {
            return this.pkgName.equals("unknown");
        }
    }
}

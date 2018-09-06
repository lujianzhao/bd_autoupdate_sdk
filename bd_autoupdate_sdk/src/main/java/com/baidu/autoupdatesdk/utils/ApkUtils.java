//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.Toast;

import com.baidu.autoupdatesdk.download.BDDownloadManager;
import com.baidu.autoupdatesdk.r.ID;

import java.io.File;

public class ApkUtils {
    private static String installApkPath;

    public ApkUtils() {
    }

    public static void install(Context ctx, String fileName) {
        File f = new File(fileName);
        if (!f.exists()) {
            File install = new File(getInstallApkPath(ctx));
            if (!install.exists()) {
                Toast.makeText(ctx, ID.getString(ctx, "bdp_update_install_file_not_exist"), Toast.LENGTH_SHORT).show();
            } else {
                installSimple(ctx, getInstallApkPath(ctx));
            }

        } else {
            installSimple(ctx, getRealInstallApkPath(ctx, fileName));
        }
    }

    public static void installSimple(Context ctx, String installPath) {
        if (TextUtils.isEmpty(installPath)) {
            Toast.makeText(ctx, ID.getString(ctx, "bdp_update_install_file_not_exist"), Toast.LENGTH_SHORT).show();
        } else {
            File f = new File(installPath);
            if (!f.exists()) {
                Toast.makeText(ctx, ID.getString(ctx, "bdp_update_install_file_not_exist"), Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);

                Uri uri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){//24
                    uri = FileProvider.getUriForFile(ctx,ctx.getPackageName()+".fileprovider",f);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }else {
                    uri = Uri.fromFile(f);
                }
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
                if (!(ctx instanceof Activity)) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }

                ctx.startActivity(intent);
            }
        }
    }

    public static String getRealInstallApkPath(Context context, String preFilePath) {
        try {
            File intallFile = new File(getInstallApkPath(context));
            File preFile = new File(preFilePath);
            if (preFile.exists()) {
                if (intallFile.exists()) {
                    intallFile.delete();
                }

                preFile.renameTo(intallFile);
            }

            return intallFile.getAbsolutePath();
        } catch (Exception var4) {
//            LogUtils.printE(var4.getMessage());
            return null;
        }
    }

    public static String getInstallApkPath(Context context) {
        if (TextUtils.isEmpty(installApkPath)) {
            installApkPath = BDDownloadManager.getInstance().createCacheDir(context) + "waitingforinstall.apk";
        }

        return installApkPath;
    }
}

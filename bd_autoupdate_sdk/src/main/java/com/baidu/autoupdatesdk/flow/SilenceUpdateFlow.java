//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk.flow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.text.TextUtils;

import com.baidu.autoupdatesdk.AppUpdateInfo;
import com.baidu.autoupdatesdk.AppUpdateInfoForInstall;
import com.baidu.autoupdatesdk.ConfirmUpdategActivity;
import com.baidu.autoupdatesdk.ConfirmUpdategActivity.OnActionListener;
import com.baidu.autoupdatesdk.ICallback;
import com.baidu.autoupdatesdk.action.ActionFactory;
import com.baidu.autoupdatesdk.analytics.Tag;
import com.baidu.autoupdatesdk.analytics.TagRecorder;
import com.baidu.autoupdatesdk.download.BDDownloadManager;
import com.baidu.autoupdatesdk.download.BDDownloadManager.DownloadType;
import com.baidu.autoupdatesdk.download.BDDownloadManager.OnDownloadProgressListener;
import com.baidu.autoupdatesdk.download.BDNotificationManager;
import com.baidu.autoupdatesdk.download.BDNotificationManager.OnClickListener;
import com.baidu.autoupdatesdk.flow.MergePatchTask.OnMergeCompleteListener;
import com.baidu.autoupdatesdk.r.ID;
import com.baidu.autoupdatesdk.utils.ApkUtils;
import com.baidu.autoupdatesdk.utils.BDUtils;
import com.baidu.autoupdatesdk.utils.NetworkUtils;
import com.baidu.autoupdatesdk.utils.PreferenceUtils;

import java.io.File;

public class SilenceUpdateFlow {
    public SilenceUpdateFlow() {
    }

    public void start(Context context, boolean useHttps) {
        final Context appCtx = context.getApplicationContext();
        final int ignoreCode = PreferenceUtils.getIgnoreVersionCode(context);
//        LogUtils.printI("ignoreVersionCode: " + ignoreCode);
        if (NetworkUtils.isWifiActive(appCtx)) {
            TagRecorder.onTag(context, Tag.newInstance(1));
            ActionFactory.checkAppUpdate(appCtx, new ICallback<AppUpdateInfo>() {
                public void onCallback(int resultCode, String resultDesc, AppUpdateInfo extraData) {
                    if (resultCode == 10000 && extraData != null) {
                        TagRecorder.onTag(appCtx, Tag.newInstance(2));
//                        LogUtils.printI("ignoreVersionCode: " + ignoreCode + ", newVersionCode: " + extraData.getAppVersionCode());
                        File latestApk = BDDownloadManager.getInstance().getLatestApkFile(appCtx, extraData.getAppVersionCode() - 1, ignoreCode);
                        if (latestApk == null) {
                            if (extraData.getAppVersionCode() > BDUtils.getVersionCode(appCtx) && extraData.getAppVersionCode() != ignoreCode) {
                                SilenceUpdateFlow.this.doDownload(appCtx, extraData);
                            }
                        } else {
                            int versionCode = BDDownloadManager.getInstance().getFileVersionCode(latestApk);
                            SilenceUpdateFlow.this.doInstall(appCtx, extraData, latestApk.getAbsolutePath(), versionCode);
                        }
                    }

                }
            }, useHttps);
        } else {
            File latestApk = BDDownloadManager.getInstance().getLatestApkFileExceptIgnore(context, ignoreCode);
            if (latestApk != null) {
                int versionCode = BDDownloadManager.getInstance().getFileVersionCode(latestApk);
                this.doInstall(appCtx, (AppUpdateInfo)null, latestApk.getAbsolutePath(), versionCode);
            }
        }

    }

    private void doDownload(Context ctx, AppUpdateInfo info) {
        this.download(ctx, info);
    }

    @SuppressLint({"NewApi"})
    private void download(final Context ctx, final AppUpdateInfo info) {
        BDDownloadManager.getInstance().startDownload(ctx, DownloadType.silence, info, new OnDownloadProgressListener() {
            public void onSuccess(String downloadPath) {
                File file = new File(downloadPath);
                if (file.exists()) {
                    if (downloadPath.endsWith(".apk")) {
//                        LogUtils.printI("apk downloaded");
                    } else if (downloadPath.endsWith(".xdt")) {
                        try {
                            PackageInfo pkgInfo = ctx.getPackageManager().getPackageInfo(info.getAppPackage(), 0);
                            String oldFile = pkgInfo.applicationInfo.sourceDir;
                            MergePatchTask task = new MergePatchTask(ctx, oldFile, downloadPath, info, new OnMergeCompleteListener() {
                                public void onComplete(boolean success, String savePath) {
//                                    LogUtils.printI("merge completed");
                                }
                            });
                            if (VERSION.SDK_INT >= 11) {
                                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                            } else {
                                task.execute(new Void[0]);
                            }
                        } catch (NameNotFoundException var6) {
//                            LogUtils.printE(var6.getMessage());
                        }
                    } else {
                        file.delete();
                    }

                }
            }

            public void onStart() {
            }

            public void onPercent(int percent, long rcvLen, long fileSize) {
            }

            public void onFail(Throwable error, String content) {
            }

            public void onInstead() {
            }
        });
    }

    private void doInstall(Context ctx, AppUpdateInfo info, final String argApkPath, int versionCode) {
        if (!MergePatchTask.isMerging()) {
            String mainTip;
            if (BDUtils.isAppActive(ctx)) {
                mainTip = null;
                String minorTip = null;
                if (info != null) {
                    mainTip = ctx.getString(ID.getString(ctx, "bdp_update_install_main_tip"), new Object[]{BDUtils.getVersionName(ctx), info.getAppVersionName()});
                    minorTip = info.getAppChangeLog();
                } else {
                    AppUpdateInfoForInstall afi = PreferenceUtils.getInstallInfo(ctx);
                    if (afi != null) {
                        mainTip = ctx.getString(ID.getString(ctx, "bdp_update_install_main_tip"), new Object[]{BDUtils.getVersionName(ctx), afi.getAppVersionName()});
                        minorTip = afi.getAppChangeLog();
                    }
                }

                if (!TextUtils.isEmpty(minorTip)) {
                    String minor = ctx.getString(ID.getString(ctx, "bdp_update_minor_tip")) + "<br>";
                    minorTip = minor + minorTip;
                }

                boolean canClose;
                if (info != null) {
                    canClose = info.getForceUpdate() != 1;
                } else {
                    canClose = true;
                }

                ConfirmUpdategActivity.show(ctx, 2, mainTip, minorTip, canClose, versionCode, new OnActionListener() {
                    public void onUpdate(Context context) {
                    }

                    public void onUpdateRecommend(Context context) {
                        ApkUtils.install(context, argApkPath);
                    }

                    public void onIgnoreVersion(Context context, int ignoreVersion) {
                        PreferenceUtils.setIgnoreVersionCode(context, ignoreVersion);
                    }

                    public void onClose(Context context) {
                    }
                });
            } else {
                if (info != null) {
                    mainTip = info.getAppSname();
                } else {
                    AppUpdateInfoForInstall afi = PreferenceUtils.getInstallInfo(ctx);
                    mainTip = afi.getAppSName();
                }

                BDNotificationManager.getInstance(ctx).downloadCompleteNotify(mainTip, new OnClickListener() {
                    public void onClick(Context context) {
                        ApkUtils.install(context, argApkPath);
                    }
                });
            }

        }
    }
}

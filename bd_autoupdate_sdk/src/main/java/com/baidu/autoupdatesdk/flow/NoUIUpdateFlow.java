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

import com.baidu.autoupdatesdk.AppUpdateInfo;
import com.baidu.autoupdatesdk.AppUpdateInfoForInstall;
import com.baidu.autoupdatesdk.CPCheckUpdateCallback;
import com.baidu.autoupdatesdk.CPUpdateDownloadCallback;
import com.baidu.autoupdatesdk.ICallback;
import com.baidu.autoupdatesdk.action.ActionFactory;
import com.baidu.autoupdatesdk.analytics.Tag;
import com.baidu.autoupdatesdk.analytics.TagRecorder;
import com.baidu.autoupdatesdk.download.BDDownloadManager;
import com.baidu.autoupdatesdk.download.BDDownloadManager.DownloadType;
import com.baidu.autoupdatesdk.download.BDDownloadManager.OnDownloadProgressListener;
import com.baidu.autoupdatesdk.flow.MergePatchTask.OnMergeCompleteListener;
import com.baidu.autoupdatesdk.utils.ApkUtils;
import com.baidu.autoupdatesdk.utils.BDUtils;
import com.baidu.autoupdatesdk.utils.NetworkUtils;
import com.baidu.autoupdatesdk.utils.PreferenceUtils;

import java.io.File;

public class NoUIUpdateFlow {
    private CPCheckUpdateCallback checkUpdateCallback;
    private CPUpdateDownloadCallback updateDownloadCallback;

    public NoUIUpdateFlow() {
    }

    public void start(Context context, CPCheckUpdateCallback callback, boolean useHttps) {
        if (callback != null) {
            this.checkUpdateCallback = callback;
            final Context appCtx = context.getApplicationContext();
            if (NetworkUtils.isNetActive(appCtx)) {
                TagRecorder.onTag(context, Tag.newInstance(1));
                ActionFactory.checkAppUpdate(appCtx, new ICallback<AppUpdateInfo>() {
                    public void onCallback(int resultCode, String resultDesc, AppUpdateInfo extraData) {
                        AppUpdateInfo info = null;
                        AppUpdateInfoForInstall infoForInstall = null;
                        if (resultCode == 10000 && extraData != null) {
                            TagRecorder.onTag(appCtx, Tag.newInstance(2));
                            File latestApk = BDDownloadManager.getInstance().getLatestApkFile(appCtx, extraData.getAppVersionCode() - 1, -1);
                            if (latestApk == null) {
                                if (extraData.getAppVersionCode() > BDUtils.getVersionCode(appCtx)) {
                                    info = extraData;
                                }
                            } else if (!MergePatchTask.isMerging()) {
                                AppUpdateInfoForInstall afi = PreferenceUtils.getInstallInfo(appCtx);
                                if (afi != null) {
                                    afi.setInstallPath(ApkUtils.getRealInstallApkPath(appCtx, latestApk.getAbsolutePath()));
                                }

                                infoForInstall = afi;
                            }
                        }

                        NoUIUpdateFlow.this.checkUpdateCallback.onCheckUpdateCallback(info, infoForInstall);
                    }
                }, useHttps);
            } else {
                this.checkUpdateCallback.onCheckUpdateCallback((AppUpdateInfo)null, (AppUpdateInfoForInstall)null);
            }

        }
    }

    @SuppressLint({"NewApi"})
    public void download(final Context context, final AppUpdateInfo info, CPUpdateDownloadCallback callback) {
        if (callback != null) {
            this.updateDownloadCallback = callback;
            this.updateDownloadCallback.onStart();
            BDDownloadManager.getInstance().startDownload(context, DownloadType.nouiupdate, info, new OnDownloadProgressListener() {
                public void onSuccess(String downloadPath) {
                    File file = new File(downloadPath);
                    if (!file.exists()) {
                        NoUIUpdateFlow.this.updateDownloadCallback.onFail(new RuntimeException("download file is not exists."), "download file is not exists.");
                    } else {
                        if (downloadPath.endsWith(".apk")) {
                            if (!MergePatchTask.isMerging()) {
                                NoUIUpdateFlow.this.updateDownloadCallback.onDownloadComplete(ApkUtils.getRealInstallApkPath(context, downloadPath));
                            }
                        } else if (downloadPath.endsWith(".xdt")) {
                            try {
                                PackageInfo pkgInfo = context.getPackageManager().getPackageInfo(info.getAppPackage(), 0);
                                String oldFile = pkgInfo.applicationInfo.sourceDir;
                                MergePatchTask task = new MergePatchTask(context, oldFile, downloadPath, info, new OnMergeCompleteListener() {
                                    public void onComplete(boolean success, String savePath) {
                                        if (success) {
                                            NoUIUpdateFlow.this.updateDownloadCallback.onDownloadComplete(ApkUtils.getRealInstallApkPath(context, savePath));
                                        } else {
                                            NoUIUpdateFlow.this.updateDownloadCallback.onFail(new RuntimeException("merge patch failed."), "merge patch failed.");
                                        }

                                    }
                                });
                                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                            } catch (NameNotFoundException var6) {
//                                LogUtils.printE(var6.getMessage());
                            }
                        } else {
                            file.delete();
                        }

                        NoUIUpdateFlow.this.updateDownloadCallback.onStop();
                    }
                }

                public void onStart() {
                }

                public void onPercent(int percent, long rcvLen, long fileSize) {
                    NoUIUpdateFlow.this.updateDownloadCallback.onPercent(percent, rcvLen, fileSize);
                }

                public void onInstead() {
                    NoUIUpdateFlow.this.updateDownloadCallback.onStop();
                }

                public void onFail(Throwable error, String content) {
                    NoUIUpdateFlow.this.updateDownloadCallback.onFail(error, content);
                }
            });
        }
    }
}

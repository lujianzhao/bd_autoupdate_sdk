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
import android.text.TextUtils;

import com.baidu.autoupdatesdk.AppUpdateInfo;
import com.baidu.autoupdatesdk.AppUpdateInfoForInstall;
import com.baidu.autoupdatesdk.ConfirmUpdategActivity;
import com.baidu.autoupdatesdk.ConfirmUpdategActivity.OnActionListener;
import com.baidu.autoupdatesdk.ICallback;
import com.baidu.autoupdatesdk.UICheckUpdateCallback;
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
import com.baidu.autoupdatesdk.utils.NetworkUtils.OnCheckCanDownloadCallback;
import com.baidu.autoupdatesdk.utils.PreferenceUtils;

import java.io.File;

public class UIUpdateFlow {
    private UICheckUpdateCallback checkUpdateCallback;

    public UIUpdateFlow() {
    }

    public void start(Context context, UICheckUpdateCallback callback, boolean useHttps) {
        if (callback != null) {
            this.checkUpdateCallback = callback;
            final Context appCtx = context.getApplicationContext();
            final int ignoreCode = PreferenceUtils.getIgnoreVersionCode(context);
//            LogUtils.printI("ignoreVersionCode: " + ignoreCode);
            if (NetworkUtils.isNetActive(appCtx)) {
                TagRecorder.onTag(context, Tag.newInstance(1));
                ActionFactory.checkAppUpdate(appCtx, new ICallback<AppUpdateInfo>() {
                    public void onCallback(int resultCode, String resultDesc, AppUpdateInfo extraData) {
                        if (resultCode == 10000 && extraData != null) {
//                            LogUtils.printI("newVersionCode: " + extraData.getAppVersionCode());
                            TagRecorder.onTag(appCtx, Tag.newInstance(2));
                            File latestApk = BDDownloadManager.getInstance().getLatestApkFile(appCtx, extraData.getAppVersionCode() - 1, ignoreCode);
                            if (latestApk == null) {
                                if (extraData.getAppVersionCode() > BDUtils.getVersionCode(appCtx) && extraData.getAppVersionCode() != ignoreCode) {
                                    UIUpdateFlow.this.doDownload(appCtx, extraData);
                                } else {
                                    UIUpdateFlow.this.checkUpdateCallback.onCheckComplete();
                                }
                            } else {
                                int versionCode = BDDownloadManager.getInstance().getFileVersionCode(latestApk);
                                UIUpdateFlow.this.doInstallInChecking(appCtx, extraData, latestApk.getAbsolutePath(), versionCode);
                            }
                        } else {
                            UIUpdateFlow.this.checkUpdateCallback.onNoUpdateFound();
                            UIUpdateFlow.this.checkUpdateCallback.onCheckComplete();
                        }

                    }
                }, useHttps);
            } else {
                File latestApk = BDDownloadManager.getInstance().getLatestApkFileExceptIgnore(context, ignoreCode);
                int versionCode = BDDownloadManager.getInstance().getFileVersionCode(latestApk);
                if (latestApk != null) {
                    this.doInstallInChecking(appCtx, (AppUpdateInfo)null, latestApk.getAbsolutePath(), versionCode);
                } else {
                    this.checkUpdateCallback.onCheckComplete();
                }
            }

        }
    }

    private void doDownload(final Context ctx, final AppUpdateInfo info) {
        if (BDUtils.isAppActive(ctx)) {
            String packageSize;
            if (TextUtils.isEmpty(info.getAppPath())) {
                packageSize = BDUtils.byteToMb(info.getAppSize());
            } else {
                packageSize = BDUtils.byteToMb(info.getAppPathSize());
            }

            String mainTip = ctx.getString(ID.getString(ctx, "bdp_update_download_main_tip"), new Object[]{BDUtils.getVersionName(ctx), info.getAppVersionName(), packageSize});
            String minorTip;
            if (!TextUtils.isEmpty(info.getAppChangeLog())) {
                String minor = ctx.getString(ID.getString(ctx, "bdp_update_minor_tip")) + "<br>";
                minorTip = minor + info.getAppChangeLog();
            } else {
                minorTip = "";
            }

            boolean canClose = info.getForceUpdate() != 1;
            TagRecorder.onTag(ctx, Tag.newInstance(3));
            ConfirmUpdategActivity.show(ctx, 1, mainTip, minorTip, canClose, info.getAppVersionCode(), new OnActionListener() {
                public void onUpdate(Context context) {
                    TagRecorder.onTag(context, Tag.newInstance(6));
                    NetworkUtils.checkNetWorkBeforeDownloading(context, new OnCheckCanDownloadCallback() {
                        public void onCallback(boolean isShowDialog, boolean canDownload) {
                            if (isShowDialog) {
                                TagRecorder.onTag(ctx, Tag.newInstance(7));
                                if (canDownload) {
                                    TagRecorder.onTag(ctx, Tag.newInstance(9));
                                } else {
                                    TagRecorder.onTag(ctx, Tag.newInstance(8));
                                }
                            }

                            if (canDownload) {
                                UIUpdateFlow.this.download(ctx, info);
                            }

                        }
                    });
                }

                public void onUpdateRecommend(Context context) {
                    TagRecorder.onTag(context, Tag.newInstance(5));
                    (new AsUpdateFlow()).start(context, true);
                }

                public void onIgnoreVersion(Context context, int ignoreVersion) {
                    PreferenceUtils.setIgnoreVersionCode(context, ignoreVersion);
                }

                public void onClose(Context context) {
                    TagRecorder.onTag(context, Tag.newInstance(4));
                    UIUpdateFlow.this.checkUpdateCallback.onCheckComplete();
                }
            });
        } else {
            BDNotificationManager.getInstance(ctx).newUpdateNotify(info, new OnClickListener() {
                public void onClick(Context context) {
                    UIUpdateFlow.this.download(context, info);
                }
            });
            this.checkUpdateCallback.onCheckComplete();
        }

    }

    private void doInstallAfterDownload(Context ctx, AppUpdateInfo info, final String argApkPath) {
        String appName;
        if (info != null) {
            appName = info.getAppSname();
        } else {
            AppUpdateInfoForInstall afi = PreferenceUtils.getInstallInfo(ctx);
            appName = afi.getAppSName();
        }

        BDNotificationManager.getInstance(ctx).downloadCompleteNotify(appName, new OnClickListener() {
            public void onClick(Context context) {
                ApkUtils.install(context, argApkPath);
            }
        });
        ApkUtils.install(ctx, argApkPath);
    }

    private void doInstallInChecking(Context context, AppUpdateInfo info, final String argApkPath, int versionCode) {
        if (MergePatchTask.isMerging()) {
            this.checkUpdateCallback.onCheckComplete();
        } else {
            String mainTip;
            if (BDUtils.isAppActive(context)) {
                mainTip = null;
                String minorTip = null;
                if (info != null) {
                    mainTip = context.getString(ID.getString(context, "bdp_update_install_main_tip"), new Object[]{BDUtils.getVersionName(context), info.getAppVersionName()});
                    minorTip = info.getAppChangeLog();
                } else {
                    AppUpdateInfoForInstall afi = PreferenceUtils.getInstallInfo(context);
                    if (afi != null) {
                        mainTip = context.getString(ID.getString(context, "bdp_update_install_main_tip"), new Object[]{BDUtils.getVersionName(context), afi.getAppVersionName()});
                        minorTip = afi.getAppChangeLog();
                    }
                }

                if (!TextUtils.isEmpty(minorTip)) {
                    String minor = context.getString(ID.getString(context, "bdp_update_minor_tip")) + "<br>";
                    minorTip = minor + minorTip;
                }

                boolean canClose = info == null || info.getForceUpdate() != 1;
                TagRecorder.onTag(context, Tag.newInstance(3));
                ConfirmUpdategActivity.show(context, 2, mainTip, minorTip, canClose, versionCode, new OnActionListener() {
                    public void onUpdate(Context context) {
                    }

                    public void onUpdateRecommend(Context context) {
                        ApkUtils.install(context, argApkPath);
                    }

                    public void onIgnoreVersion(Context context, int ignoreVersion) {
                        PreferenceUtils.setIgnoreVersionCode(context, ignoreVersion);
                    }

                    public void onClose(Context context) {
                        TagRecorder.onTag(context, Tag.newInstance(4));
                        UIUpdateFlow.this.checkUpdateCallback.onCheckComplete();
                    }
                });
            } else {
                if (info != null) {
                    mainTip = info.getAppSname();
                } else {
                    AppUpdateInfoForInstall afi = PreferenceUtils.getInstallInfo(context);
                    mainTip = afi.getAppSName();
                }

                BDNotificationManager.getInstance(context).downloadCompleteNotify(mainTip, new OnClickListener() {
                    public void onClick(Context context) {
                        ApkUtils.install(context, argApkPath);
                    }
                });
                this.checkUpdateCallback.onCheckComplete();
            }

        }
    }

    @SuppressLint({"NewApi"})
    private void download(final Context ctx, final AppUpdateInfo info) {
        BDDownloadManager.getInstance().startDownload(ctx, DownloadType.uiupdate, info, new OnDownloadProgressListener() {
            public void onSuccess(String downloadPath) {
                File file = new File(downloadPath);
                if (file.exists()) {
                    if (downloadPath.endsWith(".apk")) {
                        UIUpdateFlow.this.doInstallAfterDownload(ctx, info, downloadPath);
                    } else if (downloadPath.endsWith(".xdt")) {
                        try {
                            PackageInfo pkgInfo = ctx.getPackageManager().getPackageInfo(info.getAppPackage(), 0);
                            String oldFile = pkgInfo.applicationInfo.sourceDir;
                            MergePatchTask task = new MergePatchTask(ctx, oldFile, downloadPath, info, new OnMergeCompleteListener() {
                                public void onComplete(boolean success, String savePath) {
                                    if (success) {
                                        UIUpdateFlow.this.doInstallAfterDownload(ctx, info, savePath);
                                    } else {
                                        BDNotificationManager.getInstance(ctx).dismissNotify();
                                    }

                                }
                            });
                            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                        } catch (NameNotFoundException var6) {
//                            LogUtils.printE(var6.getMessage());
                            BDNotificationManager.getInstance(ctx).dismissNotify();
                        }
                    } else {
                        boolean b = file.delete();
//                        LogUtils.printI("file: " + file.getName() + ", delete: " + b);
                        BDNotificationManager.getInstance(ctx).dismissNotify();
                    }

                }
            }

            public void onStart() {
                long size;
                if (TextUtils.isEmpty(info.getAppPath())) {
                    size = info.getAppSize();
                } else {
                    size = info.getAppPathSize();
                }

                BDNotificationManager.getInstance(ctx).downloadNotify(info.getAppSname(), BDUtils.byteToMb(size), 0);
            }

            public void onPercent(int argPercent, long rcvLen, long fileSize) {
                int percent;
                long size;
                if (TextUtils.isEmpty(info.getAppPath())) {
                    percent = argPercent;
                    size = info.getAppSize();
                } else {
                    percent = (int)((double)argPercent * 0.9D);
                    size = info.getAppPathSize();
                }

                BDNotificationManager.getInstance(ctx).downloadNotify(info.getAppSname(), BDUtils.byteToMb(size), percent);
            }

            public void onFail(Throwable error, String content) {
                BDNotificationManager.getInstance(ctx).dismissNotify();
            }

            public void onInstead() {
                BDNotificationManager.getInstance(ctx).dismissNotify();
            }
        });
    }
}

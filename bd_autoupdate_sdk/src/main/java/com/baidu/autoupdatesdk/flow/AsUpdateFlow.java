//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk.flow;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.baidu.autoupdatesdk.analytics.Tag;
import com.baidu.autoupdatesdk.analytics.TagRecorder;
import com.baidu.autoupdatesdk.dialogs.AsConfirmDialog;
import com.baidu.autoupdatesdk.download.AsDownloadManager;
import com.baidu.autoupdatesdk.download.AsDownloadManager.OnDownloadProgressListener;
import com.baidu.autoupdatesdk.download.AsNotificationManager;
import com.baidu.autoupdatesdk.utils.ApkUtils;
import com.baidu.autoupdatesdk.utils.AppSearchUtils;
import com.baidu.autoupdatesdk.utils.BDUtils;
import com.baidu.autoupdatesdk.utils.NetworkUtils;
import com.baidu.autoupdatesdk.utils.NetworkUtils.OnCheckCanDownloadCallback;

import java.io.File;

public class AsUpdateFlow {
    public AsUpdateFlow() {
    }

    public void start(Context context, boolean needUI) {
        if (AppSearchUtils.canAsUpdate(context)) {
            AppSearchUtils.invokeAppSearch(context);
        } else {
            this.doDownloadAs(context, needUI);
        }

    }

    private void doDownloadAs(final Context ctx, boolean needUI) {
        if (BDUtils.isAppActive(ctx)) {
            if (!needUI) {
                this.downloadAs(ctx);
                return;
            }

            TagRecorder.onTag(ctx, Tag.newInstance(11));
            AsConfirmDialog dialog = new AsConfirmDialog(ctx);
            dialog.setBtnInstallListener(new OnClickListener() {
                public void onClick(View v) {
                    TagRecorder.onTag(ctx, Tag.newInstance(12));
                    NetworkUtils.checkNetWorkBeforeDownloading(ctx, new OnCheckCanDownloadCallback() {
                        public void onCallback(boolean isShowDialog, boolean canDownload) {
                            if (isShowDialog) {
                                TagRecorder.onTag(ctx, Tag.newInstance(14));
                                if (canDownload) {
                                    TagRecorder.onTag(ctx, Tag.newInstance(16));
                                } else {
                                    TagRecorder.onTag(ctx, Tag.newInstance(15));
                                }
                            }

                            if (canDownload) {
                                AsUpdateFlow.this.downloadAs(ctx);
                            }

                        }
                    });
                }
            });
            dialog.show();
        } else {
            AsNotificationManager.getInstance(ctx).newUpdateNotify(new com.baidu.autoupdatesdk.download.AsNotificationManager.OnClickListener() {
                public void onClick(Context context) {
                    AsUpdateFlow.this.downloadAs(context);
                }
            });
        }

    }

    private void doInstallAs(Context context, final String argApkPath) {
        AsNotificationManager.getInstance(context).downloadCompleteNotify(new com.baidu.autoupdatesdk.download.AsNotificationManager.OnClickListener() {
            public void onClick(Context context) {
                ApkUtils.installSimple(context, argApkPath);
            }
        });
        ApkUtils.installSimple(context, argApkPath);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PACKAGE_ADDED");
        filter.addDataScheme("package");
        context.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String data = intent.getDataString();
                if (!TextUtils.isEmpty(data) && data.contains("com.baidu.appsearch")) {
                    AsNotificationManager.getInstance(context).dismissNotify();
                }

                context.unregisterReceiver(this);
            }
        }, filter);
    }

    @SuppressLint({"NewApi"})
    private void downloadAs(final Context ctx) {
        AsDownloadManager.getInstance().deleteAllApk(ctx);
        AsDownloadManager.getInstance().startDownload(ctx, new OnDownloadProgressListener() {
            public void onSuccess(String downloadPath) {
                File file = new File(downloadPath);
                if (file.exists()) {
                    if (downloadPath.endsWith(".apk")) {
                        AsUpdateFlow.this.doInstallAs(ctx, downloadPath);
                    } else {
                        boolean b = file.delete();
//                        LogUtils.printI("file: " + file.getName() + ", delete: " + b);
                        AsNotificationManager.getInstance(ctx).dismissNotify();
                    }

                }
            }

            public void onStart() {
                AsNotificationManager.getInstance(ctx).downloadNotify("", 0);
            }

            public void onPercent(int percent, long rcvLen, long fileSize) {
                AsNotificationManager.getInstance(ctx).downloadNotify(BDUtils.byteToMb(fileSize), percent);
            }

            public void onFail(Throwable error, String content) {
                AsNotificationManager.getInstance(ctx).dismissNotify();
            }
        });
    }
}

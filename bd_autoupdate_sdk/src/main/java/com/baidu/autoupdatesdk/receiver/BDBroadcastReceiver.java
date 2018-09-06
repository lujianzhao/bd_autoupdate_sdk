//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.baidu.autoupdatesdk.analytics.Tag;
import com.baidu.autoupdatesdk.analytics.TagRecorder;
import com.baidu.autoupdatesdk.download.AsDownloadManager;
import com.baidu.autoupdatesdk.download.AsNotificationManager;
import com.baidu.autoupdatesdk.download.BDNotificationManager;

public class BDBroadcastReceiver extends BroadcastReceiver {
    public BDBroadcastReceiver() {
    }

    public void onReceive(Context context, Intent intent) {
        if (!"com.baidu.autoupdatesdk.ACTION_NEW_UPDATE".equals(intent.getAction()) && !"com.baidu.autoupdatesdk.ACTION_DOWNLOAD_COMPLETE".equals(intent.getAction())) {
            if (!"com.baidu.autoupdatesdk.ACTION_NEW_AS".equals(intent.getAction()) && !"com.baidu.autoupdatesdk.ACTION_AS_DOWNLOAD_COMPLETE".equals(intent.getAction())) {
                if ("android.intent.action.PACKAGE_ADDED".equals(intent.getAction())) {
                    String data = intent.getDataString();
                    if (!TextUtils.isEmpty(data) && data.contains("com.baidu.appsearch")) {
                        TagRecorder.onTag(context, Tag.newInstance(31));
                        AsNotificationManager.getInstance(context).dismissNotify();
                        AsDownloadManager.getInstance().deleteAllApk(context);
                    }
                }
            } else if (AsNotificationManager.listener != null) {
                AsNotificationManager.listener.onClick(context);
            }
        } else if (BDNotificationManager.listener != null) {
            BDNotificationManager.listener.onClick(context);
        }

    }
}

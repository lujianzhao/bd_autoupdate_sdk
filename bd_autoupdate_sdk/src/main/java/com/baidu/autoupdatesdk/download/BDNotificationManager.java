//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk.download;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.app.NotificationCompat.Builder;

import com.baidu.autoupdatesdk.AppUpdateInfo;
import com.baidu.autoupdatesdk.r.ID;
import com.baidu.autoupdatesdk.receiver.BDBroadcastReceiver;
import com.baidu.autoupdatesdk.utils.BDUtils;

public class BDNotificationManager {
    public static BDNotificationManager.OnClickListener listener;
    public static final String ACTION_NEW_UPDATE = "com.baidu.autoupdatesdk.ACTION_NEW_UPDATE";
    public static final String ACTION_DOWNLOAD_COMPLETE = "com.baidu.autoupdatesdk.ACTION_DOWNLOAD_COMPLETE";
    private static BDNotificationManager instance;
    private int notifyId;
    private Context context;
    private NotificationManager nm;
    private Builder builder;

    private BDNotificationManager(Context context) {
        this.context = context;
        this.nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("download", "下载", NotificationManager.IMPORTANCE_LOW);
            nm.createNotificationChannel(channel);
        }

        this.builder = new Builder(context,"download");
        this.builder.setSmallIcon(ID.getDrawable(context, "bdp_update_logo"));
        this.notifyId = (context.getPackageName() + "com.baidu.autoupdatesdk").hashCode();
//        LogUtils.printI("notifyId: " + this.notifyId);
    }

    public static BDNotificationManager getInstance(Context context) {
        if (instance == null) {
            instance = new BDNotificationManager(context);
        }

        return instance;
    }

    public void newUpdateNotify(AppUpdateInfo info, BDNotificationManager.OnClickListener argListener) {
        listener = argListener;
        Intent clickIntent = new Intent(context, BDBroadcastReceiver.class);
        clickIntent.setAction("com.baidu.autoupdatesdk.ACTION_NEW_UPDATE");
        clickIntent.setPackage(this.context.getPackageName());
        String appName = info.getAppSname();
        String tip = this.context.getString(ID.getString(this.context, "bdp_update_new_download"));
        this.builder.setProgress(0, 0, false)
                .setContentTitle(appName)
                .setContentText(tip)
                .setContentInfo((CharSequence)null)
                .setTicker(tip)
                .setLargeIcon(((BitmapDrawable)BDUtils.getAppIcon(this.context)).getBitmap())
                .setWhen(0L)
                .setContentIntent(PendingIntent.getBroadcast(this.context, this.notifyId, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setAutoCancel(true)
                .setDefaults(4);
        this.nm.notify(this.notifyId, this.builder.build());
    }

    public void downloadCompleteNotify(String appName, BDNotificationManager.OnClickListener argListener) {
        listener = argListener;
        Intent clickIntent = new Intent(context, BDBroadcastReceiver.class);
        clickIntent.setAction("com.baidu.autoupdatesdk.ACTION_DOWNLOAD_COMPLETE");
        clickIntent.setPackage(this.context.getPackageName());

        String tip = this.context.getString(ID.getString(this.context, "bdp_update_download_complete"));
        this.builder.setProgress(0, 0, false)
                .setContentTitle(appName)
                .setContentText(tip)
                .setContentInfo((CharSequence)null)
                .setLargeIcon(((BitmapDrawable)BDUtils.getAppIcon(this.context)).getBitmap())
                .setWhen(0L)
                .setTicker(tip)
                .setContentIntent(PendingIntent.getBroadcast(this.context, this.notifyId, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setAutoCancel(true)
                .setDefaults(4);
        this.nm.cancel(this.notifyId);
        this.nm.notify(this.notifyId, this.builder.build());
    }

    public void downloadNotify(String appName, String size, int percent) {
        String tip = percent > 0 ? "" : this.context.getString(ID.getString(this.context, "bdp_update_tip_waiting"));
        this.builder.setProgress(100, percent, false)
                .setLargeIcon(((BitmapDrawable)BDUtils.getAppIcon(this.context)).getBitmap())
                .setContentTitle(appName)
                .setContentText(tip)
                .setContentInfo(size)
                .setTicker("")
                .setWhen(0L)
                .setContentIntent(PendingIntent.getBroadcast(this.context, this.notifyId, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT))
                .setOngoing(false)
                .setDefaults(4);
        this.nm.notify(this.notifyId, this.builder.build());
    }

    public void dismissNotify() {
        this.nm.cancel(this.notifyId);
    }

    public interface OnClickListener {
        void onClick(Context var1);
    }
}

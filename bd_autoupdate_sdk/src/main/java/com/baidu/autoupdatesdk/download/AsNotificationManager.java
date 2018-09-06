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
import android.os.Build;
import android.support.v4.app.NotificationCompat.Builder;

import com.baidu.autoupdatesdk.r.ID;
import com.baidu.autoupdatesdk.receiver.BDBroadcastReceiver;

public class AsNotificationManager {
    public static AsNotificationManager.OnClickListener listener;
    public static final String ACTION_NEW_AS = "com.baidu.autoupdatesdk.ACTION_NEW_AS";
    public static final String ACTION_AS_DOWNLOAD_COMPLETE = "com.baidu.autoupdatesdk.ACTION_AS_DOWNLOAD_COMPLETE";
    private static AsNotificationManager instance;
    private int notifyId;
    private Context context;
    private NotificationManager nm;
    private Builder builder;

    private AsNotificationManager(Context context) {
        this.context = context;
        this.nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("download", "下载", NotificationManager.IMPORTANCE_LOW);
            nm.createNotificationChannel(channel);
        }

        this.builder = new Builder(context,"download");
        this.builder.setSmallIcon(ID.getDrawable(context, "bdp_update_logo"));
        this.notifyId = (context.getPackageName() + "com.baidu.autoupdatesdk.4as").hashCode();
//        LogUtils.printI("notifyId: " + this.notifyId);
    }

    public static AsNotificationManager getInstance(Context context) {
        if (instance == null) {
            instance = new AsNotificationManager(context);
        }

        return instance;
    }

    public void newUpdateNotify(AsNotificationManager.OnClickListener argListener) {
        listener = argListener;
        Intent clickIntent = new Intent(context, BDBroadcastReceiver.class);
        clickIntent.setAction("com.baidu.autoupdatesdk.ACTION_NEW_AS");
        clickIntent.setPackage(this.context.getPackageName());
        String appName = this.context.getString(ID.getString(this.context, "bdp_update_as_notify_title"));
        String tip = this.context.getString(ID.getString(this.context, "bdp_update_as_notify_tip"));
        this.builder.setProgress(0, 0, false).setContentTitle(appName).setContentText(tip).setContentInfo((CharSequence)null).setTicker(tip).setWhen(0L).setContentIntent(PendingIntent.getBroadcast(this.context, this.notifyId, clickIntent, 134217728)).setAutoCancel(true).setDefaults(4);
        this.nm.notify(this.notifyId, this.builder.build());
    }

    public void downloadCompleteNotify(AsNotificationManager.OnClickListener argListener) {
        listener = argListener;
        Intent clickIntent = new Intent(context, BDBroadcastReceiver.class);
        clickIntent.setAction("com.baidu.autoupdatesdk.ACTION_AS_DOWNLOAD_COMPLETE");
        clickIntent.setPackage(this.context.getPackageName());
        String tip = this.context.getString(ID.getString(this.context, "bdp_update_as_download_complete"));
        String appName = this.context.getString(ID.getString(this.context, "bdp_update_as_notify_title"));
        this.builder.setProgress(0, 0, false).setContentTitle(appName).setContentText(tip).setContentInfo((CharSequence)null).setWhen(0L).setTicker(tip).setContentIntent(PendingIntent.getBroadcast(this.context, this.notifyId, clickIntent, 134217728)).setAutoCancel(true).setDefaults(4);
        this.nm.cancel(this.notifyId);
        this.nm.notify(this.notifyId, this.builder.build());
    }

    public void downloadNotify(String size, int percent) {
        String appName = this.context.getString(ID.getString(this.context, "bdp_update_as_notify_title"));
        String tip = percent > 0 ? "" : this.context.getString(ID.getString(this.context, "bdp_update_tip_waiting"));
        this.builder.setProgress(100, percent, false).setContentTitle(appName).setContentText(tip).setContentInfo(size).setTicker("").setWhen(0L).setContentIntent(PendingIntent.getBroadcast(this.context, this.notifyId, new Intent(), 134217728)).setOngoing(false).setDefaults(4);
        this.nm.notify(this.notifyId, this.builder.build());
    }

    public void dismissNotify() {
        this.nm.cancel(this.notifyId);
    }

    public interface OnClickListener {
        void onClick(Context var1);
    }
}

package com.baidu.autoupdatesdk;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author: lujianzhao
 * @date: 03/09/2018 下午1:08
 * @Description:
 */
public class AppUpdateInfo  implements Parcelable {
    private String appSname;
    private String appVersionName;
    private String appPackage;
    private int appVersionCode;
    private String appUrl;
    private long appSize;
    private String appPath;
    private long appPathSize;
    private String appIconUrl;
    private String appChangeLog;
    private String appMd5;
    private int forceUpdate;
    public static final Creator<AppUpdateInfo> CREATOR = new Creator<AppUpdateInfo>() {
        public AppUpdateInfo createFromParcel(Parcel arg0) {
            AppUpdateInfo entry = new AppUpdateInfo();
            entry.appSname = arg0.readString();
            entry.appVersionName = arg0.readString();
            entry.appPackage = arg0.readString();
            entry.appVersionCode = arg0.readInt();
            entry.appUrl = arg0.readString();
            entry.appSize = arg0.readLong();
            entry.appPath = arg0.readString();
            entry.appPathSize = arg0.readLong();
            entry.appIconUrl = arg0.readString();
            entry.appChangeLog = arg0.readString();
            entry.appMd5 = arg0.readString();
            entry.forceUpdate = arg0.readInt();
            return entry;
        }

        public AppUpdateInfo[] newArray(int arg0) {
            return null;
        }
    };

    public AppUpdateInfo() {
    }

    public AppUpdateInfo(String appSname, String appVersionName, String appPackage, int appVersionCode, String appUrl, long appSize, String appPath, long appPathSize, String appIconUrl, String appChangeLog, String appMd5, int forceUpdate) {
        this.appSname = appSname;
        this.appVersionName = appVersionName;
        this.appPackage = appPackage;
        this.appVersionCode = appVersionCode;
        this.appUrl = appUrl;
        this.appSize = appSize;
        this.appPath = appPath;
        this.appPathSize = appPathSize;
        this.appIconUrl = appIconUrl;
        this.appChangeLog = appChangeLog;
        this.appMd5 = appMd5;
        this.forceUpdate = forceUpdate;
    }

    public String getAppSname() {
        return this.appSname;
    }

    public void setAppSname(String appSname) {
        this.appSname = appSname;
    }

    public String getAppVersionName() {
        return this.appVersionName;
    }

    public void setAppVersionName(String appVersionName) {
        this.appVersionName = appVersionName;
    }

    public String getAppPackage() {
        return this.appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public int getAppVersionCode() {
        return this.appVersionCode;
    }

    public void setAppVersionCode(int appVersionCode) {
        this.appVersionCode = appVersionCode;
    }

    public String getAppUrl() {
        return this.appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public long getAppSize() {
        return this.appSize;
    }

    public void setAppSize(long appSize) {
        this.appSize = appSize;
    }

    public String getAppPath() {
        return this.appPath;
    }

    public void setAppPath(String appPath) {
        this.appPath = appPath;
    }

    public long getAppPathSize() {
        return this.appPathSize;
    }

    public void setAppPathSize(long appPathSize) {
        this.appPathSize = appPathSize;
    }

    public String getAppIconUrl() {
        return this.appIconUrl;
    }

    public void setAppIconUrl(String appIconUrl) {
        this.appIconUrl = appIconUrl;
    }

    public String getAppChangeLog() {
        return this.appChangeLog;
    }

    public void setAppChangeLog(String appChangeLog) {
        this.appChangeLog = appChangeLog;
    }

    public String getAppMd5() {
        return this.appMd5;
    }

    public void setAppMd5(String appMd5) {
        this.appMd5 = appMd5;
    }

    public int getForceUpdate() {
        return this.forceUpdate;
    }

    public void setForceUpdate(int forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.appSname);
        dest.writeString(this.appVersionName);
        dest.writeString(this.appPackage);
        dest.writeInt(this.appVersionCode);
        dest.writeString(this.appUrl);
        dest.writeLong(this.appSize);
        dest.writeString(this.appPath);
        dest.writeLong(this.appPathSize);
        dest.writeString(this.appIconUrl);
        dest.writeString(this.appChangeLog);
        dest.writeString(this.appMd5);
        dest.writeInt(this.forceUpdate);
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk.download;

import android.content.Context;
import android.text.TextUtils;
import com.baidu.autoupdatesdk.AppUpdateInfo;
import com.baidu.autoupdatesdk.analytics.Tag;
import com.baidu.autoupdatesdk.analytics.TagRecorder;
import com.baidu.autoupdatesdk.download.BDFileDownloader.OnFileProgressListener;
import com.baidu.autoupdatesdk.utils.ApkUtils;
import com.baidu.autoupdatesdk.utils.BDUtils;
import java.io.File;
import java.io.FilenameFilter;

public class BDDownloadManager {
    private static BDDownloadManager instance;
    private static BDDownloadManager.ApkFileFilter apkFileFilter;
    private static BDDownloadManager.TmpFileFilter tmpFileFilter;
    private BDFileDownloader downloader;
    private AppUpdateInfo info;
    private BDDownloadManager.DownloadType type;
    private BDDownloadManager.OnDownloadProgressListener listener;

    private BDDownloadManager() {
    }

    public static BDDownloadManager getInstance() {
        if (instance == null) {
            instance = new BDDownloadManager();
        }

        return instance;
    }

    public void startDownload(Context context, BDDownloadManager.DownloadType type, AppUpdateInfo info, BDDownloadManager.OnDownloadProgressListener listener) {
        if (this.info != null && this.info.getAppVersionCode() == info.getAppVersionCode()) {
            if (type == BDDownloadManager.DownloadType.uiupdate && this.type == BDDownloadManager.DownloadType.silence || type == BDDownloadManager.DownloadType.nouiupdate && this.type != BDDownloadManager.DownloadType.nouiupdate) {
                this.setListener(listener);
                this.type = type;
            }

        } else {
            this.type = type;
            this.setListener(listener);
            if (this.downloader != null) {
                this.downloader.stop(true);
                this.downloader = null;
            }

            String path = this.createCacheDir(context);
            long fileSize;
            String url;
            if (!TextUtils.isEmpty(info.getAppPath())) {
                fileSize = info.getAppPathSize();
                url = info.getAppPath();
//                LogUtils.printI("update type: patch");
            } else {
                fileSize = info.getAppSize();
                url = info.getAppUrl();
//                LogUtils.printI("update type: full");
            }

            this.deleteAllTmp(context);
            this.downloader = new BDFileDownloader();
            this.downloader.start(context, path + this.getFileTempName(info), fileSize, url, new BDDownloadManager.OnFileProgressListenerWrapper(context, info));
        }
    }

    private void setListener(BDDownloadManager.OnDownloadProgressListener listener) {
        if (this.listener != null) {
            this.listener.onInstead();
        }

        this.listener = listener;
    }

    public AppUpdateInfo getDownloadingApp() {
        return this.info;
    }

    public int getFileVersionCode(File file) {
        int versionCode = -1;
        if (file == null) {
            return versionCode;
        } else {
            try {
                String name = file.getName();
                int extIndex = name.lastIndexOf(46);
                name = name.substring(0, extIndex);
                versionCode = Integer.valueOf(name.substring(name.lastIndexOf("-") + 1, name.length()));
            } catch (Exception var5) {
//                LogUtils.printRelease(var5.getMessage());
            }

            return versionCode;
        }
    }

    public File getLatestApkFile(Context context, int compareCode, int ignoreCode) {
        File latestFile = null;
        File dir = new File(this.getPkgCacheDir(context));
        if (dir.exists() && dir.isDirectory()) {
            File[] apks = dir.listFiles(this.getApkFileFilter());
            if (apks != null && apks.length != 0) {
                for(int i = 0; i < apks.length; ++i) {
                    File apk = apks[i];
                    if (apk != null) {
                        try {
                            int fileCode = this.getFileVersionCode(apk);
                            if (fileCode > compareCode && fileCode != ignoreCode && fileCode > BDUtils.getVersionCode(context)) {
                                latestFile = apk;
                                continue;
                            }
                        } catch (Exception var10) {
//                            LogUtils.printE(var10.getMessage());
                        }

                        if (!ApkUtils.getInstallApkPath(context).equals(apk.getAbsolutePath())) {
                            apk.delete();
                        }
                    }
                }

//                LogUtils.printI("latestApkPath: " + (latestFile == null ? "" : latestFile.getAbsolutePath()));
                return latestFile;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public File getLatestApkFileExceptIgnore(Context context, int ignoreCode) {
        return this.getLatestApkFile(context, BDUtils.getVersionCode(context), ignoreCode);
    }

    private String getFileName(AppUpdateInfo info) {
        if (info == null) {
            return null;
        } else {
            StringBuilder builder = new StringBuilder(info.getAppPackage());
            builder.append('-');
            builder.append(info.getAppVersionCode());
            if (!TextUtils.isEmpty(info.getAppPath())) {
                builder.append(".xdt");
            } else {
                builder.append(".apk");
            }

            return builder.toString();
        }
    }

    private String getFileTempName(AppUpdateInfo info) {
        if (info == null) {
            return null;
        } else {
            StringBuilder builder = new StringBuilder(info.getAppPackage());
            builder.append('-');
            builder.append(info.getAppVersionCode());
            builder.append(".tmp");
            return builder.toString();
        }
    }

    public String createCacheDir(Context context) {
        String path = this.getPkgCacheDir(context);
        File file = new File(path);

        try {
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception var5) {
//            LogUtils.printE(var5.getMessage());
        }

        return path;
    }

    private String getPkgCacheDir(Context context) {
        File sdDir = context.getExternalFilesDir((String)null);
        if (sdDir == null) {
            sdDir = context.getExternalCacheDir();
        }

        if (sdDir == null) {
            sdDir = context.getCacheDir();
        }

        StringBuilder builder;
        if (sdDir != null) {
            builder = new StringBuilder(sdDir.getAbsolutePath());
        } else {
            builder = new StringBuilder("/sdcard");
        }

        builder.append("/autoupdatecache/");
        return builder.toString();
    }

    private BDDownloadManager.ApkFileFilter getApkFileFilter() {
        if (apkFileFilter == null) {
            apkFileFilter = new BDDownloadManager.ApkFileFilter();
        }

        return apkFileFilter;
    }

    private void deleteAllTmp(Context context) {
        File dir = new File(this.createCacheDir(context));
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles(this.getTmpFileFilter());
            if (files != null && files.length > 0) {
                for(int i = 0; i < files.length; ++i) {
                    files[i].delete();
                }
            }
        }

    }

    private BDDownloadManager.TmpFileFilter getTmpFileFilter() {
        if (tmpFileFilter == null) {
            tmpFileFilter = new BDDownloadManager.TmpFileFilter();
        }

        return tmpFileFilter;
    }

    public static enum DownloadType {
        uiupdate,
        nouiupdate,
        silence;

        private DownloadType() {
        }
    }

    public interface OnDownloadProgressListener {
        void onStart();

        void onPercent(int var1, long var2, long var4);

        void onSuccess(String var1);

        void onFail(Throwable var1, String var2);

        void onInstead();
    }

    private class OnFileProgressListenerWrapper implements OnFileProgressListener {
        private Context context;
        private AppUpdateInfo appUpdateInfo;

        public OnFileProgressListenerWrapper(Context context, AppUpdateInfo argAppUpdateInfo) {
            this.context = context;
            this.appUpdateInfo = argAppUpdateInfo;
        }

        public void onStart() {
            BDDownloadManager.this.info = this.appUpdateInfo;
            if (BDDownloadManager.this.listener != null) {
                BDDownloadManager.this.listener.onStart();
            }

//            LogUtils.printI("download: onStart");
        }

        public void onPercent(int percent, long rcvLen, long fileSize) {
            if (BDDownloadManager.this.listener != null) {
                BDDownloadManager.this.listener.onPercent(percent, rcvLen, fileSize);
            }

        }

        public void onFail(Throwable error, String content) {
            if (BDDownloadManager.this.listener != null) {
                BDDownloadManager.this.listener.onFail(error, content);
                BDDownloadManager.this.listener = null;
            }

            BDDownloadManager.this.info = null;
//            LogUtils.printI("download: onFail " + content);
        }

        public void onPause() {
        }

        public void onSuccess(String downloadPath) {
            File file = new File(BDDownloadManager.this.getPkgCacheDir(this.context) + BDDownloadManager.this.getFileTempName(this.appUpdateInfo));
            if (BDDownloadManager.this.listener != null) {
                if (file.exists()) {
                    TagRecorder.onTag(this.context, Tag.newInstance(10));
                    File newPath = new File(BDDownloadManager.this.getPkgCacheDir(this.context) + BDDownloadManager.this.getFileName(this.appUpdateInfo));
                    file.renameTo(newPath);
                    BDDownloadManager.this.listener.onSuccess(newPath.getAbsolutePath());
                } else {
                    BDDownloadManager.this.listener.onFail(new RuntimeException("download failed."), "download failed.");
                }

                BDDownloadManager.this.listener = null;
            }

            BDDownloadManager.this.info = null;
//            LogUtils.printI("download: onSuccess, " + downloadPath);
        }

        public void onReciver() {
        }
    }

    private static class TmpFileFilter implements FilenameFilter {
        private TmpFileFilter() {
        }

        public boolean accept(File dir, String filename) {
            return !TextUtils.isEmpty(filename) && filename.endsWith(".tmp");
        }
    }

    private static class ApkFileFilter implements FilenameFilter {
        private ApkFileFilter() {
        }

        public boolean accept(File dir, String filename) {
            return !TextUtils.isEmpty(filename) && filename.endsWith(".apk");
        }
    }
}

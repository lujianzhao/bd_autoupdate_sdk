//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk.download;

import android.content.Context;
import android.text.TextUtils;
import com.baidu.autoupdatesdk.analytics.Tag;
import com.baidu.autoupdatesdk.analytics.TagRecorder;
import com.baidu.autoupdatesdk.download.BDFileDownloader.OnFileProgressListener;
import java.io.File;
import java.io.FilenameFilter;

public class AsDownloadManager {
    private static final String url = "http://dl.ops.baidu.com/appsearch_AndroidPhone_1012700a.apk";
    private static AsDownloadManager instance;
    private static AsDownloadManager.ApkFileFilter apkFileFilter;
    private static AsDownloadManager.TmpFileFilter tmpFileFilter;
    private BDFileDownloader downloader;
    private AsDownloadManager.OnDownloadProgressListener listener;

    private AsDownloadManager() {
    }

    public static AsDownloadManager getInstance() {
        if (instance == null) {
            instance = new AsDownloadManager();
        }

        return instance;
    }

    public void startDownload(Context context, AsDownloadManager.OnDownloadProgressListener listener) {
        this.listener = listener;
        if (this.downloader != null) {
            this.downloader.stop(true);
            this.downloader = null;
        }

        String path = this.createCacheDir(context);
        long fileSize = 0L;
        this.deleteAllTmp(context);
        this.downloader = new BDFileDownloader();
        this.downloader.start(context, path + this.getFileTempName(), fileSize, "http://dl.ops.baidu.com/appsearch_AndroidPhone_1012700a.apk", new AsDownloadManager.OnFileProgressListenerWrapper(context));
    }

    public void deleteAllApk(Context context) {
        File dir = new File(this.createCacheDir(context));
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles(this.getApkFileFilter());
            if (files != null && files.length > 0) {
                for(int i = 0; i < files.length; ++i) {
                    files[i].delete();
                }
            }
        }

    }

    private String getFileName() {
        return "com.baidu.appsearch.apk";
    }

    private String getFileTempName() {
        StringBuilder builder = new StringBuilder("com.baidu.appsearch");
        builder.append(".tmp");
        return builder.toString();
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

    private AsDownloadManager.ApkFileFilter getApkFileFilter() {
        if (apkFileFilter == null) {
            apkFileFilter = new AsDownloadManager.ApkFileFilter();
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

    private AsDownloadManager.TmpFileFilter getTmpFileFilter() {
        if (tmpFileFilter == null) {
            tmpFileFilter = new AsDownloadManager.TmpFileFilter();
        }

        return tmpFileFilter;
    }

    public interface OnDownloadProgressListener {
        void onStart();

        void onPercent(int var1, long var2, long var4);

        void onSuccess(String var1);

        void onFail(Throwable var1, String var2);
    }

    private class OnFileProgressListenerWrapper implements OnFileProgressListener {
        private Context context;

        public OnFileProgressListenerWrapper(Context context) {
            this.context = context;
        }

        public void onStart() {
            if (AsDownloadManager.this.listener != null) {
                AsDownloadManager.this.listener.onStart();
            }

//            LogUtils.printI("download: onStart");
        }

        public void onPercent(int percent, long rcvLen, long fileSize) {
            if (AsDownloadManager.this.listener != null) {
                AsDownloadManager.this.listener.onPercent(percent, rcvLen, fileSize);
            }

        }

        public void onFail(Throwable error, String content) {
            if (AsDownloadManager.this.listener != null) {
                AsDownloadManager.this.listener.onFail(error, content);
            }

//            LogUtils.printI("download: onFail " + content);
        }

        public void onPause() {
        }

        public void onSuccess(String downloadPath) {
            File file = new File(AsDownloadManager.this.getPkgCacheDir(this.context) + AsDownloadManager.this.getFileTempName());
            if (AsDownloadManager.this.listener != null) {
                if (file.exists()) {
                    TagRecorder.onTag(this.context, Tag.newInstance(17));
                    File newPath = new File(AsDownloadManager.this.getPkgCacheDir(this.context) + AsDownloadManager.this.getFileName());
                    file.renameTo(newPath);
                    AsDownloadManager.this.listener.onSuccess(newPath.getAbsolutePath());
                } else {
                    AsDownloadManager.this.listener.onFail(new RuntimeException("download failed."), "download failed.");
                }
            }

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

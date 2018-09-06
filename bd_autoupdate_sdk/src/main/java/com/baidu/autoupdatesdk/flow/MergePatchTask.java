//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk.flow;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import com.baidu.appsearch.patchupdate.GDiffPatcher;
import com.baidu.appsearch.patchupdate.Utility;
import com.baidu.autoupdatesdk.AppUpdateInfo;
import com.baidu.autoupdatesdk.download.BDDownloadManager;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

public class MergePatchTask extends AsyncTask<Void, Void, Boolean> {
    private static MergePatchTask.XdtFileFilter xdtFileFilter;
    private static boolean isMerging = false;
    private Context context;
    private String oldFile;
    private String patchFile;
    private String outputFile;
    private MergePatchTask.OnMergeCompleteListener listener;

    public static boolean isMerging() {
        return isMerging;
    }

    public MergePatchTask(Context ctx, String oldFile, String newFile, AppUpdateInfo info, MergePatchTask.OnMergeCompleteListener listener) {
        this.context = ctx;
        this.oldFile = oldFile;
        this.patchFile = newFile;
        this.outputFile = BDDownloadManager.getInstance().createCacheDir(ctx) + info.getAppPackage() + "-" + info.getAppVersionCode() + ".apk";
        this.listener = listener;
    }

    protected Boolean doInBackground(Void... params) {
        isMerging = true;

        try {
            GDiffPatcher patcher = new GDiffPatcher();
            if (Utility.isGzipFile(this.patchFile)) {
                String unGzip = BDDownloadManager.getInstance().createCacheDir(this.context) + "ungzip.xdt";
                GDiffPatcher.unGZip(this.patchFile, unGzip);

                try {
                    patcher.patch(new File(this.oldFile), new File(unGzip), new File(this.outputFile));
                } catch (IOException var6) {
//                    LogUtils.printE(var6.getMessage());
                }
            } else {
                try {
                    patcher.patch(new File(this.oldFile), new File(this.patchFile), new File(this.outputFile));
                } catch (IOException var5) {
//                    LogUtils.printE(var5.getMessage());
                }
            }

            return true;
        } catch (Exception var7) {
//            LogUtils.printE(var7.getMessage());
            return false;
        }
    }

    protected void onPostExecute(Boolean result) {
        isMerging = false;
        if (result) {
//            LogUtils.printI("merge success: " + this.outputFile);
        } else {
//            LogUtils.printI("merge failed.");
        }

        if (this.listener != null) {
            this.listener.onComplete(result, this.outputFile);
        }

        this.deleteAllXdt();
    }

    private void deleteAllXdt() {
        File dir = new File(BDDownloadManager.getInstance().createCacheDir(this.context));
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles(this.getXdtFileFilter());
            if (files != null && files.length > 0) {
                for(int i = 0; i < files.length; ++i) {
                    files[i].delete();
                }
            }
        }

    }

    private MergePatchTask.XdtFileFilter getXdtFileFilter() {
        if (xdtFileFilter == null) {
            xdtFileFilter = new MergePatchTask.XdtFileFilter();
        }

        return xdtFileFilter;
    }

    private static class XdtFileFilter implements FilenameFilter {
        private XdtFileFilter() {
        }

        public boolean accept(File dir, String filename) {
            return !TextUtils.isEmpty(filename) && filename.endsWith(".xdt");
        }
    }

    public interface OnMergeCompleteListener {
        void onComplete(boolean var1, String var2);
    }
}

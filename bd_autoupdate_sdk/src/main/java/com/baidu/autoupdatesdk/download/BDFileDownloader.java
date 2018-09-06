//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk.download;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import com.baidu.autoupdatesdk.http.FileHttpResponseHandler;
import com.baidu.autoupdatesdk.http.SimpleAsyncHttpClient;
import java.lang.ref.WeakReference;

public final class BDFileDownloader {
    private HandlerThread mWriteThread = new HandlerThread("Thread[NdFileDownloader]", 10);
    private BDFileDownloader.FileHttpResponseHandlerWrapper mResponseHandler;
    private WeakReference<SimpleAsyncHttpClient<Object, Object, Object>> mSimpleAsyncHttpClient;
    private String mDownloadUrl;
    private long mFileSize;
    private String mFileFullPath;
    private Context context;
    private volatile boolean mCanceled = false;
    private BDFileDownloader.OnFileProgressListener mListener;
    private BDFileDownloader.FileProgressHandler mUiHandler = new BDFileDownloader.FileProgressHandler();
    private static final int DOWNLOAD_FAIL_MESSAGE = 1;
    private static final int DOWNLOAD_START_MESSAGE = 2;
    private static final int DOWNLOAD_PERCENT_MESSAGE = 3;
    private static final int DOWNLOAD_SUCCESS_MESSAGE = 4;

    public BDFileDownloader() {
        this.mWriteThread.start();
        this.mResponseHandler = new BDFileDownloader.FileHttpResponseHandlerWrapper(new Handler(this.mWriteThread.getLooper()) {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (!BDFileDownloader.this.mCanceled) {
                    BDFileDownloader.this.mResponseHandler.handleMessage(msg);
                }

            }
        });
    }

    public boolean start(Context context, String fileFullPath, long fileSize, String url, BDFileDownloader.OnFileProgressListener listener) {
        if (this.mWriteThread == null) {
            return false;
        } else if (this.mSimpleAsyncHttpClient != null) {
            return false;
        } else {
            this.init(context, url, fileFullPath, fileSize, listener);
            if (this.mListener != null) {
                this.mListener.onStart();
            }

            this.doGet();
            return true;
        }
    }

    private void init(Context ctx, String url, String fileFullPath, long fileSize, BDFileDownloader.OnFileProgressListener listener) {
        this.context = ctx;
        this.mDownloadUrl = url;
        this.mFileSize = fileSize;
        this.mFileFullPath = fileFullPath;
        if (this.mFileSize <= 0L) {
            this.mFileSize = 2147483647L;
        }

        this.mListener = listener;
        this.mResponseHandler.setFileFullPath(fileFullPath);
    }

    private void doGet() {
        this.mSimpleAsyncHttpClient = SimpleAsyncHttpClient.getRequest(this.context, this.mDownloadUrl, this.mResponseHandler);
    }

    private void cancelNetConnection() {
        if (this.mSimpleAsyncHttpClient != null) {
            SimpleAsyncHttpClient<Object, Object, Object> simpleAsyncHttpClient = (SimpleAsyncHttpClient)this.mSimpleAsyncHttpClient.get();
            if (simpleAsyncHttpClient != null) {
                boolean canceled = simpleAsyncHttpClient.cancel(true);
//                LogUtils.printI("NdFileDownloader:stop " + (canceled ? "1" : "0"));
            }

            this.mSimpleAsyncHttpClient = null;
        }

    }

    public void stop(boolean interruput) {
        this.mCanceled = true;
        this.cancelNetConnection();
        this.forceStopReceive();
    }

    private void forceStopReceive() {
        if (this.mWriteThread != null) {
            this.mWriteThread.interrupt();
            this.mWriteThread = null;
        }

        if (this.mResponseHandler != null) {
            this.mResponseHandler.close();
            this.mResponseHandler = null;
        }

    }

    public interface OnFileProgressListener {
        void onStart();

        void onPercent(int var1, long var2, long var4);

        void onFail(Throwable var1, String var2);

        void onPause();

        void onSuccess(String var1);

        void onReciver();
    }

    final class FileProgressHandler extends Handler {
        FileProgressHandler() {
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Object[] response;
            switch(msg.what) {
                case 1:
                    response = (Object[])((Object[])msg.obj);
                    if (BDFileDownloader.this.mListener != null) {
                        BDFileDownloader.this.mListener.onFail((Throwable)response[0], (String)response[1]);
                    }
                    break;
                case 2:
                    if (BDFileDownloader.this.mListener != null) {
                        BDFileDownloader.this.mListener.onStart();
                    }
                    break;
                case 3:
                    response = (Object[])((Object[])msg.obj);
                    if (BDFileDownloader.this.mListener != null) {
                        BDFileDownloader.this.mListener.onPercent((Integer)response[0], (Long)response[1], (Long)response[2]);
                    }
                    break;
                case 4:
                    if (BDFileDownloader.this.mListener != null) {
                        BDFileDownloader.this.mListener.onSuccess(BDFileDownloader.this.mFileFullPath);
                    }
            }

        }

        public void sendDownloadFailMessage(Throwable error, String content) {
            this.sendMessage(this.obtainMessage(1, new Object[]{error, content}));
        }

        public void sendDownloadPercentMessage(int percent, long receiveLength, long fileSize) {
            this.sendMessage(this.obtainMessage(3, new Object[]{percent, receiveLength, fileSize}));
        }

        public void sendDownloadStartMessage() {
            this.sendMessage(this.obtainMessage(2, (Object)null));
        }

        public void sendDownloadSuccessMessage() {
            this.sendMessage(this.obtainMessage(4, (Object)null));
        }
    }

    final class FileHttpResponseHandlerWrapper extends FileHttpResponseHandler {
        private boolean mSuccess = false;

        public FileHttpResponseHandlerWrapper(Handler handler) {
            super(handler);
        }

        public void onDownloadStart() {
            super.onDownloadStart();
            BDFileDownloader.this.mUiHandler.sendDownloadStartMessage();
        }

        public void onDownloadFail(Throwable error, String content) {
            super.onDownloadFail(error, content);
            BDFileDownloader.this.mUiHandler.sendDownloadFailMessage(error, content);
        }

        public void onDownloadPercent(int percent, long receiveLength, long fileSize) {
            super.onDownloadPercent(percent, receiveLength, fileSize);
            BDFileDownloader.this.mUiHandler.sendDownloadPercentMessage(percent, receiveLength, fileSize);
        }

        public void onDownloadSuccess() {
            super.onDownloadSuccess();
            BDFileDownloader.this.mUiHandler.sendDownloadSuccessMessage();
            this.mSuccess = true;
        }

        public void onFailure(Throwable error, String content) {
            super.onFailure(error, content);
            BDFileDownloader.this.cancelNetConnection();
        }

        public void onFinish() {
            super.onFinish();
            if (this.mSuccess) {
                BDFileDownloader.this.stop(true);
            }

        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }

        public void onSegmentReceive(byte[] slice, int length) {
            super.onSegmentReceive(slice, length);
            if (BDFileDownloader.this.mListener != null) {
                BDFileDownloader.this.mListener.onReciver();
            }

        }

        public void onStartReceive(int contentLength, String charset) {
            super.onStartReceive(contentLength, charset);
        }
    }
}

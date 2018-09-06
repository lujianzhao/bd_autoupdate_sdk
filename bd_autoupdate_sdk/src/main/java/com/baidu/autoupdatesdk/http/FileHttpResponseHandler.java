//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk.http;

import android.os.Handler;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileHttpResponseHandler extends AsyncHttpResponseHandler {
    private String mFilePath;
    private RandomAccessFile mRandomFile;
    private long mFileTotalSize = 0L;
    private FileHttpResponseHandler.State mState;
    private int mLastPercent = -100;
    private static final int mStep = 1;

    public FileHttpResponseHandler(Handler handler) {
        super(handler);
    }

    public void setFileFullPath(String fileFullPath) {
        this.mFilePath = fileFullPath;
    }

    public void onStart() {
        super.onStart();
        this.sendDownloadStartMessage();

        try {
            if (this.mRandomFile != null) {
                this.mRandomFile.close();
            }

            this.mRandomFile = new RandomAccessFile(this.mFilePath, "rw");
            long length = this.mRandomFile.length();
            this.mRandomFile.seek(length);
            if (this.mFileTotalSize <= 0L) {
                this.mFileTotalSize = 2147483647L;
            }
        } catch (FileNotFoundException var3) {
            this.sendDownloadFailMessage(var3, (String)null);
//            LogUtils.printE(var3.getMessage());
        } catch (IOException var4) {
            this.sendDownloadFailMessage(var4, (String)null);
//            LogUtils.printE(var4.getMessage());
        }

    }

    public void onDownloadStart() {
//        LogUtils.printI("FileHttpResponseHandler：onDownloadStart");
    }

    public void onDownloadFail(Throwable error, String content) {
//        LogUtils.printI("FileHttpResponseHandler：onDownloadFail");
    }

    public void onDownloadPercent(int percent, long receiveLength, long fileSize) {
        if (percent % 10 == 0) {
//            LogUtils.printI("FileHttpResponseHandler：onDownloadPercent: " + percent);
        }

    }

    public void onDownloadSuccess() {
//        LogUtils.printI("FileHttpResponseHandler：onDownloadSuccess");
    }

    private void sendDownloadFailMessage(Throwable error, String content) {
        this.mState = FileHttpResponseHandler.State.FAIL;
        if (this.mRandomFile != null) {
            try {
                this.mRandomFile.close();
            } catch (Exception var4) {
//                LogUtils.printE(var4.getMessage());
            }

            this.mRandomFile = null;
        }

        this.onDownloadFail(error, content);
    }

    private void sendDownloadPercentMessage(int percent, long receiveLength, long fileSize) {
        if (percent - this.mLastPercent >= 1) {
            this.mLastPercent = percent;
            this.onDownloadPercent(percent, receiveLength, fileSize);
        }
    }

    private void sendDownloadStartMessage() {
        this.mState = FileHttpResponseHandler.State.START;
        this.onDownloadStart();
    }

    private void sendDownloadSuccessMessage() {
        this.mState = FileHttpResponseHandler.State.SUCCESS;
        this.onDownloadSuccess();
    }

    public void onFinish() {
        super.onFinish();
    }

    public void onStartReceive(int contentLength, String charset) {
        super.onStartReceive(contentLength, charset);
        if (this.mState != FileHttpResponseHandler.State.FAIL && this.mRandomFile != null) {
            try {
                long length = this.mRandomFile.length();
                this.mFileTotalSize = (long)contentLength + length;
                this.sendDownloadPercentMessage((int)(length * 100L / this.mFileTotalSize), length, this.mFileTotalSize);
            } catch (IOException var5) {
                this.sendDownloadFailMessage(var5, (String)null);
//                LogUtils.printE(var5.getMessage());
            }

        }
    }

    public void onSegmentReceive(byte[] slice, int length) {
        super.onSegmentReceive(slice, length);
        if (this.mState != FileHttpResponseHandler.State.FAIL && this.mRandomFile != null) {
            try {
                this.mRandomFile.write(slice, 0, length);
                long rcvLength = this.mRandomFile.length();
                this.sendDownloadPercentMessage((int)(rcvLength * 100L / this.mFileTotalSize), rcvLength, this.mFileTotalSize);
            } catch (Exception var5) {
                this.sendDownloadFailMessage(var5, (String)null);
//                LogUtils.printE(var5.getMessage());
            }

        }
    }

    public void onSuccessReceive() {
        super.onSuccessReceive();
        if (this.mState != FileHttpResponseHandler.State.FAIL && this.mRandomFile != null) {
            try {
                this.mRandomFile.close();
                this.mRandomFile = null;
                this.sendDownloadPercentMessage(100, this.mFileTotalSize, this.mFileTotalSize);
                this.sendDownloadSuccessMessage();
            } catch (IOException var2) {
                this.sendDownloadFailMessage(var2, (String)null);
//                LogUtils.printE(var2.getMessage());
            }

        }
    }

    public void close() {
        if (this.mRandomFile != null) {
            try {
                this.mRandomFile.close();
            } catch (Exception var2) {
//                LogUtils.printE(var2.getMessage());
            }

            this.mRandomFile = null;
        }

    }

    public void onFailure(Throwable error, String content) {
        super.onFailure(error, content);
        this.sendDownloadFailMessage(error, content);
    }

    private static enum State {
        NULL,
        START,
        DOWNLIADING,
        FAIL,
        SUCCESS;

        private State() {
        }
    }
}

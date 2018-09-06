//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk.http;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncHttpResponseHandler {
    private static final int FAILURE_MESSAGE = 1;
    private static final int START_MESSAGE = 2;
    private static final int FINISH_MESSAGE = 3;
    private static final int RECEIVE_MESSAGE_START = 4;
    private static final int RECEIVE_MESSAGE_UPDATE = 5;
    private static final int RECEIVE_MESSAGE_END = 6;
    private Handler handler;
    private static final int BUFF_SIZE = 1024;

    public AsyncHttpResponseHandler() {
        if (Looper.myLooper() != null) {
            this.handler = new Handler() {
                public void handleMessage(Message msg) {
                    AsyncHttpResponseHandler.this.handleMessage(msg);
                }
            };
        }

    }

    public AsyncHttpResponseHandler(Handler h) {
        this.handler = h;
    }

    public void onStart() {
//        LogUtils.printI("AsyncHttpResponseHandler:onStart");
    }

    public void onFinish() {
//        LogUtils.printI("AsyncHttpResponseHandler:onFinish");
    }

    public void onStartReceive(int contentLength, String charset) {
    }

    public void onSegmentReceive(byte[] slice, int length) {
    }

    public void onSuccessReceive() {
    }

    /** @deprecated */
    public void onFailure(Throwable error) {
//        LogUtils.printI("AsyncHttpResponseHandler:onFailure:" + error);
    }

    public void onFailure(Throwable error, String content) {
//        LogUtils.printI("AsyncHttpResponseHandler:onFailure");
        this.onFailure(error);
    }

    protected void sendFailureMessage(Throwable e, String responseBody) {
        this.sendMessage(this.obtainMessage(1, new Object[]{e, responseBody}));
    }

    protected void sendStartMessage() {
        this.sendMessage(this.obtainMessage(2, (Object)null));
    }

    protected void sendFinishMessage() {
        this.sendMessage(this.obtainMessage(3, (Object)null));
    }

    protected void sendReceiveStartMessage(int length, String charset) {
        Message msg = this.obtainMessage(4, (Object)null);
        msg.obj = new Object[]{charset, null};
        msg.arg1 = length;
        this.sendMessage(msg);
    }

    protected void sendReceiveUpdateMessage(byte[] slice, int length) {
        Message msg = this.obtainMessage(5, slice);
        msg.arg1 = length;
        this.sendMessage(msg);
    }

    protected void sendReceiveEndMessage() {
        this.sendMessage(this.obtainMessage(6, (Object)null));
    }

    protected void handleFailureMessage(Throwable e, String responseBody) {
        this.onFailure(e, responseBody);
    }

    protected void handleReceiveStartMessage(int length, String charset) {
        this.onStartReceive(length, charset);
    }

    protected void handleReceiveUpdateMessage(byte[] slice, int length) {
        this.onSegmentReceive(slice, length);
    }

    protected void handleReceiveEndMessage() {
        this.onSuccessReceive();
    }

    protected void handleMessage(Message msg) {
        switch(msg.what) {
            case 1:
                Object[] repsonse1 = (Object[])((Object[])msg.obj);
                this.handleFailureMessage((Throwable)repsonse1[0], (String)repsonse1[1]);
                break;
            case 2:
                this.onStart();
                break;
            case 3:
                this.onFinish();
                break;
            case 4:
                Object[] repsonse4 = (Object[])((Object[])msg.obj);
                this.handleReceiveStartMessage(msg.arg1, (String)repsonse4[0]);
                break;
            case 5:
                byte[] segment = (byte[])((byte[])msg.obj);
                this.handleReceiveUpdateMessage(segment, msg.arg1);
                break;
            case 6:
                this.handleReceiveEndMessage();
        }

    }

    protected void sendMessage(Message msg) {
        if (this.handler != null) {
            Thread thread = this.handler.getLooper().getThread();
            if (thread.isAlive() && !thread.isInterrupted()) {
                this.handler.sendMessage(msg);
            }
        } else {
            this.handleMessage(msg);
        }

    }

    protected Message obtainMessage(int responseMessage, Object response) {
        Message msg = null;
        if (this.handler != null) {
            msg = this.handler.obtainMessage(responseMessage, response);
        } else {
            msg = new Message();
            msg.what = responseMessage;
            msg.obj = response;
        }

        return msg;
    }

    URL sendResponseMessage(HttpURLConnection httpURLConnection) {
        try {
            int statusCode = httpURLConnection.getResponseCode();
            String responseBody;
            if (statusCode == 301 || statusCode == 302) {
                httpURLConnection.disconnect();
                responseBody = httpURLConnection.getHeaderField("location");
                if (responseBody == null) {
                    return null;
                }

                responseBody = this.getDirectUrl(responseBody, httpURLConnection.getURL().toString());
                responseBody = responseBody.replace(" ", "%20");
                URL reUrl = new URL(responseBody);
                return reUrl;
            }

            if (statusCode >= 200 && statusCode < 300) {
                InputStream in = httpURLConnection.getInputStream();
                if (in == null) {
                    this.sendReceiveStartMessage(0, (String)null);
                    this.sendReceiveUpdateMessage(new byte[0], 0);
                    this.sendReceiveEndMessage();
                    return null;
                }

                int contentLength = httpURLConnection.getContentLength();
                if (contentLength > 2147483647) {
                    throw new IllegalArgumentException("HTTP entity too large to be buffered in memory");
                }

                if (contentLength < 0) {
                    contentLength = 4096;
                }

                String charset = "Receive Start";
                this.sendReceiveStartMessage(contentLength, charset);
                boolean readDone = false;
                byte[] tmp = null;
                boolean var8 = false;

                do {
                    if (Thread.currentThread().isInterrupted()) {
                        this.sendFailureMessage(new InterruptedException("request interupted!"), (String)null);
                        return null;
                    }

                    if (tmp == null) {
                        tmp = new byte[1024];
                    }

                    int offset = 0;
                    int remain = 1024;

                    do {
                        if (Thread.currentThread().isInterrupted()) {
                            this.sendFailureMessage(new InterruptedException("request interupted!"), (String)null);
                            return null;
                        }

                        int length = in.read(tmp, offset, remain);
                        if (length == -1) {
                            readDone = true;
                            break;
                        }

                        offset += length;
                        remain -= length;
                    } while(remain > 0);

                    if (offset >= 0) {
                        this.sendReceiveUpdateMessage(tmp, offset);
                    }

                    tmp = null;
                } while(!readDone);

                in.close();
                this.sendReceiveEndMessage();
            } else {
//                LogUtils.printE("http " + statusCode);
                responseBody = httpURLConnection.getResponseMessage();
                this.sendFailureMessage(new RuntimeException(statusCode + ": " + responseBody), responseBody);
            }
        } catch (IOException var11) {
//            LogUtils.printE(var11.getMessage());
            this.sendFailureMessage(var11, (String)null);
        }

        return null;
    }

    private String getDirectUrl(String url, String oldUrl) {
        if (url == null) {
            return url;
        } else if (!url.startsWith("http://") && !url.startsWith("https://")) {
            if (!oldUrl.startsWith("http://") && !oldUrl.startsWith("https://")) {
                return url;
            } else {
                String[] httpStr = oldUrl.split("//");
                String[] oldUrls = httpStr[1].split("/");
                return httpStr[0] + "//" + oldUrls[0] + "/" + url;
            }
        } else {
            return url;
        }
    }
}

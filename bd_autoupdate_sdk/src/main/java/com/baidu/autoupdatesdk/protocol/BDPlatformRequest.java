//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk.protocol;

import android.os.Looper;
import com.baidu.autoupdatesdk.ICallback;
import com.baidu.autoupdatesdk.UICallback;
import com.baidu.autoupdatesdk.action.ICancelable;
import com.baidu.autoupdatesdk.utils.NetworkUtils;
import com.baidu.autoupdatesdk.utils.ThreadPoolUtils;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;

public class BDPlatformRequest implements ICancelable {
    private static ExecutorService threadPool = ThreadPoolUtils.newCachedThreadPool();
    private static ExecutorService singleThreadPool = ThreadPoolUtils.newSingleThreadExecutor();
    private boolean cancel = false;

    public static ExecutorService getExecutor() {
        return threadPool;
    }

    public static ExecutorService getHandShakeThreadExecutor() {
        return singleThreadPool;
    }

    private BDPlatformRequest() {
    }

    public static BDPlatformRequest newRequest() {
        return new BDPlatformRequest();
    }

    public <T> void asyncPost(ProtocolCoder<T> coder, ICallback<T> callback) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new RuntimeException("This thread(non_ui) forbids invoke.");
        } else {
            this.post2Pool(coder, callback);
        }
    }

    private <T> void post2Pool(final ProtocolCoder<T> coder, ICallback<T> callback) {
        final ICallback<T> fCallback = UICallback.wrap(callback);
        threadPool.submit(new Runnable() {
            public void run() {
                BDPlatformRequest.this.syncPost(coder, fCallback);
            }
        });
    }

    public <T> void syncPost(ProtocolCoder<T> coder, ICallback<T> callback) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("This thread(ui) forbids invoke.");
        } else if (!NetworkUtils.isNetActive(coder.getAppContext())) {
            coder.setResultError(-1, coder.getNoArgsErrorDesc("Net not connected."));
            coder.applyCallback(callback);
        } else {
            HttpURLConnection httpURLConnection = null;

            try {
                String url = coder.getUrl();
//                LogUtils.printI("request: " + url);
                httpURLConnection = NetworkUtils.getProtocolConnection(coder.getAppContext(), new URL(url));
                httpURLConnection.setRequestMethod("POST");
                byte[] requestBody = coder.onPrepareHttpRequestBody();
                if (requestBody == null) {
                    coder.setResultError(-2147483648, coder.getNoArgsErrorDesc("encode error"));
                    coder.applyCallback(callback);
                    return;
                }

                if (this.cancel) {
                    coder.applyCancel(callback);
                    return;
                }

                OutputStream outputStream = new BufferedOutputStream(httpURLConnection.getOutputStream());
                outputStream.write(requestBody);
                outputStream.flush();
                outputStream.close();
                int statusCode = httpURLConnection.getResponseCode();
                if (statusCode < 200 || statusCode >= 300) {
                    coder.setResultError(-2, coder.getArgsErrorDesc("http %d", new Object[]{statusCode}));
                    coder.applyCallback(callback);
                    return;
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                int bufferSize = true;
                byte[] buffer = new byte[512];
                InputStream in = httpURLConnection.getInputStream();
//                boolean var11 = true;

                int read;
                while((read = in.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, read);
                }

                byte[] contentByteArray = byteArrayOutputStream.toByteArray();
                byteArrayOutputStream.flush();
                byteArrayOutputStream.close();
                if (this.cancel) {
                    coder.applyCancel(callback);
                    return;
                }

                coder.onParseHttpResponseBody(contentByteArray);
                coder.applyCallback(callback);
            } catch (IOException var17) {
                this.onNetError(coder, callback);
//                LogUtils.printE(var17.getMessage());
            } catch (Exception var18) {
                this.onUnknownError(coder, callback, var18.getMessage());
//                LogUtils.printE(var18.getMessage());
            } finally {
                this.release(httpURLConnection);
            }

        }
    }

    private void release(HttpURLConnection httpURLConnection) {
        if (httpURLConnection != null) {
            httpURLConnection.disconnect();
            httpURLConnection = null;
        }

    }

    private <T> void onUnknownError(ProtocolCoder<T> coder, ICallback<T> callback, String message) {
        coder.setResultError(-2147483648, message);
        coder.applyCallback(callback);
    }

    private <T> void onNetError(ProtocolCoder<T> coder, ICallback<T> callback) {
        coder.setResultError(-1, coder.getNoArgsErrorDesc("connect error"));
        coder.applyCallback(callback);
    }

    public void cancel() {
        this.cancel = true;
    }
}

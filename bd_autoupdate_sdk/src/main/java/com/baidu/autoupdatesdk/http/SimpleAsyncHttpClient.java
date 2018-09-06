//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk.http;

import android.content.Context;
import android.os.AsyncTask;
import com.baidu.autoupdatesdk.utils.NetworkUtils;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SimpleAsyncHttpClient<ParamsT, ProgressT, ResultT> extends AsyncTask<ParamsT, ProgressT, ResultT> {
    private static final String GET = "GET";
    private static final String POST = "POST";
    private Context mContext;
    private String mUrl;
    private AsyncHttpResponseHandler mAsyncHttpResponseHandler;
    private String mRequestMethod = "GET";

    private SimpleAsyncHttpClient(Context context, String url, AsyncHttpResponseHandler asyncHttpResponseHandler, String requestMethod) {
        this.mContext = context;
        this.mUrl = url;
        this.mAsyncHttpResponseHandler = asyncHttpResponseHandler;
        this.mRequestMethod = requestMethod;
    }

    private void release(HttpURLConnection httpURLConnection) {
        if (httpURLConnection != null) {
            httpURLConnection.disconnect();
            httpURLConnection = null;
        }

    }

    protected void onPreExecute() {
        super.onPreExecute();
        this.mAsyncHttpResponseHandler.sendStartMessage();
    }

    protected void onPostExecute(ResultT result) {
        super.onPostExecute(result);
        this.mAsyncHttpResponseHandler.sendFinishMessage();
    }

    protected ResultT doInBackground(ParamsT... paramArrayOfParams) {
        try {
            URL url = new URL(this.mUrl);
            this.doRequest(url);
        } catch (MalformedURLException var4) {
            this.mAsyncHttpResponseHandler.sendFailureMessage(var4, (String)null);
        }

        return null;
    }

    private URL doRequest(URL originalURL) {
        HttpURLConnection mHttpURLConnection = null;

        try {
            mHttpURLConnection = NetworkUtils.getDownloadConnection(this.mContext, originalURL);
            mHttpURLConnection.setRequestMethod(this.mRequestMethod);
            URL reURL = this.mAsyncHttpResponseHandler.sendResponseMessage(mHttpURLConnection);
            if (reURL != null) {
                this.doRequest(reURL);
            }
        } catch (IOException var4) {
            this.mAsyncHttpResponseHandler.sendFailureMessage(var4, (String)null);
        }

        this.release(mHttpURLConnection);
        return null;
    }

    public static <ParamsT, ProgressT, ResultT> WeakReference<SimpleAsyncHttpClient<ParamsT, ProgressT, ResultT>> getRequest(Context context, String url, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        SimpleAsyncHttpClient<ParamsT, ProgressT, ResultT> simpleAsyncHttpClient = new SimpleAsyncHttpClient(context, url, asyncHttpResponseHandler, "GET");
        simpleAsyncHttpClient.execute((ParamsT[])null);
        WeakReference<SimpleAsyncHttpClient<ParamsT, ProgressT, ResultT>> wrSimpleAsyncHttpClient = new WeakReference(simpleAsyncHttpClient);
        return wrSimpleAsyncHttpClient;
    }

    public static <ParamsT, ProgressT, ResultT> WeakReference<SimpleAsyncHttpClient<ParamsT, ProgressT, ResultT>> postRequest(Context context, String url, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        SimpleAsyncHttpClient<ParamsT, ProgressT, ResultT> simpleAsyncHttpClient = new SimpleAsyncHttpClient(context, url, asyncHttpResponseHandler, "POST");
        simpleAsyncHttpClient.execute((ParamsT[])null);
        WeakReference<SimpleAsyncHttpClient<ParamsT, ProgressT, ResultT>> wrSimpleAsyncHttpClient = new WeakReference(simpleAsyncHttpClient);
        return wrSimpleAsyncHttpClient;
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import com.baidu.autoupdatesdk.analytics.Tag;
import com.baidu.autoupdatesdk.analytics.TagRecorder;
import com.baidu.autoupdatesdk.dialogs.WarnningDialog;
import com.baidu.autoupdatesdk.dialogs.WarnningDialog.ButtonType;
import com.baidu.autoupdatesdk.r.ID;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Proxy;
import java.net.SocketException;
import java.net.URL;
import java.net.Proxy.Type;
import java.util.Enumeration;

public class NetworkUtils {
    private static WeakReference<Context> mContext;

    public NetworkUtils() {
    }

    public static HttpURLConnection getProtocolConnection(Context context, URL url) throws IOException {
        HttpURLConnection httpURLConnection = null;
        Proxy proxy = getProxy(context, url);
        if (proxy != null) {
            httpURLConnection = (HttpURLConnection)url.openConnection(proxy);
            setCMWAPProperty(context, httpURLConnection, url.getHost());
        } else {
            httpURLConnection = (HttpURLConnection)url.openConnection();
        }

        setProtocolConnectionParams(httpURLConnection);
        return httpURLConnection;
    }

    public static HttpURLConnection getDownloadConnection(Context context, URL url) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
        httpURLConnection.setInstanceFollowRedirects(false);
        return httpURLConnection;
    }

    private static void setCMWAPProperty(Context context, HttpURLConnection httpURLConnection, String host) {
        String currentApnName = getCurrentApnInUse(context);
        if (!TextUtils.isEmpty(currentApnName) && currentApnName.startsWith("CMWAP")) {
            httpURLConnection.setRequestProperty("X-Online-Host", host);
            httpURLConnection.setDoInput(true);
        }

    }

    private static void setProtocolConnectionParams(HttpURLConnection httpURLConnection) {
//        int operationTimeout = true;
        httpURLConnection.setConnectTimeout(15000);
        httpURLConnection.setReadTimeout(15000);
        HttpURLConnection.setFollowRedirects(false);
        httpURLConnection.setInstanceFollowRedirects(false);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setRequestProperty("accept", "*/*");
        httpURLConnection.setRequestProperty("connection", "Keep-Alive");
        httpURLConnection.setRequestProperty("ACCEPT-LANGUAGE", "zh-cn");
        httpURLConnection.setRequestProperty("ACCEPT-CHARSET", "UTF-8");
    }

    private static Proxy getProxy(Context context, URL url) {
        String proxyHost = android.net.Proxy.getDefaultHost();
        int proxyPort = android.net.Proxy.getDefaultPort();
        Proxy proxy = null;
        if (proxyHost != null) {
            proxy = new Proxy(Type.valueOf(url.getProtocol().toUpperCase()), new InetSocketAddress(proxyHost, proxyPort));
        }

        return proxy;
    }

    private static String getCurrentApnInUse(Context mcontext) {
        String name = "no";
        ConnectivityManager manager = (ConnectivityManager)mcontext.getSystemService(Context.CONNECTIVITY_SERVICE);

        try {
            NetworkInfo activeNetInfo = manager.getActiveNetworkInfo();
            if (activeNetInfo != null && activeNetInfo.isAvailable()) {
                name = activeNetInfo.getExtraInfo();
            }
        } catch (Exception var4) {
//            LogUtils.printE(var4.getMessage());
        }

        return TextUtils.isEmpty(name) ? null : name.toUpperCase();
    }

    public static boolean isNetActive(Context context) {
        context = updateContext(context);
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            return info != null && info.isAvailable();
        }
    }

    public static boolean isWifiActive(Context context) {
        ConnectivityManager mConnMgra = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfowifi = mConnMgra.getNetworkInfo(1);
        return networkInfowifi.isAvailable() && networkInfowifi.isConnected();
    }

    public static String getLocalIpAddress() {
        try {
            Enumeration en = NetworkInterface.getNetworkInterfaces();

            while(en.hasMoreElements()) {
                NetworkInterface intf = (NetworkInterface)en.nextElement();
                Enumeration enumIpAddr = intf.getInetAddresses();

                while(enumIpAddr.hasMoreElements()) {
                    InetAddress inetAddress = (InetAddress)enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = inetAddress.getHostAddress();
                        if (inetAddress instanceof Inet4Address) {
                            return ip;
                        }
                    }
                }
            }
        } catch (SocketException var5) {
//            LogUtils.printE(var5.getMessage());
        }

        return null;
    }

    private static Context updateContext(Context context) {
        if (context != null) {
            mContext = new WeakReference(context.getApplicationContext());
        } else if (mContext != null) {
            context = (Context)mContext.get();
        }

        return context;
    }

    public static void checkNetWorkBeforeDownloading(Context context, final NetworkUtils.OnCheckCanDownloadCallback callback) {
        if (!isNetActive(context)) {
            Toast.makeText(context, ID.getString(context, "bdp_update_request_net_error"), Toast.LENGTH_SHORT).show();
            if (callback != null) {
                callback.onCallback(false, false);
            }

        } else if (isWifiActive(context)) {
            if (callback != null) {
                callback.onCallback(false, true);
            }

        } else {
            WarnningDialog dialog = new WarnningDialog(context);
            dialog.setContent(context.getString(ID.getString(context, "bdp_update_no_wifi_confirm_tip"))).setButtonA(context.getString(ID.getString(context, "bdp_update_no_wifi_confirm_continue")), new View.OnClickListener() {
                public void onClick(View v) {
                    TagRecorder.onTag(v.getContext(), Tag.newInstance(9));
                    if (callback != null) {
                        callback.onCallback(true, true);
                    }

                }
            }).setButtonB(context.getString(ID.getString(context, "bdp_update_no_wifi_confirm_stop")), new View.OnClickListener() {
                public void onClick(View v) {
                    TagRecorder.onTag(v.getContext(), Tag.newInstance(8));
                    if (callback != null) {
                        callback.onCallback(true, false);
                    }

                }
            }, ButtonType.notSuggestion);
            dialog.show();
        }
    }

    public interface OnCheckCanDownloadCallback {
        void onCallback(boolean var1, boolean var2);
    }
}

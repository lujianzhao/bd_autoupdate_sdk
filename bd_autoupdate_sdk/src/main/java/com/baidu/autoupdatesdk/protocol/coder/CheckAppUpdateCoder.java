//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk.protocol.coder;

import android.content.Context;
import android.os.Build.VERSION;
import android.text.TextUtils;
import com.baidu.autoupdatesdk.AppUpdateInfo;
import com.baidu.autoupdatesdk.protocol.Constant;
import com.baidu.autoupdatesdk.protocol.Pair;
import com.baidu.autoupdatesdk.protocol.ProtocolCoder;
import com.baidu.autoupdatesdk.utils.BDUtils;
import com.baidu.autoupdatesdk.utils.DeviceUtils;
import com.baidu.autoupdatesdk.utils.FileUtils;
import com.baidu.autoupdatesdk.utils.JsonUtils;
import com.baidu.autoupdatesdk.utils.ManifestUtils;
import com.baidu.autoupdatesdk.utils.NdPackageParser;
import com.baidu.autoupdatesdk.utils.NetworkUtils;
import com.baidu.autoupdatesdk.utils.PreferenceUtils;
import java.io.File;
import org.json.JSONException;
import org.json.JSONObject;

public class CheckAppUpdateCoder extends ProtocolCoder<AppUpdateInfo> {
    private int appId;
    private String appKey;
    private String appPackage;
    private String appVersionCode;
    private String appSignMD5;
    private String mMAC;
    private String mCID;
    private String mBEAR;
    private String mDPI;
    private String apiLevel;
    private boolean useHttps;

    protected CheckAppUpdateCoder(Context context, String baseUrl) {
        super(context, baseUrl);
    }

    public static CheckAppUpdateCoder newInstance(Context context) {
        return newInstance(context, false);
    }

    public static CheckAppUpdateCoder newInstance(Context context, boolean useHttps) {
        CheckAppUpdateCoder coder = new CheckAppUpdateCoder(context, Constant.getDefaultUrl());
        coder.setActionID((short)1001);
        coder.useHttps = useHttps;
        coder.appId = ManifestUtils.getAppID(context);
        coder.appKey = ManifestUtils.getAppKey(context);
        coder.appPackage = context.getPackageName();
        coder.appVersionCode = BDUtils.getVersionCode(context) + "";
        coder.appSignMD5 = BDUtils.getAppSignMd5(context);
        coder.mMAC = getMAC(context);
        coder.mCID = BDUtils.getCellID(context) + "";
        coder.mBEAR = NetworkUtils.isWifiActive(context) ? "wf" : "3g";
        coder.mDPI = DeviceUtils.getScreenWidth(context) + "_" + DeviceUtils.getScreenHeight(context);
        coder.apiLevel = VERSION.SDK_INT + "";
        return coder;
    }

    protected JSONObject onPrepareRequestBody() throws JSONException {
        String appMD5 = PreferenceUtils.getInstalledMD5(this.getAppContext());
        NdPackageParser parser = new NdPackageParser(this.getAppContext(), this.getAppContext().getPackageName());
        parser.parse();
        File installed = new File(parser.pkg.installPath);
        if (installed != null && installed.exists()) {
            long lastModified = PreferenceUtils.getInstalledLastModified(this.getAppContext());
            if (lastModified != installed.lastModified()) {
                appMD5 = FileUtils.getFileMD5(parser.pkg.installPath);
                PreferenceUtils.setInstalledLastModified(this.getAppContext(), lastModified);
                PreferenceUtils.setInstalledMD5(this.getAppContext(), appMD5);
            }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("AppId", this.appId);
        jsonObject.put("AppKey", this.appKey);
        jsonObject.put("AppPackage", this.appPackage);
        jsonObject.put("AppVersionCode", this.appVersionCode);
        jsonObject.put("AppSignMD5", this.appSignMD5);
        jsonObject.put("AppMD5", appMD5);
        jsonObject.put("MAC", this.mMAC);
        jsonObject.put("CID", this.mCID);
        jsonObject.put("BEAR", this.mBEAR);
        jsonObject.put("DPI", this.mDPI);
        jsonObject.put("ApiLevel", this.apiLevel);
        jsonObject.put("prot", this.useHttps ? "https" : "http");
        return jsonObject;
    }

    protected boolean onParseBody(int resultCode, Pair<String, AppUpdateInfo> result, JSONObject object) {
        if (resultCode == 10000 && object != null) {
            String appSname = JsonUtils.stringTypeValue(object, "AppSname");
            if (TextUtils.isEmpty(appSname)) {
                result.first = this.getAbsentErrorDesc("AppSname");
                return false;
            } else {
                String appVersionName = JsonUtils.stringTypeValue(object, "AppVersionName");
                if (TextUtils.isEmpty(appVersionName)) {
                    result.first = this.getAbsentErrorDesc("AppVersionName");
                    return false;
                } else {
                    String appPackage = JsonUtils.stringTypeValue(object, "AppPackage");
                    if (TextUtils.isEmpty(appPackage)) {
                        result.first = this.getAbsentErrorDesc("AppPackage");
                        return false;
                    } else {
                        Number appVersionCode = JsonUtils.numberTypeValue(object, "AppVersionCode");
                        if (appVersionCode == null) {
                            result.first = this.getAbsentErrorDesc("AppVersionCode");
                            return false;
                        } else {
                            String appUrl = JsonUtils.stringTypeValue(object, "AppUrl");
                            if (TextUtils.isEmpty(appUrl)) {
                                result.first = this.getAbsentErrorDesc("AppUrl");
                                return false;
                            } else {
                                Number appSize = JsonUtils.numberTypeValue(object, "AppSize");
                                if (appSize == null) {
                                    result.first = this.getAbsentErrorDesc("AppSize");
                                    return false;
                                } else {
                                    String appPath = JsonUtils.stringTypeValue(object, "AppPath");
                                    Number appPathSize = JsonUtils.numberTypeValue(object, "AppPathSize");
                                    String appIconUrl = JsonUtils.stringTypeValue(object, "AppIconUrl");
                                    if (TextUtils.isEmpty(appIconUrl)) {
                                        result.first = this.getAbsentErrorDesc("AppIconUrl");
                                        return false;
                                    } else {
                                        String appChangeLog = JsonUtils.stringTypeValue(object, "AppChangeLog");
                                        String appMd5 = JsonUtils.stringTypeValue(object, "AppMd5");
                                        Number forceUpdate = JsonUtils.numberTypeValue(object, "ForceUpdate");
                                        if (forceUpdate == null) {
                                            result.first = this.getAbsentErrorDesc("ForceUpdate");
                                            return false;
                                        } else {
                                            AppUpdateInfo info = new AppUpdateInfo(appSname, appVersionName, appPackage, appVersionCode.intValue(), appUrl, appSize.longValue(), appPath, appPathSize == null ? 0L : appPathSize.longValue(), appIconUrl, appChangeLog, appMd5, forceUpdate.intValue());
                                            result.second = info;
                                            PreferenceUtils.setInstallInfo(this.getAppContext(), info);
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            return true;
        }
    }

    private static String getMAC(Context context) {
        String value = DeviceUtils.getMAC(context);
        if (!TextUtils.isEmpty(value)) {
            StringBuilder sb = new StringBuilder();

            for(int i = 0; i < value.length(); ++i) {
                char c = value.charAt(i);
                if (Character.isLetterOrDigit(c)) {
                    sb.append(c);
                }
            }

            return sb.toString().toUpperCase(Constant.default_Locale);
        } else {
            return "";
        }
    }
}

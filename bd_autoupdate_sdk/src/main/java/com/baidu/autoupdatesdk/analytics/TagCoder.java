//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk.analytics;

import android.content.Context;
import android.os.Build.VERSION;
import android.text.TextUtils;

import com.baidu.android.common.util.CommonParam;
import com.baidu.autoupdatesdk.protocol.Constant;
import com.baidu.autoupdatesdk.protocol.Pair;
import com.baidu.autoupdatesdk.protocol.ProtocolCoder;
import com.baidu.autoupdatesdk.utils.BDUtils;
import com.baidu.autoupdatesdk.utils.DeviceUtils;
import com.baidu.autoupdatesdk.utils.ManifestUtils;
import com.baidu.autoupdatesdk.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

public class TagCoder extends ProtocolCoder<Void> {
    private int appId;
    private String appKey;
    private String appPackage;
    private String appVersionCode;
    private String mCUID;
    private String mMAC;
    private String mDPI;
    private String apiLevel;
    private List<Tag> tags;

    protected TagCoder(Context context, String baseUrl) {
        super(context, baseUrl);
    }

    public static TagCoder newInstance(Context context, List<Tag> tags) {
        TagCoder coder = new TagCoder(context, Constant.getDefaultUrl());
        coder.setActionID((short)1004);
        coder.appId = ManifestUtils.getAppID(context);
        coder.appKey = ManifestUtils.getAppKey(context);
        coder.appPackage = context.getPackageName();
        coder.appVersionCode = BDUtils.getVersionCode(context) + "";
        coder.mCUID = getCUID(context);
        coder.mMAC = getMAC(context);
        coder.mDPI = DeviceUtils.getScreenWidth(context) + "_" + DeviceUtils.getScreenHeight(context);
        coder.apiLevel = VERSION.SDK_INT + "";
        coder.tags = tags;
        return coder;
    }

    protected JSONObject onPrepareRequestBody() throws JSONException {
        Context context = this.getAppContext();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("AppId", this.appId);
        jsonObject.put("AppKey", this.appKey);
        jsonObject.put("AppPackage", this.appPackage);
        jsonObject.put("AppVersionCode", this.appVersionCode);
        jsonObject.put("CUID", this.mCUID);
        jsonObject.put("MAC", this.mMAC);
        jsonObject.put("DPI", this.mDPI);
        jsonObject.put("ApiLevel", this.apiLevel);
        jsonObject.put("IPAddress", NetworkUtils.getLocalIpAddress());
        jsonObject.put("MobileModels", DeviceUtils.getPhoneType(context));
        JSONArray array = new JSONArray();
        Iterator var4 = this.tags.iterator();

        while(var4.hasNext()) {
            Tag tag = (Tag)var4.next();
            array.put(tag.toJSON());
        }

        jsonObject.put("ActionContent", array);
        return jsonObject;
    }

    protected boolean onParseBody(int resultCode, Pair<String, Void> result, JSONObject object) {
        return true;
    }

    private static String getCUID(Context context) {
        String value = "";

        try {
            value = CommonParam.getCUID(context);
        } catch (Exception var3) {
//            LogUtils.printE(var3.getMessage());
        }

        if (value == null) {
            value = "";
        }

        return value;
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

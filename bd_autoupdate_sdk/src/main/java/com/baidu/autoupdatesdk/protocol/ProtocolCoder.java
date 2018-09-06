//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk.protocol;

import android.content.Context;
import android.text.TextUtils;

import com.baidu.autoupdatesdk.ICallback;
import com.baidu.autoupdatesdk.protocol.crypto.Base64;
import com.baidu.autoupdatesdk.protocol.crypto.DESede;
import com.baidu.autoupdatesdk.r.ID;
import com.baidu.autoupdatesdk.utils.BDUtils;
import com.baidu.autoupdatesdk.utils.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ProtocolCoder<T> {
    private static final String sCharset = "utf-8";
    private String baseUrl;
    private Context appContext;
    private int actionID;
    private int resultCode;
    private String resultDesc;
    private T extraData;

    protected ProtocolCoder(Context context, String baseUrl) {
        this.baseUrl = baseUrl;
        this.appContext = context.getApplicationContext();
    }

    final String getUrl() {
        String url = this.updateURL();
        if (!TextUtils.isEmpty(url)) {
            this.baseUrl = url;
        }

        return this.baseUrl.replaceFirst("_ActionID", this.actionID + "").replaceFirst("_Ver", "1.3.1");
    }

    protected String updateURL() {
        return "";
    }

    protected final Context getAppContext() {
        return this.appContext;
    }

    final void applyCancel(ICallback<T> callback) {
    }

    final void setResultError(int resultCode, String resultDesc) {
        this.resultCode = resultCode;
        this.resultDesc = resultDesc;
    }

    final void setResult(int resultCode, String resultDesc, T extraData) {
        this.resultCode = resultCode;
        this.resultDesc = resultDesc;
        this.extraData = extraData;
    }

    public final int getResultCode() {
        return this.resultCode;
    }

    public final String getResultDesc() {
        return this.resultDesc;
    }

    public final T getExtraData() {
        return this.extraData;
    }

    protected final void setActionID(short actionID) {
        this.actionID = actionID;
    }

    protected boolean onParseBody(int resultCode, Pair<String, T> result, JSONObject object) {
        return false;
    }

    protected JSONObject onPrepareRequestBody() throws JSONException {
        return null;
    }

    private JSONObject decodeJSON(byte[] decodedBody) {
        try {
            return (JSONObject)JsonUtils.parseResponse(decodedBody, "utf-8");
        } catch (Exception var3) {
//            LogUtils.printE(var3.getMessage());
            return null;
        }
    }

    final void onParseHttpResponseBody(byte[] buffer) {
        JSONObject object = this.decodeJSON(buffer);
        if (object == null) {
            this.setResultError(-2, this.getNoArgsErrorDesc("json error"));
        } else {
            String bodyResultCode = object.optString("ResultCode", (String)null);
            if (TextUtils.isEmpty(bodyResultCode)) {
                this.setResultError(-2, this.getAbsentErrorDesc("ResultCode"));
            } else {
                boolean var4 = true;

                int opResultCode;
                try {
                    opResultCode = Integer.parseInt(bodyResultCode);
                } catch (NumberFormatException var11) {
                    this.setResultError(-2, this.getFormatErrorDesc("ResultCode"));
                    return;
                }

                String opErrprDesc = object.optString("ResultMsg", (String)null);
                if (opResultCode != 10000) {
                    this.setResultError(opResultCode, opErrprDesc);
                }

                String contentJson = null;
                JSONObject content = null;

                try {
                    byte[] by = Base64.decode(object.optString("Content", "").getBytes("utf-8"));
                    contentJson = new String(decryptTrippleDes(BDUtils.hexStringToBytes("78ce10521a046e95ed8c5bc1bba12a6029bee2769576d532"), by), "utf-8");
                    content = new JSONObject(contentJson);
                } catch (Exception var10) {
//                    LogUtils.printE(var10.getMessage());
                }

                if (content == null) {
                    this.setResultError(-2, this.getFormatErrorDesc("Content"));
                }

                Pair<String, T> result = new Pair((Object)null, (Object)null);
                if (!this.onParseBody(opResultCode, result, content)) {
                    this.setResultError(-2, (String)result.first);
                } else {
                    String resultDesc = !TextUtils.isEmpty(opErrprDesc) ? opErrprDesc : (String)result.first;
                    this.setResult(opResultCode, resultDesc, result.second);
                }

            }
        }
    }

    final String getNoArgsErrorDesc(String reason) {
        Context context = this.getAppContext();
        return context.getString(ID.getString(context, "bdp_update_request_net_error"));
    }

    protected final String getAbsentErrorDesc(String field) {
        Context context = this.getAppContext();
        return context.getString(ID.getString(context, "bdp_update_request_net_error"));
    }

    protected final String getFormatErrorDesc(String field) {
        Context context = this.getAppContext();
        return context.getString(ID.getString(context, "bdp_update_request_net_error"));
    }

    protected final String getArgsErrorDesc(String fmt, Object... formatArgs) {
        Context context = this.getAppContext();
        return context.getString(ID.getString(context, "bdp_update_request_net_error"));
    }

    final void applyCallback(ICallback<T> callback) {
        try {
            this.onPreApplyCallback(this.resultCode, this.resultDesc, this.extraData);
            if (callback != null) {
                callback.onCallback(this.resultCode, this.resultDesc, this.extraData);
            }
        } catch (Exception var3) {
//            LogUtils.printE(var3.getMessage());
        }

    }

    private void onPreApplyCallback(int resultCode2, String resultDesc2, T extraData2) {
//        LogUtils.printRelease("ACT:" + this.actionID + ",resultCode:" + this.resultCode + ",resultDesc:" + this.resultDesc);
    }

    public byte[] onPrepareHttpRequestBody() {
        try {
            return this.assembleHttpRequestBody(this.onPrepareBody());
        } catch (Exception var2) {
//            LogUtils.printE(var2.getMessage());
            return null;
        }
    }

    protected final byte[] onPrepareBody() throws JSONException, UnsupportedEncodingException {
        JSONObject object = this.onPrepareRequestBody();
        byte[] json = null;
        if (object != null) {
            String payload = object.toString();
//            LogUtils.printI("Post: " + payload);
            json = payload.getBytes("utf-8");
        } else {
//            LogUtils.printI("Post: NULL");
        }

        return json;
    }

    private byte[] assembleHttpRequestBody(byte[] raw) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, Exception {
        return raw == null ? null : Base64.encode(trippleDes(BDUtils.hexStringToBytes("78ce10521a046e95ed8c5bc1bba12a6029bee2769576d532"), raw));
    }

    private static byte[] trippleDes(byte[] key, byte[] buffer) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, Exception {
        if (key == null) {
            throw new IllegalArgumentException("Invalid Session");
        } else {
            DESede des = new DESede(key);
            return des.encrypt(buffer);
        }
    }

    private static byte[] decryptTrippleDes(byte[] trippleDesKey, byte[] buffer) {
        if (trippleDesKey == null) {
            throw new IllegalArgumentException("Invalid Session");
        } else {
            byte[] result = new byte[buffer.length];
            DESede des = new DESede(trippleDesKey);

            try {
                result = des.decrypt(buffer);
            } catch (Exception var5) {
                result = null;
            }

            return result;
        }
    }
}

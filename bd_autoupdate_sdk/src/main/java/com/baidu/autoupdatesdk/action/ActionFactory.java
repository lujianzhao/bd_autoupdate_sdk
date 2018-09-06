//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk.action;

import android.content.Context;
import com.baidu.autoupdatesdk.AppUpdateInfo;
import com.baidu.autoupdatesdk.ICallback;
import com.baidu.autoupdatesdk.protocol.BDPlatformRequest;
import com.baidu.autoupdatesdk.protocol.coder.CheckAppUpdateCoder;

public class ActionFactory {
    public static final int RESULT_CODE_SUCCESS = 10000;
    public static final int RESULT_CODE_NO_UPDATE = 30000;

    private ActionFactory() {
    }

    public static void checkAppUpdate(Context context, ICallback<AppUpdateInfo> callback, boolean useHttps) {
        CheckAppUpdateCoder coder = CheckAppUpdateCoder.newInstance(context, useHttps);
        BDPlatformRequest action = BDPlatformRequest.newRequest();
        action.asyncPost(coder, callback);
    }
}

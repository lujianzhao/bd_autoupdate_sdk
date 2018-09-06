//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk.analytics;

import com.baidu.autoupdatesdk.utils.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Tag {
    private int actionStatus;
    private String actionTime;

    Tag(int status) {
        this.actionStatus = status;
        this.actionTime = DateUtils.parseDate(new Date(), "yyyy-MM-dd HH:mm:ss");
    }

    public static Tag newInstance(int status) {
        return new Tag(status);
    }

    public JSONObject toJSON() {
        JSONObject result = new JSONObject();

        try {
            result.put("ActionStatus", this.actionStatus);
            result.put("ActionTime", this.actionTime);
        } catch (JSONException var3) {
//            LogUtils.printRelease(var3.getMessage());
        }

        return result;
    }
}

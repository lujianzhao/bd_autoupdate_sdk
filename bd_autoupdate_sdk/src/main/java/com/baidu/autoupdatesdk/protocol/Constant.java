//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk.protocol;

import java.util.Locale;

public class Constant {
    public static final boolean debug = false;
    public static final Locale default_Locale;
    private static final String DEFAULT_API_URL = "http://srsdk.baidu.com/appuapi/callapi?ActionID=_ActionID&Ver=_Ver&Source=1";
    private static final String DEFAULT_API_URL_DEBUG = "appuapi/callapi?ActionID=_ActionID&Ver=_Ver&Source=1";
    public static final String SDK_VERSION = "1.3.1";

    public Constant() {
    }

    public static String getDefaultUrl() {
        return "http://srsdk.baidu.com/appuapi/callapi?ActionID=_ActionID&Ver=_Ver&Source=1";
    }

    static {
        default_Locale = Locale.US;
    }
}

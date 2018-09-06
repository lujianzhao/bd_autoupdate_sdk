//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk;

public interface CPUpdateDownloadCallback {
    void onStart();

    void onPercent(int var1, long var2, long var4);

    void onDownloadComplete(String var1);

    void onFail(Throwable var1, String var2);

    void onStop();
}

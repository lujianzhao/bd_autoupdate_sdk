//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk;

import android.os.Handler;
import android.os.Looper;

public class UICallback<T> implements ICallback<T> {
    private ICallback<T> base;

    public static <T> UICallback<T> wrap(ICallback<T> base) {
        return new UICallback(base);
    }

    private UICallback(ICallback<T> base) {
        this.base = base;
    }

    public void onCallback(int resultCode, String resultDesc, T extraData) {
        if (this.base != null) {
            Looper looper = Looper.myLooper();
            if (looper != Looper.getMainLooper()) {
                this.dispatch2UI(resultCode, resultDesc, extraData);
            } else {
                try {
                    this.base.onCallback(resultCode, resultDesc, extraData);
                } catch (Exception var6) {
//                    LogUtils.printE(var6.getMessage());
                }
            }

        }
    }

    private void dispatch2UI(final int resultCode, final String resultDesc, final T extraData) {
        (new Handler(Looper.getMainLooper())).post(new Runnable() {
            public void run() {
                try {
                    UICallback.this.base.onCallback(resultCode, resultDesc, extraData);
                } catch (Exception var2) {
//                    LogUtils.printE(var2.getMessage());
                }

            }
        });
    }
}

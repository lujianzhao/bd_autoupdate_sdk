//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk.utils;

import android.annotation.SuppressLint;
import android.os.Build.VERSION;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolUtils {
    private ThreadPoolUtils() {
    }

    @SuppressLint({"NewApi"})
    public static ExecutorService newSingleThreadExecutor() {
        ThreadPoolExecutor tp = new ThreadPoolExecutor(1, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue());
        if (VERSION.SDK_INT >= 9) {
            tp.allowCoreThreadTimeOut(true);
        }

        return tp;
    }

    public static ExecutorService newCachedThreadPool() {
        return Executors.newCachedThreadPool();
    }
}

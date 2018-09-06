//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk.utils;

import android.content.Context;

public class BspatchUtil {
    public BspatchUtil() {
    }

    public static String getLibDir(Context ctx) {
        String libDir;
        try {
            String cacheDir = ctx.getCacheDir().getCanonicalPath();
            libDir = cacheDir.replace("cache", "lib");
        } catch (Exception var4) {
//            LogUtils.printE(var4.getMessage());
            libDir = "/data/data/" + ctx.getPackageName() + "/lib";
        }

        return libDir;
    }

    public static boolean patch(Context ctx, String oldFile, String newFile, String patchFile) {
        return patch(getLibDir(ctx), oldFile, newFile, patchFile);
    }

    private static native boolean patch(String var0, String var1, String var2, String var3);

    static {
        System.loadLibrary("bspatch");
    }
}

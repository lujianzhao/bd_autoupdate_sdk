//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk.utils;

import android.text.TextUtils;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public DateUtils() {
    }

    public static String parseDate(Date date, String pattern) {
        if (TextUtils.isEmpty(pattern)) {
            return null;
        } else {
            DateFormat format = new SimpleDateFormat(pattern, Locale.CHINA);
            return format.format(date);
        }
    }

    public static Date parseString(String date, String pattern) {
        if (!TextUtils.isEmpty(pattern)) {
            SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.CHINA);

            try {
                return format.parse(date);
            } catch (ParseException var4) {
//                LogUtils.printE(var4.getMessage());
            }
        }

        return null;
    }

    public static Date parseLong(long times) {
        return new Date(times);
    }
}

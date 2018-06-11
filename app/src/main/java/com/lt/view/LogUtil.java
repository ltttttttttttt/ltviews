package com.lt.view;

import android.util.Log;

/**
 * 作   用:   打印log之类的帮助类
 */

public class LogUtil {
    final static boolean isDebug = BuildConfig.DEBUG;

    /**
     * 打破4k限制打印log
     */
    public static void i(String tagName, String msg) {
        if (!isDebug)
            return;
        int strLength = msg.length();
        int start = 0;
        int end = 4000;
        for (int i = 0; i < 100; i++) {
            if (strLength > end) {
                Log.i(tagName + i, msg.substring(start, end));
                start = end;
                end = end + 4000;
            } else {
                Log.i(tagName + i, msg.substring(start, strLength));
                break;
            }
        }
    }

    /**
     * 打破4k限制打印log
     */
    public static void e(String tagName, String msg) {
        if (!isDebug)
            return;
        int strLength = msg.length();
        int start = 0;
        int end = 4000;
        for (int i = 0; i < 100; i++) {
            if (strLength > end) {
                Log.e(tagName + i, msg.substring(start, end));
                start = end;
                end = end + 4000;
            } else {
                Log.e(tagName + i, msg.substring(start, strLength));
                break;
            }
        }
    }
}

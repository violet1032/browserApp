package com.zp.browser.utils;

import android.util.Log;

/**
 * <p>
 * 描述:
 * <p>
 * 作者:Administrator
 * <p>
 * 时间:2018/2/5 15:34
 * <p>
 * 版本:
 */
public class LogUtil {
    public static void logError(Class c, String str) {
        Log.e(c.getSimpleName(), str);
    }
}

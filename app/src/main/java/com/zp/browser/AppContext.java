/*
 * Copyright (c) 2015, 张涛.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zp.browser;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.DisplayMetrics;

import com.baidu.location.LocationClient;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.zp.browser.api.FHttpClient;
import com.zp.browser.bean.User;
import com.zp.browser.db.DBHelper;

import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.KJDB;
import org.kymjs.kjframe.bitmap.BitmapConfig;
import org.kymjs.kjframe.http.HttpConfig;

import cn.jpush.android.api.JPushInterface;

/**
 * @author kymjs (https://www.kymjs.com/)
 * @since 2015-3
 */
public class AppContext extends Application {
    private final String tag = "AppContext";

    public static Context applicationContext;
    public static AppContext appContext;

    // 获取网络图片对象
    public static KJBitmap bitmap;

    public static User user;

    // http相关
    public static FHttpClient http;
    // 设备屏幕宽高
    public static int screenHeight, screenWidth;
    public static int versionCode;
    public static String versionName;

    public static String downLoadUrl;

    // 百度定位
    public LocationClient locationClient = null;
    public static double longitude; // 经度
    public static double latitude;// 纬度
    public static String city;
    public static String province;

    // db
    public static KJDB dBHelper;

    public static int share_font_size = 16;
    public static String short_name = "BBK";

    @Override
    public void onCreate() {
        super.onCreate();
        HttpConfig.CACHEPATH = AppConfig.httpCachePath;
        CrashHandler.create(this);

        applicationContext = getApplicationContext();
        appContext = this;

        // 获取屏幕尺寸
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        // 获取版本信息
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(this.getPackageName(), 0);
            versionName = packageInfo.versionName;
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // http实例化
        HttpConfig config = new HttpConfig();
        HttpConfig.DEBUG = true;
        HttpConfig.TIMEOUT = AppConfig.getInstance().getHttpTimeout();
        config.cacheTime = AppConfig.getInstance().getHttpCacheTime();
        http = new FHttpClient(config);
        BitmapConfig bitmapConfig = new BitmapConfig();
        bitmapConfig.cacheTime = AppConfig.getInstance().getBitmapCacheTime();
        bitmap = new KJBitmap(bitmapConfig);

        user = new User();

        // 配置友盟
        PlatformConfig.setWeixin("wxdfbe692a0b6b00b3", "ee34c8841d94d342b5800041aaefef4b");
        PlatformConfig.setQQZone("101468109", "e05a06a58e5822b3fe16a8abbd538246"); // 正式版
//        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba"); // 测试版
        Config.DEBUG = true;

        // 数据库实例化
        dBHelper = DBHelper.getInstance(applicationContext);

        // 推送实例化
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }

    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

            return false;
        }

        return true;
    }
}

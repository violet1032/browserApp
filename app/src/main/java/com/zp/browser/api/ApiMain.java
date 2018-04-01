package com.zp.browser.api;

import com.zp.browser.AppContext;

import org.kymjs.kjframe.http.HttpCallBack;

import java.util.HashMap;
import java.util.Map;

/**
 * <p/>
 * 描述:
 * <p/>
 * 作者:Administrator
 * <p/>
 * 时间:2018/2/5 18:43
 * <p/>
 * 版本:
 */
public class ApiMain {

    public static void getAdvertList(FHttpCallBack callBack) {
        // 参数设置
        Map<String, Object> params = new HashMap<>();

        // 地址
        String url = URLs.getAdvertList;

        AppContext.http.post(url, params, callBack, false, false);
    }

    public static void getNewsList(FHttpCallBack callBack) {
        // 参数设置
        Map<String, Object> params = new HashMap<>();

        // 地址
        String url = URLs.getNewsList;

        AppContext.http.post(url, params, callBack, false, false);
    }

    public static void getWeather(String city, FHttpCallBack callBack) {
        // 参数设置
        Map<String, Object> params = new HashMap<>();
        params.put("city", city);

        // 地址
        String url = "https://www.sojson.com/open/api/weather/json.shtml";

        AppContext.http.get(url, params, callBack, false, false);
    }
    public static void searchUrl(FHttpCallBack callBack) {
        // 参数设置
        Map<String, Object> params = new HashMap<>();

        // 地址
        String url = URLs.searchUrl;

        AppContext.http.get(url, params, callBack, false, false);
    }
    public static void getVersion(FHttpCallBack callBack) {
        // 参数设置
        Map<String, Object> params = new HashMap<>();
        params.put("type",0);

        // 地址
        String url = URLs.getVersion;

        AppContext.http.get(url, params, callBack, false, false);
    }

    public static void getSearchSuggestion(String url,HttpCallBack callBack){
        // 参数设置

        // 地址

        AppContext.http.get(url, callBack);
    }
}

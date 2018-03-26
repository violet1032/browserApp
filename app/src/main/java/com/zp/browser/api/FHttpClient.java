package com.zp.browser.api;

import android.util.Log;

import com.zp.browser.AppConfig;
import com.zp.browser.utils.UIHelper;

import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;
import org.kymjs.kjframe.http.HttpParams;
import org.kymjs.kjframe.http.Request;

import java.io.File;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/18.
 */
public class FHttpClient extends KJHttp {
    private final String TAG = "FHttpClient";
    private boolean setCookie = false;
    private boolean login = false;

    public FHttpClient(HttpConfig httpConfig) {
        super(httpConfig);
    }

    public Request<byte[]> post(String url, Map<String, Object> httpParams, HttpCallBack
            callback) {

        if (!login) {
            FHttpCallBack.url = url;
            FHttpCallBack.callBack = callback;
            FHttpCallBack.map = httpParams;
        }

        HttpParams params = new HttpParams();
        params.put("", "");
        for (String key :
                httpParams.keySet()) {
            if (httpParams.get(key) != null)
                params.put(key, httpParams.get(key).toString());
            else if (AppConfig.DEBUG)
                UIHelper.ToastMessage(key + "参数为空");
        }

        String cookie = "JSESSIONID=" + AppConfig.getInstance().getmPre().getString("cookie", null);
        if (AppConfig.DEBUG) {
            Log.e(TAG, "http请求参数:" + params.getUrlParams());
            Log.e(TAG, "http请求地址:" + url);
        }
        params.putHeaders("Cookie", cookie);
        if (AppConfig.DEBUG)
            Log.e(TAG, "http请求cookie:" + cookie);
        return super.post(url, params, callback);
    }

    public Request<byte[]> postFile(String url, Map<String, Object> httpParams, File file, HttpCallBack
            callback) {

        if (!login) {
            FHttpCallBack.url = url;
            FHttpCallBack.callBack = callback;
            FHttpCallBack.map = httpParams;
        }

        HttpParams params = new HttpParams();
        params.put("", "");
        for (String key :
                httpParams.keySet()) {
            if (httpParams.get(key) != null)
                params.put(key, httpParams.get(key).toString());
            else if (AppConfig.DEBUG)
                UIHelper.ToastMessage(key + "参数为空");
        }
        if (file != null)
            params.put("file", file);

        String cookie = "JSESSIONID=" + AppConfig.getInstance().getmPre().getString("cookie", null);
        if (AppConfig.DEBUG) {
            Log.e(TAG, "http请求参数:" + params.getUrlParams());
            Log.e(TAG, "http请求地址:" + url);
        }
        params.putHeaders("Cookie", cookie);
        if (AppConfig.DEBUG)
            Log.e(TAG, "http请求cookie:" + cookie);
        return super.post(url, params, callback);
    }

    public void post(String url, Map<String, Object> params, HttpCallBack callback, boolean
            setCookie) {
        this.setCookie = setCookie;
        post(url, params, callback);
    }

    public void post(String url, Map<String, Object> params, HttpCallBack callback, boolean
            setCookie, boolean login) {
        this.setCookie = setCookie;
        this.login = login;
        post(url, params, callback);
    }


    public Request<byte[]> get(String url, Map<String, Object> httpParams, HttpCallBack
            callback) {
        HttpParams params = new HttpParams();
        params.put("", "");
        for (String key :
                httpParams.keySet()) {
            if (httpParams.get(key) != null)
                params.put(key, httpParams.get(key).toString());
            else if (AppConfig.DEBUG)
                UIHelper.ToastMessage(key + "参数为空");
        }

        String cookie = "JSESSIONID=" + AppConfig.getInstance().getmPre().getString("cookie", null);
        if (AppConfig.DEBUG) {
            Log.e(TAG, "http请求参数:" + params.getUrlParams());
            Log.e(TAG, "http请求地址:" + url);
        }
        params.putHeaders("Cookie", cookie);
        if (AppConfig.DEBUG)
            Log.e(TAG, "http请求cookie:" + cookie);
        return super.get(url, params, callback);
    }

    public Request<byte[]> get(String url, HttpCallBack
            callback) {
        return super.get(url, callback);
    }

    public void get(String url, Map<String, Object> params, HttpCallBack callback, boolean
            setCookie, boolean login) {
        this.setCookie = setCookie;
        this.login = login;
        get(url, params, callback);
    }
}

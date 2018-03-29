package com.zp.browser.api;


import com.zp.browser.AppContext;

import java.util.HashMap;
import java.util.Map;

/**
 * <p/>
 * 描述:
 * <p/>
 * 作者:Administrator
 * <p/>
 * 时间:2018/2/5 15:09
 * <p/>
 * 版本:
 */
public class ApiUser {

    public static void otherLogin(String unionid, String openid, String nickname, String headimgurl, FHttpCallBack callBack) {
        // 参数设置
        Map<String, Object> params = new HashMap<>();
        params.put("unionid", unionid);
        params.put("openid", openid);
        params.put("nickname", nickname);
        params.put("headimgurl", headimgurl);

        // 地址
        String url = URLs.otherLogin;

        AppContext.http.get(url, params, callBack, false, false);
    }

    public static void login(String phone, String password, FHttpCallBack callBack) {
        // 参数设置
        Map<String, Object> params = new HashMap<>();
        params.put("phone", phone);
        params.put("password", password);

        // 地址
        String url = URLs.login;

        AppContext.http.get(url, params, callBack, false, false);
    }

    public static void logout(FHttpCallBack callBack) {
        // 参数设置
        Map<String, Object> params = new HashMap<>();
        // 地址
        String url = URLs.LOGOUT;

        AppContext.http.get(url, params, callBack, false, false);
    }

    public static void register(String phone, String password, String rePassword, String smscode, FHttpCallBack callBack) {
        // 参数设置
        Map<String, Object> params = new HashMap<>();
        params.put("phone", phone);
        params.put("password", password);
        params.put("repassword", rePassword);
        params.put("sms_code", smscode);
        // 地址
        String url = URLs.register;

        AppContext.http.get(url, params, callBack, false, false);
    }

    public static void sendIdentifyingCode(String phone, FHttpCallBack callBack) {
        // 参数设置
        Map<String, Object> params = new HashMap<>();
        params.put("phone", phone);
        // 地址
        String url = URLs.sendIdentifyingCode;

        AppContext.http.get(url, params, callBack, false, false);
    }

    public static void getUserInfo(FHttpCallBack callBack) {
        // 参数设置
        Map<String, Object> params = new HashMap<>();
        // 地址
        String url = URLs.getUserInfo;

        AppContext.http.get(url, params, callBack, false, false);
    }

    public static void getSystemParam(FHttpCallBack callBack) {
        // 参数设置
        Map<String, Object> params = new HashMap<>();
        // 地址
        String url = URLs.getSystemParam;

        AppContext.http.get(url, params, callBack, false, false);
    }

    public static void readAward(FHttpCallBack callBack) {
        // 参数设置
        Map<String, Object> params = new HashMap<>();
        // 地址
        String url = URLs.readAward;

        AppContext.http.get(url, params, callBack, false, false);
    }
    public static void getInviteInfo(FHttpCallBack callBack) {
        // 参数设置
        Map<String, Object> params = new HashMap<>();
        // 地址
        String url = URLs.getInviteInfo;

        AppContext.http.get(url, params, callBack, false, false);
    }
    public static void getRegisterAward(FHttpCallBack callBack) {
        // 参数设置
        Map<String, Object> params = new HashMap<>();
        // 地址
        String url = URLs.getRegisterAward;

        AppContext.http.get(url, params, callBack, false, false);
    }

}

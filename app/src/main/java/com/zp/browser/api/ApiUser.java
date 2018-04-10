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

    public static void shareAward(FHttpCallBack callBack) {
        // 参数设置
        Map<String, Object> params = new HashMap<>();
        // 地址
        String url = URLs.shareAward;

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

    public static void editInfo(String name, String content, FHttpCallBack callBack) {
        // 参数设置
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("content", content);
        // 地址
        String url = URLs.editInfo;

        AppContext.http.get(url, params, callBack, false, false);
    }

    public static void editPassword(String oldPassword, String newPassword, String reNewPassword, FHttpCallBack callBack) {
        // 参数设置
        Map<String, Object> params = new HashMap<>();
        params.put("oldPassword", oldPassword);
        params.put("newPassword", newPassword);
        params.put("reNewPassword", reNewPassword);
        // 地址
        String url = URLs.editPassword;

        AppContext.http.get(url, params, callBack, false, false);
    }

    public static void forgot(String phone, String password, String repassword, String sms_code, FHttpCallBack callBack) {
        // 参数设置
        Map<String, Object> params = new HashMap<>();
        params.put("phone", phone);
        params.put("sms_code", sms_code);
        params.put("password", password);
        params.put("repassword", repassword);
        // 地址
        String url = URLs.forgot;

        AppContext.http.get(url, params, callBack, false, false);
    }

    public static void getAwardHistory(int page, FHttpCallBack callBack) {
        // 参数设置
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        // 地址
        String url = URLs.getAwardHistory;

        AppContext.http.get(url, params, callBack, false, false);
    }

}

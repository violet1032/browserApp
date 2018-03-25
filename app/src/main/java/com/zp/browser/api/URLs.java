package com.zp.browser.api;

/**
 * Created by Administrator on 2016/5/18.
 */
public class URLs {

    public static String IP = "www.zipengdp.cn"; // ip
//        public static String IP = "192.168.1.119:8087"; // ip
    public static String HOST = IP;
    public final static String HTTP = "http://";
    private final static String URL_SPLITTER = "/";
    public final static String COMMON = "api/"; // 公用部分
//        public final static String APP_STORE_HOST = HTTP + HOST + URL_SPLITTER;
    public final static String APP_STORE_HOST = HTTP + HOST + URL_SPLITTER + COMMON;

    public final static String otherLogin = APP_STORE_HOST + "user/otherLogin"; // 微信qq登录
    public final static String login = APP_STORE_HOST + "user/login"; // 手机登录
    public final static String register = APP_STORE_HOST + "user/register"; // 手机注册
    public final static String getUserInfo = APP_STORE_HOST + "user/getUserInfo"; // 获取用户信息
    public final static String getSystemParam = APP_STORE_HOST + "user/getSystemParam"; // 获取系统配置
    public final static String readAward = APP_STORE_HOST + "user/readAward"; // 阅读咨询奖励
    public final static String shareAward = APP_STORE_HOST + "user/shareAward"; // 分享成功奖励


    public final static String sendIdentifyingCode = APP_STORE_HOST + "sms/sendIdentifyingCode"; // 获取手机号
    public final static String getAdvertList = APP_STORE_HOST + "advert/list"; // 获取首页外链列表
    public final static String getNewsList = APP_STORE_HOST + "news/list"; // 获取快讯列表

    public final static String LOGOUT = APP_STORE_HOST + "user/logout"; // 退出登录
    public final static String IMG_UPLOAD = APP_STORE_HOST + "file/upload/avatarUploa";// 图片上传


    //-----部分暂未发出来的接口-----

}

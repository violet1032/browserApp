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
    public final static String getAdvertList = APP_STORE_HOST + "advert/list"; // 获取首页外链列表

    public final static String LOGOUT = APP_STORE_HOST + "api/user/logout"; // 退出登录
    public final static String IMG_UPLOAD = APP_STORE_HOST + "file/upload/avatarUploa";// 图片上传


    //-----部分暂未发出来的接口-----

}

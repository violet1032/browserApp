package com.zp.browser.bean;

/**
 * <p>
 * 描述:
 * <p>
 * 作者:Administrator
 * <p>
 * 时间:2016/7/5 10:35
 * <p>
 * 版本:
 */
public enum E_COIN_CACH_TYPE {
    none(0,"无"),
    in_share_success(1,"分享成功奖励"),
    in_friends_read(2,"好友阅读资讯页"),
    in_friends_active(3,"好友激活成功"),
    in_friends_income(4,"好友收益分成"),
    in_active(5,"注册奖励"),
    in_read(6,"资讯浏览"),
    in_use(7,"使用时长");
    public int value;
    public String name;

    private E_COIN_CACH_TYPE(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static String getIndex(int value) {
        E_COIN_CACH_TYPE[] e_detector_statuses = values();
        for (E_COIN_CACH_TYPE e_detector_status :
                e_detector_statuses) {
            if (e_detector_status.value == value)
                return e_detector_status.name;
        }
        return null;
    }
}

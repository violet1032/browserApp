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
public enum E_GENDER_TYPE {
    unknow(0, "未设置"),
    male(1, "男"),
    femal(2, "女");

    public int value;
    public String name;

    E_GENDER_TYPE(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static E_GENDER_TYPE getIndex(int value) {
        E_GENDER_TYPE[] types = values();
        for (E_GENDER_TYPE type :
                types) {
            if (type.value == value) {
                return type;
            }
        }
        return unknow;
    }
}

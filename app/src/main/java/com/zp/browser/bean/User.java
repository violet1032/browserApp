package com.zp.browser.bean;


import com.zp.browser.utils.JsonUtils;

import org.json.JSONException;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p/>
 * 描述:
 * <p/>
 * 作者:Administrator
 * <p/>
 * 时间:2016/7/5 17:04
 * <p/>
 * 版本:
 */
public class User implements Serializable {
    private int id;
    private String username;
    private String nickname;
    private String avatar;
    private BigDecimal coin;
    private E_GENDER_TYPE genderType;
    private String shareLink;
    private String phone;
    private String invite_bg;
    private String share_news_bg;
    private int count;
    private String invitation_code;

    public User parse(String jsonData) {
        try {
            JsonUtils jsonUtils = new JsonUtils(jsonData);
            JsonUtils jsonUtils1 = jsonUtils.getJSONUtils("user");
            setId(jsonUtils1.getInt("id"));
            setUsername(jsonUtils1.getString("username"));
            setNickname(jsonUtils1.getString("nickname"));
            setAvatar(jsonUtils1.getString("avatar"));
            setCoin(jsonUtils1.getBigDecimal("coin"));
            setShareLink(jsonUtils1.getString("share"));
            setPhone(jsonUtils1.getString("telephone"));
            setInvite_bg(jsonUtils1.getString("invite_bg"));
            setShare_news_bg(jsonUtils1.getString("share_news_bg"));
            setCount(jsonUtils1.getInt("count"));
            setInvitation_code(jsonUtils1.getString("invitation_code"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public String getInvitation_code() {
        return invitation_code;
    }

    public void setInvitation_code(String invitation_code) {
        this.invitation_code = invitation_code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


    public E_GENDER_TYPE getGenderType() {
        return genderType;
    }

    public void setGenderType(E_GENDER_TYPE genderType) {
        this.genderType = genderType;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public BigDecimal getCoin() {
        return coin;
    }

    public void setCoin(BigDecimal coin) {
        this.coin = coin;
    }

    public String getShareLink() {
        return shareLink;
    }

    public void setShareLink(String shareLink) {
        this.shareLink = shareLink;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getInvite_bg() {
        return invite_bg;
    }

    public void setInvite_bg(String invite_bg) {
        this.invite_bg = invite_bg;
    }

    public String getShare_news_bg() {
        return share_news_bg;
    }

    public void setShare_news_bg(String share_news_bg) {
        this.share_news_bg = share_news_bg;
    }
}

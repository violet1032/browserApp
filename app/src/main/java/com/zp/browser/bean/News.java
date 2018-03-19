package com.zp.browser.bean;

import com.zp.browser.utils.JsonUtils;

import org.json.JSONException;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/3/19 0019.
 */
public class News implements Serializable {
    private int id;
    private String content;
    private String dateline;
    private int hours;

    public News parse(String json) throws JSONException {
        JsonUtils jsonUtils = new JsonUtils(json);
        setId(jsonUtils.getInt("id"));
        setContent(jsonUtils.getString("content"));
        setDateline(jsonUtils.getString("dateline"));
        setHours(jsonUtils.getInt("hours"));
        return this;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }
}

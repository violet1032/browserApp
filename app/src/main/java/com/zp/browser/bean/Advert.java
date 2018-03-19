package com.zp.browser.bean;

import com.zp.browser.utils.JsonUtils;

import org.json.JSONException;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/3/19 0019.
 */
public class Advert implements Serializable {
    private int id;
    private String url;
    private int type;
    private String name;
    private String icon;

    public Advert parse(String json) throws JSONException {
        JsonUtils jsonUtils = new JsonUtils(json);
        setId(jsonUtils.getInt("id"));
        setUrl(jsonUtils.getString("url"));
        setIcon(jsonUtils.getString("icon"));
        setName(jsonUtils.getString("name"));
        setType(jsonUtils.getInt("type"));
        return this;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}

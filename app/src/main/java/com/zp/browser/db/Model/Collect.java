package com.zp.browser.db.Model;

import java.io.Serializable;

/**
 * <p>
 * 描述:
 * <p>
 * 作者:Administrator
 * <p>
 * 时间:2018/3/22 17:25
 * <p>
 * 版本:
 */
public class Collect implements Serializable{
    private int id;
    private String title;
    private String domain;
    private String icon;
    private String url;
    private long dateline;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getDateline() {
        return dateline;
    }

    public void setDateline(long dateline) {
        this.dateline = dateline;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

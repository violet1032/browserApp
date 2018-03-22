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
public class SearchHistory implements Serializable{
    private int id;
    private String content;
    private long dateline;

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

    public long getDateline() {
        return dateline;
    }

    public void setDateline(long dateline) {
        this.dateline = dateline;
    }
}

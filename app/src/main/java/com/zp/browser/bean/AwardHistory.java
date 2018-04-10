package com.zp.browser.bean;

import com.zp.browser.utils.JsonUtils;

import org.json.JSONException;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Administrator on 2018/3/19 0019.
 */
public class AwardHistory implements Serializable {
    private int id;
    private String content;
    private String dateline;
    private BigDecimal coin;

    public AwardHistory parse(String json) throws JSONException {
        JsonUtils jsonUtils = new JsonUtils(json);
        setId(jsonUtils.getInt("id"));
        setContent(jsonUtils.getString("content"));
        setDateline(jsonUtils.getString("dateline"));
        setCoin(new BigDecimal(jsonUtils.getString("coin")));
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

    public BigDecimal getCoin() {
        return coin;
    }

    public void setCoin(BigDecimal coin) {
        this.coin = coin;
    }
}

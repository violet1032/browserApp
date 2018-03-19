package com.zp.browser.bean;

import com.zp.browser.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/19 0019.
 */
public class NewsList implements Serializable {
    private List<News> list = new ArrayList<>();

    private int pageNumber;
    private int pageSize;
    private int totalRow;
    private int totalPage;

    public NewsList parse(String json) throws JSONException {
        JsonUtils j = new JsonUtils(json);
        JsonUtils jsonUtils = j.getJSONUtils("data");
        setPageNumber(jsonUtils.getInt("pageNumber"));
        setTotalPage(jsonUtils.getInt("totalPage"));
        setPageSize(jsonUtils.getInt("pageSize"));
        setTotalRow(jsonUtils.getInt("totalRow"));

        JSONArray jsonArray = jsonUtils.getJSONArray("list");
        for (int i = 0; i < jsonArray.length(); i++) {
            News news = new News().parse(jsonArray.getString(i));
            list.add(news);
        }
        return this;
    }

    public List<News> getList() {
        return list;
    }

    public void setList(List<News> list) {
        this.list = list;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalRow() {
        return totalRow;
    }

    public void setTotalRow(int totalRow) {
        this.totalRow = totalRow;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }
}

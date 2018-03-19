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
public class AdverList implements Serializable {
    private List<Advert> list = new ArrayList<>();

    public AdverList parse(String json) throws JSONException {
        JsonUtils j = new JsonUtils(json);
        JSONArray jsonArray = j.getJSONArray("data");
        for (int i = 0; i < jsonArray.length(); i++) {
            Advert advert = new Advert().parse(jsonArray.getString(i));
            list.add(advert);
        }
        return this;
    }

    public List<Advert> getList() {
        return list;
    }

    public void setList(List<Advert> list) {
        this.list = list;
    }
}

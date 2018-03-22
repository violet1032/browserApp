package com.zp.browser.utils;

import android.os.Handler;

import com.zp.browser.AppConfig;
import com.zp.browser.api.ApiMain;
import com.zp.browser.api.FHttpCallBack;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Map;

/**
 * <p/>
 * 描述:
 * <p/>
 * 作者:Administrator
 * <p/>
 * 时间:2018/3/22 10:06
 * <p/>
 * 版本:
 */
public class WeatherUtil {
    private static WeatherUtil weatherUtil;
    private String url = "https://www.sojson.com/open/api/weather/json.shtml?city=";

    public static WeatherUtil getInstance() {
        if (weatherUtil == null)
            weatherUtil = new WeatherUtil();
        return weatherUtil;
    }

    public void getWeather(String city, final Handler handler) {
        // 判断上次获取天气时间
        long last = AppConfig.getInstance().getmPre().getLong("last_weather", 0);
        if (last < System.currentTimeMillis() - 1000 * 60 * 60 * 24) {
            // 如果上次获取时间超过一天
            // 重新获取

            FHttpCallBack callBack = new FHttpCallBack() {
                @Override
                public void onSuccess(Map<String, String> headers, byte[] t) {
                    super.onSuccess(headers, t);
                    String str = new String(t);
                    try {
                        JsonUtils j = new JsonUtils(str);
                        JsonUtils jsonUtils = j.getJSONUtils("data");
                        if (jsonUtils != null) {
                            String wendu = jsonUtils.getString("wendu");
                            String pm25 = jsonUtils.getString("pm25");
                            String quality = jsonUtils.getString("quality");

                            JSONArray jsonArray = jsonUtils.getJSONArray("forecast");
                            String sweather = "";
                            if (jsonArray.length() > 0) {
                                JsonUtils jsonUtils1 = new JsonUtils(jsonArray.getString(0));
                                sweather = jsonUtils1.getString("type");
                            }

                            AppConfig.getInstance().mPreSet("wendu", wendu);
                            AppConfig.getInstance().mPreSet("quality", quality);
                            AppConfig.getInstance().mPreSet("pm25", pm25);
                            AppConfig.getInstance().mPreSet("weather", sweather);
                            AppConfig.getInstance().mPreSet("last_weather", System.currentTimeMillis());

                        }
                        handler.sendEmptyMessage(109);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            ApiMain.getWeather(city, callBack);
        } else {
            handler.sendEmptyMessage(109);
        }
    }
}

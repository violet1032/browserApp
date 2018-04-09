package com.zp.browser.ui.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.zp.browser.AppConfig;
import com.zp.browser.AppContext;
import com.zp.browser.R;
import com.zp.browser.adapter.AdvertListAdapter;
import com.zp.browser.api.ApiMain;
import com.zp.browser.api.ApiUser;
import com.zp.browser.api.FHttpCallBack;
import com.zp.browser.bean.AdverList;
import com.zp.browser.bean.Advert;
import com.zp.browser.bean.News;
import com.zp.browser.bean.NewsList;
import com.zp.browser.bean.Result;
import com.zp.browser.ui.LoginActivity;
import com.zp.browser.ui.MainActivity;
import com.zp.browser.ui.UserActivity;
import com.zp.browser.ui.common.BaseFragment;
import com.zp.browser.ui.dialog.ShareDialog;
import com.zp.browser.utils.StringUtils;
import com.zp.browser.utils.UIHelper;
import com.zp.browser.utils.WeatherUtil;
import com.zp.browser.widget.GridViewScroll;

import org.json.JSONException;
import org.kymjs.kjframe.ui.BindView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * <p/>
 * 描述:
 * <p/>
 * 作者:Administrator
 * <p/>
 * 时间:2018/3/22 11:06
 * <p/>
 * 版本:
 */
public class MainFragment extends BaseFragment {

    private AdverList adverList = new AdverList();
    private AdvertListAdapter advertListAdapter;
    @BindView(id = R.id.act_main_grid_main)
    private GridViewScroll gridView;

    @BindView(id = R.id.act_main_lay_list)
    private LinearLayout layList;

    private Handler handler;

    private Timer timer;
    private TimerTask timerTask;

    @BindView(id = R.id.act_main_tv_coin)
    private TextView txCoin;
    @BindView(id = R.id.act_main_tv_city)
    private TextView txCity;
    @BindView(id = R.id.act_main_tv_temperature)
    private TextView txWendu;
    @BindView(id = R.id.act_main_tv_air)
    private TextView txAir;
    @BindView(id = R.id.act_main_tv_weather)
    private TextView txWeather;
    @BindView(id = R.id.act_main_tv_pm25)
    private TextView txPm25;
    @BindView(id = R.id.fg_main_tv_1)
    private TextView tx1;
    @BindView(id = R.id.fg_main_tv_2)
    private TextView tx2;
    @BindView(id = R.id.fg_main_tv_3)
    private TextView tx3;
    @BindView(id = R.id.act_main_lay_search, click = true)
    private LinearLayout laySearch;
    @BindView(id = R.id.fg_main_lay_wakuang, click = true)
    private LinearLayout layWakuang;
    @BindView(id = R.id.fg_main_img_2)
    private ImageView img2;
    @BindView(id = R.id.fg_main_img_1)
    private ImageView img1;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = View.inflate(getActivity(), R.layout.fragment_main, null);
        return view;
    }

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);

        if (AppContext.user.getId() == 0) {
            txCoin.setText("登录");
        } else {
            txCoin.setText(AppContext.user.getCoin().toString());
        }

        txCity.setText(AppContext.city);

        changeStyle();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    @Override
    protected void initData() {
        super.initData();

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {

                switch (message.what) {
                    case 1:
                        // 更新积分
                        if (AppContext.user.getId() > 0)
                            txCoin.setText(AppContext.user.getCoin().toString());
                        else
                            txCoin.setText("登录");
                        countDown();
                        break;
                    case 109:
                        String wendu = AppConfig.getInstance().getmPre().getString("wendu", "");
                        String quality = AppConfig.getInstance().getmPre().getString("quality", "");
                        String pm25 = AppConfig.getInstance().getmPre().getString("pm25", "");
                        String sweather = AppConfig.getInstance().getmPre().getString("weather", "");

                        txPm25.setText(pm25);
                        txWendu.setText(wendu + "℃");
                        txAir.setText(quality);
                        txWeather.setText(sweather);
                        break;
                    case 101:
                        Advert advert = (Advert) message.obj;
                        if (advert.getType() == 0) {
                            ((MainActivity) getActivity()).webviewStart(advert.getUrl());
                        } else if (advert.getType() == 1) {
                            // 挖矿
                            ((MainActivity) getActivity()).wakuang();
                        }
                        break;
                }
                return false;
            }
        });

        // 倒计时
        if (timer == null)
            timer = new Timer();
        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(1);
                }
            };
            timer.schedule(timerTask, 1000, 1000);
        }

        getPersimmions();

        getAdvertList();

        getNewsList();

        getLocation();
    }

    @Override
    protected void widgetClick(View v) {
        super.widgetClick(v);

        switch (v.getId()) {
            case R.id.act_main_lay_search:
                ((MainActivity) getActivity()).searchShow("");
                break;
            case R.id.fg_main_lay_wakuang:
                if (AppContext.user.getId() == 0) {
                    LoginActivity.startActivity(getActivity());
                } else {
                    UserActivity.startActivity(getActivity());
                }
                break;
        }
    }

    private void getAdvertList() {
        FHttpCallBack callBack = new FHttpCallBack() {
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                super.onSuccess(headers, t);
                String str = new String(t);
                Result result = new Result().parse(str);
                if (result.isOk()) {
                    try {
                        adverList.parse(str);

                        advertListAdapter = new AdvertListAdapter(gridView, adverList.getList(), handler);
                        gridView.setAdapter(advertListAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        UIHelper.ToastMessage("外链数据解析错误");
                    }
                }
            }

            @Override
            public void onPreStart() {
                super.onPreStart();
                UIHelper.showLoadingDialog(getActivity());
            }

            @Override
            public void onFinish() {
                super.onFinish();
                UIHelper.stopLoadingDialog();
            }
        };
        ApiMain.getAdvertList(callBack);
    }

    private int pageNumber;
    private boolean isLast;

    public void getNewsList() {
        if(!isLast) {
            pageNumber++;
            FHttpCallBack callBack = new FHttpCallBack() {
                @Override
                public void onSuccess(Map<String, String> headers, byte[] t) {
                    super.onSuccess(headers, t);
                    String str = new String(t);
                    Result result = new Result().parse(str);
                    if (result.isOk()) {
                        try {
                            NewsList newsList = new NewsList();
                            newsList.parse(str);

                            if (newsList.getPageNumber() == newsList.getTotalPage()) {
                                isLast = true;
                            }

                            if(newsList.getList().size() > 0)
                            addNews(newsList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            UIHelper.ToastMessage("外链数据解析错误");
                        }
                    }
                }

                @Override
                public void onPreStart() {
                    super.onPreStart();
                    UIHelper.showLoadingDialog(getActivity());
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    UIHelper.stopLoadingDialog();
                }
            };
            ApiMain.getNewsList(pageNumber, 20, callBack);
        }
    }

    @TargetApi(24)
    private void addNews(NewsList newsList) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        for (int i = 0; i < newsList.getList().size(); i++) {
            final News news = newsList.getList().get(i);
            LinearLayout layItem = (LinearLayout) inflater.inflate(R.layout.listitem_news, null);
            TextView tvTime = layItem.findViewById(R.id.listitem_news_tv_time);
            final TextView tvContent = layItem.findViewById(R.id.listitem_news_tv_content);
            TextView tvCountDown = layItem.findViewById(R.id.listitem_news_tv_countdown);
            TextView tvTimeTitle = layItem.findViewById(R.id.listitem_news_tv_time_tile);
            LinearLayout layTime = layItem.findViewById(R.id.listitem_news_lay_time);

            String date = time(news.getDateline(), i);
            if (!StringUtils.isEmpty(date)) {
                layTime.setVisibility(View.VISIBLE);
                tvTimeTitle.setText(date);
            } else {
                layTime.setVisibility(View.GONE);
            }

            int start = 0;
            StringBuffer stringBuffer = new StringBuffer();
            String content = news.getContent();
            while (content.indexOf("rgb", start + 1) > 0) {
                if (start > 0) {
                    int e1 = content.indexOf(")", start);
                    stringBuffer.append(content.substring(e1 + 1, content.indexOf("rgb", start + 1)));
                } else
                    stringBuffer.append(content.substring(start, content.indexOf("rgb", start + 1)));
                start = content.indexOf("rgb", start + 1);
                int s = content.indexOf("(", start);
                int e = content.indexOf(")", start);
                String rgb = content.substring(s + 1, e);
                String[] strs = rgb.replaceAll(" ", "").split(",");
                if (strs.length == 3) {
                    String color = UIHelper.toHex(Integer.parseInt(strs[0]), Integer.parseInt(strs[1]), Integer.parseInt(strs[2]));
                    stringBuffer.append(color);
                }
                int s2 = content.indexOf("rgb", e);
                if (s2 < 0)
                    stringBuffer.append(content.substring(e + 1));
            }

            if (stringBuffer.length() == 0) {
                stringBuffer.append(content);
            }

            tvTime.setText(StringUtils.getDateHM(StringUtils.date_fromat_change_4(news.getDateline())));
            tvContent.setText(Html.fromHtml(stringBuffer.toString().replaceAll("background-color:", "")));
            String str2 = getCoundDown(news.getDateline(), news.getHours());
            tvCountDown.setText(str2);
            if (str2.equals("已结束")) {
                layItem.findViewById(R.id.listitem_news_lay_countdown).setVisibility(View.GONE);
            } else {
                layItem.findViewById(R.id.listitem_news_lay_countdown).setVisibility(View.VISIBLE);
            }


            layItem.findViewById(R.id.listitem_news_lay_content).setOnClickListener(new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onClick(View view) {
                    int maxLines = tvContent.getMaxLines();
                    if (maxLines == 5) {
                        tvContent.setMaxLines(1000);
                    } else
                        tvContent.setMaxLines(5);
                    readAward();
                }
            });

            layItem.findViewById(R.id.listitem_news_lay_share).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 分享挖矿
                    if (AppContext.user.getId() == 0)
                        LoginActivity.startActivity(getActivity());
                    else
                        ShareDialog.startActivity(getActivity(), 1, news);
                }
            });

            if (AppConfig.getInstance().getmPre().getBoolean("isNight", false)) {
                layItem.findViewById(R.id.listitem_news_lay_time).setBackgroundColor(getResources().getColor(R.color.night_black_4));
                layItem.findViewById(R.id.listitem_news_line).setBackgroundColor(getResources().getColor(R.color.night_black_4));
                layItem.findViewById(R.id.listitem_news_point).setBackgroundResource(R.drawable.circle_black);
                layItem.findViewById(R.id.listitem_news_tv_time).setBackgroundResource(R.drawable.arrow_box_black);
            } else {
                layItem.findViewById(R.id.listitem_news_lay_time).setBackgroundColor(getResources().getColor(R.color.gray_3));
                layItem.findViewById(R.id.listitem_news_line).setBackgroundColor(getResources().getColor(R.color.gray_2));
                layItem.findViewById(R.id.listitem_news_point).setBackgroundResource(R.drawable.circle_orange);
                layItem.findViewById(R.id.listitem_news_tv_time).setBackgroundResource(R.drawable.arrow_box_gray);
            }

            layList.addView(layItem);
        }
    }

    public void readAward() {
        if (AppContext.user.getId() > 0) {
            FHttpCallBack callBack = new FHttpCallBack() {
                @Override
                public void onSuccess(Map<String, String> headers, byte[] t) {
                    super.onSuccess(headers, t);
                }
            };
            ApiUser.readAward(callBack);
        }
    }

    private Map<String, Integer> map = new HashMap<>();

    private String time(String date, int position) {
        long ll = StringUtils.date_fromat_change_4(date);
        String str = StringUtils.getDateYMD(ll);

        if (map.get(str) != null && map.get(str) != position) {
            return null;
        } else {
            map.put(str, position);
            String d = StringUtils.getDateMD(ll);


            StringBuffer stringBuffer = new StringBuffer();

            // 判断是否是今天昨天
            long today = StringUtils.getDay0(0).getTime();
            if (ll >= today)
                stringBuffer.append("今天 ");
            else if (ll >= today - 1000 * 60 * 60 * 24 && ll < today)
                stringBuffer.append("昨天 ");

            stringBuffer.append(d);
            stringBuffer.append(" " + StringUtils.getWeekday(new Date(ll)));

            return stringBuffer.toString();
        }
    }

    private String getCoundDown(String time, int hours) {
        long l = StringUtils.date_fromat_change_4(time);
        long ll = l + hours * 1000 * 60 * 60;
        long offset = ll - System.currentTimeMillis();
        if (offset <= 0)
            return "已结束";

        long h = offset / 1000 / 60 / 60;
        long m = (offset - h * 1000 * 60 * 60) / 1000 / 60;
        long s = (offset - h * 1000 * 60 * 60 - m * 1000 * 60) / 1000;

        StringBuffer stringBuffer = new StringBuffer();
        if (h > 0)
            stringBuffer.append(StringUtils.zeroFill((int) h) + ":");
        if (m > 0)
            stringBuffer.append(StringUtils.zeroFill((int) m) + ":");
        if (s > 0)
            stringBuffer.append(StringUtils.zeroFill((int) s));
        return stringBuffer.toString();
    }

    private void countDown() {
        for (int i = 0; i < layList.getChildCount(); i++) {
            TextView textView = layList.getChildAt(i).findViewById(R.id.listitem_news_tv_countdown);
            String str = textView.getText().toString();
            if (str.equals("已结束")) {
                continue;
            } else {
                long l = StringUtils.getTimeHMS(str);
                textView.setText(StringUtils.getDateHMS(l - 1000));
            }
        }
    }

    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();

    private void getLocation() {
        mLocationClient = new LocationClient(getActivity().getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    private final int SDK_PERMISSION_REQUEST = 127;
    private String permissionInfo;

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取地址相关的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            String city = location.getCity();    //获取城市

            txCity.setText(city);

            WeatherUtil.getInstance().getWeather(city, handler);
        }
    }

    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            /*
             * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
			 */
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            // 读取电话状态权限
            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (getActivity().checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)) {
                return true;
            } else {
                permissionsList.add(permission);
                return false;
            }

        } else {
            return true;
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            //requestCode即所声明的权限获取码，在checkSelfPermission时传入
            case 1:
                BAIDU_READ_PHONE_STATE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //获取到权限，做相应处理
                    //调用定位SDK应确保相关权限均被授权，否则会引起定位失败
                    getLocation();
                } else {
                    //没有获取到权限，做特殊处理
                    UIHelper.ToastMessage("没有获取到权限");
                }
                break;
            default:
                break;
        }
    }

    public void changeStyle() {
        if (AppConfig.getInstance().getmPre().getBoolean("isNight", false)) {
            laySearch.setBackgroundResource(R.drawable.shape_rounded_h_black_4);

            txAir.setTextColor(getResources().getColor(R.color.night_text_1));
            txCity.setTextColor(getResources().getColor(R.color.night_text_1));
            txWeather.setTextColor(getResources().getColor(R.color.night_text_1));
            txWendu.setTextColor(getResources().getColor(R.color.night_text_1));
            tx1.setTextColor(getResources().getColor(R.color.night_text_1));
            tx2.setTextColor(getResources().getColor(R.color.night_text_1));
            tx3.setTextColor(getResources().getColor(R.color.night_text_1));

            gridView.setBackgroundColor(getResources().getColor(R.color.night_black_2));
            layList.setBackgroundColor(getResources().getColor(R.color.night_black_2));

            img1.setImageResource(R.drawable.icon_search_black);
            img2.setImageResource(R.drawable.main_person_black);

            for (int i = 0; i < layList.getChildCount(); i++) {
                LinearLayout linearLayout = layList.getChildAt(i).findViewById(R.id.listitem_news_lay_time);
                linearLayout.setBackgroundColor(getResources().getColor(R.color.night_black_4));

                layList.getChildAt(i).findViewById(R.id.listitem_news_line).setBackgroundColor(getResources().getColor(R.color.night_black_4));
                layList.getChildAt(i).findViewById(R.id.listitem_news_point).setBackgroundResource(R.drawable.circle_black);
                layList.getChildAt(i).findViewById(R.id.listitem_news_tv_time).setBackgroundResource(R.drawable.arrow_box_black);
            }
        } else {
            laySearch.setBackgroundResource(R.drawable.click_btn_round_h_skyblue_3);

            txAir.setTextColor(getResources().getColor(R.color.white));
            txCity.setTextColor(getResources().getColor(R.color.white));
            txWeather.setTextColor(getResources().getColor(R.color.white));
            txWendu.setTextColor(getResources().getColor(R.color.white));
            tx1.setTextColor(getResources().getColor(R.color.white));
            tx2.setTextColor(getResources().getColor(R.color.white));
            tx3.setTextColor(getResources().getColor(R.color.white));

            gridView.setBackgroundColor(getResources().getColor(R.color.white));
            layList.setBackgroundColor(getResources().getColor(R.color.white));

            img1.setImageResource(R.drawable.icon_search_white);
            img2.setImageResource(R.drawable.main_person);

            for (int i = 0; i < layList.getChildCount(); i++) {
                LinearLayout linearLayout = layList.getChildAt(i).findViewById(R.id.listitem_news_lay_time);
                linearLayout.setBackgroundColor(getResources().getColor(R.color.gray_3));
                layList.getChildAt(i).findViewById(R.id.listitem_news_line).setBackgroundColor(getResources().getColor(R.color.gray_2));
                layList.getChildAt(i).findViewById(R.id.listitem_news_point).setBackgroundResource(R.drawable.circle_orange);
                layList.getChildAt(i).findViewById(R.id.listitem_news_tv_time).setBackgroundResource(R.drawable.arrow_box_gray);
            }
        }
    }
}

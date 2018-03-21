package com.zp.browser.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.zp.browser.AppContext;
import com.zp.browser.R;
import com.zp.browser.adapter.AdvertListAdapter;
import com.zp.browser.api.ApiMain;
import com.zp.browser.api.FHttpCallBack;
import com.zp.browser.bean.AdverList;
import com.zp.browser.bean.News;
import com.zp.browser.bean.NewsList;
import com.zp.browser.bean.Result;
import com.zp.browser.ui.common.BaseActivity;
import com.zp.browser.ui.dialog.MenuDialog;
import com.zp.browser.utils.StringUtils;
import com.zp.browser.utils.UIHelper;
import com.zp.browser.widget.GridViewScroll;

import org.json.JSONException;
import org.kymjs.kjframe.ui.BindView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity {

    private AdverList adverList = new AdverList();
    private AdvertListAdapter advertListAdapter;
    @BindView(id = R.id.act_main_grid_main)
    private GridViewScroll gridView;

    private NewsList newsList = new NewsList();
//    private NewsListAdapter newsListAdapter;
//    @BindView(id = R.id.act_main_lv_news)
//    private ListviewScroll listview;

    @BindView(id = R.id.act_main_lay_list)
    private LinearLayout layList;

    private Handler handler;

    private Timer timer;
    private TimerTask timerTask;

    @BindView(id = R.id.act_main_lay_menu, click = true)
    private LinearLayout layMenu;
    @BindView(id = R.id.act_main_tv_coin)
    private TextView txCoin;
    @BindView(id = R.id.act_main_tv_city)
    private TextView txCity;

    public static void startActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.activity_main);
    }

    @Override
    public void initData() {
        super.initData();

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {

                switch (message.what) {
                    case 1:
                        countDown();
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
    public void initWidget() {
        super.initWidget();

        if (AppContext.user.getUid() == 0) {
            txCoin.setText("登录");
        }

        txCity.setText(AppContext.city);
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);

        switch (v.getId()) {
            case R.id.act_main_lay_menu:
                MenuDialog.startActivity(this);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
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

                        advertListAdapter = new AdvertListAdapter(gridView, adverList.getList());
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
                UIHelper.showLoadingDialog(MainActivity.this);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                UIHelper.stopLoadingDialog();
            }
        };
        ApiMain.getAdvertList(callBack);
    }

    private void getNewsList() {
        FHttpCallBack callBack = new FHttpCallBack() {
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                super.onSuccess(headers, t);
                String str = new String(t);
                Result result = new Result().parse(str);
                if (result.isOk()) {
                    try {
                        newsList.parse(str);

                        addNews();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        UIHelper.ToastMessage("外链数据解析错误");
                    }
                }
            }

            @Override
            public void onPreStart() {
                super.onPreStart();
                UIHelper.showLoadingDialog(MainActivity.this);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                UIHelper.stopLoadingDialog();
            }
        };
        ApiMain.getNewsList(callBack);
    }

    private void addNews() {
        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < newsList.getList().size(); i++) {
            News news = newsList.getList().get(i);
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

            tvTime.setText(StringUtils.getDateHM(StringUtils.date_fromat_change_4(news.getDateline())));
            tvContent.setText(news.getContent());
            tvCountDown.setText(getCoundDown(news.getDateline(), news.getHours()));

            layItem.findViewById(R.id.listitem_news_lay_content).setOnClickListener(new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onClick(View view) {
                    int maxLines = tvContent.getMaxLines();
                    if (maxLines == 5)
                        tvContent.setMaxLines(1000);
                    else
                        tvContent.setMaxLines(5);
                }
            });

            layItem.findViewById(R.id.listitem_news_lay_share).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });


            layList.addView(layItem);
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
        mLocationClient = new LocationClient(getApplicationContext());
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
        public void onReceiveLocation(BDLocation location){
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取地址相关的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            String city = location.getCity();    //获取城市

            txCity.setText(city);
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
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
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
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)){
                return true;
            }else{
                permissionsList.add(permission);
                return false;
            }

        }else{
            return true;
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode) {
            //requestCode即所声明的权限获取码，在checkSelfPermission时传入
            case 1:
                BAIDU_READ_PHONE_STATE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //获取到权限，做相应处理
                    //调用定位SDK应确保相关权限均被授权，否则会引起定位失败
                    UIHelper.ToastMessage("获取定位");
                    getLocation();
                } else{
                    //没有获取到权限，做特殊处理
                    UIHelper.ToastMessage("没有获取到权限");
                }
                break;
            default:
                break;
        }
    }
}

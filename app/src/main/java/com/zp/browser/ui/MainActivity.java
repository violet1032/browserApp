package com.zp.browser.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zp.browser.R;
import com.zp.browser.adapter.AdvertListAdapter;
import com.zp.browser.api.ApiMain;
import com.zp.browser.api.FHttpCallBack;
import com.zp.browser.bean.AdverList;
import com.zp.browser.bean.News;
import com.zp.browser.bean.NewsList;
import com.zp.browser.bean.Result;
import com.zp.browser.ui.common.BaseActivity;
import com.zp.browser.utils.LogUtil;
import com.zp.browser.utils.StringUtils;
import com.zp.browser.utils.UIHelper;
import com.zp.browser.widget.GridViewScroll;

import org.json.JSONException;
import org.kymjs.kjframe.ui.BindView;

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

        getAdvertList();

        getNewsList();
    }

    @Override
    public void initWidget() {
        super.initWidget();

    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);

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
            LogUtil.logError(MainActivity.class, "str:" + str);
            if (str.equals("已结束")) {
                continue;
            } else {
                long l = StringUtils.getTimeHMS(str);
                textView.setText(StringUtils.getDateHMS(l - 1000));
            }
        }
    }
}

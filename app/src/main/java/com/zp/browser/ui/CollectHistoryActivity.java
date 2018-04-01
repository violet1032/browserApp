package com.zp.browser.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zp.browser.AppConfig;
import com.zp.browser.AppContext;
import com.zp.browser.R;
import com.zp.browser.adapter.CollectHistoryListAdapter;
import com.zp.browser.adapter.HistoryListAdapter;
import com.zp.browser.db.Model.Collect;
import com.zp.browser.db.Model.ScanHistory;
import com.zp.browser.ui.common.BaseActivity;

import org.kymjs.kjframe.ui.BindView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CollectHistoryActivity extends BaseActivity {

    @BindView(id = R.id.umeng_banner_img_left, click = true)
    private ImageView imgBack;

    @BindView(id = R.id.act_collect_history_bg)
    private LinearLayout layBg;
    @BindView(id = R.id.umeng_banner_lay_bg)
    private RelativeLayout layTitleBg;

    @BindView(id = R.id.act_collect_history_lv_c)
    private ListView lvCollect;
    @BindView(id = R.id.act_collect_history_lv_h)
    private ListView lvHistory;

    @BindView(id = R.id.act_collect_history_tv_title_1, click = true)
    private TextView tvTitle1;
    @BindView(id = R.id.act_collect_history_tv_title_2, click = true)
    private TextView tvTitle2;
    @BindView(id = R.id.act_collect_history_img_1)
    private ImageView imgTitle1;
    @BindView(id = R.id.act_collect_history_img_2)
    private ImageView imgTitle2;

    private CollectHistoryListAdapter collectHistoryListAdapter;
    private HistoryListAdapter historyListAdapter;

    private Handler handler;
    private static Handler mainHandler;

    public static void startActivity(Context context,Handler mainHandler) {
        Intent intent = new Intent();
        intent.setClass(context, CollectHistoryActivity.class);
        context.startActivity(intent);
        CollectHistoryActivity.mainHandler = mainHandler;
    }

    @Override
    public void setRootView() {
        super.setRootView();

        setContentView(R.layout.activity_collect_history);
    }

    @Override
    public void initWidget() {
        super.initWidget();
    }

    @Override
    public void initData() {
        super.initData();

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case 1:
                        final Collect collect = (Collect) message.obj;

                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                Message m = new Message();
                                m.what = 102;
                                m.obj = collect.getUrl();
                                mainHandler.sendMessage(m);
                            }
                        }, 500);

                        finish();
                        break;
                    case 2:
                        final ScanHistory scanHistory = (ScanHistory) message.obj;

                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                Message m = new Message();
                                m.what = 102;
                                m.obj = scanHistory.getUrl();
                                mainHandler.sendMessage(m);
                            }
                        }, 500);

                        finish();
                        break;
                }
                return false;
            }
        });

        getList();
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);

        switch (v.getId()) {
            case R.id.umeng_banner_img_left:
                finish();
            case R.id.act_collect_history_tv_title_1:
                change(0);
                break;
            case R.id.act_collect_history_tv_title_2:
                change(1);
                break;
        }
    }

    private void getList() {
        List<Collect> list = AppContext.dBHelper.findAll(Collect.class, "dateline desc");

        collectHistoryListAdapter = new CollectHistoryListAdapter(lvCollect, list, handler);
        lvCollect.setAdapter(collectHistoryListAdapter);

        List<ScanHistory> list1 = AppContext.dBHelper.findAll(ScanHistory.class, "dateline desc");
        historyListAdapter = new HistoryListAdapter(lvHistory, list1, handler);
        lvHistory.setAdapter(historyListAdapter);

        changeStyle();
    }

    public void changeStyle() {
        if (AppConfig.getInstance().getmPre().getBoolean("isNight", false)) {
            layBg.setBackgroundColor(getResources().getColor(R.color.night_black_1));
            layTitleBg.setBackgroundColor(getResources().getColor(R.color.night_black_1));

            lvHistory.setBackgroundColor(getResources().getColor(R.color.night_black_2));
            lvCollect.setBackgroundColor(getResources().getColor(R.color.night_black_2));
        } else {
            layBg.setBackgroundColor(getResources().getColor(R.color.main_skyblue));
            layTitleBg.setBackgroundColor(getResources().getColor(R.color.main_skyblue));
            lvHistory.setBackgroundColor(getResources().getColor(R.color.white));
            lvCollect.setBackgroundColor(getResources().getColor(R.color.white));
        }
    }

    private int last = 0;

    private void change(int curr) {
        if (curr == last)
            return;

        if (curr == 0) {
            lvCollect.setVisibility(View.VISIBLE);
            lvHistory.setVisibility(View.GONE);

            tvTitle1.setTextColor(getResources().getColor(R.color.white));
            tvTitle2.setTextColor(getResources().getColor(R.color.gray_3));

            imgTitle1.setVisibility(View.VISIBLE);
            imgTitle2.setVisibility(View.GONE);
        } else {
            lvCollect.setVisibility(View.GONE);
            lvHistory.setVisibility(View.VISIBLE);

            tvTitle1.setTextColor(getResources().getColor(R.color.gray_3));
            tvTitle2.setTextColor(getResources().getColor(R.color.white));

            imgTitle1.setVisibility(View.GONE);
            imgTitle2.setVisibility(View.VISIBLE);
        }

        last = curr;
    }
}

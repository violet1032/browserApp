package com.zp.browser.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zp.browser.AppConfig;
import com.zp.browser.AppContext;
import com.zp.browser.R;
import com.zp.browser.adapter.AwardHistoryAdapter;
import com.zp.browser.api.ApiUser;
import com.zp.browser.api.FHttpCallBack;
import com.zp.browser.bean.AwardHistory;
import com.zp.browser.bean.AwardHistoryList;
import com.zp.browser.bean.Result;
import com.zp.browser.ui.common.BaseActivity;
import com.zp.browser.utils.UIHelper;
import com.zp.browser.widget.MyScrollView;
import com.zp.browser.widget.OnOverScrolledListener;

import org.json.JSONException;
import org.kymjs.kjframe.ui.BindView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AwardHistoryActivity extends BaseActivity {
    @BindView(id = R.id.umeng_banner_title)
    private TextView tvTitle;
    @BindView(id = R.id.umeng_banner_tv_right)
    private TextView tvRight;
    @BindView(id = R.id.umeng_banner_img_left, click = true)
    private ImageView imgBack;

    @BindView(id = R.id.act_award_history_lay_bg)
    private LinearLayout layBg;
    @BindView(id = R.id.umeng_banner_lay_bg)
    private RelativeLayout layTitleBg;
    @BindView(id = R.id.act_award_history_lay_bg_2)
    private MyScrollView layBg_2;

    @BindView(id = R.id.act_award_history_lv)
    private ListView listView;

//    @BindView(id = R.id.act_award_history_lay_refresh)
//    private RefreshLayout layRefresh;

    private List<AwardHistory> awardHistoryList = new ArrayList<>();
    private AwardHistoryAdapter awardHistoryAdapter;


    public static void startActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, AwardHistoryActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.activity_award_history);
    }

    @Override
    public void initWidget() {
        super.initWidget();

        tvTitle.setText("收益记录");

        layBg_2.setOnOverScrolledListener(new OnOverScrolledListener() {
            @Override
            public void scrollTop() {

            }

            @Override
            public void scrollBottom() {
                getList();
            }
        });

        changeStyle();

        if(AppContext.user.getId()>0){
            tvRight.setVisibility(View.VISIBLE);
            tvRight.setText(getString(R.string.user_text_13) + AppContext.user.getCost());
            tvRight.setTextColor(getResources().getColor(R.color.red));
        }
    }

    @Override
    public void initData() {
        super.initData();

        getList();
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);

        switch (v.getId()) {
            case R.id.umeng_banner_img_left:
                finish();
                break;
        }
    }

    public void changeStyle() {
        if (AppConfig.getInstance().getmPre().getBoolean("isNight", false)) {
            layBg.setBackgroundColor(getResources().getColor(R.color.night_black_1));
            layTitleBg.setBackgroundColor(getResources().getColor(R.color.night_black_1));
            layBg_2.setBackgroundColor(getResources().getColor(R.color.night_black_2));
            listView.setBackgroundColor(getResources().getColor(R.color.night_black_2));
        }
    }

    private int page;
    private boolean isLastPage;

    private void getList() {
        if (!isLastPage) {
            page++;
            FHttpCallBack callBack = new FHttpCallBack() {
                @Override
                public void onSuccess(Map<String, String> headers, byte[] t) {
                    super.onSuccess(headers, t);
                    String str = new String(t);
                    Result result = new Result().parse(str);
                    if (result.isOk()) {
                        try {
                            AwardHistoryList list = new AwardHistoryList();
                            list.parse(str);

                            if (list.getList().size() > 0)
                                awardHistoryList.addAll(list.getList());
                            if (list.getPageNumber() >= list.getTotalPage()) {
                                isLastPage = true;
                            }

                            if (awardHistoryAdapter == null) {
                                awardHistoryAdapter = new AwardHistoryAdapter(listView, awardHistoryList);
                                listView.setAdapter(awardHistoryAdapter);
                            } else
                                awardHistoryAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            UIHelper.ToastMessage("数据解析错误");
                        }
                    }
                }

                @Override
                public void onPreStart() {
                    super.onPreStart();
                    if (page == 1)
                        UIHelper.showLoadingDialog(AwardHistoryActivity.this);
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    if (page == 1)
                        UIHelper.stopLoadingDialog();
                }
            };
            ApiUser.getAwardHistory(page, callBack);
        }
    }
}

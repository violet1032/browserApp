package com.zp.browser.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.zp.browser.R;
import com.zp.browser.adapter.AdvertListAdapter;
import com.zp.browser.adapter.NewsListAdapter;
import com.zp.browser.api.ApiMain;
import com.zp.browser.api.FHttpCallBack;
import com.zp.browser.bean.AdverList;
import com.zp.browser.bean.NewsList;
import com.zp.browser.bean.Result;
import com.zp.browser.ui.common.BaseActivity;
import com.zp.browser.utils.UIHelper;
import com.zp.browser.widget.GridViewScroll;
import com.zp.browser.widget.ListviewScroll;

import org.json.JSONException;
import org.kymjs.kjframe.ui.BindView;

import java.util.Map;

public class MainActivity extends BaseActivity {

    private AdverList adverList = new AdverList();
    private AdvertListAdapter advertListAdapter;
    @BindView(id = R.id.act_main_grid_main)
    private GridViewScroll gridView;

    private NewsList newsList = new NewsList();
    private NewsListAdapter newsListAdapter;
    @BindView(id = R.id.act_main_lv_news)
    private ListviewScroll listview;

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

                        newsListAdapter = new NewsListAdapter(listview, newsList.getList());
                        listview.setAdapter(newsListAdapter);
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
}

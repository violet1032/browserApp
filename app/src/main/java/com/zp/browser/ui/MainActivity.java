package com.zp.browser.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;

import com.zp.browser.R;
import com.zp.browser.api.ApiMain;
import com.zp.browser.api.FHttpCallBack;
import com.zp.browser.bean.Result;
import com.zp.browser.ui.common.BaseActivity;
import com.zp.browser.utils.UIHelper;

import java.util.Map;

public class MainActivity extends BaseActivity {

    public Handler handler;

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
    }

    @Override
    public void initWidget() {
        super.initWidget();

    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);

    }

    private void getAdvertList(){
        FHttpCallBack callBack = new FHttpCallBack(){
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                super.onSuccess(headers, t);
                String str = new String(t);
                Result result = new Result().parse(str);
                if(result.isOk()){

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
}

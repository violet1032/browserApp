package com.zp.browser.ui;

import android.view.View;

import com.zp.browser.R;
import com.zp.browser.ui.common.BaseActivity;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends BaseActivity {

    private Timer timer;
    private TimerTask timerTask;

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.activity_splash);
    }

    @Override
    public void initData() {
        super.initData();

        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                LoginActivity.startActivity(SplashActivity.this);
                finish();
            }
        };
        timer.schedule(timerTask, 2000);
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
        if (timerTask != null)
            timerTask.cancel();
        if (timer != null)
            timer.cancel();
    }
}

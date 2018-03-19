package com.zp.browser.ui.common;

import android.view.WindowManager;

import org.kymjs.kjframe.KJActivity;

/**
 * <p/>
 * 描述:
 * <p/>
 * 作者:Administrator
 * <p/>
 * 时间:2018/1/29 11:23
 * <p/>
 * 版本:
 */
public class BaseActivity extends KJActivity{
    @Override
    public void setRootView() {

    }

    @Override
    public void initWidget() {
        super.initWidget();

        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }
}

package com.zp.browser.ui.dialog;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.zp.browser.R;
import com.zp.browser.ui.common.BaseActivity;

/**
 * <p>
 * 描述:
 * <p>
 * 作者:Administrator
 * <p>
 * 时间:2018/2/9 15:24
 * <p>
 * 版本:
 */
public class MenuDialog extends BaseActivity {

    public static void startActivity(Context activity, String version, String content) {
        Intent intent = new Intent();
        intent.setClass(activity, MenuDialog.class);
        intent.putExtra("version", version);
        intent.putExtra("content", content);
        activity.startActivity(intent);
    }

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.dialog_menu);

        setFinishOnTouchOutside(false);


    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    public void initWidget() {
        super.initWidget();
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);

        switch (v.getId()) {
            case R.id.dialog_menu_lay_exit:
                System.exit(0);
                break;
        }
    }
}

package com.zp.browser.ui.dialog;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zp.browser.AppContext;
import com.zp.browser.R;
import com.zp.browser.api.ApiCommon;
import com.zp.browser.ui.LoginActivity;
import com.zp.browser.ui.UserActivity;
import com.zp.browser.ui.common.BaseActivity;

import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.widget.RoundImageView;

/**
 * <p/>
 * 描述:
 * <p/>
 * 作者:Administrator
 * <p/>
 * 时间:2018/2/9 15:24
 * <p/>
 * 版本:
 */
public class MenuDialog extends BaseActivity {

    @BindView(id = R.id.dialog_menu_img_head, click = true)
    private RoundImageView imgHead;
    @BindView(id = R.id.dialog_menu_img_close, click = true)
    private ImageView imgClose;
    @BindView(id = R.id.dialog_menu_lay_exit, click = true)
    private LinearLayout layExit;

    public static void startActivity(Context activity) {
        Intent intent = new Intent();
        intent.setClass(activity, MenuDialog.class);
        activity.startActivity(intent);
    }

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.dialog_menu);

        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setWindowAnimations(R.style.AnimBottom);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        lp.width = metrics.widthPixels; // 宽度设置
        dialogWindow.setAttributes(lp);
    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    public void initWidget() {
        super.initWidget();

        imgHead.setBorderThickness(5);

        if (AppContext.user.getId() > 0) {
            ApiCommon.getNetBitmap(AppContext.user.getAvatar(), imgHead, false);
        }
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);

        switch (v.getId()) {
            case R.id.dialog_menu_lay_exit:
                System.exit(0);
                break;
            case R.id.dialog_menu_img_close:
                finish();
                break;
            case R.id.dialog_menu_img_head:
                if (AppContext.user.getId() == 0) {
                    LoginActivity.startActivity(this);
                }else{
                    UserActivity.startActivity(this);
                }
                break;
        }
    }
}

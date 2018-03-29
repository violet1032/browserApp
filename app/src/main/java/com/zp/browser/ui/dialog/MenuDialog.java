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
import android.widget.TextView;

import com.zp.browser.AppConfig;
import com.zp.browser.AppContext;
import com.zp.browser.R;
import com.zp.browser.api.ApiCommon;
import com.zp.browser.ui.LoginActivity;
import com.zp.browser.ui.UserActivity;
import com.zp.browser.ui.common.BaseActivity;
import com.zp.browser.utils.UIHelper;

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
    @BindView(id = R.id.dialog_menu_img_share, click = true)
    private ImageView imgShare;
    @BindView(id = R.id.dialog_menu_lay_exit, click = true)
    private LinearLayout layExit;
    @BindView(id = R.id.dialog_menu_lay_day, click = true)
    private LinearLayout layDay;

    @BindView(id=R.id.dialog_menu_tv_style)
    private TextView tvStyle;
    @BindView(id=R.id.dialog_menu_img_style)
    private ImageView imgStyle;

    @BindView(id=R.id.dialog_menu_lay_bg)
    private LinearLayout layBg;

    public static void startActivity(Context activity) {
        Intent intent = new Intent();
        intent.setClass(activity, MenuDialog.class);
        activity.startActivity(intent);
    }

    @Override
    public void setRootView() {
        super.setRootView();
        setTheme(UIHelper.getStyleDialog());

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

        changeStyle();
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
                } else {
                    UserActivity.startActivity(this);
                }
                finish();
                break;
            case R.id.dialog_menu_img_share:
                // 分享
                if (AppContext.user.getId() == 0) {
                    LoginActivity.startActivity(MenuDialog.this);
                } else {
                    ShareDialog.startActivity(MenuDialog.this, 0);
                }
                finish();
                break;
            case R.id.dialog_menu_lay_day:
                if (AppConfig.getInstance().getmPre().getBoolean("isNight", false)) {
                    AppConfig.getInstance().mPreSet("isNight", false);
                } else {
                    AppConfig.getInstance().mPreSet("isNight", true);
                }

                Intent intent = new Intent("android.intent.style_change");
                sendBroadcast(intent);

                finish();
                break;
        }
    }

    public void changeStyle() {
        if (AppConfig.getInstance().getmPre().getBoolean("isNight", false)) {
            layBg.setBackgroundColor(getResources().getColor(R.color.night_black_2));
            imgClose.setImageResource(R.drawable.icon_arrow_down_black);

            imgStyle.setImageResource(R.drawable.icon_rijianmoshi_black);
            tvStyle.setText("日间模式");
        }else{
            layBg.setBackgroundColor(getResources().getColor(R.color.white));
            imgClose.setImageResource(R.drawable.icon_arrow_down_blue);

            imgStyle.setImageResource(R.drawable.icon_yejianmoshi_black);
            tvStyle.setText("夜间模式");
        }
    }
}

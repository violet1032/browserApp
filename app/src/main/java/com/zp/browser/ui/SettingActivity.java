package com.zp.browser.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zp.browser.AppConfig;
import com.zp.browser.R;
import com.zp.browser.ui.common.BaseActivity;
import com.zp.browser.widget.SwitchButton;

import org.kymjs.kjframe.ui.BindView;

public class SettingActivity extends BaseActivity {
    @BindView(id = R.id.umeng_banner_title)
    private TextView tvTitle;
    @BindView(id = R.id.umeng_banner_img_left, click = true)
    private ImageView imgBack;

    @BindView(id = R.id.act_setting_bg)
    private LinearLayout layBg;
    @BindView(id = R.id.umeng_banner_lay_bg)
    private RelativeLayout layTitleBg;

    @BindView(id = R.id.act_setting_bg_1)
    private LinearLayout layBg_2;

    @BindView(id=R.id.act_setting_lay_clear,click = true)
    private RelativeLayout layClear;
    @BindView(id=R.id.act_setting_lay_download,click = true)
    private RelativeLayout layDownload;
    @BindView(id=R.id.act_setting_lay_last,click = true)
    private RelativeLayout layLast;
    @BindView(id=R.id.act_setting_lay_type,click = true)
    private RelativeLayout layType;
    @BindView(id=R.id.act_setting_lay_faxian,click = true)
    private RelativeLayout layFaxian;
    @BindView(id=R.id.act_setting_lay_douzaisou,click = true)
    private RelativeLayout layDouzaisou;

    @BindView(id=R.id.act_setting_switch_last)
    private SwitchButton sbtnLast;
    @BindView(id=R.id.act_setting_switch_faxian)
    private SwitchButton sbtnFaxian;
    @BindView(id=R.id.act_setting_switch_douzaisou)
    private SwitchButton sbtnDouzaisou;


    public static void startActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, SettingActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.activity_setting);
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

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    public void initWidget() {
        super.initWidget();



        tvTitle.setText("设置");

        changeStyle();
    }


    public void changeStyle() {
        if (AppConfig.getInstance().getmPre().getBoolean("isNight", false)) {
            layBg.setBackgroundColor(getResources().getColor(R.color.night_black_1));
            layTitleBg.setBackgroundColor(getResources().getColor(R.color.night_black_1));
            layBg_2.setBackgroundColor(getResources().getColor(R.color.night_black_2));

            childStyle(layBg_2, getResources().getColor(R.color.night_text_1));

            layClear.setBackgroundColor(getResources().getColor(R.color.night_black_2));
            layDownload.setBackgroundColor(getResources().getColor(R.color.night_black_2));
            layLast.setBackgroundColor(getResources().getColor(R.color.night_black_2));
            layType.setBackgroundColor(getResources().getColor(R.color.night_black_2));
            layFaxian.setBackgroundColor(getResources().getColor(R.color.night_black_2));
            layDouzaisou.setBackgroundColor(getResources().getColor(R.color.night_black_2));
        } else {
            layBg.setBackgroundColor(getResources().getColor(R.color.main_skyblue));
            layTitleBg.setBackgroundColor(getResources().getColor(R.color.main_skyblue));
            layBg_2.setBackgroundColor(getResources().getColor(R.color.gray_bg));

            childStyle(layBg_2, getResources().getColor(R.color.black_3));

            layClear.setBackgroundColor(getResources().getColor(R.color.white));
            layDownload.setBackgroundColor(getResources().getColor(R.color.white));
            layLast.setBackgroundColor(getResources().getColor(R.color.white));
            layType.setBackgroundColor(getResources().getColor(R.color.white));
            layFaxian.setBackgroundColor(getResources().getColor(R.color.white));
            layDouzaisou.setBackgroundColor(getResources().getColor(R.color.white));
        }
    }

    public void childStyle(ViewGroup viewGroup, int color) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(color);
            } else if (view instanceof ViewGroup) {
                if (((ViewGroup) view).getChildCount() > 0) {
                    childStyle((ViewGroup) view, color);
                }
            }
        }
    }
}

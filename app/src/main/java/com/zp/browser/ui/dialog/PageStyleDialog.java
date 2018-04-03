package com.zp.browser.ui.dialog;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.zp.browser.AppConfig;
import com.zp.browser.R;
import com.zp.browser.ui.common.BaseActivity;

import org.kymjs.kjframe.ui.BindView;

/**
 * Created by Administrator on 2018/4/3 0003.
 */
public class PageStyleDialog extends BaseActivity {

    @BindView(id = R.id.dialog_clear_btn_cancel, click = true)
    private Button btnCancel;
    @BindView(id = R.id.dialog_clear_btn_clear, click = true)
    private Button btnClear;

    @BindView(id = R.id.dialog_clear_cbox_1)
    private CheckBox cbox1;
    @BindView(id = R.id.dialog_clear_cbox_2)
    private CheckBox cbox2;
    @BindView(id = R.id.dialog_clear_cbox_3)
    private CheckBox cbox3;

    @BindView(id=R.id.dialog_page_bg_1)
    private LinearLayout layBg_1;
    @BindView(id=R.id.dialog_page_bg_2)
    private LinearLayout layBg_2;
    @BindView(id=R.id.dialog_page_bg_3)
    private LinearLayout layBg_3;

    public static void startActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, PageStyleDialog.class);
        context.startActivity(intent);
    }

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.dialog_page);
    }

    @Override
    public void initWidget() {
        super.initWidget();

        cbox1.setChecked(AppConfig.getInstance().getmPre().getBoolean("page_1", false));
        cbox2.setChecked(AppConfig.getInstance().getmPre().getBoolean("page_2", false));
        cbox3.setChecked(AppConfig.getInstance().getmPre().getBoolean("page_3", false));

        changeStyle();
    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
            case R.id.dialog_clear_btn_cancel:
                finish();
                break;
            case R.id.dialog_clear_btn_clear:
                set();
                finish();
                break;
        }
    }

    private void set(){
        AppConfig.getInstance().mPreSet("page_1",cbox1.isChecked());
        AppConfig.getInstance().mPreSet("page_2",cbox2.isChecked());
        AppConfig.getInstance().mPreSet("page_3", cbox3.isChecked());
    }

    public void changeStyle() {
        if (AppConfig.getInstance().getmPre().getBoolean("isNight", false)) {
            layBg_1.setBackgroundColor(getResources().getColor(R.color.night_black_1));
            layBg_2.setBackgroundColor(getResources().getColor(R.color.night_black_2));
            layBg_3.setBackgroundColor(getResources().getColor(R.color.night_black_2));

            btnClear.setBackgroundResource(R.drawable.click_btn_round_h_black_3);
            btnCancel.setBackgroundResource(R.drawable.click_btn_round_h_black_3);

            btnCancel.setTextColor(getResources().getColor(R.color.white));
        } else {
            layBg_1.setBackgroundColor(getResources().getColor(R.color.gray_3));
            layBg_2.setBackgroundColor(getResources().getColor(R.color.white));
            layBg_3.setBackgroundColor(getResources().getColor(R.color.white));

            btnCancel.setBackgroundResource(R.drawable.click_btn_round_h_white);
            btnClear.setBackgroundResource(R.drawable.click_btn_round_skyblue);

            btnCancel.setTextColor(getResources().getColor(R.color.black_3));
        }
    }
}

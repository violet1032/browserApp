package com.zp.browser.ui.dialog;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.zp.browser.AppConfig;
import com.zp.browser.AppContext;
import com.zp.browser.R;
import com.zp.browser.db.Model.ScanHistory;
import com.zp.browser.db.Model.SearchHistory;
import com.zp.browser.ui.common.BaseActivity;
import com.zp.browser.utils.UIHelper;

import org.kymjs.kjframe.ui.BindView;

import java.io.File;

/**
 * Created by Administrator on 2018/4/3 0003.
 */
public class ClearDialog extends BaseActivity {

    @BindView(id = R.id.dialog_clear_btn_cancel, click = true)
    private Button btnCancel;
    @BindView(id = R.id.dialog_clear_btn_clear, click = true)
    private Button btnClear;

    @BindView(id=R.id.dialog_clear_cbox_1)
    private CheckBox cbox1;
    @BindView(id=R.id.dialog_clear_cbox_2)
    private CheckBox cbox2;
    @BindView(id=R.id.dialog_clear_cbox_3)
    private CheckBox cbox3;
    @BindView(id=R.id.dialog_clear_cbox_4)
    private CheckBox cbox4;
    @BindView(id=R.id.dialog_clear_cbox_5)
    private CheckBox cbox5;
    @BindView(id=R.id.dialog_clear_cbox_6)
    private CheckBox cbox6;
    @BindView(id=R.id.dialog_clear_cbox_7)
    private CheckBox cbox7;
    @BindView(id=R.id.dialog_clear_cbox_8)
    private CheckBox cbox8;
    @BindView(id=R.id.dialog_clear_cbox_9)
    private CheckBox cbox9;

    @BindView(id=R.id.dialog_clear_bg)
    private LinearLayout layBg_1;
    @BindView(id=R.id.dialog_clear_bg_2)
    private LinearLayout layBg_2;
    @BindView(id=R.id.dialog_clear_bg_3)
    private LinearLayout layBg_3;

    public static void startActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, ClearDialog.class);
        context.startActivity(intent);
    }

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.dialog_clear);
    }

    @Override
    public void initWidget() {
        super.initWidget();

        cbox1.setChecked(true);
        cbox2.setChecked(true);
        cbox3.setChecked(true);
        cbox8.setChecked(true);

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
                clear();
                finish();
                break;
        }
    }

    private void clear(){
        UIHelper.showLoadingDialog(this);

        if(cbox2.isChecked()){
            // 清除搜索历史
            AppContext.dBHelper.deleteByWhere(SearchHistory.class,"1=1");
        }

        if(cbox3.isChecked()){
            // 清除搜索历史
            AppContext.dBHelper.deleteByWhere(ScanHistory.class,"1=1");
        }

        if(cbox4.isChecked()){
            deleteSaveFile(new File(AppConfig.getSaveImagePath()));
        }

        UIHelper.stopLoadingDialog();

        UIHelper.ToastMessage("已清除所选项");
        finish();
    }

    private void deleteSaveFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                deleteSaveFile(f);
            }
        } else if (file.exists()) {
            file.delete();
        }
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

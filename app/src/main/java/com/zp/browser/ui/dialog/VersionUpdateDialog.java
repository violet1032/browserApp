package com.zp.browser.ui.dialog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zp.browser.AppConfig;
import com.zp.browser.R;
import com.zp.browser.ui.common.BaseActivity;

import org.kymjs.kjframe.ui.BindView;

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
public class VersionUpdateDialog extends BaseActivity {

    @BindView(id = R.id.dialog_version_tv_version)
    private TextView tvVersion;
    @BindView(id = R.id.dialog_version_tv_content)
    private TextView tvContent;

    @BindView(id = R.id.dialog_version_btn_exit, click = true)
    private Button btnExit;
    @BindView(id = R.id.dialog_version_btn_sure, click = true)
    private Button btnSure;

    private String version;
    private String content;


    public static void startActivity(Context activity, String version, String content) {
        Intent intent = new Intent();
        intent.setClass(activity, VersionUpdateDialog.class);
        intent.putExtra("version", version);
        intent.putExtra("content", content);
        activity.startActivity(intent);
    }

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.dialog_version_update);

        setFinishOnTouchOutside(false);
    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    public void initWidget() {
        super.initWidget();

        content = getIntent().getStringExtra("content");
        version = getIntent().getStringExtra("version");

        tvContent.setText(content);
        tvVersion.setText("v" + version);
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);

        switch (v.getId()) {
            case R.id.dialog_version_btn_exit:
                System.exit(0);
                break;
            case R.id.dialog_version_btn_sure:
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(AppConfig.getInstance().getmPre().getString("android_update_url", ""));
                intent.setData(content_url);
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            System.exit(0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

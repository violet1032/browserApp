package com.zp.browser.ui.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zp.browser.R;
import com.zp.browser.ui.common.BaseActivity;
import com.zp.browser.utils.StringUtils;
import com.zp.browser.utils.UIHelper;

import org.kymjs.kjframe.ui.BindView;

/**
 * <p>
 * 描述:
 * <p>
 * 作者:Administrator
 * <p>
 * 时间:2018/4/3 17:17
 * <p>
 * 版本:
 */
public class EditInfoDialog extends BaseActivity {

    @BindView(id = R.id.dialog_edtinfo_tv_title)
    private TextView tvTitle;
    @BindView(id = R.id.dialog_editinfo_tv_cancel, click = true)
    private TextView tvCancel;
    @BindView(id = R.id.dialog_editinfo_tv_sure, click = true)
    private TextView tvSure;

    @BindView(id = R.id.dialog_edtinfo_edt)
    private EditText edtContent;

    private static Handler lastHandler;

    public static void startActivity(Context context, String title, String content, Handler handler, int type) {
        Intent intent = new Intent();
        intent.setClass(context, EditInfoDialog.class);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        intent.putExtra("type", type);
        context.startActivity(intent);
        lastHandler = handler;
    }

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.dialog_editinfo);
    }

    @Override
    public void initWidget() {
        super.initWidget();

        tvTitle.setText(getIntent().getStringExtra("title"));
        edtContent.setText(getIntent().getStringExtra("content"));
    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);

        switch (v.getId()) {
            case R.id.dialog_editinfo_tv_cancel:
                finish();
                break;
            case R.id.dialog_editinfo_tv_sure:
                String content = edtContent.getText().toString().trim();
                if (StringUtils.isEmpty(content)) {
                    UIHelper.ToastMessage("请输入内容");
                    return;
                }
                Message message = new Message();
                message.what = 0;
                message.obj = edtContent.getText().toString().trim();
                message.arg1 = getIntent().getIntExtra("type", 0);
                lastHandler.sendMessage(message);
                finish();
                break;
        }
    }
}

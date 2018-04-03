package com.zp.browser.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zp.browser.AppConfig;
import com.zp.browser.R;
import com.zp.browser.api.ApiUser;
import com.zp.browser.api.FHttpCallBack;
import com.zp.browser.bean.Result;
import com.zp.browser.ui.common.BaseActivity;
import com.zp.browser.utils.StringUtils;
import com.zp.browser.utils.UIHelper;

import org.kymjs.kjframe.ui.BindView;

import java.util.Map;

public class ChangePasswordActivity extends BaseActivity {

    @BindView(id = R.id.umeng_banner_title)
    private TextView tvTitle;
    @BindView(id = R.id.umeng_banner_img_left, click = true)
    private ImageView imgBack;

    @BindView(id = R.id.act_change_password_bg)
    private LinearLayout layBg;
    @BindView(id = R.id.umeng_banner_lay_bg)
    private RelativeLayout layTitleBg;
    @BindView(id = R.id.act_change_password_bg_1)
    private LinearLayout layBg_2;

    @BindView(id = R.id.act_change_password_lay_password)
    private LinearLayout layPassword;
    @BindView(id = R.id.act_change_password_lay_newpassword)
    private LinearLayout layNewPassword;
    @BindView(id = R.id.act_change_password_lay_repassword)
    private LinearLayout layRePassword;

    @BindView(id = R.id.act_change_password_edt_password)
    private EditText edtPassword;
    @BindView(id = R.id.act_change_password_edt_newpassword)
    private EditText edtNewPassword;
    @BindView(id = R.id.act_change_password_edt_repassword)
    private EditText edtRePassword;

    @BindView(id = R.id.act_change_password_img_password)
    private ImageView imgPassword;
    @BindView(id = R.id.act_change_password_img_newpassword)
    private ImageView imgNewPassword;
    @BindView(id = R.id.act_change_password_img_repassword)
    private ImageView imgRePassword;

    @BindView(id = R.id.act_change_password_btn, click = true)
    private Button btnChange;

    public static void startActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, ChangePasswordActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.activity_change_password);
    }

    @Override
    public void initWidget() {
        super.initWidget();

        tvTitle.setText("修改密码");

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
            case R.id.umeng_banner_img_left:
                finish();
                break;
            case R.id.act_change_password_btn:
                change();
                break;
        }
    }

    public void changeStyle() {
        if (AppConfig.getInstance().getmPre().getBoolean("isNight", false)) {
            layBg.setBackgroundColor(getResources().getColor(R.color.night_black_1));
            layTitleBg.setBackgroundColor(getResources().getColor(R.color.night_black_1));
            layBg_2.setBackgroundColor(getResources().getColor(R.color.night_black_2));

            layRePassword.setBackgroundResource(R.drawable.shape_rounded_h_black_3);
            layPassword.setBackgroundResource(R.drawable.shape_rounded_h_black_3);
            layNewPassword.setBackgroundResource(R.drawable.shape_rounded_h_black_3);

            imgPassword.setImageResource(R.drawable.password_2_black);
            imgNewPassword.setImageResource(R.drawable.password_2_black);
            imgRePassword.setImageResource(R.drawable.password_2_black);

            edtPassword.setTextColor(getResources().getColor(R.color.white));
            edtNewPassword.setTextColor(getResources().getColor(R.color.white));
            edtRePassword.setTextColor(getResources().getColor(R.color.white));

            btnChange.setBackgroundResource(R.drawable.click_btn_round_h_black_3);
        } else {
            layBg.setBackgroundColor(getResources().getColor(R.color.main_skyblue));
            layTitleBg.setBackgroundColor(getResources().getColor(R.color.main_skyblue));
            layBg_2.setBackgroundColor(getResources().getColor(R.color.gray_bg));

            layRePassword.setBackgroundResource(R.drawable.shape_rounded_h_white);
            layPassword.setBackgroundResource(R.drawable.shape_rounded_h_white);
            layNewPassword.setBackgroundResource(R.drawable.shape_rounded_h_white);

            imgPassword.setImageResource(R.drawable.password_2);
            imgNewPassword.setImageResource(R.drawable.password_2);
            imgRePassword.setImageResource(R.drawable.password_2);

            edtPassword.setTextColor(getResources().getColor(R.color.black_3));
            edtNewPassword.setTextColor(getResources().getColor(R.color.black_3));
            edtRePassword.setTextColor(getResources().getColor(R.color.black_3));

            btnChange.setBackgroundResource(R.drawable.click_btn_round_h_skyblue_3);
        }
    }

    private void change() {
        String password = edtPassword.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();
        String rePassword = edtRePassword.getText().toString().trim();

        if (StringUtils.isEmpty(password)) {
            UIHelper.ToastMessage("请输入原密码");
            return;
        }
        if (StringUtils.isEmpty(newPassword)) {
            UIHelper.ToastMessage("请输入新密码");
            return;
        }
        if (!newPassword.equals(rePassword)) {
            UIHelper.ToastMessage("两次输入的密码不一样");
            return;
        }

        FHttpCallBack callBack = new FHttpCallBack() {
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                super.onSuccess(headers, t);
                String str = new String(t);
                Result result = new Result().parse(str);
                if (result.isOk()) {
                    UIHelper.ToastMessage("修改成功");
                    finish();
                } else {
                    UIHelper.ToastMessage(result.getMsg());
                }
            }

            @Override
            public void onPreStart() {
                super.onPreStart();
                UIHelper.showLoadingDialog(ChangePasswordActivity.this);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                UIHelper.stopLoadingDialog();
            }
        };
        ApiUser.editPassword(password, newPassword, rePassword, callBack);
    }
}

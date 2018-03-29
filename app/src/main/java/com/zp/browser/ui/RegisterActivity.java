package com.zp.browser.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import java.util.Timer;
import java.util.TimerTask;

public class RegisterActivity extends BaseActivity {
    @BindView(id = R.id.umeng_banner_title)
    private TextView tvTitle;
    @BindView(id = R.id.umeng_banner_img_left, click = true)
    private ImageView imgBack;

    @BindView(id = R.id.act_register_edt_phone)
    private EditText edtPhone;
    @BindView(id = R.id.act_register_edt_password)
    private EditText edtPassword;
    @BindView(id = R.id.act_register_edt_repassword)
    private EditText edtRePassword;
    @BindView(id = R.id.act_register_edt_smscode)
    private EditText edtCode;
    @BindView(id = R.id.act_register_tv_getcode, click = true)
    private TextView tvGetCode;

    @BindView(id = R.id.act_register_btn, click = true)
    private Button btnRegister;

    private int sendCountDown = 60;
    private Handler handler;

    private Timer timer;
    private TimerTask timerTask;

    @BindView(id=R.id.act_register_lay_bg)
    private LinearLayout layBg;
    @BindView(id=R.id.umeng_banner_lay_bg)
    private RelativeLayout layTitleBg;
    @BindView(id=R.id.act_register_lay_bg_2)
    private LinearLayout layBg_2;
    @BindView(id=R.id.act_register_lay_phone)
    private LinearLayout layPhone;
    @BindView(id=R.id.act_register_lay_password)
    private LinearLayout layPassword;
    @BindView(id=R.id.act_register_lay_repassword)
    private LinearLayout layRePassword;
    @BindView(id=R.id.act_register_lay_code)
    private LinearLayout layCode;
    @BindView(id=R.id.act_register_img_phone)
    private ImageView imgPhone;
    @BindView(id=R.id.act_register_img_password)
    private ImageView imgPassword;
    @BindView(id=R.id.act_register_img_repassword)
    private ImageView imgRePassword;
    @BindView(id=R.id.act_register_img_line)
    private ImageView imgLine;
    @BindView(id=R.id.act_register_img_code)
    private ImageView imgCode;
    @BindView(id=R.id.act_register_cbox)
    private CheckBox cbox;

    public static void startActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, RegisterActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.activity_register);
    }

    @Override
    public void initWidget() {
        super.initWidget();

        tvTitle.setText("手机号注册");

        changeStyle();
    }

    @Override
    public void initData() {
        super.initData();

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case 1:
                        sendCountDown--;
                        if (sendCountDown <= 0) {
                            sendCountDown = 0;
                            if (timer != null) {
                                timer.cancel();
                                timer = null;
                            }

                            if (timerTask != null) {
                                timerTask.cancel();
                                timerTask = null;
                            }

                            tvGetCode.setText("重新发送");
                            tvGetCode.setEnabled(true);
                            tvGetCode.setTextColor(getResources().getColor(R.color.main_skyblue));
                        } else
                            tvGetCode.setText("重新发送（" + sendCountDown + "）");
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
            case R.id.umeng_banner_img_left:
                finish();
                break;
            case R.id.act_register_btn:
                // 注册
                regsiter();
                break;
            case R.id.act_register_tv_getcode:
                getCode();
                break;
        }
    }

    private void regsiter() {
        String phone = edtPhone.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String repassword = edtRePassword.getText().toString().trim();
        String code = edtCode.getText().toString().trim();

        if (StringUtils.isEmpty(phone)) {
            UIHelper.ToastMessage("请输入手机号");
            return;
        }

        if (StringUtils.isEmpty(password)) {
            UIHelper.ToastMessage("请输入密码");
            return;
        }

        if (StringUtils.isEmpty(repassword)) {
            UIHelper.ToastMessage("请重复密码");
            return;
        }

        if (StringUtils.isEmpty(code)) {
            UIHelper.ToastMessage("请输入验证码");
            return;
        }

        if (!password.equals(repassword)) {
            UIHelper.ToastMessage("两次密码不相等");
            return;
        }

        if(!cbox.isChecked()){
            UIHelper.ToastMessage("请同意用户协议后继续");
            return;
        }

        FHttpCallBack callBack = new FHttpCallBack() {
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                super.onSuccess(headers, t);
                String str = new String(t);
                Result result = new Result().parse(str);
                if (result.isOk()) {
                    UIHelper.ToastMessage(result.getMsg());
                    finish();
                } else
                    UIHelper.ToastMessage(result.getMsg());
            }

            @Override
            public void onPreStart() {
                super.onPreStart();
                UIHelper.showLoadingDialog(RegisterActivity.this);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                UIHelper.stopLoadingDialog();
            }
        };
        ApiUser.register(phone, password, repassword, code, callBack);
    }


    private void getCode() {
        String phone = edtPhone.getText().toString().trim();

        if (StringUtils.isEmpty(phone)) {
            UIHelper.ToastMessage("请输入手机号");
            return;
        }

        FHttpCallBack callBack = new FHttpCallBack() {
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                super.onSuccess(headers, t);
                String str = new String(t);
                Result result = new Result().parse(str);
                if (result.isOk()) {
                    UIHelper.ToastMessage(result.getMsg());
                    tvGetCode.setEnabled(false);
                    tvGetCode.setText("重新发送（" + sendCountDown + "）");
                    tvGetCode.setTextColor(getResources().getColor(R.color.gray));

                    if (timer == null)
                        timer = new Timer();
                    if (timerTask == null) {
                        timerTask = new TimerTask() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(1);
                            }
                        };
                        timer.schedule(timerTask, 1000, 1000);
                    }
                } else
                    UIHelper.ToastMessage(result.getMsg());
            }

            @Override
            public void onPreStart() {
                super.onPreStart();
                UIHelper.showLoadingDialog(RegisterActivity.this);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                UIHelper.stopLoadingDialog();
            }
        };
        ApiUser.sendIdentifyingCode(phone, callBack);
    }

    public void changeStyle() {
        if (AppConfig.getInstance().getmPre().getBoolean("isNight", false)) {
            layBg.setBackgroundColor(getResources().getColor(R.color.night_black_1));
            layTitleBg.setBackgroundColor(getResources().getColor(R.color.night_black_1));
            layBg_2.setBackgroundColor(getResources().getColor(R.color.night_black_2));
            layPhone.setBackgroundResource(R.drawable.shape_rounded_h_black_3);
            layPassword.setBackgroundResource(R.drawable.shape_rounded_h_black_3);
            layRePassword.setBackgroundResource(R.drawable.shape_rounded_h_black_3);
            layCode.setBackgroundResource(R.drawable.shape_rounded_h_black_3);

            btnRegister.setBackgroundResource(R.drawable.click_btn_round_h_black_3);

            imgPhone.setImageResource(R.drawable.icon_mobile_black);
            imgPassword.setImageResource(R.drawable.password_2_black);
            imgRePassword.setImageResource(R.drawable.password_2_black);
            imgCode.setImageResource(R.drawable.sms_code_black);

            tvGetCode.setTextColor(getResources().getColor(R.color.gray));

            imgLine.setBackgroundColor(getResources().getColor(R.color.night_black_4));

            edtPassword.setTextColor(getResources().getColor(R.color.white));
            edtPhone.setTextColor(getResources().getColor(R.color.white));
            edtRePassword.setTextColor(getResources().getColor(R.color.white));
            edtCode.setTextColor(getResources().getColor(R.color.white));
        }else{
            layBg.setBackgroundColor(getResources().getColor(R.color.main_skyblue));
            layTitleBg.setBackgroundColor(getResources().getColor(R.color.main_skyblue));
            layBg_2.setBackgroundColor(getResources().getColor(R.color.gray_bg));
            layPhone.setBackgroundResource(R.drawable.shape_rounded_h_white);
            layPassword.setBackgroundResource(R.drawable.shape_rounded_h_white);
            layRePassword.setBackgroundResource(R.drawable.shape_rounded_h_white);
            layCode.setBackgroundResource(R.drawable.shape_rounded_h_white);

            btnRegister.setBackgroundResource(R.drawable.click_btn_round_h_skyblue_3);

            imgPhone.setImageResource(R.drawable.icon_mobile_blue);
            imgPassword.setImageResource(R.drawable.password_2);
            imgRePassword.setImageResource(R.drawable.password_2);
            imgCode.setImageResource(R.drawable.sms_code);



            tvGetCode.setTextColor(getResources().getColor(R.color.main_skyblue));

            imgLine.setBackgroundColor(getResources().getColor(R.color.main_skyblue));

            edtPassword.setTextColor(getResources().getColor(R.color.black_3));
            edtPhone.setTextColor(getResources().getColor(R.color.black_3));
            edtRePassword.setTextColor(getResources().getColor(R.color.black_3));
            edtCode.setTextColor(getResources().getColor(R.color.black_3));
        }
    }
}

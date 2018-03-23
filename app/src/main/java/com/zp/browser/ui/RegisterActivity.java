package com.zp.browser.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
    @BindView(id = R.id.act_register_edt_password)
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
}

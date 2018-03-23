package com.zp.browser.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zp.browser.AppConfig;
import com.zp.browser.AppContext;
import com.zp.browser.R;
import com.zp.browser.api.ApiUser;
import com.zp.browser.api.FHttpCallBack;
import com.zp.browser.bean.Result;
import com.zp.browser.ui.common.BaseActivity;
import com.zp.browser.ui.dialog.VersionUpdateDialog;
import com.zp.browser.utils.JsonUtils;
import com.zp.browser.utils.StringUtils;
import com.zp.browser.utils.UIHelper;

import org.json.JSONException;
import org.kymjs.kjframe.ui.BindView;

import java.util.Map;

public class LoginActivity extends BaseActivity {

    @BindView(id = R.id.umeng_banner_title)
    private TextView tvTitle;
    @BindView(id = R.id.umeng_banner_img_left, click = true)
    private ImageView imgBack;

    @BindView(id = R.id.act_login_edt_phone)
    private EditText edtPhone;
    @BindView(id = R.id.act_login_edt_password)
    private EditText edtPassword;
    @BindView(id = R.id.act_login_btn_login, click = true)
    private Button btnLogin;
    @BindView(id = R.id.act_login_tv_register, click = true)
    private TextView tvRegister;
    @BindView(id = R.id.act_login_tv_forgot, click = true)
    private TextView tvForgot;

    public static void startActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.activity_login);
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);

        switch (v.getId()) {
            case R.id.umeng_banner_img_left:
                finish();
                break;
            case R.id.act_login_btn_login:
                // 登录
                login();
                break;
            case R.id.act_login_tv_register:
                // 注册
                RegisterActivity.startActivity(this);
                break;
            case R.id.act_login_tv_forgot:
                // 忘记密码
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

        tvTitle.setText("手机号登录");

        getAppVersion();
    }

    private void login() {
        final String phone = edtPhone.getText().toString().trim();
        final String password = edtPassword.getText().toString().trim();

        if (StringUtils.isEmpty(phone)) {
            UIHelper.ToastMessage("请输入手机号");
            return;
        }
        if (StringUtils.isEmpty(password)) {
            UIHelper.ToastMessage("请输入密码");
            return;
        }

        FHttpCallBack callBack = new FHttpCallBack() {
            @Override
            public void onFinish() {
                super.onFinish();
                UIHelper.stopLoadingDialog();
            }

            @Override
            public void onPreStart() {
                super.onPreStart();
                UIHelper.showLoadingDialog(LoginActivity.this);
            }

            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                super.onSuccess(headers, t);
                String str = new String(t);
                Result result = new Result();
                result.parse(str);
                if (result.isOk()) {
                    // 登录成功
                    AppContext.user.parse(str);

                    // 缓存信息
                    AppConfig.getInstance().mPreSet("phone", phone);
                    AppConfig.getInstance().mPreSet("password", password);

                    MainActivity.startActivity(LoginActivity.this);
                    finish();
                } else {
                    UIHelper.ToastMessage(result.getMsg());
                }
            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);

            }
        };
        ApiUser.login(phone, password, callBack);
    }

    private void getAppVersion() {
        FHttpCallBack callBack = new FHttpCallBack() {
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                super.onSuccess(headers, t);
                String str = new String(t);
                Result result = new Result().parse(str);
                if (result.isOk()) {
                    try {
                        JsonUtils j = new JsonUtils(str);
                        JsonUtils jsonUtils = j.getJSONUtils("info");
                        String content = jsonUtils.getString("content");
                        String version = jsonUtils.getString("version");

                        // 比较版本
                        if (version.compareTo(AppContext.versionName) > 0) {
                            // 如果有新版本
                            VersionUpdateDialog.startActivity(LoginActivity.this, version, content);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                UIHelper.stopLoadingDialog();
            }

            @Override
            public void onPreStart() {
                super.onPreStart();
                UIHelper.showLoadingDialog(LoginActivity.this);
            }
        };
//        ApiUser.getAppVersion(callBack);
    }
}

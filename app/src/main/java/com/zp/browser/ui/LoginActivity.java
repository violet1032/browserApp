package com.zp.browser.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.umeng.socialize.UMShareAPI;
import com.zp.browser.AppConfig;
import com.zp.browser.AppContext;
import com.zp.browser.R;
import com.zp.browser.api.FHttpCallBack;
import com.zp.browser.bean.Result;
import com.zp.browser.ui.common.BaseActivity;
import com.zp.browser.ui.dialog.VersionUpdateDialog;
import com.zp.browser.utils.JsonUtils;
import com.zp.browser.utils.UIHelper;

import org.json.JSONException;

import java.util.Map;

public class LoginActivity extends BaseActivity {

    public static void startActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.activity_login);
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);


    }

    @Override
    public void initData() {
        super.initData();

//        if (Build.VERSION.SDK_INT >= 23) {
//            String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.READ_LOGS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SET_DEBUG_APP, Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.GET_ACCOUNTS, Manifest.permission.WRITE_APN_SETTINGS};
//            ActivityCompat.requestPermissions(this, mPermissionList, 123);
//        }

        // 自动登录
//        if (!StringUtils.isEmpty(AppConfig.getInstance().getLoginWxUnionid())) {
//            // 自动登录
//            appLogin(AppConfig.getInstance().getLoginWxUnionid(), AppConfig.getInstance().getLoginWxOpenid(),
//                    AppConfig.getInstance().getLoginWxNickname(), AppConfig.getInstance().getLoginWxNickname());
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

    }

    @Override
    public void initWidget() {
        super.initWidget();

        getAppVersion();
    }

    private void appLogin(String unionid, String openid, String nickname, String headimgurl) {
        FHttpCallBack callBack = new FHttpCallBack() {
            @Override
            public void onFinish() {
                super.onFinish();
                UIHelper.stopLoadingDialog();
            }

            @Override
            public void onPreStart() {
                super.onPreStart();
            }

            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                super.onSuccess(headers, t);
                String str = new String(t);
                Result result = new Result();
                result.parse(str);
                if (result.isOk()) {
                    // 登录成功
                    AppContext.user.parse(str,true);

                    // 缓存信息
                    AppConfig.getInstance().setLoginWxHeadimgurl(AppContext.user.getAvatar());
                    AppConfig.getInstance().setLoginWxUnionid(AppContext.user.getOpenid());
                    AppConfig.getInstance().setLoginWxOpenid(AppContext.user.getOpenid());
                    AppConfig.getInstance().setLoginWxNickname(AppContext.user.getNickname());

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
//        ApiUser.appLogin(unionid, openid, nickname, headimgurl, callBack);
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

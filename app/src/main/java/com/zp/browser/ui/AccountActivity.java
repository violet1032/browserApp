package com.zp.browser.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xcinfo.album.ui.ChooseDialog;
import com.zp.browser.AppConfig;
import com.zp.browser.AppContext;
import com.zp.browser.R;
import com.zp.browser.api.ApiCommon;
import com.zp.browser.api.ApiUser;
import com.zp.browser.api.FHttpCallBack;
import com.zp.browser.bean.Result;
import com.zp.browser.bean.User;
import com.zp.browser.ui.common.BaseActivity;
import com.zp.browser.ui.dialog.EditInfoDialog;
import com.zp.browser.utils.JsonUtils;
import com.zp.browser.utils.StringUtils;
import com.zp.browser.utils.UIHelper;

import org.json.JSONException;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.widget.RoundImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class AccountActivity extends BaseActivity {
    @BindView(id = R.id.umeng_banner_title)
    private TextView tvTitle;
    @BindView(id = R.id.umeng_banner_img_left, click = true)
    private ImageView imgBack;

    @BindView(id = R.id.act_account_bg)
    private LinearLayout layBg;
    @BindView(id = R.id.umeng_banner_lay_bg)
    private RelativeLayout layTitleBg;

    @BindView(id = R.id.act_account_bg_1)
    private LinearLayout layBg_2;

    @BindView(id = R.id.act_account_lay_avatar, click = true)
    private RelativeLayout layAvatar;
    @BindView(id = R.id.act_account_lay_nickname, click = true)
    private RelativeLayout layNickname;
    @BindView(id = R.id.act_account_lay_coin, click = true)
    private RelativeLayout layCoin;
    @BindView(id = R.id.act_account_lay_password, click = true)
    private RelativeLayout layPassword;
    @BindView(id = R.id.act_account_lay_logout, click = true)
    private RelativeLayout layLogout;

    @BindView(id = R.id.act_account_tv_nickname)
    private TextView tvNickname;
    @BindView(id = R.id.act_account_tv_coin)
    private TextView tvCoin;

    @BindView(id = R.id.act_account_img_head)
    private RoundImageView imgHead;

    private static Handler lastHandler;

    private Handler handler;


    public static void startActivity(Context context, Handler handler) {
        Intent intent = new Intent();
        intent.setClass(context, AccountActivity.class);
        context.startActivity(intent);

        lastHandler = handler;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                if (!StringUtils.isEmpty(data.getAction())) {
                    if (data.getAction().equals(ChooseDialog.ACTION_SELECTED_PICS)) {
                        // 图片上传
                        ArrayList<String> uris = (ArrayList<String>) data.getSerializableExtra(ChooseDialog.PUT_INTENT);
                        for (String str :
                                uris) {
                            // 遍历图片上传
                            imgUpLoad(str);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.activity_account);
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);

        switch (v.getId()) {
            case R.id.umeng_banner_img_left:
                finish();
                break;
            case R.id.act_account_lay_logout:
                AppContext.user = new User();
                AppConfig.getInstance().mPreSet("autoLogin", false);
                finish();
                lastHandler.sendEmptyMessage(0);
                break;
            case R.id.act_account_lay_avatar:
                ChooseDialog.startActivity(this, 1, false);
                break;
            case R.id.act_account_lay_nickname:
                EditInfoDialog.startActivity(this, "修改昵称", AppContext.user.getNickname(), handler, 0);
                break;
        }
    }

    @Override
    public void initData() {
        super.initData();

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case 0:
                        int type = message.arg1;
                        switch (type) {
                            case 0:
                                // 昵称
                                String nickname = (String) message.obj;
                                if (!StringUtils.isEmpty(nickname)) {
                                    tvNickname.setText(nickname);
                                    editInfo("nickname", nickname);
                                }
                                break;
                        }
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void initWidget() {
        super.initWidget();

        tvTitle.setText("账号管理");

        ApiCommon.getNetBitmap(AppContext.user.getAvatar(), imgHead, false);
        tvNickname.setText(AppContext.user.getNickname());
        tvCoin.setText(AppContext.user.getCoin() + "");

        changeStyle();
    }


    public void changeStyle() {
        if (AppConfig.getInstance().getmPre().getBoolean("isNight", false)) {
            layBg.setBackgroundColor(getResources().getColor(R.color.night_black_1));
            layTitleBg.setBackgroundColor(getResources().getColor(R.color.night_black_1));
            layBg_2.setBackgroundColor(getResources().getColor(R.color.night_black_2));

            childStyle(layBg_2, getResources().getColor(R.color.night_text_1));

            layAvatar.setBackgroundColor(getResources().getColor(R.color.night_black_2));
            layNickname.setBackgroundColor(getResources().getColor(R.color.night_black_2));
            layCoin.setBackgroundColor(getResources().getColor(R.color.night_black_2));
            layPassword.setBackgroundColor(getResources().getColor(R.color.night_black_2));
            layLogout.setBackgroundColor(getResources().getColor(R.color.night_black_2));
        } else {
            layBg.setBackgroundColor(getResources().getColor(R.color.main_skyblue));
            layTitleBg.setBackgroundColor(getResources().getColor(R.color.main_skyblue));
            layBg_2.setBackgroundColor(getResources().getColor(R.color.gray_bg));

            childStyle(layBg_2, getResources().getColor(R.color.black_3));

            layAvatar.setBackgroundColor(getResources().getColor(R.color.white));
            layNickname.setBackgroundColor(getResources().getColor(R.color.white));
            layCoin.setBackgroundColor(getResources().getColor(R.color.white));
            layPassword.setBackgroundColor(getResources().getColor(R.color.white));
            layLogout.setBackgroundColor(getResources().getColor(R.color.white));
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

    /**
     * 图片上传
     */
    public void imgUpLoad(final String uri) {
        FHttpCallBack callBack = new FHttpCallBack() {
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                super.onSuccess(headers, t);
                String str = new String(t);
                Result result = new Result().parse(str);
                if (result.isOk()) {
                    try {
                        JsonUtils jsonUtils = new JsonUtils(str);
                        changeAvatar(jsonUtils.getString("message"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onPreStart() {
                super.onPreStart();
                UIHelper.showLoadingDialog(AccountActivity.this);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                UIHelper.stopLoadingDialog();
            }
        };

        ApiCommon.postFile(new File(uri), callBack);
    }

    public void changeAvatar(String url) {
        FHttpCallBack callBack = new FHttpCallBack() {
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                super.onSuccess(headers, t);
                String str = new String(t);
                Result result = new Result().parse(str);
                if (result.isOk()) {
                    UIHelper.ToastMessage("修改成功");
                    try {
                        JsonUtils jsonUtils = new JsonUtils(str);
                        String url = jsonUtils.getString("content");
                        ApiCommon.getNetBitmap(url, imgHead, false);

                        Message message = new Message();
                        message.what = 1;
                        message.obj = url;
                        lastHandler.sendMessage(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else
                    UIHelper.ToastMessage(result.getMsg());
            }

            @Override
            public void onPreStart() {
                super.onPreStart();
                UIHelper.showLoadingDialog(AccountActivity.this);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                UIHelper.stopLoadingDialog();
            }
        };
        ApiUser.editInfo("avatar", url, callBack);
    }

    public void editInfo(String name, String content) {
        FHttpCallBack callBack = new FHttpCallBack() {
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                super.onSuccess(headers, t);
                String str = new String(t);
                Result result = new Result().parse(str);
                if (result.isOk()) {

                } else {
                    UIHelper.ToastMessage(result.getMsg());
                }
            }

            @Override
            public void onPreStart() {
                super.onPreStart();
                UIHelper.showLoadingDialog(AccountActivity.this);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                UIHelper.stopLoadingDialog();
            }
        };
        ApiUser.editInfo(name, content, callBack);
    }
}

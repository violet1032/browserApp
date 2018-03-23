package com.zp.browser.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zp.browser.AppContext;
import com.zp.browser.R;
import com.zp.browser.api.ApiCommon;
import com.zp.browser.api.ApiUser;
import com.zp.browser.api.FHttpCallBack;
import com.zp.browser.bean.Result;
import com.zp.browser.ui.common.BaseActivity;
import com.zp.browser.utils.JsonUtils;
import com.zp.browser.utils.UIHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.widget.RoundImageView;

import java.util.Map;

public class UserActivity extends BaseActivity {
    @BindView(id = R.id.umeng_banner_title)
    private TextView tvTitle;
    @BindView(id = R.id.umeng_banner_img_left, click = true)
    private ImageView imgBack;

    @BindView(id = R.id.act_user_img_head)
    private RoundImageView imgHead;
    @BindView(id = R.id.act_user_tv_nickname)
    private TextView tvNickname;
    @BindView(id = R.id.act_user_tv_code)
    private TextView tvCode;
    @BindView(id = R.id.act_user_tv_count)
    private TextView tvCount;
    @BindView(id = R.id.act_user_tv_coin)
    private TextView tvCoin;

    @BindView(id=R.id.act_user_lay_share)
    private LinearLayout layShare;
    @BindView(id=R.id.act_user_lay_share_item)
    private RelativeLayout layShareItem;
    @BindView(id=R.id.act_user_lay_invite)
    private LinearLayout layInvite;
    @BindView(id=R.id.act_user_lay_invite_item)
    private RelativeLayout layInviteItem;


    public static void startActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, UserActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.activity_user);
    }

    @Override
    public void initWidget() {
        super.initWidget();

        tvTitle.setText("个人中心");
    }

    @Override
    public void initData() {
        super.initData();

        getUserInfo();
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);

        switch (v.getId()) {
            case R.id.umeng_banner_img_left:
                finish();
                break;
        }
    }

    public void getUserInfo() {
        if (AppContext.user.getId() > 0) {
            FHttpCallBack callBack = new FHttpCallBack() {

                @Override
                public void onPreStart() {
                    super.onPreStart();
                    UIHelper.showLoadingDialog(UserActivity.this);
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    UIHelper.stopLoadingDialog();
                }

                @Override
                public void onSuccess(Map<String, String> headers, byte[] t) {
                    super.onSuccess(headers, t);
                    String str = new String(t);
                    Result result = new Result();
                    result.parse(str);
                    if (result.isOk()) {
                        AppContext.user.parse(str);

                        ApiCommon.getNetBitmap(AppContext.user.getAvatar(), imgHead, false);
                        tvNickname.setText(AppContext.user.getNickname());
                        tvCode.setText(AppContext.user.getInvitation_code());
                        tvCount.setText(AppContext.user.getCount() + "");
                        tvCoin.setText(AppContext.user.getCoin().toString());
                    }
                }

                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);

                }
            };
            ApiUser.getUserInfo(callBack);
        }
    }

    public void getSystemParam() {
        if (AppContext.user.getId() > 0) {
            FHttpCallBack callBack = new FHttpCallBack() {

                @Override
                public void onPreStart() {
                    super.onPreStart();
                    UIHelper.showLoadingDialog(UserActivity.this);
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    UIHelper.stopLoadingDialog();
                }

                @Override
                public void onSuccess(Map<String, String> headers, byte[] t) {
                    super.onSuccess(headers, t);
                    String str = new String(t);
                    Result result = new Result();
                    result.parse(str);
                    if (result.isOk()) {
                        try {
                            JsonUtils jsonUtils = new JsonUtils(str);
                            JSONArray jsonArray = jsonUtils.getJSONArray("share_rule_news");
                            layShare.removeView(layShareItem);
                            for (int i = 0; i < jsonArray.length(); i++) {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            UIHelper.ToastMessage("规则数据解析错误");
                        }
                    }
                }

                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);

                }
            };
            ApiUser.getSystemParam(callBack);
        }
    }
}

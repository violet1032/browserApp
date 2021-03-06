package com.zp.browser.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zp.browser.AppConfig;
import com.zp.browser.AppContext;
import com.zp.browser.R;
import com.zp.browser.api.ApiCommon;
import com.zp.browser.api.ApiUser;
import com.zp.browser.api.FHttpCallBack;
import com.zp.browser.bean.Result;
import com.zp.browser.ui.common.BaseActivity;
import com.zp.browser.utils.JsonUtils;
import com.zp.browser.utils.StringUtils;
import com.zp.browser.utils.UIHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.widget.RoundImageView;

import java.util.Map;

public class UserActivity extends BaseActivity {
    @BindView(id = R.id.umeng_banner_title)
    private TextView tvTitle;
    @BindView(id = R.id.umeng_banner_tv_right)
    private TextView tvRight;
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

    @BindView(id = R.id.act_user_lay_share)
    private LinearLayout layShare;
    @BindView(id = R.id.act_user_lay_invite)
    private LinearLayout layInvite;

    @BindView(id = R.id.act_user_tv_share, click = true)
    private TextView tvGoShare;
    @BindView(id = R.id.act_user_tv_invite, click = true)
    private TextView tvGoInvite;
    @BindView(id = R.id.act_user_tv_account, click = true)
    private TextView tvAccount;
    @BindView(id = R.id.act_user_tv_difficulty, click = true)
    private TextView tvDifficulty;
    @BindView(id = R.id.act_user_tv_cost, click = true)
    private TextView tvCost;
    @BindView(id = R.id.act_user_tv_1, click = true)
    private TextView tv1;
    @BindView(id = R.id.act_user_tv_2, click = true)
    private TextView tv2;
    @BindView(id = R.id.act_user_tv_3, click = true)
    private TextView tv3;

    @BindView(id = R.id.act_user_lay_bg)
    private LinearLayout layBg;
    @BindView(id = R.id.act_user_lay_bg_2)
    private LinearLayout layBg_2;
    @BindView(id = R.id.umeng_banner_lay_bg)
    private RelativeLayout layTitleBg;
    @BindView(id = R.id.act_user_lay_bg_3)
    private RelativeLayout layBg_3;
    @BindView(id = R.id.act_user_lay_bg_4)
    private LinearLayout layBg_4;

    @BindView(id = R.id.act_user_lay_1, click = true)
    private LinearLayout lay1;
    @BindView(id = R.id.act_user_lay_2, click = true)
    private RelativeLayout lay2;

    private Handler handler;

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

        if (AppContext.user.getId() > 0) {
            tvRight.setVisibility(View.VISIBLE);
            tvRight.setText(getString(R.string.user_text_13) + AppContext.user.getCost());
            tvRight.setTextColor(getResources().getColor(R.color.red));
        }

        tv1.setText(AppContext.short_name+"挖矿获利次数");
        tv2.setText("已获得"+AppContext.short_name+"糖果数量");
        tv3.setText("如何挖矿获得"+AppContext.short_name+"糖果");

        changeStyle();
    }

    @Override
    public void initData() {
        super.initData();

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == 0) {
                    finish();
                } else if (message.what == 1) {
                    ApiCommon.getNetBitmap((String) message.obj, imgHead, false);
                }
                return false;
            }
        });

        getUserInfo();

        getSystemParam();
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);

        switch (v.getId()) {
            case R.id.umeng_banner_img_left:
                finish();
                break;
            case R.id.act_user_tv_share:
                finish();
                break;
            case R.id.act_user_tv_invite:
                InviteFriendsActivity.startActivity(UserActivity.this);
                break;
            case R.id.act_user_tv_account:
                AccountActivity.startActivity(this, handler);
                break;
            case R.id.act_user_lay_1:
                AwardHistoryActivity.startActivity(this);
                break;
            case R.id.act_user_lay_2:
                AwardHistoryActivity.startActivity(this);
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

                        tvDifficulty.setText(AppContext.user.getDifficulty()+"");
                        tvCost.setText("$ " + AppContext.user.getCost());
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
                            LayoutInflater inflater = LayoutInflater.from(UserActivity.this);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                RelativeLayout item = (RelativeLayout) inflater.inflate(R.layout.layout_rule_item, null);

                                JsonUtils jsonUtils1 = new JsonUtils(jsonArray.getString(i));
                                String unit = jsonUtils1.getString("unit");
                                if (!StringUtils.isEmpty(unit)) {
                                    if (unit.contains("贝壳")) {
                                    } else {
                                        item.findViewById(R.id.layout_rule_item_img_3).setVisibility(View.GONE);
                                    }
                                }

                                ((TextView) item.findViewById(R.id.layout_rule_item_tv_name)).setText(jsonUtils1.getString("name"));
                                ((TextView) item.findViewById(R.id.act_user_tv_num)).setText(jsonUtils1.getString("award"));
                                ((TextView) item.findViewById(R.id.layout_rule_item_tv_unit)).setText(unit);

                                layShare.addView(item);
                            }

                            JSONArray jsonArray2 = jsonUtils.getJSONArray("share_rule_qrcode");
                            for (int i = 0; i < jsonArray2.length(); i++) {
                                RelativeLayout item = (RelativeLayout) inflater.inflate(R.layout.layout_rule_item, null);

                                JsonUtils jsonUtils1 = new JsonUtils(jsonArray2.getString(i));
                                String unit = jsonUtils1.getString("unit");
                                if (!StringUtils.isEmpty(unit)) {
                                    if (unit.contains("贝壳")) {
                                    } else {
                                        item.findViewById(R.id.layout_rule_item_img_3).setVisibility(View.GONE);
                                    }
                                }

                                ((TextView) item.findViewById(R.id.layout_rule_item_tv_name)).setText(jsonUtils1.getString("name"));
                                ((TextView) item.findViewById(R.id.act_user_tv_num)).setText(jsonUtils1.getString("award"));
                                ((TextView) item.findViewById(R.id.layout_rule_item_tv_unit)).setText(unit);

                                layInvite.addView(item);
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

    public void changeStyle() {
        if (AppConfig.getInstance().getmPre().getBoolean("isNight", false)) {
            layBg.setBackgroundColor(getResources().getColor(R.color.night_black_1));
            layTitleBg.setBackgroundColor(getResources().getColor(R.color.night_black_1));
            layBg_2.setBackgroundColor(getResources().getColor(R.color.night_black_2));
            layBg_3.setBackgroundColor(getResources().getColor(R.color.night_black_2));
            layBg_4.setBackgroundColor(getResources().getColor(R.color.night_black_2));
        } else {
            layBg.setBackgroundColor(getResources().getColor(R.color.main_skyblue));
            layTitleBg.setBackgroundColor(getResources().getColor(R.color.main_skyblue));
            layBg_2.setBackgroundColor(getResources().getColor(R.color.gray_bg));
            layBg_3.setBackgroundColor(getResources().getColor(R.color.white));
            layBg_4.setBackgroundColor(getResources().getColor(R.color.gray_6));
        }
    }
}

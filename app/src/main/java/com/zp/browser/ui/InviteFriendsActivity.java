package com.zp.browser.ui;

import android.content.Context;
import android.content.Intent;
import android.text.ClipboardManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.zp.browser.AppConfig;
import com.zp.browser.AppContext;
import com.zp.browser.R;
import com.zp.browser.api.ApiUser;
import com.zp.browser.api.FHttpCallBack;
import com.zp.browser.bean.Result;
import com.zp.browser.ui.common.BaseActivity;
import com.zp.browser.ui.dialog.ShareDialog;
import com.zp.browser.utils.JsonUtils;
import com.zp.browser.utils.UIHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.kymjs.kjframe.ui.BindView;

import java.math.BigDecimal;
import java.util.Map;

public class InviteFriendsActivity extends BaseActivity {
    @BindView(id = R.id.umeng_banner_title)
    private TextView tvTitle;
    @BindView(id = R.id.umeng_banner_img_left, click = true)
    private ImageView imgBack;

    @BindView(id = R.id.act_invite_friends_img_invite, click = true)
    private ImageView imgInvite;

    @BindView(id = R.id.act_invite_friends_tv_url)
    private TextView tvUrl;
    @BindView(id = R.id.act_invite_friends_tv_copy, click = true)
    private TextView tvCopy;
    @BindView(id = R.id.act_invite_friends_tv_time, click = true)
    private TextView tvTime;
    @BindView(id = R.id.act_invite_friends_tv_num, click = true)
    private TextView tvCoin;
    @BindView(id = R.id.act_invite_friends_tv_rule, click = true)
    private TextView tvRule;
    @BindView(id = R.id.act_invite_friends_lay_record)
    private TableLayout layHistory;

    @BindView(id = R.id.act_invite_friends_lay_bg)
    private LinearLayout layBg;
    @BindView(id = R.id.umeng_banner_lay_bg)
    private RelativeLayout layTitleBg;
    @BindView(id = R.id.act_invite_friends_lay_bg_2)
    private LinearLayout layBg_2;

    public static void startActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, InviteFriendsActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.activity_invite_friends);
    }

    @Override
    public void initWidget() {
        super.initWidget();

        tvTitle.setText("邀请好友拿佣金");

        tvUrl.setText(AppContext.user.getShareLink());

        changeStyle();
    }

    @Override
    public void initData() {
        super.initData();

        getInviteInfo();
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);

        switch (v.getId()) {
            case R.id.umeng_banner_img_left:
                finish();
                break;
            case R.id.act_invite_friends_img_invite:
                ShareDialog.startActivity(InviteFriendsActivity.this, 0);
                break;
            case R.id.act_invite_friends_tv_copy:
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setText(AppContext.user.getShareLink());
                Toast.makeText(this, "复制成功", Toast.LENGTH_LONG).show();
                break;
        }
    }

    public void getInviteInfo() {
        FHttpCallBack callBack = new FHttpCallBack() {
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                super.onSuccess(headers, t);
                String str = new String(t);
                Result result = new Result().parse(str);
                if (result.isOk()) {
                    try {
                        JsonUtils jsonUtils = new JsonUtils(str);
                        String time = jsonUtils.getString("time");

                        BigDecimal bigDecimal = jsonUtils.getBigDecimal("coin");

                        tvTime.setText(time);
                        tvCoin.setText(bigDecimal.toString());

                        // 邀请规则
                        StringBuffer stringBuffer = new StringBuffer();
                        JSONArray jsonArray = jsonUtils.getJSONArray("invite_rule");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            stringBuffer.append(i + 1);
                            stringBuffer.append("、");
                            stringBuffer.append(jsonArray.getString(i));
                            if (i < jsonArray.length() - 1)
                                stringBuffer.append("\n");
                        }
                        tvRule.setText(stringBuffer.toString());

                        // 邀请记录
                        JSONArray jsonArray1 = jsonUtils.getJSONArray("invite_history");

                        boolean m1 = false;
                        boolean m2 = false;
                        boolean m3 = false;
                        boolean m4 = false;
                        for (int i = 0; i < jsonArray1.length(); i++) {
                            JsonUtils jsonUtils1 = new JsonUtils(jsonArray1.getString(i));
                            TableRow tableRow = null;
                            if (jsonUtils1.getString("name").equals("M1")) {
                                tableRow = (TableRow) layHistory.getChildAt(2);
                                m1 = true;
                            } else if (jsonUtils1.getString("name").equals("M2")) {
                                tableRow = (TableRow) layHistory.getChildAt(4);
                                m2 = true;
                            } else if (jsonUtils1.getString("name").equals("M3")) {
                                tableRow = (TableRow) layHistory.getChildAt(6);
                                m3 = true;
                            } else if (jsonUtils1.getString("name").equals("M4")) {
                                tableRow = (TableRow) layHistory.getChildAt(8);
                                m4 = true;
                            }

                            if (tableRow != null) {
                                ((TextView) tableRow.getChildAt(0)).setText(jsonUtils1.getString("name"));
                                ((TextView) tableRow.getChildAt(1)).setText(jsonUtils1.getString("award_once"));
                                ((TextView) tableRow.getChildAt(2)).setText(jsonUtils1.getString("award_percent"));
                                ((TextView) tableRow.getChildAt(3)).setText(jsonUtils1.getString("count"));
                            }
                        }

                        if (m1) {
                            layHistory.getChildAt(1).setVisibility(View.VISIBLE);
                            layHistory.getChildAt(2).setVisibility(View.VISIBLE);
                        } else {
                            layHistory.getChildAt(1).setVisibility(View.GONE);
                            layHistory.getChildAt(2).setVisibility(View.GONE);
                        }

                        if (m2) {
                            layHistory.getChildAt(3).setVisibility(View.VISIBLE);
                            layHistory.getChildAt(4).setVisibility(View.VISIBLE);
                        } else {
                            layHistory.getChildAt(3).setVisibility(View.GONE);
                            layHistory.getChildAt(4).setVisibility(View.GONE);
                        }

                        if (m3) {
                            layHistory.getChildAt(5).setVisibility(View.VISIBLE);
                            layHistory.getChildAt(6).setVisibility(View.VISIBLE);
                        } else {
                            layHistory.getChildAt(5).setVisibility(View.GONE);
                            layHistory.getChildAt(6).setVisibility(View.GONE);
                        }

                        if (m4) {
                            layHistory.getChildAt(7).setVisibility(View.VISIBLE);
                            layHistory.getChildAt(8).setVisibility(View.VISIBLE);
                        } else {
                            layHistory.getChildAt(7).setVisibility(View.GONE);
                            layHistory.getChildAt(8).setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    UIHelper.ToastMessage(result.getMsg());
                }
            }

            @Override
            public void onPreStart() {
                super.onPreStart();
                UIHelper.showLoadingDialog(InviteFriendsActivity.this);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                UIHelper.stopLoadingDialog();
            }
        };
        ApiUser.getInviteInfo(callBack);
    }

    public void changeStyle() {
        if (AppConfig.getInstance().getmPre().getBoolean("isNight", false)) {
            layBg.setBackgroundColor(getResources().getColor(R.color.night_black_1));
            layTitleBg.setBackgroundColor(getResources().getColor(R.color.night_black_1));

            childStyle(layBg_2, getResources().getColor(R.color.night_text_1));
        } else {
            layBg.setBackgroundColor(getResources().getColor(R.color.main_skyblue));
            layTitleBg.setBackgroundColor(getResources().getColor(R.color.main_skyblue));
            childStyle(layBg_2, getResources().getColor(R.color.white));
        }
    }

    public void childStyle(ViewGroup viewGroup, int color) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof TextView) {
                if (color == getResources().getColor(R.color.white) && view.getId() == R.id.act_invite_friends_tv_1)
                    ((TextView) view).setTextColor(getResources().getColor(R.color.main_skyblue));
                else if(color == getResources().getColor(R.color.white) && view.getId() == R.id.act_invite_friends_tv_copy)
                    ((TextView) view).setTextColor(getResources().getColor(R.color.orange_3));
                else
                    ((TextView) view).setTextColor(color);

            } else if (view instanceof ViewGroup) {
                if (((ViewGroup) view).getChildCount() > 0) {
                    childStyle((ViewGroup) view, color);
                }
            }
        }
    }
}

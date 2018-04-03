package com.zp.browser.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zp.browser.AppConfig;
import com.zp.browser.AppContext;
import com.zp.browser.R;
import com.zp.browser.api.ApiMain;
import com.zp.browser.api.FHttpCallBack;
import com.zp.browser.bean.Result;
import com.zp.browser.ui.common.BaseActivity;
import com.zp.browser.ui.dialog.ClearDialog;
import com.zp.browser.ui.dialog.PageStyleDialog;
import com.zp.browser.ui.dialog.VersionUpdateDialog;
import com.zp.browser.utils.JsonUtils;
import com.zp.browser.utils.UIHelper;
import com.zp.browser.widget.SwitchButton;

import org.json.JSONException;
import org.kymjs.kjframe.ui.BindView;

import java.util.Map;

public class SettingActivity extends BaseActivity {
    @BindView(id = R.id.umeng_banner_title)
    private TextView tvTitle;
    @BindView(id = R.id.umeng_banner_img_left, click = true)
    private ImageView imgBack;

    @BindView(id = R.id.act_setting_bg)
    private LinearLayout layBg;
    @BindView(id = R.id.umeng_banner_lay_bg)
    private RelativeLayout layTitleBg;

    @BindView(id = R.id.act_setting_bg_1)
    private LinearLayout layBg_2;

    @BindView(id = R.id.act_setting_lay_clear, click = true)
    private RelativeLayout layClear;
//    @BindView(id = R.id.act_setting_lay_download, click = true)
//    private RelativeLayout layDownload;
    @BindView(id = R.id.act_setting_lay_last, click = true)
    private RelativeLayout layLast;
    @BindView(id = R.id.act_setting_lay_type, click = true)
    private RelativeLayout layType;
//    @BindView(id=R.id.act_setting_lay_faxian,click = true)
//    private RelativeLayout layFaxian;
//    @BindView(id=R.id.act_setting_lay_douzaisou,click = true)
//    private RelativeLayout layDouzaisou;

    @BindView(id = R.id.act_setting_switch_last)
    private SwitchButton sbtnLast;
//    @BindView(id=R.id.act_setting_switch_faxian)
//    private SwitchButton sbtnFaxian;
//    @BindView(id=R.id.act_setting_switch_douzaisou)
//    private SwitchButton sbtnDouzaisou;

    @BindView(id = R.id.act_setting_tv_version)
    private TextView tvVersion;
    @BindView(id = R.id.act_setting_lay_version, click = true)
    private RelativeLayout layVersion;


    public static void startActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, SettingActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.activity_setting);
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);

        switch (v.getId()) {
            case R.id.umeng_banner_img_left:
                finish();
                break;
            case R.id.act_setting_lay_last:
                //
                if (sbtnLast.isChecked())
                    sbtnLast.setChecked(false);
                else
                    sbtnLast.setChecked(true);
                AppConfig.getInstance().setLast(sbtnLast.isChecked());
                break;
            case R.id.act_setting_lay_version:
                getVersion();
                break;
            case R.id.act_setting_lay_clear:
                ClearDialog.startActivity(this);
                break;
            case R.id.act_setting_lay_type:
                PageStyleDialog.startActivity(this);
                break;

//            case R.id.act_setting_lay_faxian:
//                //
//                if(sbtnFaxian.isChecked())
//                    sbtnFaxian.setChecked(false);
//                else
//                    sbtnFaxian.setChecked(true);
//                AppConfig.getInstance().setFaxian(sbtnFaxian.isChecked());
//                break;
//            case R.id.act_setting_lay_douzaisou:
//                //
//                if(sbtnDouzaisou.isChecked())
//                    sbtnDouzaisou.setChecked(false);
//                else
//                    sbtnDouzaisou.setChecked(true);
//                AppConfig.getInstance().setDouzaisou(sbtnDouzaisou.isChecked());
//                break;
        }
    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    public void initWidget() {
        super.initWidget();

        tvTitle.setText("设置");
        tvVersion.setText("v" + AppContext.versionName);

        sbtnLast.setChecked(AppConfig.getInstance().isLast());

        sbtnLast.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                AppConfig.getInstance().setLast(b);
            }
        });
//        sbtnDouzaisou.setChecked(AppConfig.getInstance().isDouzaisou());
//        sbtnFaxian.setChecked(AppConfig.getInstance().isFaxian());
//        sbtnDouzaisou.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                AppConfig.getInstance().setDouzaisou(b);
//            }
//        });
//        sbtnFaxian.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                AppConfig.getInstance().setFaxian(b);
//            }
//        });

        changeStyle();
    }


    public void changeStyle() {
        if (AppConfig.getInstance().getmPre().getBoolean("isNight", false)) {
            layBg.setBackgroundColor(getResources().getColor(R.color.night_black_1));
            layTitleBg.setBackgroundColor(getResources().getColor(R.color.night_black_1));
            layBg_2.setBackgroundColor(getResources().getColor(R.color.night_black_2));

            childStyle(layBg_2, getResources().getColor(R.color.night_text_1));

            layClear.setBackgroundColor(getResources().getColor(R.color.night_black_2));
//            layDownload.setBackgroundColor(getResources().getColor(R.color.night_black_2));
            layLast.setBackgroundColor(getResources().getColor(R.color.night_black_2));
            layType.setBackgroundColor(getResources().getColor(R.color.night_black_2));
            layVersion.setBackgroundColor(getResources().getColor(R.color.night_black_2));
//            layFaxian.setBackgroundColor(getResources().getColor(R.color.night_black_2));
//            layDouzaisou.setBackgroundColor(getResources().getColor(R.color.night_black_2));
        } else {
            layBg.setBackgroundColor(getResources().getColor(R.color.main_skyblue));
            layTitleBg.setBackgroundColor(getResources().getColor(R.color.main_skyblue));
            layBg_2.setBackgroundColor(getResources().getColor(R.color.gray_bg));

            childStyle(layBg_2, getResources().getColor(R.color.black_3));

            layClear.setBackgroundColor(getResources().getColor(R.color.white));
//            layDownload.setBackgroundColor(getResources().getColor(R.color.white));
            layLast.setBackgroundColor(getResources().getColor(R.color.white));
            layType.setBackgroundColor(getResources().getColor(R.color.white));
            layVersion.setBackgroundColor(getResources().getColor(R.color.white));
//            layFaxian.setBackgroundColor(getResources().getColor(R.color.white));
//            layDouzaisou.setBackgroundColor(getResources().getColor(R.color.white));
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

    public void getVersion() {
        FHttpCallBack callBack = new FHttpCallBack() {
            @Override
            public void onPreStart() {
                super.onPreStart();
                UIHelper.showLoadingDialog(SettingActivity.this);
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
                Result result = new Result().parse(str);
                if (result.isOk()) {
                    try {
                        JsonUtils j = new JsonUtils(str);
                        JsonUtils jsonUtils = j.getJSONUtils("version");

                        if (jsonUtils.getString("version").compareTo(AppContext.versionName) > 0) {
                            VersionUpdateDialog.startActivity(SettingActivity.this, jsonUtils.getString("version"),
                                    jsonUtils.getString("content"), jsonUtils.getBoolean("must"), jsonUtils.getString("url"));
                        } else {
                            UIHelper.ToastMessage("已经是最新版");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        ApiMain.getVersion(callBack);
    }
}

package com.zp.browser.ui.dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.zp.browser.AppConfig;
import com.zp.browser.AppContext;
import com.zp.browser.R;
import com.zp.browser.api.ApiUser;
import com.zp.browser.api.FHttpCallBack;
import com.zp.browser.bean.News;
import com.zp.browser.bean.Result;
import com.zp.browser.ui.common.BaseActivity;
import com.zp.browser.utils.ImageCreateUtil;
import com.zp.browser.utils.ImageUtils;
import com.zp.browser.utils.JsonUtils;
import com.zp.browser.utils.StringUtils;
import com.zp.browser.utils.UIHelper;

import org.json.JSONException;
import org.kymjs.kjframe.ui.BindView;

import java.io.File;
import java.util.Map;


/**
 * Created by Administrator on 2018/3/24 0024.
 */
public class ShareDialog extends BaseActivity {
    private int type;

    @BindView(id = R.id.dialog_share_img)
    private ImageView imgContent;

    @BindView(id = R.id.dialog_share_btn_cancel, click = true)
    private Button btnCancel;
    @BindView(id = R.id.dialog_share_img_qq, click = true)
    private ImageView imgQQ;

    @BindView(id = R.id.dialog_share_lay_1)
    private LinearLayout layBg;

    private Handler handler;

    private String path;
    private News news;

    @BindView(id = R.id.dialog_share_tv_content)
    private TextView textContent;

    public static void startActivity(Context activity, int type) {
        Intent intent = new Intent();
        intent.setClass(activity, ShareDialog.class);
        intent.putExtra("type", type);
        activity.startActivity(intent);
    }

    public static void startActivity(Context activity, int type, News news) {
        Intent intent = new Intent();
        intent.setClass(activity, ShareDialog.class);
        intent.putExtra("type", type);
        intent.putExtra("news", news);
        activity.startActivity(intent);
    }

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.dialog_share);

        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setWindowAnimations(R.style.AnimBottom);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        lp.width = metrics.widthPixels; // 宽度设置
        dialogWindow.setAttributes(lp);
    }

    @Override
    public void initWidget() {
        super.initWidget();

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
                        path = (String) message.obj;
                        imgContent.setImageBitmap(ImageUtils.getBitmapByPath(path));
                        break;
                    case 2:
                        ImageCreateUtil.createShareImage(AppContext.user.getShareLink(), AppContext.user.getShare_news_bg(),
                                handler, news, ShareDialog.this, textContent,registerAward);
                        break;
                }
                return false;
            }
        });

        type = getIntent().getIntExtra("type", 0);

        news = (News) getIntent().getSerializableExtra("news");
        textContent.setText(Html.fromHtml(news.getContent()));

        if (type == 0) {
            // 邀请好友
            ImageCreateUtil.createInviteImage(AppContext.user.getShareLink(), AppContext.user.getInvite_bg(), handler);
        } else {
            getRegisterAward();
        }

//        textContent.setVisibility(View.GONE);
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
            case R.id.dialog_share_btn_cancel:
                finish();
                break;
            case R.id.dialog_share_img_qq:
                if (StringUtils.isEmpty(path)) {
                    UIHelper.ToastMessage("请等待二维码生成");
                } else {
                    File file = new File(path);
                    UMImage image = new UMImage(ShareDialog.this, file);//本地文件
                    image.compressStyle = UMImage.CompressStyle.QUALITY;//质量压缩，适合长图的分享
                    image.compressFormat = Bitmap.CompressFormat.PNG;//用户分享透明背景的图片可以设置这种方式，但是qq好友，微信朋友圈，不支持透明背景图片，会变成黑色
                    ShareAction shareAction = new ShareAction(ShareDialog.this).withText("区块浏览器");
                    shareAction.setPlatform(SHARE_MEDIA.QQ);
                    shareAction.setCallback(new UMShareListener() {
                        @Override
                        public void onStart(SHARE_MEDIA share_media) {

                        }

                        @Override
                        public void onResult(SHARE_MEDIA share_media) {
                            Toast.makeText(ShareDialog.this, "分享成功", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onError(SHARE_MEDIA share_media, Throwable throwable) {

                        }

                        @Override
                        public void onCancel(SHARE_MEDIA share_media) {

                        }
                    });
                    shareAction.withMedia(image).share();
                }
                break;
        }
    }

    public void changeStyle() {
        if (AppConfig.getInstance().getmPre().getBoolean("isNight", false)) {
            layBg.setBackgroundColor(getResources().getColor(R.color.night_black_2));
            btnCancel.setTextColor(getResources().getColor(R.color.night_text_1));
        } else {
            layBg.setBackgroundColor(getResources().getColor(R.color.white));
            btnCancel.setTextColor(getResources().getColor(R.color.orange_3));
        }
    }

    private String registerAward = "0";

    public void getRegisterAward() {
        FHttpCallBack callBack = new FHttpCallBack() {
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                super.onSuccess(headers, t);
                String str = new String(t);
                Result result = new Result().parse(str);
                if (result.isOk()) {
                    try {
                        JsonUtils jsonUtils = new JsonUtils(str);
                        registerAward = jsonUtils.getString("data");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                handler.sendEmptyMessage(2);
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                handler.sendEmptyMessage(2);
            }
        };
        ApiUser.getRegisterAward(callBack);
    }

}

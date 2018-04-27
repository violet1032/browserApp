package com.zp.browser.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
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

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
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
import com.zp.browser.widget.CustomDialog;

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
    @BindView(id = R.id.dialog_share_img_qqzone, click = true)
    private ImageView imgQZone;
    @BindView(id = R.id.dialog_share_img_wechat, click = true)
    private ImageView imgWechat;
    @BindView(id = R.id.dialog_share_img_wechat_friends, click = true)
    private ImageView imgWechatFriends;

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

                        if (type == 0) {
                            // 邀请好友
                            String link;
                            if (AppContext.user.getId() > 0)
                                link = AppContext.user.getShareLink();
                            else
                                link = shareLink;

                            String bg;
                            if (AppContext.user.getId() > 0)
                                bg = AppContext.user.getInvite_bg();
                            else
                                bg = inviteBg;
                            ImageCreateUtil.createInviteImage(link, bg, handler, registerAward);
                        } else {
                            String link;
                            if (AppContext.user.getId() > 0)
                                link = AppContext.user.getShareLink();
                            else
                                link = shareLink;
                            ImageCreateUtil.createShareImage(link, AppContext.user.getShare_news_bg(),
                                    handler, news, ShareDialog.this, textContent, registerAward);
                        }
                        break;
                }
                return false;
            }
        });

        type = getIntent().getIntExtra("type", 0);

        if (type == 1) {
            news = (News) getIntent().getSerializableExtra("news");
            textContent.setTextSize(AppContext.share_font_size);
            textContent.setText(Html.fromHtml(news.getContent()));
        }

        getRegisterAward();

//        textContent.setVisibility(View.GONE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
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
                    share(SHARE_MEDIA.QQ);
                }
                break;
            case R.id.dialog_share_img_qqzone:
                if (StringUtils.isEmpty(path)) {
                    UIHelper.ToastMessage("请等待二维码生成");
                } else {
                    share(SHARE_MEDIA.QZONE);
                }
                break;
            case R.id.dialog_share_img_wechat:
                if (StringUtils.isEmpty(path)) {
                    UIHelper.ToastMessage("请等待二维码生成");
                } else {
                    share(SHARE_MEDIA.WEIXIN);
                }
                break;
            case R.id.dialog_share_img_wechat_friends:
                if (StringUtils.isEmpty(path)) {
                    UIHelper.ToastMessage("请等待二维码生成");
                } else {
                    share(SHARE_MEDIA.WEIXIN_CIRCLE);
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
    private String shareLink;
    private String inviteBg;

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
                        shareLink = jsonUtils.getString("share");
                        inviteBg = jsonUtils.getString("invite_bg");
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


    public void shareAward() {
        FHttpCallBack callBack = new FHttpCallBack() {
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                super.onSuccess(headers, t);
                String str = new String(t);
                Result result = new Result().parse(str);
                if (result.isOk()) {
                    UIHelper.ToastMessage("已成功获得奖励");
                } else
                    UIHelper.ToastMessage(result.getMsg());
            }
        };
        ApiUser.shareAward(news.getId(), callBack);
    }

    public void share(final SHARE_MEDIA share_media){
        if(AppConfig.getInstance().getmPre().getBoolean("firstShare",true) && type == 1){
            CustomDialog.Builder builder = new CustomDialog.Builder (ShareDialog.this);
            builder.setShowSuccess(true)
                    .setTitle("分享后需要返回区块浏览器才能得到奖励")
                    .setShowNotice(false)
                    .setShowSuccess(false)
                    .setOnTouchOutside(false)//设置点击周围不退回
                            //设置监听
                    .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton("分享", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                            AppConfig.getInstance().mPreSet("firstShare", false);
                            File file = new File(path);
                            UMImage image = new UMImage(ShareDialog.this, file);//本地文件
                            image.compressStyle = UMImage.CompressStyle.QUALITY;//质量压缩，适合长图的分享
                            image.compressFormat = Bitmap.CompressFormat.PNG;//用户分享透明背景的图片可以设置这种方式，但是qq好友，微信朋友圈，不支持透明背景图片，会变成黑色
                            ShareAction shareAction = new ShareAction(ShareDialog.this).withText("区块浏览器");
                            shareAction.setPlatform(share_media);
                            shareAction.setCallback(new UMShareListener() {
                                @Override
                                public void onStart(SHARE_MEDIA share_media) {
                                }

                                @Override
                                public void onResult(SHARE_MEDIA share_media) {
                                    if (AppContext.user.getId() > 0 && type == 1)
                                        shareAward();
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
                    }).create().show();
        }else{
            File file = new File(path);
            UMImage image = new UMImage(ShareDialog.this, file);//本地文件
            image.compressStyle = UMImage.CompressStyle.QUALITY;//质量压缩，适合长图的分享
            image.compressFormat = Bitmap.CompressFormat.PNG;//用户分享透明背景的图片可以设置这种方式，但是qq好友，微信朋友圈，不支持透明背景图片，会变成黑色
            ShareAction shareAction = new ShareAction(ShareDialog.this).withText("区块浏览器");
            shareAction.setPlatform(share_media);
            shareAction.setCallback(new UMShareListener() {
                @Override
                public void onStart(SHARE_MEDIA share_media) {
                }

                @Override
                public void onResult(SHARE_MEDIA share_media) {
                    if (AppContext.user.getId() > 0 && type == 1)
                        shareAward();
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

    }
}

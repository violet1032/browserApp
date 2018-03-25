package com.zp.browser.ui.dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.zp.browser.AppContext;
import com.zp.browser.R;
import com.zp.browser.ui.common.BaseActivity;
import com.zp.browser.utils.ImageCreateUtil;
import com.zp.browser.utils.ImageUtils;
import com.zp.browser.utils.StringUtils;
import com.zp.browser.utils.UIHelper;

import org.kymjs.kjframe.ui.BindView;

import java.io.File;


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

    private Handler handler;

    private String path;

    public static void startActivity(Context activity, int type) {
        Intent intent = new Intent();
        intent.setClass(activity, ShareDialog.class);
        intent.putExtra("type", type);
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
                }
                return false;
            }
        });

        type = getIntent().getIntExtra("type", 0);

        if (type == 0) {
            // 邀请好友
            ImageCreateUtil.createInviteImage(AppContext.user.getShareLink(),AppContext.user.getInvite_bg(), handler);
        }
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

}

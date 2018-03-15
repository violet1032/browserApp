package com.zp.browser.api;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.zp.browser.AppConfig;
import com.zp.browser.AppContext;
import com.zp.browser.R;
import com.zp.browser.utils.ImageUtils;
import com.zp.browser.utils.StringUtils;
import com.zp.browser.utils.UIHelper;

import org.kymjs.kjframe.bitmap.BitmapCallBack;

import java.io.File;

/**
 * <p/>
 * description:
 * <p/>
 * author:zipeng
 * <p/>
 * createTime:2015/9/14 21:00
 * <p/>
 * version:1.0
 */
public class ApiCommon {
    private static final String TAG = "ApiCommon";

    /**
     * @param url   图片地址
     * @param img   图片控件
     * @param thumb 是否获取缩略图
     */
    public static void getNetBitmap(String url, final ImageView img, boolean thumb) {
        // 色块
        final int i = img.getId() % 11;
        if (StringUtils.isEmpty(url)) {
            img.setImageResource(R.color.color_1 + i);
            return;
        }
        // 如果地址没有域名，则给地址加上域名
        url = StringUtils.getImgHttpUrl(url, thumb);
        Log.e(TAG, "url:::" + url);
        BitmapCallBack callBack = new BitmapCallBack() {
            @Override
            public void onPreLoad() {
                super.onPreLoad();
                img.setImageResource(R.color.color_1 + i);
            }

            @Override
            public void onFailure(Exception e) {
                super.onFailure(e);
                img.setImageResource(R.color.color_1 + i);
            }
        };
        AppContext.bitmap.display(img, url);
    }

    /**
     * 上传图片
     *
     * @param file         图片文件
     * @param RequestURL   上传地址
     * @param key          上传类型：头像：avatar、订单图片：orderpic
     * @param httpCallBack 上传后的回调函数
     */
    public static void uploadPicture(File file, String RequestURL, String key, FHttpCallBack
            httpCallBack) {
        Bitmap bitmap = ImageUtils.getCompressBitmap(file.getPath());
        File fileTemporary = new File(AppConfig.getSaveImagePath() + "pic.png");
        UIHelper.compressBmpToFile(bitmap, fileTemporary);
        FileImageUpload.uploadFile(fileTemporary, RequestURL, key, httpCallBack);
    }

    /**
     * 上传图片
     *
     * @param file         图片文件
     * @param httpCallBack 上传后的回调函数
     */
    public static void uploadPicture(File file, FHttpCallBack httpCallBack) {
        uploadPicture(file, URLs.IMG_UPLOAD, "avatar", httpCallBack);
    }


    /**
     * 传入地址获取图片名称
     *
     * @param url
     * @return
     */
    private static String getImgName(String url) {
        String imgName = null;
        if (!StringUtils.isEmpty(url)) {
            // 截取地址最后的图片名
            int start = 0;
            while (url.indexOf("/", start + 1) > 0) {
                start = url.indexOf("/", start + 1);
            }
            imgName = url.substring(start + 1);
        }
        return imgName;
    }
}

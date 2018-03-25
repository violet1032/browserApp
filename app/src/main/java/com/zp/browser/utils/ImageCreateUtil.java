package com.zp.browser.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;

import com.zp.browser.AppConfig;
import com.zp.browser.AppContext;

import org.kymjs.kjframe.http.HttpCallBack;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/24 0024.
 */
public class ImageCreateUtil {

    public static void createInviteImage(final String content,String imgUrl, final Handler handler) {
        final String filename = System.currentTimeMillis() + ".png";
        final String path = AppConfig.SAVE_IMAGE_PATH + filename;
        HttpCallBack httpCallBack = new HttpCallBack() {
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                super.onSuccess(headers, t);

                // 图片下载后，加二维码
                Bitmap qrcode = QRCodeUtil.generateBitmap(content, 230, 230);

                Bitmap bg = ImageUtils.getBitmapByPath(path).copy(Bitmap.Config.ARGB_8888, true);

                int heightBg = bg.getHeight();
                int widthBg = bg.getWidth();
                int heightQrcode = qrcode.getHeight();
                int widthQrcode = qrcode.getWidth();

                Paint paint = new Paint();
                paint.setAntiAlias(true);
                final int color = 0xff777777;
                paint.setColor(color);
                paint.setTextSize(30);

                Canvas canvas = new Canvas(bg);
                canvas.drawBitmap(qrcode, widthBg - widthQrcode - 20, heightBg - heightQrcode - 60, null);
                canvas.drawText("长按识别二维码",widthBg - widthQrcode,heightBg - 50,paint);

                try {
                    String fianlPath = AppConfig.SAVE_IMAGE_PATH + System.currentTimeMillis() + ".png";
                    ImageUtils.saveImage(AppContext.applicationContext, fianlPath, bg);

                    Message message = new Message();
                    message.what = 1;
                    message.obj = fianlPath;
                    handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        AppContext.bitmap.saveImage(AppContext.applicationContext, imgUrl, AppConfig.SAVE_IMAGE_PATH + filename, true, httpCallBack);
    }
}

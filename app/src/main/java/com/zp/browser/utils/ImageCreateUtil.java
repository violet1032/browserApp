package com.zp.browser.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.widget.TextView;

import com.zp.browser.AppConfig;
import com.zp.browser.AppContext;
import com.zp.browser.R;
import com.zp.browser.bean.News;

import org.kymjs.kjframe.http.HttpCallBack;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/24 0024.
 */
public class ImageCreateUtil {

    public static void createInviteImage(final String content, String imgUrl, final Handler handler) {
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
                canvas.drawText("长按识别二维码", widthBg - widthQrcode, heightBg - 50, paint);

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

    public static void createShareImage(final String qrCodeUrl, String imgUrl, final Handler handler, final News news, final Activity context, final TextView textView) {
        final String filename = System.currentTimeMillis() + ".png";
        final String path = AppConfig.SAVE_IMAGE_PATH + filename;
        HttpCallBack httpCallBack = new HttpCallBack() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                super.onSuccess(headers, t);
                // 创建一个白色底的bitmap
                Bitmap backgroud = ImageUtils.drawableToBitmap(context.getResources().getDrawable(R.drawable.bg_box_white, null), 1f);
                Bitmap bg = ImageUtils.getBitmapByPath(path).copy(Bitmap.Config.ARGB_8888, true);

                // 获取新闻内容图片
                Bitmap content = ImageUtils.getViewBitmap(textView);
                Matrix matrix = new Matrix();
                matrix.postScale(0.65f, 0.65f);
                // 得到新的图片
                Bitmap contentnew = Bitmap.createBitmap(content, 0, 0, content.getWidth(), content.getHeight(), matrix, true);

                // 计算整体高度 和宽度
                int totalWidth = bg.getWidth();
                int totalHeight = 500 + contentnew.getHeight();


                float scaleX = (float) totalWidth / 100f;
                float scaleY = (float) totalHeight / 100f;
//                float scaleX = (float) totalWidth / (float) backgroud.getWidth();
//                float scaleY = (float) totalHeight / (float) backgroud.getHeight();
                LogUtil.logError(ImageCreateUtil.class, "totalWidth:" + totalWidth);
                LogUtil.logError(ImageCreateUtil.class, "totalHeight:" + totalHeight);
                LogUtil.logError(ImageCreateUtil.class, "backgroud.getWidth():" + backgroud.getWidth());
                LogUtil.logError(ImageCreateUtil.class, "backgroud.getHeight():" + backgroud.getHeight());

                LogUtil.logError(ImageCreateUtil.class, "scaleX:" + scaleX);
                LogUtil.logError(ImageCreateUtil.class, "scaleY:" + scaleY);

                // 缩放背景
                matrix.postScale(scaleX, scaleY);
                // 得到新的图片
                Bitmap backgroudNew = Bitmap.createBitmap(backgroud, 0, 0, backgroud.getWidth(), backgroud.getHeight(), matrix, true);

                // 图片下载后，加二维码
                Bitmap qrcode = QRCodeUtil.generateBitmap(qrCodeUrl, 230, 230);

                Paint paint1 = new Paint();
                paint1.setAntiAlias(true);
                final int color = 0xffaaaaaa;
                paint1.setColor(color);
                paint1.setTextSize(24);

                TextPaint paintContent = new TextPaint();
                paintContent.setAntiAlias(true);
                final int color2 = 0xff222222;
                paintContent.setColor(color2);
                paintContent.setTextSize(30);

                // 开始绘制
                Canvas canvas = new Canvas(backgroudNew);

                // 绘制图片背景
                canvas.drawBitmap(bg, 0, 0, null);

                // 添加时间
                Bitmap clock = ImageUtils.drawableToBitmap(context.getResources().getDrawable(R.drawable.icon_time, null), 0.6f);
                canvas.drawBitmap(clock, 50, 300, null);
                canvas.drawText(news.getDateline(), 85, 320, paint1);

                // 添加新闻内容
                canvas.drawBitmap(contentnew, 50, 340, null);

                // 二维码
                canvas.drawBitmap(qrcode, 50, 340 + contentnew.getHeight() + 20, null);

                // 右边布局
                canvas.drawText("扫码领取", 50 + qrcode.getWidth() + 10, 340 + contentnew.getHeight() + 40, paintContent);

                try {
                    String fianlPath = AppConfig.SAVE_IMAGE_PATH + System.currentTimeMillis() + ".png";
                    ImageUtils.saveImage(AppContext.applicationContext, fianlPath, backgroudNew);

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

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

    public static void createInviteImage(final String content, String imgUrl, final Handler handler, final String registerAward) {
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

                TextPaint paintContent = new TextPaint();
                paintContent.setAntiAlias(true);
                final int color2 = 0xffff2093;
                paintContent.setColor(color2);
                paintContent.setTextSize(240);

                canvas.drawText(registerAward, 160, 780, paintContent);

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

    public static void createShareImage(final String qrCodeUrl, String imgUrl, final Handler handler,
                                        final News news, final Activity context, final TextView textView, final String registerAward) {
        final String filename = System.currentTimeMillis() + ".png";
        final String path = AppConfig.SAVE_IMAGE_PATH + filename;
        HttpCallBack httpCallBack = new HttpCallBack() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                super.onSuccess(headers, t);

                try {
//                    Drawable drawable = AppContext.appContext.getResources().getDrawable(R.drawable.bg_box_white, null);
//                    // 创建一个白色底的bitmap
//                    Bitmap backgroud = Bitmap.createBitmap(100, 100, drawable
//                            .getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
//                            : Bitmap.Config.RGB_565);
//                    Canvas canvasBG = new Canvas(backgroud);
//                    drawable.setBounds(0, 0, 100, 100);
//                    drawable.draw(canvasBG);

                    Bitmap backgroud = ImageUtils.drawableToBitmap(AppContext.appContext.getResources().getDrawable(R.drawable.bg_box_white, null), 1f);
                    Bitmap bg = ImageUtils.getBitmapByPath(path).copy(Bitmap.Config.ARGB_8888, true);


                    // 重新设定文本宽度


                    // 获取新闻内容图片
                    Bitmap content = ImageUtils.getViewBitmap(textView);
                    float f =  (float)bg.getWidth() * 7f / ((float)content.getWidth() * 9f);
                    Matrix matrix2 = new Matrix();
                    matrix2.postScale(f, f);
                    // 得到新的图片
                    Bitmap contentnew = Bitmap.createBitmap(content, 0, 0, content.getWidth(), content.getHeight(), matrix2, true);


                    // 计算整体高度 和宽度
                    int totalWidth = bg.getWidth();
                    int totalHeight = 550 + contentnew.getHeight();

                    float scaleX = (float) totalWidth / (float)backgroud.getWidth();
                    float scaleY = (float) totalHeight / (float)backgroud.getHeight();

                    // 缩放背景
                    Matrix matrix = new Matrix();
                    matrix.postScale(scaleX, scaleY);
                    // 得到新的图片
                    Bitmap backgroudNew = Bitmap.createBitmap(backgroud, 0, 0, backgroud.getWidth(), backgroud.getHeight(), matrix, true);


                    // 图片下载后，加二维码
                    Bitmap qrcode = QRCodeUtil.generateBitmap(qrCodeUrl, 180, 180);

                    Paint paint1 = new Paint();
                    paint1.setAntiAlias(true);
                    final int color = 0xffaaaaaa;
                    paint1.setColor(color);
                    paint1.setTextSize(24);

                    TextPaint paintContent = new TextPaint();
                    paintContent.setAntiAlias(true);
                    final int color2 = 0xff444444;
                    paintContent.setColor(color2);
                    paintContent.setTextSize(25);

                    // 开始绘制
                    Canvas canvas = new Canvas(backgroudNew);

                    // 绘制图片背景
                    canvas.drawBitmap(bg, 0, 0, null);


//                    canvas.drawText("density:" + AppContext.appContext.getResources().getDisplayMetrics().density, 0, 20, paint1);
//                    canvas.drawText("totalHeight:" + totalHeight, 0, 40, paint1);
//                    canvas.drawText("contentnew.getWidth:"+contentnew.getWidth(), 0, 60, paint1);
//                    canvas.drawText("contentnew.getHeight:"+contentnew.getHeight(), 0, 80, paint1);
//                    canvas.drawText("backgroud.getWidth:"+backgroud.getWidth(), 0, 100, paint1);
//                    canvas.drawText("backgroud.getHeight:"+backgroud.getHeight(), 0, 120, paint1);
//                    canvas.drawText("canvas.getHeight:"+canvas.getHeight(), 0, 140, paint1);
//                    canvas.drawText("canvas.getWidth:"+canvas.getWidth(), 0, 160, paint1);
//                    canvas.drawText("bg.getHeight:"+bg.getHeight(), 0, 180, paint1);
//                    canvas.drawText("bg.getWidth:"+bg.getWidth(), 0, 200, paint1);
//                    canvas.drawText("scaleX:"+scaleX, 0, 220, paint1);
//                    canvas.drawText("scaleY:"+scaleY, 0, 240, paint1);


                    // 添加时间
                    Bitmap clock = ImageUtils.drawableToBitmap(AppContext.appContext.getResources().getDrawable(R.drawable.icon_time, null), 0.6f);
                    canvas.drawBitmap(clock, 50, 300, null);
                    canvas.drawText(news.getDateline(), 85, 320, paint1);

                    // 添加新闻内容
                    canvas.drawBitmap(contentnew, 50, 340, null);

                    // 二维码
                    canvas.drawBitmap(qrcode, 30, 340 + contentnew.getHeight(), null);


                    // 右边布局
                    if (registerAward.compareTo("0") > 0) {
                        canvas.drawText("扫码领取", 30 + qrcode.getWidth(), 340 + contentnew.getHeight() + 60, paintContent);

                        TextPaint paint3 = new TextPaint();
                        paint3.setAntiAlias(true);
                        final int color3 = 0xffea8131;
                        paint3.setColor(color3);
                        paint3.setTextSize(50);
                        canvas.drawText(registerAward, 30 + qrcode.getWidth(), 340 + contentnew.getHeight() + 120, paint3);

                        TextPaint paint4 = new TextPaint();
                        paint4.setAntiAlias(true);
                        final int color4 = 0xff555555;
                        paint4.setColor(color4);
                        paint4.setTextSize(32);
                        canvas.drawText(AppContext.short_name+"糖果", 30 + qrcode.getWidth() + 120, 340 + contentnew.getHeight() + 120, paint4);

                        TextPaint paint5 = new TextPaint();
                        paint5.setAntiAlias(true);
                        final int color5 = 0xffaaaaaa;
                        paint5.setColor(color5);
                        paint5.setTextSize(20);
                        canvas.drawText("长按识别二维码", 30 + qrcode.getWidth(), 340 + contentnew.getHeight() + 150, paint5);

                        Bitmap candy = ImageUtils.drawableToBitmap(AppContext.appContext.getResources().getDrawable(R.drawable.candy, null), 0.5f);
                        canvas.drawBitmap(candy, 30 + qrcode.getWidth() +120+ getTextWidth(paint4,AppContext.short_name+"糖果"), 340 + contentnew.getHeight() + 90, null);
                    }


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
                }catch (Exception e){

                }
            }
        };
        AppContext.bitmap.saveImage(AppContext.applicationContext, imgUrl, AppConfig.SAVE_IMAGE_PATH + filename, true, httpCallBack);
    }

    public static int getTextWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }
        return iRet;
    }
}

package com.zp.browser.receiver;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.zp.browser.AppContext;
import com.zp.browser.R;
import com.zp.browser.ui.MainActivity;
import com.zp.browser.utils.LogUtil;
import com.zp.browser.utils.StringUtils;

import cn.jpush.android.api.JPushInterface;

/**
 * <p/>
 * 描述:
 * <p/>
 * 作者:Administrator
 * <p/>
 * 时间:2018/5/4 9:50
 * <p/>
 * 版本:
 */
public class MsgReceiver extends BroadcastReceiver {
    @SuppressLint("NewApi")
    @Override
    public void onReceive(Context context2, Intent intent) {
        LogUtil.logError(MsgReceiver.class, "进入MsgReceiver广播");

        NotificationManager nm = (NotificationManager) AppContext.appContext.getSystemService(Context.NOTIFICATION_SERVICE);

        Bundle bundle = intent.getExtras();

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            String title = bundle.getString(JPushInterface.EXTRA_TITLE);
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            if (StringUtils.isEmpty(title)) {
                title = AppContext.appContext.getResources().getString(R.string.app_name_cn);
            }
            Intent intent2 = new Intent(AppContext.appContext, MainActivity.class);
            PendingIntent pendingIntents = PendingIntent.getActivity(AppContext.appContext, (int) (Math.random() * 100), intent2, PendingIntent.FLAG_ONE_SHOT);

//            Notification.Builder builder;
//            if (Build.VERSION.SDK_INT >= 26) {
//                NotificationChannel channel = new NotificationChannel("browser_1",
//                        "Channel1", NotificationManager.IMPORTANCE_DEFAULT);
//                channel.enableLights(true); //是否在桌面icon右上角展示小红点
//                channel.setLightColor(Color.RED); //小红点颜色
//                channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
//                nm.createNotificationChannel(channel);
//
//                builder = new Notification.Builder(AppContext.appContext, "browser_1");
//            } else {
//                builder = new Notification.Builder(AppContext.appContext);
//            }
//
//            LogUtil.logError(MsgReceiver.class,"message:"+message);
//
//            Notification n = builder
//                    .setAutoCancel(true)
//                    .setContentTitle(title)
//                    .setContentText(message)
//                    .setContentIntent(pendingIntents)
//                    .setSmallIcon(R.drawable.logo)
//                    .setTicker(message)
//                    .setDefaults(Notification.DEFAULT_SOUND)
//                    .setWhen(System.currentTimeMillis())
//                    .setStyle(new Notification.BigTextStyle())
//                    .build();
//            nm.notify((int) System.currentTimeMillis(), n);





            Notification.Builder builder;
            if (Build.VERSION.SDK_INT >= 26) {
                NotificationChannel notificationChannel = nm.getNotificationChannel("browser_1");
                if (notificationChannel == null) {
                    NotificationChannel channel = new NotificationChannel("browser_1",
                            "Channel1", NotificationManager.IMPORTANCE_DEFAULT);
                    channel.enableLights(true); //是否在桌面icon右上角展示小红点
                    channel.setLightColor(Color.RED); //小红点颜色
                    channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
                    nm.createNotificationChannel(channel);
                }
                builder = new Notification.Builder(AppContext.appContext,"browser_1");
            } else {
                builder = new Notification.Builder(AppContext.appContext);
            }

            Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle();
            bigTextStyle.setBigContentTitle(title);
            bigTextStyle.bigText(message);

            Notification n = builder
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentIntent(pendingIntents)
                    .setSmallIcon(R.drawable.logo)
                    .setTicker(message)
                    .setWhen(System.currentTimeMillis())
                    .setStyle(bigTextStyle)
                    .build();
            nm.notify((int) System.currentTimeMillis(), n);
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            LogUtil.logError(MsgReceiver.class, "接受到推送下来的通知");
            String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
            LogUtil.logError(MsgReceiver.class, "title:" + title);
            String message = bundle.getString(JPushInterface.EXTRA_ALERT);
            LogUtil.logError(MsgReceiver.class, "message:" + message);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            LogUtil.logError(MsgReceiver.class, "extras:" + extras);
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            LogUtil.logError(MsgReceiver.class, "notifactionId:" + notifactionId);

            if (StringUtils.isEmpty(title)) {
                title = AppContext.appContext.getResources().getString(R.string.app_name_cn);
            }
            LogUtil.logError(MsgReceiver.class, "title:" + title);

            Notification.Builder builder = new Notification.Builder(AppContext.appContext);
            builder.setStyle(new Notification.BigTextStyle());
            builder.setAutoCancel(false);
            builder.setContentTitle(title);
            builder.setContentText(message);
            builder.setTicker(message);
            builder.setSmallIcon(R.mipmap.ic_launcher);

            Intent intent2 = new Intent(AppContext.appContext, MainActivity.class);
            PendingIntent pendingIntents = PendingIntent.getActivity(AppContext.appContext, (int) (Math.random() * 100), intent2, PendingIntent.FLAG_ONE_SHOT);
            builder.setContentIntent(pendingIntents);


            Notification notification = builder.build();
            nm.notify(notifactionId, notification);
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {

        } else {
        }
    }
}

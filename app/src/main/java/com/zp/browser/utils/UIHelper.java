package com.zp.browser.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.zp.browser.AppContext;
import com.zp.browser.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * 界面ui控件等工具类
 * <p/>
 * description:
 * <p/>
 * author:zipeng
 * <p/>
 * createTime:2015/9/14 20:18
 * <p/>
 * version:1.0
 */
public class UIHelper {
    private static AlertDialog dlg;

    /**
     * 全局web样式
     */
    // 链接样式文件，代码块高亮的处理
    public final static String linkCss = "<script type=\"text/javascript\" src=\"file:///android_asset/shCore.js\"></script>"
            + "<script type=\"text/javascript\" src=\"file:///android_asset/brush.js\"></script>"
            + "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/shThemeDefault.css\">"
            + "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/shCore.css\">"
            + "<script type=\"text/javascript\">SyntaxHighlighter.all();</script>";
    public final static String WEB_STYLE = "<style>* {color:#aaa} </style>";


    /**
     * 获取屏幕尺寸
     */
    public static int getDisplayWidth() {
        DisplayMetrics metrics = AppContext.applicationContext.getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        return screenWidth;
    }

    public static int getDisplayHeight() {
        DisplayMetrics metrics = AppContext.applicationContext.getResources().getDisplayMetrics();
        int screenHeight = metrics.heightPixels;
        return screenHeight;

    }

    /**
     * 获得状态栏的高度（单位为px）
     *
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context) {

        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    /**
     * 获取当前屏幕截图，包含状态栏
     *
     * @param activity
     * @return
     */
    public static Bitmap snapShotWithStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        int width = getDisplayWidth();
        int height = getDisplayHeight();
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
        view.destroyDrawingCache();
        return bp;

    }


    /**
     * 获取当前屏幕截图，不包含状态栏
     *
     * @param activity
     * @return
     */
    public static Bitmap snapShotWithoutStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        int width = getDisplayWidth();
        int height = getDisplayHeight();
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return bp;

    }


    /**
     * 显示加载等待界面
     *
     * @param context
     */
    public static void showLoadingDialog(Context context) {
        if (dlg == null) {
            dlg = new AlertDialog.Builder(context, R.style.CustomDialog).create();
            dlg.show();
            dlg.setCancelable(false);
            Window window = dlg.getWindow();
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.dialog_loading, null);
            window.setContentView(view);
        } else {
            if (!dlg.isShowing()) {
                dlg = new AlertDialog.Builder(context, R.style.CustomDialog).create();
                dlg.show();
                dlg.setCancelable(false);
                Window window = dlg.getWindow();
                LayoutInflater inflater = LayoutInflater.from(context);
                View view = inflater.inflate(R.layout.dialog_loading, null);
                window.setContentView(view);
            }
        }
    }

    /**
     * 取消加载界面
     */
    public static void stopLoadingDialog() {
        if (dlg != null) {
            dlg.dismiss();
        }
    }

    /**
     * 编辑栏错误提示
     *
     * @param str
     * @return
     * @Description
     * @author zipeng
     */
    public static CharSequence edtError(String str) {
        return Html.fromHtml("<font color=#ff0000>" + str + "</font>");
    }

    /**
     * 编辑栏错误提示
     *
     * @param editText
     * @param str
     */
    public static void edtError(EditText editText, String str) {
        if (editText != null)
            editText.setError(edtError(str));
    }

//    /**
//     * 发送App异常崩溃报告
//     *
//     * @param cont
//     * @param crashReport
//     */
//    public static void sendAppCrashReport(final Context cont,
//                                          final String crashReport) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(cont);
//        builder.setIcon(android.R.drawable.ic_dialog_info);
//        builder.setTitle(R.string.app_error);
//        builder.setMessage(R.string.app_error_message);
//        builder.setPositiveButton(R.string.submit_report,
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        // 发送异常报告
//                        Intent i = new Intent(Intent.ACTION_SEND);
//                        // i.setType("text/plain"); //模拟器
//                        i.setType("message/rfc822"); // 真机
//                        i.putExtra(Intent.EXTRA_EMAIL,
//                                new String[]{"zhangdeyi@oschina.net"});
//                        i.putExtra(Intent.EXTRA_SUBJECT, "客户端 - 错误报告");
//                        i.putExtra(Intent.EXTRA_TEXT, crashReport);
//                        cont.startActivity(Intent.createChooser(i, "发送错误报告"));
//                        // 退出
//                        AppManager.getAppManager().AppExit(cont);
//                    }
//                });
//        builder.setNegativeButton(R.string.sure,
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        // 退出
//                        AppManager.getAppManager().AppExit(cont);
//                    }
//                });
//        builder.show();
//    }

    /**
     * 弹出Toast消息
     *
     * @param msg
     */
    public static void ToastMessage(String msg) {
        Toast toast = Toast.makeText(AppContext.applicationContext, msg,
                Toast.LENGTH_SHORT);
        //可以控制toast显示的位置
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * 显示错误页面
     *
     * @param viewGroup
     * @param activity
     */
    public static void showErrorLayout(ViewGroup viewGroup,
                                       final Activity activity) {
        LayoutInflater inflater = LayoutInflater.from(activity
                .getApplicationContext());
        View v = inflater.inflate(R.layout.error_hint, null);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout
                .LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        // 隐藏所有子控件
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            viewGroup.getChildAt(i).setVisibility(View.GONE);
        }

        viewGroup.addView(v, layoutParams);

        LinearLayout back = (LinearLayout) v
                .findViewById(R.id.error_hint_lay_back);
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });

    }

    /**
     * 将dip单位的数值转化为px单位的值
     *
     * @param dpValue
     * @return
     * @Description
     * @author zipeng
     */
    public static int dip2px(float dpValue) {
        final float scale = AppContext.appContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);

    }

    /**
     * 将px单位的数值转化为dip单位的值
     *
     * @param dpValue
     * @return
     * @Description
     * @author zipeng
     */
    public static int px2dip(float dpValue) {
        final float scale = AppContext.appContext.getResources().getDisplayMetrics().density;
        return (int) ((dpValue) / scale);

    }

    /**
     * sp转px
     *
     * @param spValue
     * @return
     */
    public static int sp2px(float spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spValue, AppContext.appContext.getResources().getDisplayMetrics());
    }

    /**
     * px转sp
     *
     * @param pxVal
     * @return
     */
    public static float px2sp(float pxVal) {
        return (pxVal / AppContext.appContext.getResources().getDisplayMetrics().scaledDensity);
    }


    /**
     * 获取宽度
     *
     * @param view
     * @return
     */
    public static int getViewWidth(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return view.getMeasuredWidth();
    }

    /**
     * 获取高度
     *
     * @param view
     * @return
     */
    public static int getViewHeight(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return view.getMeasuredHeight();
    }

    /**
     * @param bmp
     * @param file
     * @Description 将图片保存到本地时进行压缩, 即将图片从Bitmap形式变为File形式时进行压缩
     * @author Administrator
     */
    public static void compressBmpToFile(Bitmap bmp, File file) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        while ((float) baos.toByteArray().length / 1024 > (float) 120) {
            baos.reset();
            options -= 10;
            bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 隐藏软键盘
     *
     * @param activity
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static void setListViewHeightHaveSpecialetMsgEditT(ListView listView,ScrollView scrollView) {
        if (listView == null) return;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {// pre-condition
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            // totalHeight += listItem.getMeasuredHeight() + 0;
            totalHeight += listItem.getMeasuredHeight() + 10;
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);

        listView.setLayoutParams(params);
    }


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        //获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { //listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); //计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); //统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        //listView.getDividerHeight()获取子项间分隔符占用的高度
        //params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }


}

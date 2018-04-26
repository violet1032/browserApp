package com.zp.browser.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.zp.browser.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 定制对话框
 * <p/>
 * CreateTime 2015/04/09
 *
 * @author QuZipeng
 * @version 1.0
 */
public class CustomDialog extends AlertDialog {
    static View layout;

    public CustomDialog(Context context, int theme) {
        super (context, theme);
    }

    public CustomDialog(Context context) {
        super(context);
    }

    public static class Builder {

        private Context context;
        private String title;
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;
//        private EditText edit;//判断输入框是否显示
//        private String edit_p;
        private boolean touch = false;
        private boolean showSuccess = false;
        private boolean showNotice = false;
        private int backTime = 0;
        private int msgTextColor = R.color.black_3;

        public int getMsgTextColor() {
            return msgTextColor;
        }

        public Builder setMsgTextColor(int msgTextColor) {
            this.msgTextColor = msgTextColor;
            return this;
        }
        public Builder setOnTouchOutside(Boolean f)
        {
            touch=f;
            return this;
        }
        public Builder setShowSuccess(boolean b) {
            showSuccess = b;
            return this;
        }

        public Builder setShowNotice(boolean b) {
            showNotice = b;
            return this;
        }

        public Builder setBackTime(int time) {
            backTime = time;
            return this;
        }

        private OnClickListener positiveButtonClickListener,
                negativeButtonClickListener;

        private onTimeoutListener onTimeoutListener;



        public interface onTimeoutListener {
            void onTimeOut(CustomDialog dialog);
        }

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setMessage(int message) {
            this.message = (String) context.getText (message);
            return this;
        }

        public Builder setTitle(int title) {
            this.title = (String) context.getText (title);
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        public Builder setPositiveButton(int positiveButtonText,
                                         OnClickListener listener) {
            this.positiveButtonText = (String) context
                    .getText (positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText,
                                         OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonText,
                                         OnClickListener listener) {
            this.negativeButtonText = (String) context
                    .getText (negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText,
                                         OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

//        public Builder setEditText(EditText edit,String edit_p){
//            this.edit=edit;
//            this.edit_p=edit_p;
//            return this;
//        }

        public Builder setOnTimeOutListener(
                onTimeoutListener listener) {
            this.onTimeoutListener = listener;
            return this;
        }

        /**
         * 创建dialog
         */
        public CustomDialog create() {

            LayoutInflater inflater = LayoutInflater.from(context);
            // 设置弹框风格
            final CustomDialog dialog = new CustomDialog(context,
                    R.style.CustomDialog);
            layout = inflater.inflate (R.layout.custom_dialog, null);

            if (showSuccess) {
                Drawable drawable = context.getResources ().getDrawable (R.drawable.ic_success_gray);
                ((TextView) layout.findViewById (R.id.title)).setCompoundDrawablesWithIntrinsicBounds (null, drawable, null, null);
            }

            if (showNotice) {
                Drawable drawable = context.getResources ().getDrawable (R.drawable.ic_notice_gray);
                ((TextView) layout.findViewById (R.id.title)).setCompoundDrawablesWithIntrinsicBounds (null, drawable, null, null);
            }

            // 设置标题
            ((TextView) layout.findViewById (R.id.title)).setText (title);
            //设置点击屏幕Dialog不消失
            if(touch){
                dialog.setCanceledOnTouchOutside(false);
            }
//            //设置输入框是否可见
//            if(edit_p != null){
//                edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                    @Override
//                    public void onFocusChange(View v, boolean hasFocus) {
//                        if (hasFocus) {
//                            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//                        }
//                    }
//                });
//                layout.findViewById (R.id.edit_moblie).setFocusable(true);
//                ((TextView) layout.findViewById(R.id.title)).setTextSize(20);
//                dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
//            }
//            else{
//                layout.findViewById (R.id.edit_moblie).setVisibility (
//                        View.GONE);
//            }
            // 设置确认按钮是否可见
            if (positiveButtonText != null) {
                ((Button) layout.findViewById (R.id.positiveButton))
                        .setText (positiveButtonText);
                if (positiveButtonClickListener != null) {
                    ((Button) layout.findViewById (R.id.positiveButton))
                            .setOnClickListener (new View.OnClickListener () {
                                public void onClick(View v) {
                                    positiveButtonClickListener.onClick (dialog,
                                            DialogInterface.BUTTON_POSITIVE);
                                }
                            });
                }
            } else {
                layout.findViewById (R.id.positiveButton).setVisibility (
                        View.GONE);
                layout.findViewById (R.id.custom_dialog_img_1).setVisibility (
                        View.GONE);
                ((Button) layout.findViewById (R.id.negativeButton))
                        .setBackgroundResource (R.drawable.click_dialog_single);
            }
            // 设置取消按钮是否可见
            if (negativeButtonText != null) {
                ((Button) layout.findViewById (R.id.negativeButton))
                        .setText (negativeButtonText);
                if (negativeButtonClickListener != null) {
                    ((Button) layout.findViewById (R.id.negativeButton))
                            .setOnClickListener (new View.OnClickListener () {
                                public void onClick(View v) {
                                    negativeButtonClickListener.onClick (dialog,
                                            DialogInterface.BUTTON_NEGATIVE);
                                }
                            });
                }
            } else {
                layout.findViewById (R.id.negativeButton).setVisibility (
                        View.GONE);
                layout.findViewById (R.id.custom_dialog_img_1).setVisibility (
                        View.GONE);
                ((Button) layout.findViewById (R.id.positiveButton))
                        .setBackgroundResource (R.drawable.click_dialog_single);
            }
            // 设置内容是否可见
            if (message != null) {
                ((TextView) layout.findViewById (R.id.message)).setText (message);
                ((TextView) layout.findViewById (R.id.message)).setTextColor (context.getResources ().getColor (msgTextColor));
            } else if (contentView != null) {
                ((LinearLayout) layout.findViewById (R.id.content))
                        .removeAllViews ();
                ((LinearLayout) layout.findViewById (R.id.content)).addView (
                        contentView, new LayoutParams(
                                LayoutParams.WRAP_CONTENT,
                                LayoutParams.WRAP_CONTENT));
            }

            StringBuffer stringBuffer = new StringBuffer();
            // 显示返回倒计时
            if (backTime > 0) {
                stringBuffer.append (backTime);
                stringBuffer.append (context.getString (R.string.back_time));
                ((TextView) layout.findViewById (R.id.back_time)).setText (stringBuffer);
                timer = new Timer();
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        Message msg = new Message();
                        msg.what = 101;
                        msg.obj = dialog;
                        handler.sendMessage (msg);
                    }
                };
                timer.schedule (timerTask, 1000, 1000);
            } else {
                (layout.findViewById (R.id.back_time)).setVisibility (View.GONE);
            }
            return dialog;
        }

        private TimerTask timerTask;
        private Timer timer;

        Handler handler = new Handler(new Handler.Callback () {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case 101:
                        CustomDialog dialog = (CustomDialog) message.obj;
                        timeSet (dialog);
                        break;
                }
                return false;
            }
        });

        private void timeSet(CustomDialog dialog) {
            backTime--;
            if (backTime == 0) {
                onTimeoutListener.onTimeOut (dialog);
                timerTask.cancel ();
                timer.cancel ();
            } else {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append (backTime);
                stringBuffer.append (context.getString (R.string.back_time));
                ((TextView) layout.findViewById (R.id.back_time)).setText (stringBuffer);
            }
        }
    }

    @Override
    public void show() {
        super.show();
        setContentView(layout);
    }

}

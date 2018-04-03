package com.zp.browser.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by Administrator on 2018/4/3 0003.
 */
public class MyScrollView extends ScrollView {

    private boolean isFristBottom;
    private boolean isFristTop;

    private OnOverScrolledListener onOverScrolledListener;

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        if (scrollY == 0) {
            if (clampedY) {
                if (!isFristTop) {
                    isFristTop = true;
                    if (onOverScrolledListener != null)
                        onOverScrolledListener.scrollTop();
                }
            } else {
                isFristTop = false;
            }
        } else if (scrollY > 200) {
            if (clampedY) {
                if (!isFristBottom) {
                    isFristBottom = true;
                    if (onOverScrolledListener != null)
                        onOverScrolledListener.scrollBottom();
                }
            } else {
                isFristBottom = false;
            }
        }
    }

    public OnOverScrolledListener getOnOverScrolledListener() {
        return onOverScrolledListener;
    }

    public void setOnOverScrolledListener(OnOverScrolledListener onOverScrolledListener) {
        this.onOverScrolledListener = onOverScrolledListener;
    }
}

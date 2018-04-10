package com.zp.browser.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.zp.browser.utils.LogUtil;

/**
 * Created by Administrator on 2018/3/4 0004.
 */
public class PageListview extends ListView {

    private boolean isFristBottom;
    private boolean isFristTop;

    private OnOverScrolledListener onOverScrolledListener;

    public PageListview(Context context) {
        super(context);
    }

    public PageListview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PageListview(Context context, AttributeSet attrs,
                        int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        LogUtil.logError(PageListview.class,"onScrollChanged:"+t);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        LogUtil.logError(PageListview.class, "onOverScrolled:" + scrollY);
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

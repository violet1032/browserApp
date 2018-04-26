package com.zp.browser.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

import com.zp.browser.AppContext;


/**
 * 对话框中部的信息显示控件高度
 * <P>
 * CreateTime 2015/08/01
 * 
 * @author QuZipeng
 * @version 1.0.0
 * 
 */
public class DialogMessageScrollView extends ScrollView {
	private int maxHeight;

	public DialogMessageScrollView(Context context) {
		super(context);
		maxHeight = AppContext.screenHeight / 2;
	}

	public DialogMessageScrollView(Context context, AttributeSet attr) {
		super(context, attr);
		maxHeight = AppContext.screenHeight / 2;
	}

	public DialogMessageScrollView(Context context, AttributeSet attr,
								   int defStyle) {
		super(context, attr, defStyle);
		maxHeight = AppContext.screenHeight / 2;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int width = MeasureSpec.getSize(widthMeasureSpec);

		View child = getChildAt(0);
		int child_height = child.getHeight();

		if (child_height == 0)
			child_height = 1;
		else if (child_height > maxHeight * 2 / 3)
			child_height = maxHeight * 2 / 3;

		setMeasuredDimension(width, child_height);
	}
}

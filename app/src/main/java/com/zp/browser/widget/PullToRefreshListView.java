package com.zp.browser.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zp.browser.R;


/**
 * ListView下拉刷新和加载更多
 * <p/>
 * <p/>
 * <strong>变更说明:</strong>
 * <p/>
 * 默认如果设置了OnRefreshListener接口和OnLoadMoreListener接口
 * <p/>
 * 剩余三个Flag： <br>
 * mIsAutoLoadMore(是否自动加载更多) <br>
 * mIsMoveToFirstItemAfterRefresh(下拉刷新后是否显示第一条Item) <br>
 * mIsDoRefreshOnWindowFocused(当该ListView所在的控件显示到屏幕上时，是否直接显示正在刷新...)
 *
 * @author zipeng
 */
public class PullToRefreshListView extends ListView implements OnScrollListener {
    private final String tag = "PullToRefreshListView";

    private boolean canScroll = true; // 是否可以滑动

    /**
     * 实际的padding的距离与界面上偏移距离的比例
     */
    private final static int RATIO = 3;

    // ===========================以下4个常量为
    // 下拉刷新的状态标识===============================
    /**
     * 松开刷新
     */
    public final static int RELEASE_TO_REFRESH = 0;
    /**
     * 下拉刷新
     */
    public final static int PULL_TO_REFRESH = 1;
    /**
     * 正在刷新
     */
    public final static int REFRESHING = 2;
    /**
     * 刷新完成 or 什么都没做，恢复原状态。
     */
    public final static int DONE = 3;
    /**
     * 完成刷新时状态，0：什么都没做，1：刷新成功，2：刷新失败
     */
    public int done_state = 0;

    // ===========================以下3个常量为
    // 加载更多的状态标识===============================
    /**
     * 加载中
     */
    private final static int ENDINT_LOADING = 1;
    /**
     * 手动完成刷新
     */
    private final static int ENDINT_MANUAL_LOAD_DONE = 2;
    /**
     * 自动完成刷新
     */
    private final static int ENDINT_AUTO_LOAD_DONE = 3;

    /**
     * 下拉刷新头部动画相关
     */
    private int height = 0;
    private boolean startHeaderViewAnim = false; // 是否实行头部动画，用于刷新后缓慢消失
    Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case 11:
                    height += 20;
                    if (height >= mHeadViewHeight) {
                        height = mHeadViewHeight;
                        // timer.cancel();
                        startHeaderViewAnim = false;
                    }
                    mHeadRootView.setPadding(0, -1 * height, 0, 0);
                    break;
                case 13:
                    if (height != 0)
                        height -= 20;
                    if (height < 0) {
                        height = 0;
                        // 开始刷新
                        mHeadState = REFRESHING;
                        changeHeadViewByState();
                        onRefresh();
                        mIsRecored = false;
                        mIsBack = false;
                        break;
                    }
                    mHeadRootView.setPadding(0, -1 * height, 0, 0);
                    break;
            }

            return false;
        }
    });
    ;

    /**
     * <strong>下拉刷新HeadView的实时状态flag</strong>
     * <p/>
     * <p/>
     * 0 : RELEASE_TO_REFRESH;
     * <p/>
     * 1 : PULL_To_REFRESH;
     * <p/>
     * 2 : REFRESHING;
     * <p/>
     * 3 : DONE;
     */
    public static int mHeadState;

    /**
     * <strong>加载更多FootView（EndView）的实时状态flag</strong>
     * <p/>
     * <p/>
     * 0 : 完成/等待刷新 ;
     * <p/>
     * 1 : 加载中
     */
    public static int mEndState;

    // ================================= 功能设置Flag
    // ================================

    /**
     * 可以加载更多否
     */
    private boolean mCanLoadMore = false;

    /**
     * 可以下拉刷新否
     */
    private boolean mCanRefresh = false;

    /**
     * 可以自动加载更多吗？（注意，先判断是否有加载更多，如果没有，这个flag也没有意义）
     */
    private boolean mIsAutoLoadMore = true;

    /**
     * 下拉刷新后是否显示第一条Item
     */
    private boolean mIsMoveToFirstItemAfterRefresh = false;

    /**
     * 当该ListView所在的控件显示到屏幕上时，是否直接显示正在刷新...
     */
    private boolean mIsDoRefreshOnUIChanged = false;

    public boolean isCanLoadMore() {
        return mCanLoadMore;
    }

    public void setCanLoadMore(boolean pCanLoadMore) {
        mCanLoadMore = pCanLoadMore;
        if (mCanLoadMore) {
            if (getFooterViewsCount() == 0)
                addFooterView();
            else
                setEndRootViewVisibility(true);
        } else {
            if (null != mEndRootView) {
                removeFooterView(mEndRootView);
            }
        }
    }

    public boolean isCanRefresh() {
        return mCanRefresh;
    }

    public void setCanRefresh(boolean pCanRefresh) {
        mCanRefresh = pCanRefresh;
    }

    public boolean isAutoLoadMore() {
        return mIsAutoLoadMore;
    }

    public void setAutoLoadMore(boolean pIsAutoLoadMore) {
        mIsAutoLoadMore = pIsAutoLoadMore;
    }

    public boolean isMoveToFirstItemAfterRefresh() {
        return mIsMoveToFirstItemAfterRefresh;
    }

    public void setMoveToFirstItemAfterRefresh(
            boolean pIsMoveToFirstItemAfterRefresh) {
        mIsMoveToFirstItemAfterRefresh = pIsMoveToFirstItemAfterRefresh;
    }

    public boolean isDoRefreshOnUIChanged() {
        return mIsDoRefreshOnUIChanged;
    }

    public void setDoRefreshOnUIChanged(boolean pIsDoRefreshOnWindowFocused) {
        mIsDoRefreshOnUIChanged = pIsDoRefreshOnWindowFocused;
    }

    // ============================================================================

    private LayoutInflater mInflater;

    // 头部
    private LinearLayout mHeadRootView;
    private TextView mTipsTextView;
    private ImageView mArrowImageView;
    private ProgressBar mProgressBar;
    private ImageView imgDone;

    // 脚部
    private View mEndRootView;
    private ProgressBar mEndLoadProgressBar;
    private TextView mEndLoadTipsTextView;

    // 没有数据提醒界面
    private View HasNODataHintView;

    /**
     * headView动画
     */
    private RotateAnimation mArrowAnim;
    /**
     * headView反转动画
     */
    private RotateAnimation mArrowReverseAnim;

    /**
     * 用于保证startY的值在一个完整的touch事件中只被记录一次
     */
    private boolean mIsRecored;

    private int mHeadViewHeight;

    private int mStartY;
    private boolean mIsBack;

    private int mFirstItemIndex;
    private int mLastItemIndex;
    private int mCount;

    @SuppressWarnings("unused")
    private boolean mEnoughCount;// 足够数量充满屏幕？

    private OnRefreshListener mRefreshListener;
    private OnLoadMoreListener mLoadMoreListener;

    private String mLabel;

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String pLabel) {
        mLabel = pLabel;
    }

    public PullToRefreshListView(Context pContext) {
        super(pContext);
        init(pContext);
    }

    public PullToRefreshListView(Context pContext, AttributeSet pAttrs) {
        super(pContext, pAttrs);
        init(pContext);
    }

    public PullToRefreshListView(Context pContext, AttributeSet pAttrs,
                                 int pDefStyle) {
        super(pContext, pAttrs, pDefStyle);
        init(pContext);
    }

    /**
     * 初始化操作
     *
     * @param pContext
     */
    private void init(Context pContext) {
        // final ViewConfiguration _ViewConfiguration =
        // ViewConfiguration.get(pContext);
        // mTouchSlop = _ViewConfiguration.getScaledTouchSlop();

        setCacheColorHint(pContext.getResources().getColor(R.color.transparent));
        setOnLongClickListener(null);
        mInflater = LayoutInflater.from(pContext);
        HasNODataHintView = mInflater.inflate(R.layout.list_hasnot_data, this,
                false);

        addHeadView();

        setOnScrollListener(this);

        initPullImageAnimation(0);
    }

    /**
     * 添加下拉刷新的HeadView
     */
    private void addHeadView() {
        mHeadRootView = (LinearLayout) mInflater.inflate(
                R.layout.pull_to_refresh_head, this, false);

        mArrowImageView = (ImageView) mHeadRootView
                .findViewById(R.id.head_arrowImageView);
        imgDone = (ImageView) mHeadRootView.findViewById(R.id.head_img_down);
        mArrowImageView.setMinimumWidth(70);
        mArrowImageView.setMinimumHeight(50);
        mProgressBar = (ProgressBar) mHeadRootView
                .findViewById(R.id.head_progressBar);
        mTipsTextView = (TextView) mHeadRootView
                .findViewById(R.id.head_tipsTextView);

        measureView(mHeadRootView);
        mHeadViewHeight = mHeadRootView.getMeasuredHeight();

        mHeadRootView.setPadding(0, -1 * mHeadViewHeight, 0, 0);
        mHeadRootView.invalidate();

        addHeaderView(mHeadRootView, null, false);

        mHeadState = DONE;
        done_state = 0;
        changeHeadViewByState();
    }

    /**
     * 添加加载更多FootView
     */
    private void addFooterView() {
        mEndRootView = mInflater.inflate(R.layout.load_more, this, false);
        mEndRootView.setVisibility(View.VISIBLE);
        mEndLoadProgressBar = (ProgressBar) mEndRootView
                .findViewById(R.id.pull_to_refresh_progress);
        mEndLoadTipsTextView = (TextView) mEndRootView
                .findViewById(R.id.load_more);
        mEndRootView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mCanLoadMore) {
                    if (mCanRefresh) {
                        if (mEndState != ENDINT_LOADING
                                && mHeadState != REFRESHING) {
                            mEndState = ENDINT_LOADING;
                            onLoadMore();
                        }
                    } else if (mEndState != ENDINT_LOADING) {
                        mEndState = ENDINT_LOADING;
                        onLoadMore();
                    }
                }
            }
        });

        addFooterView(mEndRootView);

        if (mIsAutoLoadMore) {
            mEndState = ENDINT_AUTO_LOAD_DONE;
        } else {
            mEndState = ENDINT_MANUAL_LOAD_DONE;
        }
    }

    /**
     * 实例化下拉刷新的箭头的动画效果
     */
    private void initPullImageAnimation(final int pAnimDuration) {

        int _Duration;

        if (pAnimDuration > 0) {
            _Duration = pAnimDuration;
        } else {
            _Duration = 250;
        }

        Interpolator _Interpolator = new LinearInterpolator();

        mArrowAnim = new RotateAnimation(0, -180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mArrowAnim.setInterpolator(_Interpolator);
        mArrowAnim.setDuration(_Duration);
        mArrowAnim.setFillAfter(true);

        mArrowReverseAnim = new RotateAnimation(-180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mArrowReverseAnim.setInterpolator(_Interpolator);
        mArrowReverseAnim.setDuration(_Duration);
        mArrowReverseAnim.setFillAfter(true);
    }

    /**
     * 测量HeadView宽高(此方法仅适用于LinearLayout)
     *
     * @param pChild
     */
    private void measureView(View pChild) {
        ViewGroup.LayoutParams p = pChild.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;

        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
                    MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED);
        }
        pChild.measure(childWidthSpec, childHeightSpec);
    }

    /**
     * 为了判断滑动到ListView底部没
     */
    @Override
    public void onScroll(AbsListView pView, int pFirstVisibleItem,
                         int pVisibleItemCount, int pTotalItemCount) {
        mFirstItemIndex = pFirstVisibleItem;
        mLastItemIndex = pFirstVisibleItem + pVisibleItemCount - 2;
        mCount = pTotalItemCount - 2;
        if (pTotalItemCount > pVisibleItemCount) {
            mEnoughCount = true;
        } else {
            mEnoughCount = false;
        }
    }

    /**
     * 加载判断。
     */
    @Override
    public void onScrollStateChanged(AbsListView pView, int pScrollState) {
        if (mCanLoadMore) {
            if (mLastItemIndex == mCount && pScrollState == SCROLL_STATE_IDLE) {
                if (mEndState != ENDINT_LOADING) {
                    if (mIsAutoLoadMore) {
                        if (mCanRefresh) {
                            if (mHeadState != REFRESHING) {
                                mEndState = ENDINT_LOADING;
                                onLoadMore();
                                changeEndViewByState();
                            }
                        } else {
                            mEndState = ENDINT_LOADING;
                            onLoadMore();
                            changeEndViewByState();
                        }
                    } else {
                        mEndState = ENDINT_MANUAL_LOAD_DONE;
                        changeEndViewByState();
                    }
                }
            }
        } else if (mEndRootView != null
                && mEndRootView.getVisibility() == VISIBLE) {
            mEndRootView.setVisibility(View.GONE);
            this.removeFooterView(mEndRootView);
        }
    }

    /**
     * 改变加载更多状态
     */
    private void changeEndViewByState() {
        if (mCanLoadMore) {
            switch (mEndState) {
                case ENDINT_LOADING: // 刷新中
                    if (mEndLoadTipsTextView.getText().equals(R.string.loading_eg)) {
                        break;
                    }
                    mEndLoadTipsTextView.setText(R.string.loading_eg);
                    mEndLoadTipsTextView.setVisibility(View.VISIBLE);
                    mEndLoadProgressBar.setVisibility(View.VISIBLE);
                    break;
                case ENDINT_MANUAL_LOAD_DONE:// 手动刷新完成
                    mEndLoadTipsTextView.setText(R.string.click_to_refresh);
                    mEndLoadTipsTextView.setVisibility(View.VISIBLE);
                    mEndLoadProgressBar.setVisibility(View.GONE);
                    mEndRootView.setVisibility(View.VISIBLE);
                    break;
                case ENDINT_AUTO_LOAD_DONE:// 自动刷新完成
                    switch (done_state) {
                        case 1:
                            mEndLoadTipsTextView.setText(R.string.refresh_succeed);
                            break;
                        case 2:
                            mEndLoadTipsTextView.setText(R.string.refresh_fail);
                            break;
                    }

                    mEndLoadTipsTextView.setVisibility(View.VISIBLE);
                    mEndLoadProgressBar.setVisibility(View.GONE);

                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            mEndRootView.setPadding(0, -mEndRootView.getHeight(),
                                    0, 0);
                        }
                    }, 1000);

                    mEndRootView.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 注意事项： 此方法不适用于ViewPager中， 方法为：直接调用pull2RefreshManually();
     */
    @Override
    public void onWindowFocusChanged(boolean pHasWindowFocus) {
        super.onWindowFocusChanged(pHasWindowFocus);
        if (mIsDoRefreshOnUIChanged) {
            if (pHasWindowFocus) {
                pull2RefreshManually();
            }
        }
    }

    /**
     * 当该ListView所在的控件显示到屏幕上时，直接显示正在刷新...
     */
    public void pull2RefreshManually() {
        mHeadState = REFRESHING;
        changeHeadViewByState();
        onRefresh();

        mIsRecored = false;
        mIsBack = false;
    }

    /**
     */
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        if (mCanRefresh) {
            if (mCanLoadMore && mEndState == ENDINT_LOADING) {
                return super.onTouchEvent(event);
            }

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:

                    if (mFirstItemIndex == 0 && !mIsRecored) {
                        mIsRecored = true;
                        mStartY = (int) event.getY();
                    } else if (mFirstItemIndex == 0 && mIsRecored) {
                        mStartY = (int) event.getY();
                    }

                    break;

                case MotionEvent.ACTION_UP:

                    if (mHeadState != REFRESHING) {

                        if (mHeadState == DONE) {
                            done_state = 0;
                        }
                        if (mHeadState == PULL_TO_REFRESH) {
                            mHeadState = DONE;
                            done_state = 0;
                            changeHeadViewByState();
                        }
                        if (mHeadState == RELEASE_TO_REFRESH) {
                            mHeadState = REFRESHING;
                            changeHeadViewByState();
                            onRefresh();
                        }
                    }

                    mIsRecored = false;
                    mIsBack = false;

                    break;

                case MotionEvent.ACTION_MOVE:

                    int _TempY = (int) event.getY();

                    if (!mIsRecored && mFirstItemIndex == 0) {
                        mIsRecored = true;
                        mStartY = _TempY;
                    }

                    if (mHeadState != REFRESHING && mIsRecored) {

                        if (mHeadState == RELEASE_TO_REFRESH) {

//                            setSelection (0);

                            if (((_TempY - mStartY) / RATIO < mHeadViewHeight)
                                    && (_TempY - mStartY) > 0) {
                                mHeadState = PULL_TO_REFRESH;
                                changeHeadViewByState();
                            } else if (_TempY - mStartY <= 0) {
                                mHeadState = DONE;
                                done_state = 0;
                                changeHeadViewByState();
                            }
                        }
                        if (mHeadState == PULL_TO_REFRESH) {

//                            setSelection (0);

                            if ((_TempY - mStartY) / RATIO >= mHeadViewHeight) {
                                mHeadState = RELEASE_TO_REFRESH;
                                mIsBack = true;
                                changeHeadViewByState();
                            } else if (_TempY - mStartY <= 0) {
                                mHeadState = DONE;
                                done_state = 0;
                                changeHeadViewByState();
                            }
                        }

                        if (mHeadState == DONE) {
                            if (_TempY - mStartY > 0) {
                                mHeadState = PULL_TO_REFRESH;
                                done_state = 0;
                                changeHeadViewByState();
                            }
                        }

                        if (mHeadState == PULL_TO_REFRESH) {
                            mHeadRootView.setPadding(0, -1 * mHeadViewHeight
                                    + (_TempY - mStartY) / RATIO, 0, 0);

                        }

                        if (mHeadState == RELEASE_TO_REFRESH) {
                            mHeadRootView.setPadding(0, (_TempY - mStartY) / RATIO
                                    - mHeadViewHeight, 0, 0);
                        }
                    }
                    break;
            }
        }

        return super.onTouchEvent(event);
    }

    /**
     * 当HeadView状态改变时候，调用该方法，以更新界面
     */
    private void changeHeadViewByState() {
        switch (mHeadState) {
            case RELEASE_TO_REFRESH:
                mArrowImageView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                imgDone.setVisibility(GONE);
                mTipsTextView.setVisibility(View.VISIBLE);
                // mLastUpdatedTextView.setVisibility(View.VISIBLE);

                mArrowImageView.clearAnimation();
                mArrowImageView.startAnimation(mArrowAnim);
                mTipsTextView.setText(R.string.release_to_refresh);

                break;
            case PULL_TO_REFRESH:
                mProgressBar.setVisibility(View.GONE);
                imgDone.setVisibility(GONE);
                mTipsTextView.setVisibility(View.VISIBLE);
                mArrowImageView.clearAnimation();
                mArrowImageView.setVisibility(View.VISIBLE);
                if (mIsBack) {
                    mIsBack = false;
                    mArrowImageView.clearAnimation();
                    mArrowImageView.startAnimation(mArrowReverseAnim);
                    mTipsTextView.setText(R.string.pull_to_refresh);
                } else {
                    mTipsTextView.setText(R.string.pull_to_refresh);
                }
                break;

            case REFRESHING:
                changeHeaderViewRefreshState();
                break;
            case DONE:

                switch (done_state) {
                    case 0:
                        mHeadRootView.setPadding(0, -1 * mHeadViewHeight, 0, 0);
                        mProgressBar.setVisibility(View.GONE);
                        imgDone.setVisibility(GONE);
                        mArrowImageView.clearAnimation();
                        mArrowImageView.setImageResource(R.drawable.pull_icon_big);
                        mTipsTextView.setText(R.string.pull_to_refresh);
                        break;
                    case 1:
                        mTipsTextView.setText(R.string.refresh_succeed);
                        mProgressBar.setVisibility(GONE);
                        imgDone.setVisibility(VISIBLE);
                        imgDone.setImageResource(R.drawable.refresh_succeed);

                        if (startHeaderViewAnim) {
                            handler.postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    height = 0;
                                    new Thread() {
                                        public void run() {
                                            for (int j = 0; j < 10; j++) {
                                                handler.sendEmptyMessage(11);
                                                try {
                                                    sleep(20);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }.start();
                                }
                            }, 1000);
                        }

                        break;
                    case 2:
                        mTipsTextView.setText(R.string.refresh_fail);
                        mProgressBar.setVisibility(GONE);
                        imgDone.setVisibility(VISIBLE);
                        imgDone.setImageResource(R.drawable.refresh_fail);
                        if (startHeaderViewAnim) {
                            handler.postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    height = 0;
                                    new Thread() {
                                        public void run() {
                                            for (int j = 0; j < 10; j++) {
                                                handler.sendEmptyMessage(11);
                                                try {
                                                    sleep(20);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }.start();
                                }
                            }, 1000);
                        }
                        break;
                }
                break;
        }
    }

    /**
     * 改变HeadView在刷新状态下的显示
     */
    private void changeHeaderViewRefreshState() {
        mHeadRootView.setPadding(0, 0, 0, 0);
        mProgressBar.setVisibility(View.VISIBLE);
        imgDone.setVisibility(GONE);
        mArrowImageView.clearAnimation();
        mArrowImageView.setVisibility(View.GONE);
        mTipsTextView.setText(R.string.loading_eg);
    }

    /**
     * 下拉刷新监听接口
     */
    public interface OnRefreshListener {
        public void onRefresh();
    }

    /**
     * 加载更多监听接口
     */
    public interface OnLoadMoreListener {
        public void onLoadMore();
    }

    public void setOnRefreshListener(OnRefreshListener pRefreshListener) {
        if (pRefreshListener != null) {
            mRefreshListener = pRefreshListener;
            mCanRefresh = true;
        }
    }

    public void setOnLoadListener(OnLoadMoreListener pLoadMoreListener) {
        if (pLoadMoreListener != null) {
            mLoadMoreListener = pLoadMoreListener;
//			mCanLoadMore = true;
            if (mCanLoadMore && getFooterViewsCount() == 0) {
                addFooterView();
            }
        }
    }

    /**
     * 正在下拉刷新
     */
    private void onRefresh() {
        if (mRefreshListener != null) {
            startHeaderViewAnim = true; // 设置可以执行头部消失的动画
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    mRefreshListener.onRefresh();
                }
            }, 300);
        }
    }

    /**
     * 开始下拉刷新
     *
     * @Description
     * @author zipeng
     */
    public void startRefresh() {
        if (mRefreshListener != null) {
            startHeaderViewAnim = true; // 设置可以执行头部消失的动画
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    height = mHeadViewHeight;
                    new Thread() {
                        public void run() {
                            for (int j = 0; j < 30; j++) {
                                if (height <= 0)
                                    break;
                                handler.sendEmptyMessage(13);
                                try {
                                    sleep(20);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }.start();
                }
            }, 100);
        }
    }

    /**
     * 下拉刷新成功
     */
    public void onRefreshComplete() {
        onRefreshComplete(true);
    }

    /**
     * 下拉刷新完成
     *
     * @param isSucc 是否成功
     */
    public void onRefreshComplete(boolean isSucc) {
        if (mCanRefresh) {
            if (isSucc)
                done_state = 1;
            else
                done_state = 2;

            mHeadState = DONE;
            changeHeadViewByState();

            if (mIsMoveToFirstItemAfterRefresh) {
                mFirstItemIndex = 0;
                setSelection(0);
            }

            // 查看列表中有无数据，没有则显示提示
            if (getAdapter().getCount() == 1)
                showHasNoDataView();
        }
    }

    /**
     * 正在加载更多，FootView显示 ： 加载中...
     */
    private void onLoadMore() {
        removeFooterView(mEndRootView);
        removeFooterView(HasNODataHintView);
        addFooterView(mEndRootView);

        if (mLoadMoreListener != null) {
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    mEndRootView.setPadding(0, 0, 0, 0);
                    mEndLoadTipsTextView.setText(R.string.loading_eg);
                    mEndLoadTipsTextView.setVisibility(View.VISIBLE);
                    mEndLoadProgressBar.setVisibility(View.VISIBLE);
                    mLoadMoreListener.onLoadMore();
                }
            }, 100);
        }
    }

    /**
     * 加载更多完成
     */
    public void onLoadMoreComplete() {
        onLoadMoreComplete(true);
    }

    /**
     * 加载更多完成
     *
     * @param isSucc 是否成功
     */
    public void onLoadMoreComplete(boolean isSucc) {
        if (mCanLoadMore) {
            if (mIsAutoLoadMore) {
                mEndState = ENDINT_AUTO_LOAD_DONE;
            } else {
                mEndState = ENDINT_MANUAL_LOAD_DONE;
            }
            if (isSucc)
                done_state = 1;
            else
                done_state = 2;
            changeEndViewByState();
        }
    }

    /**
     * 主要更新一下刷新时间
     */
    public void setAdapter(BaseAdapter adapter) {
        super.setAdapter(adapter);
    }

    public void setEndRootViewVisibility(boolean b) {

        if (mEndRootView == null) {
            return;
        }

        if (getFooterViewsCount() == 0 && b) {
            addFooterView();
        } else {
            removeFooterView(mEndRootView);
        }
    }

    public View getFooterView() {
        return mEndRootView;
    }

    public void showHasNoDataView() {
        removeFooterView(mEndRootView);
        removeFooterView(HasNODataHintView);
        addFooterView(HasNODataHintView);
    }

    public void removeHasNoDataView() {
        removeFooterView(mEndRootView);
        removeFooterView(HasNODataHintView);
        addFooterView(mEndRootView);
    }

    public boolean isCanScroll() {
        return canScroll;
    }

    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }


    public boolean isOnMeasure;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        isOnMeasure = true;
        if (!canScroll) {
            int expandSpec = MeasureSpec.makeMeasureSpec(
                    Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        isOnMeasure = false;
        super.onLayout(changed, l, t, r, b);
    }
}

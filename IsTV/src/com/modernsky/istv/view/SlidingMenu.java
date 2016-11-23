package com.modernsky.istv.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.modernsky.istv.R;
import com.nineoldandroids.view.ViewHelper;

public class SlidingMenu extends HorizontalScrollView {
    private GestureDetector mGestureDetector;
    private static double SCROLL_ANGLE = 30;

    // 滑动距离及坐标
    private float xDistance, yDistance, xLast, yLast;
    private LinearLayout mWapper;
    private ViewGroup mMenu;
    private ViewGroup mContent;
    private int mScreenWidth;

    private int mMenuWidth;
    // dp
    private int mMenuRightPadding = 50;

    private boolean once;

    private boolean isOpen;

    public boolean isOpen() {
        return isOpen;
    }

    private OnSlidingMenuLister slidingMemuLister;

    public interface OnSlidingMenuLister {
        void onSlidingMenuLister(boolean isOpen);
    }

    /**
     * 未使用自定义属性时，调用
     *
     * @param context
     * @param attrs
     */
    public SlidingMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 当使用了自定义属性时，会调用此构造方法
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public SlidingMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // 获取我们定义的属性
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SlidingMenu, defStyle, 0);

        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.SlidingMenu_rightPadding:
                    mMenuRightPadding = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()));
                    break;
            }
        }
        a.recycle();

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mGestureDetector = new GestureDetector(new YScrollDetecotr());
        setFadingEdgeLength(0);
    }

    public SlidingMenu(Context context) {
        this(context, null);
    }

    /**
     * 设置子View的宽和高 设置自己的宽和高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!once) {
            mWapper = (LinearLayout) getChildAt(0);
            mMenu = (ViewGroup) mWapper.getChildAt(0);
            mContent = (ViewGroup) mWapper.getChildAt(1);
            mMenuWidth = mMenu.getLayoutParams().width = mScreenWidth - mMenuRightPadding;
            mContent.getLayoutParams().width = mScreenWidth;
            once = true;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 通过设置偏移量，将menu隐藏
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            this.scrollTo(mMenuWidth, 0);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                // 隐藏在左边的宽度
                int scrollX = getScrollX();
                if (scrollX >= mMenuWidth / 2) {
                    this.smoothScrollTo(mMenuWidth, 0);
                    isOpen = false;
                } else {
                    this.smoothScrollTo(0, 0);
                    isOpen = true;
                }
                if (slidingMemuLister != null) {
                    slidingMemuLister.onSlidingMenuLister(isOpen);
                }
                return true;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 打开菜单
     */
    public void openMenu() {
        if (isOpen)
            return;
        this.smoothScrollTo(0, 0);
        isOpen = true;
        if (slidingMemuLister != null) {
            slidingMemuLister.onSlidingMenuLister(true);
        }
    }

    /**
     * 关闭菜单
     */
    public void closeMenu() {
        if (!isOpen)
            return;
        this.smoothScrollTo(mMenuWidth, 0);
        isOpen = false;
        if (slidingMemuLister != null) {
            slidingMemuLister.onSlidingMenuLister(false);
        }
    }

    /**
     * 关闭菜单
     */
    public void close() {
        this.scrollTo(mMenuWidth, 0);
        isOpen = false;
    }

    /**
     * 切换菜单
     */
    public void toggle() {
        if (isOpen) {
            closeMenu();
        } else {
            openMenu();
        }
    }

    /**
     * 滚动发生时
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        float scale = l * 1.0f / mMenuWidth; // 1 ~ 0

        /**
         * 区别1：内容区域1.0~0.9 缩放的效果 scale : 1.0~0.0 == 0.9 + 0.1 * scale
         *
         * 区别2：菜单的偏移量需要修改
         *
         * 区别3：菜单的显示时有缩放以及透明度变化 缩放：0.7 ~1.0 1.0 - scale * 0.3 透明度 0.6 ~ 1.0 0.6+
         * 0.4 * (1- scale) ;
         *
         */
        float rightScale = 0.9f + 0.1f * scale;
        float leftScale = 1.0f - scale * 0.3f;
        float leftAlpha = 0.6f + 0.4f * (1 - scale);

        // 调用属性动画，设置TranslationX
        ViewHelper.setTranslationX(mMenu, mMenuWidth * scale * 0.8f);

        ViewHelper.setScaleX(mMenu, leftScale);
        ViewHelper.setScaleY(mMenu, leftScale);
        ViewHelper.setAlpha(mMenu, leftAlpha);
        // 设置content的缩放的中心点
        ViewHelper.setPivotX(mContent, 0);
        ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
        ViewHelper.setScaleX(mContent, rightScale);
        ViewHelper.setScaleY(mContent, rightScale);

    }

    public void setOnSlidingMenuLister(OnSlidingMenuLister lister) {
        this.slidingMemuLister = lister;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev) && (mGestureDetector == null || mGestureDetector.onTouchEvent(ev));
    }

    class YScrollDetecotr extends SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (Math.abs(distanceX) <= 5.0f) {
                return false;
            }

            // if (Math.abs(distanceY) >= Math.abs(distanceX)) {
            // System.out.println("distanceX = " + distanceX
            // + " , distanceY = " + distanceY);
            // return true;
            // }
            // return false;

            double angle = Math.atan2(Math.abs(distanceY), Math.abs(distanceX));
            // LogUtils.tiaoshi("angle-->", (180 * angle) / Math.PI + "");
            if ((180 * angle) / Math.PI < SCROLL_ANGLE) {
                return true;
            }
            return false;
        }
    }

}

/**
 *
 */
package com.modernsky.istv.view;

import android.content.Context;
import android.graphics.PointF;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * @author： fengqingyun2008
 * @Email： fengqingyun2008@gmail.com
 * @version：1.0
 * @创建时间：2015-6-8 下午5:24:28
 * @类说明：
 */
public class ChildViewPager extends ViewPager {
    private GestureDetector mGestureDetector;
    private static double SCROLL_ANGLE = 30;

    /**
     * 触摸时按下的点
     **/
    PointF downP = new PointF();
    /**
     * 触摸时当前的点
     **/
    PointF curP = new PointF();
    OnSingleTouchListener onSingleTouchListener;
    private Context context;

    public ChildViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        // TODO Auto-generated constructor stub
        initView(context);
    }

    private void initView(Context context) {
        mGestureDetector = new GestureDetector(new MyScrollDetecotr());

    }

    public ChildViewPager(Context context) {
        super(context);
        this.context = context;
        // TODO Auto-generated constructor stub
        initView(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        // 当拦截触摸事件到达此位置的时候，返回true，
        // 说明将onTouch拦截在此控件，进而执行此控件的onTouchEvent
        // int x = (int) arg0.getX();
        // if (x < getDisplay().getWidth() / 10)
        return false;
//        return super.onInterceptTouchEvent(ev) && mGestureDetector.onTouchEvent(ev);

    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        // TODO Auto-generated method stub
        // 每次进行onTouch事件都记录当前的按下的坐标
        curP.x = arg0.getX();
        curP.y = arg0.getY();

        if (arg0.getAction() == MotionEvent.ACTION_DOWN) {
            // 记录按下时候的坐标
            // 切记不可用 downP = curP ，这样在改变curP的时候，downP也会改变
            downP.x = arg0.getX();
            downP.y = arg0.getY();
            // 此句代码是为了通知他的父ViewPager现在进行的是本控件的操作，不要对我的操作进行干扰
            getParent().requestDisallowInterceptTouchEvent(mGestureDetector.onTouchEvent(arg0));
        }

        if (arg0.getAction() == MotionEvent.ACTION_MOVE) {
            // 此句代码是为了通知他的父ViewPager现在进行的是本控件的操作，不要对我的操作进行干扰
            getParent().requestDisallowInterceptTouchEvent(mGestureDetector.onTouchEvent(arg0));
        }

        if (arg0.getAction() == MotionEvent.ACTION_UP) {
            // 在up时判断是否按下和松手的坐标为一个点
            // 如果是一个点，将执行点击事件，这是我自己写的点击事件，而不是onclick
            if (Math.abs(downP.x - curP.x) < 5.0f
                    && Math.abs(downP.y - curP.y) < 5.0f) {
                onSingleTouch();
                return true;
            }
        }

        return super.onTouchEvent(arg0);
    }

    /**
     * 单击
     */
    public void onSingleTouch() {
        if (onSingleTouchListener != null) {
            onSingleTouchListener.onSingleTouch();
        }
    }

    /**
     * 创建点击事件接口
     *
     * @author wanpg
     */
    public interface OnSingleTouchListener {
         void onSingleTouch();
    }

    public void setOnSingleTouchListener(
            OnSingleTouchListener onSingleTouchListener) {
        this.onSingleTouchListener = onSingleTouchListener;
    }

    class MyScrollDetecotr extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (Math.abs(distanceX) <= 1.0f) {
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


//
//    @Override
//    public void requestChildFocus(View child, View focused) {
//        LogUtils.d("childviewpager requestChildFocus");
//        if (getDescendantFocusability() == FOCUS_BLOCK_DESCENDANTS) {
//                    return;
//                }
//
//             // Unfocus us, if necessary
//             super.unFocus();
//
//             // We had a previous notion of who had focus. Clear it.
//             if (mFocused != child) {
//                    if (mFocused != null) {
//                           mFocused.unFocus();
//                        }
//
//                    mFocused = child;
//               }
//            if (mParent != null) {
//                   mParent.requestChildFocus(this, focused);
//               }
//        super.requestChildFocus(child, focused);
//    }
}

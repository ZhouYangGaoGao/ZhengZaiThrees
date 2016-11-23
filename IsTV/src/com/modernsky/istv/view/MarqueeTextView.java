/**
 *
 */
package com.modernsky.istv.view;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author： fengqingyun2008
 * @Email： fengqingyun2008@gmail.com
 * @version：1.0
 * @创建时间：2015-5-29 下午6:58:53
 * @类说明：
 */
public class MarqueeTextView extends TextView {

    /**
     * 是否停止滚动
     */
    private boolean mStopMarquee;
    private String mText;
    private float mCoordinateX;
    private int mCoordinateY = 0;
    private float mTextWidth;

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mCoordinateY = getHeight() - getPaddingBottom() - 5;
    }

    public void setText(String text) {
        this.mText = text;
        mTextWidth = getPaint().measureText(mText);
        mCoordinateX = getWidth();
        // if (mHandler.hasMessages(0))
        // mHandler.removeMessages(0);
        mHandler.sendEmptyMessageDelayed(0, 2000);
    }

    @Override
    protected void onAttachedToWindow() {
        mStopMarquee = false;
        if (!TextUtils.isEmpty(mText))
            mHandler.sendEmptyMessageDelayed(0, 2000);
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        mStopMarquee = true;
        if (mHandler.hasMessages(0))
            mHandler.removeMessages(0);
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!TextUtils.isEmpty(mText))
            canvas.drawText(mText, mCoordinateX, mCoordinateY, getPaint());
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    mCoordinateX -= 0.5f;
                    invalidate();
                    if (!mStopMarquee) {
                        sendEmptyMessageDelayed(0, 30);
                    }

                    break;
            }
            super.handleMessage(msg);
        }
    };

}
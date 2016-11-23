/**
 *
 */
package com.modernsky.istv.view;

import com.modernsky.istv.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

/**
 * @author： fengqingyun2008
 * @Email： fengqingyun2008@gmail.com
 * @version：1.0
 * @创建时间：2015-5-27 下午5:17:16
 * @类说明：
 */
public class RotateTextView extends TextView {

    // XML调用方法
    // <com.modernsky.istv.view.RotateTextView
    // android:id="@+id/item_rotate_textview"
    // android:layout_width="wrap_content"
    // android:layout_height="wrap_content"
    // android:background="@drawable/icon_conner_city"
    // android:paddingBottom="40dp"
    // android:text="成都"
    // android:textSize="20sp"
    // app:degree="45" />

    // Java调用
    // RotateTextView mText = (RotateTextView) findViewById (R.id.text);
    // mText.setDegrees(10);

    private static final int DEFAULT_DEGREES = 0;

    private int mDegrees;

    public RotateTextView(Context context) {
        super(context, null);
    }

    public RotateTextView(Context context, AttributeSet attrs) {
        super(context, attrs, android.R.attr.textViewStyle);

        this.setGravity(Gravity.CENTER);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.RotateTextView);
        mDegrees = a.getInteger(R.styleable.RotateTextView_degree,
                DEFAULT_DEGREES);

        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getCompoundPaddingLeft(), getExtendedPaddingTop());
        canvas.rotate(mDegrees, this.getWidth() / 2f, this.getHeight() / 2f);
        super.onDraw(canvas);
        canvas.restore();
    }

    public void setDegrees(int degrees) {
        mDegrees = degrees;
    }

}

package com.modernsky.istv.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.modernsky.istv.R;

public class RoundAngleImageView extends ImageView {

    private Paint mPaint, mPaintSide, mPaintSweep;
    private int roundWidth = 0;
    private int roundHeight = 0;
    private Paint paint2;
    private int sideWidth = 0;
    private int sideColor = Color.GRAY;//xml里面设置颜色值不能带有透明度
    private float sideStartAngle = -90;//起始角度默认-90度
    private float sideSweepAngle = 0;
    private int sideAngleColor = Color.BLUE;

    public RoundAngleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public RoundAngleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RoundAngleImageView(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs,
                    R.styleable.RoundAngleImageView);
            roundWidth = a.getDimensionPixelSize(
                    R.styleable.RoundAngleImageView_roundWidth, roundWidth);
            roundHeight = a.getDimensionPixelSize(
                    R.styleable.RoundAngleImageView_roundHeight, roundHeight);
            sideWidth = a.getDimensionPixelSize(
                    R.styleable.RoundAngleImageView_sideWidth, sideWidth);
            sideColor = a.getColor(R.styleable.RoundAngleImageView_sideColor, sideColor);
            a.recycle();
        } else {
            float density = context.getResources().getDisplayMetrics().density;
            roundWidth = (int) (roundWidth * density);
            roundHeight = (int) (roundHeight * density);
            sideWidth = (int) (sideWidth * density);
        }

        mPaint = new Paint();
        mPaint.setColor(sideColor);
        mPaint.setStrokeWidth(sideWidth);
        mPaint.setAntiAlias(true);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        mPaintSide = new Paint();
        mPaintSide.setColor(sideColor);
        mPaintSide.setAntiAlias(true);
        mPaintSide.setFilterBitmap(true);
        mPaintSide.setStyle(Paint.Style.STROKE);
        mPaintSide.setStrokeWidth(sideWidth);

        mPaintSweep = new Paint();
        mPaintSweep.setColor(sideAngleColor);
        mPaintSweep.setAntiAlias(true);
        mPaintSweep.setFilterBitmap(true);
        mPaintSweep.setStyle(Paint.Style.STROKE);
        mPaintSweep.setStrokeWidth(sideWidth);


        paint2 = new Paint();
        paint2.setXfermode(null);
    }

    @Override
    public void draw(Canvas canvas) {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                Config.ARGB_8888);
        if (roundHeight == 0 && roundWidth == 0) {
            roundHeight = getHeight() / 2;
            roundWidth = getHeight() / 2;
        }
        Canvas canvas2 = new Canvas(bitmap);
        super.draw(canvas2);
        drawLiftUp(canvas2);
        drawRightUp(canvas2);
        drawLiftDown(canvas2);
        drawRightDown(canvas2);
        canvas.drawBitmap(bitmap, 0, 0, paint2);
        if (sideWidth > 0) {
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, roundHeight - sideWidth / 2, mPaintSide);
        }
        if (sideSweepAngle != 0) {
            canvas.drawArc(new RectF(sideWidth / 2, sideWidth / 2, getWidth() - sideWidth / 2, getHeight() - sideWidth / 2), sideStartAngle, sideSweepAngle, false, mPaintSweep);
        }
        bitmap.recycle();
    }

    private void drawLiftUp(Canvas canvas) {
        Path path = new Path();
        path.moveTo(0, roundHeight);
        path.lineTo(0, 0);
        path.lineTo(roundWidth, 0);
        path.arcTo(new RectF(0, 0, roundWidth * 2, roundHeight * 2), -90, -90);
        path.close();
        canvas.drawPath(path, mPaint);
    }

    private void drawLiftDown(Canvas canvas) {
        Path path = new Path();
        path.moveTo(0, getHeight() - roundHeight);
        path.lineTo(0, getHeight());
        path.lineTo(roundWidth, getHeight());
        path.arcTo(new RectF(0, getHeight() - roundHeight * 2,
                 roundWidth * 2, getHeight()), 90, 90);
        path.close();
        canvas.drawPath(path, mPaint);
    }

    private void drawRightDown(Canvas canvas) {
        Path path = new Path();
        path.moveTo(getWidth() - roundWidth, getHeight());
        path.lineTo(getWidth(), getHeight());
        path.lineTo(getWidth(), getHeight() - roundHeight);
        path.arcTo(new RectF(getWidth() - roundWidth * 2, getHeight()
                - roundHeight * 2, getWidth(), getHeight()), 0, 90);
        path.close();
        canvas.drawPath(path, mPaint);
    }

    private void drawRightUp(Canvas canvas) {
        Path path = new Path();
        path.moveTo(getWidth(), roundHeight);
        path.lineTo(getWidth(), 0);
        path.lineTo(getWidth() - roundWidth, 0);
        path.arcTo(new RectF(getWidth() - roundWidth * 2, 0, getWidth(),
                roundHeight * 2), -90, 90);
        path.close();
        canvas.drawPath(path, mPaint);
    }

    public float getSideStartAngle() {
        return sideStartAngle;
    }

    public void setSideStartAngle(float sideStartAngle) {
        this.sideStartAngle = sideStartAngle;
        invalidate();
    }

    public float getSideSweepAngle() {
        return sideSweepAngle;
    }

    public void setSideSweepAngle(float sideSweepAngle) {
        this.sideSweepAngle = sideSweepAngle;
        invalidate();
    }

    public int getSideAngleColor() {
        return sideAngleColor;
    }

    public void setSideAngleColor(int sideAngleColorId) {
        this.sideAngleColor = getResources().getColor(sideAngleColorId);
        if (mPaintSweep != null)
            mPaintSweep.setColor(sideAngleColor);
        invalidate();
    }
}

package main.java.cn.aigestudio.datepicker.utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import com.modernsky.istv.R;

import main.java.cn.aigestudio.datepicker.views.MonthView;

/**
 * 
 * @category 该类是实现画小圆的，需要传参数改变圆的位置和圆的颜色 参数为： color(0为蓝色,1为粉色) canvas(画布)
 *           leftOrRight (在左边或在右边) HightOrBottom(在上面还是在下面) CirclesSize(圆的大小)
 * 
 * @author wwj
 * 
 */
public class PaintCircle {

	private static Paint mPaint;

	public static void paintCircles(MonthView view,int color, Canvas canvas,
			float leftOrRight, float hightOrBottom, float CirclesSize) {
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
		// 判断返回的type ，根据type画不同的圆,1时为当天,2为活动日,3
		if (color == 0) {
			mPaint.setColor(Color.rgb(176, 216, 244));// 蓝色画笔
		} else if (color == 1) {
			mPaint.setColor(Color.rgb(213, 50, 250));// 粉色画笔
		} else if (color == 2) {
			mPaint.setColor(Color.rgb(34, 42, 47));// 黑色画笔
		} else  if (color == 3){
			mPaint.setColor(view.getContext().getResources().getColor(R.color.hui64));// 灰色画笔
		}
         
		canvas.drawCircle(leftOrRight, hightOrBottom, CirclesSize, mPaint);// 小圆蓝色

	}

	public static Point PaintCircless(int n, float x, float y, float radius) {
		Point p = new Point();
		double tan = (Math.PI / 12) * n;
		p.x = (int) (x + (radius * 9 / 11) * Math.cos(tan));
		p.y = (int) (y + (radius * 9 / 11) * Math.sin(tan));

		return p;
	}

}

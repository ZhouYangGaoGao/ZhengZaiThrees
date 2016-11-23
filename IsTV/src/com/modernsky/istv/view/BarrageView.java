/**
 *
 */
package com.modernsky.istv.view;

import java.util.HashSet;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.modernsky.istv.bean.TanmuBean;
import com.modernsky.istv.utils.ScreenUtils;

/**
 * @author： fengqingyun2008
 * @Email： fengqingyun2008@gmail.com
 * @version：1.0
 * @创建时间：2015-9-8 下午2:28:20
 * @类说明：弹幕
 */
public class BarrageView extends RelativeLayout {
    private Context mContext;

    /**
     * @param context
     * @param attrs
     */
    public BarrageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        tanmuBean = new TanmuBean();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    public void showTanmu(String content, float textSize, int textColor) {
        if (getVisibility() != View.VISIBLE)
            return;
        final TextView textView = new TextView(mContext);

        textView.setTextSize(textSize);
        textView.setText(content);
        // textView.setSingleLine();
        textView.setTextColor(textColor);

        int leftMargin = getRight() - getLeft() - getPaddingLeft();
        // 计算本条弹幕的topMargin(随机值，但是与屏幕中已有的不重复)
        int verticalMargin = getRandomTopMargin();
        textView.setTag(verticalMargin);

        LayoutParams params = new LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);

        params.topMargin = verticalMargin;

        textView.setLayoutParams(params);
        Animation anim = createTranslateAnim(mContext, leftMargin,
                -ScreenUtils.getScreenWidth(mContext));
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 移除该组件
                removeView(textView);
                // 移除占位
                int verticalMargin = (Integer) textView.getTag();
                existMarginValues.remove(verticalMargin);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        textView.startAnimation(anim);

        addView(textView);
    }

    // 记录当前仍在显示状态的弹幕的位置（避免重复）
    private HashSet<Integer> existMarginValues = new HashSet<Integer>();
    private int linesCount;
    private int validHeightSpace;
    private TanmuBean tanmuBean;

    private int getRandomTopMargin() {
        // 计算用于弹幕显示的空间高度
        if (validHeightSpace == 0) {
            validHeightSpace = getBottom() - getTop() - getPaddingTop()
                    - getPaddingBottom();
        }

        // 计算可用的行数
        if (linesCount == 0) {
            linesCount = validHeightSpace
                    / ScreenUtils.dip2px(mContext, tanmuBean.getMinTextSize()
                    * (1 + tanmuBean.getRange()));
            if (linesCount == 0) {
                throw new RuntimeException("Not enough space to show text.");
                // return 0;
            }
        }
        int randomIndex = (int) (Math.random() * linesCount);
        int marginValue = randomIndex * (validHeightSpace / linesCount);
        if (!existMarginValues.contains(marginValue)) {
            existMarginValues.add(marginValue);
            return marginValue;
        }
        return marginValue;
    }

    /**
     * 创建平移动画
     */
    public Animation createTranslateAnim(Context context, int fromX, int toX) {
        TranslateAnimation tlAnim = new TranslateAnimation(fromX, toX, 0, 0);
        // 自动计算时间
        long duration = (long) (Math.abs(toX - fromX) * 1.0f
                / ScreenUtils.getScreenWidth(context) * 4000);
        tlAnim.setDuration(duration + 2000);
        // tlAnim.setInterpolator(new DecelerateAccelerateInterpolator());
        tlAnim.setFillAfter(true);

        return tlAnim;
    }

    public class DecelerateAccelerateInterpolator implements Interpolator {

        // input从0～1，返回值也从0～1.返回值的曲线表征速度加减趋势
        @Override
        public float getInterpolation(float input) {
            return (float) (Math.tan((input * 2 - 1) / 4 * Math.PI)) / 2.0f + 0.5f;
        }
    }

}

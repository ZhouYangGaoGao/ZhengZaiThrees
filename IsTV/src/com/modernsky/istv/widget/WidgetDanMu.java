package com.modernsky.istv.widget;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.modernsky.istv.R;
import com.modernsky.istv.bean.XiuchanMessage;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.BitmapTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.ScreenUtils;
import com.modernsky.istv.utils.Utils;
import com.modernsky.istv.view.RoundAngleImageView;

import java.util.HashSet;

public class WidgetDanMu extends LinearLayout {
    private View inflate = null;
    //
    private RelativeLayout mBarrageView = null;
    private XiuchanMessage message = null;
    private boolean notification = false;
    private boolean isMyGift = false;
    private boolean isSelf = false;
    private int validHeightSpace;
    private HashSet<Integer> existMarginValues = new HashSet<Integer>();
    //
    private LinearLayout ll_danmu;
    private RoundAngleImageView img_avatar = null;
    private ImageView img_liwu = null;
    private TextView tv_name = null;
    private TextView tv_songgei = null;
    private TextView tv_danmu = null;
    private int linesCount;
    private int singleLineHeight;

    public WidgetDanMu(Context context) {
        super(context);
        inflate = LayoutInflater.from(context).inflate(R.layout.widget_danmu, this);
        initView(inflate);
    }

    public void setParameter(RelativeLayout mBarrageView,//
                             XiuchanMessage message,//
                             boolean isSelf) {
        this.mBarrageView = mBarrageView;
        this.message = message;
        this.isSelf = isSelf;
    }

    private void initView(View view) {
        // 头像
        ll_danmu = (LinearLayout) view.findViewById(R.id.ll_danmu);
        img_avatar = (RoundAngleImageView) view.findViewById(R.id.civ);
        tv_name = (TextView) view.findViewById(R.id.tv_name1);// name
        tv_name.setVisibility(View.GONE);
        tv_songgei = (TextView) view.findViewById(R.id.tv_songgei);// 送给主播
        tv_songgei.setVisibility(View.GONE);
        tv_danmu = (TextView) view.findViewById(R.id.tv_danmu);// 弹幕内容
        img_liwu = (ImageView) view.findViewById(R.id.img_liwu);// 礼物
        img_liwu.setVisibility(View.GONE);
    }

    public void show() {
        setText();
        int leftMargin = setPosition();
        Animation anim = setAnimation(leftMargin);
        inflate.startAnimation(anim);
        mBarrageView.addView(inflate);
    }

    private void setText() {
        // 头像 + 弹幕
//        Picasso.with(getContext()).load(message.getFromUserPic()).into(img_avatar);
        BitmapTool.getInstance().getAdapterUitl().display(img_avatar, message.getFromUserPic());
        tv_danmu.setText(message.getFromUserName()+": "+message.getMsg());
        tv_danmu.setTextColor(Color.WHITE);
        // 礼物弹幕
        if (message.getGiftId() != null) {
            img_avatar.setImageResource(R.drawable.icon_lingdang);
            img_liwu.setVisibility(View.VISIBLE);
            // tv_name.setText(message.getFromUserName());// 本人信息
            tv_danmu.setTextColor(Color.parseColor("#FBBF00"));
            BitmapTool.getInstance().getAdapterUitl().display(img_liwu, message.getPic());
            return;
        }
        //
        String currentId = message.getFromUserId() + "";
        String userId = UserService.getInatance().getUserBean(getContext()).getId();//
        LogUtils.d(currentId + " " + userId);
        // 系统管理员
        if (currentId.equals(Constants.Id_ZhengXiaoZai)) {
            tv_danmu.setTextColor(Color.rgb(248, 255, 103));
        }
        // 如果是自己 ，颜色变成蓝色
        if (currentId.equals(userId)) {
            tv_danmu.setTextColor(Color.parseColor("#8DBFD8"));
        }
    }

    private int setPosition() {
        int leftMargin = 0;
        if (notification) {
            if (isMyGift) {
                leftMargin = (mBarrageView.getRight() - mBarrageView.getLeft() - mBarrageView.getPaddingLeft());
            } else {
                leftMargin = 2 * (mBarrageView.getRight() - mBarrageView.getLeft() - mBarrageView.getPaddingLeft());
            }
        }
        if (!notification) {
            leftMargin = mBarrageView.getRight() - mBarrageView.getLeft() - mBarrageView.getPaddingLeft();
        }
        return leftMargin;
    }

    //
    private Animation setAnimation(int leftMargin) {
        Animation anim = createTranslateAnim(getContext(), leftMargin, -ScreenUtils.getScreenWidth(getContext()));
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 移除该组件
                mBarrageView.removeView(inflate);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        //
        if (notification && !isMyGift) {
            anim.setStartOffset(mBarrageView.getChildCount() * anim.getDuration() / 4);
        }
        return anim;
    }

//    // 计算本条弹幕的topMargin(随机值，但是与屏幕中已有的不重复)
//    private int getRandomTopMargin() {
//        // 计算用于弹幕显示的空间高度
//        validHeightSpace = mBarrageView.getBottom() - mBarrageView.getTop() - mBarrageView.getPaddingTop() - mBarrageView.getPaddingBottom();
//        int marginTop = (int) (Math.random() * (validHeightSpace - 100));
//        if (!existMarginValues.contains(marginTop)) {
//            existMarginValues.add(marginTop);
//        }
//        Log.w("xqp", "marginTop " + marginTop);
//        return marginTop;
//    }

    /**
     * 创建平移动画
     */
    public Animation createTranslateAnim(Context context, int fromX, int toX) {
        int marginTop = getRandomTopMargin();
        TranslateAnimation tlAnim = new TranslateAnimation(fromX, toX, marginTop, marginTop);
        // 自动计算时间
        long duration = (long) (Math.abs(toX - fromX) * 1.0f / ScreenUtils.getScreenWidth(context) * 4000);
        tlAnim.setDuration(duration + 2000);
        // tlAnim.setInterpolator(new DecelerateAccelerateInterpolator());
        tlAnim.setFillAfter(true);
        return tlAnim;
    }

    private int getRandomTopMargin() {
        // 计算用于弹幕显示的空间高度
        if (validHeightSpace == 0) {
            validHeightSpace = mBarrageView.getBottom() - mBarrageView.getTop()
                    - mBarrageView.getPaddingTop()
                    - mBarrageView.getPaddingBottom();
        }
        if (singleLineHeight == 0) {
            singleLineHeight = Utils.getViewHeight(inflate);
        }
//        LogUtils.t("validHeightSpace", "validHeightSpace=" + validHeightSpace);
//        LogUtils.t("singleLineHeight", "singleLineHeight=" + singleLineHeight);
        // 计算可用的行数
        if (linesCount == 0) {
            linesCount = validHeightSpace
                    / (singleLineHeight + 5);
            if (linesCount == 0) {
//                throw new RuntimeException("Not enough space to show text.");
                LogUtils.d("Not enough space to show text.and give difoult lineCount = 1");
                return 1;
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


    public class DecelerateAccelerateInterpolator implements Interpolator {

        // input从0～1，返回值也从0～1.返回值的曲线表征速度加减趋势
        @Override
        public float getInterpolation(float input) {
            return (float) (Math.tan((input * 2 - 1) / 4 * Math.PI)) / 2.0f + 0.5f;
        }
    }
}
package com.modernsky.istv.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.modernsky.istv.R;
import com.modernsky.istv.bean.MusicInfo;
import com.modernsky.istv.service.DianTaiService;
import com.modernsky.istv.tool.BitmapTool;
import com.modernsky.istv.tool.DialogTool;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.PreferencesUtils;

//import android.view.animation.RotateAnimation;

public class DrageLayout extends RelativeLayout implements
        android.view.View.OnClickListener {
    //
    private ViewDragHelper mDragHelper;
    private View view;// 拖拽的小球
    private RelativeLayout layout_buttom;
    private boolean isTrans;
    private ImageView bacImg, bagImgRight, img5, img6;
    private float otherX, otherY;
    private ImageView mPlayBtn;
    private ImageView mPlayNext;
    private ImageView mIfLike;
    private ImageView mDeleteSong;
    private TextView textSongName;
    private TextView textPersonName;
    private RelativeLayout mPlayLauout;
    private RelativeLayout mNextLayout;
    private RelativeLayout mIfLikeLayout;
    private RelativeLayout mDeleteLayout;
    private Context context;
    private MyReceive myReceive;
    private RoundAngleImageView imgButtomView;
    private RoundAngleImageView img4;
    private View leftButtomView;
    private ImageView img3, imgButtom3;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    backButtom();
                    break;
                case 1:
                    showButtom();
                    break;
                case 2:
                    mPlayLauout.setEnabled(true);
                    break;
                case 3:
                    mNextLayout.setEnabled(true);
                    break;
                case 4:
                    mIfLikeLayout.setEnabled(true);
                    break;
                case 5:
                    mDeleteLayout.setEnabled(true);
                    break;

                default:
                    break;
            }
        }
    };
//    private Handler mHandler = new Handler() {
//        public void handleMessage(android.os.Message msg) {
//            switch (msg.what) {
//                case 0:
//                    backButtom();
//                    break;
//                case 1:
//                    showButtom();
//                    break;
//                case 2:
//                    mPlayLauout.setEnabled(true);
//                    break;
//                case 3:
//                    mNextLayout.setEnabled(true);
//                    break;
//                case 4:
//                    mIfLikeLayout.setEnabled(true);
//                    break;
//                case 5:
//                    mDeleteLayout.setEnabled(true);
//                    break;
//
//                default:
//                    break;
//            }
//        }
//    };
    protected boolean isAnmShowing;
    private int defultBottom;
    private boolean isRolate;

    @Override
    public void invalidate() {
        LogUtils.d("invalidate");
        super.invalidate();
    }

    public DrageLayout(Context context) {
        this(context, null);
    }

    public DrageLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrageLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }

    public void setDefultBottom(int defultBottom) {
        this.defultBottom = defultBottom;
    }

    public void initDrageLayoutPosition() {
        //
        LayoutParams params = (LayoutParams) view.getLayoutParams();
        params.bottomMargin = DianTaiService.getInstance().getMarginButtom();
        LogUtils.d("buttom=" + DianTaiService.getInstance().getMarginButtom());
        LogUtils.d("initDrageLayoutPosition.bottomMargin====" + params.bottomMargin);
        params.leftMargin = DianTaiService.getInstance().getMarginLeft();
        isRight = !DianTaiService.getInstance().isLeftOrRight();
        if (DianTaiService.getInstance().isLeftOrRight()) {
            getImg().setVisibility(View.VISIBLE);
            getImgRight().setVisibility(View.INVISIBLE);
            LogUtils.d("leftleft");
        } else {
            getImg().setVisibility(View.INVISIBLE);
            getImgRight().setVisibility(View.VISIBLE);
            LogUtils.d("rightright");
        }
        view.setLayoutParams(params);
    }

    public void getDrageLayoutPositionToLocal() {
        //
//        LayoutParams params = (LayoutParams) view.getLayoutParams();
        DianTaiService.getInstance().setMarginButtom(
                (int) (this.getHeight() - view.getY() - view.getHeight()));
        LogUtils.d("getDrageLayoutPositionToLocal.bottomMargin===="
                + (this.getHeight() - view.getY() - view.getHeight()));
        DianTaiService.getInstance().setMarginLeft((int) view.getX());
        DianTaiService.getInstance().setLeftOrRight(!isRight);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        myReceive = new MyReceive();
        IntentFilter filter = new IntentFilter(DianTaiService.ACTION_SERVICE);
        context.registerReceiver(myReceive, filter);
        // initDrageLayoutPosition();
        LogUtils.d("1111onAttachedToWindow");
        updateMusic();
    }

    private void updateMusic() {
        isPlaying = DianTaiService.getInstance().isIsplaying();
        MusicInfo info = DianTaiService.getInstance().getCurrentMusicInfo();
        if (info != null) {
            textPersonName.setText(info.getStarringNames());
            textSongName.setText(info.getName());
            if (!isPlaying) {
                if (isRolate) {
//                    animatorSet.cancel();
                    cancleRolote();
                    isRolate = false;
                }
                mPlayBtn.setImageResource(R.drawable.radio_controlbar_play);
            } else {
                if (!isRolate) {
//                    animatorSet.start();
                    startRolote();
                    isRolate = true;
                }
                LogUtils.d("");
                mPlayBtn.setImageResource(R.drawable.radio_controlbar_pause);
            }
            if (info.getIslike().equals("1")) {
                mIfLike.setImageResource(R.drawable.radio_controlbar_1like_hl);
            } else {
                mIfLike.setImageResource(R.drawable.radio_controlbar_1like);
            }
            BitmapTool.getInstance().showLocalView(img4, info.getSmallPic());
            BitmapTool.getInstance().showLocalView(imgButtomView, info.getSmallPic());
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        // clearAnimation();
//        animatorSet.cancel();
        cancleRolote();
        context.unregisterReceiver(myReceive);
        getDrageLayoutPositionToLocal();
        LogUtils.d("onDetachedFromWindow");
        super.onDetachedFromWindow();
    }

    public ImageView getImg() {
        return bacImg == null ? ((ImageView) view.findViewById(R.id.img_bac))
                : bacImg;
    }

    public ImageView getImgRight() {
        return bagImgRight == null ? ((ImageView) view
                .findViewById(R.id.img_bac2)) : bagImgRight;
    }

    public ImageView getImg5() {
        return img5 == null ? ((ImageView) view.findViewById(R.id.img5)) : img5;
    }

    public ImageView getImg6() {
        return img6 == null ? ((ImageView) view.findViewById(R.id.img6)) : img6;

    }

    /**
     * @params ViewGroup forParent 必须是一个ViewGroup
     * @params float sensitivity 灵敏度
     * @params Callback cb 回调
     */
    private void init() {
        mDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragCallback());
    }

//    RotateAnimation animation;

    private ObjectAnimator img3Animator, img4Animator, img3ButtomAnimator, img4ButtomAnimator;
    private AnimatorSet animatorSet = new AnimatorSet();

    public void initAnimator() {
        img3Animator = ObjectAnimator.ofFloat(img3, "rotation", rolateValues,
                360 + rolateValues);
        img4Animator = ObjectAnimator.ofFloat(img4.getParent(), "rotation", rolateValues,
                360 + rolateValues);
        img3ButtomAnimator = ObjectAnimator.ofFloat(imgButtom3, "rotation", rolateValues,
                360 + rolateValues);
        img4ButtomAnimator = ObjectAnimator.ofFloat(imgButtomView.getParent(), "rotation", rolateValues,
                360 + rolateValues);
        img3Animator.setInterpolator(new LinearInterpolator());
        img3Animator.setRepeatCount(RotateAnimation.INFINITE);// 设置重复次数
        img3Animator.setDuration(3000);
        img4Animator.setInterpolator(new LinearInterpolator());
        img4Animator.setRepeatCount(RotateAnimation.INFINITE);// 设置重复次数
        img4Animator.setDuration(3000);
        img3ButtomAnimator.setInterpolator(new LinearInterpolator());
        img3ButtomAnimator.setRepeatCount(RotateAnimation.INFINITE);// 设置重复次数
        img3ButtomAnimator.setDuration(3000);
        img4ButtomAnimator.setInterpolator(new LinearInterpolator());
        img4ButtomAnimator.setRepeatCount(RotateAnimation.INFINITE);// 设置重复次数
        img4ButtomAnimator.setDuration(3000);
        animatorSet.playTogether(img3Animator, img4Animator, img3ButtomAnimator, img4ButtomAnimator);
    }

    float rolateValues = 0;

    public void startRolote() {
        initAnimator();
        animatorSet.start();
    }

    public void cancleRolote() {
        rolateValues = img3.getRotation();
        animatorSet.cancel();
    }
//    public void initAnimation() {
//        // likeanimation = ObjectAnimator.ofFloat(mIfLikeLayout, "rotation", 0,
//        // 360);
//        // animation.setInterpolator(new LinearInterpolator());
//        // likeanimation.setRepeatCount(RotateAnimation.INFINITE);// 设置重复次数
//        // likeanimation.setDuration(2000);
//        // animation.play(likeanimation);
//        animation = new RotateAnimation(0f, 359f, Animation.RELATIVE_TO_SELF,
//                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//
//        animation.setDuration(3000);// 设置动画持续时间
//        animation.setInterpolator(new LinearInterpolator());
//
//        /** 常用方法 */
//        animation.setRepeatCount(RotateAnimation.INFINITE);// 设置重复次数
//        animation.setFillAfter(true);// 动画执行完后是否停留在执行完的状态
//        // animation.setStartOffset(long startOffset);//执行前的等待时间
//
//        img3.setAnimation(animation);
//        ((View) img4.getParent()).setAnimation(animation);
//        imgButtom3.setAnimation(animation);
//        ((View) imgButtomView.getParent()).setAnimation(animation);
//
//    }

    // 设置拖拽小球 和 底层的 布局
    public void setView(View view, RelativeLayout buttomlayout) {
        this.view = view;
        // invalidate();
        this.layout_buttom = buttomlayout;
        leftButtomView = buttomlayout.findViewById(R.id.leftimg);
        mPlayLauout = (RelativeLayout) layout_buttom
                .findViewById(R.id.playLayout);
        mNextLayout = (RelativeLayout) layout_buttom
                .findViewById(R.id.nextLayout);
        mIfLikeLayout = (RelativeLayout) layout_buttom
                .findViewById(R.id.likeLayout);
        mDeleteLayout = (RelativeLayout) layout_buttom
                .findViewById(R.id.deleteLayout);
        imgButtomView = (RoundAngleImageView) layout_buttom
                .findViewById(R.id.img4_buttom);
        img4 = (RoundAngleImageView) view.findViewById(R.id.img4);
        img3 = (ImageView) view.findViewById(R.id.img3);
        imgButtom3 = (ImageView) layout_buttom.findViewById(R.id.imgButtom3);

        mPlayBtn = (ImageView) layout_buttom.findViewById(R.id.playOrStop);
        mPlayNext = (ImageView) layout_buttom.findViewById(R.id.next);
        mIfLike = (ImageView) layout_buttom.findViewById(R.id.like);
        mDeleteSong = (ImageView) layout_buttom.findViewById(R.id.delete);
        textSongName = (TextView) layout_buttom.findViewById(R.id.name_song);
        textPersonName = (TextView) layout_buttom
                .findViewById(R.id.name_person);

        mPlayLauout.setOnClickListener(this);
        mNextLayout.setOnClickListener(this);
        mIfLikeLayout.setOnClickListener(this);
        mDeleteLayout.setOnClickListener(this);
        leftButtomView.setOnClickListener(this);
        // 初始化
//        initAnimation();
        initAnimator();
        isRolate = DianTaiService.getInstance().isIsplaying();
        LogUtils.d("1111isRolate==" + isRolate);
        if (isRolate) {
            // invalidate();
//            animatorSet.start();
            startRolote();
        } else {
//            animatorSet.cancel();
            cancleRolote();
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (isTrans) {
            view.layout(l, t, r, b);
        }
    }

    private boolean isRight = false;
    private boolean hasRelease;// 手指是否离开屏幕
    private float viewReleasY;

    private class ViewDragCallback extends ViewDragHelper.Callback {

        /**
         * 尝试捕获子view，一定要返回true
         *
         * @param View
         * child 尝试捕获的view
         * @param int pointerId 指示器id？ 这里可以决定哪个子view可以拖动
         */
        float x1, x2, y1, y2;
        private boolean once;
        private long time1;
        private long time2;

        @Override
        public boolean tryCaptureView(View view, int pointerId) {
            LogUtils.d("tryCaptureView--isTrans=" + isTrans);
            return isTrans;
        }

        /**
         * 处理水平方向上的拖动
         *
         * @param child 被拖动到view
         * @param left  移动到达的x轴的距离
         * @param dx    建议的移动的x距离
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            System.out.println("left = " + left + ", dx = " + dx);
            Log.d("onViewDragStateChanged", "dx=" + dx);
            // 两个if主要是为了让viewViewGroup里
            if (getPaddingLeft() > left) {
                return getPaddingLeft();
            }

            if (getWidth() - child.getWidth() < left) {
                return getWidth() - child.getWidth();
            }
            return left;
        }

        /**
         * 处理竖直方向上的拖动
         *
         * @param child 被拖动到view
         * @param top   移动到达的y轴的距离
         * @param dy    建议的移动的y距离
         */
        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            // 两个if主要是为了让viewViewGroup里
            if (getPaddingTop() > top) {
                return getPaddingTop();
            }

            if (getHeight() - child.getHeight() < top) {
                return getHeight() - child.getHeight();
            }
            Log.d("onViewDragStateChanged", "dy=" + dy);
            System.out.println("top = " + top + ", dy = " + dy);
            return top;

        }

        /**
         * 当拖拽到状态改变时回调
         *
         * @params 新的状态
         */
        @SuppressLint("NewApi")
        @Override
        public void onViewDragStateChanged(int state) {
            switch (state) {
                case ViewDragHelper.STATE_DRAGGING: // 正在被拖动
                    hasRelease = false;
                    if (!once) {
                        time1 = System.currentTimeMillis();
                        x1 = view.getX();
                        y1 = view.getY();
                        once = true;
                    }
                    Log.d("onViewDragStateChanged", "STATE_DRAGGING");
                    break;
                case ViewDragHelper.STATE_IDLE: // view没有被拖拽或者 正在进行fling/snap
                    LogUtils.d("onViewDragStateChanged", "STATE_IDLE");
                    x2 = view.getX();
                    y2 = view.getY();
                    time2 = System.currentTimeMillis();
                    if (Math.abs(x2 - x1) < 10 && Math.abs(y2 - y1) < 10
                            && time2 - time1 < 500) {
                        Log.d("onViewDragStateChanged", "onclick");
                        if (view.getVisibility() == View.VISIBLE) {
                            mHandler.removeMessages(0);
                            mHandler.removeMessages(1);
                            mHandler.sendEmptyMessage(1);
                            // showButtom();
                        }

                    }
                    once = false;
                    isTrans = false;
                    if (isRight) {
                        getImgRight().setVisibility(View.VISIBLE);
                    } else {
                        getImg().setVisibility(View.VISIBLE);
                    }
                    getImg5().setVisibility(View.VISIBLE);
                    getImg6().setVisibility(View.VISIBLE);
                    // view.setBackgroundResource(R.drawable.radio_bg_0_bg);
                    params = (LayoutParams) view.getLayoutParams();
                    if (!hasRelease) {
                        // 未执行viewrelease方法时候调用
                        params.bottomMargin = (int) (DrageLayout.this.getHeight()
                                - view.getHeight() - view.getY());
                        if (view.getX() > DrageLayout.this.getWidth() / 2) {
                            getImgRight().setVisibility(View.VISIBLE);
                            getImg().setVisibility(View.INVISIBLE);
                            params.leftMargin = (int) (DrageLayout.this.getWidth() - view
                                    .getWidth());
                        } else {
                            getImgRight().setVisibility(View.INVISIBLE);
                            getImg().setVisibility(View.VISIBLE);
                            params.leftMargin = 0;
                        }
                    } else {
                        // 已viewrelease方法时候调用
                        params.bottomMargin = (int) (DrageLayout.this.getHeight()
                                - view.getHeight() - viewReleasY);
                        if (isRight) {
                            params.leftMargin = (int) (DrageLayout.this.getWidth() - view
                                    .getWidth());
                        } else {
                            params.leftMargin = 0;
                        }
                    }
                    view.setLayoutParams(params);
                    getDrageLayoutPositionToLocal();
                    break;
                case ViewDragHelper.STATE_SETTLING: // fling完毕后被放置到一个位置
                    LogUtils.d("onViewDragStateChanged", "STATE_SETTLING");
                    break;
            }
            super.onViewDragStateChanged(state);

        }

        // 手指释放的时候回调
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            // mAutoBackView手指释放时可以自动回去
            LogUtils.d("onViewReleased--isTrans=" + isTrans);
            // if (isTrans) {
            viewReleasY = view.getY();

            if (view.getX() + view.getWidth() / 2 < DrageLayout.this.getWidth() / 2) {
                isRight = false;
                mDragHelper.settleCapturedViewAt(0, (int) view.getY());
            } else {
                mDragHelper.settleCapturedViewAt(
                        (int) (DrageLayout.this.getWidth() - view.getWidth()),
                        (int) view.getY());
                isRight = true;
            }
            // } else {
            // if (otherX + view.getWidth() / 2 < DrageLayout.this.getWidth() /
            // 2) {
            // isRight = false;
            // mDragHelper.settleCapturedViewAt(0, (int) otherY);
            // } else {
            // mDragHelper.settleCapturedViewAt((int) (DrageLayout.this
            // .getWidth() - view.getWidth()), (int) otherY);
            // isRight = true;
            // }
            // }
            LogUtils.d("onViewReleasedisRight==" + isRight);
            hasRelease = true;
            invalidate();
            // mDragHelper.settleCapturedViewAt(0, (int) view.getY());
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top,
                                          int dx, int dy) {
            // TODO Auto-generated method stub
            if (Math.abs(dx) < 50 && Math.abs(dy) < 50) {
                Log.d("onViewDragStateChanged", "onViewPositionChanged");
            }
            super.onViewPositionChanged(changedView, left, top, dx, dy);
        }
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            // LogUtils.d("computeScroll");
            invalidate();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_DOWN:
                mDragHelper.cancel(); // 相当于调用 processTouchEvent收到ACTION_CANCEL
                break;
        }

        /**
         * 检查是否可以拦截touch事件 如果onInterceptTouchEvent可以return true 则这里return true
         */
        // return false;
        return isTrans;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /**
         * 处理拦截到的事件 这个方法会在返回前分发事件
         */
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (event.getX() > view.getLeft() && event.getX() < view.getRight()
                    && event.getY() > view.getTop()
                    && event.getY() < view.getBottom()) {
                isTrans = true;
                getImg().setVisibility(View.INVISIBLE);
                getImgRight().setVisibility(View.INVISIBLE);
                getImg5().setVisibility(View.INVISIBLE);
                getImg6().setVisibility(View.INVISIBLE);
                // view.setBackgroundResource(R.color.transparent);
            } else {
                otherX = view.getX();
                otherY = view.getY();
                isTrans = false;
            }

        }

        // LogUtils.d("DrageLayout onTouchEvent+isTrans=" + isTrans);
        if (isTrans) {
            mDragHelper.processTouchEvent(event);
        }
        return isTrans;
    }

    float x, y;
    private LayoutParams viewParams;
    private LayoutParams params;

    public void showButtom() {

        AnimatorSet set = new AnimatorSet();

        set.playTogether(ObjectAnimator.ofFloat(layout_buttom, "translationX",
                -layout_buttom.getWidth(), 0));

        // 动画周期为500ms
        layout_buttom.setVisibility(View.VISIBLE);
        view.setVisibility(View.INVISIBLE);
        set.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                // TODO Auto-generated method stub
                x = view.getX();
                y = view.getY();
                viewParams = (LayoutParams) view.getLayoutParams();
                //
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // TODO Auto-generated method stub
                LogUtils.d("onAnimationEnd");
                hideButtomDelay();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub
                hideButtomDelay();
                LogUtils.d("onAnimationEnd");
            }
        });
        set.setDuration(500).start();
    }

    public void hideButtomDelay() {
        mHandler.removeMessages(0);
        mHandler.removeMessages(1);
        mHandler.sendEmptyMessageDelayed(0, 1000 * 5);
    }

    public void backButtom() {
        if (isAnmShowing) {
            return;
        }
        LogUtils.d("backButtom");
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(layout_buttom, "translationX",
                0, -layout_buttom.getWidth()));
        // 动画周期为500ms
        set.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                // TODO Auto-generated method stub
                isAnmShowing = true;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub
            }

            @SuppressLint("NewApi")
            @Override
            public void onAnimationEnd(Animator animation) {
                // TODO Auto-generated method stub
                viewParams.leftMargin = (int) x;
                // viewParams.topMargin=(int) y;
                float margionbuttom = DrageLayout.this.getHeight() - y
                        - view.getHeight();
                LogUtils.d("x===" + x + "y======" + y + "margionbuttom=="
                        + margionbuttom);
                viewParams.bottomMargin = (int) margionbuttom;
                view.setLayoutParams(viewParams);
                // 上边是还原小球动画前的位置
                view.setVisibility(View.VISIBLE);
                layout_buttom.setVisibility(View.INVISIBLE);
                isAnmShowing = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                view.setVisibility(View.VISIBLE);
                // view.setLayoutParams(viewParams);
                layout_buttom.setVisibility(View.INVISIBLE);
                isAnmShowing = false;
            }
        });
        set.setDuration(500).start();
    }

    // 0 play状态 1 暂停 2 停止 3 上一首 4 下一首 5 喜欢
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playLayout:
                LogUtils.d("play-----");
                mPlayLauout.setEnabled(false);
                mHandler.sendEmptyMessageDelayed(2, 500);
                if (isPlaying) {
                    sendBroadcastToService(1);
                } else {
                    sendBroadcastToService(0);
                }
                PreferencesUtils.saveBooleanPreferences(context, PreferencesUtils.TYPE_DIANTAI_TOGLE, !isPlaying);
                hideButtomDelay();
                break;
            case R.id.nextLayout:
                LogUtils.d("next-----");
                mPlayNext.setEnabled(false);
                mHandler.sendEmptyMessageDelayed(3, 500);
                sendBroadcastToService(4);
                hideButtomDelay();
                break;
            case R.id.likeLayout:
                LogUtils.d("like-----");
                mIfLikeLayout.setEnabled(false);
                mHandler.sendEmptyMessageDelayed(4, 500);
                if (DianTaiService.getInstance().getCurrentMusicInfo() != null) {
//				if (DianTaiService.getInstance().getCurrentMusicInfo().getIslike().equals("1")) {
//					Utils.toast(getContext(), "已经喜欢，不能重复喜欢");
//				} else {
//				}
                    //取消喜欢接口更改
                    sendBroadcastToService(5);
                }
                hideButtomDelay();
                break;
            case R.id.deleteLayout:
                LogUtils.d("delete-----");
                mDeleteLayout.setEnabled(false);
                mHandler.sendEmptyMessageDelayed(5, 500);
                sendBroadcastToService(6);
                hideButtomDelay();
                break;
            case R.id.leftimg:
                mHandler.removeMessages(0);
                mHandler.sendEmptyMessage(0);
                break;
            default:
                break;
        }

    }

    // 0 play状态 1 暂停 2 停止 3 上一首 4 下一首 5 喜欢 6删除
    protected void sendBroadcastToService(int state) {

        Intent intent = new Intent();
        intent.setAction("com.modernsky.istv.MusicService");
        intent.putExtra("control", state);
        // 向后台Service发送播放控制的广播
        context.sendBroadcast(intent);
    }

    private boolean isPlaying;

    public class MyReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DianTaiService.ACTION_SERVICE)) {
                int showDialog = intent.getIntExtra("showDialog", 0);
                if (showDialog == 1) {
                    // 显示dialog
                    DialogTool.showNetDialog(DrageLayout.this.getContext());
                }

                isPlaying = intent.getBooleanExtra("state", false);
                LogUtils.d("isPlaying=" + isPlaying);

                // 喜欢的话更新图片
                int isDeteleOrTrue = intent.getIntExtra("isDeteleOrTrue", -1);
                if (isDeteleOrTrue == 1) {
                    mIfLike.setImageResource(R.drawable.radio_controlbar_1like_hl);
                } else if (isDeteleOrTrue == 3) {//取消喜欢
                    mIfLike.setImageResource(R.drawable.radio_controlbar_1like);
                }
                // 是否更新界面
                boolean isNeedUpdate = intent.getBooleanExtra("isNeedUpdate",
                        false);
                if (isNeedUpdate) {
                    updateMusic();

                } else {
                    if (isPlaying) {
                        if (!isRolate) {
//                            animatorSet.start();
                            startRolote();
                            isRolate = true;
                        }
                        mPlayBtn.setImageResource(R.drawable.radio_controlbar_pause);
                    } else {
                        if (isRolate) {
//                            animatorSet.cancel();
                            cancleRolote();
                            isRolate = false;
                        }
                        mPlayBtn.setImageResource(R.drawable.radio_controlbar_play);
                    }

                }

            }
        }
    }

}
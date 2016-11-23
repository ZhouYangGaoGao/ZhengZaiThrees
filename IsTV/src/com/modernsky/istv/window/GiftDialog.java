package com.modernsky.istv.window;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.modernsky.istv.R;
import com.modernsky.istv.acitivity.ShowActivity;
import com.modernsky.istv.adapter.CommonAdapter;
import com.modernsky.istv.adapter.ViewHolder;
import com.modernsky.istv.bean.GiftBean;
import com.modernsky.istv.tool.BitmapTool;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.view.RotateTextView;
import com.modernsky.istv.view.RoundAngleImageView;

import java.util.List;

/**
 * Created by zqg on 2016/3/11.
 */
public class GiftDialog extends Dialog implements View.OnClickListener {
    private ShowActivity showActivity;
    private GridView gridView;
    private View line;
    private RoundAngleImageView giftImg;
    private List<GiftBean> giftList;
    private CommonAdapter<GiftBean> mGiftAdapter;
    private static final int SendGift = 3;
    private boolean gridItemClicked = true;
    private int giftNum;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SendGift:
                    gridItemClicked = true;
                    setSendGift(giftNum);
                    giftNum = 0;
                    break;
            }
        }
    };

    public GiftBean getmCurrentGiftBean() {
        return mCurrentGiftBean;
    }

    public void setmCurrentGiftBean(GiftBean mCurrentGiftBean) {
        this.mCurrentGiftBean = mCurrentGiftBean;
    }

    private GiftBean mCurrentGiftBean;
    private boolean isSendGift;
    private long giftTime;
    private int choisegiftNum = -1;
    private View giftView;
    private int giftBuyMb;
    private RelativeLayout giftNumLayout;

    private OnGiftListenner listenner;

    public TextView getLeftMbs() {
        return leftMbs;
    }

    public void setLeftMbs(TextView leftMbs) {
        this.leftMbs = leftMbs;
    }

    private TextView leftMbs;

    private void initGiftView(View view, int num) {
        if (giftView != null) {
            this.giftView.setBackgroundResource(R.color.mediacontroller_bg);
        }
        choisegiftNum = num;
        this.giftView = view;
        this.giftView.setBackgroundResource(R.color.white_33alpha);
    }

    public void clearGiftView() {
        if (giftView != null) {
            choisegiftNum = -1;
            this.giftView.setBackgroundResource(R.color.mediacontroller_bg);
        }
    }

    public GiftDialog(ShowActivity context, List<GiftBean> giftList, OnGiftListenner listenner) {
        super(context, R.style.peopleInfo_dialog);
        this.showActivity = context;
        this.giftList = giftList;
        this.listenner = listenner;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_gift);
        initView();
        initAdapter();
        initListenner();
    }

    private void initListenner() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.d("gridItemClicked===" + gridItemClicked);
                if (gridItemClicked) {
                    initGiftView(view, position);
                    mCurrentGiftBean = giftList.get(position);
                    LogUtils.d("ImgUrl()===" + mCurrentGiftBean.getImgUrl());
                    BitmapTool.getInstance().getAdapterUitl().display(giftImg, mCurrentGiftBean.getImgUrl());
                    isSendGift = true;
                    mHandler.removeMessages(SendGift);
                }
            }
        });
    }

    public void clearData() {
        giftNum = 0;
        isSendGift = false;
        giftImg.setImageResource(R.drawable.but_liwu);
        clearGiftView();
        leftMbs.setText("(剩余M豆:" + showActivity.getUserBean().getMbCount() + ")");
    }

    private void initAdapter() {
        if (mGiftAdapter == null) {
            mGiftAdapter = new CommonAdapter<GiftBean>(getContext(), giftList, R.layout.item_gite_gridview) {
                @Override
                public void convert(ViewHolder helper, GiftBean item) {
                    if (choisegiftNum == helper.getPosition()) {
                        helper.getConvertView().setBackgroundResource(R.color.white_33alpha);
                    }
                    RotateTextView jiaoText = helper.getView(R.id.item_rotate_textview);
                    ImageView giftImg = helper.getView(R.id.img_item_giftgrid);
                    BitmapTool.getInstance().getAdapterUitl().display(giftImg, item.getImgUrl());
                    if (item.getMb() > 0) {
                        jiaoText.setBackgroundResource(R.drawable.icon_md_selector);
                        helper.setText(R.id.item_rotate_textview, item.getMb() + "");
                    } else {
                        jiaoText.setBackgroundResource(R.drawable.shape_greymb);
                        helper.setText(R.id.item_rotate_textview, "免费");
                    }
                    helper.setText(R.id.tv_name_gift, item.getName());
                }
            };
            gridView.setAdapter(mGiftAdapter);
        } else {
            mGiftAdapter.notifyDataSetChanged();
        }
    }

    private void initView() {
        this.setCanceledOnTouchOutside(true);
        gridView = (GridView) findViewById(R.id.gridView_mediacontroller);
        line = findViewById(R.id.line_below_grid);
        giftImg = (RoundAngleImageView) findViewById(R.id.btn_sendgift_media);
        giftImg.setOnClickListener(this);
        giftNumLayout = (RelativeLayout) findViewById(R.id.layout_giftnum);
        leftMbs = (TextView) findViewById(R.id.tv_num__MBconsume);
        leftMbs.setText("(剩余M豆:" + showActivity.getUserBean().getMbCount() + ")");
    }


    public interface OnGiftListenner {
        void onSendGift(int giftNum); //买全部再送

        void onInitBuyMbGift();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sendgift_media:
                if (isSendGift) {
                    gridItemClicked = false;
                    giftNum++;
                    if (((System.currentTimeMillis() - giftTime) < 500)) {
                        mHandler.removeMessages(SendGift);
                        mHandler.sendEmptyMessageDelayed(SendGift, 500);
                    } else {
                        mHandler.sendEmptyMessageDelayed(SendGift, 500);
                    }
                    giftTime = System.currentTimeMillis();
                }
                break;
        }

    }


    private void setSendGift(int giftNum) {
//        int total = Integer.parseInt(giftCount);
        int total = giftNum;
        LogUtils.d("setSendGift==" + giftNum);
        if (mCurrentGiftBean == null) {
            return;
        }
        // 礼物不够的情况下
        LogUtils.d("(mCurrentGiftBean.getType()==" + (mCurrentGiftBean.getType()));

        giftBuyMb = giftNum * mCurrentGiftBean.getMb();
        switch (mCurrentGiftBean.getType()) {
            case 1://普通礼物
                if (mCurrentGiftBean.getMb() == 0) {// 礼物价格是0的情况下，直接赠送，不需购买
                    listenner.onSendGift(giftNum);
                    doAnimate();
//                sendGift(true);
                } else if (showActivity.getUserBean().getMbCount() >= giftBuyMb) {
                    // http://test.zhengzai.tv/m/show/gift/buy?userId=555eda02af4d3b1c929294c9&giftId=5565569ce5a681c9f7572b2b&count=30
                    int myMb = showActivity.getUserBean().getMbCount();
                    LogUtils.d("myMb==" + showActivity.getUserBean().getMbCount() + "giftBuyMb==" + giftBuyMb);
                    showActivity.getUserBean().setMbCount(myMb - giftBuyMb);
                    listenner.onSendGift(giftNum);
//                        listenner.onCastMbBuyGift(total
//                                - mCurrentGiftBean.getUserHaveCount(), total);
                    doAnimate();

                    // 没有足够的金币
                } else {
                    listenner.onInitBuyMbGift();
                }

                break;
            case 2: //草莓
                int myPower = showActivity.getUserBean().getStrawCount();
                LogUtils.d("mypowe");

                if (myPower < total) {
                    // 有足够的power
                    LogUtils.d("myPower==" + myPower + "total==" + total);
                    giftBuyMb = (total - myPower)
                            * mCurrentGiftBean.getMb();
                    if (mCurrentGiftBean.getMb() == 0) {// 礼物价格是0的情况下，直接赠送，不需购买
                        listenner.onSendGift(giftNum);
                        doAnimate();
//                sendGift(true);
                    } else if (showActivity.getUserBean().getMbCount() >= giftBuyMb) {
                        LogUtils.d("myMb==" + showActivity.getUserBean().getMbCount() + "giftBuyMb==" + giftBuyMb);
                        // http://test.zhengzai.tv/m/show/gift/buy?userId=555eda02af4d3b1c929294c9&giftId=5565569ce5a681c9f7572b2b&count=30
                        int myMb = showActivity.getUserBean().getMbCount();
                        showActivity.getUserBean().setMbCount(myMb - giftBuyMb);
                        showActivity.getUserBean().setStrawCount(0);
                        listenner.onSendGift(giftNum);
//                        listenner.onCastMbBuyGift(total
//                                - myPower, total);
                        doAnimate();
                        // 没有足够的金币
                    } else {
                        listenner.onInitBuyMbGift();
                    }
                    // 有礼物
                } else {
                    showActivity.getUserBean().setStrawCount(myPower - total);
                    listenner.onSendGift(giftNum);
                    doAnimate();
//            sendGift(true);
                }
                break;
        }
    }

    private void doAnimate() {
        doAnimateOpen("", giftNum);
        giftNum = 0;
    }


    @SuppressLint("NewApi")
    private void doAnimateOpen(String url, int total) {
        for (int j = 1; j <= total; j++) {
            LogUtils.d("total==" + total);
            final int h = j;
            LogUtils.d("h=" + h);

            final TextView textView = new TextView(showActivity);
            LogUtils.d("imgurl=" + url);
            textView.setText("+ " + h);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(Color.parseColor("#3ee5b0"));
//            textView.setTextSize(22);
//            BitmapTool.getInstance().showLocalView(textView, url);
            giftNumLayout.postDelayed(new Runnable() {
                // android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                @Override
                public void run() {
                    final android.widget.RelativeLayout.LayoutParams params = new android.widget.RelativeLayout.LayoutParams(
                            giftNumLayout.getWidth() / 2, giftNumLayout.getWidth() / 2);
                    params.addRule(RelativeLayout.CENTER_HORIZONTAL);

                    // params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    // int k = (int) (Math.random() * 8);
                    // LogUtils.d("Math.random()10=" + k);
                    // params.leftMargin = 10 * k;
                    // textView.setLayoutParams(params);
                    giftNumLayout.addView(textView, params);
                    // show_gift.addView(textView);


                    final AnimatorSet set = new AnimatorSet();
                    set.addListener(new Animator.AnimatorListener() {

                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            giftNumLayout.removeView(textView);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            giftNumLayout.removeView(textView);

                        }
                    });
                    ObjectAnimator ofFloat = ObjectAnimator
                            .ofFloat(textView, "translationY", (int) (giftNumLayout
                                            .getBottom() - giftNumLayout.getTop()),
                                    (int) ((giftNumLayout.getBottom() - giftNumLayout
                                            .getTop()) / 3));
                    ofFloat.setDuration(1000);
                    ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(textView, "scaleX", 2f, 0.5f);
                    ofFloat2.setDuration(1000);
                    ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(textView, "scaleY", 2f, 0.5f);
                    ofFloat3.setDuration(1000);

//                    int widthLength = (show_gift.getRight() - show_gift
//                            .getLeft()) / 8;
//                    int location = (int) (Math.random() * 6);
//                    ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(textView,
//                            "translationX", widthLength * (location + 1),
//                            location * widthLength);
//                    ofFloat2.setRepeatMode(ObjectAnimator.REVERSE);
//                    ofFloat2.setRepeatCount(3);
//                    ofFloat2.setDuration(500);
//                    // ofFloat2.setInterpolator(new AccelerateInterpolator());
//                    ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(textView,
//                            "alpha", 0f, 1);
//                    ofFloat3.setDuration(2000);
                    set.playTogether(ofFloat, ofFloat2, ofFloat3);
                    set.setInterpolator(new LinearInterpolator());// 动画速度逐步减小
                    // 动画周期为500ms
                    // set.setDuration(1 * 1000).start();
                    set.start();
                }
            }, j * 300);

        }
    }


}

package com.modernsky.istv.view;

import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.adapter.CommonAdapter;
import com.modernsky.istv.adapter.ViewHolder;
import com.modernsky.istv.bean.LiveInfo;
import com.modernsky.istv.bean.PeopleIdAndPicInfo;
import com.modernsky.istv.bean.UserEntity;
import com.modernsky.istv.bean.XiuchanMessage;
import com.modernsky.istv.tool.BitmapTool;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by zqg on 2016/2/23.
 */
public class ShowNewMediacontroler extends FrameLayout implements View.OnClickListener {

    private static final int sDefaultTimeout = Integer.MAX_VALUE;
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;
    //    private static final int SendGift = 3;
//    private View giftView;
    private boolean hasFormMenu;
    private boolean hasWuTai;
    private boolean isLocked;
    private RelativeLayout mVideoInfoLayout;//右上角 视频信息的布局  有举报 清晰度  舞台切换
    private TextView mQxdText, mWUtaiText, mJiemudanText; //右上角 清晰度  和 舞台
    private RelativeLayout mAnchorInfoLayout;//左上角 主播信息的布局
//    private long giftTime;

    public ImageView getAttentionImg() {
        return attentionImg;
    }

    public void setAttentionImg(ImageView attentionImg) {
        this.attentionImg = attentionImg;
    }

    private ImageView attentionImg;




    private boolean isShow;

    public List<PeopleIdAndPicInfo> getPicList() {
        return picList;
    }

    public void setPicList(List<PeopleIdAndPicInfo> picList) {
        this.picList = picList;
    }

    private List<PeopleIdAndPicInfo> picList;
    private List<XiuchanMessage> mMessList;
    private boolean isLandscape;//是否是横屏
    private CommonAdapter<XiuchanMessage> mesAdapter;
    private CommonAdapter<PeopleIdAndPicInfo> picAdapter;
    private CommonAdapter<LiveInfo> mWutaiAdapter;
//    private CommonAdapter<String> mQXDAdapter;
    private ImageView mChatBtn;//聊天按钮
    private ImageView mReportBtn;//举报按钮
//    private int position = 0;
    private ImageView bacImgBtn;


    private BaseActivity mActivity;
    private ImageView mLockButton;//锁屏按钮
    private ImageView mChangeSizeBtn; //变换全屏半屏按钮
    private ImageView mShareBtn; // 分享按钮
    private ImageView mZanBtn; // 赞按钮
    private RoundAngleImageView mGiftBtn; // 礼物按钮
    private TextView mTimeLeft;

    public TextView getmAudienceNums() {
        return mAudienceNums;
    }

    public void setmAudienceNums(TextView mAudienceNums) {
        this.mAudienceNums = mAudienceNums;
    }

    private TextView mAudienceNums;// 剩余时间， 我的名字， 我的头像， 我的观众的个数
    private ImageView mImg;//主播头像
    private ImageView verticalImg;// 主播竖屏头像

    public TextView getVerticalTimeText() {
        return verticalTimeText;
    }

    public void setVerticalTimeText(TextView verticalTimeText) {
        this.verticalTimeText = verticalTimeText;
    }

    private TextView verticalTimeText;//竖屏 剩余时间
    private RelativeLayout verticalAnchorInfo;//竖屏 的主播布局

    private HorizontalListView mAudienceList;
    private ListView mMessageList;
    private ListView mQxdList;//清晰度的list
    private ListView mWutaiList;//舞台的列表
//    private List<String> mQXDInfoList;
    private List<LiveInfo> liveInfos;
    private boolean shouldDoControllerAnimation;

    public TextView getCharRoomName() {
        return charRoomName;
    }

    private TextView charRoomName;

    public TextView getLookforwadVideoName() {
        return lookforwadVideoName;
    }

    public void setLookforwadVideoName(TextView lookforwadVideoName) {
        this.lookforwadVideoName = lookforwadVideoName;
    }

    private TextView lookforwadVideoName;

    private long mDuration;
    private boolean liveMode;

    public TextView getLeftMBNumText() {
        return leftMBNumText;
    }

    public void setLeftMBNumText(TextView leftMBNumText) {
        this.leftMBNumText = leftMBNumText;
    }

    private TextView leftMBNumText;
    private RelativeLayout buttomPlayLayout;//回看底部布局
    private LinearLayout buttomLiveLayout;//直播底部布局
    private ImageButton buttomPlayBtn;//会看的播放暂停
    private TextView butomPlayTimeText;//底部点播计时显示
    private ImageView buttomPlayZanImg;//点播底部赞的按钮
    //    private SeekBar mSeekBar;//点播的进度条
    private ImageView buttomPlayChatImg;//底部点播评论按钮
    private ImageView buttomPlayFangdaImg;//底部切换屏幕的按钮
    private RelativeLayout giftNumLayout;//数字的动画
//    private boolean mDragging;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            long pos;
            switch (msg.what) {
                case FADE_OUT:
                    hide();
                    break;
                case SHOW_PROGRESS:
//                    LogUtils.d("SHOW_PROGRESS---");
                    pos = setProgress();
                    if (isShow()) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                        updatePausePlay();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        hide();
        super.onDetachedFromWindow();
    }


    private boolean once;
    private SeekBar mProgress;
    private Context mContext;
    private AudioManager mAM;


    public ImageButton getButtomPlayBtn() {
        return buttomPlayBtn;
    }

    public TextView getButomPlayTimeText() {
        return butomPlayTimeText;
    }

    public ImageView getButtomPlayZanImg() {
        return buttomPlayZanImg;
    }


    public ImageView getButtomPlayChatImg() {
        return buttomPlayChatImg;
    }

    public ImageView getButtomPlayFangdaImg() {
        return buttomPlayFangdaImg;
    }

    public RoundAngleImageView getmGiftBtn() {
        return mGiftBtn;
    }

    public ImageView getmZanBtn() {
        return mZanBtn;
    }

    public ImageView getmShareBtn() {
        return mShareBtn;
    }

    public ImageView getmChangeSizeBtn() {
        return mChangeSizeBtn;
    }

    public ImageView getmLockButton() {
        return mLockButton;
    }


    public TextView getmWUtaiText() {
        return mWUtaiText;
    }

    public TextView getmQxdText() {
        return mQxdText;
    }

    public boolean isLandscape() {
        return isLandscape;
    }

    public ImageView getmChatBtn() {
        return mChatBtn;
    }

    public ImageView getmReportBtn() {
        return mReportBtn;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setIsLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }


    public void setmMessageList(ListView mMessageList) {
        this.mMessageList = mMessageList;
    }


    public ListView getmMessageList() {
        return mMessageList;
    }

    public TextView getmTimeLeft() {
        return mTimeLeft;
    }

    public void show() {
        show(sDefaultTimeout);
    }

    public void hide() {
        if (shouldDoControllerAnimation) {
            isShow = false;
            fadeOut(this);
//            setVisibility(View.GONE);
        }
    }

    private boolean initController(Context context) {
        mContext = context;
        isShow = true;
        shouldDoControllerAnimation = true;
        picList = new ArrayList<PeopleIdAndPicInfo>();
        mAM = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        return true;
    }

    private void fadeIn(final View view) {
        shouldDoControllerAnimation = false;
        Animation mySpaceAnim = AnimationUtils.loadAnimation(this.getContext(), R.anim.fade_in);
        mySpaceAnim.setInterpolator(new LinearInterpolator());
        view.startAnimation(mySpaceAnim);
        view.setVisibility(View.VISIBLE);
        mySpaceAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                shouldDoControllerAnimation = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void fadeOut(final View view) {
        shouldDoControllerAnimation = false;
        Animation mySpaceAnim = AnimationUtils.loadAnimation(this.getContext(), R.anim.fade_out);
        mySpaceAnim.setInterpolator(new LinearInterpolator());
        view.startAnimation(mySpaceAnim);
        mySpaceAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
                shouldDoControllerAnimation = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public HorizontalListView getmAudienceList() {
        return mAudienceList;
    }

    public ImageView getmImg() {
        return mImg;
    }

    private View mRoot;
    private boolean mFromXml;

    public ShowNewMediacontroler(Context context) {
        super(context);
        isShow = true;
    }

    public ShowNewMediacontroler(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRoot = this;
        mFromXml = true;

        initController(context);
    }

    @Override
    protected void onFinishInflate() {
        if (mRoot != null) {
            initControllerView(mRoot);
        }
        super.onFinishInflate();
    }

    public void updateController() {
        LogUtils.d("updateController");
        updatePausePlay();
        setProgress();
        if (!once) {
            show(sDefaultTimeout);
            once = true;
        }
    }

    public void initAnchorEntity(UserEntity anchorEntity) {
        if (anchorEntity == null) {
            return;
        }
        BitmapTool.getInstance().getAdapterUitl().display(mImg, anchorEntity.getFaceUrl());
        BitmapTool.getInstance().getAdapterUitl().display(verticalImg, anchorEntity.getFaceUrl());
        if (liveMode) {
            mTimeLeft.setText("直播中  00:00:00");
        } else {
            mTimeLeft.setText(anchorEntity.getUserName());
        }
        verticalTimeText.setText("直播中  00:00:00");
        anchorId = anchorEntity.getId();
        if (anchorEntity.getVideo() == null) {
            return;
        }

//        if (anchorEntity.getIsAttention() == 0) {
////            attentionImg.setImageResource(R.drawable.icon_hearts_nor);
//        } else {
////            attentionImg.setImageResource(R.drawable.icon);
//        }
//        charRoomName.setText(anchorEntity.getHerald().getName());
    }

    public void show(int timeout) {
        LogUtils.d("show");
        if (!isShow()) {
            if (buttomPlayBtn != null) {
                buttomPlayBtn.requestFocus();
            }
            if (mFromXml) {
//                setVisibility(View.VISIBLE);
                if (shouldDoControllerAnimation) {
                    isShow = true;
                    fadeIn(this);
//            setVisibility(View.VISIBLE);
                }
                isShow = true;
            }

        }
        updatePausePlay();
        mHandler.sendEmptyMessage(SHOW_PROGRESS);
        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT),
                    timeout);
        }
    }


    private void updatePausePlay() {
        if (mRoot == null || buttomPlayBtn == null)
            return;
        if (listenner != null && listenner.isPlaying())
            buttomPlayBtn.setImageResource(R.drawable.mediacontroller_pause);
        else
            buttomPlayBtn.setImageResource(R.drawable.mediacontroller_play);
    }

    public void setMode(boolean isLiveMode) {
        liveMode = isLiveMode;
        if (liveMode) {

            buttomPlayLayout.setVisibility(GONE);
            buttomLiveLayout.setVisibility(VISIBLE);
        } else {

            buttomPlayLayout.setVisibility(VISIBLE);
            buttomLiveLayout.setVisibility(GONE);
        }
    }



    private void initControllerView(View rootView) {
        charRoomName = (TextView) findViewById(R.id.tv_message_anchor_livemedia);
        buttomPlayLayout = (RelativeLayout) rootView.findViewById(R.id.ll_look_forword);
        buttomLiveLayout = (LinearLayout) rootView.findViewById(R.id.ll_live);
        buttomPlayBtn = (ImageButton) rootView.findViewById(R.id.im_play_or_pause);
        butomPlayTimeText = (TextView) rootView.findViewById(R.id.tv_video_time);
        buttomPlayZanImg = (ImageView) rootView.findViewById(R.id.btn_zan_lookforward);
//        mSeekBar = (SeekBar) rootView.findViewById(R.id.seek_video);

//        rootView.findViewById(R.id.btn_zan_lookforward).setOnClickListener(this);
        lookforwadVideoName=(TextView) rootView.findViewById(R.id.tv_video_name);
        buttomPlayChatImg = (ImageView) rootView.findViewById(R.id.btn_chat_lookforward);
        buttomPlayFangdaImg = (ImageView) rootView.findViewById(R.id.btn_fangda_lookforward);
        buttomPlayChatImg.setOnClickListener(this);
        buttomPlayFangdaImg.setOnClickListener(this);
        buttomPlayZanImg.setOnClickListener(this);
        buttomPlayBtn.setOnClickListener(this);
        attentionImg = (ImageView) findViewById(R.id.isAttention);

        mProgress = (SeekBar) rootView.findViewById(R.id.seek_video);
        mProgress.setOnSeekBarChangeListener(mSeekListener);
        mProgress.setMax(1000);
        //上边主要是点播下面的控制器

        mQxdText = (TextView) rootView.findViewById(R.id.tv_qxd_mediacontroller);
        mQxdText.setVisibility(View.GONE);
        mJiemudanText = (TextView) rootView.findViewById(R.id.tv_jiemudan_mediacontroller);
        mWUtaiText = (TextView) rootView.findViewById(R.id.tv_wutai_mediacontroller);
        mQxdList = (ListView) rootView.findViewById(R.id.lv_qxd_mediacontroller);
        mWutaiList = (ListView) rootView.findViewById(R.id.lv_wutai_mediacontroller);
//        mQxdText.setOnClickListener(this);
        mJiemudanText.setOnClickListener(this);
        mWUtaiText.setOnClickListener(this);
        //上边主要是清晰度 舞台

        giftNumLayout = (RelativeLayout) rootView.findViewById(R.id.layout_giftnum);
        bacImgBtn = (ImageView) rootView.findViewById(R.id.img_btn_back);
        leftMBNumText = (TextView) rootView.findViewById(R.id.tv_num__MBconsume);
        mVideoInfoLayout = (RelativeLayout) rootView.findViewById(R.id.layout_info_video);
        mAnchorInfoLayout = (RelativeLayout) rootView.findViewById(R.id.rl_user_livemedia);
        mGiftBtn = (RoundAngleImageView) rootView.findViewById(R.id.btn_sendgift_media);
        mReportBtn = (ImageView) rootView.findViewById(R.id.btn_report_livemedia);
        mChangeSizeBtn = (ImageView) rootView.findViewById(R.id.btn_changesize_media);
        mShareBtn = (ImageView) rootView.findViewById(R.id.btn_share_media);
        mZanBtn = (ImageView) rootView.findViewById(R.id.btn_zan_media);
        mLockButton = (ImageView) rootView.findViewById(R.id.btn_lock_media);
        mChatBtn = (ImageView) rootView.findViewById(R.id.btn_chat_media);
        mTimeLeft = (TextView) rootView.findViewById(R.id.tv_name_livemedia);
        mImg = (ImageView) rootView.findViewById(R.id.img_user_livemedia);
        verticalImg = (ImageView) rootView.findViewById(R.id.img_user_livemedia_vertical);
        verticalTimeText = (TextView) rootView.findViewById(R.id.leftTime);
        verticalAnchorInfo = (RelativeLayout) rootView.findViewById(R.id.layout_vertical);

        mAudienceNums = (TextView) rootView.findViewById(R.id.tv_num_audiens);
        mAudienceList = (HorizontalListView) rootView.findViewById(R.id.audienList_livemedia);
        mMessageList = (ListView) rootView.findViewById(R.id.messList_livemedia);
        bacImgBtn.setOnClickListener(this);
        mGiftBtn.setOnClickListener(this);
        mChangeSizeBtn.setOnClickListener(this);
        mLockButton.setOnClickListener(this);
        mChatBtn.setOnClickListener(this);
        mReportBtn.setOnClickListener(this);
        mZanBtn.setOnClickListener(this);
        mShareBtn.setOnClickListener(this);
        mImg.setOnClickListener(this);
        //上面主要是直播的时候显示的
//        initPeoplePicListData();
        initTextColor();
        initAdapter();
        mAudienceList.setAdapter(picAdapter);
        setMessageList();
        initMessageListListenner();
        mAudienceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.d("setOnItemClickListener==" + position);
                listenner.onAnchorImgClicked(false, picList.get(position).getId());
            }
        });
        full(true);

    }

    private void initMessageListListenner() {
        mMessageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!TextUtils.isEmpty(mMessList.get(position).getFromUserName())) {
                    listenner.onChatClicked("回复: " + mMessList.get(position).getFromUserName(),mMessList.get(position).getFromUserName());
                }
            }
        });
    }

    List<ForegroundColorSpan> spans;

    private void initTextColor() {
        spans = new ArrayList<ForegroundColorSpan>();
        spans.add(new ForegroundColorSpan(ShowNewMediacontroler.this.getContext().getResources().getColor(R.color.text_rede8)));
        spans.add(new ForegroundColorSpan(ShowNewMediacontroler.this.getContext().getResources().getColor(R.color.text_purple9a)));
        spans.add(new ForegroundColorSpan(ShowNewMediacontroler.this.getContext().getResources().getColor(R.color.text_pinkff)));
        spans.add(new ForegroundColorSpan(ShowNewMediacontroler.this.getContext().getResources().getColor(R.color.text_yellowff)));
        spans.add(new ForegroundColorSpan(ShowNewMediacontroler.this.getContext().getResources().getColor(R.color.text_green05)));
        spans.add(new ForegroundColorSpan(ShowNewMediacontroler.this.getContext().getResources().getColor(R.color.text_blue03)));
        spans.add(new ForegroundColorSpan(ShowNewMediacontroler.this.getContext().getResources().getColor(R.color.text_grenb2)));
    }

    public void addMessage(XiuchanMessage message) {
        if (message.getGiftId() != null && !TextUtils.isEmpty(message.getPic())) {
            if (message.getCount() == 0) {
                return;
            }
        }
        if (message.getType() == 7 && message.getOnceTime() == 0) {
            return;
        }

        if (message.getType() == 9) {
            return;
        }


        if (mMessList.size() > 0 && message.getType() == 8) {
            if (TextUtils.isEmpty(message.getFromUserId())) {
                return;
            }
            if ((message.getFromUserId()).equals(mMessList.get(mMessList.size() - 1).getFromUserId()) && (mMessList.get(mMessList.size() - 1).getType() == 8)) {
                return;
            }
        }

        if (mMessList.size() >= 15) {
            mMessList.remove(0);
        }
        mMessList.add(message);
        setMessageList();
    }

    private String anchorId = "";

    private void setMessageList() {
        if (mMessList == null) {
            mMessList = new ArrayList<XiuchanMessage>();
        }
        if (mesAdapter == null) {
            mesAdapter = new CommonAdapter<XiuchanMessage>(this.getContext(), mMessList, R.layout.item_messagelist) {
                @Override
                public void convert(ViewHolder helper, final XiuchanMessage item) {
                    TextView tv_left, tv_right;
                    ImageView img;
                    View rootView;
                    RoundAngleImageView picImg;
                    picImg = helper.getView(R.id.img_user_livemedia);
                    rootView = helper.getView(R.id.rootview);
                    tv_left = helper.getView(R.id.text_left_item_message);
                    tv_right = helper.getView(R.id.tv_right);
                    img = helper.getView(R.id.img_gift);
                    tv_right.setVisibility(View.GONE);
                    img.setVisibility(View.GONE);
                    int count = (int) (Math.random() * 7);
                    int count2 = 6;
                    if (count > 0) {
                        count2 = count - 1;
                    }
                    Spannable word;

                    BitmapTool.getInstance().getAdapterUitl().display(picImg, item.getFromUserPic());
                    if (!TextUtils.isEmpty(item.getFromUserId())) {
                        picImg.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                listenner.onMessageImgClicked(item.getFromUserId());
                            }
                        });
                    }
                    // 礼物弹幕
                    if (item.getGiftId() != null && !TextUtils.isEmpty(item.getPic())) {
//            img_avatar.setImageResource(R.drawable.icon_lingdang);
                        img.setVisibility(View.VISIBLE);
                        tv_left.setText(item.getMsg());
                        int num = item.getCount();
                        LogUtils.d("message  list  controller" + item.toString());
                        String content = item.getGiftUserName() + " 送给选手";
                        BitmapTool.getInstance().getAdapterUitl().display(img, item.getPic());
                        tv_left.setTextColor(ShowNewMediacontroler.this.getContext().getResources().getColor(R.color.whitef0));
                        LogUtils.d("isMyself  content---" + content);
                        word = new SpannableString(content);
                        rootView.setBackgroundResource(R.drawable.bg_bluetanmu);
                        word.setSpan(spans.get(count), item.getGiftUserName().length(),
                                content.length(),
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tv_left.setText(word);

                        tv_right.setVisibility(VISIBLE);
                        tv_right.setText("X" + num + "!");
                        tv_right.setTextColor(spans.get(count).getForegroundColor());

                        return;
                    }


//                    String currentId = item.getFromUserId() + "";
                    LogUtils.d("message---" + item.toString());
                    switch (item.getType()) {
                        case 2:
                        case 3:
                        case 4:
                            String content0 = item.getMsg();
                            word = new SpannableString(content0);
                            word.setSpan(spans.get(count), 5,
                                    content0.length(),
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            rootView.setBackgroundResource(R.drawable.bg_bluetanmu);
                            tv_left.setText(word);

                            return;
                        case 8:
                            String content1 = item.getFromUserName() + " 进入了房间";
//                            LogUtils.d("content1===" + content1);
//                            tv_left.setTextColor(ShowNewMediacontroler.this.getContext().getResources().getColor(R.color.whitef0));
//                            word = new SpannableString(content1);
//                            word.setSpan(spans.get(count), 0,
//                                    content1.length(),
//                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                            rootView.setBackgroundResource(R.drawable.bg_bluetanmu);
                            tv_left.setText(content1);
                            return;
                        case 7:
                            String content2 = "选手增加了时长" + item.getOnceTime() / (1000 * 60) + "分";
//                            word = new SpannableString(content2);
//                            word.setSpan(spans.get(count), 0,
//                                    content2.length(),
//                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            rootView.setBackgroundResource(R.drawable.bg_bluetanmu);
                            tv_left.setText(content2);
                            return;
                        case 6:
                            String content3 = "选手结束了直播" ;
//                            word = new SpannableString(content2);
//                            word.setSpan(spans.get(count), 0,
//                                    content2.length(),
//                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            rootView.setBackgroundResource(R.drawable.bg_yellowtanmu);
                            tv_left.setText(content3);
                            return;
                    }


                    if (TextUtils.isEmpty(item.getFromUserId())) {
                        return;
                    }
                    //是否是我的
                    boolean isAnchor;
                    isAnchor = anchorId.equals(item.getFromUserId());
                    //是否是我的
                    String content = "";
                    if (isAnchor) {
                        rootView.setBackgroundResource(R.drawable.bg_yellowtanmu);
                    }
                    boolean isReturn =
                            !TextUtils.isEmpty(item.getToUserName());

                    if (!isReturn) {
                        content = item.getFromUserName() + " " + item.getMsg();
                        tv_left.setTextColor(ShowNewMediacontroler.this.getContext().getResources().getColor(R.color.whitef0));
                        word = new SpannableString(content);
                        word.setSpan(spans.get(count), item.getFromUserName().length() + 1,
                                content.length(),
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else {

                        content = item.getFromUserName() + " 回复 " + item.getToUserName() + " " + item.getMsg();
                        tv_left.setTextColor(ShowNewMediacontroler.this.getContext().getResources().getColor(R.color.whitef0));
                        word = new SpannableString(content);
                        word.setSpan(spans.get(count), item.getFromUserName().length(),
                                item.getFromUserName().length() + 4,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        word.setSpan(spans.get(count2),
                                item.getFromUserName().length() + 4 + item.getToUserName().length() + 1,
                                content.length(),
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    tv_left.setText(word);
                }
            };
            mMessageList.setAdapter(mesAdapter);
        } else {
            mesAdapter.notifyDataSetChanged();
        }
        if (mMessList.size() > 0) {
            mMessageList.setSelection(mMessList.size() - 1);
        }
    }


    public void initPeoplePicListData(List<PeopleIdAndPicInfo> list) {
        picList.clear();
        picList.addAll(list);
        picAdapter.notifyDataSetChanged();
    }

    private void initAdapter() {
        if (picList == null)
            picList = new ArrayList<PeopleIdAndPicInfo>();
        if (picAdapter == null) {
            picAdapter = new CommonAdapter<PeopleIdAndPicInfo>(this.getContext(), picList, R.layout.item_people_pic) {
                @Override
                public void convert(ViewHolder helper, final PeopleIdAndPicInfo item) {
                    RoundAngleImageView rounImg = helper.getView(R.id.img_item);
                    BitmapTool.getInstance().getAdapterUitl().display(rounImg, item.getFaceUrl());
                }
            };
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (event.getRepeatCount() == 0
                && (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
                || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_SPACE)) {
//            doPauseResume();
//            show(sDefaultTimeout);
//            if (mPauseButton != null)
//                mPauseButton.requestFocus();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP) {
//            if (mPlayer.isPlaying()) {
//                mPlayer.pause();
//                updatePausePlay();
//            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK
                || keyCode == KeyEvent.KEYCODE_MENU) {
            hide();
            return true;
        } else {
            if (isShowing())
                hide();
//            else
//                show(sDefaultTimeout);
        }
        return super.dispatchKeyEvent(event);
    }

    public boolean isShowing() {
        return isShow;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_qxd_mediacontroller://清晰度按钮
                if (mQxdList.getVisibility() == View.VISIBLE) {
                    mQxdList.setVisibility(View.GONE);
                } else {
                    mQxdList.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tv_wutai_mediacontroller://舞台按钮
                if (mWutaiList.getVisibility() == View.VISIBLE) {
                    mWutaiList.setVisibility(View.GONE);
                } else {
                    mWutaiList.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tv_jiemudan_mediacontroller://节目单按钮
                listenner.onJiemudanClicked();
                break;
            case R.id.btn_report_livemedia://举报按钮
                listenner.onReportClicked();
                break;
            case R.id.btn_lock_media://锁屏按钮
                isLocked = !isLocked;
                initLockState(isLocked);
//                if (isLocked) {
//                    mLockButton.setBackgroundResource(R.drawable.but_suohl);
////                    mLockButton.setImageResource(R.drawable.icon_07bofang_locked);
//                } else {
//                    mLockButton.setBackgroundResource(R.drawable.but_suo);
////                    mLockButton.setImageResource(R.drawable.icon_07bofang_unlock);
//                }
//                mControllerClickedListener.onLockChanged(isLocked);
                listenner.onLockClicked(isLocked);
                break;
            case R.id.btn_changesize_media: //切换横竖屏按钮
                listenner.onChangeScreenClicked();
                break;
            case R.id.btn_share_media: //分享按钮
                listenner.onSharedClicked();
                break;
            case R.id.btn_chat_media: //聊天按钮
//                mesNum = -1;
                listenner.onChatClicked("","");
                break;
            case R.id.btn_zan_media: //赞按钮
                listenner.onZanClicked();
                break;
            case R.id.btn_sendgift_media: //礼物按钮
                listenner.onGiftBtnClicked();

//                if (mGiftGridView.getVisibility() != VISIBLE) {
//                    setGiftData();
//                    mGiftGridView.setVisibility(VISIBLE);
//                    underGridLine.setVisibility(VISIBLE);
//                    listenner.showGiftGuidDialog();
//                }
//                if (isSendGift) {
//                    gridItemClicked = false;
//                    giftNum++;
//                    if (((System.currentTimeMillis() - giftTime) < 500)) {
//                        mHandler.removeMessages(SendGift);
//                        mHandler.sendEmptyMessageDelayed(SendGift, 500);
//                    } else {
//                        mHandler.sendEmptyMessageDelayed(SendGift, 500);
//                    }
//                    giftTime = System.currentTimeMillis();
//                }

                break;
            //点播底部的控件
            case R.id.btn_zan_lookforward: //赞按钮
                listenner.onZanClicked();
                break;
            case R.id.btn_fangda_lookforward: //放大按钮
                listenner.onChangeScreenClicked();
                break;
            case R.id.im_play_or_pause: //播放暂停
                listenner.onPlayClicked(!listenner.isPlaying());
                break;
            case R.id.img_user_livemedia: //主播头像的点击
                listenner.onAnchorImgClicked(true, "");
                break;
            case R.id.img_btn_back: //返回按钮
                listenner.onBackClicked();
                break;

            default:
                break;
        }
    }

    private void initLockState(boolean islocked) {
        if (isLandscape) {
            if (islocked) {
                mLockButton.setBackgroundResource(R.drawable.but_suohl);
            } else {
                mLockButton.setBackgroundResource(R.drawable.but_suo);
            }
        } else {
            if (islocked) {
                mLockButton.setBackgroundResource(R.drawable.icon_sp_lockhl);
            } else {
                mLockButton.setBackgroundResource(R.drawable.icon_sp_lock);
            }
        }

    }

    public void setLiveWutai(List<LiveInfo> infos) {
        liveInfos = infos;
        if (mWutaiAdapter == null) {
            mWutaiAdapter = new CommonAdapter<LiveInfo>(this.getContext(), liveInfos,
                    R.layout.tv_single) {

                @Override
                public void convert(final ViewHolder helper, final LiveInfo item) {
                    helper.setText(R.id.tv_hot_detail, item.getMsg());
                    helper.getConvertView().setOnClickListener(
                            new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    mWUtaiText.setText(item.getMsg());
//                                    btn_wutai.setText(item.getMsg());
                                    if (listenner != null) {
                                        listenner.onWuTaiClicked(helper.getPosition());
//                                        .onWutaiClicked(helper
//                                                        .getPosition());
                                        mWutaiList.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                }
            };
        }
        mWutaiList.setAdapter(mWutaiAdapter);
    }

    public void setQXD(List<String> infos) {
//        mQxdList = (ListView) findViewById(R.id.rg_play);
//        if (mQXDInfoList == null) {
//            mQXDInfoList = new ArrayList<String>();
//        }
//        mQXDInfoList.clear();
//        mQXDInfoList.addAll(infos);
//        position = mQXDInfoList.indexOf("高清");
//        if (mQXDAdapter == null) {
//            mQXDAdapter = new CommonAdapter<String>(this.getContext(), mQXDInfoList, R.layout.tv_single) {
//                @Override
//                public void convert(final ViewHolder helper, final String item) {
//                    helper.setText(R.id.tv_hot_detail, item);
//                    helper.getConvertView().setOnClickListener(new OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            position = helper.getPosition();
//                            mQxdText.setText(item);
//                            if (listenner != null) {
//                                listenner.onQXDClicked(helper.getPosition());
////                                mQXDlistView.setVisibility(View.INVISIBLE);
//                                mQxdList.setVisibility(View.INVISIBLE);
//                            }
//                        }
//                    });
//                }
//            };
//        }
//        mQxdList.setAdapter(mQXDAdapter);
//        mQxdText.setText(mQXDAdapter.getItem(position));
    }

    private OnShowNewMediacontrollerListener listenner;

    public void setListenner(OnShowNewMediacontrollerListener listenner) {
        this.listenner = listenner;
    }


    public boolean isShow() {
        return isShow;
    }

    public boolean isHasFormMenu() {
        return hasFormMenu;
    }

    public void setHasFormMenu(boolean hasFormMenu) {
        this.hasFormMenu = hasFormMenu;
    }

    public boolean isHasWuTai() {
        return hasWuTai;
    }

    public void setHasWuTai(boolean hasWuTai) {
        this.hasWuTai = hasWuTai;
    }

    public List<XiuchanMessage> getmMessList() {
        return mMessList;
    }

    public void setmMessList(List<XiuchanMessage> mMessList) {
        this.mMessList = mMessList;
    }

    public void setmInstantSeeking(boolean mInstantSeeking) {
        this.mInstantSeeking = mInstantSeeking;
    }



    public void setmActivity(BaseActivity mActivity) {
        this.mActivity = mActivity;
    }


    public interface OnShowNewMediacontrollerListener {
        void onReportClicked();

        void onLockClicked(boolean isLock);

        void onChangeScreenClicked();

        void onSharedClicked();

        void onChatClicked(String hint,String toUserName);

        void onZanClicked();

        void onWuTaiClicked(int pozition);

        void onQXDClicked(int position);

        void onGiftBtnClicked();

        void onJiemudanClicked();

        boolean isPlaying();

        void onPlayClicked(boolean playOrstop);

        void onMessageImgClicked(String videoId);

        void onAnchorImgClicked(boolean isAnchor, String userId);

        long getDuration();

        long getCurrentPosition();

        void seekTo(long pos);

        void showGiftGuidDialog();

        int getBufferPercentage();

        void onBackClicked();

        void onSendGift(boolean showDialog, int sentGiftNUm);

        void castMbBuyGift(int count, int sentGiftNUm);//购买礼物的个数

        void onInitMbDialog();
    }


    private boolean mInstantSeeking;
    private SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStartTrackingTouch(SeekBar bar) {
//            mDragging = true;
            show(sDefaultTimeout);
            mHandler.removeMessages(SHOW_PROGRESS);
            if (mInstantSeeking)
                mAM.setStreamMute(AudioManager.STREAM_MUSIC, true);
        }

        @Override
        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (!fromuser) {
                return;
            }
            long newposition = (mDuration * progress) / 1000;
            String time = StringUtils.generateTime(newposition);
            if (butomPlayTimeText != null) {
                butomPlayTimeText.setText(time);
            }
            if (mInstantSeeking) {
                listenner.seekTo(newposition);
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar bar) {
            if (!mInstantSeeking) {
                listenner.seekTo((mDuration * bar.getProgress()) / 1000);
            }
//            LogUtils.d("mDuration:" + mDuration);
//            LogUtils.d("getProgress:" + bar.getProgress());
            show(sDefaultTimeout);
            mHandler.removeMessages(SHOW_PROGRESS);
            mAM.setStreamMute(AudioManager.STREAM_MUSIC, false);
//            mDragging = false;
            mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
        }
    };

    public long setProgress() {
        if (listenner == null)
            return 0;
//        LogUtils.d("setProgress");
        long position = listenner.getCurrentPosition();
        long duration = listenner.getDuration();
        if (mProgress != null) {
            if (duration > 0) {
                long pos = 1000L * position / duration;
                mProgress.setProgress((int) pos);
            }
            int percent = listenner.getBufferPercentage();
            mProgress.setSecondaryProgress(percent * 10);
        }
        mDuration = duration;


        if (butomPlayTimeText != null) {
            butomPlayTimeText.setText(StringUtils.generateTime(position) + "/" + StringUtils.generateTime(mDuration));
        }
        return position;
    }


    public void full(boolean tag) {

        isLandscape = tag;
        initLockState(isLocked);
        LogUtils.d("ShowNewMediacontroler----isLandscape--" + isLandscape + "---liveMode---" + liveMode);
        mWutaiList.setVisibility(View.GONE);

        if (liveMode) {  //秀场
            buttomPlayLayout.setVisibility(GONE);
            buttomLiveLayout.setVisibility(VISIBLE);

            if (isLandscape) {
                verticalAnchorInfo.setVisibility(GONE);
                mChangeSizeBtn.setImageResource(R.drawable.icon_nofullsize);
                leftMBNumText.setVisibility(VISIBLE);
                mAnchorInfoLayout.setVisibility(View.VISIBLE);
                mAudienceList.setVisibility(View.VISIBLE);
                mMessageList.setVisibility(View.VISIBLE);
                mGiftBtn.setVisibility(View.VISIBLE);
                mGiftBtn.setImageResource(R.drawable.but_liwu);
                mAudienceNums.setVisibility(View.VISIBLE);
                mReportBtn.setVisibility(View.VISIBLE);
                mReportBtn.setImageResource(R.drawable.icon_jubao_selector);
                mChatBtn.setImageResource(R.drawable.but_shuru);
                mShareBtn.setImageResource(R.drawable.icon_share_selector);
                mZanBtn.setImageResource(R.drawable.icon_zan_select);
                mVideoInfoLayout.setVisibility(View.VISIBLE);
                bacImgBtn.setVisibility(GONE);
                if (hasFormMenu) {
                    mJiemudanText.setVisibility(View.VISIBLE);
                } else {
                    mJiemudanText.setVisibility(View.GONE);
                }
                if (hasWuTai) {
                    mWUtaiText.setVisibility(View.VISIBLE);
                } else {
                    mWUtaiText.setVisibility(View.GONE);
                }
            } else {
                verticalAnchorInfo.setVisibility(VISIBLE);
//                mChangeSizeBtn.setImageResource(R.drawable.icon_fullsize);
                mChangeSizeBtn.setImageResource(R.drawable.icon_sp_fullsize);
                bacImgBtn.setVisibility(VISIBLE);
                leftMBNumText.setVisibility(GONE);
//                mVideoInfoLayout.setVisibility(View.GONE);
                mAnchorInfoLayout.setVisibility(View.GONE);
                mAudienceList.setVisibility(View.GONE);
                mMessageList.setVisibility(View.GONE);
                mGiftBtn.setVisibility(View.GONE);
                mAudienceNums.setVisibility(View.GONE);
                mReportBtn.setImageResource(R.drawable.icon_sp_jubao_selector);
                mChatBtn.setImageResource(R.drawable.icon_sp_pinglun);
                mShareBtn.setImageResource(R.drawable.icon_sp_share_selector);
                mZanBtn.setImageResource(R.drawable.icon_sp_zan_select);
//                mReportBtn.setVisibility(View.GONE);
            }
        } else {     //回看
            verticalAnchorInfo.setVisibility(GONE);
            mAnchorInfoLayout.setVisibility(View.GONE);
            mAudienceList.setVisibility(View.GONE);
            mMessageList.setVisibility(View.GONE);
            mGiftBtn.setVisibility(View.GONE);
            mAudienceNums.setVisibility(View.GONE);
            leftMBNumText.setVisibility(GONE);

            buttomPlayLayout.setVisibility(VISIBLE);
            buttomLiveLayout.setVisibility(GONE);
            if (isLandscape) {
                buttomPlayFangdaImg.setImageResource(R.drawable.icon_07bofang_suoxiao);
//                buttomPlayFangdaImg.setImageResource(R.drawable.icon_nofullsize);
                bacImgBtn.setVisibility(GONE);
                mReportBtn.setVisibility(View.VISIBLE);
                mAnchorInfoLayout.setVisibility(VISIBLE);
                mVideoInfoLayout.setVisibility(View.VISIBLE);
                mJiemudanText.setVisibility(View.GONE);
                mWUtaiText.setVisibility(View.GONE);
                mReportBtn.setImageResource(R.drawable.icon_jubao_selector);
                mShareBtn.setImageResource(R.drawable.icon_share_selector);
            } else {
                buttomPlayFangdaImg.setImageResource(R.drawable.icon_07bofang_fangda);
//                buttomPlayFangdaImg.setImageResource(R.drawable.icon_fullsize);
                bacImgBtn.setVisibility(VISIBLE);
//                mReportBtn.setVisibility(View.GONE);
                mAnchorInfoLayout.setVisibility(GONE);
//                mVideoInfoLayout.setVisibility(View.GONE);
                mReportBtn.setImageResource(R.drawable.icon_sp_jubao_selector);
                mShareBtn.setImageResource(R.drawable.icon_sp_share_selector);
            }
        }


    }


    // 横竖屏切换
    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            setFullMode();
            full(true);
        } else {
            full(false);
//            setVerticalMode(inModule);
        }
        refreshDrawableState();
        invalidate();
    }

}

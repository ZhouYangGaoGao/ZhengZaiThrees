package com.modernsky.istv.view;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.modernsky.istv.R;
import com.modernsky.istv.acitivity.LivePublishActivity;
import com.modernsky.istv.adapter.CommonAdapter;
import com.modernsky.istv.adapter.ViewHolder;
import com.modernsky.istv.bean.PeopleIdAndPicInfo;
import com.modernsky.istv.bean.XiuchanMessage;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.BitmapTool;
import com.modernsky.istv.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by zqg on 2016/2/23.
 */
public class LivePublisherMediacontroler extends FrameLayout implements View.OnClickListener {

    private boolean showMessageList = true;
    private ImageView mShareImg;
    //    private ImageView mPlayButton;
    private ImageView mCloseOrOpenMessageImg;
    private CommonAdapter<PeopleIdAndPicInfo> picAdapter;
    private boolean isShow;

    public List<PeopleIdAndPicInfo> getPicList() {
        return picList;
    }

    public void setPicList(List<PeopleIdAndPicInfo> picList) {
        this.picList = picList;
    }

    private List<PeopleIdAndPicInfo> picList;

    private List<XiuchanMessage> chatMessages;

    public List<XiuchanMessage> getChatMessages() {
        return chatMessages;
    }

    private CommonAdapter<XiuchanMessage> mesAdapter;
    private ImageView mMessageBtn;
    private ImageView mCamBtn;
//    private TextView timeAddText;

//    public TextView getPowerConsumeText() {
//        return powerConsumeText;
//    }
//
//    public void setPowerConsumeText(TextView powerConsumeText) {
//        this.powerConsumeText = powerConsumeText;
//    }

//    public TextView getTimeAddText() {
//        return timeAddText;
//    }
//
//    public void setTimeAddText(TextView timeAddText) {
//        this.timeAddText = timeAddText;
//    }

//    private TextView powerConsumeText;

    public ImageView getmMessageBtn() {
        return mMessageBtn;
    }

    public ImageView getmCamBtn() {
        return mCamBtn;
    }

    public ImageView getmLightBtn() {
        return mLightBtn;
    }


    public TextView getmName() {
        return mName;
    }

    public TextView getmAudienceNums() {
        return mAudienceNums;
    }

    private ImageView mLightBtn; // 暂停播放按钮， 说话的按钮，挑换前后摄像头按钮
    private TextView mName;

    public TextView getmVideoTitle() {
        return mVideoTitle;
    }

    public void setmVideoTitle(TextView mVideoTitle) {
        this.mVideoTitle = mVideoTitle;
    }

    private TextView mVideoTitle;

    public void setmAudienceNums(TextView mAudienceNums) {
        this.mAudienceNums = mAudienceNums;
    }

    private TextView mAudienceNums;// 剩余时间， 我的名字， 我的头像， 我的观众的个数
    private ImageView mImg;//我的头像
    private HorizontalListView mAudienceList;
    private ListView mMessageList;

//    private ImageView mGetTimeBtn;


    public void show() {
        isShow = true;
        setVisibility(View.VISIBLE);
    }

    public void hide() {
        isShow = false;
        setVisibility(View.GONE);
    }


    public HorizontalListView getmAudienceList() {
        return mAudienceList;
    }

    public ImageView getmImg() {
        return mImg;
    }

    private View mRoot;
    private boolean mFromXml;

    public LivePublisherMediacontroler(Context context) {
        super(context);
        isShow = true;
    }

    public LivePublisherMediacontroler(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRoot = this;
        mFromXml = true;
        isShow = true;
        picList = new ArrayList<PeopleIdAndPicInfo>();
    }

    @Override
    protected void onFinishInflate() {
        if (mRoot != null) {
            initControllerView(mRoot);
        }
        super.onFinishInflate();
    }

    @Override
    protected void onDetachedFromWindow() {
        hide();
        super.onDetachedFromWindow();
    }


    private void initControllerView(View rootView) {
        mLightBtn = (ImageView) rootView.findViewById(R.id.btn_light_livemedia);

        mCloseOrOpenMessageImg = (ImageView) rootView.findViewById(R.id.btn_closeOropen_messageList);
//        mGetTimeBtn = (ImageView) findViewById(R.id.btn_moretime_livemedia);
//        timeAddText= (TextView) findViewById(R.id.tv_time_add);
//        powerConsumeText= (TextView) findViewById(R.id.tv_num_strawberry_consume);


        mShareImg = (ImageView) rootView.findViewById(R.id.btn_share_livemedia);
        mMessageBtn = (ImageView) rootView.findViewById(R.id.btn_message_livemedia);
        mCamBtn = (ImageView) rootView.findViewById(R.id.btn_cam_livemedia);
        mName = (TextView) rootView.findViewById(R.id.tv_name_livemedia);
        mVideoTitle = (TextView) rootView.findViewById(R.id.tv_message_anchor_livemedia);
        mImg = (ImageView) rootView.findViewById(R.id.img_user_livemedia);
        mAudienceNums = (TextView) rootView.findViewById(R.id.tv_num_audiens);
        mAudienceList = (HorizontalListView) rootView.findViewById(R.id.audienList_livemedia);
        mMessageList = (ListView) rootView.findViewById(R.id.messList_livemedia);
        mLightBtn.setOnClickListener(this);
//        mGetTimeBtn.setOnClickListener(this);

        mCloseOrOpenMessageImg.setOnClickListener(this);
        mMessageBtn.setOnClickListener(this);
        mCamBtn.setOnClickListener(this);
        mImg.setOnClickListener(this);
        mShareImg.setOnClickListener(this);
        initAdapter();
        mAudienceList.setAdapter(picAdapter);
        mAudienceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.d("setOnItemClickListener==" + position);
                listenner.onPeopleImgClicked(position);
//                Utils.toast(LivePublisherMediacontroler.this.getContext(), picList.get(position));
            }
        });

        chatMessages = new ArrayList<XiuchanMessage>();
        initTextColor();
        initChatMessageList();
        initChatMessageListListenner();
    }

    private void initChatMessageListListenner() {
        mMessageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                huifuNum = position;
                if (!TextUtils.isEmpty(chatMessages.get(position).getFromUserName())) {
                    listenner.onMesBtnClick("回复: " + chatMessages.get(position).getFromUserName(),
                            chatMessages.get(position).getFromUserName(),chatMessages.get(position).getFromUserId());
                }
            }
        });
    }

    private void initTextColor() {
        spans = new ArrayList<ForegroundColorSpan>();
        spans.add(new ForegroundColorSpan(LivePublisherMediacontroler.this.getContext().getResources().getColor(R.color.text_rede8)));
        spans.add(new ForegroundColorSpan(LivePublisherMediacontroler.this.getContext().getResources().getColor(R.color.text_purple9a)));
        spans.add(new ForegroundColorSpan(LivePublisherMediacontroler.this.getContext().getResources().getColor(R.color.text_pinkff)));
        spans.add(new ForegroundColorSpan(LivePublisherMediacontroler.this.getContext().getResources().getColor(R.color.text_yellowff)));
        spans.add(new ForegroundColorSpan(LivePublisherMediacontroler.this.getContext().getResources().getColor(R.color.text_green05)));
        spans.add(new ForegroundColorSpan(LivePublisherMediacontroler.this.getContext().getResources().getColor(R.color.text_blue03)));
        spans.add(new ForegroundColorSpan(LivePublisherMediacontroler.this.getContext().getResources().getColor(R.color.text_grenb2)));
    }

    public void addMessage(XiuchanMessage message) {
        if (message.getGiftId() != null && !TextUtils.isEmpty(message.getPic())) {
            if (message.getCount()==0) {
                return;
            }
        }
        if (message.getType() == 7 && message.getOnceTime() == 0) {
//            ((LivePublishActivity) LivePublisherMediacontroler.this.getContext()).doAnimateOpen("", 1, true);
            return;
        }
        if (message.getType() == 0 ) {
//            ((LivePublishActivity) LivePublisherMediacontroler.this.getContext()).doAnimateOpen("", 1, true);
            return;
        }
        if (message.getType() == 9) {
            ((LivePublishActivity) LivePublisherMediacontroler.this.getContext()).doAnimateOpen("", 1, true);
            return;
        }

        if (TextUtils.isEmpty(message.getFromUserPic())) {
            return;
        }
        if (chatMessages.size() > 0 && message.getType() == 8) {
                if (message.getFromUserId().equals(chatMessages.get(chatMessages.size() - 1).getFromUserId()) && chatMessages.get(chatMessages.size() - 1).getType() == 8) {
                    return;
                }
        }
        if (chatMessages.contains(message))
            return;

        if (chatMessages.size() >= 20) {
            chatMessages.remove(0);
        }
        chatMessages.add(message);
        initChatMessageList();
    }

    private List<ForegroundColorSpan> spans;

    private void initChatMessageList() {
        if (mesAdapter == null) {
            mesAdapter = new CommonAdapter<XiuchanMessage>(this.getContext(), chatMessages, R.layout.item_messagelist) {
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

                    if (!TextUtils.isEmpty(item.getFromUserId())) {
                        picImg.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                listenner.onMessageImgClicked(item.getFromUserId());
                            }
                        });
                    }
                    LogUtils.d("item.getFromUserPic()===-" + item.getFromUserPic());
                    BitmapTool.getInstance().getAdapterUitl().display(picImg, item.getFromUserPic());
//              // 礼物弹幕
                    if (item.getGiftId() != null && !TextUtils.isEmpty(item.getPic())) {
//            img_avatar.setImageResource(R.drawable.icon_lingdang);
                        img.setVisibility(View.VISIBLE);
                        tv_left.setText(item.getMsg());
                        int num = item.getCount();
                        LogUtils.d("message  list  controller" + item.toString());
                        String content = item.getGiftUserName() + " 送给选手";
                        BitmapTool.getInstance().getAdapterUitl().display(img, item.getPic());
                        tv_left.setTextColor(LivePublisherMediacontroler.this.getContext().getResources().getColor(R.color.whitef0));
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
                    LogUtils.d("message---" + item.toString());
                    switch (item.getType()) {
                        case 2:
                        case 3:
                        case 4:
                            String content0 = item.getMsg();
                            word = new SpannableString(content0);
                            word.setSpan(spans.get(count), 0,
                                    content0.length(),
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            rootView.setBackgroundResource(R.drawable.bg_bluetanmu);
                            tv_left.setText(word);

                            return;
                        case 8:
                            String content1 = item.getFromUserName() + "进入了房间";
//                            LogUtils.d("content1===" + content1);
//                            tv_left.setTextColor(LivePublisherMediacontroler.this.getResources().getColor(R.color.whitef0));
//                            word = new SpannableString(content1);
//                            word.setSpan(spans.get(count), item.getFromUserName().length(),
//                                    item.getFromUserName().length() + 5,
//                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                            rootView.setBackgroundResource(R.drawable.bg_bluetanmu);
                            tv_left.setText(content1);
                            return;
                        case 7:
                            String content2 = "本场直播延长了" + item.getOnceTime() / (1000 * 60) + "分钟";
//                            word = new SpannableString(content2);
//                            word.setSpan(spans.get(count), 0,
//                                    content2.length(),
//                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            rootView.setBackgroundResource(R.drawable.bg_bluetanmu);
                            tv_left.setText(content2);
//                            tv_left.setText(word);
                            return;
                    }


                    if (TextUtils.isEmpty(item.getFromUserId())) {
                        return;
                    }
                    //是否是我的
                    boolean isMyself = item.getFromUserId().equals(UserService.getInatance().getUserBean(LivePublisherMediacontroler.this.getContext()).getId());
                    //是否是我的
                    String content = "";


                    if (isMyself) {
//                        content = "我" + " " + item.getMsg();
//                        tv_left.setTextColor(LivePublisherMediacontroler.this.getResources().getColor(R.color.whitef0));
//                        LogUtils.d("isMyself  content---"+content);
//                        word = new SpannableString(content);
//                        word.setSpan(spans.get(count), 2,
//                                content.length(),
//                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        rootView.setBackgroundResource(R.drawable.bg_yellowtanmu);
                    }
                    boolean isReturn =
                            !TextUtils.isEmpty(item.getToUserName());

                    if (!isReturn) {
                        content = item.getFromUserName() + " " + item.getMsg();
                        tv_left.setTextColor(LivePublisherMediacontroler.this.getResources().getColor(R.color.whitef0));
                        word = new SpannableString(content);
                        word.setSpan(spans.get(count), item.getFromUserName().length() + 1,
                                content.length(),
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else {

                        content = item.getFromUserName() + " 回复 " + item.getToUserName() + " " + item.getMsg();
                        tv_left.setTextColor(LivePublisherMediacontroler.this.getResources().getColor(R.color.whitef0));
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
        if (chatMessages.size() > 0) {
            mMessageList.setSelection(chatMessages.size() - 1);
        }

    }

    public void initPeoplePicListData(List<PeopleIdAndPicInfo> list) {
        picList.clear();
        picList.addAll(list);
        picAdapter.notifyDataSetChanged();
    }

//    private int huifuNum = -1;

    private void initAdapter() {

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
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.btn_play_livemedia://播放结束按钮
//                listenner.onPlayBtnClick();
//                break;
//            case R.id.btn_moretime_livemedia:
//                listenner.onGetMoreTimeClicked();
//                break;
            case R.id.btn_message_livemedia://打字的按钮
//                huifuNum = -1;
                listenner.onMesBtnClick("","","");
                break;
            case R.id.btn_cam_livemedia: //调换摄像头按钮
                listenner.onCamClick();
                break;
            case R.id.img_user_livemedia: //我的头像的按钮
                listenner.onUserImgClick();
                break;
            case R.id.btn_light_livemedia: //闪光灯的开闭
                listenner.onLightClick();
                LogUtils.d("mediacontroller---onLightClick");
            case R.id.btn_share_livemedia: //分享
//                listenner.onShareClicked();
                break;
            case R.id.btn_closeOropen_messageList: //弹幕开关
//                showMessageList = !showMessageList;
//                if (showMessageList) {
//                    mMessageList.setVisibility(View.VISIBLE);
//                } else {
//                    mMessageList.setVisibility(View.GONE);
//                }
//                listenner.onCloseOrOpenMessageClicked(showMessageList);
                break;
            default:
                break;
        }
    }

    private OnLivePublishMediacontrollerListener listenner;

    public void setListenner(OnLivePublishMediacontrollerListener listenner) {
        this.listenner = listenner;
    }


    public boolean isShow() {
        return isShow;
    }

//
//    public int getHuifuNum() {
//        return huifuNum;
//    }


    public interface OnLivePublishMediacontrollerListener {
        void onPlayBtnClick();

        void onMesBtnClick(String hint,String toUsername,String toUserId);

        void onLightClick();

        void onCamClick();

        void onMicClick();

        void onScreenPrinted();

        void onCameraCloseOrOpen();

        void onShareClicked();

        void onCloseOrOpenMessageClicked(boolean showDanmuList);

        void onUserImgClick();


        void onPeopleImgClicked(int num);

        void onMessageImgClicked(String userId);

        void onGetMoreTimeClicked();

    }
}

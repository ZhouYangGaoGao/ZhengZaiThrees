package com.modernsky.istv.acitivity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.action.UserAction;
import com.modernsky.istv.bean.AnchorChatroomInfo;
import com.modernsky.istv.bean.PeopleIdAndPicInfo;
import com.modernsky.istv.bean.PublishUrlInfo;
import com.modernsky.istv.bean.UserEntity;
import com.modernsky.istv.bean.XiuchanMessage;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.BitmapTool;
import com.modernsky.istv.tool.DialogTool;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.TimeTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.CheckCode;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.FileUtils;
import com.modernsky.istv.utils.LocalCacheUtil;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.PreferencesUtils;
import com.modernsky.istv.utils.ThreeAppParams;
import com.modernsky.istv.utils.Utils;
import com.modernsky.istv.utils.WeakHandler;
import com.modernsky.istv.view.LivePublisherMediacontroler;
import com.modernsky.istv.view.LivePublisherMediacontroler.OnLivePublishMediacontrollerListener;
import com.modernsky.istv.window.PeopleInfoDialog;
import com.modernsky.istv.window.TanmuDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.nodemedia.LivePublisher;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.ChatRoomInfo;
import io.rong.imlib.model.ChatRoomMemberInfo;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;
import main.java.cn.aigestudio.datepicker.utils.LogUtil;
import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


/**
 * Created by zqg on 2016/2/23.
 */
public class LivePublishActivity extends BaseActivity implements LivePublisher.LivePublishDelegate {
    LivePublisherMediacontroler controller;
    private boolean shouldDoControllerAnimation = true;//当淡入淡出的时候 不能在操作显示与隐藏
    private SurfaceView sv;
    private boolean isStarting = false;
    private boolean isMicOn = true;
    private boolean isCamOn = true;
    private boolean isFlsOn = true;
    private List<PeopleIdAndPicInfo> picLists;
    private int leftSecends;
    private String activityId;

    private ImageView dotImg;

    private int needCountPower;

    private int liveLeftTime = 15 * 60;

    private AnchorChatroomInfo anchorChatroomInfo;

    private boolean hasEnteredChatroom;
    List<PublishUrlInfo> publishUrlInfo;
    String publishUrl = "rtmp://w.gslb.lecloud.com/live/20160217300055299";
    private ImageView mPlayButton; //播放按钮
    TextView mTimeLeftText; //剩余时间
    RelativeLayout giftLayout;
    private boolean ifhasSendOut;
    private TanmuDialog mTanmuDialog;
    private GifImageView network_gifimageview;
    private String mChatRoomId = "";
    private int priceCount = 0;
    private int mbCount = 0;
    private int powerCount = 0;
    private TextView prizeNum, mbNum, powerNum;
    private List<PeopleIdAndPicInfo> peopleLists;
    private ImageView mGetTimeBtn;
    private TextView timeAddText;
    private TextView timeState;
    private TextView powerConsumeText;
    private String videoId;

    private boolean hasReachedTime;
    //    private int peopleTotleNum = 1;
    private int onlinePeopleNum = 1;
    private AlertDialog failedDialog;
    private boolean shouldClose;
    // 登录状态改变广播
//    private MyReceive myReceive;

    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_livepublish);
    }

    @Override
    public void findViewById() {
        leftSecends = 15 * 60;
        Utils.setFullScreen(this);
        isStarting = false;
        dotImg = (ImageView) findViewById(R.id.dotImg);
        mGetTimeBtn = (ImageView) findViewById(R.id.btn_moretime_livemedia);
        mGetTimeBtn.setOnClickListener(this);
        timeAddText = (TextView) findViewById(R.id.tv_time_add);
        powerConsumeText = (TextView) findViewById(R.id.tv_num_strawberry_consume);
        prizeNum = (TextView) findViewById(R.id.tv_num_prize);
        mbNum = (TextView) findViewById(R.id.tv_num_mb);
        powerNum = (TextView) findViewById(R.id.tv_num_power);
        timeState = (TextView) findViewById(R.id.tv_time_state);
        timeState.setText("距离直播开始");
        Utils.setTextColorPaint(prizeNum, getResources().getColor(R.color.purple9f), getResources().getColor(R.color.purple36));
        Utils.setTextColorPaint(powerNum, getResources().getColor(R.color.purple9f), getResources().getColor(R.color.purple36));
        sv = (SurfaceView) findViewById(R.id.cameraView);
        mPlayButton = (ImageView) findViewById(R.id.btn_play_livemedia);
        mPlayButton.setOnClickListener(this);
        mTimeLeftText = (TextView) findViewById(R.id.tv_time_leave_livemedia);
        giftLayout = (RelativeLayout) findViewById(R.id.giftLayout);
        publishUrlInfo = new ArrayList<PublishUrlInfo>();
        initGif();
        initPublishView();
        initMediaController();
        getGestureDetector();
        handler.sendEmptyMessage(0);
        peopleLists = new ArrayList<PeopleIdAndPicInfo>();
//        getAnchorGetThings();
        anchorChatroomInfo = (AnchorChatroomInfo) getIntent().getSerializableExtra("anchorInfo");
        activityId = getIntent().getStringExtra("activityId");
        publishUrl = getIntent().getStringExtra("publishUrl");
        mChatRoomId = anchorChatroomInfo.getChatroomId();
        this.videoId = anchorChatroomInfo.getVideoId() + "";
        pubLishTimes = 3;
        if (!TextUtils.isEmpty(mChatRoomId)) {
            goToChatRoom();
        }
        getUrlAndmChatRoom();
        initAnchorInfos(anchorChatroomInfo);
        initGuidPage();
    }

    Timer timer;
//    private int allTimeCount = 0;  //用于计时  获取人数

    private void initStartTime() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
//                allTimeCount++;
                initTime();
//                if (liveLeftTime != 0 && leftSecends <=0) {
//                    initPeopleNum();
//                }
            }
        }, 0, 1000);

    }

//    private void initPeopleNum() {
//        LogUtils.d("initPeopleNum----allTimeCount="+allTimeCount);
//        if (allTimeCount % 5 == 0) {
//            if (!TextUtils.isEmpty(videoId)) {
//                onGetPeopleNum();
//            }
//        }
//    }

    private void onGetPeopleNum() {
        LogUtils.d("videoId---" + videoId);
        SendActtionTool.get(Constants.URL_GET_NUMINLIVE,
                ServiceAction.Action_User,
                UserAction.Action_GET_TOTLE_NUM, this, UrlTool.getParams(Constants.VIDEO_ID, videoId));
    }

    Dialog guidDialog, peopleAirDialog;

    private void initGuidPage() {
        if (!PreferencesUtils.getBooleanPreferences(this, PreferencesUtils.TYPE_GUIDE[3])) {
            guidDialog = DialogTool.createGuideDialog(this, 3, true, new DialogTool.DialogGuideListener() {
                @Override
                public void onGuide(int index) {
                    guidDialog.cancel();
                    initPeoPleAir();
                }
            });
            PreferencesUtils.saveBooleanPreferences(LivePublishActivity.this, PreferencesUtils.TYPE_GUIDE[3], true);
        }
    }

    private void initPeoPleAir() {
        if (!PreferencesUtils.getBooleanPreferences(this, PreferencesUtils.TYPE_GUIDE[7])) {
            peopleAirDialog = DialogTool.createGuideDialog(this, 7, true, new DialogTool.DialogGuideListener() {
                @Override
                public void onGuide(int index) {
                    peopleAirDialog.cancel();
                }
            });
            PreferencesUtils.saveBooleanPreferences(LivePublishActivity.this, PreferencesUtils.TYPE_GUIDE[7], true);
        }

    }

    private boolean canGetTimeThroughPower = true;


    private void initUserInfo() {
        mbNum.setText(String.valueOf(0));
        LogUtils.d("power----" + UserService.getInatance().getUserBean(this).getStrawCount());
//        powerNum.setText(UserService.getInatance().getUserBean(this).getStrawCount() + "");
//        powerNum.setText(anchorChatroomInfo.getUser().getStrawCount() + "");
        powerNum.setText(String.valueOf(anchorChatroomInfo.getStrawCount()));
        prizeNum.setText(String.valueOf(0));

        controller.getmName().setText(UserService.getInatance().getUserBean(this).getUserName());
        BitmapTool.getInstance().getAdapterUitl().display(controller.getmImg(), UserService.getInatance().getUserBean(this).getFaceUrl());
        controller.getmVideoTitle().setText(UserService.getInatance().getUserBean(this).getHerald().getName());
    }

    /**
     * 消耗power来 获取时常
     */
    private void getMoerTimeThroughPower() {

        LogUtils.d("getMoerTimeThroughPower----" + UserService.getInatance().getUserBean(this).getStrawCount());
        LogUtils.d("powerge===" + Integer.valueOf(powerNum.getText().toString()) + "needCountPower====" + needCountPower);
        if (Integer.valueOf(powerNum.getText().toString()) < needCountPower) {
            Utils.toast(this, "时间不够？粉丝来凑！喊观众们来给你送power吧！");
            return;
        }
        showLoadingDialog();
        RequestParams params = UrlTool.getParams("chatroomId", mChatRoomId, Constants.USER_ID, UserService.getInatance().getUserBean(this).getId(), "count", needCountPower + "");
        canGetTimeThroughPower = false;
        SendActtionTool.get(Constants.UserParams.URL_ADDTIME_USERPOWER, null, UserAction.ACTION_ADDTIME_USERPOWER, LivePublishActivity.this,
                params);
    }

//    /**
//     * 获取主播的power等信息
//     */
//    private void getAnchorGetThings() {
//        SendActtionTool.post(Constants.UserParams.URL_GETANCHORINFO, null, UserAction.ACTION_GETANCHOR_INFO, LivePublishActivity.this,
//                UrlTool.getParams("chatroomId", mChatRoomId));
//    }


    private int totlaGiftNum = 0;

    public void doAnimateOpen(String url, int total, final boolean isZan) {
        for (int j = 0; j < total; j++) {
            LogUtils.d("total==" + total);
            final int h = j;
            LogUtils.d("h=" + h);
            final ImageView textView = new ImageView(this);
            LogUtils.d("imgurl=" + url);
            if (isZan) {
                textView.setImageResource(R.drawable.applaud);
            } else {
                BitmapTool.getInstance().showLocalView(textView, url);
            }
            giftLayout.postDelayed(new Runnable() {
                // android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                @Override
                public void run() {
                    final android.widget.RelativeLayout.LayoutParams params = new android.widget.RelativeLayout.LayoutParams(
                            giftLayout.getWidth() / 3, giftLayout.getWidth() / 3);
                    // params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    // int k = (int) (Math.random() * 8);
                    // LogUtils.d("Math.random()10=" + k);
                    // params.leftMargin = 10 * k;
                    // textView.setLayoutParams(params);
                    giftLayout.addView(textView, params);
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
                            giftLayout.removeView(textView);
                            if (!isZan) {
                                totlaGiftNum--;
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            giftLayout.removeView(textView);
                            if (!isZan) {
                                totlaGiftNum--;
                            }

                        }
                    });
                    ObjectAnimator ofFloat = ObjectAnimator
                            .ofFloat(textView, "translationY", (int) (giftLayout
                                            .getBottom() - giftLayout.getTop()),
                                    (int) ((giftLayout.getBottom() - giftLayout
                                            .getTop()) / 2 * Math.random()));
                    ofFloat.setDuration(4000);
                    int widthLength = (giftLayout.getRight() - giftLayout
                            .getLeft()) / 8;
                    int location = (int) (Math.random() * 6);
                    ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(textView,
                            "translationX", widthLength * (location + 1),
                            location * widthLength);
                    ofFloat2.setRepeatMode(ObjectAnimator.REVERSE);
                    ofFloat2.setRepeatCount(3);
                    ofFloat2.setDuration(700);
                    // ofFloat2.setInterpolator(new AccelerateInterpolator());
                    ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(textView,
                            "alpha", 0f, 1);
                    ofFloat3.setDuration(3000);
                    set.playTogether(ofFloat, ofFloat2, ofFloat3);
                    set.setInterpolator(new LinearInterpolator());// 动画速度逐步减小
                    // 动画周期为500ms
                    // set.setDuration(1 * 1000).start();
                    set.start();
                }
            }, (!isZan) ? (totlaGiftNum + j) * 100 : j * 100);
        }
        if (!isZan) {
            totlaGiftNum += total;
        }
    }


    /**
     * 获取主播power等信息
     */
    private void getUrlAndmChatRoom() {
        if (UserService.getInatance().getUserBean(this).getHerald().getVideoId() == null) {
            Utils.toast(this, "您没有直播");
//            finish();
            return;
        }
        leftSecends = (int) ((UserService.getInatance().getUserBean(this).getHerald().getStartTime() - System.currentTimeMillis()) / 1000);
        LogUtils.d("getUrlAndmChatRoom--leftSecends" + leftSecends);
        initUserInfo();
        initStartTime();

        try {
            LivePublisher.startPublish(publishUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.d("VIDEO_ID-----" + UserService.getInatance().getUserBean(this).getHerald().getVideoId());

    }


    private UserEntity currentUserEntity;

    //关闭直播
    private void closePublish() {

        if (TextUtils.isEmpty(mChatRoomId) || TextUtils.isEmpty(activityId)) {
            return;
        }
        RequestParams params = UrlTool.getParams(Constants.ACTIVITY_ID,
                activityId, Constants.CHATROOM_ID, mChatRoomId);
        SendActtionTool.get(Constants.UserParams.URL_END_PUBLISH, null, UserAction.ACTION_END_PUBLISH, this, params);
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        super.onSuccess(service, action, value);
        switch ((UserAction) action) {

            case ACTION_GETANCHOR_INFO://获取主播的power 赞等
                try {
                    anchorChatroomInfo = JSON.parseObject(((JSONObject) value).getString("data"), AnchorChatroomInfo.class);

                    initAnchorInfos(anchorChatroomInfo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            //主播增加时长
            case ACTION_ADDTIME_USERPOWER:
//                canGetTimeThroughPower = true;
//
//                LogUtils.d("ACTION_ADDTIME_USERPOWER---data=" + value.toString());
//                try {
//                    Utils.toast(this, "加时成功");
//                    Utils.sendBroadcastToMainactivityToUpdateUserinfo(this);
//                    int count = ((JSONObject) value).getJSONObject("data").getInt("count");
//                    long addTime = ((JSONObject) value).getJSONObject("data").getLong("addTime");
//                    int nextCount = ((JSONObject) value).getJSONObject("data").getInt("nextCount");
//                    long nextAddTime = ((JSONObject) value).getJSONObject("data").getLong("nextAddTime");
//
//                    liveLeftTime += (addTime / 1000);
////                    powerCount -= count;
////                    powerNum.setText(powerCount + "");
//                    timeWouldGet = nextAddTime;
//                    needCountPower = nextCount;
//                    timeAddText.setText("+" + nextAddTime / (1000 * 60) + "分");
//                    powerConsumeText.setText("(消耗" + nextCount + "power)");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                break;
            case ACTION_GETPEOPLEINFOS:
                try {
                    LogUtils.d("peopleLists.size()======" + peopleLists.size());
                    peopleLists = JSON.parseArray(((JSONObject) value).getString("data"), PeopleIdAndPicInfo.class);
                    LogUtils.d("peopleLists.size()======" + peopleLists.size());
                    controller.initPeoplePicListData(peopleLists);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case ACTION_END_PUBLISH:
                try {
//                    int exper = ((JSONObject) value).getJSONObject("data").getInt("exper"); //人气
                    int mbCount = ((JSONObject) value).getJSONObject("data").getInt("mbCount"); //mb
                    int praiseCount = ((JSONObject) value).getJSONObject("data").getInt("praiseCount"); //赞
                    int strawCount = ((JSONObject) value).getJSONObject("data").getInt("strawCount"); //草莓数
//                    int viewCount = ((JSONObject) value).getJSONObject("data").getInt("viewCount"); //观看
                    prizeNum.setText(String.valueOf(praiseCount));
                    powerNum.setText(String.valueOf(strawCount));
                    mbNum.setText(String.valueOf(mbCount));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
//            case ACTION_GETVIDEO_DETAIL:
//                try {
//                    ResultBean<ShowBean> resultBean = JSON.parseObject(
//                            value.toString(),
//                            new TypeReference<ResultBean<ShowBean>>() {
//                            });
//                    if (resultBean != null) {
//                        ShowBean data = resultBean.data;
//                        long endTime = TimeTool.getMillTime(data.getEndTime());
//
//                    } else {
//                        LogUtils.d("---resultBean == null");
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                break;
            case Action_GET_TOTLE_NUM:
                try {
                    int num = ((JSONObject) value).getJSONObject("data").getInt("onLineTotals");
//                    if (num >= controller.getPicList().size()) {
//                        onlinePeopleNum = num;
//                    }
                    onlinePeopleNum = num;
                    controller.getmAudienceNums().setText(String.valueOf(onlinePeopleNum));
                    LogUtils.d("onlinePeopleNum===" + onlinePeopleNum);
//                    onlinePeopleNum = ((JSONObject) value).getJSONObject("data").getInt("totals");
//                    peopleTotleNum = ((JSONObject) value).getJSONObject("data").getInt("totals");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    private long timeWouldGet;

    @Override
    public void onFinish(ServiceAction service, Object action) {
        super.onFinish(service, action);
        switch ((UserAction) action) {
            case ACTION_ADDTIME_USERPOWER:
                canGetTimeThroughPower = true;
                dismissDialog();
                break;

        }
    }

    private void initAnchorInfos(AnchorChatroomInfo info) {
//        prizeNum.setText(info.getPraiseCount() + "");
//        powerNum.setText(info.getStrawCount() + "");
//        mbNum.setText(info.getMbCount() + "");
        timeAddText.setText("+" + info.getCanAddTime() / (1000 * 60) + "分");
        powerConsumeText.setText("(消耗" + info.getNeedStraw() + "power)");
        LogUtils.d("timeAddText----" + info.getCanAddTime() / (1000 * 60) + "分");
        LogUtils.d("powerConsumeText----" + info.getNeedStraw() + "power");
        timeWouldGet = info.getCanAddTime();
        needCountPower = info.getNeedStraw();
        if (info.getAddTime() > 0) {
            initLeftTime(info.getAddTime());
        }
//        liveLeftTime = (int) ((UserService.getInatance().getUserBean(this).getHerald().getStartTime()+info.getAddTime() - System.currentTimeMillis()) / 1000);
        LogUtils.d("startTime=" + (UserService.getInatance().getUserBean(this).getHerald().getStartTime() +
                "addTime===" + info.getAddTime() + "nowTime=" + System.currentTimeMillis()) + "liveLeftTime==" + liveLeftTime);

    }


    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {
        super.onFaile(service, action, value);

        LogUtils.d("action----" + action.toString());
        Utils.toast(this, value.toString());
    }

    @Override
    public void onException(ServiceAction service, Object action, Object value) {
        super.onException(service, action, value);

        LogUtils.d("action----" + action.toString());
        Utils.toast(this, value.toString());
    }


    private void initGif() {
        network_gifimageview = (GifImageView) findViewById(R.id.network_gifimageview);
    }

    private List<String> gifList;

    public void queueDisplayGif(final XiuchanMessage message,
                                final boolean showGif, boolean isMyGift) {
        if (gifList == null)
            gifList = new ArrayList<String>();
        final String msgId = String.valueOf(message.getMsgId());
        if (TextUtils.isEmpty(message.getGifImgUrl())) {
            disPlayPicAnima(message.getPic(), message.getCount());
            return;
        }

        gifList.add(msgId);
        LogUtils.d("gifList.size()==" + gifList.size());

        int delayMillis = 0;
        if (isMyGift) {
            delayMillis = 0;
        } else {
            delayMillis = 2000 * gifList.size();
        }
        network_gifimageview.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (PreferencesUtils.TYPE_TANMU_SHOW) {
                    if (showGif) {
                        dispalyGIF(message.getGifImgUrl(), message.getGiftId(),
                                msgId);
                    } else {
                        gifList.remove(msgId);
                    }
                    disPlayPicAnima(message.getPic(), message.getCount());
                } else {
                    gifList.remove(msgId);
                }

            }
        }, delayMillis);
    }

    // 显示gif图
    private void dispalyGIF(String url, String gifId, String msgId) {
        String gifNmae = url.substring(url.lastIndexOf("/"));
        File file = new File(LocalCacheUtil.pictureFilePath, gifNmae);
        if (file.exists()) {
            try {
                displayGif(network_gifimageview, file.getPath(), msgId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            File gifSavePath1 = new File(LocalCacheUtil.cacheFilePath, gifNmae);
            loaderImage(url, gifSavePath1, network_gifimageview, msgId);
        }
    }

    private void displayGif(final GifImageView gifView, String path,
                            String msgId) throws IOException {
        GifDrawable gifFrom = new GifDrawable(path);
        gifView.setVisibility(View.VISIBLE);
        gifFrom.start();
        gifFrom.setLoopCount(1);
        gifFrom.addAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationCompleted() {
                network_gifimageview.setVisibility(View.GONE);
            }
        });
        network_gifimageview.setImageDrawable(gifFrom);
        if (gifList != null && gifList.contains(msgId)) {
            gifList.remove(msgId);
        }
    }

    protected void disPlayPicAnima(String pic, int count) {
        LogUtils.d("pic=" + pic);
        LogUtils.d("count=" + count);
//        int sqrt = (int) Math.sqrt(count);
//        if (sqrt < 1) {
//            sqrt = 1;
//        }
        // if (sqrt > 5) {
        // sqrt = 5;
        // }
        LogUtils.d("sqrt=" + count);
        doAnimateOpen(pic, count, false);
    }

    public void loaderImage(String url, final File saveFile,
                            final GifImageView gifView, final String msgId) {
        LogUtils.t("url========", url);

        HttpUtils http = new HttpUtils();
        // 下载图片
        http.download(url, saveFile.getAbsolutePath(), true, false,
                new RequestCallBack<File>() {

                    public void onSuccess(ResponseInfo<File> responseInfo) {
                        try {
                            LogUtils.t("onSuccess========",
                                    responseInfo.result.getAbsolutePath());
                            String sourceFilePath = responseInfo.result
                                    .getAbsolutePath();// 下载路径
                            String destFilePath = LocalCacheUtil.pictureFilePath// 复制路径
                                    .getAbsolutePath()
                                    + "/" + saveFile.getName();
                            if (FileUtils
                                    .copyFile(sourceFilePath, destFilePath)) {
                                LogUtils.d("复制文件成功");
                                FileUtils.deleteFile(responseInfo.result
                                        .getAbsolutePath());
                            }
                            displayGif(gifView, destFilePath, msgId);
                        } catch (Exception e) {
                            LogUtils.d("Exception========" + e.getMessage());
                        }
                    }

                    public void onFailure(HttpException error, String msg) {
                        LogUtils.e("onFailure========" + msg);
                    }
                });
    }


    public void initPublishView() {
        LivePublisher.init(this); // 1.初始化
        LivePublisher.setDelegate(this); // 2.设置事件回调

        /**
         * 设置输出音频参数 码率 32kbps 使用HE-AAC ,部分服务端不支持HE-AAC,会导致发布失败
         */
        LivePublisher.setAudioParam(32 * 1000, LivePublisher.AAC_PROFILE_HE);

        /**
         * 设置输出视频参数 宽 640 高 360 fps 15 码率 300kbps 以下建议分辨率及比特率 不用超过1280x720
         * 320X180@15 ~~ 200kbps 480X272@15 ~~ 250kbps 568x320@15 ~~ 300kbps
         * 640X360@15 ~~ 400kbps 720x405@15 ~~ 500kbps 854x480@15 ~~ 600kbps
         * 960x540@15 ~~ 700kbps 1024x576@15 ~~ 800kbps 1280x720@15 ~~ 1000kbps
         * 使用main profile
         */
        LivePublisher.setVideoParam(1024, 576, 15, 400 * 1000, LivePublisher.AVC_PROFILE_MAIN);

        /**
         * 是否开启背景噪音抑制
         */
        LivePublisher.setDenoiseEnable(true);

        /**
         * 开始视频预览， cameraPreview ： 用以回显摄像头预览的SurfaceViewd对象，如果此参数传入null，则只发布音频
         * interfaceOrientation ： 程序界面的方向，也做调整摄像头旋转度数的参数， camId：
         * 摄像头初始id，LivePublisher.CAMERA_BACK 后置，LivePublisher.CAMERA_FRONE 前置
         */
        LivePublisher.startPreview(sv, getWindowManager().getDefaultDisplay().getRotation(), LivePublisher.CAMERA_BACK); // 5.开始预览
        // 如果传null
        // 则只发布音频
        LogUtils.d("sv startPreview" + ((sv == null) ? " sv == null" : " sv != null"));
    }

    private GestureDetector mGestureDetector;

    public GestureDetector getGestureDetector() {
        if (mGestureDetector == null) {
            mGestureDetector = new GestureDetector(this,
                    new MyGestureListener());
        }
        return mGestureDetector;
    }

    private void fadeIn(final View view) {
        shouldDoControllerAnimation = false;
        Animation mySpaceAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
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
        Animation mySpaceAnim = AnimationUtils.loadAnimation(this, R.anim.fade_out);
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

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        /**
         * 双击
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            LogUtil.d("LivePublishActivity-----onSingleTapConfirmed");
            if (shouldDoControllerAnimation) {
                if (controller.isShow()) {


                    fadeOut(controller);
                    controller.hide();
                } else {
                    fadeIn(controller);
                    controller.show();
                }
//                doAnimateOpen("sss", 20);
            }
            return false;
        }

        /**
         * 滑动
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            return super.onScroll(e1, e2, distanceX, distanceY);
        }

    }

    @Override
    public void onBackPressed() {
//        closePublish();
//        super.onBackPressed();
    }

    private void stopFinish() {
        shouldClose = true;
        initMessage("", 6,"","");
        LivePublisher.stopPreview();
        LivePublisher.stopPublish();// 停止发布
//        timer.cancel();
        Utils.sendBroadcastToMainactivityToUpdateUserinfo(this);
        Utils.startTranscripts(LivePublishActivity.this, activityId, mChatRoomId);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play_livemedia:
                dialog();
//                if (isStarting) {
//                }
//                    stopFinish();
//                } else {
//                    /**
//                     * 设置视频发布的方向，此方法为可选，如果不调用，则输出视频方向跟随界面方向，如果特定指出视频方向，
//                     * 在startPublish前调用设置 videoOrientation ： 视频方向 VIDEO_ORI_PORTRAIT
//                     * home键在 下 的 9:16 竖屏方向 VIDEO_ORI_LANDSCAPE home键在 右 的 16:9 横屏方向
//                     * VIDEO_ORI_PORTRAIT_REVERSE home键在 上 的 9:16 竖屏方向
//                     * VIDEO_ORI_LANDSCAPE_REVERSE home键在 左 的 16:9 横屏方向
//                     */
//                    // LivePublisher.setVideoOrientation(LivePublisher.VIDEO_ORI_PORTRAIT);
//
//                    /**
//                     * 开始视频发布 rtmpUrl rtmp流地址
//                     */
////				String pubUrl = SharedPreUtil.getString(this, "pubUrl");
//                    LivePublisher.startPublish(publishUrl);
//
//
//                }
                break;
            case R.id.btn_moretime_livemedia:
                LogUtils.d("canGetTimeThroughPower----" + canGetTimeThroughPower);
                if (canGetTimeThroughPower) {
                    getMoerTimeThroughPower();
                }
                break;
            case R.id.action0:
                break;
        }
    }

    private void initMediaController() {
        controller = (LivePublisherMediacontroler) findViewById(R.id.livePublisherMedia);
        controller.setListenner(listener);
    }

    AlertDialog stopDialog;

    protected void dialog() {
        if (stopDialog == null) {
            stopDialog = new AlertDialog.Builder(LivePublishActivity.this)
                    .setTitle("提示")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // zwz  主播退出界面
                            sendBack();
                            dialog.dismiss();
                            stopFinish();


                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setMessage("确认结束直播吗？").create();
        }
        stopDialog.show();

    }

    /**
     * 主播退出调用
     */
    private void sendBack(){
        // zwz
        // http://210.14.158.158:8686/endEngine?videoId=9999&anchorId=5636dc190cf283d14ac4a885&praiseCount=100
        RequestParams params = UrlTool.getPostParams(Constants.VIDEO_ID, String.valueOf(anchorChatroomInfo.getVideoId()),
                "anchorId", anchorChatroomInfo.getUserId());
        SendActtionTool.post(Constants.URL_ZHIBO_END, null, UserAction.ACTION_ZHIBO_END, this, params);

        LogUtils.d("zwz=====back",String.valueOf(anchorChatroomInfo.getVideoId())+
                "============================="+anchorChatroomInfo.getUserId());
    }

    private void failedDialog() {
        if (failedDialog == null) {
            failedDialog = new AlertDialog.Builder(LivePublishActivity.this)
                    .setTitle("提示")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LivePublisher.startPublish(publishUrl);


                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            stopFinish();
                        }
                    })
                    .setMessage("直播中断，是否重新推流").create();
        }
        try {
            failedDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    OnLivePublishMediacontrollerListener listener = new OnLivePublishMediacontrollerListener() {
        @Override
        public void onPlayBtnClick() {
            if (isStarting) {

                LivePublisher.stopPublish();// 停止发布
            }
//            else {
//                /**
//                 * 设置视频发布的方向，此方法为可选，如果不调用，则输出视频方向跟随界面方向，如果特定指出视频方向，
//                 * 在startPublish前调用设置 videoOrientation ： 视频方向 VIDEO_ORI_PORTRAIT
//                 * home键在 下 的 9:16 竖屏方向 VIDEO_ORI_LANDSCAPE home键在 右 的 16:9 横屏方向
//                 * VIDEO_ORI_PORTRAIT_REVERSE home键在 上 的 9:16 竖屏方向
//                 * VIDEO_ORI_LANDSCAPE_REVERSE home键在 左 的 16:9 横屏方向
//                 */
//                // LivePublisher.setVideoOrientation(LivePublisher.VIDEO_ORI_PORTRAIT);
//
//                /**
//                 * 开始视频发布 rtmpUrl rtmp流地址
//                 */
////				String pubUrl = SharedPreUtil.getString(this, "pubUrl");
//                LivePublisher.startPublish(publishUrl);
//            }
        }

        @Override
        public void onMesBtnClick(String hint,String toUserName,String toUserId) {
            showTanmuDialog(hint,toUserName,toUserId);
//            Utils.toast(LivePublishActivity.this, "聊天的按钮");
        }

        @Override
        public void onLightClick() {
            LogUtils.d("activity---onLightClick");
            int ret = -1;
            if (isFlsOn) {
                ret = LivePublisher.setFlashEnable(false);
            } else {
                ret = LivePublisher.setFlashEnable(true);
            }
            if (ret == -1) {
                // 无闪光灯,或处于前置摄像头,不支持闪光灯操作
            } else if (ret == 0) {
                // 闪光灯被关闭
                controller.getmLightBtn().setBackgroundResource(R.drawable.but_shanguang);
//                flashBtn.setBackgroundResource(R.drawable.ic_flash_off);
                isFlsOn = false;
            } else {
                // 闪光灯被打开
                controller.getmLightBtn().setBackgroundResource(R.drawable.but_shanguang_on);
//                flashBtn.setBackgroundResource(R.drawable.ic_flash_on);
                isFlsOn = true;
            }
        }

        @Override
        public void onCamClick() {
            LivePublisher.switchCamera();// 切换前后摄像头
            LivePublisher.setFlashEnable(false);// 关闭闪光灯,前置不支持闪光灯
            isFlsOn = false;
//            controller.getmLightBtn().setBackgroundResource(R.drawable.ic_flash_off);
//            flashBtn.setBackgroundResource(R.drawable.ic_flash_off);
        }

        @Override
        public void onMicClick() {

        }

        @Override
        public void onScreenPrinted() {

        }

        @Override
        public void onCameraCloseOrOpen() {
            if (isStarting) {
                isCamOn = !isCamOn;
                LivePublisher.setCamEnable(isCamOn);
                if (isCamOn) {
                    handler.sendEmptyMessage(2103);
                } else {
                    handler.sendEmptyMessage(2102);
                }
            }
        }

        @Override
        public void onShareClicked() {
            //分享按钮
//            Utils.toast(LivePublishActivity.this, "分享的按钮");
        }

        @Override
        public void onCloseOrOpenMessageClicked(boolean showDanmuList) {
            //关闭和打开弹幕
//            DialogTool.createPeopleInfoDialog(LivePublishActivity.this, peopleInfoListenner);
        }

        @Override
        public void onUserImgClick() {

        }

        @Override
        public void onPeopleImgClicked(int num) {
            if (controller.getPicList().get(num).getId().equals(UserService.getInatance().getUserBean(LivePublishActivity.this).getId())) {
                return;
            }
            new PeopleInfoDialog(LivePublishActivity.this, controller.getPicList().get(num).getId()).show();
        }

        @Override
        public void onMessageImgClicked(String userId) {
            if (!userId.equals(UserService.getInatance().getUserBean(LivePublishActivity.this).getId())) {
                new PeopleInfoDialog(LivePublishActivity.this, userId).show();
            }
        }

        @Override
        public void onGetMoreTimeClicked() {
//            LogUtils.d("canGetTimeThroughPower----" + canGetTimeThroughPower);
//            if (canGetTimeThroughPower) {
//                getMoerTimeThroughPower();
//            }
        }
    };

    @Override
    public void onEventCallback(int i, String s) {
        handler.sendEmptyMessage(i);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector != null && mGestureDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    private void initTime() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                LogUtils.d("leftSecends===" + leftSecends + "liveLeftTime===" + liveLeftTime);
                if (leftSecends > 0) {
//                    if (System.currentTimeMillis() >= UserService.getInatance().getUserBean(LivePublishActivity.this).getHerald().getStartTime() - 15 * 1000 * 60) {
//                    }
                    leftSecends--;
                    mTimeLeftText.setText(TimeTool.getTimeFromSec(leftSecends));
                } else {
                    if (!hasReachedTime) {
                        timeState.setText("距离直播结束");
                        dotImg.setImageResource(R.drawable.dot_red);
                        hasReachedTime = true;
                    }
                    if (liveLeftTime > 0) {
                        liveLeftTime--;
                        mTimeLeftText.setText(TimeTool.getTimeFromSec(liveLeftTime));//直播后的倒计时
                    } else {
                        stopFinish();
                    }
                }


            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private int pubLishTimes;
//    private Handler handler = new Handler() {
//        // 回调处理
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//
//                case 2000:
//                    Toast.makeText(LivePublishActivity.this, "正在发布视频", Toast.LENGTH_SHORT).show();
//                    break;
//                case 2001:
//                    pubLishTimes = 3;
//                    Toast.makeText(LivePublishActivity.this, "视频发布成功", Toast.LENGTH_SHORT).show();
////                    mPlayButton.setBackgroundResource(R.drawable.ic_video_start);
////                    videoBtn.setBackgroundResource(R.drawable.ic_video_start);
//                    isStarting = true;
////                    handler.sendEmptyMessage(0);
//                    break;
//                case 2002:
//                    pubLishTimes--;
//                    if (pubLishTimes >= 0) {
//                        LivePublisher.startPublish(publishUrl);
//                    } else {
//                        failedDialog();
////                        Toast.makeText(LivePublishActivity.this, "视频发布失败", Toast.LENGTH_SHORT).show();
//                    }
//                    break;
//                case 2004:
//                    if (!shouldClose) {
//                        pubLishTimes--;
//                        if (pubLishTimes >= 0 && liveLeftTime > 0) {
//                            failedDialog();
//                        } else {
//                            Toast.makeText(LivePublishActivity.this, "视频中断", Toast.LENGTH_SHORT).show();
//                            isStarting = false;
//                            stopFinish();
//                        }
//                    }
//
//                    break;
//                case 2005:
//                    if (!shouldClose) {
//                        pubLishTimes--;
//                        if (pubLishTimes >= 0 && liveLeftTime > 0) {
//                            failedDialog();
//                        } else {
//                            Toast.makeText(LivePublishActivity.this, "网络异常,发布中断", Toast.LENGTH_SHORT).show();
//                            isStarting = false;
//                            stopFinish();
//                        }
//                    }
//
////                    if (liveLeftTime > 0) {
////                        failedDialog();
////                        return;
////                    }
////                    Toast.makeText(LivePublishActivity.this, "网络异常,发布中断", Toast.LENGTH_SHORT).show();
//                    break;
//                case 2100:
////                    // mic off
////                    micBtn.setBackgroundResource(R.drawable.ic_mic_off);
////                    Toast.makeText(LivePublishActivity.this, "麦克风静音", Toast.LENGTH_SHORT).show();
//                    break;
//                case 2101:
////                    // mic on
////                    micBtn.setBackgroundResource(R.drawable.ic_mic_on);
////                    Toast.makeText(LivePublishActivity.this, "麦克风恢复", Toast.LENGTH_SHORT).show();
//                    break;
//                case 2102:
////                    // camera off
////                    camBtn.setBackgroundResource(R.drawable.ic_cam_off);
////                    Toast.makeText(LivePublishActivity.this, "摄像头传输关闭", Toast.LENGTH_SHORT).show();
//                    break;
//                case 2103:
////                    // camera on
////                    camBtn.setBackgroundResource(R.drawable.ic_cam_on);
////                    Toast.makeText(LivePublishActivity.this, "摄像头传输打开", Toast.LENGTH_SHORT).show();
//                    break;
//                case 0:
////                    if (leftSecends > 0) {
////
////                        msg = obtainMessage(0);
////                        handler.sendMessageDelayed(msg, 1000);
////                        initTime();
////                    } else {
////                        stopFinish();
////                    }
//                    break;
//                case 5:
//                    LogUtils.d("XiuchanMessage+++   case5");
//                    XiuchanMessage message = (XiuchanMessage) msg.obj;
//                    controller.addMessage(message);
////                    boolean isMyGift = false;
////                    if (message != null)
////                        isMyGift = UserService.getInatance().getUserBean(LivePublishActivity.this).getId().equals(message.getGiftUserId());
//                    switch (message.getType()) {
//                        // 顶部和底部部通知
//                        case 3:
//                        case 4:
//                            LivePublishActivity.this.queueDisplayGif(message, !TextUtils.isEmpty(message.getGifImgUrl()), false);
//                            break;
//                        // 顶端通知
//                        case 2:
//                        case 1:
//                        default:
//                            addPeopleAndUpdatePowerInfos(message);
//                            break;
//                    }
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
    private WeakHandler handler = new WeakHandler(this) {
        // 回调处理
        @Override
        public void conventHandleMessage( Message msg) {
//            super.handleMessage(msg);
            LogUtils.d("msgwhat----"+msg.what+"pubLishTimes---"+pubLishTimes+"shouldClose---"+shouldClose);
            switch (msg.what) {

                case 2000:
                    Toast.makeText(LivePublishActivity.this, "正在发布视频", Toast.LENGTH_SHORT).show();
                    break;
                case 2001:
                    pubLishTimes = 3;
                    Toast.makeText(LivePublishActivity.this, "视频发布成功", Toast.LENGTH_SHORT).show();
//                    mPlayButton.setBackgroundResource(R.drawable.ic_video_start);
//                    videoBtn.setBackgroundResource(R.drawable.ic_video_start);
                    isStarting = true;
//                    handler.sendEmptyMessage(0);
                    break;
                case 2002:
                    pubLishTimes--;
                    if (pubLishTimes >= 0) {
                        LivePublisher.startPublish(publishUrl);
                    } else {
                        failedDialog();
//                        Toast.makeText(LivePublishActivity.this, "视频发布失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2004:
                    if (!shouldClose) {
                        pubLishTimes--;
                        if (pubLishTimes >= 0 && liveLeftTime > 0) {
                            failedDialog();
                        } else {
                            Toast.makeText(LivePublishActivity.this, "视频中断", Toast.LENGTH_SHORT).show();
                            isStarting = false;
                            stopFinish();
                        }
                    }

                    break;
                case 2005:
                    if (!shouldClose) {
                        pubLishTimes--;
                        if (pubLishTimes >= 0 && liveLeftTime > 0) {
                            failedDialog();
                        } else {
                            Toast.makeText(LivePublishActivity.this, "网络异常,发布中断", Toast.LENGTH_SHORT).show();
                            isStarting = false;
                            stopFinish();
                        }
                    }

//                    if (liveLeftTime > 0) {
//                        failedDialog();
//                        return;
//                    }
//                    Toast.makeText(LivePublishActivity.this, "网络异常,发布中断", Toast.LENGTH_SHORT).show();
                    break;
                case 2100:
//                    // mic off
//                    micBtn.setBackgroundResource(R.drawable.ic_mic_off);
//                    Toast.makeText(LivePublishActivity.this, "麦克风静音", Toast.LENGTH_SHORT).show();
                    break;
                case 2101:
//                    // mic on
//                    micBtn.setBackgroundResource(R.drawable.ic_mic_on);
//                    Toast.makeText(LivePublishActivity.this, "麦克风恢复", Toast.LENGTH_SHORT).show();
                    break;
                case 2102:
//                    // camera off
//                    camBtn.setBackgroundResource(R.drawable.ic_cam_off);
//                    Toast.makeText(LivePublishActivity.this, "摄像头传输关闭", Toast.LENGTH_SHORT).show();
                    break;
                case 2103:
//                    // camera on
//                    camBtn.setBackgroundResource(R.drawable.ic_cam_on);
//                    Toast.makeText(LivePublishActivity.this, "摄像头传输打开", Toast.LENGTH_SHORT).show();
                    break;
                case 0:
//                    if (leftSecends > 0) {
//
//                        msg = obtainMessage(0);
//                        handler.sendMessageDelayed(msg, 1000);
//                        initTime();
//                    } else {
//                        stopFinish();
//                    }
                    break;
                case 5:
                    LogUtils.d("XiuchanMessage+++   case5");
                    XiuchanMessage message = (XiuchanMessage) msg.obj;
                    controller.addMessage(message);
//                    boolean isMyGift = false;
//                    if (message != null)
//                        isMyGift = UserService.getInatance().getUserBean(LivePublishActivity.this).getId().equals(message.getGiftUserId());
                    switch (message.getType()) {
                        // 顶部和底部部通知
                        case 3:
                        case 4:
                            LivePublishActivity.this.queueDisplayGif(message, !TextUtils.isEmpty(message.getGifImgUrl()), false);
                            break;
                        // 顶端通知
                        case 2:
                        case 1:
                        default:
                            addPeopleAndUpdatePowerInfos(message);
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
};


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 注意：如果你的业务方案需求只做单一方向的视频直播，可以不处理这段

        // 如果程序UI没有锁定屏幕方向，旋转手机后，请把新的界面方向传入，以调整摄像头预览方向
        LivePublisher.setCameraOrientation(getWindowManager().getDefaultDisplay().getRotation());

        // 还没有开始发布视频的时候，可以跟随界面旋转的方向设置视频与当前界面方向一致，但一经开始发布视频，是不能修改视频发布方向的了
        // 请注意：如果视频发布过程中旋转了界面，停止发布，再开始发布，是不会触发"onConfigurationChanged"进入这个参数设置的
        if (!isStarting) {
            switch (getWindowManager().getDefaultDisplay().getRotation()) {
                case Surface.ROTATION_0:
                    LivePublisher.setVideoOrientation(LivePublisher.VIDEO_ORI_PORTRAIT);
                    break;
                case Surface.ROTATION_90:
                    LivePublisher.setVideoOrientation(LivePublisher.VIDEO_ORI_LANDSCAPE);
                    break;
                case Surface.ROTATION_180:
                    LivePublisher.setVideoOrientation(LivePublisher.VIDEO_ORI_PORTRAIT_REVERSE);
                    break;
                case Surface.ROTATION_270:
                    LivePublisher.setVideoOrientation(LivePublisher.VIDEO_ORI_LANDSCAPE_REVERSE);
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
//        unregisterReceiver(myReceive);
//        chatQuit();
        timer.cancel();
        super.onDestroy();
        Utils.sendBroadcastToService(8, this);
        sv = null;
        LivePublisher.stopPreview();
        LivePublisher.stopPublish();
    }

    /**
     * @param value
     * @param type  1 5  普通消息 8  我来了  6 结束
     */
    private void initMessage(String value, int type,String toUserName,String toUserId) {
        LogUtils.d("initMessage---------toUserName==="+toUserName+"toUserId==="+toUserId);
        XiuchanMessage sendXiuchangMsg = new XiuchanMessage();
        if (UserService.getInatance().getUserBean(this).getId().equals(toUserId)) {
            toUserName="";
            toUserId="";
        }
        sendXiuchangMsg.setToUserName(toUserName);
        sendXiuchangMsg.setToUserId(toUserId);
//        if (toUserName != null) {
//            sendXiuchangMsg.setToUserName(toUserName);
//            sendXiuchangMsg.setToUserId(toUserId);
//        } else {
//            sendXiuchangMsg.setToUserName(null);
//        }
        sendXiuchangMsg.setMsg(CheckCode.checkSensitive(this, value));
        sendXiuchangMsg.setFromUserName(UserService.getInatance().getUserBean(this).getUserName());
        sendXiuchangMsg.setFromUserId(UserService.getInatance().getUserBean(this).getId());
        if (type == 1 && !TextUtils.isEmpty(toUserName)) {
            sendXiuchangMsg.setType(5);
        } else {
            sendXiuchangMsg.setType(type);
        }
//        if (isComming) {
//            sendXiuchangMsg.setType(8);
//        }
        sendXiuchangMsg.setFromUserPic(UserService.getInatance().getUserBean(this).getFaceUrl());
        sendMessage(sendXiuchangMsg);
    }


    private int reContect = 3;

    private void goToChatRoom() {
        if (UserService.getInatance().getUserBean(this).getHerald().getChatroomId() == null) {
            Utils.toast(this, "获取聊天室信息失败");
//            finish();
            return;
        }
//        this.mChatRoomId = UserService.getInatance().getUserBean(this).getHerald().getChatroomId();
        LogUtils.d("getHeraldmChatroomId=========");
        chatConnectServer();
    }

    /**
     * 连接到服务器
     */
    private void chatConnectServer() {
        if (reContect < 0) {
            Utils.toast(this, "加入聊天室失败！");
            return;
        }
        reContect--;
        if (!ThreeAppParams.IS_CONNECT_SERVER) {
            LogUtils.t(ShowActivity.class.getName(),
                    "开始连接容云服务" + UserService.getInatance().getUserBean(this).getRongcloudToken());
            RongIMClient.connect(UserService.getInatance().getUserBean(this).getRongcloudToken(),
                    new RongIMClient.ConnectCallback() {
                        @Override
                        public void onSuccess(String arg0) {
                            ThreeAppParams.IS_CONNECT_SERVER = true;
                            LogUtils.t(ShowActivity.class.getName(),
                                    "链接容云服务器成功");
                            chatListener();
                        }

                        @Override
                        public void onTokenIncorrect() {
                            LogUtils.t(ShowActivity.class.getName(), "连接失败");
                        }

                        @Override
                        public void onError(
                                io.rong.imlib.RongIMClient.ErrorCode arg0) {
                            LogUtils.t(ShowActivity.class.getName(), "连接失败" + arg0);
                        }
                    });
        } else {
            chatListener();
        }
    }
    private String toUserName,toUserId;
    public void showTanmuDialog(String hint, final String toUserName, final String toUserId) {
        LogUtils.d("showTanmuDialog------toUserName==="+toUserName+"toUserId==="+toUserId);
        this.toUserName=toUserName;
        this.toUserId=toUserId;
        if (mTanmuDialog == null)
            mTanmuDialog = new TanmuDialog(this, new TanmuDialog.OnSendTanmuListener() {
                @Override
                public void sendMessage(String edtStr) {
                    if (hasEnteredChatroom) {
                        initMessage(edtStr, 1,LivePublishActivity.this.toUserName,LivePublishActivity.this.toUserId);
                    }
//                    LivePublishActivity.this.sendMessage(edtStr);
                }
            });
        mTanmuDialog.show();
        if (!TextUtils.isEmpty(hint)) {
            if (!UserService.getInatance().getUserBean(LivePublishActivity.this).getId().equals(toUserId)) {
                mTanmuDialog.setHintMethod(hint);
            }
        }
    }

    private void initChatRoomPeopleAndNum() {
        PeopleIdAndPicInfo people = new PeopleIdAndPicInfo();
        people.setId(UserService.getInatance().getUserBean(this).getId());
        people.setFaceUrl(UserService.getInatance().getUserBean(this).getFaceUrl());
        peopleLists.clear();
        peopleLists.add(people);
        controller.initPeoplePicListData(peopleLists);
        controller.getmAudienceNums().setText("1");
    }

    /**
     * 加入聊天时 获取新的消息
     */
    private void chatListener() {
        if (TextUtils.isEmpty(mChatRoomId)) {
            return;
        }
        RongIMClient.getInstance().joinChatRoom(mChatRoomId, 20,
                new RongIMClient.OperationCallback() {
                    @Override
                    public void onSuccess() {
                        LogUtils.t(LiveActivity.class.getName(), "加入聊天室成功_:"
                                + mChatRoomId);
                        hasEnteredChatroom = true;

                        initMessage("进入了房间", 8,"","");
//                        initChatRoomNum();
                        initChatRoomPeopleAndNum();
//                        getChatRoomInfo();
                    }

                    @Override
                    public void onError(
                            io.rong.imlib.RongIMClient.ErrorCode arg0) {
                        LogUtils.t(LiveActivity.class.getName(), "加入聊天室失败" + arg0.toString());
                        chatConnectServer();
                    }
                });
        RongIMClient
                .setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageListener() {
                    @Override
                    public boolean onReceived(io.rong.imlib.model.Message mes, int arg1) {
                        // 输出消息类型。
                        MessageContent content = mes.getContent();
                        // 此处输出判断是否是文字消息，并输出，其他消息同理。
                        if (!(content instanceof TextMessage)) {
                            return false;
                        }
                        LogUtils.d("XiuchanMessage+++   收到消息");
                        String string = ((TextMessage) content).getContent();
                        if (string == null || !string.contains("type")) {
                            return false;
                        }
                        LogUtils.d("XiuchanMessage1111---" + string);
                        XiuchanMessage obj = JSON.parseObject(string,
                                XiuchanMessage.class);
                        obj.setMsgId(mes.getMessageId());
                        obj.setTime(TimeTool.getTimeStr(
                                new Date(mes.getSentTime()), LivePublishActivity.this));
                        LogUtils.d("XiuchanMessage-----" + obj.getType() + obj.toString());
                        android.os.Message message = new android.os.Message();
                        message.obj = obj;
                        message.what = 5;
                        handler.sendMessage(message);
                        return false;
                    }

                });

    }


    /**
     * //获取聊天室信息
     */
    private void getChatRoomInfo() {

        RongIMClient.getInstance().getChatRoomInfo(mChatRoomId, 20, ChatRoomInfo.ChatRoomMemberOrder.RC_CHAT_ROOM_MEMBER_ASC, new RongIMClient.ResultCallback<ChatRoomInfo>() {
            @Override
            public void onSuccess(ChatRoomInfo chatRoomInfo) {
                int count = chatRoomInfo.getTotalMemberCount();
                List<ChatRoomMemberInfo> memberInfo = chatRoomInfo.getMemberInfo();
                String ids = "";
                for (int i = 0; i < memberInfo.size(); i++) {
                    if (i == 0) {
                        ids += memberInfo.get(i).getUserId();
                    } else {
                        ids += "," + memberInfo.get(i).getUserId();
                    }
                    LogUtils.d("peopleid" + memberInfo.get(i).getUserId());
                }
                LogUtils.d("count=====" + count);
//                num = count;
//                String userId = memberInfo.get(0).getUserId();
//                mShowActivity.setPeopleNum(count);
//                mShowActivity.getPeopleInfo(ids);
//                getPeopleInfo(ids);
//                controller.getmAudienceNums().setText(num + "");
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }


    private void addPeopleAndUpdatePowerInfos(XiuchanMessage message) {
        if (message.getType() == 8) {
            PeopleIdAndPicInfo people = new PeopleIdAndPicInfo();
            people.setId(message.getFromUserId());
            people.setFaceUrl(message.getFromUserPic());
            onGetPeopleNum();
            if (peopleLists.contains(people))
                return;
            peopleLists.add(0, people);
            if (peopleLists.size() > 20) {
                peopleLists.remove(20);
            }
//            onlinePeopleNum++;
            LogUtils.d("message.getType() == 8onlinePeopleNum===" + onlinePeopleNum);
//            controller.getmAudienceNums().setText(onlinePeopleNum+"");
            controller.initPeoplePicListData(peopleLists);
        } else if (message.getType() == 7) {

            LogUtils.d("zengjia shichang  ----" + message.getOnceTime() + "zengjia shichang  ----" + message.getAddTime());
            LogUtils.d("zengjia shichang  ----" + message.getOnceTime() / 60000 + "zengjia shichang  ----" + message.getAddTime() / 60000);
            prizeNum.setText(String.valueOf(message.getPraiseCount()));
            powerNum.setText(String.valueOf(message.getStrawCount()));
//            mbNum.setText(message.getMbCount() + "");
            mbNum.setText(String.valueOf(message.getExper() ));
            if (message.getOnceTime() != 0) {
                Utils.toast(LivePublishActivity.this, "加时成功");
                long addTime = message.getAddTime();
                initLeftTime(addTime);
            }
//            mShowActivity.updateLeftTime(addTime);
        }

    }

    private void initLeftTime(long addTime) {
        if ((UserService.getInatance().getUserBean(this).getHerald().getStartTime() >= System.currentTimeMillis())) {
            liveLeftTime = (int) addTime / 1000;
        } else {
            liveLeftTime = (int) ((UserService.getInatance().getUserBean(this).getHerald().getStartTime() + addTime - System.currentTimeMillis()) / 1000);
        }
    }

    /**
     * 退出聊天室
     */
    private void chatQuit() {
        if (mChatRoomId == null) {
            return;
        }
        RongIMClient.getInstance().quitChatRoom(mChatRoomId,
                new RongIMClient.OperationCallback() {
                    @Override
                    public void onSuccess() {
                        LogUtils.t(LiveActivity.class.getName(), "退出聊天室成功");
                    }

                    @Override
                    public void onError(
                            io.rong.imlib.RongIMClient.ErrorCode arg0) {
                        LogUtils.t(LiveActivity.class.getName(), "退出聊天室失败");
                    }
                });
    }

    //发送消息
    private void sendMessage(final XiuchanMessage sendXiuchangMsg) {
        final String jsonString = JSON.toJSONString(sendXiuchangMsg);
        TextMessage messageContent = TextMessage.obtain(jsonString);
        LogUtils.d("sendMessage" + jsonString);
        // 发送消息的封装
        RongIMClient.getInstance().sendMessage(Conversation.ConversationType.CHATROOM,
                mChatRoomId, messageContent, UserService.getInatance().getUserBean(LivePublishActivity.this).getId(), "",
                new RongIMClient.SendMessageCallback() {
                    @Override
                    public void onError(Integer arg0, RongIMClient.ErrorCode arg1) {
                        LogUtils.t(ShowActivity.class.getName(), "Integer:"
                                + arg0 + "---发送消息失败:" + arg1.toString());
                        ifhasSendOut = true;
                        // btn_send.setEnabled(true);
                    }

                    @Override
                    public void onSuccess(Integer arg0) {
                        sendMsgSuccess(sendXiuchangMsg, jsonString, arg0);
                        // btn_send.setEnabled(true);
                        ifhasSendOut = true;
                        LogUtils.d("onSuccess111111111");
                    }
                }, new RongIMClient.ResultCallback<io.rong.imlib.model.Message>() {
                    @Override
                    public void onSuccess(io.rong.imlib.model.Message arg0) {
                        LogUtils.t(ShowActivity.class.getName(), "发送消成功:"
                                + arg0.toString());
                        // sendMsgSuccess(sendXiuchangMsg, jsonString,
                        // arg0.getMessageId());
                        // btn_send.setEnabled(true);
                        ifhasSendOut = true;
                        LogUtils.d("onSuccess222222222");
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode arg0) {
                        LogUtils.t(ShowActivity.class.getName(), "发送消息失败:"
                                + arg0.toString());
                        // btn_send.setEnabled(true);
                        ifhasSendOut = true;
                    }
                });
    }

    private void sendMsgSuccess(final XiuchanMessage sendXiuchangMsg,
                                final String jsonString, Integer arg0) {
        LogUtils.t(ShowActivity.class.getName(), "发送消息成功");
        if (sendXiuchangMsg != null) {
            sendXiuchangMsg.setTime("刚刚");
            sendXiuchangMsg.setMsgId(arg0);
            android.os.Message message = new android.os.Message();
            message.obj = sendXiuchangMsg;
            message.what = 5;
            handler.sendMessage(message);
        }
//        setToUserName(null);
//        edtChat.setHint(null);
//        // 发送回复功能
//        if (sendXiuchangMsg.getToUserName() != null) {
//            sendHuiFu(jsonString, String.valueOf(arg0), mChatroomId);
//        }
    }

//    public class MyReceive extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            switch (intent.getAction()) {
//                case Intent.ACTION_SCREEN_ON:
//                    LogUtils.d("livepublishactivity--Intent.ACTION_SCREEN_ON");
//                    break;
//                case Intent.ACTION_SCREEN_OFF:
//                    LogUtils.d("livepublishactivity--Intent.ACTION_SCREEN_OFF");
//                    finish();
//                    break;
//            }
//        }
//    }

}

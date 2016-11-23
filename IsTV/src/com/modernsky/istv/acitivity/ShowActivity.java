/**
 *
 */
package com.modernsky.istv.acitivity;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lecloud.entity.ActionInfo;
import com.letv.controller.PlayContext;
import com.letv.controller.PlayProxy;
import com.letv.simple.utils.LetvParamsUtils;
import com.letv.simple.utils.PlayerFactory;
import com.letv.universal.iplay.EventPlayProxy;
import com.letv.universal.iplay.ISplayer;
import com.letv.universal.iplay.OnPlayStateListener;
import com.letv.universal.play.util.PlayerParamsHelper;
import com.letv.universal.widget.ILeVideoView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.action.XiuchangAction;
import com.modernsky.istv.bean.FormWuTaiInfo;
import com.modernsky.istv.bean.GiftBean;
import com.modernsky.istv.bean.LiveInfo;
import com.modernsky.istv.bean.PeopleIdAndPicInfo;
import com.modernsky.istv.bean.RechargeBean;
import com.modernsky.istv.bean.ResultBean;
import com.modernsky.istv.bean.ShowBean;
import com.modernsky.istv.bean.ShowLatestVideo;
import com.modernsky.istv.bean.UserEntity;
import com.modernsky.istv.bean.XiuchanMessage;
import com.modernsky.istv.fragment.ChatRoomShowFragment;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.service.XiuchangService;
import com.modernsky.istv.tool.BitmapTool;
import com.modernsky.istv.tool.DialogTool;
import com.modernsky.istv.tool.DialogTool.DialogLister;
import com.modernsky.istv.tool.NetworkHelper;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.TimeTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.Constants.UserParams;
import com.modernsky.istv.utils.FileUtils;
import com.modernsky.istv.utils.GeneralTool;
import com.modernsky.istv.utils.JsonUtils;
import com.modernsky.istv.utils.LocalCacheUtil;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.PreferencesUtils;
import com.modernsky.istv.utils.ScreenUtils;
import com.modernsky.istv.utils.Utils;
import com.modernsky.istv.utils.WeakHandler;
import com.modernsky.istv.view.DZZThreeSharePop;
import com.modernsky.istv.view.ShowNewMediacontroler;
import com.modernsky.istv.window.GiftDialog;
import com.modernsky.istv.window.PeopleInfoDialog;
import com.modernsky.istv.window.TanmuDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * @author： fengqingyun2008
 * @Email： fengqingyun2008@gmail.com
 * @version：1.0
 * @创建时间：2015-5-29 下午5:39:00
 * @类说明：秀场
 */
@SuppressLint({"Override", "NewApi"})
public class ShowActivity extends BaseActivity
        implements OnPlayStateListener {
    ShowNewMediacontroler mControler;

    private String mVideoId;
    private View mLoadingView;
    private View mShowContent;

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    Timer timer;
    private RelativeLayout show_gift;
    private String mStreamId = "201510123000113";
    private String mLiveId = "201510123000113";
    // 乐视用
    private RelativeLayout mPlayerLayoutView;
    private boolean isBackgroud;

    public UserEntity getUserBean() {
        return userBean;
    }

    public void setUserBean(UserEntity userBean) {
        this.userBean = userBean;
    }

    private UserEntity userBean;
    private ChatRoomShowFragment chatRoomFragment;
    private long exitTime;
    private GifImageView network_gifimageview = null;
    private List<String> mQXDList;
    private int windowWidth;
    private int windowHeight;
    //    private boolean isFirstSetRate = true;
    private String mActivityId;

    List<RechargeBean> MBDatas;

    /**
     * 最大声音
     */
    private int mMaxVolume;
    /**
     * 当前声音
     */
    private int mVolume = -1;
    /**
     * 当前亮度
     */
    private float mBrightness = -1f;


    public int getLeftSecends() {
        return leftSecends;
    }

    public void setLeftSecends(int leftSecends) {
        this.leftSecends = leftSecends;
    }

    private int leftSecends;
    /**
     * 定时隐藏
     */
    private WeakHandler mDismissHandler = new WeakHandler(this) {
        @Override
        public void conventHandleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    mVolumeBrightnessLayout.setVisibility(View.GONE);
                    break;
                case 1:
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    break;
                case 2:
                    break;
                default:
                    break;
            }
        }


    };

    private int shouldSentGiftNum;
    private View mVolumeBrightnessLayout;
    private AudioManager mAudioManager;
    private ImageView mOperationBg;
    private TextView tv_video_name;
    private ImageView mOperationPercent;
    // private RelativeLayout rl_video_info;
    private RelativeLayout frameLayout;
    private GestureDetector mGestureDetector;
    //    private boolean ifHasPrised;
//    private boolean ifHasShoucang;
    private Dialog formDialog;
    private Dialog mbDialog;
    //    private Dialog buyMbDialog;
    private Boolean isDialogShow = false;
    private ImageView mLoadingImg;
    private AnimationDrawable anim;
    private List<FormWuTaiInfo> mFormList;
    private int breakTime = 0;

    private String singerId = "";
    private boolean isMyselfInfo;
    public final static String DATA = "data";
    //////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////

    private ISplayer player;
    private String path = "";
    private PlayContext playContext;
    private ILeVideoView videoView;
    private Bundle mBundle;
    private long lastposition;
    public SurfaceHolder mSurfaceHolder;
    Map<Integer, String> definationsMap;
    private RelativeLayout.LayoutParams relativeMatchLayoutParams;

    private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            /**
             * surfaceview 销毁的时候销毁播放器
             */
            stopAndRelease();
            LogUtils.d("LiveActivity------surfaceDestroyed");
        }

        @Override
        public void surfaceCreated(final SurfaceHolder holder) {
            mSurfaceHolder = holder;
            /**
             * 创建播放器
             */
            LogUtils.d("LiveActivity------surfaceCreated");
            videoView.setLayoutParams(relativeMatchLayoutParams);
            createOnePlayer(holder.getSurface());
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            LogUtils.d("LiveActivity------surfaceChanged");
            if (player != null) {
                /**
                 * surfaceView 宽高改变的时候，需要通知player
                 */
                PlayerParamsHelper.setViewSizeChange(player, width, height);
            }
        }
    };
    private String mChatroomId;


    private List<PeopleIdAndPicInfo> peopleLists;

    public UserEntity getAnchorChatroomInfo() {
        return anchorChatroomInfo;
    }

    public void setAnchorChatroomInfo(UserEntity anchorChatroomInfo) {
        this.anchorChatroomInfo = anchorChatroomInfo;
    }

    private UserEntity anchorChatroomInfo;

//    private AnchorChatroomInfo anchorChatroomInfo;

    private void updatePeopleList(Collection<? extends PeopleIdAndPicInfo> list) {
        peopleLists.clear();
        peopleLists.addAll(list);
        getMyMediaController().initPeoplePicListData(peopleLists);
    }

    public void setPeopleNum(int num) {
        chatRoomFragment.peopleNum = num;
        getMyMediaController().getmAudienceNums().setText("" + num);
    }

    public void getPeopleInfo(String ids) {
        RequestParams params = UrlTool.getPostParams("uids",
                ids);
        SendActtionTool.post(UserParams.URL_GETPEOPLE_LISTS,
                ServiceAction.Action_xiuchang, XiuchangAction.ACTION_GETPEOPLELIST,
                this, params);
    }

    private GiftDialog.OnGiftListenner giftListenner = new GiftDialog.OnGiftListenner() {
        @Override
        public void onSendGift(int giftNum) {

            if (chatRoomFragment != null && chatRoomFragment.cf != null) {
                ShowActivity.this.shouldSentGiftNum = giftNum;
                sendMessageToChatroom(giftDialog.getmCurrentGiftBean(), giftNum, false);
                doMySelfGiftAnimation(giftDialog.getmCurrentGiftBean(), shouldSentGiftNum);
                Utils.toast(ShowActivity.this, "赠送礼物成功");
                getMyMediaController().getLeftMBNumText().setText("(剩余M豆:" + userBean.getMbCount() + ")");
                giftDialog.getLeftMbs().setText("(剩余M豆:" + userBean.getMbCount() + ")");
            } else {
                Utils.toast(ShowActivity.this, "聊天室未初始化");
            }
//            sendGift(giftNum);
        }

        @Override
        public void onInitBuyMbGift() {
//            initMbDialog();
            initGoumaiDialog();
        }

    };
    GiftDialog giftDialog;

    public void initGiftDialog() {
        if (giftDialog == null) {
            List<GiftBean> giftlist = XiuchangService.getInstance().getDataGift();
            if (giftlist == null || giftlist.size() == 0) {
                getGiftData();
                return;
            }

            giftDialog = new GiftDialog(this, giftlist, giftListenner);
            giftDialog.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (ShowActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        getMyMediaController().getmGiftBtn().setVisibility(View.VISIBLE);
                        getMyMediaController().getLeftMBNumText().setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            giftDialog.clearData();
        }
        giftDialog.show();
        getMyMediaController().getmGiftBtn().setVisibility(View.GONE);
        getMyMediaController().getLeftMBNumText().setVisibility(View.GONE);
    }

    private void getGiftData() {
        SendActtionTool.get(Constants.XiuchangParams.GIFT_LIST,
                ServiceAction.Action_xiuchang,
                XiuchangAction.ACTION_LIST_GIFT, this);
    }

    /**
     * 停止和释放播放器
     */
    private void stopAndRelease() {
        if (player != null) {
            lastposition = player.getCurrentPosition();
            player.stop();
            player.reset();
            player.release();
            player = null;
        }
    }


    //获取主播的信息
    private void getAnchorInfo(String chatroomId) {
        SendActtionTool.post(Constants.UserParams.URL_GETANCHORINFO, null, XiuchangAction.ACTION_GETANCHORINFO, ShowActivity.this,
                UrlTool.getPostParams("chatroomId", chatroomId));
    }

    /**
     * 创建一个新的播放器
     *
     * @param surface
     */
    private void createOnePlayer(Surface surface) {
        if (mBundle == null)
            return;
        player = PlayerFactory.createOnePlayer(playContext, mBundle, this, surface);
        if (lastposition > 0 && mBundle.getInt(PlayProxy.PLAY_MODE) == EventPlayProxy.PLAYER_VOD) {
            player.seekTo(lastposition);
        }

        /**
         * 该过程是异步的，在播放器回调事件中获取到该过程的结果。 请求成功:
         * <p/>
         * ISplayer.MEDIA_EVENT_PREPARE_COMPLETE，此时调用start()方法开始播放 请求失败：
         * ISplayer.PLAYER_PROXY_ERROR://请求媒体资源信息失败
         * ISplayer.MEDIA_ERROR_NO_STREAM:// 播放器尝试连接媒体服务器失败
         */
        player.prepareAsync();
    }

    private void initFullLayoutParams() {
        relativeMatchLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        relativeMatchLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        relativeMatchLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        relativeMatchLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        relativeMatchLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
    }

    private void initPlayView() {
        LogUtils.d("initPlayView");
        initVideoView();// 初始化videoView
        initPlayContext();// 初始化playContext
    }

    /**
     * 初始化PlayContext PlayContext作为播放器的上下文而存在，既保存了播放器运行时的临时数据，也记录播放器所需要的环境变量。
     */
    private void initPlayContext() {
        LogUtils.d("LiveActivity--" + "initPlayContext");
        playContext = new PlayContext(ShowActivity.this);
        // 当前视频渲染所使用的View，可能是Surfaceview(glSurfaceView也属于Surfaceview
        // ),也可能是textureView
        playContext.setVideoContentView(videoView.getMysef());
    }

    private void initVideoView() {
        /**
         * 创建视频显示View，ILeVideoView是我们定义的基础view用于显示视频，当前如果用户希望自己的view
         */
        LogUtils.d("LiveActivity--" + "initVideoView");
        videoView = (ILeVideoView) findViewById(R.id.sf);
        videoView.getHolder().addCallback(surfaceCallback);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // // 秀场分享
            // case R.id.xiuchuang_shareBtn:
            // share = new PopThreeShare(this);
            // share.setShareUrl("", "", Constants.URL_SHARE);
            // share.showBototomPop();
            // break;
            default:
                break;
        }
    }

    @Override
    public void setContentView(Bundle savedInstanceState) {
//        createTime = System.currentTimeMillis();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        userBean = UserService.getInatance().getUserBean(this);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                        | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_show);
        reSetWindowSize();
        initFullLayoutParams();
        initShowPlayView();
        mVideoId = getIntent().getStringExtra(Constants.VIDEO_ID);
        singerId = getIntent().getStringExtra("singerId");
//        mVideoId = "1843";
//        singerId = "56d7b0aee4b05b5cfa40534e";
        // 获取电话通讯服务
        TelephonyManager tpm = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);
        // 创建一个监听对象，监听电话状态改变事件
        tpm.listen(new MyPhoneStateListener(),
                PhoneStateListener.LISTEN_CALL_STATE);

    }


    private void initTime() {
        leftSecends--;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                LogUtils.d("leftSecends---"+leftSecends);
                if (leftSecends > 0) {
                    getMyMediaController().getmTimeLeft().setText("直播中:" + TimeTool.getTimeFromSec(leftSecends));
                    getMyMediaController().getVerticalTimeText().setText("直播中:" + TimeTool.getTimeFromSec(leftSecends));
                } else {
                    getMyMediaController().getmTimeLeft().setText("直播结束");
                    getMyMediaController().getVerticalTimeText().setText("直播结束");
                }
            }
        });

    }

    private void initUpdateTime() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                initTime();
            }
        }, 0, 1000);
    }



    @Override
    public void findViewById() {
        leftSecends = 15 * 60;

        initView();
//        rootView = findViewById(R.id.main_content_top);
        mLoadingView = findViewById(R.id.video_loading);
//        mBarrageView = (RelativeLayout) findViewById(R.id.barrage);
        show_gift = (RelativeLayout) findViewById(R.id.show_gift);
        mShowContent = findViewById(R.id.fragmentRoot);
        initGif();
        // 加载视频播放资源
        playVideo(mVideoId);
//        getIfZanAndIfShoucang(mVideoId);
        full(false);
        mPlayerLayoutView.postDelayed(new Runnable() {
            @Override
            public void run() {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            }
        }, 1000);
        PreferencesUtils.TYPE_TANMU_SHOW = true;
        getMbDataList();
        getGiftData();
//        initFragment("570480c7e4b0697e0f7cb4e8"); //showactivity fragment 的模拟入口
    }

    @Override
    public void onBackPressed() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setScreenOrientation(false);
            return;
        }

        if (chatRoomFragment != null && !TextUtils.isEmpty(chatRoomFragment.getToUserId())) {
            chatRoomFragment.backImpress();
            return;
        }
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出播放", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
            return;
        }
        finish();
    }

    /**
     * 初始化首个Fragment
     */
    private void initFragment(String mChatroomId) {
        this.mChatroomId = mChatroomId;
        chatRoomFragment = new ChatRoomShowFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", mChatroomId);
        chatRoomFragment.setArguments(bundle);
        android.support.v4.app.FragmentTransaction beginTransaction = getSupportFragmentManager()
                .beginTransaction();
        beginTransaction.add(R.id.fragmentRoot, chatRoomFragment,
                chatRoomFragment.getClass().getName()).commit();
    }

    private void initShowPlayView() {
        mQXDList = new ArrayList<String>();
        mPlayerLayoutView = (RelativeLayout) this
                .findViewById(R.id.rlayout_show);
        initPlayView();
    }


    //获取 人物的  人数
    private void ongetTotlePeople() {
        RequestParams params = UrlTool.getParams(Constants.VIDEO_ID,
                mVideoId);
        LogUtils.d("gettotal  remshu  starta ----------");
        SendActtionTool.get(UserParams.URL_GET_NUMINLIVE,
                ServiceAction.Action_xiuchang,
                XiuchangAction.ACTION_GETPEOPLENUM, this, params);
    }

    /**
     * 获取是否点赞和是否收藏
     */
    private void getIfZanAndIfShoucang(String videoId) {
        if (UserService.getInatance().isNeedLogin(this)) {
            getMyMediaController().getmZanBtn().setBackgroundResource(R.drawable.but_zan_bac);
        } else {
            RequestParams params = UrlTool.getParams(Constants.VIDEO_ID,
                    videoId, Constants.USER_ID, UserService.getInatance()
                            .getUserBean(this).getId());
            SendActtionTool.get(UserParams.URL_USER_IfHasZanAndShoucang,
                    ServiceAction.Action_xiuchang,
                    XiuchangAction.ACTION_GET_ZANANDShOUCANG, this, params);
        }
    }

    private void initGetPrisedAndShouCang(JSONObject valueinfo) {

        try {
            JSONObject value = valueinfo.getJSONObject("data");
            String isPraise = value.getString("isPraise");
//            String isCollection = value.getString("isCollection");
//            int praiseCount = value.getInt("praiseCount");
//            int commentCount = value.getInt("commentCount");
            if (isPraise.equals("0")) {
                getMyMediaController().getmZanBtn().setBackgroundResource(R.drawable.but_zan_bac);
            } else if (isPraise.equals("1")) {
                getMyMediaController().getmZanBtn().setBackgroundResource(R.drawable.but_zanhl_bac);
            }
//            if (isCollection.equals("0")) {
//            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 播放指定视频
     *
     * @param videoId
     */
    private void playVideo(String videoId) {
        RequestParams params = UrlTool.getParams(Constants.VIDEO_ID, videoId,
                Constants.USER_ID, userBean.getId(), Constants.FILTER,
                Constants.FILTER_VIDEO_SHOW);
        if (!TextUtils.isEmpty(videoId)) {
            SendActtionTool.get(Constants.URL_VIDEO_DETAIL,
                    ServiceAction.Action_xiuchang,
                    XiuchangAction.Action_GetVideo, this, params);
        }
    }

    /**
     * 举报某主播
     *
     * @param
     */
    private void reportAnchor(String info) {
        //
        String usId;
        String contact;
        if (UserService.getInatance().getUserBean(getBaseContext()) == null) {
            contact = "";
            usId = "";
        } else {
            contact = UserService.getInatance().getUserBean(getBaseContext()).getMobile();
            usId = UserService.getInatance().getUserBean(getBaseContext()).getId();
        }
        String verName = "";
        try {
            verName = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String toUserId = "";
        if (anchorChatroomInfo != null) {
            toUserId = anchorChatroomInfo.getId();
        }
        RequestParams params = UrlTool.getParams(Constants.USER_ID, usId,//
                Constants.CONTENT, info,//
                "contact", contact,//
                Constants.TYPE, "2",//
                "toUserId", toUserId,//
                Constants.SOURCE, Constants.ANDROID_MOBILE,//
                Constants.VERSION, verName + "");
        SendActtionTool.get(UserParams.URL_USER_REPORT, ServiceAction.Action_xiuchang, XiuchangAction.ACTION_REPORT_ANCHOR, ShowActivity.this, params);

    }


    public GestureDetector getGestureDetector() {
        if (mGestureDetector == null) {
            mGestureDetector = new GestureDetector(this,
                    new MyGestureListener());
        }
        return mGestureDetector;
    }

    private void initView() {

        MBDatas = new ArrayList<RechargeBean>();
        peopleLists = new ArrayList<PeopleIdAndPicInfo>();
        include_video = findViewById(R.id.include_video);
        // ~~~ 绑定控件
        tv_video_name = (TextView) findViewById(R.id.tv_video_name);
        mVolumeBrightnessLayout = findViewById(R.id.operation_volume_brightness);
        mOperationBg = (ImageView) findViewById(R.id.operation_bg);
        mOperationPercent = (ImageView) findViewById(R.id.operation_percent);
        mLoadingView = findViewById(R.id.video_loading);
        mLoadingImg = (ImageView) findViewById(R.id.img_loading);

        anim = (AnimationDrawable) mLoadingImg.getBackground();
        // rl_video_info = (RelativeLayout) findViewById(R.id.rl_video_info);
        frameLayout = (RelativeLayout) findViewById(R.id.fragmentRoot);
        // ~~~ 绑定数据
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        getMyMediaController().setListenner(controllerListenner);
        getMyMediaController().setMode(true);
        getMyMediaController().show();
        getMyMediaController().full(false);
        getMyMediaController().setmActivity(this);
        getGestureDetector();
        LogUtils.d("（剩余M豆:" + userBean.getMbCount() + "）");
        getMyMediaController().getLeftMBNumText().setText("(剩余M豆:" + userBean.getMbCount() + ")");
        Utils.showLoadingAnim(mLoadingView, anim);
    }

    public ShowNewMediacontroler getMyMediaController() {
        if (mControler == null) {
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mControler = (ShowNewMediacontroler) findViewById(R.id.mediacontroller_view);
        }
        return mControler;
    }


    /**
     * 手势结束
     */
    private void endGesture() {
        mVolume = -1;
        mBrightness = -1f;

        // 隐藏
        mDismissHandler.removeMessages(0);
        mDismissHandler.sendEmptyMessageDelayed(0, 500);
    }


    private class MyGestureListener extends SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            if (getMyMediaController().isShow()) {
                LogUtils.d("onSingleTapConfirmedisshow1111");
                getMyMediaController().hide();
            } else {
                getMyMediaController().show();
                LogUtils.d("onSingleTapConfirmedisshow12222");
            }


            return false;
        }

        /**
         * 滑动
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            try {
                LogUtils.d("onScroll333");
                float mOldX = e1.getX(), mOldY = e1.getY();
                int y = (int) e2.getRawY();
                int x = (int) e2.getRawX();

                if (Math.abs((mOldX - x)) < windowWidth * 0.1
                        && Math.abs((mOldY - y)) > windowWidth * 0.1) {// 左右滑动长度不超过屏幕的十分之一
                    if (mOldX > windowWidth * 4.0 / 5)// 右边滑动
                        onVolumeSlide((mOldY - y) / windowHeight);
                    else if (mOldX < windowWidth / 5.0)// 左边滑动
                        onBrightnessSlide((mOldY - y) / windowHeight);
                } else {
                    return false;
                }
            } catch (Exception e) {
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    /**
     * 滑动改变声音大小
     *
     * @param percent
     */
    private void onVolumeSlide(float percent) {
        if (mVolume == -1) {
            mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mVolume < 0)
                mVolume = 0;
            // 显示
            mOperationBg.setImageResource(R.drawable.video_volumn_bg);
            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
        }
        int index = (int) (percent * mMaxVolume) + mVolume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;
        // 变更声音
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
        // 变更进度条
        ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
        lp.width = findViewById(R.id.operation_full).getLayoutParams().width
                * index / mMaxVolume;
        mOperationPercent.setLayoutParams(lp);
    }

    /**
     * 滑动改变亮度
     *
     * @param percent
     */
    private void onBrightnessSlide(float percent) {
        if (mBrightness < 0) {
            mBrightness = getWindow().getAttributes().screenBrightness;
            if (mBrightness <= 0.00f)
                mBrightness = 0.50f;
            if (mBrightness < 0.01f)
                mBrightness = 0.01f;

            // 显示
            mOperationBg.setImageResource(R.drawable.video_brightness_bg);
            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
        }
        WindowManager.LayoutParams lpa = getWindow().getAttributes();
        lpa.screenBrightness = mBrightness + percent;
        if (lpa.screenBrightness > 1.0f)
            lpa.screenBrightness = 1.0f;
        else if (lpa.screenBrightness < 0.01f)
            lpa.screenBrightness = 0.01f;
        getWindow().setAttributes(lpa);

        ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
        lp.width = (int) (findViewById(R.id.operation_full).getLayoutParams().width * lpa.screenBrightness);
        mOperationPercent.setLayoutParams(lp);

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        getUserInfo(true);
        rePlayVideo(mVideoId);
    }

    /**
     * 重置播放指定视频
     *
     * @param videoId
     */
    public void rePlayVideo(String videoId) {
        RequestParams params = UrlTool.getParams(Constants.VIDEO_ID, videoId,
                Constants.USER_ID, userBean.getId(), Constants.FILTER,
                Constants.FILTER_VIDEO_SHOW);
        if (!TextUtils.isEmpty(videoId)) {
            SendActtionTool.get(Constants.URL_VIDEO_DETAIL,
                    ServiceAction.Action_xiuchang,
                    XiuchangAction.Action_GetVideo_Restart, this, params);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        UserService.getInatance().setUserBean(userBean, this);
        Utils.sendBroadcastToMainactivityToUpdateUserinfo(this);
        if (player != null) {
            player.pause();
            isBackgroud = true;
        }
    }

    private void sendCloseInfo(String videoId) {
        RequestParams params = UrlTool.getPostParams(Constants.VIDEO_ID, videoId);
        SendActtionTool.post(Constants.URL_EXIT_SERVLET, null, XiuchangAction.ACTION_SEND_CLOSEINFO, this, params);
    }

    @Override
    protected void onDestroy() {
        sendCloseInfo(mVideoId);
        super.onDestroy();
        if (playContext != null) {
            playContext.destory();
        }
        isBackgroud = true;
        if (timer != null) {
            timer.cancel();
        }
//        Utils.sendBroadcastToService(8, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            if (isBackgroud) {
                player.start();
//                this.mPlayerView.playVideo(mStreamId, "测试频道");
            }
        }
    }

    public void playvideo() {
        if (mSurfaceHolder != null) {
            if (player != null) {
                player.setParameter(player.getPlayerId(), mBundle);
                player.setOnPlayStateListener(this);
                if (mSurfaceHolder.getSurface() == null) {
                    throw new RuntimeException("surface is null!");
                }
                player.setDisplay(mSurfaceHolder.getSurface());
                player.prepareAsync();
            } else {
                createOnePlayer(mSurfaceHolder.getSurface());
            }
        } else {
            initPlayView();
            LogUtils.d("surface is null!!!!");
        }
        addPlayToServices(mVideoId);
    }

    /**
     * 添加播放统计
     *
     * @param videoId
     */
    private void addPlayToServices(String videoId) {
        RequestParams params = UrlTool.getPostParams(Constants.VIDEO_ID, videoId);
        SendActtionTool.post(Constants.URL_LIVE_ENGINE, null, XiuchangAction.Action_addPlayToServices, this, params);
    }


    private List<LiveInfo> mLiveInfoList = new ArrayList<LiveInfo>();
    /**
     * 是否需要自动恢复播放，用于自动暂停，恢复播放
     */
    private String mTitle;
//    private boolean isFirstPlay = true;

    /**
     * 播放，初始话播放控件
     *
     * @param
     */
    private void setPlayUrl(String streamId) {
        if (streamId.length() < 3) {
            Utils.toast(this, "直播流异常，请退出后再次进入！");
            return;
        }
        this.mStreamId = streamId;
        this.mLiveId = passStreamId(mStreamId);
        LogUtils.d("setPlayUrl", mLiveId);
        mBundle = LetvParamsUtils.setLiveParams(null, mLiveId, false, false);
        playvideo();
    }

    private void stopPlayer() {
        if (player != null)
            player.pause();
    }

    private void startPlayer() {
        if (player != null)
            player.start();
    }

    private void reSetWindowSize() {
        Display disp = getWindowManager().getDefaultDisplay();
        windowWidth = disp.getWidth();
        windowHeight = disp.getHeight();
    }

    private String startTime = "";
    private String location = "";
    private String descrepyion = "";

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        if (XiuchangAction.ACTION_GETPEOPLELIST != action) {
            super.onSuccess(service, action, value);
        }
        JSONObject obj = (JSONObject) value;
        switch ((XiuchangAction) action) {
            case Action_GetVideo:
                try {
                    // TODO
                    ResultBean<ShowLatestVideo> resultVideoBean = JSON
                            .parseObject(
                                    value.toString(),
                                    new TypeReference<ResultBean<ShowLatestVideo>>() {
                                    });
                    ShowLatestVideo albumLastVideo = resultVideoBean.data;

                    String mChatroomId = albumLastVideo.getChatroomId();
//                     mChatroomId = "570480c7e4b0697e0f7cb4e8";
                    if (TextUtils.isEmpty(mChatroomId)) {
                        Utils.toast(ShowActivity.this, "获取聊天室信息失败");
                    } else {
                        getAnchorInfo(mChatroomId);
                        initFragment(mChatroomId);
                    }
                    if (albumLastVideo.getUserinfo() != null) {
                        if (!TextUtils.isEmpty(albumLastVideo.getUserinfo().getUserId())) {
                            singerId = albumLastVideo.getUserinfo().getUserId();
                            LogUtils.d("singerId===" + singerId);
                        }
                    }
                    getUserInfo(false);

                        startTime = albumLastVideo.getShowTime();
                        location = albumLastVideo.getLocation();
                        descrepyion = albumLastVideo.getDescription();
//                        updateLeftTime(0);
                        leftSecends = (int) ((Long.parseLong(startTime) + 15 * 1000 * 60 - System.currentTimeMillis()) / 1000);
                        mFormList = albumLastVideo.getCatalog();
                        if (mFormList == null || mFormList.size() == 0) {
                            getMyMediaController().setHasFormMenu(false);
                        } else {
                            getMyMediaController().setHasFormMenu(true);
                        }
                        mTitle = albumLastVideo.getName();

                        LogUtils.d("mTitle===" + mTitle);
                        getMyMediaController().getCharRoomName().setText(mTitle);
//                        getMyMediaController().setVideoShowName(mTitle);
                        List<LiveInfo> liveInfo2 = albumLastVideo.getLiveInfo();

                        if (liveInfo2 != null && liveInfo2.size() > 1) {
                            mLiveInfoList.clear();
                            mLiveInfoList.addAll(liveInfo2);
//                            getMyMediaController().showWutai(true);
//                            getMyMediaController().setLiveWutai(liveInfo2);

                            getMyMediaController().setHasWuTai(true);
                            getMyMediaController().setLiveWutai(liveInfo2);
                        } else {
                            mLiveInfoList = null;
//                            getMyMediaController().showWutai(false);
                            getMyMediaController().setHasWuTai(false);
                        }
                        getMyMediaController().setHasWuTai(false);
                        checkNetAndPlay(albumLastVideo.getLiveInfo().get(0));
                        mActivityId = albumLastVideo.getLiveInfo().get(0).getActivityId();

                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.e("javaBean 转型失败 " + e.toString());
                }
                break;
            case Action_GetVideo_Restart:
                try {
                    ResultBean<ShowBean> resultBean = JSON.parseObject(
                            value.toString(),
                            new TypeReference<ResultBean<ShowBean>>() {
                            });
                    if (resultBean != null) {
                        ShowBean data = resultBean.data;
                        long endTime = TimeTool.getMillTime(data.getEndTime());
                        if (endTime > 0 && endTime < System.currentTimeMillis()) {
                            Utils.toast(this, "感谢您的观看，本场直播已经结束。");
                            finish();
                        }
                    } else {
                        LogUtils.d("---resultBean == null");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case Action_addPraiseResouce:
                LogUtils.d("zan;;;;;;;;Action_addPraiseResouce" + value.toString());
                break;
            case ACTION_GET_ZANANDShOUCANG:
                initGetPrisedAndShouCang((JSONObject) value);
                break;
            case ACTION_REPORT_ANCHOR:
                Utils.toast(this, "举报成功");
                reportDialog.cancel();
                break;

            case ACTION_GETMBDATA:
                try {
                    MBDatas.clear();
                    MBDatas.addAll(JSON.parseArray(obj.getJSONArray("data").toString(), RechargeBean.class));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            // 请求个人信息
            case ACTION_USER_INFO:
                String datas;
                try {
                    datas = obj.getJSONObject(Constants.USER_ENTITY).toString();
                    UserEntity user = JsonUtils.parse(datas, UserEntity.class);
                    if (isMyselfInfo) {
                        UserService.getInatance().setUserBean(user, this);
                        userBean = user;
//         （剩余MB:23445）
                        LogUtils.d("（剩余M豆==:==" + user.getMbCount());
//                        chatRoomFragment.gf.getMbText().setText("（剩余M豆:" + user.getMbCount() + "）");
                        getMyMediaController().getLeftMBNumText().setText("(剩余M豆:" + user.getMbCount() + ")");
                        if (giftDialog != null) {
                            giftDialog.getLeftMbs().setText("(剩余M豆:" + +user.getMbCount() + ")");
                        }

                    } else {
                        anchorChatroomInfo = user;
                        initAnchorInfos(user);
                        initUpdateTime();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case ACTION_GETANCHORINFO:
                try {
                    long addTime = ((JSONObject) value).getJSONObject("data").getLong("addTime");
                    leftSecends = (int) ((Long.parseLong(startTime) + addTime - System.currentTimeMillis()) / 1000);
//                    anchorChatroomInfo = JSON.parseObject(((JSONObject) value).getString("data"), AnchorChatroomInfo.class);
//                    initAnchorInfos(anchorChatroomInfo);
//                    initUpdateTime();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LogUtils.d("ACTION_GETANCHORINFO-----");
                break;
            case ACTION_GETPEOPLELIST:
                try {
//                    LogUtils.d("peopleLists.size()======" + peopleLists.size());
                    peopleLists = JSON.parseArray(((JSONObject) value).getString("data"), PeopleIdAndPicInfo.class);
//                    LogUtils.d("peopleLists.size()======" + peopleLists.size());
                    Collections.reverse(peopleLists);
                    getMyMediaController().initPeoplePicListData(peopleLists);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case ACTION_GETPEOPLENUM:
                try {
                    int num = ((JSONObject) value).getJSONObject("data").getInt("onLineTotals");
                    if (num >= getMyMediaController().getPicList().size()) {
                        chatRoomFragment.peopleNum = num;
                    }
                    setPeopleNum(num);
//                    chatRoomFragment.peopleNum = ((JSONObject) value).getJSONObject("data").getInt("totals");
                    LogUtils.d("chatRoomFragment.peopleNum=====" + chatRoomFragment.peopleNum);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case ACTION_LIST_GIFT:
                try {
                    List<GiftBean> giftDatas = JSON.parseArray(obj.getJSONArray("data")
                            .toString(), GiftBean.class);
                    XiuchangService.getInstance().setDataGift(giftDatas);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }


    public void updateLeftTime(long addAllTime) {
        if (!TextUtils.isEmpty(startTime)) {
            LogUtils.d("addAllTime===" + addAllTime);
            LogUtils.d("startTime===" + startTime);
            leftSecends = (int) ((Long.parseLong(startTime) + addAllTime - System.currentTimeMillis()) / 1000);
//            mDismissHandler.sendEmptyMessage(2);
        }
    }

    //给 主播信息初始化
    private void initAnchorInfos(UserEntity info) {
        getMyMediaController().initAnchorEntity(info);
        chatRoomFragment.updateUserEnty(info, startTime, location, descrepyion);
    }

    /**
     * 获取个人信息
     */
    public void getUserInfo(boolean isMyseflf) {
        RequestParams params;
        isMyselfInfo = isMyseflf;
        if (isMyseflf) {
            params = UrlTool.getPostParams(Constants.USER_ID,
                    userBean.getId());
        } else {
            params = UrlTool.getPostParams(Constants.USER_ID,
                    singerId);
        }

        SendActtionTool.post(UserParams.URL_GET_ONE,
                ServiceAction.Action_xiuchang, XiuchangAction.ACTION_USER_INFO,
                this, params);
    }


    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {
        super.onFaile(service, action, value);
        if (action != XiuchangAction.Action_addPraiseResouce) {
            Utils.toast(this, value.toString());
        }
    }

    @Override
    public void onException(ServiceAction service, Object action, Object value) {
        super.onException(service, action, value);
    }

    /**
     * 播放直播流
     *
     * @param liveInfo
     */
    private void playLive(LiveInfo liveInfo) {
        if (liveInfo != null) {
            String streamId = liveInfo.getStreamId();
            if (!TextUtils.isEmpty(streamId))
                setPlayUrl(streamId);
            else
                Utils.toast(this, "直播地址为空");

        } else {
            Utils.toast(this, "没有视频信息");
        }
    }

    private String passStreamId(String str) {
        // hz_20150819300007025
        str = str.trim();
        str = str.substring(0, str.length() - 2);
        if (str.startsWith("hz_") && str.length() > 3) {
            str = str.substring(3, str.length());
        }
        return str;
    }

    private void checkNetAndPlay(final LiveInfo liveInfo) {
        if (NetworkHelper.isNetworkConnected(this)
                && !NetworkHelper.isWifiConnected(this)
                && !PreferencesUtils.getBooleanPreferences(this,
                PreferencesUtils.TYPE_NET_TOGLE)) {
            DialogTool.createNetWorkDialog(this, new DialogLister() {

                @Override
                public void onCountinue() {
                    playLive(liveInfo);
                }

                @Override
                public void onCancelListener() {
                    finish();
                }
            }).show();
        } else {
            playLive(liveInfo);
        }

    }

    private View include_video;
    private boolean isLocked;
    private boolean fangDalock;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (videoView != null) {
            /**
             * 当屏幕旋转的时候，videoView需要全屏居中显示, 如果用户使用自己的view显示视频（比如SurfaceView）,
             * 比较简单的方法是：对surfaceView的layourParams()进行设置。 1）竖屏转横屏的时候，可以占满全屏居中显示；
             * 2）横屏转竖屏时，需要设置layoutParams()恢复之前的显示大小
             *
             */
            videoView.setVideoLayout(-1, 0);
        }
        LogUtils.d("onConfigurationChanged");
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (isDialogShow && formDialog != null) {
                formDialog.show();
            }
            initZanGuidDialog();
            full(true);
            if (player != null) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            }
            if (fangDalock) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                fangDalock = false;
            } else {
                if (!isLocked) {
                    mDismissHandler.removeMessages(1);
                    mDismissHandler.sendEmptyMessageDelayed(1, 2000);
                }
            }

        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (formDialog != null) {
                formDialog.dismiss();
            }
            checkVerticalDialog();
            full(false);
//            if (player != null) {
//            }
            if (!isLocked) {
                mDismissHandler.removeMessages(1);
                mDismissHandler.sendEmptyMessageDelayed(1, 2000);
            }
        }
    }

    private void checkVerticalDialog() {
        if (zanGuidDialog != null && zanGuidDialog.isShowing()) {
            zanGuidDialog.cancel();
        }

        if (giftGuidDialog != null && giftGuidDialog.isShowing()) {
            giftGuidDialog.cancel();
        }
    }

    Dialog zanGuidDialog;
    Dialog giftGuidDialog;

    private void initZanGuidDialog() {
        if (!PreferencesUtils.getBooleanPreferences(ShowActivity.this, PreferencesUtils.TYPE_GUIDE[5])) {
            zanGuidDialog = DialogTool.createGuideDialog(this, 5, true, new DialogTool.DialogGuideListener() {
                @Override
                public void onGuide(int index) {
                    zanGuidDialog.cancel();
//                    controllerListenner.onZanClicked();
//                    initGiftGuidDialog();
                }
            });
            PreferencesUtils.saveBooleanPreferences(ShowActivity.this, PreferencesUtils.TYPE_GUIDE[5], true);
        }
    }

    private void initGiftGuidDialog() {
        if (!PreferencesUtils.getBooleanPreferences(ShowActivity.this, PreferencesUtils.TYPE_GUIDE[6])) {
            giftGuidDialog = DialogTool.createGuideDialog(this, 6, true, new DialogTool.DialogGuideListener() {
                @Override
                public void onGuide(int index) {
                    giftGuidDialog.cancel();
//                    controllerListenner.onZanClicked();
                }
            });
            PreferencesUtils.saveBooleanPreferences(ShowActivity.this, PreferencesUtils.TYPE_GUIDE[6], true);
        }
    }


    private void full(boolean enable) {
        LogUtils.d("showActivity  enable==" + enable);
        reSetWindowSize();
        if (enable) {
            Utils.setFullScreen(this);
            mShowContent.setVisibility(View.GONE);
        } else {
            Utils.cancelFullScreen(this);
            if (mTanmuDialog != null && mTanmuDialog.isShowing())
                mTanmuDialog.dismiss();
            mShowContent.setVisibility(View.VISIBLE);
        }
        LayoutParams layoutParams = include_video.getLayoutParams();
        if (enable) {
//            layoutParams.height = getWindow().getAttributes().height;
            layoutParams.height = LayoutParams.MATCH_PARENT;
            layoutParams.width = LayoutParams.MATCH_PARENT;
            include_video.setLayoutParams(layoutParams);
        } else {
            // getMediaController().setPaddingRelative(0, 0, 0, 0);
            layoutParams.height = ScreenUtils.getScreenHeight(this) / 3;
            layoutParams.width = LayoutParams.MATCH_PARENT;
            LogUtils.d("layoutParams.height==" + layoutParams.height);
            include_video.setLayoutParams(layoutParams);
        }
        if (videoView != null)
            videoView.setLayoutParams(relativeMatchLayoutParams);
    }

//    @Override
//    public void onBackClicked() {
//        LogUtils.d("onBackClicked--");
//        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            setScreenOrientation(false);
//        } else {
//            GeneralTool.KeyBoardCancle(this);
//            finish();
//        }
//    }

    private void setScreenOrientation(boolean toLandScape) {
        int screenOrientationPortrait = toLandScape ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setRequestedOrientation(screenOrientationPortrait);
        mDismissHandler.removeMessages(1);
        mDismissHandler.sendEmptyMessageDelayed(1, 2000);
    }

//    @Override
//    public void onFangdaClicked() {
//        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            fangDalock = false;
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        } else {
//            fangDalock = true;
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        }
//    }

//    @Override
//    public void onTanmuClicked(boolean b) {
//        PreferencesUtils.TYPE_TANMU_SHOW = b;
//        if (b) {
//            mBarrageView.setVisibility(View.VISIBLE);
//        } else
//            mBarrageView.setVisibility(View.GONE);
//    }

    private TanmuDialog mTanmuDialog;

//    @Override
//    public void showTanmuDialog() {
//        if (mTanmuDialog == null)
//            mTanmuDialog = new TanmuDialog(ShowActivity.this,
//                    new OnSendTanmuListener() {
//                        @Override
//                        public void sendMessage(String edtStr) {
//                            // TODO 重要方法，需要重构代码
//                            chatRoomFragment.cf.setEditText(edtStr);
//                        }
//                    });
//        mTanmuDialog.show();
//
//    }

    class MyPhoneStateListener extends PhoneStateListener {
        private boolean isNeedAutoPlay;

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE: // 空闲
                    if (isNeedAutoPlay) {
                        startPlayer();
                        isNeedAutoPlay = false;
                    }
                    break;
                case TelephonyManager.CALL_STATE_RINGING: // 来电
                    if (isPlaying) {
                        isNeedAutoPlay = true;
                        stopPlayer();
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK: // 摘机（正在通话中）
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {

        super.onActivityResult(arg0, arg1, arg2);
        LogUtils.d("arg0==" + arg0 + "arg1" + arg1);
        if (share != null && arg2 != null) {
            share.setSinaWeibo(arg0, arg1, arg2);
        }
    }


    private void addPeople(String id, String pic) {
        PeopleIdAndPicInfo people = new PeopleIdAndPicInfo();
        people.setId(id);
        people.setFaceUrl(pic);
        if (peopleLists.contains(people)) {
            return;
        }

        if (peopleLists.size() >= 20) {
            peopleLists.remove(19);
        }

        peopleLists.add(0, people);
//        this.setPeopleNum(chatRoomFragment.getPeopleNum() + 1);
        getMyMediaController().initPeoplePicListData(peopleLists);

    }

    /**
     * 发送弹幕
     */
    public void sendTanmu(final XiuchanMessage message, final boolean isSelf) {

        if (message.getType() == 8) {
            ongetTotlePeople();
            addPeople(message.getFromUserId(), message.getFromUserPic());
//            getMyMediaController().getmAudienceNums().setText("");
        }

        if (message.getType() == 9) {
            if (!userBean.getId().equals(message.getFromUserId())) {
                disPlayPicAnima(message.getPic(), 1, true);
                return;
            }
        }
        if (message.getType() == 3 || message.getType() == 4) {
            if (userBean.getId().equals(message.getFromUserId())) {
                return;
            }
        }
        getMyMediaController().addMessage(message);

    }


    private void initGif() {
        network_gifimageview = (GifImageView) findViewById(R.id.network_gifimageview);
    }

    private List<String> gifList;

    /**
     * 排队显示动画
     *
     * @param message
     * @param showGif
     * @param isMyGift
     */
    public void queueDisplayGif(final XiuchanMessage message,
                                final boolean showGif, boolean isMyGift) {
        if (gifList == null)
            gifList = new ArrayList<String>();
        final String msgId = String.valueOf(message.getMsgId());

        if (TextUtils.isEmpty(message.getGifImgUrl())) {
            disPlayPicAnima(message.getPic(), message.getCount(), false);
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
                    disPlayPicAnima(message.getPic(), message.getCount(), false);
                } else {
                    gifList.remove(msgId);
                }

            }
        }, delayMillis);
    }

    public void doMySelfGiftAnimation(GiftBean giftBean, int num) {
        if (!TextUtils.isEmpty(giftBean.getGifImgUrl())) {
            LogUtils.d("getGifImgUrl==" + giftBean.getGifImgUrl());
            dispalyGIF(giftBean.getGifImgUrl(), giftBean.getId(),
                    "");
        }
        disPlayPicAnima(giftBean.getImgUrl(), num, false);
        addMyGiftMessage(giftBean, num);
    }


    private void sendMessageToChatroom(GiftBean giftBean, int num, boolean isZan) {
        chatRoomFragment.cf.sendMessage(initMessage(giftBean, num, isZan));
    }

    private XiuchanMessage initMessage(GiftBean giftBean, int num, boolean isZan) {
        XiuchanMessage message = new XiuchanMessage();
        message.setFromUserId(userBean.getId());
        message.setFromUserPic(userBean.getFaceUrl());
        message.setFromUserName(userBean.getUserName());
        message.setCount(num);
        if (anchorChatroomInfo != null) {
            message.setToUserName(anchorChatroomInfo.getUserName());
            message.setToUserId(anchorChatroomInfo.getId());
        }
        if (isZan) {
            message.setType(9);
            return message;
        }
        if (TextUtils.isEmpty(message.getGifImgUrl())) {
            message.setType(3);
        } else {
            message.setType(4);
        }
        message.setGifImgUrl(giftBean.getGifImgUrl());
        message.setGiftPrice(giftBean.getMb());
//        message.setGiftPrice(giftBean.getMb());

        message.setGiftUserName(userBean.getUserName());
        message.setGiftId(giftBean.getId());
        message.setPic(giftBean.getImgUrl());
        message.setToUserId(anchorChatroomInfo.getId());
        message.setMsg(userBean.getUserName() + "送给选手" + num + "个" + giftBean.getName());
        return message;
    }

    public void addMyGiftMessage(GiftBean giftBean, int num) {
        XiuchanMessage message = new XiuchanMessage();
        if (TextUtils.isEmpty(message.getGifImgUrl())) {
            message.setType(3);
        } else {
            message.setType(4);
        }
        message.setGifImgUrl(giftBean.getGifImgUrl());
        message.setGiftPrice(giftBean.getMb());
//        message.setGiftPrice(giftBean.getMb());
        message.setCount(num);
        message.setGiftUserName(userBean.getUserName());
        message.setGiftId(giftBean.getId());
        message.setPic(giftBean.getImgUrl());
        message.setMsg(userBean.getUserName() + "送给选手" + num + "个" + giftBean.getName());
        message.setFromUserId(userBean.getId());
        message.setFromUserName(userBean.getUserName());
        message.setFromUserPic(userBean.getFaceUrl());
//        chatRoomFragment.cf.addMessage(message);
        getMyMediaController().addMessage(message);
    }

    int i = 0;
    private int mQXDIndex = -1;
    private boolean isPlaying;
    //    public PopThreeShare share;
    public DZZThreeSharePop share;

    protected void disPlayPicAnima(String pic, int count, boolean isZan) {
        LogUtils.d("pic=" + pic);
        LogUtils.d("count=" + count);
        LogUtils.d("sqrt=" + count);

        doAnimateOpen(pic, count, isZan);
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
                                    .copyFile(sourceFilePath, destFilePath))
                                LogUtils.d("复制文件成功");
                            FileUtils.deleteFile(responseInfo.result
                                    .getAbsolutePath());
                            displayGif(gifView, destFilePath, msgId);
                        } catch (Exception e) {
                            FileUtils.deleteFile(responseInfo.result
                                    .getAbsolutePath());
                            LogUtils.e("Exception========" + e.getMessage());
                        }
                    }

                    public void onFailure(HttpException error, String msg) {
                        LogUtils.e("onFailure========" + msg);
                        FileUtils.deleteFile(saveFile
                                .getAbsolutePath());
                    }
                });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector != null && mGestureDetector.onTouchEvent(event)) {
            return true;
        }
        // 处理手势结束
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                endGesture();
                break;
        }

        return super.onTouchEvent(event);
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

        if (!TextUtils.isEmpty(msgId)) {
            if (gifList != null && gifList.contains(msgId)) {
                gifList.remove(msgId);
            }
        }

    }

    private int totlaGiftNum = 0;

    @SuppressLint("NewApi")
    private void doAnimateOpen(String url, int total, final boolean isZan) {
//        int i = 0;
        for (int j = 0; j < total; j++) {
            LogUtils.d("total==" + total);
            final int h = j;
            LogUtils.d("h=" + h);
            final ImageView textView = new ImageView(ShowActivity.this);
            LogUtils.d("imgurl=" + url);
            if (isZan) {
                textView.setBackgroundResource(R.drawable.applaud);
            } else {
                BitmapTool.getInstance().showLocalView(textView, url);
            }
            show_gift.postDelayed(new Runnable() {
                // android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                @Override
                public void run() {
                    final android.widget.RelativeLayout.LayoutParams params = new android.widget.RelativeLayout.LayoutParams(
                            show_gift.getWidth() / 3, show_gift.getWidth() / 3);
                    // params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    // int k = (int) (Math.random() * 8);
                    // LogUtils.d("Math.random()10=" + k);
                    // params.leftMargin = 10 * k;
                    // textView.setLayoutParams(params);
                    show_gift.addView(textView, params);
                    // show_gift.addView(textView);

                    final AnimatorSet set = new AnimatorSet();
                    set.addListener(new AnimatorListener() {

                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            show_gift.removeView(textView);
                            if (!isZan) {
                                totlaGiftNum--;
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            show_gift.removeView(textView);
                            if (!isZan) {
                                totlaGiftNum--;
                            }

                        }
                    });
                    ObjectAnimator ofFloat = ObjectAnimator
                            .ofFloat(textView, "translationY", (int) (show_gift
                                            .getBottom() - show_gift.getTop()),
                                    (int) ((show_gift.getBottom() - show_gift
                                            .getTop()) / 2 * Math.random()));
                    ofFloat.setDuration(3000);
                    int widthLength = (show_gift.getRight() - show_gift
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
//            }, j * 200);
            }, (!isZan) ? (totlaGiftNum + j) * 100 : j * 100);
        }
        if (!isZan) {
            totlaGiftNum += total;
        }
    }

//    private void doPrize() {
//        RequestParams params = UrlTool.getParams(Constants.CHATROOM_ID,
//                mChatroomId);
//        SendActtionTool.get(UserParams.URL_PRICEANCHOR,
//                ServiceAction.Action_Comment,
//                XiuchangAction.Action_addPraiseResouce, this, params);
//    }


    /**
     * 对视频资源点赞
     */
//    private void praisVideo(String resouceId) {
//        RequestParams params = UrlTool.getParams(Constants.RESOURCE_ID,
//                resouceId, Constants.USER_ID, userBean.getId(),
//                Constants.STATUS, String.valueOf(userBean.getStatus()));
//        SendActtionTool.get(UserParams.URL_ADD_PRAISE_RESOUCE,
//                ServiceAction.Action_Comment,
//                XiuchangAction.Action_addPraiseResouce, this, params);
//    }
    private void share(String tille) {
        if (share == null) {
            share = new DZZThreeSharePop(ShowActivity.this);
        }
        if (anchorChatroomInfo == null) {
            Utils.toast(this, "未获取选手信息");
            return;
        }

        share.setShareInfo("正在现场", getRandomContent(), anchorChatroomInfo.getFaceUrl(), mVideoId + "_" + mActivityId, true);
//        share.setShareUrl("正在现场", type, anchorChatroomInfo.getUser().getFaceUrl(), "");
//        share.showBototomPop();
        share.showAsDropDown(getMyMediaController().getmShareBtn());
    }

    /**
     * 随机生成分享内容
     *
     * @param
     * @return
     */
    private String getRandomContent() {

        String[] strs = getResources().getStringArray(R.array.shareContent_show);

        Random random = new Random();
        int num = random.nextInt(3);

        return strs[num];
    }
//    public void shareUrl(String tille, String content, String url) {
//        if (share == null) {
//            share = new DZZThreeSharePop(ShowActivity.this);
//        }
//        share.setShareUrl(tille, content, url);
//        share.showBototomPop();
//    }


    /**
     * 播放器回调
     */
    @Override
    public void videoState(int state, Bundle bundle) {
        handleADEvent(state, bundle);// 处理广告事件
        handleVideoInfoEvent(state, bundle);// 处理视频信息事件
        handlePlayerEvent(state, bundle);// 处理播放器事件
        handleLiveEvent(state, bundle);// 处理直播类事件,如果是点播，则这些事件不会回调
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////

    /**
     * 处理视频信息类事件
     *
     * @param state
     * @param bundle
     */
    private void handleVideoInfoEvent(int state, Bundle bundle) {
        switch (state) {
            case EventPlayProxy.PROXY_WAITING_SELECT_DEFNITION_PLAY:// 获取码率
                /**
                 * 处理码率
                 */
                if (playContext != null) {
                    definationsMap = playContext.getDefinationsMap();// 获取到的码率
                    if (definationsMap != null ) {
                        mQXDList.clear();
                        Iterator<Map.Entry<Integer, String>> iterator = definationsMap.entrySet().iterator();
                        while ( iterator.hasNext()) {
                            Map.Entry<Integer, String> next = iterator.next();
                            Integer key = next.getKey();// 码率所对于的key值,key值用于切换码率时，方法playedByDefination(type)所对于的值
                            String value = next.getValue();// 码率名字，比如：标清，高清，超清
                            LogUtils.d("Iterator -- key==" + key + "----------value===" + value);
                            mQXDList.add(value);
                        }
//                        getMyMediaController().setQXD(mQXDList);
                        if (mQXDList.contains("高清")) {
                            playContext.setCurrentDefinationType(Utils.getKey(definationsMap, "高清"));

                        }
                    }
                }
                break;
            case EventPlayProxy.PROXY_VIDEO_INFO_REFRESH:// 获取视频信息，比如title等等
                break;
            case ISplayer.PLAYER_PROXY_ERROR:// 请求媒体资源信息失败
//                int errorCode = bundle.getInt("errorCode");
                String msg = bundle.getString("errorMsg");
                Utils.toast(this, msg);
                break;
        }
    }

    /**
     * 处理播放器本身事件
     *
     * @param state
     * @param bundle
     */
    private void handlePlayerEvent(int state, Bundle bundle) {
        getMyMediaController().updateController();
        switch (state) {

            case ISplayer.MEDIA_EVENT_VIDEO_SIZE:
                if (videoView != null && player != null) {
                    /**
                     * 获取到视频的宽高的时候，此时可以通过视频的宽高计算出比例，进而设置视频view的显示大小。
                     * 如果不按照视频的比例进行显示的话，(以surfaceView为例子)内容会填充整个surfaceView。
                     * 意味着你的surfaceView显示的内容有可能是拉伸的
                     */
                    videoView.onVideoSizeChange(player.getVideoWidth(), player.getVideoHeight());

                    /**
                     * 获取宽高的另外一种方式
                     */
                    bundle.getInt("width");
                    bundle.getInt("height");
                }
                break;

            case ISplayer.MEDIA_EVENT_PREPARE_COMPLETE:// 播放器准备完成，此刻调用start()就可以进行播放了
                LogUtils.d("RankActivity--  +ISplayer.MEDIA_EVENT_PREPARE_COMPLETE");
                if (player != null) {
                    player.start();
                }
                break;

            case ISplayer.MEDIA_EVENT_FIRST_RENDER:// 视频第一帧数据绘制
                LogUtils.d("RankActivity--  +ISplayer.MEDIA_EVENT_FIRST_RENDER");
                isPlaying = true;
                Utils.cancaleLoadingAnim(mLoadingView, anim);
                break;
            case ISplayer.MEDIA_EVENT_PLAY_COMPLETE:// 视频播放完成
//                isPlaying = false;
                LogUtils.d("RankActivity--  +ISplayer.MEDIA_EVENT_PLAY_COMPLETE");
                break;
            case ISplayer.MEDIA_EVENT_BUFFER_START:// 开始缓冲
                Utils.showLoadingAnim(mLoadingView, anim);
                if (breakTime == 2) {
//                    getMyMediaController().show();
//                    mMediaController.show();
//                    mMediaController.showBreak();
                    breakTime = 0;
                    return;
                }
                breakTime++;
                LogUtils.d("RankActivity--  +ISplayer.MEDIA_EVENT_BUFFER_START");
                break;
            case ISplayer.MEDIA_EVENT_BUFFER_END:// 缓冲结束
//                mMediaController.hideBreak();
                Utils.cancaleLoadingAnim(mLoadingView, anim);
                LogUtils.d("RankActivity--  +ISplayer.MEDIA_EVENT_BUFFER_END");
                break;
            case ISplayer.MEDIA_EVENT_SEEK_COMPLETE:// seek完成
                LogUtils.d("RankActivity--  +ISplayer.MEDIA_EVENT_SEEK_COMPLETE ");
                break;
            case ISplayer.MEDIA_ERROR_DECODE_ERROR:// 解码错误
                LogUtils.d("RankActivity--  +ISplayer.MEDIA_ERROR_DECODE_ERROR ");
                break;
            case ISplayer.MEDIA_ERROR_NO_STREAM:// 播放器尝试连接媒体服务器失败
                LogUtils.d("RankActivity--  +ISplayer.MEDIA_ERROR_NO_STREAM  ");
                break;
            default:
                break;
        }
    }


    /**
     * 处理直播类事件
     */
    private void handleLiveEvent(int state, Bundle bundle) {
        switch (state) {
            case EventPlayProxy.PROXY_SET_ACTION_LIVE_CURRENT_LIVE_ID:// 获取当前活动直播的id
//                String liveId = bundle.getString("liveId");
                break;
            case EventPlayProxy.PROXY_WATING_SELECT_ACTION_LIVE_PLAY:// 当收到该事件后，用户可以选择优先播放的活动直播
                ActionInfo actionInfo = playContext.getActionInfo();
                // 查找正在播放的直播 或者 可以秒转点播的直播信息
                com.lecloud.entity.LiveInfo liveInfo = actionInfo.getFirstCanPlayLiveInfo();
                if (liveInfo != null) {
                    playContext.setLiveId(liveInfo.getLiveId());
                }
                break;
            default:
                break;
        }
    }

    private void getMbDataList() {
        SendActtionTool.post(UserParams.URL_RECHARGE_LIST, ServiceAction.Action_xiuchang, XiuchangAction.ACTION_GETMBDATA, this);
    }


    /**
     * 处理广告事件
     *
     * @param state
     * @param bundle
     */
    private void handleADEvent(int state, Bundle bundle) {
        switch (state) {
            case EventPlayProxy.PLAYER_PROXY_AD_START:// 广告开始
                break;
            case EventPlayProxy.PLAYER_PROXY_AD_END:// 广告播放结束
                break;
            case EventPlayProxy.PLAYER_PROXY_AD_POSITION:// 广告倒计时
//                int position = bundle.getInt(String.valueOf(EventPlayProxy.PLAYER_PROXY_AD_POSITION));// 获取倒计时
                break;
            default:
                break;
        }
    }

    Dialog goumaiDialog;

    public void initGoumaiDialog() {
        if (goumaiDialog == null) {
            goumaiDialog = DialogTool.createGoumai(this, -1, false, new DialogTool.onGoumaiDialog() {
                @Override
                public void onLandSpace() {
                    startActivity(new Intent(ShowActivity.this, BuyGiftActivity.class));
                }
            });
        }
        goumaiDialog.show();

    }




   private  String toUserName;
    ShowNewMediacontroler.OnShowNewMediacontrollerListener controllerListenner = new ShowNewMediacontroler.OnShowNewMediacontrollerListener() {
        @Override
        public void onReportClicked() {
            initReportDialog();
        }


        @Override
        public void onLockClicked(boolean locked) {
            isLocked = locked;
            if (isLocked) {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                else
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }

        @Override
        public void onChangeScreenClicked() {

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                fangDalock = false;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                fangDalock = true;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            LogUtils.d("onChangeScreenClicked----fangDalock==" + fangDalock);
        }

        @Override
        public void onSharedClicked() {
            share(mTitle);
        }

        @Override
        public void onChatClicked(String hint, final String toUserName) {
            ShowActivity.this.toUserName=toUserName;
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (mTanmuDialog == null)
                    mTanmuDialog = new TanmuDialog(ShowActivity.this,
                            new TanmuDialog.OnSendTanmuListener() {
                                @Override
                                public void sendMessage(String edtStr) {
                                    // TODO 重要方法，需要重构代码
                                    if (userBean.getUserName().equals(ShowActivity.this.toUserName)) {
                                        chatRoomFragment.cf.setToUserName("");
                                    } else {
                                        chatRoomFragment.cf.setToUserName(ShowActivity.this.toUserName);
                                    }
                                        chatRoomFragment.cf.setEditText(edtStr, false);
                                }
                            });
                mTanmuDialog.show();
                LogUtils.d("hint----"+hint);
                if (!TextUtils.isEmpty(hint)&&(!userBean.getUserName().equals(toUserName))) {
                    mTanmuDialog.setHintMethod(hint);
                } else {
                    mTanmuDialog.setHintMethod("在这里可以发送弹幕哦！");
                }
            } else {
                if (chatRoomFragment != null) {
                    int i = chatRoomFragment.getViewPager().getCurrentItem();
                    if (i == 0) {
                        chatRoomFragment.cf.getEdtChat().requestFocus();
                        chatRoomFragment.cf.getEdtChat().setFocusableInTouchMode(true);
                        GeneralTool.KeyBoardShow(ShowActivity.this, chatRoomFragment.cf.getEdtChat());
                    } else {
                        try {
                            chatRoomFragment.getViewPager().setCurrentItem(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
//          Utils.toast(ShowActivity.this,"聊天按钮");
        }


        @Override
        public void onZanClicked() {
//            praisVideo(mVideoId);
            if (chatRoomFragment != null && chatRoomFragment.cf != null) {
                disPlayPicAnima("", 1, true);
                if (giftDialog != null) {
                    sendMessageToChatroom(giftDialog.getmCurrentGiftBean(), 1, true);
                } else {
                    sendMessageToChatroom(null, 1, true);
                }
            } else {
                Utils.toast(ShowActivity.this, "聊天室未连接");
            }
//            doPrize();
        }

        @Override
        public void onWuTaiClicked(int index) {
//            isFirstSetRate = true;
//        isFirstPlay = true;
            LiveInfo liveInfo = mLiveInfoList.get(index);
            playLive(liveInfo);
            mActivityId = liveInfo.getActivityId();
        }

        @Override
        public void onQXDClicked(int index) {
            if (index != mQXDIndex && player != null && mQXDList != null
                    && mQXDList.size() > 0) {
                player.playedByDefination(Utils.getKey(definationsMap, mQXDList.get(index)));
                mQXDIndex = index;
            }
            breakTime = 0;
        }

        @Override
        public void onGiftBtnClicked() {
            initGiftDialog();
        }

        @Override
        public void onJiemudanClicked() {
            if (formDialog != null) {
                formDialog.show();
            } else {
                formDialog = DialogTool.createFormDialog(ShowActivity.this, mFormList, mTitle);
                formDialog.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        isDialogShow = false;
                    }
                });
            }
            isDialogShow = true;
        }

        @Override
        public boolean isPlaying() {
            return false;
        }

        @Override
        public void onPlayClicked(boolean playOrstop) {

        }

        @Override
        public void onMessageImgClicked(String userId) {
            if (userId.equals(UserService.getInatance().getUserBean(ShowActivity.this).getId())) {
                return;
            }
            new PeopleInfoDialog(ShowActivity.this, userId).show();
        }

        @Override
        public void onAnchorImgClicked(boolean isAnchor, String userId) {
            if (isAnchor) {
                if (anchorChatroomInfo == null) {
                    return;
                }
                if (!UserService.getInatance().getUserBean(ShowActivity.this).getId().equals(anchorChatroomInfo.getId())) {
                    new PeopleInfoDialog(ShowActivity.this, anchorChatroomInfo.getId()).show();
                }
            } else {
                if (!UserService.getInatance().getUserBean(ShowActivity.this).getId().equals(userId)) {
                    new PeopleInfoDialog(ShowActivity.this, userId).show();
                }
            }

        }

        @Override
        public long getDuration() {
            return 0;
        }

        @Override
        public long getCurrentPosition() {
            return 0;
        }

        @Override
        public void seekTo(long pos) {

        }

        @Override
        public void showGiftGuidDialog() {
            initGiftGuidDialog();
        }

        @Override
        public int getBufferPercentage() {
            return 0;
        }

        @Override
        public void onBackClicked() {
            LogUtils.d("onBackClicked--");
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setScreenOrientation(false);
            } else {
                GeneralTool.KeyBoardCancle(ShowActivity.this);
                finish();
            }
        }

        @Override
        public void onSendGift(boolean showDialog, int sendGiftNum) {
            ShowActivity.this.shouldSentGiftNum = sendGiftNum;
//            sendGift(sendGiftNum);
        }

        @Override
        public void castMbBuyGift(int count, int sendGiftNum) {
            ShowActivity.this.shouldSentGiftNum = sendGiftNum;
//            costMbBuyGift(count);
        }

        @Override
        public void onInitMbDialog() {
//            initMbDialog();
            initGoumaiDialog();
        }
    };
    AlertDialog reportDialog;
    String[] reportDatas = {"色情低俗", "政治敏感", "暴力恐怖", "其他"};

    private void initReportDialog() {
        if (reportDialog == null) {

            reportDialog = new AlertDialog.Builder(this).setTitle("举报的选项有").setItems(reportDatas, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    reportAnchor(reportDatas[which]);
                    Toast.makeText(ShowActivity.this, "举报的内容是：" + reportDatas[which], Toast.LENGTH_SHORT).show();
                }
            }).create();
        }
        reportDialog.show();
    }


}
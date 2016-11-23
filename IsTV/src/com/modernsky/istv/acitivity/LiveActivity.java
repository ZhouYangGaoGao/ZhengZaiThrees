package com.modernsky.istv.acitivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.http.RequestParams;
import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.action.CommentAction;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.bean.Ad;
import com.modernsky.istv.bean.FormWuTaiInfo;
import com.modernsky.istv.bean.LatestVideo;
import com.modernsky.istv.bean.LiveInfo;
import com.modernsky.istv.bean.Pinglun;
import com.modernsky.istv.bean.ResultBean;
import com.modernsky.istv.bean.ResultList;
import com.modernsky.istv.bean.ResultPinglun;
import com.modernsky.istv.bean.UserEntity;
import com.modernsky.istv.bean.XiuchanMessage;
import com.modernsky.istv.fragment.ZhiBoFragment;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.BitmapTool;
import com.modernsky.istv.tool.DialogTool;
import com.modernsky.istv.tool.DialogTool.DialogLister;
import com.modernsky.istv.tool.NetworkHelper;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.TimeTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.Constants.UserParams;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.PreferencesUtils;
import com.modernsky.istv.utils.ScreenUtils;
import com.modernsky.istv.utils.Utils;
import com.modernsky.istv.utils.WeakHandler;
import com.modernsky.istv.view.MediaController;
import com.modernsky.istv.view.MediaController.MediaPlayerControl;
import com.modernsky.istv.view.MediaController.OnHiddenListener;
import com.modernsky.istv.view.MediaController.OnMediaControllerClickedListener;
import com.modernsky.istv.view.MediaController.OnShownListener;
import com.modernsky.istv.view.PopThreeShare;
import com.modernsky.istv.widget.WidgetDanMu;
import com.modernsky.istv.window.TanmuDialog;
import com.modernsky.istv.window.TanmuDialog.OnSendTanmuListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
//  直播    257   原话
//             256   1080
//             255   720
//             254   超轻
//             253   高清
//             252   表情
//             251   流畅

/**
 * 播放界面
 *
 * @author zxm
 */
public class LiveActivity extends BaseActivity implements MediaPlayerControl,
        OnMediaControllerClickedListener, OnHiddenListener, OnShownListener, OnPlayStateListener {

    private static FragmentManager fMgr;
    private String albumId = "", videoId = "";
    private String userId = "";
    private int userStatus;
    private String stringType = "";
    private int mQXDIndex = -1;
    // private long playTime;
    // private long nowTime = 0;
    // private float mScrollX;
    private String standardPic = "";
    private Dialog formDialog;
    private boolean isDialogShow = false;
    private ImageView mLoadingImg;
    private AnimationDrawable anim;
    private RelativeLayout.LayoutParams relativeMatchLayoutParams;

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    private String mStreamId = "201510123000113";
    private String mLiveId = "201510123000113";
    private String mTitle = "";
    // private VideoView mVideoView;
    private View mVolumeBrightnessLayout;
    private ImageView mOperationBg;
    private ImageView mOperationPercent;
    private AudioManager mAudioManager;
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
    /**
     * 当前缩放模式
     */
    private GestureDetector mGestureDetector;
    private MediaController mMediaController;
    private View mLoadingView;

    FrameLayout frameLayout;
    private ZhiBoFragment chatRoomFragment;
    private View rl_ad;
    private ImageView img_ad;
    private long mPriseCount = 0;
    private BitmapUtils bitmapTool;
    private int breakTime = 0;
    private String adUrl;
    private List<Ad> mAds = new ArrayList<Ad>();
    private boolean isLive;
    private LatestVideo tempAlbumLastVideo;

    private RelativeLayout mBarrageView;
    private List<FormWuTaiInfo> mFormList;

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


    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.start();
        }
        if (isBackgroud) {
            if (!UserService.getInatance().isNeedLogin(this)) {
                UserEntity bean = UserService.getInatance().getUserBean(this);
                userId = bean.getId();
                userStatus = bean.getStatus();
                if (!TextUtils.isEmpty(videoId)) {
                    LogUtils.d("onResume====viewdoId=" + videoId);
                    getIfZanAndIfShoucang(videoId);
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.pause();
            isBackgroud = true;
        }
    }

    private void sendCloseInfo(String videoId) {
        RequestParams params = UrlTool.getPostParams(Constants.VIDEO_ID, videoId);
        SendActtionTool.post(Constants.URL_EXIT_SERVLET, null, CommentAction.Action_send_Close, this, params);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sendCloseInfo(videoId);
        if (playContext != null) {
//            addPlayRecord(String.valueOf(playTime / 1000));
            playContext.destory();
        }
        isBackgroud = true;
//        Utils.sendBroadcastToService(8, this);
    }

    private void initFullLayoutParams() {
        relativeMatchLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        relativeMatchLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        relativeMatchLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        relativeMatchLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        relativeMatchLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
    }

    /**
     * 初始化PlayContext PlayContext作为播放器的上下文而存在，既保存了播放器运行时的临时数据，也记录播放器所需要的环境变量。
     */
    private void initPlayContext() {
        LogUtils.d("LiveActivity--" + "initPlayContext");
        playContext = new PlayContext(this);
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


    /**
     * 初始化首个Fragment
     */
    private void initFragment() {
        chatRoomFragment = new ZhiBoFragment();
        currentFragment = chatRoomFragment;
//        fragmetns.add(currentFragment);
        getFragmentTransaction().add(R.id.fragmentRoot, chatRoomFragment,
                chatRoomFragment.getClass().getName()).commit();
    }

    public static boolean isOpened;

    private View include_video;
    private String mActivityId;

    @Override
    protected void onStart() {
        isOpened = true;
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        rePlayVideo(videoId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_cancel_ad:
                tongleAd(false);
                break;
            case R.id.image_ad:
                LogUtils.t("adUrl", adUrl);
                if (!TextUtils.isEmpty(adUrl)) {
                    Uri uri = Uri.parse(adUrl);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }

    int adCount;
    private int index;
    private Fragment currentFragment;
    //    private List<Fragment> fragmetns = new ArrayList<Fragment>();
    private int windowWidth;
    private int windowHeight;

    /**
     * 广告开关
     */
    private void tongleAd(boolean b) {
        if (adCount <= 0) {
            return;
        }
        if (b) {
            rl_ad.setVisibility(View.VISIBLE);
        } else {
            rl_ad.setVisibility(View.GONE);
            Ad ad = mAds.get(index % adCount);
            adUrl = ad.getUrl();
            bitmapTool.display(img_ad, ad.getBigImage());
            index++;
        }
    }

    /**
     * 分享
     *
     * @param videoId2
     */
    PopThreeShare share;
    // 乐视用
    private RelativeLayout mPlayerLayoutView;
    //    private LivePlayCenter mPlayerView;
    private boolean isBackgroud;
    private Set<String> mRateType;
    private List<String> mQXDList;
    //    private boolean isFirstPlay = true;
//    private boolean isFirstSetRate = true;
    private boolean ifHasPrised;
    private boolean ifHasShoucang;
    private long createTime;
    private int count = 0;

    private void share(String tille, String content, String vebreId) {
//        String tagLiveUrl = "http://wap.zhengzai.tv/pages/live.html?vbreId=" + vebreId;
        String tagLiveUrl =Constants.tagLiveUrl+  vebreId;
        share = new PopThreeShare(this);
//        share.setShareUrlForAnchor("正在现场", mTitle, tagLiveUrl, standardPic,"");
        share.setShareUrlForAnchor("正在现场", mTitle, tagLiveUrl, "","");
        share.showBototomPop();
    }


    /**
     * 对视频资源点赞
     */
    private void praisVideo(String resouceId) {
        if (!isUesrLogined()) {
            return;
        }
        RequestParams params = UrlTool.getParams(Constants.RESOURCE_ID,
                resouceId, Constants.USER_ID, userId, Constants.STATUS,
                String.valueOf(userStatus));
        SendActtionTool.get(UserParams.URL_ADD_PRAISE_RESOUCE,
                ServiceAction.Action_Comment,
                CommentAction.Action_addPraiseResouce, this, params);
    }


    /**
     * 判断用户状态，如果未登录，跳转登录界面
     */
    public boolean isUesrLogined() {
        if (TextUtils.isEmpty(userId)) {
            if (UserService.getInatance().isNeedLogin(this)) {
                startActivity(new Intent(this, LoginActivity.class));
                return false;
            } else {
                UserEntity bean = UserService.getInatance().getUserBean(this);
                userId = bean.getId();
                userStatus = bean.getStatus();
                return true;
            }
        }
        return true;
    }

    private void initPlayView() {
        LogUtils.d("initPlayView");
        initVideoView();// 初始化videoView
        initPlayContext();// 初始化playContext
    }

    private void initLivePlay() {
        mQXDList = new ArrayList<String>();
        this.mPlayerLayoutView = (RelativeLayout) this.findViewById(R.id.LivePlayerLayout);
        initPlayView();
//        mPlayerView = new LivePlayCenter(this, false, true);
////        mPlayerView = new LivePlayCenter(this, false, true, 1920, 1080,
////                PlayerConstants.DISPLAY_SCALE_ALL);
//        this.mPlayerLayoutView.addView(this.mPlayerView.getPlayerView());
//        this.mPlayerView.setPlayerStateCallback(new PlayerStateCallback() {
//            @Override
//            public void onStateChange(int state, Object... extra) {
//                if (state == PlayerStateCallback.PLAYER_VIDEO_PAUSE) {
//                    isPlaying = false;
//                } else if (state == PlayerStateCallback.PLAYER_VIDEO_PLAY) {
//                    isPlaying = true;
//                    LogUtils.e("onStateChange", "PLAYER_VIDEO_PLAY");
//                    Utils.cancaleLoadingAnim(mLoadingView, anim);
//                    mRateType = mPlayerView.getRateType();
//                    if (isFirstSetRate) {
//                        if (mRateType != null) {
//                            LogUtils.d("RateType", "onGetRateType1111"
//                                    + mRateType.toString());
//                            mQXDList.clear();
//                            mQXDList.addAll(mRateType);
//                            LogUtils.d("========" + mQXDList.toString());
//                            getMyMediaController().setQXD(
//                                    Utils.mRateToString(mQXDList, 1));
//                            isFirstSetRate = false;
//                        }
//                    }
//                    if (getMyMediaController().isShowing())
//                        getMyMediaController().hide();
//                } else if (state == PlayerStateCallback.PLAYER_VIDEO_RESUME) {
//                    isPlaying = true;
//                    LogUtils.d("onStateChange", "PLAYER_VIDEO_RESUME");
//                } else if (state == PlayerStateCallback.PLAYER_STOP) {
//                    isPlaying = false;
//
//                } else if (state == PlayerStateCallback.PLAYER_VIDEO_COMPLETE) {
//                    LogUtils.d("onStateChange", "PLAYER_VIDEO_COMPLETE");
//                    isPlaying = false;
//                } else if (state == PlayerStateCallback.PLAYER_BUFFERING_START) {
//                    LogUtils.d("onStateChange", " PlayerStateCallback.PLAYER_BUFFERING_START");
//                    // 开始缓存，暂停播放
////                    if (isPlaying()) {
////                        stopPlayer();
////                        needResume = true;
////                    }
//                    Utils.showLoadingAnim(mLoadingView, anim);
//                    //
//                    if (breakTime == 2) {
//                        mMediaController.show();
//                        mMediaController.showBreak();
//                        breakTime = 0;
//                        return;
//                    }
//                    breakTime++;
//                } else if (state == PlayerStateCallback.PLAYER_BUFFERING_END) {
//                    LogUtils.d("onStateChange", " PlayerStateCallback.PLAYER_BUFFERING_END");
//                    // 缓存完成，继续播放
//                    mMediaController.hideBreak();
//                    Utils.cancaleLoadingAnim(mLoadingView, anim);
////                    if (needResume) {
////                        startPlayer();
////                    }
//                } else if (state == PlayerStateCallback.PLAYER_ERROR) {
//                    LogUtils.d("onStateChange", " PlayerStateCallback.PLAYER_ERROR");
//
//                }
//            }
//        });
    }

    @Override
    public void setContentView(Bundle savedInstanceState) {
        createTime = System.currentTimeMillis();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_live);
        // 初始化乐视
        initFullLayoutParams();
        initLivePlay();
        mBarrageView = (RelativeLayout) findViewById(R.id.barrage);
        rl_ad = findViewById(R.id.rl_ad);
        img_ad = (ImageView) findViewById(R.id.image_ad);
        img_ad.setOnClickListener(this);
        findViewById(R.id.image_cancel_ad).setOnClickListener(this);
        bitmapTool = BitmapTool.getInstance().initAdapterUitl(LiveActivity.this);
        reSetWindowSize();
        initView();
        // 获取电话通讯服务
        TelephonyManager tpm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        // 创建一个监听对象，监听电话状态改变事件
        tpm.listen(new MyPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void reSetWindowSize() {
        Display disp = getWindowManager().getDefaultDisplay();
        windowWidth = disp.getWidth();
        windowHeight = disp.getHeight();
    }

    @Override
    public void findViewById() {
        // 获取FragmentManager实例
        fMgr = getSupportFragmentManager();
        Intent intent = getIntent();
        stringType = intent.getStringExtra(Constants.TYPE);
        rootView = findViewById(R.id.rootView);
        LogUtils.d("stringExtra" + stringType);
        try {
            mTitle = intent.getStringExtra(Constants.VIDEO_NAME);
            videoId = intent.getStringExtra(Constants.VIDEO_ID);
            playVideoGetInfo(videoId);
            // tv_video_name.setText(mTitle);
            getMyMediaController().setVideoShowName(mTitle);
            full(false);
            initFragment();
            mPlayerLayoutView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                }
            }, 1000);
            // getPraiseCount(videoId, "0");
            getIfZanAndIfShoucang(videoId);
        } catch (Exception e) {
            e.printStackTrace();
            tofinish();
        }
//        String liveId=intent.getStringExtra("liveId");
//        playVideoByStreamId(liveId);
    }

    /**
     * 获取是否点赞和是否收藏
     */
    private void getIfZanAndIfShoucang(String videoId) {
        RequestParams params = UrlTool.getParams(Constants.VIDEO_ID, videoId,
                Constants.USER_ID, userId);
        SendActtionTool.get(UserParams.URL_USER_IfHasZanAndShoucang,
                ServiceAction.Action_Comment,
                CommentAction.Action_getPraiseAndShoucang, this, params);
    }

    private void initGetPrisedAndShouCang(JSONObject valueinfo) {

        try {
            JSONObject value = valueinfo.getJSONObject("data");
            String isPraise = value.getString("isPraise");
            String isCollection = value.getString("isCollection");
//            int praiseCount = value.getInt("praiseCount");
//            int commentCount = value.getInt("commentCount");
            if (isPraise.equals("0")) {
                getMyMediaController().zanImg
                        .setImageResource(R.drawable.icon_07bofang_good_hl);
            } else if (isPraise.equals("1")) {
                getMyMediaController().zanImg
                        .setImageResource(R.drawable.icon_07bofang_good);
            }
            if (isCollection.equals("0")) {
                getMyMediaController().mShouCangButton
                        .setImageResource(R.drawable.icon_07bofang_shoucang);
                ifHasShoucang = false;
            } else if (isCollection.equals("1")) {
                ifHasShoucang = true;
                getMyMediaController().mShouCangButton
                        .setImageResource(R.drawable.icon_07bofang_shoucang_hl);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // /**
    // * 获取评论数量
    // */
    // private void getPraiseCount(String videoId, String buildTime) {
    //
    // RequestParams params = UrlTool.getParams(Constants.RESOURCE_ID,
    // videoId, Constants.BUILD_TIME, buildTime, Constants.USER_ID,
    // "0", Constants.STATUS, "1");
    //
    // SendActtionTool.get(UserParams.URL_COMMENT_LIST,
    // ServiceAction.Action_Comment,
    // CommentAction.Action_getPraiseCount, this, params);
    // }

    public void playVideoByUrl(String videoId, String url, String videoName) {
        mTitle = videoName;
        this.videoId = videoId;
        playVideoByStreamId(videoId);
    }

    public void playVideoByStreamId(final String streamId) {
        LogUtils.t("playVideoByUrl", "playVideoByUrl===" + streamId + "---");
        tongleAd(false);
        if (NetworkHelper.isNetworkConnected(this)
                && !NetworkHelper.isWifiConnected(this)
                && !PreferencesUtils.getBooleanPreferences(this,
                PreferencesUtils.TYPE_NET_TOGLE)) {
            DialogTool.createNetWorkDialog(this, new DialogLister() {

                @Override
                public void onCountinue() {
                    setPlayStreamId(streamId);
                }

                @Override
                public void onCancelListener() {
                    tofinish();
                }
            }).show();
        } else {
            setPlayStreamId(streamId);
        }
    }

    /**
     * 播放，初始话播放控件
     *
     * @param streamId
     */
    private void setPlayStreamId(String streamId) {
        if (streamId.length() < 3) {
            Utils.toast(this, "直播流异常，请再次进入！");
            return;
        }
        this.mStreamId = streamId;
        this.mLiveId = passStreamId(mStreamId);
        LogUtils.d("mStreamId===" + mStreamId);
//        if (isFirstPlay) {
////            boolean isPlayBest=PreferencesUtils.getBooleanPreferences(this,PreferencesUtils.TYPE_DEFAULT_QXD);
////            if (isPlayBest) {
////                this.mPlayerView.selectRateType("25", "16", "13", "10");
////            } else {
////                this.mPlayerView.selectRateType("10", "13", "16", "25");
////            }
//            this.mPlayerView.selectRateType("16");
//            isFirstPlay = false;
//        }
        mBundle = LetvParamsUtils.setLiveParams(null, mLiveId, true, false);
        playvideo();
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


    /**
     * 播放指定视频
     *
     * @param videoId
     */
    public void playVideoGetInfo(String videoId) {
        UserEntity bean = UserService.getInatance().getUserBean(this);
        if (bean != null) {
            userId = bean.getId();
        }
//        RequestParams params = UrlTool.getParams(Constants.VIDEO_ID, videoId,
//                Constants.USER_ID, userId);
        RequestParams params = UrlTool.getParams(Constants.VIDEO_ID, videoId,
                Constants.USER_ID, userId, Constants.FILTER,
                Constants.FILTER_VIDEO);
        if (!TextUtils.isEmpty(videoId)) {
            SendActtionTool.get(Constants.URL_VIDEO_DETAIL,
                    ServiceAction.Action_Comment,
                    CommentAction.Action_GetVideo_By_Id, this, params);
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
        } else
            initPlayView();
        addPlayToServices(videoId);
    }

    /**
     * 添加播放统计
     *
     * @param videoId
     */
    private void addPlayToServices(String videoId) {
        RequestParams params = UrlTool.getPostParams(Constants.VIDEO_ID, videoId);
        SendActtionTool.post(Constants.URL_LIVE_ENGINE, null, CommentAction.Action_addPlayToServices, this, params);
    }

    /**
     * 重置播放指定视频
     *
     * @param videoId
     */
    public void rePlayVideo(String videoId) {
        UserEntity bean = UserService.getInatance().getUserBean(this);
        if (bean != null) {
            userId = bean.getId();
        }
        RequestParams params = UrlTool.getParams(Constants.VIDEO_ID, videoId,
                Constants.USER_ID, userId, Constants.FILTER,
                Constants.FILTER_VIDEO);
        if (!TextUtils.isEmpty(videoId)) {
            SendActtionTool.get(Constants.URL_VIDEO_DETAIL,
                    ServiceAction.Action_Comment,
                    CommentAction.Action_GetVideo_Restart, this, params);
        }
    }

    /**
     * 发送弹幕
     */
    public void sendTanmu(final XiuchanMessage message, final boolean isSelf) {
//        if (mBarrageView.getVisibility() != View.VISIBLE) {
//            return;
//        }
//        if (message.getType()>5) {
//            return;
//        }
        if (message.getBuildTime() < createTime && count < 20) {
            count++;
            mPlayerLayoutView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    WidgetDanMu wdm = new WidgetDanMu(getBaseContext());
                    wdm.setParameter(mBarrageView, message, isSelf);
                    wdm.show();
                }
            }, count * 500);
            return;
        }
        WidgetDanMu wdm = new WidgetDanMu(getBaseContext());
        wdm.setParameter(mBarrageView, message, isSelf);
        wdm.show();
    }

    /**
     *
     */
    private void initView() {
        include_video = findViewById(R.id.include_video);
        // ~~~ 绑定控件
        // tv_video_name = (TextView) findViewById(R.id.tv_video_name);
        mVolumeBrightnessLayout = findViewById(R.id.operation_volume_brightness);
        mOperationBg = (ImageView) findViewById(R.id.operation_bg);
        mOperationPercent = (ImageView) findViewById(R.id.operation_percent);
        mLoadingView = findViewById(R.id.video_loading);
        mLoadingImg = (ImageView) findViewById(R.id.img_loading);

        anim = (AnimationDrawable) mLoadingImg.getBackground();
        // rl_video_info = (RelativeLayout) findViewById(R.id.rl_video_info);
        frameLayout = (FrameLayout) findViewById(R.id.fragmentRoot);
        // ~~~ 绑定数据
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        // 设置显示名称

        getMyMediaController().setAnchorView(include_video);
        getMyMediaController().setOnMediaControllerClickedListener(this);
        getMyMediaController().setOnShownListener(this);
        getMyMediaController().setOnHiddenListener(this);
        getMyMediaController().setMediaPlayer(this);
        getMyMediaController().setVerticalMode(0);
        getMyMediaController().show();
        // getVideoView().requestFocus();

        getGestureDetector();
        addAd();
        Utils.showLoadingAnim(mLoadingView, anim);
    }

    /**
     * 获取Fragment事务处理
     */
    private FragmentTransaction getFragmentTransaction() {
        FragmentTransaction transaction = fMgr.beginTransaction();
        transaction.setCustomAnimations(R.anim.push_right_in,
                R.anim.push_left_out, R.anim.push_left_in,
                R.anim.push_right_out);
        return transaction;
    }

    private void addAd() {
        SendActtionTool.get(Constants.URL_GET_VIDEO_AD,
                ServiceAction.Action_Comment, CommentAction.Action_getVideoAd,
                this);
    }

    public GestureDetector getGestureDetector() {
        if (mGestureDetector == null) {
            mGestureDetector = new GestureDetector(this,
                    new MyGestureListener());
        }
        return mGestureDetector;
    }

    private MediaController getMyMediaController() {
        if (mMediaController == null) {
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mMediaController = (MediaController) findViewById(R.id.mediacontroller_view);
        }
        return mMediaController;
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

//    /**
//     * 定时隐藏
//     */
//    private Handler mDismissHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 0:
//                    mVolumeBrightnessLayout.setVisibility(View.GONE);
//                    break;
//                case 1:
//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
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
                default:
                    break;
            }
        }
    };
    private List<LiveInfo> mLiveInfoList = new ArrayList<LiveInfo>();
    private List<LiveInfo> mVrInfoList = new ArrayList<LiveInfo>();
    private boolean fangDalock;
    private View rootView;


    private class MyGestureListener extends SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            if (getMyMediaController().isShowing()) {
                getMyMediaController().hide();
            } else
                getMyMediaController().show();
            LogUtils.d("VODplayerLayout");

            return false;
        }

        /**
         * 滑动
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            try {

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
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (isDialogShow && formDialog != null) {
                formDialog.show();
            }
            full(true);
            if (player != null) {
//                mPlayerView
//                        .changeOrientation(Configuration.ORIENTATION_LANDSCAPE);
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
            full(false);
//            mPlayerView.changeOrientation(Configuration.ORIENTATION_PORTRAIT);
            if (!isLocked) {
                mDismissHandler.removeMessages(1);
                mDismissHandler.sendEmptyMessageDelayed(1, 2000);
            }
        }

    }

    /**
     * 全屏模式开关
     *
     * @param enable
     */
    private void full(boolean enable) {
        // reSetWindowSize();
        if (enable) {
            Utils.setFullScreen(this);
            if (PreferencesUtils.TYPE_TANMU_SHOW)
                mBarrageView.setVisibility(View.VISIBLE);
            // rl_video_info.setVisibility(View.GONE);
            frameLayout.setVisibility(View.GONE);
//            rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        } else {
            Utils.cancelFullScreen(this);
            if (mTanmuDialog != null && mTanmuDialog.isShowing())
                mTanmuDialog.dismiss();
            mBarrageView.setVisibility(View.GONE);
            // rl_video_info.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);
//            rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
        LayoutParams layoutParams = include_video.getLayoutParams();
        if (enable) {
//            layoutParams.width = LayoutParams.MATCH_PARENT;
            layoutParams.height = LayoutParams.MATCH_PARENT;
            layoutParams.width = LayoutParams.MATCH_PARENT;

            include_video.setLayoutParams(layoutParams);
        } else {
            // getMediaController().setPaddingRelative(0, 0, 0, 0);
            layoutParams.height = ScreenUtils.getScreenHeight(this) / 3;
            layoutParams.width = LayoutParams.MATCH_PARENT;
            LogUtils.d("fullfalse==" + layoutParams.height);
            include_video.setLayoutParams(layoutParams);
        }
        if (videoView != null)
            videoView.setLayoutParams(relativeMatchLayoutParams);
    }

    private void stopPlayer() {
        if (player != null)
            player.pause();
//            mPlayerView.pauseVideo();
    }

    private void startPlayer() {
        if (player != null)
            player.start();
//            mPlayerView.resumeVideo();
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        super.onSuccess(service, action, value);
        String jsonString = value.toString();
        LogUtils.t("action== " + action.toString(), jsonString);
        switch ((CommentAction) action) {
            case Action_GetVideo_By_Id:
                try {
                    ResultBean<LatestVideo> resultVideoBean = JSON
                            .parseObject(
                                    jsonString,
                                    new TypeReference<ResultBean<LatestVideo>>() {
                                    });
                    LatestVideo albumLastVideo = resultVideoBean.data;
                    if (chatRoomFragment != null)
                        chatRoomFragment.setChatroomId(albumLastVideo
                                .getChatroomId());
                    if (!TextUtils.isEmpty(albumLastVideo.getName())) {
                        mTitle = albumLastVideo.getName();
                        getMyMediaController().setVideoShowName(mTitle);
                    }
                    checkLiveAndPlay(albumLastVideo);

                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.e("javaBean 转型失败 " + e.toString());
                }
                break;
            case Action_GetVideo_Restart:
                try {
                    ResultBean<LatestVideo> resultVideoBean = JSON
                            .parseObject(
                                    jsonString,
                                    new TypeReference<ResultBean<LatestVideo>>() {
                                    });
                    LatestVideo albumLastVideo = resultVideoBean.data;
                    long endTime = TimeTool
                            .getMillTime(albumLastVideo.getEndTime());
                    if (endTime > 0 && endTime < System.currentTimeMillis()) {
                        Utils.toast(this, "感谢您的观看，本场直播已经结束。");
                        tofinish();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case Action_addPraiseResouce:
                try {
                    ResultPinglun<Pinglun> resultPinglun = JSON.parseObject(
                            jsonString,
                            new TypeReference<ResultPinglun<Pinglun>>() {
                            });
                    if (resultPinglun.getStatus() == 1) {
                        Utils.toast(this, R.string.dianzan_success);
                        getMyMediaController().zanImg
                                .setImageResource(R.drawable.icon_07bofang_good);
                    } else {
                        Utils.toast(this, resultPinglun.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case Action_getVideoAd:
                try {
                    ResultList<Ad> resultBean = JSON.parseObject(jsonString,
                            new TypeReference<ResultList<Ad>>() {
                            });
                    List<Ad> ads = resultBean.data;
                    if (ads != null && ads.size() > 0) {
                        mAds = ads;
                        Ad ad = ads.get(0);
                        adCount = ads.size();
                        adUrl = ad.getUrl();
                        bitmapTool.display(img_ad, ad.getBigImage());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case Action_getPraiseAndShoucang:
                initGetPrisedAndShouCang((JSONObject) value);
                break;
            default:
                break;
        }
    }


    private void checkLiveAndPlay(LatestVideo albumLastVideo) {

        if (albumLastVideo.getIsNeedPay() == 1
                && albumLastVideo.getIsPay() == 0) {
            DialogTool.createPayDialog(this,
                    String.valueOf(albumLastVideo.getAlbumId()));
            tempAlbumLastVideo = albumLastVideo;
        } else
            setLiveUrlandPlay(albumLastVideo);
    }

    private void initVrAndLiveInfoList(List<LiveInfo> liveInfo2) {
        mLiveInfoList.clear();
        mVrInfoList.clear();
        for (int i = 0; i < liveInfo2.size(); i++) {
            if ("3".equals(liveInfo2.get(i).getType())) {
                mVrInfoList.add(liveInfo2.get(i));
            } else {
                mLiveInfoList.add(liveInfo2.get(i));
            }
        }
        if (mLiveInfoList.size() > 0) {
            getMyMediaController().showWutai(true);
            getMyMediaController().setLiveWutai(mLiveInfoList);
        }

        if (mVrInfoList.size() > 0) {
            getMyMediaController().showVr(true);
            getMyMediaController().setVr(mVrInfoList);
        }
    }

    private void setLiveUrlandPlay(LatestVideo albumLastVideo) {
        String streamId = "";
        if (albumLastVideo.getVideoType() == 4) {
            //获取舞台信息
            LogUtils.d("setLiveUrlandPlay");
            mFormList = albumLastVideo.getCatalog();
            if (mFormList == null || mFormList.size() == 0) {
                getMyMediaController().setHasFormMenu(false);
            } else {
                getMyMediaController().setHasFormMenu(true);
            }
            LogUtils.d("mFormList==" + mFormList.toString());
            LogUtils.d("mFormList size()==" + mFormList.size());
            List<LiveInfo> liveInfo2 = albumLastVideo.getLiveInfo();
            if (liveInfo2 != null && liveInfo2.size() > 1) {
//                mLiveInfoList.clear();
//                mLiveInfoList.addAll(liveInfo2);
                initVrAndLiveInfoList(liveInfo2);

            } else {
                mLiveInfoList = null;
                getMyMediaController().showWutai(false);
            }
            if (liveInfo2 != null) {
                LiveInfo liveInfo = liveInfo2.get(0);
                LogUtils.d("albumLastVideo.getVedioType() == 4");
                if (liveInfo != null) {
                    streamId = liveInfo.getStreamId();
                    mActivityId = liveInfo.getActivityId();
                }
            }
            isLive = true;
        }
//        else {
//            // mMediaController.setLiveMode(false);
//            // isLive = false;
//        }
        standardPic = albumLastVideo.getStandardPic();
        playVideoByStreamId(streamId);
    }

    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {
        LogUtils.t("tiaoshi", value.toString());
        if (action == CommentAction.Action_GetVideo_By_Id) {
            Utils.toast(this, "找不到视频资源！");
            tofinish();
        } else {
            Utils.toast(this, value.toString());
        }
    }

    @Override
    public void onException(ServiceAction service, Object action, Object value) {
        super.onException(service, action, value);
        LogUtils.t("onException", action.toString());
    }

    /**
     * 是否需要自动恢复播放，用于自动暂停，恢复播放
     */
    private boolean needResume;
    private long exitTime;
    private boolean isLocked;

    /*
     * (non-Javadoc) 用于锁定屏幕方向
     */
    @Override
    public void onBackClicked() {
        LogUtils.d("onBackClicked--");
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setScreenOrientation(false);
        } else
            tofinish();
    }

    private void setScreenOrientation(boolean toLandScape) {
        int screenOrientationPortrait = toLandScape ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setRequestedOrientation(screenOrientationPortrait);
        mDismissHandler.removeMessages(1);
        mDismissHandler.sendEmptyMessageDelayed(1, 2000);
    }

    @Override
    public void onLockChanged(boolean locked) {
        isLocked = locked;
        if (locked) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            else
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    @Override
    public void onShown() {
    }

    @Override
    public void onHidden() {
//        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE&&Utils.checkDeviceHasNavigationBar(this)) {
//            rootView.setSystemUiVisibility(View.INVISIBLE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//        }
    }

    @Override
    public void onStart(ServiceAction service, Object action) {
        LogUtils.d("onStart");
    }

    @Override
    public void onFinish(ServiceAction service, Object action) {
        LogUtils.d("onFinish");
    }

    // 点击返回按钮
    @Override
    public void onBackPressed() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setScreenOrientation(false);
        } else {
            // 取消回复

            if (!TextUtils.isEmpty(chatRoomFragment.getToUserName())) {
                chatRoomFragment.setToUserName("");
                return;
            }
            if (fMgr.getBackStackEntryCount() <= 1) {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    Toast.makeText(getApplicationContext(), "再按一次退出播放",
                            Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                    return;
                } else {
                    tofinish();
                }
            } else {
                super.onBackPressed();
            }
        }
    }

    private void tofinish() {
//        Utils.sendBroadcastToService(8, this);
        finish();
    }

    @Override
    public void onFangdaClicked() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            fangDalock = false;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            fangDalock = true;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        }
    }

    @Override
    public void onZanClicked() {
        praisVideo(videoId);
    }

    @Override
    public void onShouCangClicked() {
    }

    @Override
    public void onShareClicked() {
        LogUtils.d("liveActivity========mtitle" + mTitle);
        share(mTitle, mTitle, videoId + "_" + mActivityId);

    }

    @Override
    public void onJieMuClicked() {
        LogUtils.d("onJieMuClicked");
//        getMyMediaController().hide();
        if (formDialog != null) {
            formDialog.show();
        } else {
            if (mFormList != null) {
                formDialog = DialogTool.createFormDialog(this, mFormList, mTitle);
                formDialog.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        isDialogShow = false;
//                        rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
                    }
                });
            }
        }
        isDialogShow = true;
    }

    @Override
    public void onNextClicked() {
    }

    @Override
    public void onStartOrPauseClicked(boolean start) {
        tongleAd(!start);
//        if (start) {
////            mLoadingView.setVisibility(View.GONE);
//        }
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent intent) {
        LogUtils.t("PlayActivity---onActivityResult---", arg0 + "," + arg1);
        if (share != null && intent != null) {
            share.setSinaWeibo(arg0, arg1, intent);
        }
        if (intent == null) {
            return;
        }
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        if (action.equals(Constants.ACTION_PAY_RESULT)) {
            dismissDialog();
            int intExtra = intent.getIntExtra(Constants.ACTION_PAY_RESULT, -2);
            if (intExtra == 0) {
                setLiveUrlandPlay(tempAlbumLastVideo);
                tempAlbumLastVideo = null;
                Utils.toast(LiveActivity.this, R.string.pay_success);
            } else
                Utils.toast(LiveActivity.this, R.string.pay_fail);
        }

    }

    @Override
    public void onWutaiClicked(int index) {
//        isFirstSetRate = true;
//        isFirstPlay = true;
        LiveInfo liveInfo = mLiveInfoList.get(index);
        if (liveInfo.getType().equals("3")) {
            Utils.playVRNetworkStream(this, liveInfo.getHls(), liveInfo.getMsg());
        } else {
            setPlayStreamId(liveInfo.getStreamId());
            mActivityId = liveInfo.getActivityId();
        }
    }

    @Override
    public void onVrClicked(int index) {
        LiveInfo liveInfo = mVrInfoList.get(index);
        Utils.playVRNetworkStream(this, liveInfo.getHls(), liveInfo.getMsg());
    }

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
                    if (isPlaying()) {
                        isNeedAutoPlay = true;
                        stopPlayer();
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK: // 摘机（正在通话中）
                    break;
            }
        }

    }


    private TanmuDialog mTanmuDialog;
    private boolean isPlaying;


    @Override
    public void showTanmuDialog() {
        if (mTanmuDialog == null)
            mTanmuDialog = new TanmuDialog(this, new OnSendTanmuListener() {
                @Override
                public void sendMessage(String edtStr) {
                    chatRoomFragment.sendMessage(edtStr);
                }
            });
        mTanmuDialog.show();
    }

    @Override
    public void onTanmuClicked(boolean b) {
        if (b) {
            mBarrageView.setVisibility(View.VISIBLE);
        } else
            mBarrageView.setVisibility(View.GONE);
    }

    @Override
    public void start() {
        if (player != null) {
            player.start();
            isPlaying = true;
            getMyMediaController().updateController();
        }
    }

    @Override
    public void pause() {
        if (player != null) {
            player.pause();
            isPlaying = false;
            getMyMediaController().updateController();
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
        breakTime = 0;
    }

    @Override
    public boolean isPlaying() {
        return isPlaying;
    }

    @Override
    public int getBufferPercentage() {
        // if (mPlayerView != null)
        //
        // return (int) (mPlayerView.getBufferPercentage() / 1000);
        return 0;
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
                    if (definationsMap != null) {
                        mQXDList.clear();
                        Iterator<Map.Entry<Integer, String>> iterator = definationsMap.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry<Integer, String> next = iterator.next();
                            Integer key = next.getKey();// 码率所对于的key值,key值用于切换码率时，方法playedByDefination(type)所对于的值
                            String value = next.getValue();// 码率名字，比如：标清，高清，超清
                            LogUtils.d("Iterator -- key==" + key + "----------value===" + value);
                            mQXDList.add(value);
//                            mRateType.add(value);
                        }
                        getMyMediaController().setQXD(mQXDList);
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
                    mMediaController.show();
                    mMediaController.showBreak();
                    breakTime = 0;
                    return;
                }
                breakTime++;
                LogUtils.d("RankActivity--  +ISplayer.MEDIA_EVENT_BUFFER_START");
                break;
            case ISplayer.MEDIA_EVENT_BUFFER_END:// 缓冲结束
                mMediaController.hideBreak();
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

}

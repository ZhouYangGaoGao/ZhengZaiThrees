package com.modernsky.istv.acitivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
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
import com.modernsky.istv.adapter.AdapterPagerFragment;
import com.modernsky.istv.bean.Ad;
import com.modernsky.istv.bean.LatestVideo;
import com.modernsky.istv.bean.LiveInfo;
import com.modernsky.istv.bean.Pinglun;
import com.modernsky.istv.bean.ResultBean;
import com.modernsky.istv.bean.ResultList;
import com.modernsky.istv.bean.ResultPinglun;
import com.modernsky.istv.bean.UserEntity;
import com.modernsky.istv.bean.VideoPlayInfo;
import com.modernsky.istv.fragment.AnchorDetailFragment;
import com.modernsky.istv.fragment.PlayFragment;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.BitmapTool;
import com.modernsky.istv.tool.DialogTool;
import com.modernsky.istv.tool.DialogTool.DialogLister;
import com.modernsky.istv.tool.NetworkHelper;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.Constants.UserParams;
import com.modernsky.istv.utils.GeneralTool;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.PreferencesUtils;
import com.modernsky.istv.utils.ScreenUtils;
import com.modernsky.istv.utils.Utils;
import com.modernsky.istv.utils.WeakHandler;
import com.modernsky.istv.view.DZZThreeSharePop;
import com.modernsky.istv.view.ShowNewMediacontroler;
import com.modernsky.istv.window.PeopleInfoDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * 播放界面
 *
 * @author zxm
 */
@SuppressLint({"NewApi", "Override", "InflateParams"})
public class LookForwardActivity extends BaseActivity implements OnPlayStateListener {
    // 乐视
    private RelativeLayout mPlayerLayoutView;
    //    private VODPlayCenter mPlayerView;
    String uu = "53f80d6851";
    String vu = "6c31fb9cca";
    //    private Set<String> mRateType;// 播放的码率（清晰度集合）
    private boolean isBackgroud = false;
    private View rootView;
    private String albumId, videoId;
    private String userId = "";
    private int userStatus;
    private String stringType = "";
//    private long playTime;
    private long nowTime = 0;
    private float mScrollX;
    private String objectId = "";
    //    private boolean ifHasShoucang = false;
    private boolean ifHasPrised = false;
    private boolean isDialogShow = false;
    public String mTitle = "";
    private View mVolumeBrightnessLayout;
    private ImageView mOperationBg;
    private ImageView mOperationPercent;
    private AudioManager mAudioManager;
    private String singerId;
    // 卡顿次数，当卡顿发生两次的时候需要切换清晰度。
//    private int breakTime = 0;
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
    //    private MediaController mMediaController;
    private ShowNewMediacontroler mMediaController;
    private View mLoadingView;
    private ImageView mLoadingImg;
    //    LinearLayout ll_descrption;
    private long totalTime;

    //    private PlayFragment playFragment;
    private View rl_ad;
    private ImageView img_ad;
    private BitmapUtils bitmapTool;

    private String adUrl;
    private List<Ad> mAds = new ArrayList<Ad>();
    private boolean isLive;
    private LatestVideo tempAlbumLastVideo;
    // 上传评论图片临时文件
    private File tempFile;
    private String uri = "";
    private boolean isPlaying;
    int adCount;
    private int index;
    private int windowWidth;
    private int windowHeight;
    private String mCollectId;
    private int mIndex;
    private List<String> mQXDList;
    private AnimationDrawable anim;
    private long duration;
    private long mSeekToPos;
    private boolean isSeekMode;
    RelativeLayout.LayoutParams relativeMatchLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

    public final static String DATA = "data";
    //////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////
    private long seektoAftercomplate;
    private ISplayer player;
    private String path = "";
    private PlayContext playContext;
    private ILeVideoView videoView;
    private Bundle mBundle;
    private long lastposition;
    public SurfaceHolder mSurfaceHolder;
    Map<Integer, String> definationsMap;
//    RankFragment rankFragment;


    Handler controllerHandler;

    // surfaceView的生命周期

    private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            /**
             * surfaceview 销毁的时候销毁播放器
             */
            stopAndRelease();
            LogUtils.d("SplashActivity------surfaceDestroyed");
        }

        @Override
        public void surfaceCreated(final SurfaceHolder holder) {
            mSurfaceHolder = holder;
            /**
             * 创建播放器
             */
            LogUtils.d("SplashActivity------surfaceCreated");
            videoView.setLayoutParams(relativeMatchLayoutParams);
            createOnePlayer(holder.getSurface());
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            LogUtils.d("SplashActivity------surfaceChanged");
            if (player != null) {
                /**
                 * surfaceView 宽高改变的时候，需要通知player
                 */
                PlayerParamsHelper.setViewSizeChange(player, width, height);
            }
        }
    };
    private boolean isComplete;

    private PlayFragment playFragment;
    AnchorDetailFragment anchorDetailFragment;
    private UserEntity anchorChatroomInfo;


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

    private static FragmentManager fMgr;

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
        tongleAd(false);
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
        }
        isBackgroud = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (playContext != null) {
            playContext.destory();
        }
//        getMyMediaController().getmmHandler.removeMessages(SHOW_PROGRESS);
        isBackgroud = true;
        if (controllerHandler != null) {
            controllerHandler.removeMessages(2);//去掉  mediacontroller的更新界面
        }
//        Utils.sendBroadcastToService(8, this);
    }

    /**
     * 初始化PlayContext PlayContext作为播放器的上下文而存在，既保存了播放器运行时的临时数据，也记录播放器所需要的环境变量。
     */
    private void initPlayContext() {
        LogUtils.d("RankActivity--" + "initPlayContext");
        playContext = new PlayContext(this);
        // 当前视频渲染所使用的View，可能是Surfaceview(glSurfaceView也属于Surfaceview
        // ),也可能是textureView
        playContext.setVideoContentView(videoView.getMysef());
    }

    private void initVideoView() {
        /**
         * 创建视频显示View，ILeVideoView是我们定义的基础view用于显示视频，当前如果用户希望自己的view
         */
        LogUtils.d("RankActivity--" + "initVideoView");
        videoView = (ILeVideoView) findViewById(R.id.sf);
        videoView.getHolder().addCallback(surfaceCallback);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.VODplayerLayout:
                if (getMyMediaController().isShowing()) {
                    getMyMediaController().hide();
                } else {
                    getMyMediaController().show();
                }
                LogUtils.d("VODplayerLayout");
                break;
            case R.id.image_cancel_ad:
                tongleAd(false);
                break;
            case R.id.image_ad:
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

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void setContentView(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_lookforward);
        // 初始化乐视播放器
        rootView = findViewById(R.id.rootView);
        include_video = findViewById(R.id.include_video);

        initFullLayoutParams();
        initVODPlayer();
        rl_ad = findViewById(R.id.rl_ad);
        img_ad = (ImageView) findViewById(R.id.image_ad);
        img_ad.setOnClickListener(this);
        findViewById(R.id.image_cancel_ad).setOnClickListener(this);
        bitmapTool = BitmapTool.getInstance().initAdapterUitl(LookForwardActivity.this);
        reSetWindowSize();
        initView();
        // 获取电话通讯服务
        TelephonyManager tpm = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);
        // 创建一个监听对象，监听电话状态改变事件
        tpm.listen(new MyPhoneStateListener(),
                PhoneStateListener.LISTEN_CALL_STATE);
        if (!UserService.getInatance().isNeedLogin(this)) {
            UserEntity bean = UserService.getInatance().getUserBean(this);
            userId = bean.getId();
        }
        fMgr = getSupportFragmentManager();
//        initViewPager();
//        initFragment("56d960bde4b06c3f2c92d01b");
//        initFragment();
    }

    private AnchorDetailFragment initanchorFragment() {
        Bundle bundle=new Bundle();
        bundle.putBoolean("isLive",false);
        AnchorDetailFragment fragment=new AnchorDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
    private PlayFragment initplayFragment() {
        Bundle bundle=new Bundle();
        bundle.putString("videoId",videoId);
        bundle.putBoolean("iswrs",true);
        PlayFragment fragment=new PlayFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
   private  ViewPager viewPager;
    @SuppressWarnings("ConstantConditions")
    private void initViewPager() {
        //
         viewPager = (ViewPager) findViewById(R.id.viewpager);
        AdapterPagerFragment adapter = new AdapterPagerFragment(getSupportFragmentManager());
//        rankFragment=new RankFragment("",false);
        anchorDetailFragment = (anchorDetailFragment == null) ? initanchorFragment() : anchorDetailFragment;
        playFragment = (playFragment == null) ? initplayFragment() : playFragment;

        adapter.addFragment(anchorDetailFragment, "选手详情");
        adapter.addFragment(playFragment, getResources().getString(R.string.pinglun));
        viewPager.setAdapter(adapter);
        //
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        // google的bug，2为滑动完毕，0为初始化，不一样的
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout) {
            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == 2) {
                    state = 0;
                }
                super.onPageScrollStateChanged(state);
            }
        });
         viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
             @Override
             public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

             }

             @Override
             public void onPageSelected(int position) {
                 if (position == 0) {

                 } else {

                 }
             }

             @Override
             public void onPageScrollStateChanged(int state) {

             }
         });
    }

    public void setViewpager(int num) {
        if (viewPager==null) {
            return;
        }
        if (num == 0) {
            viewPager.setCurrentItem(0);
        } else {
            viewPager.setCurrentItem(1);
        }
    }
    private void initFullLayoutParams() {
        relativeMatchLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        relativeMatchLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        relativeMatchLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        relativeMatchLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        relativeMatchLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
    }

    //    PopThreeShare share;
    DZZThreeSharePop share;
    private float mScrollY;
    private float moveY;
    //    private boolean isFirstSetRate = true;
    Dialog loginDialog;


    // 乐视播放器
    private void initVODPlayer() {
        mQXDList = new ArrayList<String>();
        this.mPlayerLayoutView = (RelativeLayout) findViewById(R.id.VODplayerLayout);
        initPlayView();

    }

    private void reSetWindowSize() {
        Display disp = getWindowManager().getDefaultDisplay();
        windowWidth = disp.getWidth();
        windowHeight = disp.getHeight();
    }

    @Override
    public void findViewById() {
        Intent intent = getIntent();
        setStringType(intent.getStringExtra(Constants.TYPE));
        singerId = intent.getStringExtra("singerId");
        LogUtils.d("singerId==" + singerId);
        getAnchorInfoEntity(singerId);
        LogUtils.d("stringExtra" + getStringType());
        if (Constants.VIDEO_NAME.equals(getStringType())) {
            mTitle = intent.getStringExtra(Constants.VIDEO_NAME);
            videoId = intent.getStringExtra(Constants.VIDEO_ID);
            // objectId = intent.getStringExtra(Constants.OBJECT_ID);
            albumId = intent.getStringExtra(Constants.ALBUM_ID);
            LogUtils.d("VIDEO_NAMEalbumId==" + albumId);
            playVideo(videoId);
            getIfZanAndIfShoucang(videoId);
        } else if (Constants.ALBUM_NAME.equals(getStringType())) {
            mTitle = intent.getStringExtra(Constants.ALBUM_NAME);
            albumId = intent.getStringExtra(Constants.ALBUM_ID);
            LogUtils.d("VIDEO_NAMEalbumId==" + albumId);
            // playAlbum(albumId);
        } else if (Constants.ARTIST_ALL_VIDEO.equals(getStringType())) {
            mCollectId = intent.getStringExtra(Constants.COLLECT_ID);
            mIndex = intent.getIntExtra(Constants.INDEX, 0);
            albumId = intent.getStringExtra(Constants.ALBUM_ID);
            // objectId = intent.getStringExtra(Constants.OBJECT_ID);
            LogUtils.d("ARTIST_ALL_VIDEOalbumId==" + albumId);
        } else if (Constants.TYPE_ARTIST.equals(getStringType())) {
            mTitle = intent.getStringExtra(Constants.VIDEO_NAME);
            albumId = intent.getStringExtra(Constants.ALBUM_ID);
            objectId = intent.getStringExtra(Constants.OBJECT_ID);
            LogUtils.d("albumId===" + albumId + "objectId==" + objectId);
        }
        full(false);
        initViewPager();

        mPlayerLayoutView.postDelayed(new Runnable() {

            @Override
            public void run() {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            }
        }, 1000);
    }

    public void playVideoByUrl(final String videoId, String foreignUnique, String videoName) {
        if (!TextUtils.isEmpty(videoName)) {
            mTitle = videoName;
        }
        tongleAd(false);
        playFragment.setVideoId(videoId);
        playFragment.refresh();
        this.videoId = videoId;
        //
        vu = foreignUnique;
        if (NetworkHelper.isNetworkConnected(this)
                && !NetworkHelper.isWifiConnected(this)
                && !PreferencesUtils.getBooleanPreferences(this,
                PreferencesUtils.TYPE_NET_TOGLE)) {
            DialogTool.createNetWorkDialog(this, new DialogLister() {
                @Override
                public void onCountinue() {
                    setPlayUrl(videoId);
                }

                @Override
                public void onCancelListener() {
                    finish();
                }
            }).show();
        } else {
            setPlayUrl(videoId);
        }
    }

    /**
     * 播放，初始话播放控件
     *
     * @param videoId
     */
    private void setPlayUrl(String videoId) {
//        getMyMediaController().setVideoShowName(mTitle);
//        if (playFragment != null)
//            getPraiseCount(videoId, "0");
        mBundle = LetvParamsUtils.setVodParams(uu, vu, "", "", "");
        playvideo();
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
        addPlayRecord("0");
    }

    /**
     * 添加播放统计
     *
     * @param videoId
     */
    private void addPlayToServices(String videoId) {
        RequestParams params = UrlTool.getPostParams("recordVideoId", videoId);
        SendActtionTool.post(Constants.URL_RECORD_SERVLET, null, CommentAction.Action_addPlayToServices, this, params);
    }

    private void initPlayView() {
        LogUtils.d("initPlayView");
        initVideoView();// 初始化videoView
        initPlayContext();// 初始化playContext

    }
    /**
     * 添加播放记录
     */
    private void addPlayRecord(String playTime) {
        RequestParams params = UrlTool.getParams(Constants.USER_ID, userId,
                Constants.VIDEO_ID, videoId, Constants.SOURCE,
                Constants.MOBILE, Constants.PLAY_TIME, playTime);

        SendActtionTool.get(UserParams.URL_ADD_PLAY_RECORD,
                ServiceAction.Action_Comment,
                CommentAction.Action_addPlayRecord, this, params);

    }


    // 播放指定视频
    public void playVideo(String videoId) {
        this.videoId = videoId;
        UserEntity bean = UserService.getInatance().getUserBean(this);
        if (bean != null) {
            userId = bean.getId();
        }
        RequestParams params = UrlTool.getParams(Constants.VIDEO_ID, videoId,
                Constants.USER_ID, userId, Constants.FILTER,
                Constants.FILTER_VIDEO_PLAYER);
        if (!TextUtils.isEmpty(videoId)) {
            SendActtionTool.get(Constants.URL_VIDEO_DETAIL,
                    ServiceAction.Action_Comment,
                    CommentAction.Action_GetVideo_By_Id, this, params);
        }
    }


    // 广告开关
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
     *
     * @return 按返回键的时候先判断广告
     */
    private boolean  ifGonextWhenCheckAdafterBacePressed() {
        if (adCount <= 0) {
            return true;
        }
        boolean b = rl_ad.getVisibility() != View.VISIBLE;
        if (!b) {
            rl_ad.setVisibility(View.GONE);
        }
        return b;
    }

    // ~~~ 绑定控件
    private void initView() {
        mVolumeBrightnessLayout = findViewById(R.id.operation_volume_brightness);
        mOperationBg = (ImageView) findViewById(R.id.operation_bg);
        mOperationPercent = (ImageView) findViewById(R.id.operation_percent);
        mLoadingView = findViewById(R.id.video_loading);
        mLoadingImg = (ImageView) findViewById(R.id.img_loading);
        anim = (AnimationDrawable) mLoadingImg.getBackground();
        // ~~~ 绑定数据
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        // 设置显示名称
        getMyMediaController().setListenner(controllerListenner);
        getMyMediaController().setMode(false);
        getMyMediaController().show();
        getMyMediaController().full(false);
        controllerHandler = getMyMediaController().getHandler();
        getGestureDetector();
        addAd();
        Utils.showLoadingAnim(mLoadingView, anim);
    }

    public GestureDetector getGestureDetector() {
        if (mGestureDetector == null) {
            mGestureDetector = new GestureDetector(this,
                    new MyGestureListener());
        }
        return mGestureDetector;
    }

    private ShowNewMediacontroler getMyMediaController() {
        if (mMediaController == null) {
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mMediaController = (ShowNewMediacontroler) findViewById(R.id.controller_lookforward);
        }
        return mMediaController;
    }

    AlertDialog reportDialog;
    String[] reportDatas = {"色情低俗", "政治敏感", "暴力恐怖", "其他"};

    private void initReportDialog() {
        if (reportDialog == null) {

            reportDialog = new AlertDialog.Builder(this).setTitle("举报的选项有").setItems(reportDatas, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    reportAnchor(reportDatas[which]);
//                    Toast.makeText(LookForwardActivity.this, "举报的内容是：" + reportDatas[which], Toast.LENGTH_SHORT).show();
                }
            }).create();
        }
        reportDialog.show();
    }

    /**
     * 举报某主播
     *
     * @param
     */
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
        SendActtionTool.get(UserParams.URL_USER_REPORT, ServiceAction.Action_Comment, CommentAction.Action_Report, LookForwardActivity.this, params);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector != null && mGestureDetector.onTouchEvent(event)) {
            return true;
        }
        float moveX;
        // 处理手势结束
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mScrollX = event.getX();
                mScrollY = event.getY();
                if (player != null) {
                    nowTime = player.getCurrentPosition();
                    totalTime = player.getDuration();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = event.getX() - mScrollX;
                float f2 = moveX / mPlayerLayoutView.getWidth();
                if (Math.abs(f2) > 0.1f && totalTime > 0) {
//                    long seekToTime = (long) (nowTime + f2 * totalTime * 0.2f);
//                    seekToPos(seekToTime);
                    getMyMediaController().setProgress();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                moveX = event.getX() - mScrollX;
                float f3 = moveX / mPlayerLayoutView.getWidth();
                if (Math.abs(f3) > 0.1f && totalTime > 0) {
                    long seekToTime = (long) (nowTime + f3 * totalTime * 0.2f);
                    seekToPos(seekToTime);
                    getMyMediaController().setProgress();
                }
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


    // 定时隐藏
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
    private View include_video;
    private Dialog formDialog;

    private class MyGestureListener extends SimpleOnGestureListener {
        /**
         * 双击
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (player != null) {
                if (controllerListenner.isPlaying()) {
                    tongleAd(true);
                    player.pause();
                    isPlaying = false;
                } else {
                    tongleAd(false);
                    player.start();
                    isPlaying = true;
                }
            }
            LogUtils.d("onDoubleTap");
            return true;
        }

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
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
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
                    return false;
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

    // 滑动改变亮度
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
            LogUtils.d("qwwqqwqwonConfigurationChanged横屏+fangDalock---" + fangDalock + "---isLocked" + isLocked);
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
            LogUtils.d("qwwqqwqwonConfigurationChanged竖屏+fangDalock---" + fangDalock + "---isLocked" + isLocked);
            full(false);
//            mPlayerView.changeOrientation(Configuration.ORIENTATION_PORTRAIT);
            if (!isLocked) {
                mDismissHandler.removeMessages(1);
                mDismissHandler.sendEmptyMessageDelayed(1, 2000);
            }
        }
    }

    /**
     * 获取是否点赞和是否收藏
     */
    private void getIfZanAndIfShoucang(String videoId) {
        if (UserService.getInatance().isNeedLogin(this)) {
            ifHasPrised = false;
            getMyMediaController().getButtomPlayZanImg()
//                    .setImageResource(R.drawable.but_zan_bac);
                    .setImageResource(R.drawable.icon_07bofang_good_hl);
        } else {
            LogUtils.d("2222222222");
            RequestParams params = UrlTool.getParams(Constants.VIDEO_ID,
                    videoId, Constants.USER_ID, userId);
            SendActtionTool.get(UserParams.URL_USER_IfHasZanAndShoucang,
                    ServiceAction.Action_Comment,
                    CommentAction.Action_getPraiseAndShoucang, this, params);
        }
    }

    /**
     * 全屏模式开关
     *
     * @param enable
     */
    private void full(boolean enable) {
        reSetWindowSize();
        LayoutParams layoutParams = include_video.getLayoutParams();

        if (enable) {
            Utils.setFullScreen(this);
            // rl_video_info.setVisibility(View.GONE);
//            ll_descrption.setVisibility(View.GONE);
//            rootView.setSystemUiVisibility();
//            if (Utils.checkDeviceHasNavigationBar(this)){
//                LogUtils.d("checkDeviceHasNavigationBar");
//                rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//            }
        } else {
            Utils.cancelFullScreen(this);
            // rl_video_info.setVisibility(View.GONE);
//            ll_descrption.setVisibility(View.VISIBLE);
//            if (Utils.checkDeviceHasNavigationBar(this))
//                rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
        if (enable) {
//            layoutParams.height = getWindow().getAttributes().height;
            layoutParams.height = LayoutParams.MATCH_PARENT;
            layoutParams.width = LayoutParams.MATCH_PARENT;
//            layoutParams.height = ScreenUtils.getScreenHeight(this);
            include_video.setLayoutParams(layoutParams);
        } else {
            getMyMediaController().setPaddingRelative(0, 0, 0, 0);
            layoutParams.height = ScreenUtils.getScreenHeight(this) * 3 / 10;
            layoutParams.width = LayoutParams.MATCH_PARENT;
            include_video.setLayoutParams(layoutParams);
        }
        if (videoView != null)
            videoView.setLayoutParams(relativeMatchLayoutParams);
    }

    private void stopPlayer() {
        if (player != null)
            player.pause();
    }

    private void startPlayer() {
        if (player != null)
            player.start();
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        super.onSuccess(service, action, value);
        String jsonString = value.toString();
        switch ((CommentAction) action) {
            case Action_GetVideo_By_Id:
                try {
                    ResultBean<LatestVideo> resultVideoBean = JSON
                            .parseObject(
                                    jsonString,
                                    new TypeReference<ResultBean<LatestVideo>>() {
                                    });
                    LatestVideo albumLastVideo = resultVideoBean.data;
                    albumId = String.valueOf(albumLastVideo.getAlbumId());

                    if (!TextUtils.isEmpty(albumLastVideo.getName())) {
                        mTitle = albumLastVideo.getName();
                        getMyMediaController().getCharRoomName().setText(mTitle);
                        getMyMediaController().getLookforwadVideoName().setText(mTitle);
                    }
                        anchorDetailFragment.updateVideoInfo(albumLastVideo.getShowTime(), albumLastVideo.getLocation(), albumLastVideo.getDescription());
                    checkLiveAndPlay(albumLastVideo);
//                    if (rankFragment.isAdded())
//                        rankFragment.setmChatroomId(albumLastVideo.getChatroomId());
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.e("javaBean 转型失败 " + e.toString());
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
            case ACTION_GETANCHORINFO:
                try {
                    anchorChatroomInfo = JSON.parseObject(((JSONObject) value).getString("userEntity"), UserEntity.class);
                    LogUtils.d("anchorChatroomInfo--------");
                    initAnchorInfos(anchorChatroomInfo);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LogUtils.d("ACTION_GETANCHORINFO-----");
                break;
            case Action_Report:
                Utils.toast(this, "举报成功");
                reportDialog.cancel();
                break;

            case Action_getPraiseAndShoucang:
                LogUtils.d("Action_getPraiseAndShoucang value=" + value.toString());
                initGetPrisedAndShouCang((JSONObject) value);
                break;

            case Action_addPraiseResouce:
                try {
                    ResultPinglun<Pinglun> resultPinglun = JSON.parseObject(
                            jsonString,
                            new TypeReference<ResultPinglun<Pinglun>>() {
                            });
                    if (resultPinglun.getStatus() == 1) {
                        Utils.toast(this, R.string.dianzan_success);
                        getMyMediaController().getButtomPlayZanImg()
                                .setImageResource(R.drawable.but_zanhl);
//                                .setImageResource(R.drawable.icon_07bofang_good);
                        // setPriseCount(resultPinglun.getCount());
                    } else {
                        Utils.toast(this, resultPinglun.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
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
                getMyMediaController().getButtomPlayZanImg()
                        .setImageResource(R.drawable.icon_07bofang_good_hl);
//                        .setImageResource(R.drawable.but);
            } else if (isPraise.equals("1")) {
                getMyMediaController().getButtomPlayZanImg()
                        .setImageResource(R.drawable.but_zanhl);
//                        .setImageResource(R.drawable.icon_07bofang_good);
            }
//            if (isCollection.equals("0")) {
//                getMyMediaController().mShouCangButton
//                        .setImageResource(R.drawable.icon_07bofang_shoucang);
//                ifHasShoucang = false;
//            } else if (isCollection.equals("1")) {
//                ifHasShoucang = true;
//                getMyMediaController().mShouCangButton
//                        .setImageResource(R.drawable.icon_07bofang_shoucang_hl);
//            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 更新主播详情页信息
     *
     * @param anchorChatroomInfo
     */
    private void initAnchorInfos(UserEntity anchorChatroomInfo) {
        getMyMediaController().initAnchorEntity(anchorChatroomInfo);
        anchorDetailFragment.updateUserEnty(anchorChatroomInfo);
    }

    //获取主播的信息
    private void getAnchorInfoEntity(String singerId) {
        SendActtionTool.post(UserParams.URL_GET_ONE,
                null, CommentAction.ACTION_GETANCHORINFO,
                this, UrlTool.getParams(Constants.USER_ID, singerId));
    }

    private void checkLiveAndPlay(LatestVideo albumLastVideo) {
        if (albumLastVideo.getIsNeedPay() == 1
                && albumLastVideo.getIsPay() == 0) {
            DialogTool.createPayDialog(this, albumId);
            LogUtils.t(
                    "albumLastVideo.getIsNeedPay() == 1&& albumLastVideo.getIsPay() == 0",
                    "return");
            tempAlbumLastVideo = albumLastVideo;
            return;
        }
        setLiveUrlandPlay(albumLastVideo);
    }

    private void setLiveUrlandPlay(LatestVideo albumLastVideo) {
        String foreignUnique;
        VideoPlayInfo videoPlayInfo = albumLastVideo.getVideoPlayInfo();
        foreignUnique = videoPlayInfo.getForeignUnique();
        if (albumLastVideo.getVideoType() == 4) {
            List<LiveInfo> liveInfo2 = albumLastVideo.getLiveInfo();
            if (liveInfo2 != null && liveInfo2.size() > 1) {
                mLiveInfoList.clear();
                mLiveInfoList.addAll(liveInfo2);
                getMyMediaController().setHasWuTai(true);
//                getMyMediaController().showWutai(true);
            } else {
                mLiveInfoList = null;
                getMyMediaController().setHasWuTai(false);
//                getMyMediaController().showWutai(false);
            }
            if (liveInfo2!=null) {
                LiveInfo liveInfo = liveInfo2.get(0);
                LogUtils.d("albumLastVideo.getVedioType() == 4");
                if (liveInfo != null) {
                    // String livePath =
                    // "http://img1.peiyinxiu.com/2015020312092f84a6085b34dc7c.mp4";
                    String livePath = liveInfo.getHls();
                    if (TextUtils.isEmpty(livePath)) {
                        livePath = liveInfo.getRtmp();
                    }
                    if (!TextUtils.isEmpty(livePath)) {
                        foreignUnique = livePath;
                    }
                    // mTitle=liveInfo.getMsg();
                }
            }
//            mMediaController.setLiveMode(true);
            isLive = true;
            Utils.toast(this, "该直播已结束,请观看其他视频");
            finish();
        } else {
            mMediaController.full(false);
//            mMediaController.setVerticalMode(2);
            isLive = false;
        }
//        standardPic = albumLastVideo.getStandardPic();
        playVideoByUrl(videoPlayInfo.getVideoId() + "", foreignUnique,
                albumLastVideo.getName());
    }

    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {
        super.onFaile(service, action, value);
        if ((action != CommentAction.Action_addPlayRecord)
                && (action != CommentAction.Action_getVideoAd)) {
            Utils.toast(this, value.toString());
        }
        if (action == CommentAction.Action_GetVideo_By_Id) {
            Utils.toast(this, "找不到视频资源！");
            finish();
        }
    }

    @Override
    public void onException(ServiceAction service, Object action, Object value) {
        super.onException(service, action, value);
    }

    /**
     * 是否需要自动恢复播放，用于自动暂停，恢复播放
     */
    private boolean needResume;
    private long exitTime;
    private boolean isLocked;
    private int mQXDIndex = -1;
    private boolean fangDalock;


    private void setScreenOrientation(boolean toLandScape) {
        int screenOrientationPortrait = toLandScape ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setRequestedOrientation(screenOrientationPortrait);
        mDismissHandler.removeMessages(1);
        mDismissHandler.sendEmptyMessageDelayed(1, 2000);
    }


    @Override
    public void onStart(ServiceAction service, Object action) {
    }

    @Override
    public void onFinish(ServiceAction service, Object action) {

    }

    // 点击返回按钮
    @Override
    public void onBackPressed() {


        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setScreenOrientation(false);
            return;
        }
        if (!ifGonextWhenCheckAdafterBacePressed()) {
            return;
        }
        if (playFragment.isVisibleToUser() && !TextUtils.isEmpty(playFragment.getToUserId())) {
            playFragment.backClean();
            return;
        }
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出播放", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
            return;
        }
        finish();
    }


    @Override
    protected void onActivityResult(int arg0, int arg1, Intent intent) {
        LogUtils.t("PlayActivity---onActivityResult---", arg0 + "," + arg1);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//         Utils.setFullScreen(this);
        if (intent != null && share != null) {
            share.setSinaWeibo(arg0, arg1, intent);
        }
        // setReusult(arg0, arg1, intent);
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
                Utils.toast(LookForwardActivity.this, R.string.pay_success);
            } else
                Utils.toast(LookForwardActivity.this, R.string.pay_fail);
        }

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
                    if (controllerListenner.isPlaying()) {
                        isNeedAutoPlay = true;
                        stopPlayer();
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK: // 摘机（正在通话中）
                    break;
            }
        }

    }


//    /**
//     * 获取是否点赞和是否收藏
//     */
//    private void getIfZanAndIfShoucang(String videoId) {
//        if (UserService.getInatance().isNeedLogin(this)) {
//            ifHasPrised = false;
////            ifHasShoucang = false;
////            getMyMediaController().mShouCangButton
////                    .setImageResource(R.drawable.icon_07bofang_shoucang);
//            getMyMediaController().getButtomPlayZanImg()
//                    .setImageResource(R.drawable.but_zan);
//        } else {
//            LogUtils.d("2222222222");
//            RequestParams params = UrlTool.getParams(Constants.VIDEO_ID,
//                    videoId, Constants.USER_ID, userId);
//            SendActtionTool.get(UserParams.URL_USER_IfHasZanAndShoucang,
//                    ServiceAction.Action_Comment,
//                    CommentAction.Action_getPraiseAndShoucang, this, params);
//        }
//    }


    /**
     * 播放到指定时间
     *
     * @param time
     */
    private void seekToPos(long time) {
        if (isLive) {
            return;
        }
//        Utils.showLoadingAnim(mLoadingView, anim);
        isSeekMode = true;
        mSeekToPos = time;
        if (isComplete) {
            setPlayUrl(videoId);
            seektoAftercomplate = time;
            isComplete = false;
            return;
        }

        LogUtils.d("seekTo=" + time + " ---mVideoView.getDuration())="
                + player.getDuration());
        long toTime;
        if (time <= 0) {
            toTime = 0;
        } else if (time >= player.getDuration()) {
            toTime = player.getDuration();
        } else {
            toTime = time;
        }
        player.seekTo(toTime);
        // 重新考虑网络卡顿情况
//        breakTime = 0;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }


    /**
     * 判断用户状态，如果未登录，跳转登录界面
     */
    public boolean isUesrLogined() {
        if (TextUtils.isEmpty(userId)) {
            if (UserService.getInatance().isNeedLogin(this)) {
                if (loginDialog != null)
                    loginDialog.show();
                else
                    loginDialog = DialogTool.createToLoginDialog(this);
//                startActivity(new Intent(this, LoginActivity.class));
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

    /**
     * 分享
     */
    private void share(String tille) {
        if (share == null) {
            share = new DZZThreeSharePop(LookForwardActivity.this);
        }
        if (anchorChatroomInfo == null) {
            Utils.toast(this, "未获取选手信息");
            return;
        }
        String type = Utils.getRandomContent(this, R.array.shareContent_show);
        if (anchorChatroomInfo != null) {
            share.setShareInfo("正在现场", type, anchorChatroomInfo.getFaceUrl(), videoId + "_" + vu,vu);
            share.showBelowView(getMyMediaController().getmShareBtn());
        }


    }

    public String getStringType() {
        return stringType;
    }

    public void setStringType(String stringType) {
        this.stringType = stringType;
    }

    //对视频资源点赞
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

    private void addAd() {
        SendActtionTool.get(Constants.URL_GET_VIDEO_AD, ServiceAction.Action_Comment, CommentAction.Action_getVideoAd, this);
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
                    if (definationsMap != null ) {
                        mQXDList.clear();
                        Iterator<Map.Entry<Integer, String>> iterator = definationsMap.entrySet().iterator();
                        while ( iterator.hasNext()) {
                            Map.Entry<Integer, String> next = iterator.next();
                            Integer key = next.getKey();// 码率所对于的key值,key值用于切换码率时，方法playedByDefination(type)所对于的值
                            String value = next.getValue();// 码率名字，比如：标清，高清，超清
                            LogUtils.d("Iterator -- key==" + key + "----------value===" + value);
                            mQXDList.add(value);
//                            mRateType.add(value);
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
                    if (seektoAftercomplate>1) {
                        seekToPos(seektoAftercomplate);
                        seektoAftercomplate=0;
                    }

                }
                break;

            case ISplayer.MEDIA_EVENT_FIRST_RENDER:// 视频第一帧数据绘制
                LogUtils.d("RankActivity--  +ISplayer.MEDIA_EVENT_FIRST_RENDER");
                isPlaying = true;
                Utils.cancaleLoadingAnim(mLoadingView, anim);
                break;
            case ISplayer.MEDIA_EVENT_PLAY_COMPLETE:// 视频播放完成
                isPlaying = false;
                isComplete = true;
                getMyMediaController().hide();
                LogUtils.d("RankActivity--  +ISplayer.MEDIA_EVENT_PLAY_COMPLETE");
                break;
            case ISplayer.MEDIA_EVENT_BUFFER_START:// 开始缓冲
                Utils.showLoadingAnim(mLoadingView, anim);
//                if (breakTime == 2) {
//                    mMediaController.show();
//                    mMediaController.showBreak();
//                    breakTime = 0;
//                    return;
//                }
//                breakTime++;
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

    public PeopleInfoDialog peopleInfoDialog;

    public void initPeopleInfoDialog(String userId) {
        if (peopleInfoDialog == null) {
            peopleInfoDialog = new PeopleInfoDialog(this, userId);
        }
        peopleInfoDialog.show();
    }

    ShowNewMediacontroler.OnShowNewMediacontrollerListener controllerListenner = new ShowNewMediacontroler.OnShowNewMediacontrollerListener() {
        @Override
        public void onReportClicked() {
            initReportDialog();
//            Utils.toast(LookForwardActivity.this, "点击了反馈按钮");
        }

        @Override
        public void onLockClicked(boolean isLock) {
            isLocked = isLock;
            if (!isLocked) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                return;
            }
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }

        @Override
        public void onChangeScreenClicked() {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // LogUtils.d("onFangdaClicked1111");
                fangDalock = false;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                fangDalock = true;
                // LogUtils.d("onFangdaClicked22222");
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }

        @Override
        public void onSharedClicked() {
            LogUtils.d("mtitle=" + mTitle);
            share(mTitle);
        }

        @Override
        public void onChatClicked(String hint, String tousername) {

        }


        @Override
        public void onZanClicked() {
            if (UserService.getInatance().isNeedLogin(LookForwardActivity.this)) {
                DialogTool.createToLoginDialog(LookForwardActivity.this);
            } else {
                praisVideo(videoId);
            }
        }

        @Override
        public void onWuTaiClicked(int pozition) {

        }

        @Override
        public void onQXDClicked(int position) {
            if (index != mQXDIndex && player != null && mQXDList != null
                    && mQXDList.size() > 0) {
                player.playedByDefination(Utils.getKey(definationsMap, mQXDList.get(index)));
//            mPlayerView.setDefinition(mQXDList.get(index));
                mQXDIndex = index;
            }
//            breakTime = 0;
        }

        @Override
        public void onGiftBtnClicked() {

        }

        @Override
        public void onJiemudanClicked() {

        }

        @Override
        public boolean isPlaying() {
            return isPlaying;
        }

        @Override
        public void onPlayClicked(boolean playOrstop) {
            if (playOrstop) {
                if (isComplete)
                    seekTo(0);
                else if (player != null)
                    player.start();
                isPlaying = true;
            } else {
                if (player != null)
                    player.pause();
                isPlaying = false;
            }
            tongleAd(!playOrstop);
            getMyMediaController().updateController();
        }

        @Override
        public void onMessageImgClicked(String videoId) {

        }


        @Override
        public void onAnchorImgClicked(boolean isAnchor, String pozition) {
            if (UserService.getInatance().isNeedLogin(LookForwardActivity.this)) {
                DialogTool.createToLoginDialog(LookForwardActivity.this);
            } else {
                if (anchorChatroomInfo != null) {
                    if (!UserService.getInatance().getUserBean(LookForwardActivity.this).getId().equals(anchorChatroomInfo.getId())) {
                        initPeopleInfoDialog(anchorChatroomInfo.getId());
                    }
                }
            }
        }

        @Override
        public long getDuration() {
            if (player != null) {
//                LogUtils.d("getTotalDuration:" + player.getDuration());
                if (player.getDuration() > 0)
                    duration = player.getDuration();
                return duration;
            }
            return 0;
        }

        @Override
        public long getCurrentPosition() {
            if (player != null) {
                return player.getCurrentPosition();
            }
            return 0;
        }

        @Override
        public void seekTo(long pos) {
//        Utils.showLoadingAnim(mLoadingView, anim);
            isSeekMode = true;
            mSeekToPos = pos;
            if (player != null) {
                if (isComplete) {
                    setPlayUrl(videoId);
                    isComplete = false;
                    seektoAftercomplate = pos;
                    return;
                }
                    player.seekTo(pos);
            }
            // 重新考虑网络卡顿情况
//            breakTime = 0;
        }

        @Override
        public void showGiftGuidDialog() {

        }

        @Override
        public int getBufferPercentage() {
            if (player != null)
                return (int) (player.getBufferPercentage() / 1000);
            return 0;
        }

        @Override
        public void onBackClicked() {
            LogUtils.d("onBackClicked--");
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setScreenOrientation(false);
            } else {
                GeneralTool.KeyBoardCancle(LookForwardActivity.this);
                finish();
            }
        }

        @Override
        public void onSendGift(boolean showDialog, int giftNum) {

        }

        @Override
        public void castMbBuyGift(int count, int a) {

        }

        @Override
        public void onInitMbDialog() {

        }
    };

}


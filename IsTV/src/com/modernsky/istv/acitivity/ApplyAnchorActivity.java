package com.modernsky.istv.acitivity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
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
import com.lidroid.xutils.http.RequestParams;
import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.action.CommentAction;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.bean.AnchorInfoBean;
import com.modernsky.istv.bean.ApplyAnchorInfo;
import com.modernsky.istv.bean.ApplyStateInfo;
import com.modernsky.istv.bean.ApplyVideoInfo;
import com.modernsky.istv.bean.UserEntity;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.BitmapTool;
import com.modernsky.istv.tool.DialogTool;
import com.modernsky.istv.tool.NetworkHelper;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.PreferencesUtils;
import com.modernsky.istv.utils.StringUtils;
import com.modernsky.istv.utils.Utils;
import com.modernsky.istv.utils.WeakHandler;
import com.modernsky.istv.view.RoundAngleImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by zqg on 2016/3/4.
 */
public class ApplyAnchorActivity extends BaseActivity implements OnPlayStateListener {
    private ISplayer player;
    private String path = "";
    private PlayContext playContext;
    private ILeVideoView videoView;
    private long lastposition;
    private Bundle mBundle;
    RelativeLayout.LayoutParams relativeMatchLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
    private boolean isPlaying;
    private Map<Integer, String> definationsMap;
    private ArrayList mQXDList;
    private String videoId = "900";
    String uu = "53f80d6851";
    String vu = "6c31fb9cca";
    private String userId;
    //    HorizontalListView hlv;
//    private HorizontalListViewAdapter hlvAdapter;
    private RoundAngleImageView pic;

    private TextView btn;//申请按钮
    private SeekBar seekBar;
    private long mDuration;
    private TextView videoTime, videoName;
    private final int FADE_OUT = 1001;
    private final int SHOW_PROGRESS = 1002;
    private ImageView centerImg;
    private ImageView playBtn;
    private ImageView closeImg;
    private AudioManager mAM;
    private int isPass;//是否通过审核

    private List<ApplyAnchorInfo> anchorInfos;
    private ApplyVideoInfo applyVideoInfo;
    private List<ApplyStateInfo> applyStateInfos;

    private List<RoundAngleImageView> anchorImgs;
    private List<TextView> nameTexts;
    private List<TextView> levelTexts;

    private TextView title1, title2, content1, content2;

    private TextView levelText;

    //    //    private int getIsPassCount=0;
//    private Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            long pos;
//            switch (msg.what) {
//                case FADE_OUT:
////                    hide();
//                    break;
//                case SHOW_PROGRESS:
//                    pos = setProgress();
//                    if (!mDragging && !mHasFinished) {
//                        msg = obtainMessage(SHOW_PROGRESS);
//                        sendMessageDelayed(msg, 1000 - (pos % 1000));
//                        updatePausePlay();
//                    }
//                    break;
//            }
//        }
//    };
    private   Handler mHandler = new WeakHandler(this) {
        @Override
        public void conventHandleMessage( Message msg) {
            long pos;
            switch (msg.what) {
                case FADE_OUT:
//                    hide();
                    break;
                case SHOW_PROGRESS:
                    pos = setProgress();
                    if (!mDragging && !mHasFinished) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                        updatePausePlay();
                    }
                    break;
            }
        }
    };
    private boolean mHasFinished;
    private boolean mDragging;
    private TextView nameText;
    private SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStartTrackingTouch(SeekBar bar) {
            mDragging = true;
            mHandler.removeMessages(SHOW_PROGRESS);
//            if (mInstantSeeking)
//                mAM.setStreamMute(AudioManager.STREAM_MUSIC, true);
        }

        @Override
        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {

            if (!fromuser) {
                return;
            }
            long newposition = (mDuration * progress) / 1000;
            String time = StringUtils.generateTime(newposition);
            if (videoTime != null) {
                videoTime.setText(time);
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar bar) {

            seekTo((mDuration * bar.getProgress()) / 1000);
            LogUtils.d("mDuration:" + mDuration);
            LogUtils.d("getProgress:" + bar.getProgress());
//            show(sDefaultTimeout);
            mHandler.removeMessages(SHOW_PROGRESS);
            mAM.setStreamMute(AudioManager.STREAM_MUSIC, false);
            mDragging = false;
            mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
        }
    };
    private View playerUpView;

    public void seekTo(long pos) {
//        Utils.showLoadingAnim(mLoadingView, anim);

        if (player != null) {
            if (isComplete) {
                setPlayUrl();
                return;
            }
                player.seekTo(pos);
        }
    }
//   private void hide(){
//       centerImg.setVisibility(View.GONE);
//   }

    private void updatePausePlay() {

        if (isPlaying) {
            centerImg.setVisibility(View.GONE);
            playBtn.setImageResource(R.drawable.mediacontroller_pause);
            centerImg.setImageResource(R.drawable.mediacontroller_pause);
        } else {
            centerImg.setVisibility(View.VISIBLE);
            playBtn.setImageResource(R.drawable.mediacontroller_play);
            centerImg.setImageResource(R.drawable.mediacontroller_play);
        }
    }

    private void startOrStop() {
        LogUtils.d("startOrStop===isPlaying=");
        if (isPlaying) {
            if (player != null)
                player.pause();
            isPlaying = false;
        } else {
            if (isComplete)
                seekTo(0);
            else if (player != null)
                player.start();
            isPlaying = true;
        }
    }

    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_applyfor_anchor);
    }

    @Override
    public void findViewById() {
        initView();
        initVodPlayer();

//        grtVideoDetail(videoId);
        initList();
        getUserState();
        getDetaiInfo();

    }

    /**
     * 给三个主播list初始化
     */
    private void initList() {
        anchorImgs = Arrays.asList(new RoundAngleImageView[]{(RoundAngleImageView) findViewById(R.id.img_anchor_pic_left),
                (RoundAngleImageView) findViewById(R.id.img_anchor_pic_center),
                (RoundAngleImageView) findViewById(R.id.img_anchor_pic_right)});
        nameTexts = Arrays.asList(new TextView[]{(TextView) findViewById(R.id.tv_name_anchor_left),
                (TextView) findViewById(R.id.tv_name_anchor_center),
                (TextView) findViewById(R.id.tv_name_anchor_right)});
        levelTexts = Arrays.asList(new TextView[]{(TextView) findViewById(R.id.tv_lv_anchor_left),
                (TextView) findViewById(R.id.tv_lv_anchor_center),
                (TextView) findViewById(R.id.tv_lv_anchor_right)});
    }

    private void getUserState() {
        UserEntity bean = UserService.getInatance().getUserBean(this);
        if (bean != null) {
            userId = bean.getId();
        }
        RequestParams params = UrlTool.getParams(
                Constants.USER_ID, userId);

        SendActtionTool.get(Constants.UserParams.URL_GET_ISANCHOR,
                ServiceAction.Action_Comment,
                CommentAction.Action_Check_AnchorState, this, params);
    }

    private void initView() {
        levelText = (TextView) findViewById(R.id.tv_level);
        UserEntity userBean = UserService.getInatance().getUserBean(this);
        if (userBean != null && userBean.getRank() != null)
            levelText.setText(userBean.getRank().getRank());
        title1 = (TextView) findViewById(R.id.tv_state_what);
        content1 = (TextView) findViewById(R.id.tv_content_what);
        title2 = (TextView) findViewById(R.id.tv_title2);
        content2 = (TextView) findViewById(R.id.tv_content_anchor);

        seekBar = (SeekBar) findViewById(R.id.seek_video);
        seekBar.setOnSeekBarChangeListener(mSeekListener);
        mAM = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        playBtn = (ImageView) findViewById(R.id.img_play);
        centerImg = (ImageView) findViewById(R.id.img_center);
        closeImg = (ImageView) findViewById(R.id.img_close);
        nameText = (TextView) findViewById(R.id.text_name);

        playBtn.setOnClickListener(this);
        centerImg.setOnClickListener(this);
        closeImg.setOnClickListener(this);

        playerUpView = findViewById(R.id.view_upplayer);
        videoTime = (TextView) findViewById(R.id.time_video);
        videoName = (TextView) findViewById(R.id.name_video);
        pic = (RoundAngleImageView) findViewById(R.id.im_user);
        btn = (TextView) findViewById(R.id.btn_apply);
        btn.setOnClickListener(this);
//        hlv = (HorizontalListView) findViewById(R.id.anchorList);
//        setAnchorList();
        if (!UserService.getInatance().isNeedLogin(this)) {
            BitmapTool.getInstance().getAdapterUitl().display(pic, UserService.getInatance().getUserBean(this).getFaceUrl());
        }
        nameText.setText(UserService.getInatance().getUserBean(this).getUserName());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_apply:
                switch (isPass) {
                    case 0://没通过
                        startActivity(new Intent(this, ApplyForActivity.class));
                        finish();
                        break;
                    case 1://通过
                        startActivity(new Intent(this, PublishActivity.class));
                        finish();
                        break;
                    case 2://审核中
                        return;
//                        break;
                }
//                startActivity(new Intent(this, ApplyForActivity.class));

                break;
            case R.id.img_center:
                LogUtils.d("img_center----");
            case R.id.img_play:
                LogUtils.d("img_play----");
                startOrStop();

                break;
            case R.id.img_close:
                finish();
                break;
        }
    }


    private void initVodPlayer() {
        mQXDList = new ArrayList<String>();
        initPlayView();
    }


    private void initPlayView() {
        if (mQXDList == null)
            mQXDList = new ArrayList<String>();
        LogUtils.d("initPlayView");
        initVideoView();// 初始化videoView
        initPlayContext();// 初始化playContext
        mHandler.sendEmptyMessage(SHOW_PROGRESS);
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        super.onSuccess(service, action, value);
        switch ((CommentAction) action) {
//            case Action_GetVideo_By_Id:
//                try {
//                    ResultBean<LatestVideo> resultVideoBean = JSON
//                            .parseObject(
//                                    jsonString,
//                                    new TypeReference<ResultBean<LatestVideo>>() {
//                                    });
//                    LatestVideo albumLastVideo = resultVideoBean.data;
////                    albumId = String.valueOf(albumLastVideo.getAlbumId());
////                    if (xuanjiFragment != null
////                            && Constants.VIDEO_NAME.equals(stringType)) {
////                        xuanjiFragment.getAlbumVideo(albumId);
////                    }
////                    if (!TextUtils.isEmpty(albumLastVideo.getName())) {
////                        mTitle = albumLastVideo.getName();
////                    }
//                    setLiveUrlandPlay(albumLastVideo);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    LogUtils.e("javaBean 转型失败 " + e.toString());
//                }
//                break;
            case Action_Check_AnchorState:
                LogUtils.d("onSuccess   Action_Check_AnchorState---" + value.toString());
                try {
                    AnchorInfoBean anchorInfo = JSON.parseObject(((JSONObject) value).getString("data"), AnchorInfoBean.class);
                    isPass = anchorInfo.getIsPass();
//                    getIsPassCount=0;
                    LogUtils.d("onSuccess   Action_Check_AnchorState---isPass" + isPass);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                switch (isPass) {
                    case 0: //没通过
                        btn.setText("申请");
                        break;
                    case 1://通过
                        btn.setText("发布预告");
                        break;
                    case 2://审核中
                        btn.setText("审核中");
                        break;
                }
                break;
            case Action_GetDetailInfo_Apply:
                parseJsonData((JSONObject) value);
                break;
        }
    }

    private void parseJsonData(JSONObject value) {
        try {
            JSONArray datasArray = value.getJSONArray("data");
            JSONObject object;
            for (int i = 0; i < datasArray.length(); i++) {
                object = datasArray.getJSONObject(i);
                switch (object.getString("type")) {
                    case "1":
                        anchorInfos = (List<ApplyAnchorInfo>) JSON.parseArray(object.getString("data"), ApplyAnchorInfo.class);
                        initAnchorsData(anchorInfos);
                        break;
                    case "2":
                        applyVideoInfo = JSON.parseObject(object.getString("data"), ApplyVideoInfo.class);
                        videoName.setText(applyVideoInfo.getName());
                        playVideoByUrl(applyVideoInfo.getVideoId() + "", applyVideoInfo.getVideoPlayInfo().getForeignUnique(), applyVideoInfo.getName());
                        break;
                    case "3":
//                        applyStateInfos = JSON.parseArray(object.getString("data"), ApplyStateInfo.class);
//                        initStatesData(applyStateInfos);
                        break;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 文字初始化
    private void initStatesData(List<ApplyStateInfo> list) {
        if (list.size() >= 2) {
            title1.setText(list.get(0).getTitle());
            title2.setText(list.get(1).getTitle());
            content1.setText(list.get(0).getContent());
            content2.setText(list.get(1).getContent());
        }

    }

    private void initAnchorsData(List<ApplyAnchorInfo> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        for (int i = 0; i < anchorImgs.size(); i++) {
            if (list.size() < i + 1) {
                break;
            }
            LogUtils.d("url=" + list.get(i).getFaceUrl());
            BitmapTool.getInstance().initAdapterUitl(this).display(anchorImgs.get(i), list.get(i).getFaceUrl());
            nameTexts.get(i).setText(list.get(i).getUserName());
            levelTexts.get(i).setText(String.valueOf(list.get(i).getRank().getRank()));
        }
    }


    public void playVideoByUrl(final String videoId, String foreignUnique, String videoName) {

        this.videoId = videoId;
        //
        vu = foreignUnique;
        if (NetworkHelper.isNetworkConnected(this)
                && !NetworkHelper.isWifiConnected(this)
                && !PreferencesUtils.getBooleanPreferences(this,
                PreferencesUtils.TYPE_NET_TOGLE)) {
            DialogTool.createNetWorkDialog(this, new DialogTool.DialogLister() {
                @Override
                public void onCountinue() {
                    setPlayUrl();
                }

                @Override
                public void onCancelListener() {
//                    finish();
                }
            }).show();
        } else {
            setPlayUrl();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            if (isPlaying)
                player.start();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (playContext != null) {
//            addPlayRecord(String.valueOf(playTime / 1000));
            playContext.destory();
        }
//        isBackgroud = true;
        mHasFinished = true;
//        Utils.sendBroadcastToService(8, this);
    }

    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {
        super.onFaile(service, action, value);
        switch ((CommentAction) action) {
            case Action_Check_AnchorState:
//                if (getIsPassCount < 3) {
//                    getIsPassCount++;
//                    getUserState();
//                } else {
//                    Utils.toast(this,value.toString());
//                }
                break;
        }

    }

    @Override
    public void onException(ServiceAction service, Object action, Object value) {
        super.onException(service, action, value);
    }

    // 获取整个页面信息
    public void getDetaiInfo() {
        if (!TextUtils.isEmpty(videoId)) {
            SendActtionTool.get(Constants.URL_GET_DETAILINFO_APPLY,
                    ServiceAction.Action_Comment,
                    CommentAction.Action_GetDetailInfo_Apply, this);
        }
    }

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

    // surfaceView的生命周期

    private SurfaceHolder mSurfaceHolder;
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

    /**
     * 播放，初始话播放控件
     *
     * @param
     */
    private void setPlayUrl() {

////        getMyMediaController().setVideoShowName(mTitle);
//        if (playFragment != null)
//            getPraiseCount(videoId, "0");
        mBundle = LetvParamsUtils.setVodParams(uu, vu, "", "", "");
        playvideo();
//        if (player!=null)
//            player.start();

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
//                        mQXDList.clear();
                        Iterator<Map.Entry<Integer, String>> iterator = definationsMap.entrySet().iterator();
                        while (iterator.hasNext()) {
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
//        getMyMediaController().updateController();
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
                playerUpView.setVisibility(View.GONE);
//                Utils.cancaleLoadingAnim(mLoadingView, anim);
                break;
            case ISplayer.MEDIA_EVENT_PLAY_COMPLETE:// 视频播放完成
                isPlaying = false;
                isComplete = true;
                LogUtils.d("RankActivity--  +ISplayer.MEDIA_EVENT_PLAY_COMPLETE");
                break;
            case ISplayer.MEDIA_EVENT_BUFFER_START:// 开始缓冲
//                Utils.showLoadingAnim(mLoadingView, anim);
                LogUtils.d("RankActivity--  +ISplayer.MEDIA_EVENT_BUFFER_START");
                break;
            case ISplayer.MEDIA_EVENT_BUFFER_END:// 缓冲结束
//                mMediaController.hideBreak();
//                Utils.cancaleLoadingAnim(mLoadingView, anim);
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

    public long getDuration() {
        if (player != null) {
            LogUtils.d("getTotalDuration:" + player.getDuration());
            if (player.getDuration() > 0)
                mDuration = player.getDuration();
            return mDuration;
        }
        return 0;
    }

    public long setProgress() {

//        LogUtils.d("setProgress");
        long position = getCurrentPosition();
        long duration = getDuration();
        if (seekBar != null) {
            if (duration > 0) {
                long pos = 1000L * position / duration;
                seekBar.setProgress((int) pos);
            }
            int percent = getBufferPercentage();
            seekBar.setSecondaryProgress(percent * 10);
        }
        mDuration = duration;
        // if (mEndTime != null)
        // mEndTime.setText(StringUtils.generateTime(mDuration));
        if (videoTime != null) {
            videoTime.setText(StringUtils.generateTime(position) + "/" + StringUtils.generateTime(mDuration));
        }
        return position;
    }

    public int getBufferPercentage() {
        if (player != null)
            return (int) (player.getBufferPercentage() / 1000);
        return 0;
    }

    public long getCurrentPosition() {
        if (player != null) {
//            LogUtils.d("mPlayerView != null");
            return player.getCurrentPosition();
        }
        return 0;
    }

}

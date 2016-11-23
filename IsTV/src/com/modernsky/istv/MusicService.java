package com.modernsky.istv;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.http.RequestParams;
import com.modernsky.istv.acitivity.ScreenActivity;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.action.UserAction;
import com.modernsky.istv.bean.MusicInfo;
import com.modernsky.istv.listener.CommonListener;
import com.modernsky.istv.service.DianTaiService;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.LineControlTool;
import com.modernsky.istv.tool.LineControlTool.OnHeadSetListener;
import com.modernsky.istv.tool.NetworkHelper;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.PreferencesUtils;
import com.modernsky.istv.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicService extends Service implements CommonListener {
    // private static String PATH1 =
    // "http://mp3tuijian.9ku.com/tuijian/2015/10-09/667111.mp3?xcode=cfe65dc89e9cb8a2bbd2484dc36a3e9a9bcd1fed2f0c7e78";

    // private static final String PATH1 =
    // "http://182.92.167.30/m/audio/play/12";
    private int errorCount = 0;
    private int pageIndex = 1;
    private MediaPlayer mediaPlayer;
    private int i = 0;// 0 biao表示 未登录 1表示已登录
    private boolean isPaused = false;
    int state = -1;
    int index = 0;
    // List<String> list;
    List<MusicInfo> musicList;
    private boolean getTokenPlay = false;
    private boolean getListPlay = false;// 获取列表后播放
    private boolean deleteOrLike;// true 喜欢 false 删除
    private MusicSercieReceiver receiver;
    private List<MusicInfo> localMusicLike;
    private List<MusicInfo> localMusicDelete;
    private int countOfListNull = 0;
    private AudioManager mAudioManager;

    @Override
    public void onCreate() {
        super.onCreate();
        receiver = new MusicSercieReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.modernsky.istv.MusicService");
        filter.addAction("com.android.deskclock.ALARM_ALERT");
        filter.addAction("com.android.deskclock.ALARM_DONE");
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Constants.ACTION_LOGIN_CHANGE);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver, filter);
        getDianTaiToken();

        if (UserService.getInatance().isNeedLogin(getApplicationContext())) {
            i = 0;
        } else {
            i = 1;
        }

        getMusicList();
        // getListPlay 获取列表后默认
        getListPlay = PreferencesUtils.getBooleanPreferences(this,
                PreferencesUtils.TYPE_DIANTAI_TOGLE);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    if (index == musicList.size() - 1) {
                        index = 0;
                        pageIndex++;
                        getListPlay = true;
                        getMusicList();

                    } else {
                        playNext();
//                        index++;
//                        playMusic();
//                        // prepareAndPlay();

                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        });
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                LogUtils.t("mediaPlayer", "onPrepared");
                state = STATE_PlAY;
                sendBroadcastToActicity(true, 0, true, 0);
                errorCount = 0;
            }
        });
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                LogUtils.t("onBufferingUpdate", "" + percent);
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                LogUtils.t("onError", "what==" + what + ", extra==" + extra);
                pauseMusic();
//                playNext();
                return false;
            }
        });
        getLocalList();
        // 获取电话通讯服务
        TelephonyManager tpm = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);
        // 创建一个监听对象，监听电话状态改变事件
        tpm.listen(new MyPhoneStateLisentener(),
                PhoneStateListener.LISTEN_CALL_STATE);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(mAudioFocusListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        LineControlTool.getInstance().setOnHeadSetListener(headSetListener);
        LineControlTool.getInstance().open(this);

    }

    @Override
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub
        return super.onUnbind(intent);

    }

    private void getLocalList() {

        try {
            String localDelete = PreferencesUtils.getPreferences(this,
                    PreferencesUtils.TYPE_DELETE_MUSIC);
            LogUtils.d("localDelete====" + localDelete);
            if (!TextUtils.isEmpty(localDelete)) {
                localMusicDelete = JSON.parseArray(localDelete, MusicInfo.class);
            } else {
                localMusicDelete = new ArrayList<MusicInfo>();
            }
            LogUtils.d("localMusicDeleteList.size===="
                    + localMusicDelete.size());
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d("localMusicDelete====Exception" + e.toString());
            localMusicDelete = new ArrayList<MusicInfo>();

        }

        try {
            String localLike = PreferencesUtils.getPreferences(this,
                    PreferencesUtils.TYPE_LIKE_MUSIC);
            LogUtils.d("localLike====" + localLike);
            if (!TextUtils.isEmpty(localLike)) {
                localMusicLike = JSON.parseArray(localLike, MusicInfo.class);
            } else {
                localMusicLike = new ArrayList<MusicInfo>();
            }
            LogUtils.d("localMusicDelete.size====" + localMusicLike.size());
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d("localMusicLikeException====" + e.toString());
            localMusicLike = new ArrayList<MusicInfo>();

        }

    }

    private boolean mPausedByTransientLossOfFocus;
    OnAudioFocusChangeListener mAudioFocusListener = new OnAudioFocusChangeListener() {

        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                    LogUtils.d("AudioFocus: received AUDIOFOCUS_LOSS");
                    if (state == STATE_PlAY) {
                        mPausedByTransientLossOfFocus = false;
                    }
                    pauseMusic();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    LogUtils.d("AudioFocus: received AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");

                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    LogUtils.d("AudioFocus: received AUDIOFOCUS_LOSS_TRANSIENT");
                    if (state == STATE_PlAY) {
                        mPausedByTransientLossOfFocus = true;
                    }
                    pauseMusic();
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    LogUtils.d("AudioFocus: received AUDIOFOCUS_GAIN");
                    if (state != STATE_PlAY && mPausedByTransientLossOfFocus) {
                        mPausedByTransientLossOfFocus = false;
                        // mCurrentVolume = 0f;
                        // mPlayer.setVolume(mCurrentVolume);
                        playMusic();
                    }
                    break;
                default:
                    Log.e("", "Unknown audio focus change code");
                    break;
            }
        }
    };

    /**
     * @param i 0 表示写入删除 1表示喜欢写入喜欢
     */
    private void writeToLocalList(int i) {
        String jsonString = null;
        if (i == 0) {
            jsonString = com.alibaba.fastjson.JSONArray
                    .toJSONString(localMusicDelete);
            // array = new JSONArray(localMusicDelete);

            boolean issaveDelete = PreferencesUtils.savePreferences(this,
                    PreferencesUtils.TYPE_DELETE_MUSIC, jsonString);
            LogUtils.d("issaveDelete==" + issaveDelete);

        } else if (i == 1) {
            jsonString = com.alibaba.fastjson.JSONArray
                    .toJSONString(localMusicLike);
            boolean saveLike = PreferencesUtils.savePreferences(this,
                    PreferencesUtils.TYPE_LIKE_MUSIC, jsonString);
            LogUtils.d("issaveLike==" + saveLike);
        }
        // LogUtils.d("array==" + jsonString);
    }

    /*
     * 获取电台的token
     */
    public void getDianTaiToken() {
        SendActtionTool.get(Constants.UserParams.URL_USER_GETTOKEN, null,
                UserAction.Action_GETTOKEN, this);
    }

    // i 0表示不要userId 获取 集合 1 要userId获取集合
    private void getMusicList() {
        RequestParams params = UrlTool.getParams(Constants.USER_ID, "");
        if (i == 0) {
            params = UrlTool.getParams(Constants.USER_ID, "", "page", ""
                    + pageIndex);
//            LogUtils.d("pageIndex==" + pageIndex);
        } else if (i == 1) {// 用户id获取列表
            params = UrlTool.getParams(Constants.USER_ID, UserService
                    .getInatance().getUserBean(this).getId(), "page", ""
                    + pageIndex);
        }
//        LogUtils.d("getMusicList=====");
        SendActtionTool.get(Constants.URL_GET_MUSICLIST, null,
                UserAction.Action_GET_MUSIC_LIST, this, params);
    }

    private Map<String, String> getMap() {
        HashMap<String, String> map = new HashMap<String, String>();
        if (UserService.getInatance().getDianTaiToken() != null) {
            map.put("audio-token", UserService.getInatance().getDianTaiToken());
            LogUtils.t("audio-token", UserService.getInatance().getDianTaiToken());
        }
        return map;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        mAudioManager.abandonAudioFocus(mAudioFocusListener);
        unregisterReceiver(receiver);
        LogUtils.d("musicservice onDestroy");
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
        LineControlTool.getInstance().close(this);
        super.onDestroy();
    }

    @SuppressLint("NewApi")
    public void prepareAndPlay() {
        DianTaiService.getInstance().setIsplaying(true);
        DianTaiService.getInstance().setCurrentMusicInfo(musicList.get(index));

//        LogUtils.d("musicurl=" + Constants.UserParams.URL_GET_MUSIC
//                + musicList.get(index).getId());
        try {
            if (state != STATE_STOP) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.setDataSource(
                    this,
                    Uri.parse(Constants.UserParams.URL_GET_MUSIC
                            + musicList.get(index).getId()), getMap());
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepareAsync();
//            mediaPlayer.start();
//            LogUtils.d("prepareAndPlayislike="
//                    + musicList.get(index).getIslike());

        } catch (IllegalArgumentException e) {
            errorCount++;
            e.printStackTrace();
            LogUtils.d("errorCount==" + errorCount + " IllegalArgumentException==" + e.toString());
            if (errorCount < 3)
                playNext();
            else
                stopMusic();
        } catch (SecurityException e) {
            errorCount++;
            e.printStackTrace();
            LogUtils.d("errorCount==" + errorCount + " SecurityException==" + e.toString());
            if (errorCount < 3)
                playNext();
            else
                stopMusic();
        } catch (IllegalStateException e) {
            errorCount++;
            LogUtils.d("errorCount==" + errorCount + " IllegalStateException==" + e.toString());
            e.printStackTrace();
            if (errorCount < 3)
                playNext();
            else
                stopMusic();
        } catch (IOException e) {
            e.printStackTrace();
            stopMusic();
            LogUtils.d("errorCount==" + errorCount + " IOException==" + e.toString());
            if (errorCount < 3) {
                getTokenPlay = true;
                getDianTaiToken();
            }
            errorCount++;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d("Exception==" + e.toString());
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {

        return null;
    }

    public final int STATE_PlAY = 0;
    public final int STATE_PAUSE = 1;
    public final int STATE_STOP = 2;
    public final int STATE_PRE = 3;
    public final int STATE_NEXT = 4;
    private String token;
    private boolean isPlayAfter;
    private boolean shouPlayerAfterAlert = false;
//	public KeyguardManager mKeyguardManager;
//	public KeyguardLock mKeyguardLock;

    class MusicSercieReceiver extends BroadcastReceiver {

        @SuppressWarnings("deprecation")
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
//			mKeyguardManager = (KeyguardManager) context
//					.getSystemService(Context.KEYGUARD_SERVICE);
//			mKeyguardLock = mKeyguardManager.newKeyguardLock("");
            if (intent.getAction().equals(Constants.ACTION_LOGIN_CHANGE)) {
                // 0 之前未登录 1 之前已登录
                if (i == 0) {
                    // 未登录---》登录
                    if (!UserService.getInatance().isNeedLogin(context)) {
                        i = 1;
                        getMusicList();
                    }
                } else if (i == 1) {
                    // 登录到未登录
                    if (UserService.getInatance().isNeedLogin(context)) {
                        i = 0;
                        getMusicList();
                    }

                }

            } else if (intent.getAction().equals(
                    "com.modernsky.istv.MusicService")) {
                int control = intent.getIntExtra("control", -1);
                switch (control) {
                    // 0 play状态 1 暂停 2 停止 3 上一首 4 下一首 5 喜欢 6删除 7 电台进入播放页面的状态 8 播放后的
                    // 操作
                    case 0:// 播放音乐
//                        LogUtils.d("=========000");
                        playMusic();
                        LogUtils.d("receiveplayMusic");
                        break;
                    case 1:// 暂停播放
//                        LogUtils.d("=========111");
                        LogUtils.d("receiveplaypauseMusic");
                        pauseMusic();

                        break;
                    case 2:// 停止播放
//                        LogUtils.d("=========222");
                        if (state == STATE_PlAY || state == STATE_PAUSE) {
                            mediaPlayer.stop();
                            state = STATE_STOP;
                        }
                        sendBroadcastToActicity(false, 0, false, 0);
                        break;
                    case 3:// 上一首
//                        LogUtils.d("=========333");
                        index--;
                        index = index % 10;
                        state = STATE_PlAY;
                        playMusic();
                        // prepareAndPlay();
                        sendBroadcastToActicity(true, 0, true, 0);
                        break;
                    case 4:// 下一首
//                        LogUtils.d("=========444");
                        playNext();
                        break;
                    case 5:// 喜欢
//                        LogUtils.d("=========555");
                        if (DianTaiService.getInstance().getCurrentMusicInfo().getIslike().equals("0")) {
                            likeMusic();
                        } else if (DianTaiService.getInstance().getCurrentMusicInfo().getIslike().equals("1")) {
                            disLikeMusic();
                        }
                        break;
                    case 6:// 删除
//                        LogUtils.d("=========666");
                        deleteMusic();
                        break;

                    case 7:// 播放前的状态
//                        LogUtils.d("=========777");
                        isPlayAfter = DianTaiService.getInstance().isIsplaying();
                        pauseMusic();
                        break;
                    case 8:// 播放前的状态
//                        LogUtils.d("=========888");
                        if (isPlayAfter) {
                            playMusic();
                        }
                        break;

                    default:
                        break;
                }

            } else if (intent.getAction().equals(
                    ConnectivityManager.CONNECTIVITY_ACTION)) {
                ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = mConnectivityManager
                        .getActiveNetworkInfo();
                if (netInfo != null && netInfo.isAvailable()) {
//                    String name = netInfo.getTypeName();
                    if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {// ///WiFi网络 

                    } else if (netInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {// 有线

                    } else if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {// 手机移动网络
                        if (!DianTaiService.getInstance()
                                .isShouldPlayInYiDong() && state == STATE_PlAY) {
                            pauseMusic();
                            sendBroadcastToActicity(DianTaiService
                                    .getInstance().isIsplaying(), 0, false, 1);
                        }
                    }
                }
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                Log.i("tag",
                        "----------------- android.intent.action.SCREEN_ON------");
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Intent lockIntent = new Intent(context, ScreenActivity.class);
                lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // mKeyguardLock.disableKeyguard();
                if (state == STATE_PlAY) {
                    context.startActivity(lockIntent);
                }

                Log.i("tag",
                        "----------------- android.intent.action.SCREEN_off------");
            }
        }
    }

    private void deleteMusic() {
        if (musicList == null || musicList.size() == 0) {
            // getListPlay = true;
            index = 0;
            getMusicList();
        } else {

            if (UserService.getInatance().isNeedLogin(this)) {
                if (!localMusicDelete.contains(DianTaiService.getInstance()
                        .getCurrentMusicInfo())) {

                    localMusicDelete.add(DianTaiService.getInstance()
                            .getCurrentMusicInfo());
                    writeToLocalList(0);
                }
                this.musicList.remove(index);
                DianTaiService.getInstance().setMusicList(musicList);
//                LogUtils.d("index==" + index + "musicList.size() - 1="
//                        + (musicList.size() - 1));
                if (index >= musicList.size() - 1) {
                    index = 0;
                    getMusicList();
                } else {
                    DianTaiService.getInstance().setCurrentMusicInfo(
                            musicList.get(index));
                    if (DianTaiService.getInstance().isIsplaying()) {
                        prepareAndPlay();
                    } else {
                        //
                        // DianTaiService.getInstance().setCurrentMusicInfo(
                        // DianTaiService.getInstance().getMusicList()
                        // .get(index));
                        sendBroadcastToActicity(false, 0, true, 0);
                    }

                }
                state = STATE_STOP;
                Utils.toast(this, "删除成功");

            } else {

                deleteOrLike = false;
                likeMusicOrDelete(0);
            }
        }
    }

    private void likeMusic() {

        if (musicList == null || musicList.size() == 0) {
            getMusicList();
        } else {
            // 未登录喜欢
            if (UserService.getInatance().isNeedLogin(this)) {
                if (!localMusicLike.contains(DianTaiService.getInstance()
                        .getCurrentMusicInfo())) {
                    localMusicLike.add(DianTaiService.getInstance()
                            .getCurrentMusicInfo());
                    writeToLocalList(1);
                }
                DianTaiService.getInstance().getCurrentMusicInfo()
                        .setIslike("1");
                // 喜欢
                sendBroadcastToActicity(DianTaiService.getInstance()
                        .isIsplaying(), 1, false, 0);
                Utils.toast(this, "添加喜欢成功");
            } else {
                // 登录的喜欢
                deleteOrLike = true;
                likeMusicOrDelete(1);
            }
        }
    }

    private void disLikeMusic() {

        if (musicList == null || musicList.size() == 0) {
            getMusicList();
        } else {
            // 未登录不喜欢
            if (UserService.getInatance().isNeedLogin(this)) {
                if (localMusicLike.contains(DianTaiService.getInstance()
                        .getCurrentMusicInfo())) {
                    localMusicLike.remove(DianTaiService.getInstance()
                            .getCurrentMusicInfo());
                    writeToLocalList(1);
                }
                DianTaiService.getInstance().getCurrentMusicInfo()
                        .setIslike("0");
                // 不喜欢
                sendBroadcastToActicity(DianTaiService.getInstance()
                        .isIsplaying(), 3, false, 0);
                Utils.toast(this, "取消喜欢成功");
            } else {
                // 登录的取消喜欢
                likeMusicOrDelete(2);//取消
            }
        }
    }

    private void stopMusic() {
        if (state != STATE_STOP) {
            mediaPlayer.stop();
            state = STATE_STOP;
        }
        DianTaiService.getInstance().setIsplaying(false);
        sendBroadcastToActicity(false, 0, false, 0);
    }

    private void pauseMusic() {
        // mediaPlayer.pause();
        if (state == STATE_PlAY) {
            LogUtils.d("pauseMusic==mediaPlayer.pause()");
            mediaPlayer.pause();
            state = STATE_PAUSE;
        }
        DianTaiService.getInstance().setIsplaying(false);
        sendBroadcastToActicity(false, 0, false, 0);
    }

    private void playMusic() {
        if (TextUtils.isEmpty(token)) {
            getDianTaiToken();
            getTokenPlay = true;
        } else {
            if (musicList == null || musicList.size() == 0) {
                getListPlay = true;
                getMusicList();
            } else {
                if (NetworkHelper.isNetworkConnected(this)
                        && !NetworkHelper.isWifiConnected(this)
                        && !DianTaiService.getInstance().isShouldPlayInYiDong()) {
                    // 只有最后一个参数有用 不允许时显示
                    DianTaiService.getInstance().setIsplaying(false);
                    sendBroadcastToActicity(false, 0, true, 1);
                    return;
                }
                if (state == STATE_PAUSE) {// 如果原来状态是暂停
                    LogUtils.d("playMusicstate == STATE_PAUSE");
                    DianTaiService.getInstance().setIsplaying(true);
                    mediaPlayer.start();
                    state = STATE_PlAY;
                    sendBroadcastToActicity(true, 0, false, 0);
                } else if (state != STATE_PlAY) {
                    LogUtils.d("playMusicstate != STATE_PAUSE");
                    prepareAndPlay();
                }

            }

        }
    }

    private void playNext() {
        if (musicList == null || musicList.size() == 0) {
            getListPlay = true;
            getMusicList();
        } else {
            // index++;
            // index = index % musicList.size();
            // prepareAndPlay();
            // state = STATE_PlAY;
            // sendBroadcastToActicity(true, 0, true);
//            LogUtils.d("index==" + index + "musicList.size() - 1="
//                    + (musicList.size() - 1));
            if (index >= musicList.size() - 1) {

                index = 0;
                pageIndex++;
                getListPlay = true;
                getMusicList();
            } else {
                index++;
                // playMusic();
                if (NetworkHelper.isNetworkConnected(this)
                        && !NetworkHelper.isWifiConnected(this)
                        && !DianTaiService.getInstance()
                        .isShouldPlayInYiDong()) {
                    // 只有最后一个参数有用 不允许时显示
                    DianTaiService.getInstance().setIsplaying(false);
                    sendBroadcastToActicity(false, 0, true, 1);
                    return;
                }
                prepareAndPlay();

            }

        }


    }

    /*
     * 1喜欢 0删除 2 取消喜欢
     */
    private void likeMusicOrDelete(int j) {

//        LogUtils.d("likeMusicOrDelete=" + j);

        RequestParams params = UrlTool.getParams(Constants.USER_ID, UserService
                        .getInatance().getUserBean(this).getId(), Constants.AUDIO_ID,
                DianTaiService.getInstance().getCurrentMusicInfo().getId(),
                Constants.IS_LIKE, "" + j);
        LogUtils.d("userId=+"
                + UserService.getInatance().getUserBean(this).getId()
                + "audioId="
                + DianTaiService.getInstance().getCurrentMusicInfo()
                .getIslike() + "isLike=" + j);
        if (j == 2) {
            //取消喜欢
            SendActtionTool.get(Constants.UserParams.URL_USER_ADDSONG, null,
                    UserAction.Action_DisLikeSong, this, params);
            return;
        }
        //喜欢或者删除喜欢
        SendActtionTool.get(Constants.UserParams.URL_USER_ADDSONG, null,
                UserAction.Action_AddOrDeleteSong, this, params);

    }

    //

    /**
     * @param str            true播放 false暂停
     * @param isDeteleOrTrue 0 没有喜欢喝删除的操作 1 喜欢 2 删除 3 取消喜欢
     * @param isNeedUpdate   是否更新
     * @param showDialog     是否显示本地没有网络时dailog 当接收到有dialog时 1是显示 dialog
     */
    protected void sendBroadcastToActicity(boolean str, int isDeteleOrTrue,
                                           boolean isNeedUpdate, int showDialog) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction(DianTaiService.ACTION_SERVICE);
        intent.putExtra("state", str);
        intent.putExtra("isDeteleOrTrue", isDeteleOrTrue);
        intent.putExtra("isNeedUpdate", isNeedUpdate);
        intent.putExtra("showDialog", showDialog);
        // 向前台Activity发送播放控制的广播
        sendBroadcast(intent);
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        // TODO Auto-generated method stub
//        LogUtils.d("onSuccess=" + value.toString());
        switch ((UserAction) action) {
            case Action_GETTOKEN:
                try {
//                    token = new JSONObject(value.toString()).getJSONObject("data").getString("data");
                    token = new JSONObject(value.toString()).getString("data");
                    UserService.getInatance().setDianTaiToken(token);
//                    LogUtils.d("successtoken=" + token);
                    if (getTokenPlay) {
                        if (musicList == null) {
                            getListPlay = true;
                            getMusicList();
                        } else {
                            playMusic();
                            // prepareAndPlay();

                        }
                        getTokenPlay = false;

                    }

                } catch (JSONException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                break;
            case Action_DisLikeSong:
                DianTaiService.getInstance().getCurrentMusicInfo()
                        .setIslike("0");
                sendBroadcastToActicity(DianTaiService.getInstance().isIsplaying(), 3, false, 0);
                Utils.toast(this, "取消喜欢成功");
                break;
            // 添加或者删除成功
            case Action_AddOrDeleteSong:
                // 喜欢
                LogUtils.d("deleteOrLike=" + deleteOrLike);
                if (deleteOrLike) {
                    DianTaiService.getInstance().getCurrentMusicInfo()
                            .setIslike("1");
                    sendBroadcastToActicity(DianTaiService.getInstance().isIsplaying(), 1, false, 0);
                    Utils.toast(this, "添加喜欢成功");
                } else {
                    // 删除
                    musicList.remove(index);
                    DianTaiService.getInstance().getMusicList().remove(index);
                    Utils.toast(this, "删除成功");
                    state = STATE_STOP;
                    if (index >= musicList.size()) {
                        index = 0;
                        getMusicList();
                    } else {
                        DianTaiService.getInstance().setCurrentMusicInfo(
                                musicList.get(index));
                        if (DianTaiService.getInstance().isIsplaying()) {
                            LogUtils.d("DianTaiService.getInstance().isIsplaying()");
                            playMusic();
//						 prepareAndPlay();
                        } else {
                            DianTaiService.getInstance().setCurrentMusicInfo(
                                    DianTaiService.getInstance().getMusicList()
                                            .get(index));
                            LogUtils.d("!!DianTaiService.getInstance().isIsplaying()");
                            sendBroadcastToActicity(false, 0, true, 0);
                        }
                    }
                }
                break;
            // 获取电台的列表
            case Action_GET_MUSIC_LIST:
//                LogUtils.d("getMusicListsuccess=====");
//                LogUtils.d("musicList===" + value.toString());
                updateMusicList((JSONObject) value);
                if (musicList == null || musicList.size() == 0) {// 这页没有数据请求下一页
                    pageIndex++;
                    countOfListNull++;
                    if (countOfListNull >= 2) {
                        pageIndex = 1;
                        countOfListNull = 0;
                    }
                    getMusicList();
                } else {
                    if (getListPlay) {
                        playMusic();
                        // prepareAndPlay();
                        getListPlay = false;
                    } else {
                        sendBroadcastToActicity(false, 0, true, 0);
                    }
                }

                // if (getListPlay) {
                // if (musicList != null && musicList.size() > 0) {
                // prepareAndPlay();
                // state = STATE_PlAY;
                // sendBroadcastToActicity(true, 0, true);
                // getListPlay = false;
                // }
                // } else {
                // sendBroadcastToActicity(false, 0, true);
                // }
                break;
            default:
                break;
        }
    }

    // 过滤本地的
    public void guoLvList() {
        // LogUtils.d("guoLvList");
        if (UserService.getInatance().isNeedLogin(this)) {
            if (musicList != null && musicList.size() > 0) {
                for (int i = 0; i < musicList.size(); i++) {
                    // LogUtils.d("musicList.get(i).id==="+musicList.get(i).getId());
                    if (localMusicLike.contains(musicList.get(i))) {
                        musicList.get(i).setIslike("1");
//                        LogUtils.d("i====like" + i);
                    }
                    if (localMusicDelete.contains(musicList.get(i))) {
                        musicList.remove(i);
//                        LogUtils.d("i====delete" + i);

                    }
                }
            }
        }
    }

    private void updateMusicList(JSONObject object) {
        try {
            musicList = JSON.parseArray(object.getString("data"),
                    MusicInfo.class);
            guoLvList();
            if (musicList != null && musicList.size() > 0) {
                DianTaiService.getInstance().setMusicList(musicList);
                DianTaiService.getInstance().setCurrentMusicInfo(
                        musicList.get(index));
//                LogUtils.d("musicList=" + musicList);
            }

        } catch (JSONException e) {

            e.printStackTrace();
        }

    }

    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {
        LogUtils.d("onFaile=value" + value.toString());
        switch ((UserAction) action) {
            case Action_AddOrDeleteSong:

                Utils.toast(this, value.toString());

                break;
            case Action_GET_MUSIC_LIST:
                getMusicList();
                break;
            default:
                break;
        }

    }

    @Override
    public void onException(ServiceAction service, Object action, Object value) {
        LogUtils.d("onException=value" + value.toString());
        switch ((UserAction) action) {
            case Action_AddOrDeleteSong:
                try {
                    Utils.toast(this, value.toString());
//				Utils.toast(this, ((JSONObject) value).getString("message"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case Action_GET_MUSIC_LIST:
//                getMusicList();
                break;

            default:
                break;
        }

    }

    @Override
    public void onStart(ServiceAction service, Object action) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onFinish(ServiceAction service, Object action) {

    }

    OnHeadSetListener headSetListener = new OnHeadSetListener() {
        @Override
        public void onDoubleClick() {
            LogUtils.d("双击双击双击");
            playNext();
        }

        @Override
        public void onClick() {
            // txt.setText("单击");
            LogUtils.d("单击单击单击");
            if (state == STATE_PlAY) {
                pauseMusic();
            } else {
                playMusic();
            }
        }

        @Override
        public void onThreeClick() {
            LogUtils.d("三连击三连击");
        }
    };

    class MyPhoneStateLisentener extends PhoneStateListener {

        private boolean isNeedMusicPlay;

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE: // 空闲
                    LogUtils.d("CALL_STATE_IDLE");
                    if (isNeedMusicPlay) {

                        playMusic();
                        isNeedMusicPlay = false;
                    }
                    break;
                case TelephonyManager.CALL_STATE_RINGING: // 来电
                    LogUtils.d("CALL_STATE_RINGING");

                    if (MusicService.this.state == STATE_PlAY) {
                        LogUtils.d("state==" + state);
                        isNeedMusicPlay = true;
                        pauseMusic();
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK: // 摘机（正在通话中）
                    LogUtils.d("CALL_STATE_OFFHOOK");
                    break;
            }
        }

    }

}

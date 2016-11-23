package com.modernsky.istv.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.modernsky.istv.R;
import com.modernsky.istv.adapter.CommonAdapter;
import com.modernsky.istv.adapter.ViewHolder;
import com.modernsky.istv.bean.LiveInfo;
import com.modernsky.istv.tool.DialogTool;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.PreferencesUtils;
import com.modernsky.istv.utils.StringUtils;
import com.modernsky.istv.utils.Utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**朱洧志
 * A view containing controls for a MediaPlayer. Typically contains the buttons
 * like "Play/Pause" and a progress slider. It takes care of synchronizing the
 * controls with the state of the MediaPlayer.
 * <p/>
 * The way to use this class is to a) instantiate it programatically or b)
 * create it in your xml layout.
 * <p/>
 * a) The MediaController will create a default set of controls and put them in
 * a window floating above your application. Specifically, the controls will
 * float above the view specified with setAnchorView(). By default, the window
 * will disappear if left idle for three seconds and reappear when the user
 * touches the anchor view. To customize the MediaController's style, layout and
 * controls you should extend MediaController and override the {#link
 * {@link #makeControllerView()} method.
 * <p/>
 * b) The MediaController is a FrameLayout, you can put it in your layout xml
 * and get it through {@link #findViewById(int)}.
 * <p/>
 * NOTES: In each way, if you want customize the MediaController, the SeekBar's
 * id must be mediacontroller_progress, the Play/Pause's must be
 * mediacontroller_pause, current time's must be mediacontroller_time_current,
 * total time's must be mediacontroller_time_total, file name's must be
 * mediacontroller_file_name. And your resources must have a pause_button
 * drawable and a play_button drawable.
 * <p/>
 * Functions like show() and hide() have no effect when MediaController is
 * created in an xml layout.
 */
public class MediaController extends FrameLayout {
    private static final int sDefaultTimeout = 3000;
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;
    private MediaPlayerControl mPlayer;
    private Context mContext;
    private PopupWindow mWindow;
    private int mAnimStyle;
    private View mAnchor;
    private View mRoot;
    private SeekBar mProgress;
    private TextView mCurrentTime;
    private TextView mFileName;
    private String mTitle;
    private long mDuration;
    private boolean mShowing;
    private boolean mDragging;
    private boolean mInstantSeeking = false;
    private boolean mFromXml = false;
    private boolean isNeedShowWutai, isNeedShowVr;
    private int inModule = -1;// 直播 0 秀场  1 点播  2
    private boolean hasFormMenu;
    //
    private ImageButton mPauseButton, mBackButton, mLockButton, mKuodaButton, btn_vr,
            mNextButton, mTanmuButton;
    //    public View mLoadingView;
    public ListView mQXDlistView;
    private List<String> mQXDList;
    public TextView btn_wutai, btn_jiemudan, tv_biaoqing;
    private AudioManager mAM;
    private OnShownListener mShownListener;
    private OnHiddenListener mHiddenListener;
    private OnMediaControllerClickedListener mControllerClickedListener;
    private LinearLayout ll_break = null;
    //
    public ImageButton mShouCangButton;
    private TextView tv_sendmsg;
    private ListView lv_wutai, lv_vr;
    private List<LiveInfo> liveInfos;
    private List<LiveInfo> vrInfos;
    private CommonAdapter<LiveInfo> commonAdapter;
    private CommonAdapter<LiveInfo> vrAdapter;
    private CommonAdapter<String> mQXDAdapter;
    private boolean once = false;
    public ImageButton zanImg, img8Line;
    private View shareButton;
    private ImageView iv_line = null;
    private static int position = 0;
    private long lastBreakTime = 0;
    @SuppressLint("HandlerLeak")
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
                    if (!mDragging && mShowing) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                        updatePausePlay();
                    }
                    break;
            }
        }
    };
    private View.OnClickListener mPauseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            doPauseResume();
            show(sDefaultTimeout);
        }
    };
    private View.OnClickListener mBackListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mControllerClickedListener.onBackClicked();
        }
    };
    private boolean isLocked;

    // 横竖屏切换
    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setFullMode();
        } else {
            setVerticalMode(inModule);
        }
        refreshDrawableState();
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        hide();
        super.onDetachedFromWindow();
    }

    private OnClickListener mLockListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            isLocked = !isLocked;
            if (isLocked) {
                mLockButton.setImageResource(R.drawable.icon_07bofang_locked);
            } else {
                mLockButton.setImageResource(R.drawable.icon_07bofang_unlock);
            }
            mControllerClickedListener.onLockChanged(isLocked);
        }
    };

    private OnClickListener mFangdaListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // isLocked = true;
            // mLockButton.setImageResource(R.drawable.icon_View.VISIBLE7bofang_locked);
            mControllerClickedListener.onFangdaClicked();
        }
    };

    private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
        @Override
        public void onStartTrackingTouch(SeekBar bar) {
            mDragging = true;
            show(3600000);
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
            if (mCurrentTime != null) {
                mCurrentTime.setText(time);
            }
            if (mInstantSeeking) {
                mPlayer.seekTo(newposition);
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar bar) {
            if (!mInstantSeeking) {
                mPlayer.seekTo((mDuration * bar.getProgress()) / 1000);
            }
            LogUtils.d("mDuration:" + mDuration);
            LogUtils.d("getProgress:" + bar.getProgress());
            show(sDefaultTimeout);
            mHandler.removeMessages(SHOW_PROGRESS);
            mAM.setStreamMute(AudioManager.STREAM_MUSIC, false);
            mDragging = false;
            mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
        }
    };

    public MediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRoot = this;
        mFromXml = true;
        initController(context);
    }

    public MediaController(Context context) {
        super(context);
        if (!mFromXml && initController(context)) {
            initFloatingWindow();
        }
    }

    private boolean initController(Context context) {
        mContext = context;
        mAM = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        return true;
    }

    @Override
    public void onFinishInflate() {
        if (mRoot != null) {
            initControllerView(mRoot);
        }
        super.onFinishInflate();
    }

    private void initFloatingWindow() {
        mWindow = new PopupWindow(mContext);
        mWindow.setFocusable(false);
        mWindow.setBackgroundDrawable(null);
        mWindow.setOutsideTouchable(true);
        mAnimStyle = android.R.style.Animation;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setWindowLayoutType() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            try {
                mAnchor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
                Method setWindowLayoutType = PopupWindow.class.getMethod(
                        "setWindowLayoutType", new Class[]{int.class});
                setWindowLayoutType
                        .invoke(mWindow, WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void initControllerView(View v) {
        mPauseButton = (ImageButton) v.findViewById(getResources()
                .getIdentifier("im_play_or_pause", "id",
                        mContext.getPackageName()));
        if (mPauseButton != null) {
            mPauseButton.requestFocus();
            mPauseButton.setOnClickListener(mPauseListener);
        }
        mBackButton = (ImageButton) v
                .findViewById(getResources().getIdentifier("img_btn_back",
                        "id", mContext.getPackageName()));
        if (mBackButton != null) {
            mBackButton.requestFocus();
            mBackButton.setOnClickListener(mBackListener);
        }
        mLockButton = (ImageButton) v
                .findViewById(getResources().getIdentifier("img_btn_lock",
                        "id", mContext.getPackageName()));
        if (mLockButton != null) {
            mLockButton.requestFocus();
            mLockButton.setOnClickListener(mLockListener);
        }
        mKuodaButton = (ImageButton) v.findViewById(R.id.img_paly_fangda);
        if (mKuodaButton != null) {
            mKuodaButton.requestFocus();
            mKuodaButton.setOnClickListener(mFangdaListener);
        }
        mProgress = (SeekBar) v.findViewById(getResources().getIdentifier(
                "seek_video", "id", mContext.getPackageName()));
        if (mProgress != null) {
                SeekBar seeker = mProgress;
                seeker.setOnSeekBarChangeListener(mSeekListener);
            mProgress.setMax(1000);
        }
        iv_line = (ImageView) v.findViewById(R.id.iv_line);
        img8Line = (ImageButton) v.findViewById(R.id.ImageButton8);
        zanImg = (ImageButton) v.findViewById(R.id.ImageButton7);
        zanImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mControllerClickedListener.onZanClicked();
            }
        });
        mShouCangButton = (ImageButton) v.findViewById(R.id.ImageButton6);
        mShouCangButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mControllerClickedListener.onShouCangClicked();
            }
        });
        shareButton = v.findViewById(R.id.ImageButton5);
        shareButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mControllerClickedListener.onShareClicked();
            }
        });
        mNextButton = (ImageButton) v.findViewById(R.id.im_play_next);
        mNextButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mControllerClickedListener.onNextClicked();
            }
        });
        mTanmuButton = (ImageButton) v.findViewById(getResources().getIdentifier("img_show_tanmu", "id", mContext.getPackageName()));
        //
        ll_break = (LinearLayout) v.findViewById(R.id.ll_break);
        ll_break.setVisibility(View.GONE);
        final TextView tv_clarity = (TextView) v.findViewById(R.id.tv_clarity);
        tv_clarity.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == 0) {
                    return;
                }
                position -= 1;
                mQXDlistView.setSelection(position);
                tv_biaoqing.setText(mQXDAdapter.getItem(position));
                hideBreak();
            }
        });
        //
        PreferencesUtils.TYPE_TANMU_SHOW = true;
        if (mTanmuButton != null) {
            mTanmuButton.requestFocus();
            refreshTanmuButton();
            mTanmuButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    PreferencesUtils.TYPE_TANMU_SHOW = !PreferencesUtils.TYPE_TANMU_SHOW;
                    refreshTanmuButton();
                    mControllerClickedListener
                            .onTanmuClicked(PreferencesUtils.TYPE_TANMU_SHOW);
                    if (!PreferencesUtils.TYPE_TANMU_SHOW) {
                        Utils.toast(mContext, "弹幕已关闭！");
                    } else {
                        Utils.toast(mContext, "弹幕已打开！");
                    }
                }
            });
        }
        tv_sendmsg = (TextView) v.findViewById(R.id.tv_sendmsg);
        if (tv_sendmsg != null) {
            tv_sendmsg.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (!PreferencesUtils.TYPE_TANMU_SHOW) {
                        Utils.toast(mContext, "请点击右侧按钮，打开弹幕开关才能看到您发送的弹幕哦");
                        return;
                    }
                    v.setEnabled(false);
                    hide();
                    mControllerClickedListener.showTanmuDialog();
                    v.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            v.setEnabled(true);
                        }
                    }, 500);
                }
            });
        }
        mCurrentTime = (TextView) v.findViewById(R.id.tv_video_time);
        mFileName = (TextView) v.findViewById(R.id.tv_video_titles);
        btn_wutai = (TextView) findViewById(R.id.btn_wutai);
        btn_vr = (ImageButton) findViewById(R.id.btn_vr);
        btn_jiemudan = (TextView) findViewById(R.id.btn_jiemu);
        lv_wutai = (ListView) findViewById(R.id.lv_wutai);
        lv_vr = (ListView) findViewById(R.id.lv_vr);
        //
        tv_biaoqing = (TextView) findViewById(R.id.btn_biaoqing);
        tv_biaoqing.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mQXDlistView.getVisibility() == View.VISIBLE) {
                    mQXDlistView.setVisibility(View.INVISIBLE);
                } else {
                    mQXDlistView.setVisibility(View.VISIBLE);
                }
            }
        });
        btn_wutai.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lv_wutai.getVisibility() == View.VISIBLE) {
                    lv_wutai.setVisibility(View.INVISIBLE);
                } else {
                    lv_wutai.setVisibility(View.VISIBLE);
                    //显示vr引导页


                }
            }
        });


        btn_vr.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lv_vr.getVisibility() == View.VISIBLE) {
                    lv_vr.setVisibility(View.INVISIBLE);
                } else {
                    lv_vr.setVisibility(View.VISIBLE);
                }
            }
        });
        btn_jiemudan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mControllerClickedListener.onJieMuClicked();
            }
        });
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setFullMode();
        } else {
            setVerticalMode(inModule);
        }
    }

    private void initGuidDialog() {
        String type = PreferencesUtils.TYPE_GUIDE[8];
        Boolean hasGuid = PreferencesUtils.getBooleanPreferences(mContext, type);
//        hasGuid = false;//dialog   每次都显示dialog 调试用
        if (!hasGuid && isNeedShowVr) {
            showGuideDialog(8);
        }
    }
//    private boolean hasVrLive() {
//        boolean hasVr = false;
//        for (int i = 0; i < liveInfos.size(); i++) {
//            if (liveInfos.get(i).getType().equals("3")) {
//                hasVr = true;
//            }
//        }
//        return hasVr;
//    }

    public ListView getQXDListView() {
        if (mQXDlistView == null) {
            mQXDlistView = (ListView) findViewById(R.id.rg_play);
        }
        return mQXDlistView;
    }

    // 引导页
    private void showGuideDialog(int index) {
        String type = PreferencesUtils.TYPE_GUIDE[index];
        Boolean hasGuid = PreferencesUtils.getBooleanPreferences(mContext, type);
//        hasGuid = false;//dialog   每次都显示dialog 调试用
        if (!hasGuid) {
            DialogTool.createGuideDialog((Activity) mContext, index, false, new DialogTool.DialogGuideListener() {
                @Override
                public void onGuide(int index) {
                    switch (index) {
                        case 8:
//                            findViewById(R.id.img_me).performClick();
                            break;
                        default:
                            break;
                    }
                }
            });
            PreferencesUtils.saveBooleanPreferences(mContext, type, true);
        }
    }

    //
    public void showBreak() {
        if (System.currentTimeMillis() - lastBreakTime < 60 * 1000) {
            return;
        }
        lastBreakTime = System.currentTimeMillis();
        ll_break.setVisibility(View.VISIBLE);
        show();
    }

    public void hideBreak() {
        ll_break.setVisibility(View.GONE);
    }

    private void refreshTanmuButton() {
        if (PreferencesUtils.TYPE_TANMU_SHOW) {
            mTanmuButton.setBackgroundResource(R.drawable.but_3_3full_dm_controlbut_open);
        } else {
            mTanmuButton.setBackgroundResource(R.drawable.but_3_3full_dm_controlbut_close);
        }
    }

    public void setMediaPlayer(MediaPlayerControl player) {
        mPlayer = player;
        updatePausePlay();
    }

    /**
     * Control the action when the seekbar dragged by user
     *
     * @param seekWhenDragging True the media will seek periodically
     */
    public void setInstantSeeking(boolean seekWhenDragging) {
        mInstantSeeking = seekWhenDragging;
    }

    public void show() {
        show(sDefaultTimeout);
    }

    /**
     * Set the content of the file_name TextView
     *
     * @param name
     */
    // public void setFileName(String name) {
    // mTitle = name;
    // if (mFileName != null)
    // mFileName.setText(mTitle);
    // }
    public void setVideoShowName(String name) {
        mTitle = name;
        if (mFileName != null)
            mFileName.setText(mTitle);
    }

    /**
     * <p>
     * Change the animation style resource for this controller.
     * </p>
     * <p/>
     * <p>
     * If the controller is showing, calling this method will take effect only
     * the next time the controller is shown.
     * </p>
     *
     * @param animationStyle animation style to use when the controller appears and
     *                       disappears. Set to -1 for the default animation, 0 for no
     *                       animation, or a resource identifier for an explicit animation.
     */
    public void setAnimationStyle(int animationStyle) {
        mAnimStyle = animationStyle;
    }

    /**
     * Show the controller on screen. It will go away automatically after
     * 'timeout' milliseconds of inactivity.
     *
     * @param timeout The timeout in milliseconds. Use 0 to show the controller
     *                until hide() is called.
     */
    public void show(int timeout) {
        LogUtils.d("show");
        if (!mShowing && mAnchor != null && mAnchor.getWindowToken() != null) {
            if (mPauseButton != null) {
                mPauseButton.requestFocus();
            }
            if (mFromXml) {
                setVisibility(View.VISIBLE);
            } else {
                int[] location = new int[2];
                mAnchor.getLocationOnScreen(location);
                Rect anchorRect = new Rect(location[0], location[1],
                        location[0] + mAnchor.getWidth(),
                        location[1] + mAnchor.getHeight());
                mWindow.setAnimationStyle(mAnimStyle);
                setWindowLayoutType();
                mWindow.showAtLocation(mAnchor, Gravity.NO_GRAVITY,
                        anchorRect.left, anchorRect.bottom);
            }
            mShowing = true;
            if (mShownListener != null) {
                mShownListener.onShown();
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

    public boolean isShowing() {
        return mShowing;
    }

    public void hide() {
        LogUtils.d("hide()");
        if (mAnchor == null)
            return;
        if (mShowing) {
            try {
                mHandler.removeMessages(SHOW_PROGRESS);
                if (mFromXml)
                    setVisibility(View.GONE);
                else
                    mWindow.dismiss();
            } catch (IllegalArgumentException ex) {

                LogUtils.d("MediaController already removed");
            }
            mShowing = false;
            if (mHiddenListener != null)
                mHiddenListener.onHidden();
        }
    }

    public void setOnShownListener(OnShownListener l) {
        mShownListener = l;
    }

    public void setOnHiddenListener(OnHiddenListener l) {
        mHiddenListener = l;
    }

    public void setOnMediaControllerClickedListener(
            OnMediaControllerClickedListener l) {
        mControllerClickedListener = l;
    }

    public long setProgress() {
        if (mPlayer == null || mDragging)
            return 0;
        LogUtils.d("setProgress");
        long position = mPlayer.getCurrentPosition();
        long duration = mPlayer.getDuration();
        if (mProgress != null) {
            if (duration > 0&&position>0) {
                long pos = 1000L * position / duration;
                mProgress.setProgress((int) pos);
            }
            int percent = mPlayer.getBufferPercentage();
            mProgress.setSecondaryProgress(percent * 10);
        }
        mDuration = duration;
        // if (mEndTime != null)
        // mEndTime.setText(StringUtils.generateTime(mDuration));
        if (mCurrentTime != null) {
            mCurrentTime.setText(StringUtils.generateTime(position) + "/" + StringUtils.generateTime(mDuration));
        }
        return position;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP && isShowing()) {
            hide();
        } else if (event.getAction() == MotionEvent.ACTION_UP && !isShowing()) {
            show(sDefaultTimeout);
        }
        return false;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        show(sDefaultTimeout);
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (event.getRepeatCount() == 0
                && (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
                || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_SPACE)) {
            doPauseResume();
            show(sDefaultTimeout);
            if (mPauseButton != null)
                mPauseButton.requestFocus();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP) {
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
                updatePausePlay();
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK
                || keyCode == KeyEvent.KEYCODE_MENU) {
            hide();
            return true;
        } else {
            if (isShowing())
                hide();
            else
                show(sDefaultTimeout);
        }
        return super.dispatchKeyEvent(event);
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

    private void updatePausePlay() {
        if (mRoot == null || mPauseButton == null)
            return;

        if (mPlayer != null && mPlayer.isPlaying())
            mPauseButton.setImageResource(getResources().getIdentifier(
                    "mediacontroller_pause", "drawable",
                    mContext.getPackageName()));
        else
            mPauseButton.setImageResource(getResources().getIdentifier(
                    "mediacontroller_play", "drawable",
                    mContext.getPackageName()));
    }

    private void doPauseResume() {
        if (mPlayer == null) {
            return;
        }
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            if (mControllerClickedListener != null) {
                mControllerClickedListener.onStartOrPauseClicked(false);
            }
        } else {
            mPlayer.start();
            mControllerClickedListener.onStartOrPauseClicked(true);
        }
        updatePausePlay();
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (mPauseButton != null)
            mPauseButton.setEnabled(enabled);
        if (mProgress != null)
            mProgress.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    public boolean isHasFormMenu() {
        return hasFormMenu;
    }

    public void setHasFormMenu(boolean hasFormMenu) {
        this.hasFormMenu = hasFormMenu;
    }

    public interface OnShownListener {
         void onShown();
    }

    public interface OnHiddenListener {
         void onHidden();
    }

    public interface OnMediaControllerClickedListener {
         void onBackClicked();

        /**
         *
         */
         void showTanmuDialog();

        /**
         * @param b
         */
         void onTanmuClicked(boolean b);

         void onNextClicked();

         void onWutaiClicked(int index);

         void onVrClicked(int index);

         void onQXDClicked(int index);

         void onLockChanged(boolean locked);

         void onStartOrPauseClicked(boolean start);

         void onFangdaClicked();

         void onZanClicked();

         void onShouCangClicked();

         void onShareClicked();

         void onJieMuClicked();
    }

    public interface MediaPlayerControl {
        void start();

        void pause();

        long getDuration();

        long getCurrentPosition();

        void seekTo(long pos);

        boolean isPlaying();

        int getBufferPercentage();
    }

    // 设置竖屏
    public void setVerticalMode(int inModule) {
        this.inModule = inModule;
        // baseModule
        getQXDListView().setVisibility(View.INVISIBLE);
        mProgress.setVisibility(View.GONE);
        mKuodaButton.setImageResource(R.drawable.icon_07bofang_fangda);
        tv_biaoqing.setVisibility(View.GONE);
        btn_wutai.setVisibility(View.GONE);
        btn_vr.setVisibility(View.GONE);

        lv_wutai.setVisibility(View.GONE);
        lv_vr.setVisibility(View.GONE);
        btn_jiemudan.setVisibility(View.GONE);
        mTanmuButton.setVisibility(View.GONE);
        mCurrentTime.setVisibility(View.GONE);
        mShouCangButton.setVisibility(View.GONE);
        tv_sendmsg.setVisibility(View.GONE);
        //
        switch (inModule) {
            //LiveModule
            case 0:
                break;
            //ShowModule
            case 1:
                break;
            //PlayModule
            case 2:
                mProgress.setEnabled(true);
                mProgress.setVisibility(View.VISIBLE);
                mCurrentTime.setVisibility(View.VISIBLE);
                mShouCangButton.setVisibility(View.VISIBLE);
                break;
        }
    }

    // 设置横屏
    public void setFullMode() {
        mKuodaButton.setImageResource(R.drawable.icon_07bofang_suoxiao);
        tv_biaoqing.setVisibility(View.VISIBLE);
        //直播
        if (inModule == 0) {
            tv_sendmsg.setVisibility(View.VISIBLE);
            mTanmuButton.setVisibility(View.VISIBLE);

            if (hasFormMenu) {
                btn_jiemudan.setVisibility(View.VISIBLE);
            }

            if (isNeedShowWutai) {
                btn_wutai.setVisibility(View.VISIBLE);
            }
            if (isNeedShowVr) {
                btn_vr.setVisibility(View.VISIBLE);
                lv_vr.setVisibility(View.INVISIBLE);
            }
            initGuidDialog();
        }
        //秀场
        if (inModule == 1) {
            tv_sendmsg.setVisibility(View.VISIBLE);
            mTanmuButton.setVisibility(View.VISIBLE);
            if (hasFormMenu) {
                btn_jiemudan.setVisibility(View.VISIBLE);
            }
            if (isNeedShowWutai) {
                btn_wutai.setVisibility(View.VISIBLE);
            }

            if (isNeedShowVr) {
                btn_vr.setVisibility(View.VISIBLE);
                lv_vr.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void showWutai(boolean show) {
        isNeedShowWutai = show;
    }

    public void showVr(boolean show) {
        isNeedShowVr = show;
    }

    public void setQXD(List<String> infos) {
        mQXDlistView = (ListView) findViewById(R.id.rg_play);
        if (mQXDList == null) {
            mQXDList = new ArrayList<String>();
        }
        mQXDList.clear();
        mQXDList.addAll(infos);
        position = mQXDList.indexOf("高清");
        if (position < 0) position = 0;//解决数组下标越界的bug
        if (mQXDAdapter == null) {
            mQXDAdapter = new CommonAdapter<String>(mContext, mQXDList, R.layout.tv_choise) {
                @Override
                public void convert(final ViewHolder helper, final String item) {
                    helper.setText(R.id.tv_hot_detail, item);
                    helper.getConvertView().setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            position = helper.getPosition();
                            tv_biaoqing.setText(item);
                            if (mControllerClickedListener != null) {
                                mControllerClickedListener.onQXDClicked(helper.getPosition());
                                mQXDlistView.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            };
        }
        mQXDlistView.setAdapter(mQXDAdapter);
        tv_biaoqing.setText(mQXDAdapter.getItem(position));
    }

    public void setLiveWutai(List<LiveInfo> infos) {
        liveInfos = infos;
        if (commonAdapter == null) {
            commonAdapter = new CommonAdapter<LiveInfo>(mContext, liveInfos,
                    R.layout.tv_choise) {

                @Override
                public void convert(final ViewHolder helper, final LiveInfo item) {
                    helper.setText(R.id.tv_hot_detail, item.getMsg());
//                    if ("3".equals(item.getType().trim())) {
//                        helper.setVisibility(R.id.tv_hot_detail, GONE);
//                        helper.setVisibility(R.id.imgbtn_vr, VISIBLE);
//                    } else {
                    helper.setVisibility(R.id.tv_hot_detail, VISIBLE);
                    helper.setVisibility(R.id.imgbtn_vr, GONE);
//                    }
                    helper.getConvertView().setOnClickListener(
                            new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    btn_wutai.setText(item.getMsg());
                                    if (mControllerClickedListener != null) {
                                        mControllerClickedListener
                                                .onWutaiClicked(helper
                                                        .getPosition());
                                        lv_wutai.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });

                }
            };
        }
        lv_wutai.setAdapter(commonAdapter);
    }

    public void setVr(List<LiveInfo> infos) {
//        initGuidDialog();
        vrInfos = infos;
        if (vrAdapter == null) {
            vrAdapter = new CommonAdapter<LiveInfo>(mContext, vrInfos,
                    R.layout.tv_choise) {

                @Override
                public void convert(final ViewHolder helper, final LiveInfo item) {
                    helper.setText(R.id.tv_hot_detail, item.getMsg());
//                    if ("3".equals(item.getType().trim())) {
//                        helper.setVisibility(R.id.tv_hot_detail, GONE);
//                        helper.setVisibility(R.id.imgbtn_vr, VISIBLE);
//                    } else {
                    helper.setVisibility(R.id.tv_hot_detail, VISIBLE);
                    helper.setVisibility(R.id.imgbtn_vr, GONE);
//                    }
                    helper.getConvertView().setOnClickListener(
                            new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    if (mControllerClickedListener != null) {
                                        mControllerClickedListener
                                                .onVrClicked(helper
                                                        .getPosition());
                                        lv_vr.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });

                }
            };
        }
        lv_vr.setAdapter(vrAdapter);
    }

    /**
     * Set the view that acts as the anchor for the control view. This can for
     * example be a VideoView, or your Activity's main view.
     *
     * @param view The view to which to anchor the controller when it is visible.
     */
    public void setAnchorView(View view) {
        LogUtils.d("setAnchorView");
        mAnchor = view;
        if (!mFromXml) {
            removeAllViews();
            mRoot = makeControllerView();
            mWindow.setContentView(mRoot);
            mWindow.setWidth(android.view.ViewGroup.LayoutParams.MATCH_PARENT);
            mWindow.setHeight(android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        initControllerView(mRoot);
    }

    protected View makeControllerView() {
        return ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                getResources().getIdentifier("videocontroller", "layout",
                        mContext.getPackageName()), this);
    }
}

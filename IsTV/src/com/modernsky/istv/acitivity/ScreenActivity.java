package com.modernsky.istv.acitivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.service.DianTaiService;
import com.modernsky.istv.tool.BitmapTool;
import com.modernsky.istv.tool.DialogTool;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.PreferencesUtils;
import com.modernsky.istv.utils.ScreenUtils;
import com.modernsky.istv.utils.Utils;
import com.modernsky.istv.utils.WeakHandler;
import com.modernsky.istv.view.RoundAngleImageView;
import com.nineoldandroids.view.ViewHelper;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class ScreenActivity extends BaseActivity {
    private View rootView;
    private RoundAngleImageView roundImg;
    private TextView mTimeText, mDateText, mSongName, mPersonName;
    private ImageView mDeleteImg, mPlayImg, mNextImg, mLikeImg, mBacImg;
    private Calendar calendar;
    private int mMonth, mDay, mHour, mMinite, mWeekDay;
    private String mWeekDayS;

    // 0 play状态 1 暂停 2 停止 3 上一首 4 下一首 5 喜欢 6删除
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_screen:
                mDeleteImg.setEnabled(false);
                handler.sendEmptyMessageDelayed(1, 500);
                LogUtils.d("delete_screen");
                Utils.sendBroadcastToService(6, this);
                break;
            case R.id.play_screen:
                mPlayImg.setEnabled(false);
                handler.sendEmptyMessageDelayed(2, 500);
                LogUtils.d("play_screen");
                if (isPlaying) {
                    Utils.sendBroadcastToService(1, this);
                } else {
                    Utils.sendBroadcastToService(0, this);
                }
                PreferencesUtils.saveBooleanPreferences(this, PreferencesUtils.TYPE_DIANTAI_TOGLE, !isPlaying);
                break;
            case R.id.next_screen:
                mNextImg.setEnabled(false);
                handler.sendEmptyMessageDelayed(3, 500);
                LogUtils.d("next_screen");
                Utils.sendBroadcastToService(4, this);
                break;
            case R.id.like_screen:
                mLikeImg.setEnabled(false);
                handler.sendEmptyMessageDelayed(4, 500);
                LogUtils.d("like_screen");
                if (DianTaiService.getInstance().getCurrentMusicInfo() != null) {
//				if (DianTaiService.getInstance().getCurrentMusicInfo()
//						.getIslike().equals("1")) {
//					Utils.toast(this, "已经喜欢，不能重复喜欢");
//				} else {
                    Utils.sendBroadcastToService(5, this);
//				}
                }
                break;
            default:
                break;
        }
    }

    WeakHandler handler = new WeakHandler(this) {

        public void conventHandleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    // 更新时间
                    updateTimeAndDate();
                    break;
                case 1:
                    mDeleteImg.setEnabled(true);

                    break;
                case 2:
                    mPlayImg.setEnabled(true);
                    break;
                case 3:
                    mNextImg.setEnabled(true);
                    break;
                case 4:
                    mLikeImg.setEnabled(true);
                    break;
                default:
                    break;
            }

        }
    };
    private Timer timer;
    private MyReceive myReceive;

    private void updateTimeAndDate() {
        initTime();

        String month = "", day = "", hour = "", minite = "";
        if (mMonth < 10)
            month = "0" + mMonth;
        else
            month = "" + mMonth;
        if (mDay < 10)
            day = "0" + mDay;
        else
            day = "" + mDay;
        if (mHour < 10)
            hour = "0" + mHour;
        else
            hour = "" + mHour;
        if (mMinite < 10)
            minite = "0" + mMinite;
        else
            minite = "" + mMinite;
        mTimeText.setText(hour + ":" + minite);
        mDateText.setText(month + "月" + day + "日" + " 星期" + mWeekDayS);
    }

    private void updateSong() {
        LogUtils.d("updateSong");
        isPlaying = DianTaiService.getInstance().isIsplaying();
        if (DianTaiService.getInstance().getCurrentMusicInfo() != null) {
            mSongName.setText(DianTaiService.getInstance()
                    .getCurrentMusicInfo().getName());
            mPersonName.setText(DianTaiService.getInstance()
                    .getCurrentMusicInfo().getStarringNames());
            BitmapUtils bitUtils = BitmapTool.getInstance().getAdapterUitl();
            bitUtils.display(roundImg, DianTaiService.getInstance()
                            .getCurrentMusicInfo().getBigPic(),
                    new DefaultBitmapLoadCallBack<ImageView>() {
                        @Override
                        public void onLoadCompleted(ImageView container,
                                                    String uri, Bitmap bitmap,
                                                    BitmapDisplayConfig config, BitmapLoadFrom from) {
                            super.onLoadCompleted(container, uri, bitmap,
                                    config, from);
                            Matrix matrix = new Matrix();
                            matrix.postScale(0.5f, 0.5f); // 长和宽放大缩小的比例
                            Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0,
                                    0, bitmap.getWidth(), bitmap.getHeight(),
                                    matrix, true);
                            mBacImg.setImageDrawable(new BitmapDrawable(
                                    getResources(),
                                    blurImageAmeliorate(resizeBmp)));
                        }
                    });

            if (isPlaying) {
                mPlayImg.setImageResource(R.drawable.fm_lockscreen_but_pause);
            } else {
                mPlayImg.setImageResource(R.drawable.fm_lockscreen_but_play);
            }

            if (DianTaiService.getInstance().getCurrentMusicInfo().getIslike()
                    .equals("1")) {
                mLikeImg.setImageResource(R.drawable.fm_lockscreen_but_like_hl);
            } else {
                mLikeImg.setImageResource(R.drawable.fm_lockscreen_but_like);
            }

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                LogUtils.d("menumenumenu");
                return true;
            case KeyEvent.KEYCODE_BACK:
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 柔化效果(高斯模糊)(优化后比上面快三倍)
     *
     * @param bmp
     * @return
     */
    private Bitmap blurImageAmeliorate(Bitmap bmp) {
        long start = System.currentTimeMillis();
        // 高斯矩阵
        // int[] gauss = new int[] { 1, 2, 1, 2, 4, 2, 1, 2, 1 };
        int[] gauss = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1};
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.RGB_565);

        int pixR = 0;
        int pixG = 0;
        int pixB = 0;

        int pixColor = 0;

        int newR = 0;
        int newG = 0;
        int newB = 0;

        int delta = 30; // 值越小图片会越亮，越大则越暗

        int idx = 0;
        int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 1, length = height - 1; i < length; i++) {
            for (int k = 1, len = width - 1; k < len; k++) {
                idx = 0;
                for (int m = -1; m <= 1; m++) {
                    for (int n = -1; n <= 1; n++) {
                        pixColor = pixels[(i + m) * width + k + n];
                        pixR = Color.red(pixColor);
                        pixG = Color.green(pixColor);
                        pixB = Color.blue(pixColor);

                        newR = newR + (int) (pixR * gauss[idx]);
                        newG = newG + (int) (pixG * gauss[idx]);
                        newB = newB + (int) (pixB * gauss[idx]);
                        idx++;
                    }
                }

                newR /= delta;
                newG /= delta;
                newB /= delta;

                newR = Math.min(255, Math.max(0, newR));
                newG = Math.min(255, Math.max(0, newG));
                newB = Math.min(255, Math.max(0, newB));

                pixels[i * width + k] = Color.argb(255, newR, newG, newB);

                newR = 0;
                newG = 0;
                newB = 0;
            }
        }
        for (int i = 0; i < height; i++) {
            pixels[i * width] = Color.argb(255, 0, 0, 0);
            pixels[i * width + width - 1] = Color.argb(255, 2, 2, 2);
        }
        for (int k = 0; k < width; k++) {
            pixels[k] = Color.argb(255, 2, 2, 2);
            pixels[(height - 1) * width + k] = Color.argb(255, 2, 2, 2);
        }
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        long end = System.currentTimeMillis();
        Log.d("may", "used time=" + (end - start));
        return bitmap;
    }

    private void initTime() {
        calendar = Calendar.getInstance();
        mMonth = calendar.get(Calendar.MONTH) + 1;
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinite = calendar.get(Calendar.MINUTE);
        mWeekDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (mWeekDay == 0) {
            mWeekDayS = "日";
        } else {
            mWeekDayS = Utils.alaboToChina(mWeekDay);
        }
    }

    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.screen_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

    }

    @Override
    public void findViewById() {
        rootView = findViewById(R.id.rootView);
        rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        mBacImg = (ImageView) findViewById(R.id.img_bacground);
        roundImg = (RoundAngleImageView) findViewById(R.id.img_song_screen);
        mTimeText = (TextView) findViewById(R.id.time_screen);
        mDateText = (TextView) findViewById(R.id.date_screen);
        mSongName = (TextView) findViewById(R.id.name_song_screen);
        mPersonName = (TextView) findViewById(R.id.name_person_screen);
        mDeleteImg = (ImageView) findViewById(R.id.delete_screen);
        mPlayImg = (ImageView) findViewById(R.id.play_screen);
        mNextImg = (ImageView) findViewById(R.id.next_screen);
        mLikeImg = (ImageView) findViewById(R.id.like_screen);
        mDeleteImg.setOnClickListener(this);
        mPlayImg.setOnClickListener(this);
        mNextImg.setOnClickListener(this);
        mLikeImg.setOnClickListener(this);
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.removeMessages(0);
                    handler.sendEmptyMessage(0);
                }
            }, 0, 60000);

        }
//		updateTimeAndDate();
//		updateSong();
        myReceive = new MyReceive();
        IntentFilter filter = new IntentFilter();
        filter.addAction(DianTaiService.ACTION_SERVICE);
        this.registerReceiver(myReceive, filter);
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        unregisterReceiver(myReceive);
        super.onDestroy();
    }

    float startX, currentX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                if ((currentX - startX) > ScreenUtils.getScreenWidth(this) / 5) {
                    this.finish();
                    overridePendingTransition(0, android.R.anim.slide_out_right);
                } else {
                    ViewHelper.setTranslationX(rootView, 0);
                }

                break;
            case MotionEvent.ACTION_MOVE:
                currentX = event.getX();
                float distance = currentX - startX;
                if (distance > 0) {
                    ViewHelper.setTranslationX(rootView, distance);
                }
                break;
            case MotionEvent.ACTION_CANCEL:

                break;

            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTimeAndDate();
        updateSong();
    }

    private boolean isPlaying;

    public class MyReceive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DianTaiService.ACTION_SERVICE)) {
                int showDialog = intent.getIntExtra("showDialog", 0);
                if (showDialog == 1) {
                    // 显示dialog
                    DialogTool.showNetDialog(ScreenActivity.this);
                }
                isPlaying = intent.getBooleanExtra("state", false);

                // 喜欢的话更新图片
                int isDeteleOrTrue = intent.getIntExtra("isDeteleOrTrue", -1);
                if (isDeteleOrTrue == 1) {
                    mLikeImg.setImageResource(R.drawable.fm_lockscreen_but_like_hl);
                } else if (isDeteleOrTrue == 3) {//取消喜欢
                    mLikeImg.setImageResource(R.drawable.fm_lockscreen_but_like);
                }
                // 是否更新界面
                boolean isNeedUpdate = intent.getBooleanExtra("isNeedUpdate",
                        false);
                if (isNeedUpdate) {
                    updateSong();
                } else {
                    if (isPlaying) {
                        mPlayImg.setImageResource(R.drawable.fm_lockscreen_but_pause);
                    } else {
                        mPlayImg.setImageResource(R.drawable.fm_lockscreen_but_play);
                    }

                }

            }
        }
    }

}

package com.modernsky.istv.acitivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.tool.BitmapTool;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.MediaUtil;
import com.modernsky.istv.utils.WeakHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 开机欢迎广告界面
 *
 * @author rendy
 */
public class WelcomeActivity extends BaseActivity {
    private ImageView loadImageView;
    private TextView timeTet;
    private boolean isBackground;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.welcome_btnPs:
                isWaiteInputJiaoYan = false;
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                LogUtils.d("startActivity(new Intent(MainActivity.class))");
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_welcome);
        startJishi();
        SendActtionTool.get(Constants.URL_GET_START_AD, null, null, this);
    }

    @Override
    public void findViewById() {
        findViewById(R.id.welcome_btnPs).setOnClickListener(this);
        timeTet = (TextView) findViewById(R.id.welcome_time);
        loadImageView = (ImageView) findViewById(R.id.image);
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        super.onSuccess(service, action, value);
        JSONObject object = (JSONObject) value;
        try {
            JSONArray array = object.getJSONArray(Constants.DATA);
            String url = array.getJSONObject(0).getString("bigImage");
            HttpUtils httpUtils = new HttpUtils();
            httpUtils.download(url, MediaUtil.createAvdFile(),
                    new RequestCallBack<File>() {
                        @Override
                        public void onSuccess(ResponseInfo<File> arg0) {
                            BitmapTool
                                    .getInstance()
                                    .getAdapterUitl()
                                    .display(loadImageView,
                                            MediaUtil.createAvdFile());
                        }

                        @Override
                        public void onFailure(HttpException arg0, String arg1) {

                        }
                    });

        } catch (JSONException e) {
        }
    }

    @Override
    public void onFinish(ServiceAction service, Object action) {
        super.onFinish(service, action);
    }


    /**
     * 更新计时器
     */
    private WeakHandler handler = new WeakHandler(this) {
        @Override
        public void conventHandleMessage(Message msg) {
            time--;
            if (time <= 0) {
                stopJishi();
            } else {
                timeTet.setText(String.valueOf(time));
            }
        }


    };
    Timer timer;
    private boolean isWaiteInputJiaoYan;
    private int time = 3;

    /**
     * 开始计时
     */
    private void startJishi() {
        isWaiteInputJiaoYan = true;
        if (timer == null) {
            timer = new Timer();
        }
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        };
        timer.schedule(tt, 1000, 1000);
        handler.sendEmptyMessageDelayed(0, 3000);
    }

    /**
     * 结束计时
     */
    private void stopJishi() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (isWaiteInputJiaoYan & !isFinishing()) {
            starAct();
            isWaiteInputJiaoYan = false;
        }
    }

    private void starAct() {
        if (isBackground)
            return;
        startActivity(new Intent(getApplicationContext(),
                MainActivity.class));
        overridePendingTransition(R.anim.enter, R.anim.exit);
        finish();
        // }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isBackground) {
            isBackground = false;
            if (!isWaiteInputJiaoYan)
                starAct();
        }
    }

    @Override
    protected void onPause() {
        isBackground = true;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}

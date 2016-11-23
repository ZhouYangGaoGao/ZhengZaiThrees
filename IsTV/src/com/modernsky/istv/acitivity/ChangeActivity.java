package com.modernsky.istv.acitivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lidroid.xutils.http.RequestParams;
import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.action.UserAction;
import com.modernsky.istv.bean.UserEntity;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.Constants.UserParams;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.Utils;
import com.modernsky.istv.utils.WeakHandler;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author rendy
 *         <p/>
 *         修改密码
 */
public class ChangeActivity extends BaseActivity {
    private boolean isAgree = true;
    // 顶部指示器
    private TextView tetTime;
    // 顶部指示器
    private EditText edtPsd, edtPsd2;
    private UserEntity userbean;

    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_change_psd);
        userbean = UserService.getInatance().getUserBean(this);
    }

    @Override
    public void findViewById() {
        findViewById(R.id.register_registerBtn).setOnClickListener(this);
        findViewById(R.id.login_shoujiduanxinBtn).setOnClickListener(this);
        findViewById(R.id.tv_back).setOnClickListener(this);
        // 找到指示器
        edtPsd2 = (EditText) findViewById(R.id.register_codeEdt);
        edtPsd = (EditText) findViewById(R.id.register_phoneEdt);

    }

    public void backBtn(View view) {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 执行绑定
            case R.id.register_registerBtn:
                sendRegister();
                break;
            case R.id.login_shoujiduanxinBtn:
                Intent intent = new Intent(getApplication(), ResetPsdActivity.class);
                intent.putExtra(ResetPsdActivity.class.getName(), 1);
                startActivity(intent);
//                startActivity(new Intent(getApplicationContext(), FindPsdTypeActivity.class));
                break;
            case R.id.tv_back:
                finish();
                break;
        }
    }

    /**
     * 执行注册
     */
    private void sendRegister() {

        String phone = edtPsd.getText().toString();
        String phoneCode = edtPsd2.getText().toString();

        if (TextUtils.isEmpty(phone)) {
            Utils.toast(getApplicationContext(), "旧密码不能为空");
            return;
        }
        if (TextUtils.isEmpty(phoneCode)) {
            Utils.toast(getApplicationContext(), "新密码不能为空");
            return;
        }
        RequestParams req = UrlTool.getPostParams("userId", UserService
                        .getInatance().getUserBean(this).getId(), "key", "password",
                "value", phoneCode, "code", "0", "oldPassword", phone);
        SendActtionTool.post(UserParams.URL_USER_UPDATE,
                ServiceAction.Action_User, UserAction.Action_Regiser_Phone,
                this, req);

    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        LogUtils.d("onSuccess", value.toString());
        switch ((UserAction) action) {
            case Action_Regiser_Phone:
                Utils.toast(getApplicationContext(), "修改密码成功");
                finish();
                break;
            case Action_Regiser_Phone_Code:
                startJishi();
                break;
            default:
                break;
        }

    }

    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {
        super.onFaile(service, action, value);
        LogUtils.d("[RegisterActivity.onFaile]", (String) value);
        Utils.toast(getApplicationContext(), value.toString());

    }

    @Override
    public void onException(ServiceAction service, Object action, Object value) {
        LogUtils.d("[RegisterActivity.onException]", "网络连接异常");
        Utils.toast(getApplicationContext(), "请求异常");

    }

//    /**
//     * 更新计时器
//     */
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(android.os.Message msg) {
//            time--;
//            if (time <= 0) {
//                stopJishi();
//            } else {
//                tetTime.setText(time
//                        + getResources().getString(R.string._regetTime));
//            }
//        }
//    };
    /**
     * 更新计时器
     */
    private WeakHandler handler = new WeakHandler(this) {
        @Override
        public void conventHandleMessage( Message msg) {
            time--;
            if (time <= 0) {
                stopJishi();
            } else {
                tetTime.setText(time
                        + getResources().getString(R.string._regetTime));
            }
        }
    };
    Timer timer;
    private boolean isWaiteInputJiaoYan;
    private int time = 56;

    /**
     * 开始计时
     */
    private void startJishi() {
        if (timer == null) {
            timer = new Timer();
        }
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        };
        tetTime.setText(time + getResources().getString(R.string._regetTime));
        isWaiteInputJiaoYan = true;
        timer.schedule(tt, 1000, 1000);
    }

    /**
     * 结束计时
     */
    private void stopJishi() {
        isWaiteInputJiaoYan = false;
        time = 60;
        if (timer != null) {
            timer.cancel();
            timer = null;
            tetTime.setText(R.string._getRegisterCode);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopJishi();
    }

}

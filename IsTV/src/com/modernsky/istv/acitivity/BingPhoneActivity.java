package com.modernsky.istv.acitivity;

import android.os.Bundle;
import android.os.Message;
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
import com.modernsky.istv.utils.CheckCode;
import com.modernsky.istv.utils.Constants.UserParams;
import com.modernsky.istv.utils.GeneralTool;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.Utils;
import com.modernsky.istv.utils.WeakHandler;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author rendy
 *         <p/>
 *         设置注册界面
 */
public class BingPhoneActivity extends BaseActivity {
    private boolean isAgree = true;
    // 顶部指示器
    private TextView tetTime;
    // 顶部指示器
    private EditText edtPhone, edtCode;
    private UserEntity userbean;

    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_bing_phone);
    }

    @Override
    public void findViewById() {
        findViewById(R.id.register_registerBtn).setOnClickListener(this);
        tetTime = (TextView) findViewById(R.id.register_codeBtn);
        tetTime.setOnClickListener(this);
        // 找到指示器
        edtCode = (EditText) findViewById(R.id.register_codeEdt);
        edtPhone = (EditText) findViewById(R.id.register_phoneEdt);
//        edtPsdPone = (EditText) findViewById(R.id.register_codeEdt);
        userbean = UserService.getInatance().getUserBean(this);

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
            case R.id.register_codeBtn:
                if (isWaiteInputJiaoYan) {
                    return;
                }
                String phone = edtPhone.getText().toString();
                if (!CheckCode.isMobileNO(phone)) {
                    Utils.toast(getApplicationContext(), "请输入正确的手机号");
                    return;
                }
                RequestParams request = UrlTool.getParams(UserParams.Mobile, phone);
                SendActtionTool.get(UserParams.URL_REGISTER_CODE, null,
                        UserAction.Action_Regiser_Phone_Code, this, request);
                break;
            default:
                break;
        }
    }

    /**
     * 执行绑定
     */
    private void sendRegister() {

        String phone = edtPhone.getText().toString();
        String phoneCode = edtCode.getText().toString();
//        String phonePsd = edtPsdPone.getText().toString();
        LogUtils.d("phone1====" + phone);
        if (!CheckCode.isMobileNO(phone)) {
            Utils.toast(getApplicationContext(), "请输入正确的手机号");
            return;
        }
        LogUtils.d("phone2====" + phone);
        if (GeneralTool.isEmpty(phoneCode) || phoneCode.length() != 6) {
            Utils.toast(getApplicationContext(), "请输入正确的校验码");
            return;
        }

//        if (GeneralTool.isEmpty(phonePsd) || phonePsd.length() < 6
//                || phonePsd.length() > 20) {
//            Utils.toast(getApplicationContext(), R.string.input_psd);
//            return;
//        }
        if (!isAgree) {
            Utils.toast(getApplicationContext(), "请同意注册协议");
        }
        RequestParams req = UrlTool.getPostParams("userId", UserService
                        .getInatance().getUserBean(this).getId(), "key", "mobile", "value",
                phone, "code", phoneCode, "oldPassword", "");
        SendActtionTool.post(UserParams.URL_USER_UPDATE,
                ServiceAction.Action_User, UserAction.Action_Regiser_Phone,
                this, req);

    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        LogUtils.d("onSuccess", value.toString());
        switch ((UserAction) action) {
            case Action_Regiser_Phone:
            case Action_Regiser_Email:
                Utils.toast(getApplicationContext(), " 绑定成功");
                userbean.setMobile(edtPhone.getText().toString());
                UserService.getInatance().setUserBean(userbean, this);
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

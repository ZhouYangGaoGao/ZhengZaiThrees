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
import com.modernsky.istv.manager.DavikActivityManager;
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
 * @author rendy 找回密码
 */
public class ResetPsdActivity extends BaseActivity {
    private TextView tetTitle, tetTime;
    private EditText edtPhone, edtCode, edtPhonePsd, edtEmail;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                finish();
                break;
            // 获取校验码
            case R.id.reset_codeBtn:
                if (isWaiteInputJiaoYan) {
                    return;
                }

                String mobile = edtPhone.getText().toString();
                if (!CheckCode.isMobileNO(mobile)) {
                    Utils.toast(getApplicationContext(), "请输入正确的手机号");
                    return;
                }
                tetTime.setEnabled(false);
                RequestParams request = UrlTool
                        .getParams(UserParams.Mobile, mobile);
                SendActtionTool.get(UserParams.URL_REGISTER_CODE_MODIFY, null,
                        UserAction.Action_Regiser_Phone_Code, this, request);
                break;
            // 手机找回密码
            case R.id.reset_phoneBtn:
                String phone = edtPhone.getText().toString();
                String phoneCode = edtCode.getText().toString();
                String phonePsd = edtPhonePsd.getText().toString();

                if (!CheckCode.isMobileNO(phone)) {
                    Utils.toast(getApplicationContext(), "请输入正确的手机号");
                    return;
                }

                if (GeneralTool.isEmpty(phoneCode) || phoneCode.length() != 6) {
                    Utils.toast(getApplicationContext(), "请输入正确的校验码");
                    return;
                }

                if (GeneralTool.isEmpty(phonePsd) || phonePsd.length() < 6
                        || phonePsd.length() > 20) {
                    Utils.toast(getApplicationContext(), R.string.input_psd);
                    return;
                }

                RequestParams phoneRequest = UrlTool.getParams(UserParams.Mobile,
                        phone, UserParams.PASSWORD, phonePsd, UserParams.CODE,
                        phoneCode);
                SendActtionTool.get(UserParams.URL_USER_UPDATE_Phone_Psd, null,
                        UserAction.Action_FindPsd_Phone, this, phoneRequest);
                showLoadingDialog();
                break;
            // 邮箱 找回密码
            case R.id.reset_emialBtn:
                String email = edtEmail.getText().toString();
                if (!CheckCode.isEmail(email)) {
                    Utils.toast(getApplicationContext(), "请输入正确的邮箱号");
                    return;
                }
                RequestParams emailRequest = UrlTool.getParams(UserParams.EMAIL,
                        email);
                SendActtionTool.get(UserParams.URL_USER_UPDATE_EMAIL_Psd, null,
                        UserAction.Action_FindPsd_EMAIL, this, emailRequest);
                showLoadingDialog();
                break;
            default:
                break;
        }
    }

    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_reset_psd);
    }

    @Override
    public void findViewById() {
        findViewById(R.id.backBtn).setOnClickListener(this);
        tetTime = (TextView) findViewById(R.id.reset_codeBtn);
        tetTime.setOnClickListener(this);
        findViewById(R.id.reset_phoneBtn).setOnClickListener(this);
        findViewById(R.id.reset_emialBtn).setOnClickListener(this);

        edtCode = (EditText) findViewById(R.id.rest_codeEdt);
        edtPhone = (EditText) findViewById(R.id.reset_phoneEdt);
        edtPhonePsd = (EditText) findViewById(R.id.reset_phonePsdEdt);
        edtEmail = (EditText) findViewById(R.id.reset_emailEdt);

        tetTitle = (TextView) findViewById(R.id.reset_titleTet);

        int type = getIntent().getIntExtra(ResetPsdActivity.class.getName(), 1);
        if (type == 1) {
            findViewById(R.id.reset_phoneArea).setVisibility(View.VISIBLE);
            findViewById(R.id.reset_emailArea).setVisibility(View.GONE);
            tetTitle.setText("手机重置密码");

        } else {
            findViewById(R.id.reset_phoneArea).setVisibility(View.GONE);
            findViewById(R.id.reset_emailArea).setVisibility(View.VISIBLE);
            tetTitle.setText("邮箱重置密码");

        }
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
                tetTime.setText(time
                        + getResources().getString(R.string._regetTime));
            }
        }

        ;
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
        if (timer != null) {
            timer.cancel();
            timer = null;
            time = 60;
        }
        tetTime.setText(R.string._getRegisterCode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopJishi();
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        switch ((UserAction) action) {
            // 得到验证码
            case Action_Regiser_Phone_Code:
                tetTime.setEnabled(true);
                startJishi();
                break;
            case Action_FindPsd_EMAIL:
                Utils.toast(getApplicationContext(), "请到邮箱修改密码");
                DavikActivityManager.getScreenManager().showTargetAty(
                        MainActivity.class.getName());
                break;
            case Action_FindPsd_Phone:
                stopJishi();
                Utils.toast(getApplicationContext(), "修改密码成功");
                DavikActivityManager.getScreenManager().showTargetAty(
                        MainActivity.class.getName());
                break;
            default:
                break;
        }
    }

    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {
        LogUtils.d("onFailevalue==" + value.toString());
        switch ((UserAction) action) {
            case Action_Regiser_Phone_Code:
                tetTime.setEnabled(true);
                break;
        }
        stopJishi();
        Utils.toast(getApplicationContext(), value.toString());
    }

    @Override
    public void onException(ServiceAction service, Object action, Object value) {
        switch ((UserAction) action) {
            case Action_Regiser_Phone_Code:
                tetTime.setEnabled(true);
                break;
        }
        LogUtils.d("onExceptionvalue==" + value.toString());
        super.onException(service, action, value);
    }

    @Override
    public void onFinish(ServiceAction service, Object action) {
        dismissDialog();
    }

}

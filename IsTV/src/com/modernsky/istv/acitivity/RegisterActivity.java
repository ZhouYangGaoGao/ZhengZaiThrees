package com.modernsky.istv.acitivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.lidroid.xutils.http.RequestParams;
import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.action.UserAction;
import com.modernsky.istv.bean.UserEntity;
import com.modernsky.istv.manager.DavikActivityManager;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.CheckCode;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.Constants.UserParams;
import com.modernsky.istv.utils.GeneralTool;
import com.modernsky.istv.utils.JsonUtils;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.ThreeAppParams;
import com.modernsky.istv.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author rendy
 *         <p/>
 *         设置注册界面
 */
public class RegisterActivity extends BaseActivity {
    private boolean isAgree;
    // 顶部指示器
    private TextView tetTime, tetTitle;
    private View register_registerBtn;
    // 顶部指示器
    private View areaPhone, areaEmail;
    private EditText edtPhone, edtEmail, edtCode, edtPsdPone, edtPsdEmial;
    // 1 手机注册 2 邮箱注册
    private int indexiId = 1;
    private View indView;
    private TextView eroCode, eroName, eroPhonePsd, eroEmailPsd, eroEamil;

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 执行注册
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
                tetTime.setEnabled(false);
                showLoadingDialog();
                break;
            case R.id.activity_xiyi:
                Intent intent = new Intent(this, WebActivity.class);
                intent.putExtra(Constants.URL, Constants.URL_AGREEMENT);
                intent.putExtra(Constants.TITLE, "《服务协议》");
                RegisterActivity.this.startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_register);
    }

    @Override
    public void findViewById() {
        indView = findViewById(R.id.indexView);
        register_registerBtn = findViewById(R.id.register_registerBtn);
        register_registerBtn.setOnClickListener(this);
        findViewById(R.id.activity_xiyi).setOnClickListener(this);
        tetTime = (TextView) findViewById(R.id.register_codeBtn);
        tetTime.setOnClickListener(this);
        tetTitle = (TextView) findViewById(R.id.register_titleTet);
        areaPhone = findViewById(R.id.register_phoneArea);
        areaEmail = findViewById(R.id.register_emailArea);
        // 找到指示器
        edtCode = (EditText) findViewById(R.id.register_codeEdt);
        edtEmail = (EditText) findViewById(R.id.register_emailEdt);
        edtPhone = (EditText) findViewById(R.id.register_phoneEdt);
        edtPsdEmial = (EditText) findViewById(R.id.register_psdEmailEdt);
        edtPsdPone = (EditText) findViewById(R.id.register_psdEdt);
        indexiId = getIntent().getIntExtra(RegisterActivity.class.getName(), 1);
        indexiId = 1;  //设置默认是手机注册
        String value = getIntent().getStringExtra(RegisterOneActivity.class.getName());
        if (indexiId == 2) {
            // 邮箱注册
            areaPhone.setVisibility(View.INVISIBLE);
            areaEmail.setVisibility(View.VISIBLE);
            tetTitle.setText(R.string.regiter_email);
            edtEmail.setText(value);
        } else {
            // 手机注册
            areaPhone.setVisibility(View.VISIBLE);
            areaEmail.setVisibility(View.INVISIBLE);
            tetTitle.setText(R.string.regiter_phone);
            edtPhone.setText(value);
        }
        //
        eroName = getView(R.id.erreorCountTet);
        eroCode = getView(R.id.register_codeEroTet);
        eroPhonePsd = getView(R.id.register_psdEroTet);
        eroEmailPsd = getView(R.id.eroEmailPsd);
        eroEamil = getView(R.id.erreorEmailcut);
        edtEmail.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkEmail(edtEmail.getText().toString());
                } else {
//                    setError(edtEmail, R.drawable.edit_count_select, -1, R.drawable.edit_line_select);
                    setError(edtEmail, R.drawable.edit_phone_select, -1, R.drawable.edit_line_select);
                    eroEamil.setVisibility(View.INVISIBLE);
                }
            }
        });
        edtCode.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkCode(edtCode.getText().toString());
                } else {
                    setError(edtCode, R.drawable.edit_code_select, -1, R.drawable.edit_line_select);
//                    setError(edtCode, R.drawable.edit_phone_select, -1, R.drawable.edit_line_select);
                    eroCode.setVisibility(View.INVISIBLE);
                }
            }
        });
        edtPhone.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkPhone(edtPhone.getText().toString());
                } else {
                    setError(edtPhone, R.drawable.edit_phone_select, -1, R.drawable.edit_line_select);
//                    setError(edtPhone, R.drawable.edit_count_select, -1, R.drawable.edit_line_select);
                    eroName.setVisibility(View.INVISIBLE);
                }
            }
        });
        edtPsdEmial.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkPsdEmail(edtPsdEmial.getText().toString());
                } else {
                    setError(edtPsdEmial, R.drawable.edit_psd_select, -1, R.drawable.edit_line_select);
                    eroEmailPsd.setVisibility(View.INVISIBLE);
                }
            }

        });
        edtPsdPone.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkPsdPone(edtPsdPone.getText().toString());
                } else {
                    setError(edtPsdPone, R.drawable.edit_psd_select, -1, R.drawable.edit_line_select);
                    eroPhonePsd.setVisibility(View.INVISIBLE);
                }
            }

        });

    }

    private void checkEmail(String s) {
        if (!TextUtils.isEmpty(s)) {
            if (CheckCode.isEmail(s)) {
                setError(edtEmail, R.drawable.edit_count_select, -1,
                        R.drawable.edit_line_select);
                eroEamil.setVisibility(View.INVISIBLE);
            } else {
                setError(edtEmail, R.drawable.edit_phone_select, R.drawable.icon_0_1_wrong, R.drawable.ling_0_1_wrong);
//                setError(edtEmail, R.drawable.edit_count_select, R.drawable.icon_0_1_wrong, R.drawable.ling_0_1_wrong);
                eroEamil.setVisibility(View.VISIBLE);
            }
        } else {
//            setError(edtEmail, R.drawable.edit_count_select, R.drawable.icon_0_1_wrong, R.drawable.ling_0_1_wrong);
            setError(edtEmail, R.drawable.edit_phone_select, R.drawable.icon_0_1_wrong, R.drawable.ling_0_1_wrong);
            eroEamil.setVisibility(View.VISIBLE);
        }
    }

    private void checkCode(String s) {
        if (!TextUtils.isEmpty(s)) {
            if (s.length() == 6) {
                setError(edtCode, R.drawable.edit_code_select, -1,
                        R.drawable.edit_line_select);
                eroCode.setVisibility(View.INVISIBLE);

            } else {
                setError(edtCode, R.drawable.edit_code_select,
                        R.drawable.icon_0_1_wrong, R.drawable.ling_0_1_wrong);
                eroCode.setVisibility(View.VISIBLE);
            }
        } else {
            setError(edtCode, R.drawable.edit_code_select,
                    R.drawable.icon_0_1_wrong, R.drawable.ling_0_1_wrong);
            eroCode.setVisibility(View.VISIBLE);
        }
    }

    private void checkPhone(String s) {
        if (!TextUtils.isEmpty(s)) {
            if (CheckCode.isMobileNO(s)) {
                setError(edtPhone, R.drawable.edit_phone_select, -1,
                        R.drawable.edit_line_select);
//                setError(edtPhone, R.drawable.edit_count_select, -1,
//                        R.drawable.edit_line_select);
                eroName.setVisibility(View.INVISIBLE);

            } else {
                setError(edtPhone, R.drawable.edit_phone_select,
                        R.drawable.icon_0_1_wrong, R.drawable.ling_0_1_wrong);
//                setError(edtPhone, R.drawable.edit_count_select,
//                        R.drawable.icon_0_1_wrong, R.drawable.ling_0_1_wrong);
                eroName.setVisibility(View.VISIBLE);
            }
        } else {
            setError(edtPhone, R.drawable.edit_phone_select,
                    R.drawable.icon_0_1_wrong, R.drawable.ling_0_1_wrong);
//            setError(edtPhone, R.drawable.edit_count_select,
//                    R.drawable.icon_0_1_wrong, R.drawable.ling_0_1_wrong);
            eroName.setVisibility(View.VISIBLE);
        }

    }

    private void checkPsdEmail(String s) {

        if (!TextUtils.isEmpty(s)) {
            if (s.length() <= 16 && s.length() >= 6) {
                setError(edtPsdEmial, R.drawable.edit_psd_select, -1,
                        R.drawable.edit_line_select);
                eroEmailPsd.setVisibility(View.INVISIBLE);

            } else {
                setError(edtPsdEmial, R.drawable.edit_psd_select,
                        R.drawable.icon_0_1_wrong, R.drawable.ling_0_1_wrong);
                eroEmailPsd.setVisibility(View.VISIBLE);
            }
        } else {
            setError(edtPsdEmial, R.drawable.edit_psd_select,
                    R.drawable.icon_0_1_wrong, R.drawable.ling_0_1_wrong);
            eroEmailPsd.setVisibility(View.VISIBLE);
        }

    }

    private void checkPsdPone(String s) {

        if (!TextUtils.isEmpty(s)) {
            if (s.length() <= 16 && s.length() >= 6) {
                setError(edtPsdPone, R.drawable.edit_psd_select, -1,
                        R.drawable.edit_line_select);
                eroPhonePsd.setVisibility(View.INVISIBLE);

            } else {
                setError(edtPsdPone, R.drawable.edit_psd_select,
                        R.drawable.icon_0_1_wrong, R.drawable.ling_0_1_wrong);
                eroPhonePsd.setVisibility(View.VISIBLE);
            }
        } else {
            setError(edtPsdPone, R.drawable.edit_psd_select,
                    R.drawable.icon_0_1_wrong, R.drawable.ling_0_1_wrong);
            eroPhonePsd.setVisibility(View.VISIBLE);
        }

    }

    /**
     * @param targetView
     * @param lt
     * @param rt
     * @param bm
     */
    private void setError(EditText targetView, int lt, int rt, int bm) {
        Drawable right = rt == -1 ? null : GeneralTool
                .setNearDrawable(rt, this);
        Drawable bottom = bm == -1 ? null : GeneralTool.setNearDrawable(bm,
                this);
        Drawable left = lt == -1 ? null : GeneralTool.setNearDrawable(lt, this);
        targetView.setCompoundDrawables(left, null, right, bottom);
        targetView.setCompoundDrawables(left, null, null, bottom);
    }

    public void backBtn(View view) {
        finish();
    }

    public void agreeBtn(View view) {
        if (isAgree) {
            isAgree = false;
            indView.setBackgroundResource(R.drawable.icon_08_unchoose);
        } else {
            isAgree = true;
            indView.setBackgroundResource(R.drawable.icon_08_choose);
        }
    }


    /**
     * 执行注册
     */
    private void sendRegister() {
        switch (indexiId) {
            // 执行手机注册
            case 1:
                String phone = edtPhone.getText().toString();
                String phoneCode = edtCode.getText().toString();
                String phonePsd = edtPsdPone.getText().toString();
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
                if (!isAgree) {
                    Utils.toast(getApplicationContext(), "请同意注册协议");
                    return;
                }
                showLoadingDialog();
                register_registerBtn.setEnabled(false);
                RequestParams request = UrlTool.getParams(UserParams.EMAIL_PHONE,
                        phone, UserParams.PASSWORD, phonePsd, Constants.STATUS,
                        UserParams.Mobile, UserParams.CODE, phoneCode);
                SendActtionTool.get(UserParams.URL_REGISTER, null,
                        UserAction.Action_Regiser_Phone, this, request);
                break;
            // 执行邮箱注册
            case 2:
                String email = edtEmail.getText().toString();
                String emailpsd = edtPsdEmial.getText().toString();
                if (!CheckCode.isEmail(email)) {
                    Utils.toast(getApplicationContext(), "请输入正确的邮箱");
                    return;
                }
                if (GeneralTool.isEmpty(emailpsd) || emailpsd.length() < 6 || emailpsd.length() > 20) {
                    Utils.toast(getApplicationContext(), R.string.input_psd);
                    return;
                }
                if (!isAgree) {
                    Utils.toast(getApplicationContext(), "请同意注册协议");
                    return;
                }
                showLoadingDialog();
                RequestParams emailReq = UrlTool.getParams(UserParams.EMAIL_PHONE,
                        email, //
                        UserParams.PASSWORD,//
                        emailpsd,//
                        Constants.STATUS,//
                        UserParams.EMAIL,//
                        UserParams.CODE,//
                        "0");
                SendActtionTool.get(UserParams.URL_REGISTER, null, UserAction.Action_Regiser_Email, this, emailReq);
                break;
            default:
                break;
        }
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        LogUtils.d("onSuccess", value.toString());
        switch ((UserAction) action) {
            case Action_Regiser_Phone:
            case Action_Regiser_Email:
                Utils.toast(getApplicationContext(), "注册成功");
                JSONObject obj = (JSONObject) value;
                try {

                    String datas = obj.getJSONObject(Constants.USER_ENTITY).toString();
                    UserService.getInatance().setUserBean(JsonUtils.parse(datas, UserEntity.class), this);
                    ThreeAppParams.isNeedToken = true;
                    sendPush();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                DavikActivityManager.getScreenManager().showTargetAty(
                        MainActivity.class.getName());
                break;
            case Action_Regiser_Phone_Code:
                startJishi();
                tetTime.setEnabled(true);
                break;
            default:
                break;
        }

    }

    @Override
    public void onFinish(ServiceAction service, Object action) {
        super.onFinish(service, action);
        if (action == UserAction.Action_Regiser_Phone) {
            register_registerBtn.setEnabled(true);
        }
        dismissDialog();
    }

    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {
        super.onFaile(service, action, value);
        LogUtils.d("onFaileonFaile");
        switch ((UserAction) action) {
            case Action_Regiser_Phone_Code:
                tetTime.setEnabled(true);
                Utils.toast(getApplicationContext(), "网络异常请稍后重试");
//			break;
            default:
                LogUtils.d("[RegisterActivity.onFaile]", (String) value);
                Utils.toast(getApplicationContext(), value.toString());
                break;
        }


    }

    @Override
    public void onException(ServiceAction service, Object action, Object value) {
        switch ((UserAction) action) {
            case Action_Regiser_Phone_Code:
                tetTime.setEnabled(true);
//			Utils.toast(getApplicationContext(), "网络异常请稍后重试");
//			break;
            default:
                LogUtils.d("[RegisterActivity.onFaile]", (String) value);
                Utils.toast(getApplicationContext(), value.toString());
                break;
        }

    }

    /**
     * 更新计时器
     */
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
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
    private int time = 60;

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
        dismissDialog();
    }

}

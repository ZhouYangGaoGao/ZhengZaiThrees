package com.modernsky.istv.acitivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.http.RequestParams;
import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.action.UserAction;
import com.modernsky.istv.bean.OpenInfoBean;
import com.modernsky.istv.bean.UserEntity;
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
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import main.java.cn.aigestudio.datepicker.utils.LogUtil;

/**
 * @author rendy 登录注册
 */
public class LoginActivity extends BaseActivity {
    // 创建
    private EditText edtName, edtPsd;
    private boolean index = true;
    private String uid;
    private OpenInfoBean openInfo;
    UMShareAPI mShareAPI;
    private ImageView img_deletephone,img_deletepsd;
    private View line_phone,line_psd;
    //    UMSocialService mController = UMServiceFactory
//            .getUMSocialService("com.umeng.share");
//    UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.login");
    private TextView eroCount, eroPsd;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 返回
            case R.id.login_backBtn:
                finish();
                break;
            // 返回
            case R.id.img_deletephone:
                edtName.setText("");
                edtName.requestFocus();
                break;
            // 返回
            case R.id.img_deletepsd:
                edtPsd.setText("");
                edtPsd.requestFocus();
                break;
            // 登录
            case R.id.login_loginBtn:
                String count = edtName.getText().toString();
                String psd = edtPsd.getText().toString();
//                if (!CheckCode.isEmail(count) && !CheckCode.isMobileNO(count)) {
                if (!CheckCode.isMobileNO(count)) {
                    Utils.toast(getApplicationContext(), "请正确输入用户名");
                    break;
                }
                if (GeneralTool.isEmpty(psd) || psd.length() < 6
                        || psd.length() > 20) {
                    Utils.toast(getApplicationContext(), R.string.input_psd);
                    return;
                }
                if (index) {
                    index = false;
                    RequestParams params = UrlTool
                            .getPostParams(UserParams.EMAIL_PHONE, count,
                                    UserParams.PASSWORD, psd);
                    SendActtionTool.post(UserParams.URL_REGISTER_LOGIN,
                            ServiceAction.Action_User, UserAction.Action_login,
                            this, params);
                    showLoadingDialog();
                }
                break;
            // 快速注册
            case R.id.login_fastRegsiterBtn:
                startActivity(new Intent(getApplicationContext(),
                        RegisterOneActivity.class));
                finish();
                break;
            // 手机短信 登录
            case R.id.login_shoujiduanxinBtn:
                Intent intent = new Intent(getApplication(), ResetPsdActivity.class);
                intent.putExtra(ResetPsdActivity.class.getName(), 1);
                startActivity(intent);

//                startActivity(new Intent(getApplicationContext(),
//                        FindPsdTypeActivity.class));
                break;
            // 点击清空帐号
            case R.id.login_countEdt:
                String name = edtName.getText().toString();
                if (!CheckCode.isEmail(name) && !CheckCode.isMobileNO(name)) {
                    edtName.setText(null);
                }
                break;
            // 点击清空密码
            case R.id.login_psdEdt:
                break;
            // 设置名字点击事件
            case R.id.login_qqBtn:
                login(SHARE_MEDIA.QQ);
                break;
            case R.id.login_weiboBtn:
                login(SHARE_MEDIA.SINA);
                break;
            case R.id.login_weixinBtn:
                getWeiXinInfo();
                break;
            default:
                break;
        }
    }

    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
        mShareAPI = UMShareAPI.get(this);
//        addQQPlatform();
    }

    @Override
    public void findViewById() {
        findViewById(R.id.login_backBtn).setOnClickListener(this);
        findViewById(R.id.login_loginBtn).setOnClickListener(this);
        findViewById(R.id.login_fastRegsiterBtn).setOnClickListener(this);
        findViewById(R.id.login_qqBtn).setOnClickListener(this);
        findViewById(R.id.login_weixinBtn).setOnClickListener(this);
        findViewById(R.id.login_weiboBtn).setOnClickListener(this);
        findViewById(R.id.login_shoujiduanxinBtn).setOnClickListener(this);
        line_phone=  findViewById(R.id.line_phone);
        line_psd=  findViewById(R.id.line_psd);
        img_deletephone= (ImageView) findViewById(R.id.img_deletephone);
        img_deletephone.setOnClickListener(this);
        img_deletepsd= (ImageView) findViewById(R.id.img_deletepsd);
        img_deletepsd.setOnClickListener(this);
        edtName = (EditText) findViewById(R.id.login_countEdt);
        edtName.setOnClickListener(this);
        edtPsd = (EditText) findViewById(R.id.login_psdEdt);
        openInfo = new OpenInfoBean();
        eroCount = getView(R.id.erreorCountTet);
        eroPsd = getView(R.id.erreorPsdTet);
        edtName.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String value = edtName.getText().toString();
                    checkPhoneNumOrEmail(value);
                } else {
                    img_deletephone.setImageResource(R.drawable.icon_delet);
                    line_phone.setBackgroundResource(R.drawable.edit_line_select);
//                    Drawable bottom = GeneralTool.setNearDrawable(
//                            R.drawable.edit_line_select, LoginActivity.this);
//                    Drawable left = GeneralTool.setNearDrawable(
//                            R.drawable.edit_phone_select, LoginActivity.this);
//                    edtName.setCompoundDrawables(left, null, null, bottom);
                    eroCount.setVisibility(View.INVISIBLE);
                }
            }
        });
        edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edtName.getText().toString().length() > 0) {
                    img_deletephone.setVisibility(View.VISIBLE);
                } else {
                    img_deletephone.setVisibility(View.GONE);
                }
            }
        });
        edtPsd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edtPsd.getText().toString().length() > 0) {
                    img_deletepsd.setVisibility(View.VISIBLE);
                } else {
                    img_deletepsd.setVisibility(View.GONE);
                }
            }
        });
        edtPsd.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String value = edtPsd.getText().toString();
                    checkPassWord(value);
                } else {
//                    setError(edtPsd, R.drawable.edit_psd_select, -1,
//                            R.drawable.edit_line_select);
                    img_deletepsd.setImageResource(R.drawable.icon_delet);
                    line_psd.setBackgroundResource(R.drawable.edit_line_select);
                    eroPsd.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void checkPassWord(String s) {
        if (!TextUtils.isEmpty(s)) {
            if (s.length() <= 16 && s.length() >= 6) {
//                setError(edtPsd, R.drawable.edit_psd_select, -1,
//                        R.drawable.edit_line_select);
                img_deletepsd.setImageResource(R.drawable.icon_delet);
                line_psd.setBackgroundResource(R.drawable.edit_line_select);
                eroPsd.setVisibility(View.INVISIBLE);

            } else {
                img_deletepsd.setImageResource(R.drawable.icon_0_1_wrong);
                line_psd.setBackgroundResource(R.drawable.ling_0_1_wrong);
//                setError(edtPsd, R.drawable.edit_psd_select,
//                        R.drawable.icon_0_1_wrong, R.drawable.ling_0_1_wrong);
                eroPsd.setVisibility(View.VISIBLE);
            }
        } else {
            img_deletepsd.setImageResource(R.drawable.icon_0_1_wrong);
            line_psd.setBackgroundResource(R.drawable.ling_0_1_wrong);
//            setError(edtPsd, R.drawable.edit_psd_select,
//                    R.drawable.icon_0_1_wrong, R.drawable.ling_0_1_wrong);
            eroPsd.setVisibility(View.VISIBLE);
        }
    }

    private void checkPhoneNumOrEmail(String value) {
        if (!CheckCode.isEmail(value) && !CheckCode.isMobileNO(value)) {
//            Drawable right = GeneralTool.setNearDrawable(
//                    R.drawable.icon_0_1_wrong, LoginActivity.this);
//            Drawable bottom = GeneralTool.setNearDrawable(
//                    R.drawable.ling_0_1_wrong, LoginActivity.this);
//            Drawable left = GeneralTool.setNearDrawable(
//                    R.drawable.edit_phone_select, LoginActivity.this);
//            edtName.setCompoundDrawables(left, null, right, bottom);
            line_phone.setBackgroundResource(R.drawable.ling_0_1_wrong);
            img_deletephone.setImageResource(R.drawable.icon_0_1_wrong);
            eroCount.setVisibility(View.VISIBLE);
            // 输入正确的帐号
        } else {
//            Drawable bottom = GeneralTool.setNearDrawable(
//                    R.drawable.edit_line_select, LoginActivity.this);
//            Drawable left = GeneralTool.setNearDrawable(
//                    R.drawable.edit_phone_select, LoginActivity.this);
//            edtName.setCompoundDrawables(left, null, null, bottom);
            img_deletephone.setImageResource(R.drawable.icon_delet);
            line_phone.setBackgroundResource(R.drawable.edit_line_select);
            eroCount.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * @param targetView
     * @param
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
    }


//    private void addQQPlatform() {
////        // // 添加QQ支持, 并且设置QQ分享内容的target url
////        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this,
////                ThreeAppParams.QQ_APP_ID, ThreeAppParams.QQ_APP_KEY);
////        qqSsoHandler.setTargetUrl("http://www.umeng.com");
////        qqSsoHandler.addToSocialSDK();
////        // 添加微信平台
////        UMWXHandler wxHandler = new UMWXHandler(this, ThreeAppParams.WX_APP_ID,
////                ThreeAppParams.WX_APP_KEY);
////        wxHandler.addToSocialSDK();
//
//    }

    /**
     * 授权。如果授权成功，则获取用户信息</br>
     */
    private void login(final SHARE_MEDIA platform) {
//        showLoadingDialog();
        mShareAPI.doOauthVerify(this, platform, new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                if (LoginActivity.this.isFinishing()) {
                    Toast.makeText(getApplicationContext(), "微信异常", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(getApplicationContext(), "授权成功", Toast.LENGTH_SHORT).show();
                uid = map.get("uid");
                LogUtils.d("map=" + map.toString() + "uid==" + uid);
                if (!TextUtils.isEmpty(uid)) {
                    getUserInfo(platform);
                } else {
                    Toast.makeText(LoginActivity.this, "授权失败...",
                            Toast.LENGTH_SHORT).show();
                    dismissDialog();
                }
                dismissDialog();
            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                Toast.makeText(getApplicationContext(), "授权失败..", Toast.LENGTH_SHORT).show();
                LogUtil.e("授权失败:" + throwable.toString());
                dismissDialog();
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {
                Toast.makeText(getApplicationContext(), "授权取消..", Toast.LENGTH_SHORT).show();
                LogUtil.e("授权取消:");
                dismissDialog();
            }
        });
    }

    /**
     * 获取授权平台的用户信息</br>
     */

    private void getUserInfo(final SHARE_MEDIA platform) {
//        showLoadingDialog();
        mShareAPI.getPlatformInfo(this, platform, new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
//                dismissDialog();
                if (map != null) {
                    LogUtils.t("getUseInfo", map.toString());
                    sendUserInfo(platform, map);
                }
            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
//                dismissDialog();
                LogUtil.e("用户信息:" + throwable.toString());
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {
//                dismissDialog();
                LogUtil.e("用户信息:  取消");
            }
        });
    }

    private void sendUserInfo(SHARE_MEDIA platform, Map<String, String> info) {
        LogUtils.d("sendUserInfo");
        String sex = null;
        String city = null;
        switch (platform) {
            case QQ:
                sex = info.get("gender") + "";
                city = info.get("city") + "";
                if (sex.equals("男")) {
                    sex = "1";
                } else {
                    sex = "0";
                }
                openInfo.setSourse("QQ");
                openInfo.setSex(sex);
                openInfo.setLocation(city);
                openInfo.setName(info.get("screen_name"));
                openInfo.setUserFace(info.get("profile_image_url"));
                openInfo.setOpenId(uid);

                break;
            case SINA:
                sex = info.get("gender") + "";
                city = info.get("location") + "";
                openInfo.setSourse("WB");
                openInfo.setSex(sex);
                openInfo.setLocation(city);
                openInfo.setName(info.get("screen_name"));
                openInfo.setUserFace(info.get("profile_image_url"));
                openInfo.setOpenId(uid);
                break;
            case WEIXIN:
                city = info.get("city");
                city = city == null ? "" : city;
                sex = info.get("sex");
                openInfo.setSourse("WX");
                uid = info.get("openid");
                openInfo.setOpenId(uid);
                openInfo.setSex(sex);
                openInfo.setLocation(city);
                openInfo.setName(info.get("nickname"));
                openInfo.setUserFace(info.get("headimgurl"));
                openInfo.setOpenId(info.get("openid"));
                break;
            default:
                break;
        }
        checkNeedUserInfo(uid, openInfo.getSourse());
    }

    /**
     * 得到微信信息
     */
    private void getWeiXinInfo() {
//        LogUtils.d("getWeiXinInfo");
        //
        mShareAPI.doOauthVerify(this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                Toast.makeText(LoginActivity.this, "授权完成", Toast.LENGTH_SHORT).show();
                LogUtils.d("getWeiXinInfo" + "授权完成" + "map==" + map.toString());
                //
                mShareAPI.getPlatformInfo(LoginActivity.this, share_media, new UMAuthListener() {
                    @Override
                    public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                        LogUtils.d("getWeiXinInfo getPlatformInfo+i=" + i + "map==" + map.toString());
                        if (i == 2 ) {
                            sendWinxininfo(map);
                        } else {
                            LogUtils.t("TestData", "发生错误：" + i);
                        }
//                        dismissDialog();
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                        LogUtils.e("getWeiXinInfo  获取信息失败" + throwable.toString());
//                        dismissDialog();
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media, int i) {
//                        dismissDialog();
                        LogUtils.e("getWeiXinInfo获取信息 onCancel");
                    }
                });

            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
//                dismissDialog();
                Toast.makeText(LoginActivity.this, "授权错误",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {
//                dismissDialog();
                Toast.makeText(LoginActivity.this, "授权取消",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * @param info 发送微信信息
     */
    private void sendWinxininfo(Map<String, String> info) {
        String city = info.get("city");
        city = city == null ? "" : city;
        String sex = info.get("sex").toString() + "";
        openInfo.setSourse("WX");
        uid = info.get("openid").toString();
        openInfo.setOpenId(uid);
        openInfo.setSex(sex);
        openInfo.setLocation(city.toString());
        openInfo.setName(info.get("nickname"));
        openInfo.setUserFace(info.get("headimgurl"));
        openInfo.setOpenId(info.get("openid"));
        checkNeedUserInfo(uid, openInfo.getSourse());
    }

    // protected void onActivityResult(int requestCode, int resultCode,
    // android.content.Intent data);
    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);
        if (arg2 != null) {
            mShareAPI.onActivityResult(arg0, arg1, arg2);
        }
        if (arg1 != RESULT_OK || arg2 == null) {
            return;
        }
        switch (arg0) {
            case 5:
                String value = arg2.getStringExtra(InputContectActivity.class
                        .getName());
                String[] cp = value.split("_");
                openInfo.setEmailOrPhone(cp[0]);
                openInfo.setPassword(cp[1]);
                sendOpenInfo();
                break;

            default:
                break;
        }
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        JSONObject obj = (JSONObject) value;
        switch ((UserAction) action) {
            case Action_OPEN_LOGIN:
            case Action_login:
                dismissDialog();
                LogUtils.t("value--", value.toString());
                Utils.toast(getApplicationContext(), "登录成功");
                try {
                    String datas = obj.getJSONObject(Constants.USER_ENTITY)
                            .toString();
                    UserEntity user = JsonUtils.parse(datas, UserEntity.class);
                    UserService.getInatance().setUserBean(user, this);
                    Utils.sendBroadcastToMainactivityToUpdateUserinfo(this);
                    LogUtils.t("_______", user.getRank().getName());
                } catch (JSONException e1) {
                    e1.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ThreeAppParams.isNeedToken = true;
                sendPush();

                // 登录广播
                Intent intent = new Intent();
                intent.setAction(Constants.ACTION_LOGIN_CHANGE);
                sendBroadcast(intent);
                finish();
                break;
            case Action_Push_Acton:

                break;
            case Action_OPEN_CHECK:
                try {
                    // 第一次绑定，输入手机号
                    if ("1".equals(obj.getJSONObject(Constants.DATA).getString("isNeed"))) {
                        dismissDialog();
                        startActivityForResult(new Intent(this, InputContectActivity.class), 5);
                    }
                    // 已经绑定过，有手机号
                    else {
                        sendOpenInfo();
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                break;
            default:
                break;
        }

    }

    /**
     * @param openid
     * @param source 检测需不需要登录
     */
    private void checkNeedUserInfo(String openid, String source) {
        LogUtils.d("checkNeedUserInfo" + "openid==" + openid + "source==" + source);
        showLoadingDialog();
        SendActtionTool.get(UserParams.URL_OPEN_Check,
                ServiceAction.Action_User, UserAction.Action_OPEN_CHECK, this,
                UrlTool.getParams(UserParams.openId, openid, UserParams.SOURCE,
                        source));
    }

    /**
     * 登录数据信息
     *
     * @param
     */
    private void sendOpenInfo() {
        SendActtionTool.post(UserParams.URL_OPEN_LOGIN,
                ServiceAction.Action_User, //
                UserAction.Action_OPEN_LOGIN,//
                this,
                UrlTool.getPostParams(
                        UserParams.openId,
                        openInfo.getOpenId(),
                        UserParams.openName,
                        openInfo.getName(),
                        UserParams.faceUrl,
                        openInfo.getUserFace(),
                        UserParams.sex,
                        openInfo.getSex(),
                        UserParams.location,
                        openInfo.getLocation(),
                        UserParams.SOURCE,
                        openInfo.getSourse(),
                        UserParams.EMAIL_PHONE,
                        openInfo.getEmailOrPhone() == null ? "" : openInfo
                                .getEmailOrPhone(), UserParams.PASSWORD,
                        openInfo.getPassword()));
        showLoadingDialog();
    }

    @Override
    public void onFinish(ServiceAction service, Object action) {
        dismissDialog();
        index = true;
        super.onFinish(service, action);

    }

    @Override
    public void
    onFaile(ServiceAction service, Object action, Object value) {
        LogUtils.t("Login.onFinish()_", value.toString());
        switch ((UserAction) action) {
            case Action_Push_Acton:
                return;
            case Action_OPEN_LOGIN:
                openInfo.setEmailOrPhone("");
                openInfo.setPassword("");
                break;
            default:
                break;
        }
        Utils.toast(getApplicationContext(), value.toString());
    }

    @Override
    public void onException(ServiceAction service, Object action, Object value) {
        Utils.toast(getApplicationContext(), value.toString());
    }
}

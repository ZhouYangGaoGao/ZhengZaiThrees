package com.modernsky.istv;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.modernsky.istv.acitivity.MainActivity;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.action.UserAction;
import com.modernsky.istv.bean.UserEntity;
import com.modernsky.istv.fragment.BackHandledFragment;
import com.modernsky.istv.listener.CommonListener;
import com.modernsky.istv.manager.BaseApplication;
import com.modernsky.istv.manager.DavikActivityManager;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.DialogTool;
import com.modernsky.istv.tool.NetworkHelper;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.Constants.UserParams;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.ThreeAppParams;
import com.modernsky.istv.utils.Utils;
import com.modernsky.istv.view.LodingDialog;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.IUmengUnregisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;

import java.lang.ref.WeakReference;

/**
 * @author rendy
 *         <p/>
 *         界面 base
 */
public abstract class BaseActivity extends FragmentActivity implements
        CommonListener, OnClickListener, BackHandledFragment.BackHandlerInterface {

    private LodingDialog lodingDialog;
    private BackHandledFragment selectedFragment;
    private Handler mHandler = new MyHandler(this);
    private String currentDialogMsg="";
    private class MyHandler extends Handler {
        private final WeakReference<BaseActivity> mActivity;


        MyHandler(BaseActivity act) {
            mActivity = new WeakReference<BaseActivity>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mActivity.get() == null) {
                return;
            }
            switch (msg.what) {
                case 0:
                    String meg2 = (String) msg.getData().get("msg");
                    if (lodingDialog == null||!currentDialogMsg.equals(meg2)) {
                        Activity activity = DavikActivityManager.getScreenManager().currentActivity();
                        while (activity.getParent() != null) {
                            activity = activity.getParent();
                        }
                        currentDialogMsg=meg2;
                        lodingDialog = DialogTool.createLoadingDialog(activity, meg2);
                    }
                    if (lodingDialog.isShowing()) {
                        return;
                    } else {
                        lodingDialog.show();
                    }
                    break;
                case 1:
                    if (lodingDialog == null) {
                        return;
                    }
                    if (lodingDialog.isShowing()) {
                        lodingDialog.dismiss();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    }

    /**
     * 友盟推送
     **/
    private PushAgent mPushAgent = null;
//    private String device_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogUtils.t("BaseActivity", getClass().getSimpleName());
        DavikActivityManager.getScreenManager().pushActivity(this);
        initUM();
        setContentView(savedInstanceState);
//        if (savedInstanceState == null)
//        setupWindowAnimations();
        findViewById();
        checkNetWork();
        String device_token = UmengRegistrar.getRegistrationId(getBaseContext());
        LogUtils.d("device_token----" + device_token);
//        if (MainActivity.class.getSimpleName().equals(getClass().getSimpleName()))
    }

    private void checkNetWork() {
        if (!NetworkHelper.isNetworkConnected(this)) {
            Utils.toast(this, "未检测到网络，请设置网络。");
        }
    }
//    private void setupWindowAnimations() {
//        Slide slide = null;
//        Fade fade = null;
//        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
//            return;
//        }
//        slide = (Slide) TransitionInflater.from(this).inflateTransition(R.transition.activity_slide);
//        fade = (Fade) TransitionInflater.from(this).inflateTransition(R.transition.activity_fade);
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            return;
//        }
//        LogUtils.d("xqp", Build.VERSION.SDK_INT + "");
//        getWindow().setExitTransition(slide);
//        getWindow().setEnterTransition(slide);
//        getWindow().setReturnTransition(slide);
//    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
//        sendPush();
    }

    /**
     * 初始化友盟推送
     */
    protected void initUM() {
        LogUtils.d("MainActivityname-----" + MainActivity.class.getName());
        LogUtils.d("getSimpleName-----" + getClass().getSimpleName());
        if (!getClass().getSimpleName().equals(MainActivity.class.getSimpleName())) {
            return;
        }
        if (ThreeAppParams.umToken == null) {
            mPushAgent = BaseApplication.mPushAgent;
            if (mPushAgent != null) {
                mPushAgent.onAppStart();
                mPushAgent.enable(mRegisterCallback);
            } else {
                LogUtils.e("mPushAgent == null");
            }
            // TODO
//            device_token = UmengRegistrar.getRegistrationId(getBaseContext());
        } else {
        }
    }

    /**
     * 关闭友盟推送
     */
    protected void closePush() {
        if (mPushAgent.isEnabled() || UmengRegistrar.isRegistered(this)) {
            mPushAgent.disable(mUnregisterCallback);
        }
    }

    /**
     * 打开友盟推送
     */
    protected void startPush() {
        mPushAgent.enable(mRegisterCallback);
    }

    /**
     * 开启友盟推送
     */
    public IUmengRegisterCallback mRegisterCallback = new IUmengRegisterCallback() {
        @Override
        public void onRegistered(String registrationId) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    ThreeAppParams.umToken = mPushAgent.getRegistrationId();
                    LogUtils.t("---mRegisterCallback-----", "执行");
                    ThreeAppParams.umToken = UmengRegistrar.getRegistrationId(BaseActivity.this);
                    updateStatus();
                }
            });
        }
    };

    /**
     * 注册推送到服务器
     */
    protected void sendPush() {
        ThreeAppParams.umToken = UmengRegistrar.getRegistrationId(this);
        if (ThreeAppParams.isNeedToken && ThreeAppParams.umToken != null) {
            UserEntity bean = UserService.getInatance().getUserBean(this);
            if (bean != null && !TextUtils.isEmpty(ThreeAppParams.umToken)) {
                LogUtils.t("BaseActivity.sendPush()", ThreeAppParams.umToken);
                SendActtionTool.post(UserParams.URL_ADD_PUSH_TOKEN,
                        ServiceAction.Action_User,
                        UserAction.Action_Push_Acton, this, UrlTool.getParams(
                                Constants.USER_ID, bean.getId(), "source",
                                "android", "token", ThreeAppParams.umToken));
            }
        }
    }

    /**
     * 推出自己的服务器推送
     */
    protected void outPush() {
        UserEntity bean = UserService.getInatance().getUserBean(this);
        if (bean != null) {
            LogUtils.t("BaseActivity.outPush()", ThreeAppParams.umToken);
            SendActtionTool.post(UserParams.URL_ADD_PUSH_TOKEN,
                    ServiceAction.Action_User,
                    UserAction.Action_Push_Acton_Out, this, UrlTool.getParams(
                            UserParams.USER_ID, bean.getId(), "source",
                            "android", "token", ""));
        }
    }

    /**
     * 关闭友盟推送
     */
    private IUmengUnregisterCallback mUnregisterCallback = new IUmengUnregisterCallback() {

        @Override
        public void onUnregistered(String registrationId) {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    updateStatus();
                }
            });
        }
    };

    /**
     * 更新推送状态
     */
    private void updateStatus() {
        String info = String.format(
                "enabled:%s  isRegistered:%s  DeviceToken:%s",
                mPushAgent.isEnabled(), mPushAgent.isRegistered(),
                mPushAgent.getRegistrationId());
        LogUtils.t("BaseActivity.updateStatus()_更新友盟推送", info);
    }

    public abstract void setContentView(Bundle savedInstanceState);

    public abstract void findViewById();

    /**
     * 展示加加载信息等待 dialog
     */
    public void showLoadingDialog() {
        showLoadingDialog("");
    }

    /**
     * 展示loading弹窗，并提示相应信息
     *
     * @param msg
     */
    public void showLoadingDialog(String msg) {
        Message msg2 = new Message();
        msg2.what = 0;
        Bundle data = new Bundle();
        data.putString("msg", msg);
        msg2.setData(data);
        mHandler.sendMessage(msg2);

    }

    /**
     * 隐藏加载信息 dialog
     */
    public void dismissDialog() {
        mHandler.sendEmptyMessage(1);
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        if (action != null && value != null)
            LogUtils.t(this.getLocalClassName() + "--onSuccess--action:" + action.toString(), value.toString());
        else if (service != null && value != null)
            LogUtils.t(this.getLocalClassName() + "--onSuccess--searvice:" + service.toString(), value.toString());
        else if (value != null) {
            LogUtils.t(this.getLocalClassName() + "--onSuccess", value.toString());
        }

    }


    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {
        if (action != null && value != null)
            LogUtils.t(this.getLocalClassName() + "--onFaile--action:" + action.toString(), value.toString());
        else if (service != null && value != null)
            LogUtils.t(this.getLocalClassName() + "--onFaile--searvice:" + service.toString(), value.toString());
        else if (value != null) {
            LogUtils.t(this.getLocalClassName() + "--onFaile", value.toString());
        }
    }

    @Override
    public void onException(ServiceAction service, Object action, Object value) {
        if (action != null && value != null)
            LogUtils.t(this.getLocalClassName() + "--onException--action:" + action.toString(), value.toString());
        else if (service != null && value != null)
            LogUtils.t(this.getLocalClassName() + "--onException--searvice:" + service.toString(), value.toString());
        else
            LogUtils.t(this.getLocalClassName() + "--onException", value.toString());
//        Toast.makeText(getApplicationContext(), "接收数据异常", Toast.LENGTH_SHORT);
    }

    @Override
    public void onStart(ServiceAction service, Object action) {

    }

    @Override
    public void onFinish(ServiceAction service, Object action) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        dismissDialog();
        DavikActivityManager.getScreenManager().popActivity(this);
        super.onDestroy();
    }

    protected <T extends View> T getView(int viewId) {
        View view = findViewById(viewId);
        return (T) view;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void setSelectedFragment(BackHandledFragment backHandledFragment) {
        this.selectedFragment = selectedFragment;
    }
}

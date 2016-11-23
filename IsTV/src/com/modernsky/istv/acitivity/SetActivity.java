package com.modernsky.istv.acitivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RemoteViews;
import android.widget.Switch;
import android.widget.TextView;

import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.action.UserAction;
import com.modernsky.istv.manager.BaseApplication;
import com.modernsky.istv.manager.DavikActivityManager;
import com.modernsky.istv.nmpush.NotificationService;
import com.modernsky.istv.service.DianTaiService;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.DataClearTool;
import com.modernsky.istv.tool.DialogTool;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.FileUtils;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.PreferencesUtils;
import com.modernsky.istv.utils.Utils;
import com.modernsky.istv.utils.WeakHandler;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author rendy 设置界面
 */
public class SetActivity extends BaseActivity {
   private TextView checkVersion;
    private String onlineVersion="";
    private String onLineInfo="";
    private String onlineUrl="";
    private  TextView tv_newversion,tv_chche;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 退出登录
            case R.id.set_outBtn:
                UserService.getInatance().setUserBean(null, this);
                outPush();
                // 登出广播
                Intent intent = new Intent();
                intent.setAction(Constants.ACTION_LOGIN_CHANGE);
                sendBroadcast(intent);
                DavikActivityManager.getScreenManager().showTargetAty(
                        MainActivity.class.getName());
                break;
            // 返回
            case R.id.img_live:
                finish();
                break;
            // 绑定登录
            case R.id.set_BtnBangdingSHare:
                if (UserService.getInatance().isNeedLogin(SetActivity.this)) {
                    DialogTool.createToLoginDialog(this);
//				startActivity(new Intent(getApplicationContext(),
//						LoginActivity.class));
                } else {
                    startActivity(new Intent(getApplicationContext(),
                            BindingActivity.class));
                }

                break;
            // 清除缓存
            case R.id.set_BtnclearMemery:
                DataClearTool.cleanExternalCache(this);
                LogUtils.d("afterclearexporcachesize==="+FileUtils.getAutoFileOrFilesSize(this.getExternalCacheDir().getAbsolutePath()));
                Utils.toast(this,"清除缓存成功");
//                tv_chche.setText(FileUtils.FormetFileSize(FileUtils.getAutoFileOrFilesSize(this.getExternalCacheDir().getAbsolutePath())));
                tv_chche.setText("0M");
                break;
            // 检查版本
            case R.id.set_version:
                checkVersionUpdate();

                break;
            // 管理账号
            case R.id.set_BtnManaCOunt:
                if (UserService.getInatance().isNeedLogin(SetActivity.this)) {
                    DialogTool.createToLoginDialog(this);
//				startActivity(new Intent(getApplicationContext(),
//						LoginActivity.class));
                } else {
                    startActivity(new Intent(getApplicationContext(),
                            ClienUserActivity.class));
                }
                break;
            default:
                break;
        }
    }
    private void checkVersionUpdate() {
        if (!BaseApplication.mVersionName.equals(onlineVersion)) {
            LogUtils.t("版本更新", "开始更新");
            initNotification(onlineUrl,onLineInfo,onlineVersion);
//            DialogTool.createCheckDialog(this, onlineUrl,onLineInfo,onlineVersion,null);
        } else {
            Utils.toast(this,"已是最新版本");
            LogUtils.t("版本更新", "不用更新");
        }
    }
    private RemoteViews view = null;
    private Notification notification;
    private NotificationManager manager = null;
    private Intent notifiintent = null;
    private PendingIntent pIntent = null;//更新显示
    WeakHandler handler =new WeakHandler(this) {
        @Override
        public void conventHandleMessage(Message msg) {
            switch (msg.what) {
                case  0:
                    LogUtils.d("msg.arg1===="+msg.arg1);
                    if (msg.arg1 >= 100) {
                        view.setTextViewText(R.id.tv, "下载完成");
                        manager.cancel(1);
                        return;
                    } else {
                        view.setTextViewText(R.id.tv, "Download:" + msg.arg1 + "%");
                    }
                    view.setProgressBar(R.id.pb, 100, msg.arg1, false);

                    /**
                     * 给Notification设置布局。
                     */
                    notification.contentView = view;

                    /**
                     * 给Notification设置Intent，单击Notification会发出这个Intent。
                     */
                    notification.contentIntent = pIntent;
                    /**
                     * 发送Notification提醒。
                     */
                    manager.notify(1, notification);

                    break;
                case  1:
                    break;
                case  2:
                    break;
            }
        }
    };
    public void initNotification(String url,String versioninfo,String version) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this);
        view = new RemoteViews(
                this.getPackageName(),
                R.layout.notification_progress_layout);
        view.setImageViewResource(R.id.image, R.drawable.icon);

        builder.setContent(view);
        builder.setSmallIcon(R.drawable.icon);
        builder.setAutoCancel(true);
        notification = builder.build();

        manager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);

//         notifiintent = new Intent();
//        notifiintent.setAction(Intent.ACTION_VIEW);
//        notifiintent.setData(Uri.parse("http://www.baidu.com"));
        notifiintent = new Intent(this, NotificationService.class);
        pIntent = PendingIntent.getService(this, 0, notifiintent, 0);
        DialogTool.createCheckDialog(this, url, versioninfo, version,handler);

    }



    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_set);
    }

    @Override
    public void findViewById() {
        if (UserService.getInatance().isNeedLogin(this)) {
            findViewById(R.id.set_outBtn).setVisibility(View.GONE);
        } else {
            findViewById(R.id.set_outBtn).setOnClickListener(this);
        }
        checkVersion= (TextView) findViewById(R.id.tv_version);
        checkVersion.setText("v "+BaseApplication.mVersionName);
        tv_newversion= (TextView) findViewById(R.id.tv_newversion);
        tv_chche= (TextView) findViewById(R.id.tv_chche);
        tv_chche.setText(FileUtils.FormetFileSize(FileUtils.getAutoFileOrFilesSize(this.getExternalCacheDir().getAbsolutePath())));
            LogUtils.d("exporcachesize==="+FileUtils.getAutoFileOrFilesSize(this.getExternalCacheDir().getAbsolutePath()));
        findViewById(R.id.set_version).setOnClickListener(this);
        findViewById(R.id.img_live).setOnClickListener(this);
        findViewById(R.id.set_BtnBangdingSHare).setOnClickListener(this);
        findViewById(R.id.set_BtnclearMemery).setOnClickListener(this);
        findViewById(R.id.set_BtnManaCOunt).setOnClickListener(this);

        Switch set_DianTai = (Switch) findViewById(R.id.set_BtnMusicSet);

        set_DianTai.setChecked(PreferencesUtils.getBooleanPreferences(this,
                PreferencesUtils.TYPE_DIANTAI_TOGLE));
        set_DianTai.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferencesUtils.saveBooleanPreferences(SetActivity.this,
                        PreferencesUtils.TYPE_DIANTAI_TOGLE, isChecked);
            }

        });

        Switch slidButton = (Switch) findViewById(R.id.set_BtnNetWarn);
        slidButton.setChecked(PreferencesUtils.getBooleanPreferences(this,
                PreferencesUtils.TYPE_NET_TOGLE));
        slidButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferencesUtils.saveBooleanPreferences(SetActivity.this,
                        PreferencesUtils.TYPE_NET_TOGLE, isChecked);
                DianTaiService.getInstance().setShouldPlayInYiDong(isChecked);
            }

        });

        Switch set_ImgpinglunImg = (Switch) findViewById(R.id.set_ImgpinglunImg);
        set_ImgpinglunImg.setChecked(PreferencesUtils.getBooleanDefultTrue(
                this, PreferencesUtils.TYPE_PINGLUN_TOGLE));
        set_ImgpinglunImg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferencesUtils.saveBooleanPreferences(SetActivity.this,
                        PreferencesUtils.TYPE_PINGLUN_TOGLE, isChecked);
                if (isChecked) {
                    if (BaseApplication.mPushAgent != null) {
                        BaseApplication.mPushAgent.enable();
                    }
                } else {
                    if (BaseApplication.mPushAgent != null) {
                        BaseApplication.mPushAgent.disable();
                    }
                }
            }


        });
        Switch set_QXD = (Switch) findViewById(R.id.set_QXD);
        set_QXD.setChecked(PreferencesUtils.getBooleanPreferences(this,
                PreferencesUtils.TYPE_DEFAULT_QXD));

        set_QXD.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferencesUtils.saveBooleanPreferences(SetActivity.this,
                        PreferencesUtils.TYPE_DEFAULT_QXD, isChecked);
            }
        });

        SendActtionTool.get(Constants.UserParams.URL_Check_VERSION, ServiceAction.Action_User,//
                UserAction.Action_CHECK_VERSION, this);
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        switch ((UserAction)action) {
            case Action_CHECK_VERSION:
                JSONObject object = (JSONObject) value;
                try {
                    JSONObject data = object.getJSONObject("data");
//                    JSONObject sensitive = data.getJSONObject("sensitive");
//                    codeVersion = sensitive.getString("version");
//                    if (!codeVersion.equals(PreferencesUtils.getPreferences(this, PreferencesUtils.TYPE_Check_Verson))) {
//                        String url = sensitive.getString("url");
//                        SendActtionTool.post(url, ServiceAction.Action_User, UserAction.Action_CHECK_CODE_VERSION, this);
//                    }
                    JSONObject object2 = data.getJSONObject("android");
                    onlineVersion = object2.getString("version");
                    onlineUrl = object2.getString("url");
                     onLineInfo = "";
                    if (object2.has("msg")) {
                        onLineInfo = object2.getString("msg");
                    }
                    LogUtils.t("版本更新", onlineVersion + "_" + BaseApplication.mVersionName);
                    if (!onlineVersion.equals(BaseApplication.mVersionName)) {
                        tv_newversion.setVisibility(View.VISIBLE);
                    } else {
                        tv_newversion.setVisibility(View.GONE);
                        LogUtils.t("版本更新", "不用更新");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    LogUtils.t("版本更新", e.toString());
                }
                LogUtils.t("版本更新", "更新数据");
                break;
            default:
                break;
        }
    }

}

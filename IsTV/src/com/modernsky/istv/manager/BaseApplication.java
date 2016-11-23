package com.modernsky.istv.manager;

import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.aliyun.mbaas.oss.OSSClient;
import com.aliyun.mbaas.oss.model.AccessControlList;
import com.aliyun.mbaas.oss.model.TokenGenerator;
import com.aliyun.mbaas.oss.util.OSSToolKit;
import com.aliyun.mbaas.tools.MbaasLog;
import com.modernsky.istv.R;
import com.modernsky.istv.acitivity.MainActivity;
import com.modernsky.istv.acitivity.SplashActivity;
import com.modernsky.istv.exception.YxException;
import com.modernsky.istv.tool.BitmapTool;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.PreferencesUtils;
import com.modernsky.istv.utils.ThreeAppParams;
import com.modernsky.istv.utils.Utils;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import com.umeng.socialize.PlatformConfig;

import java.util.Map;

import io.rong.imlib.RongIMClient;

/**
 * @author rendy 软件设备 信息 ad68765212
 */
public class BaseApplication extends Application {
    public static int mNetWorkState; // 网络状态
    public static int mVersionCode; // 版本号
    public static String mVersionName = "3.3.1"; // 版本名称
    public static String jpushId = "";
    public static String xGTokend = "";
    public static DavikActivityManager activityManager = null;
    public static PushAgent mPushAgent;
    public static UMessage uMessage = null;
    // 阿里 图片上传 key

    static {
        OSSClient.setGlobalDefaultTokenGenerator(new TokenGenerator() {// 设置全局默认加签器
            @Override
            public String generateToken(String httpMethod, String md5,
                                        String type, String date, String ossHeaders,
                                        String resource) {
                String content = httpMethod + "\n" + md5 + "\n" + type
                        + "\n" + date + "\n" + ossHeaders + resource;
                return OSSToolKit.generateToken(
                        ThreeAppParams.ALI_accessKey,
                        ThreeAppParams.ALI_screctKey, content);
            }
        });
        OSSClient.setGlobalDefaultACL(AccessControlList.PUBLIC_READ); // 设置全局默认bucket访问权限
    }

    public static RefWatcher getRefWatcher(Context context) {
        BaseApplication application = (BaseApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    private RefWatcher refWatcher;

    protected RefWatcher installLeakCanary() {
        return RefWatcher.DISABLED;
    }

    @Override
    public void onCreate() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            MultiDex.install(this);// 解决 友盟分析问题
        }
        super.onCreate();
        initoncreatePost();
//        if (LogUtils.showLog)
//            refWatcher = LeakCanary.install(this);
//        else
//            refWatcher = installLeakCanary();
//
//        initLocalVersion();
//        activityManager = DavikActivityManager.getScreenManager();
//        BitmapTool.getInstance().initAdapterUitl(getApplicationContext());
//        // 初始化 阿里图片上穿
//        MbaasLog.enableLog(true);
//        OSSClient.setApplicationContext(getApplicationContext()); // 传入应用程序context
//        if (!LogUtils.showLog) {// 正式版本打开
//            // 异常监听
//            YxException crashHandler = YxException.getInstance();
//            // 注册crashHandler
//            crashHandler.init(getApplicationContext());
//        }
//        initUmengtMessage();
//        if ("com.modernsky.istv"
//                .equals(getCurProcessName(getApplicationContext()))
//                || "io.rong.push"
//                .equals(getCurProcessName(getApplicationContext()))) {
//            RongIMClient.init(this);
//            RongIMClient.getInstance();
//        }
//
//        PreferencesUtils.mContext = this;
//        initUMShare();
    }

    private void initoncreatePost() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (LogUtils.showLog)
                    refWatcher = LeakCanary.install(BaseApplication.this);
                else
                    refWatcher = installLeakCanary();

                initLocalVersion();
                activityManager = DavikActivityManager.getScreenManager();
                BitmapTool.getInstance().initAdapterUitl(getApplicationContext());
                // 初始化 阿里图片上穿
                MbaasLog.enableLog(true);
                OSSClient.setApplicationContext(getApplicationContext()); // 传入应用程序context
                if (!LogUtils.showLog) {// 正式版本打开
                    // 异常监听
                    YxException crashHandler = YxException.getInstance();
                    // 注册crashHandler
                    crashHandler.init(getApplicationContext());
                }
                initUmengtMessage();
                if ("com.modernsky.istv"
                        .equals(getCurProcessName(getApplicationContext()))
                        || "io.rong.push"
                        .equals(getCurProcessName(getApplicationContext()))) {
                    RongIMClient.init(BaseApplication.this);
                    RongIMClient.getInstance();
                }

                PreferencesUtils.mContext = BaseApplication.this;
                initUMShare();
            }
        });

    }

    private void initUMShare() {
        // // 添加QQ支持, 并且设置QQ分享内容的target url
        PlatformConfig.setWeixin(ThreeAppParams.WX_APP_ID, ThreeAppParams.WX_APP_KEY);
        //新浪微博 appkey appsecret
        PlatformConfig.setQQZone(ThreeAppParams.QQ_APP_ID, ThreeAppParams.QQ_APP_KEY);
        PlatformConfig.setSinaWeibo(ThreeAppParams.WEIBO_APP_KEY, ThreeAppParams.WEIBO_APP_SECRET);
    }

    /**
     * 获得当前进程的名字
     *
     * @param context
     * @return 进程号
     */
    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return "";
    }

    /**
     * 初始化友盟 推送
     */
    private void initUmengtMessage() {
        mPushAgent = PushAgent.getInstance(this);
        // 正式调试之后
        mPushAgent.setDebugMode(false);
        /**
         * 该Handler是在IntentService中被调用，故 1.
         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK 2.
         * IntentService里的onHandleIntent方法是并不处于主线程中，因此，如果需调用到主线程，需如下所示;
         * 或者可以直接启动Service
         * */
        UmengMessageHandler messageHandler = new UmengMessageHandler() {
            @Override
            public void dealWithCustomMessage(final Context context,
                                              final UMessage msg) {

                LogUtils.t("dealWithCustomMessage()",
                        msg.extra == null ? "map集合为空" : msg.extra.toString()
                                + "___自定义消息" + msg.custom);
                Thread ru = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        UTrack.getInstance(getApplicationContext())
                                .trackMsgClick(msg);
                        Map<String, String> data = msg.extra;
                        if (data != null) {
                            String type = data.get("type");
                            String tag = data.get("tag");
                            if (type == null || tag == null) {
                                return;
                            }
                            try {
                                if (type.equals("add_tag")) {
                                    mPushAgent.getTagManager().add(tag);
                                    LogUtils.t(
                                            "推送的消________dealWithCustomMessage()",
                                            "添加tag" + tag);
                                } else if (type.equals("del_tag")) {
                                    mPushAgent.getTagManager().delete(tag);
                                    LogUtils.t(
                                            "推送的消息________dealWithCustomMessage()",
                                            "删除tag" + tag);
                                }
                                // TODO
                                else if (type.equals("update_tag")) {
                                    mPushAgent.getTagManager().update(tag);
                                    LogUtils.t(
                                            "推送的消息________dealWithCustomMessage()",
                                            "更新tag" + tag);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                LogUtils.t("出现异常" + "dealWithCustomMessage",
                                        e.toString());

                            }
                        } else {
                            LogUtils.t("出现异常" + "dealWithCustomMessage", "null");

                        }
                    }
                });
                ru.start();
            }


            @Override
            public Notification getNotification(Context context, UMessage msg) {
                LogUtils.t("推送的消息___1_getNotification()",
                        msg.extra == null ? "map集合为空" : msg.extra.toString()
                                + "___自定义消息" + msg.custom);
                PreferencesUtils.saveLongPreferences(context, PreferencesUtils.TYPE_UMENG_PUSH, System.currentTimeMillis());
                uMessage = msg;
                LogUtils.d("msg.builder_id----" + msg.builder_id);
                switch (msg.builder_id) {
                    case 1:
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                                context);
                        RemoteViews myNotificationView = new RemoteViews(
                                context.getPackageName(),
                                R.layout.notification_view);
                        myNotificationView.setTextViewText(R.id.notification_title,
                                msg.title);
                        myNotificationView.setTextViewText(R.id.notification_text,
                                msg.text);
                        myNotificationView.setImageViewBitmap(
                                R.id.notification_large_icon,
                                getLargeIcon(context, msg));
                        myNotificationView.setImageViewResource(
                                R.id.notification_small_icon,
                                getSmallIconId(context, msg));
                        builder.setContent(myNotificationView);
                        builder.setAutoCancel(true);
                        Notification mNotification = builder.build();
                        // 由于Android
                        // v4包的bug，在2.3及以下系统，Builder创建出来的Notification，并没有设置RemoteView，故需要添加此代码
                        mNotification.contentView = myNotificationView;
                        return mNotification;
                    default:
                        // 默认为0，若填写的builder_id并不存在，也使用默认。
                        return super.getNotification(context, msg);
                }
            }

        };
        mPushAgent.setMessageHandler(messageHandler);

        /**
         * 该Handler是在BroadcastReceiver中被调用，故
         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
         * */
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {

            @Override
            public void openActivity(Context context, UMessage uMessage) {
                LogUtils.t("推送的消息__openActivity__notificationClickHandler()",
                        "" + uMessage.extra.toString());
                super.openActivity(context, uMessage);
            }

            @Override
            public void openUrl(Context context, UMessage uMessage) {
                LogUtils.t("推送的消息__openUrl__notificationClickHandler()",
                        "" + uMessage.extra.toString());

                super.openUrl(context, uMessage);
            }


            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                LogUtils.t("推送的消息_dealWithCustomAction__notificationClickHandler()",
                        "" + msg.extra.toString());
                uMessage = msg;
                if (DavikActivityManager.getScreenManager().getCurrentActivityName().equals(MainActivity.class.getName())) {
                    Utils.toAct(BaseApplication.uMessage, DavikActivityManager.getScreenManager().currentActivity());
                } else {
                    if (DavikActivityManager.getScreenManager().currentActivity() == null) {
                        LogUtils.d("DavikActivityManager.getScreenManager().currentActivity() == null");
                        Intent i = new Intent(getApplicationContext(), SplashActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//必须添加
                        startActivity(i);
                    } else {
                        LogUtils.d("currentActivity() 1= null" + DavikActivityManager.getScreenManager().currentActivity().getClass().getName());
                        Intent i = new Intent(DavikActivityManager.getScreenManager().currentActivity(), MainActivity.class);
                        DavikActivityManager.getScreenManager().currentActivity().startActivity(i);
                    }
                }
                Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
            }
        };
        mPushAgent.setNotificationClickHandler(notificationClickHandler);
    }

    /**
     * 获得当前app版本号
     **/
    public void initLocalVersion() {
        PackageInfo pinfo;
        try {
            pinfo = this.getPackageManager().getPackageInfo(
                    this.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            mVersionCode = pinfo.versionCode;
            mVersionName = pinfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}

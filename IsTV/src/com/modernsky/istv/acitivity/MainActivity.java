package com.modernsky.istv.acitivity;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.aliyun.mbaas.oss.callback.SaveCallback;
import com.aliyun.mbaas.oss.model.OSSException;
import com.lecloud.config.LeCloudPlayerConfig;
import com.letv.proxy.LeCloudProxy;
import com.lidroid.xutils.http.RequestParams;
import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.action.UserAction;
import com.modernsky.istv.bean.AnchorChatroomInfo;
import com.modernsky.istv.bean.PublishUrlInfo;
import com.modernsky.istv.bean.UserEntity;
import com.modernsky.istv.fragment.FirstFragment;
import com.modernsky.istv.fragment.LiveFragment;
import com.modernsky.istv.fragment.ShowFragment;
import com.modernsky.istv.manager.BaseApplication;
import com.modernsky.istv.manager.DavikActivityManager;
import com.modernsky.istv.nmpush.NotificationService;
import com.modernsky.istv.service.DianTaiService;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.BitmapTool;
import com.modernsky.istv.tool.DialogTool;
import com.modernsky.istv.tool.DialogTool.DialogLister;
import com.modernsky.istv.tool.DialogTool.DialogReportLister;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.Constants.UserParams;
import com.modernsky.istv.utils.FileUtils;
import com.modernsky.istv.utils.GeneralTool;
import com.modernsky.istv.utils.JsonUtils;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.MediaUtil;
import com.modernsky.istv.utils.PreferencesUtils;
import com.modernsky.istv.utils.Utils;
import com.modernsky.istv.utils.WeakHandler;
import com.modernsky.istv.view.DrageLayout;
import com.modernsky.istv.view.RoundAngleImageView;
import com.modernsky.istv.view.SlidingMenu;
import com.ta.utdid2.android.utils.NetworkUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.entity.UMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 主界面
 *
 * @author zxm
 */
public class MainActivity extends BaseActivity implements OnClickListener {

    public static FragmentManager fMgr;
    private long exitTime = 0;
    //    private ImageView indexView;
    private Button titleView;
    private Fragment fragmentLeft, fragmentCenter, fragmentRight;
    private Fragment currentFragment;
    private List<Fragment> fragmetns = new ArrayList<Fragment>();
    private RadioGroup radioGroup;
    private int measuredHeight;
    private boolean isShow = true;// 底部radiogroup
    private ObjectAnimator anim;

    public SlidingMenu getmLeftMenu() {
        return mLeftMenu;
    }

    public void setmLeftMenu(SlidingMenu mLeftMenu) {
        this.mLeftMenu = mLeftMenu;
    }

    private SlidingMenu mLeftMenu;
    private ImageView imgMySpace;//个人中心中我的空间左边的图标(动画要用)

    private View mMyspaceNewText;//个人中心中我的空间右边的new字
    private TextView tv_dengji;
    private int peopleState = 0;
    private TextView liveText;
    // 0 没有登录 登录时候不是主播 1 登录是主播 没有发布预告 2 登陆是主播 发布了预告
    //
    TextView tv_my_rongyu;

    TextView tv_lable_name;

    TextView tv_my_name;

    RoundAngleImageView headimg;

    View ll_my_rongyu;
    View ll_login_regist;
    private UserEntity userBean;
    // 反馈的dialog
    private Dialog reportDialog;


    /**
     * 头像上传处理部分
     */
    private static final int PHOTO_REQUEST_CAMERA = 101;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 201;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 301;// 结果
    Dialog eduation_dialog;
    /* 头像名称 */
    private static String PHOTO_FILE_NAME = "temp_photo.jpg";
    private File tempFile;
    private Bitmap bitmap;
    protected String userFaceUrl;
    // 登录状态改变广播
    private MyReceive myReceive;
    private int checkId = R.id.main_rb_center;

    private RadioButton mLeftButton;
    private String codeVersion;
    private Intent intent;
    private AnchorChatroomInfo anchorChatroomInfo;

    public boolean isOpen() {
        if (mLeftMenu == null) {
            return false;
        }
        return mLeftMenu.isOpen();
    }


    /**
     * 初始化友盟推送
     */
    private void toAct(UMessage msg) {
        Map<String, String> extra = msg.extra;
        if (extra != null) {
            switch (extra.get(Constants.TYPE)) {
                case "open_url":
                    String url = extra.get("url");
                    String albumId = extra.get("albumId");
                    String singerId = extra.get("singerId");
                    if (TextUtils.isEmpty(singerId)) {
                        String str;
                        if (url.contains("?"))
                            str = "&singerId=";
                        else
                            str = "?singerId=";
                        url = url + str + singerId;
                        Utils.startH5(this, msg.custom, albumId, url, 6);
                    } else
                        Utils.startH5(this, msg.custom, albumId, url, 6);
                    break;
                case "open_user":
                    String userId = extra.get("userId");
                    Utils.OpenUserInfo(this, userId, "1");
                    break;
                case "fans_list":
                    Utils.OpenUserInfo(this, "", "2");
                    break;
                case "atten_list":
                    Utils.OpenUserInfo(this, "", "1");
                    break;
                case "open_video":
                    String videoId = extra.get("videoId");
                    if ("1".equals(extra.get("isShow"))) {
                        Utils.playLookForwordDemoVideo(this, videoId, extra.get("singerId"));
                    } else {
                        Utils.playVideo(this, videoId, "");
                    }
                    break;
                case "open_live":
                    String liveId = extra.get("videoId");
                    if ("1".equals(extra.get("isShow"))) {
                        Utils.playShow(this, liveId, extra.get("singerId"));
                    } else {
                        Utils.playLive(this, liveId);
                    }
                    break;
                case "notice_list":
                    startActivity(new Intent(this, ZhanneixinActivity.class));
                    break;
                case "reload_login":
                    UserService.getInatance().setUserBean(null, this);
                    startActivity(new Intent(this, LoginActivity.class));
                    break;
                case "open_user_left":
                    if (!mLeftMenu.isOpen())
                        toggleMenu();
                    break;
            }
        }
        BaseApplication.uMessage = null;
    }


    @Override
    protected void onNewIntent(Intent intent) {
        LogUtils.d("onNewIntent");
        super.onNewIntent(intent);
        if (BaseApplication.uMessage != null) {
            LogUtils.d("mainactivity----uMessage != null");
            Utils.toAct(BaseApplication.uMessage, this);
            //            toAct(BaseApplication.uMessage);
        } else {
            LogUtils.d("mainactivity----uMessage == null");
        }
        setIntent(intent);
    }


    public class MyReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Constants.ACTION_LOGIN_CHANGE:
                    reInitView();
                    break;
                case Constants.ACTION_GETUSERINFO:
                    getUserInfo();
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        //        LeCloud.destory();
        LeCloudProxy.destory();
        unregisterReceiver(myReceive);
        LogUtils.d("onDestroystopService");
        unbindService(serviceConn);
        MobclickAgent.onKillProcess(this);
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_my_rank:
                startActivity(new Intent(this, RankActivity.class));
                //                Utils.startTranscripts(this, "A2016032100001fc", "56e676f1e4b071a4448702b4");
                break;
            case R.id.tv_title:
                //                if (UserService.getInatance().isNeedLogin(MainActivity.this)) {
                //                    DialogTool.createToLoginDialog(this);
                //                } else
                //                    Utils.startTranscripts(this, "A20160513000011r", "57356c61e4b03ef99df00114");
                //                    Utils.startTranscripts(this, "A20160317000000q", "56e98bbbe4b0997eecaa22a5");
                //                Utils.playShow(this, "2260", "");
                //                Utils.playLive(this, "2144");
                //                String vrUrl = "http://live.coop.gslb.letv.com/live/hls/20160229300169599/desc.m3u8?stream_id=20160229300169599&platid=10&splatid=1029";
                //                String vrUrl = "http://play.g3proxy.lecloud.com/vod/v2/MjAyLzE2LzEvYmNsb3VkLzE1MTQ1Ny92ZXJfMDBfMjItMTAzNTE0Mjg1Mi1hdmMtNDE4MzYyLWFhYy0zMjAwMC0yMDY4MDctMTE4OTc3NTUtYmE2ODc1ZTQ0ZDYxZTIzNTcxNjhmZjZjMjBjNDFhMDItMTQ1OTQyODQ3MDA0Ny5tcDQ=?b=459&mmsid=50935422&tm=1459428788&key=8ab43baf6755fa2fa72e3b1fbfab8c8c&platid=3&splatid=302&playid=2&tss=mp4&vtype=13&cvid=1805656461208&payff=0&pip=7c746bee0c04209d2b1078b601d282fe&format=0&sign=mb&dname=mobile&expect=1&p1=0&p2=00&p3=&tag=mobile&pid=";
                //                String vrUrl = "http://live.zhengzai.tv/live/hls/20160229300169599/desc.m3u8?stream_id=20160229300169599&platid=10&splatid=1029";
                //                String vrUrl = "http://play.g3proxy.lecloud.com/vod/v2/MTgyLzIyLzgwL2JjbG91ZC8xNTE0NTcvdmVyXzAwXzIyLTEwMzUxNDI4NDktYXZjLTc5Nzg1MS1hYWMtNjQwMDAtMjA2ODA3LTIyNTM2NDQzLTk3NDhmZGYyODVlYjhjNTYxODAwMmZkZWM0OTFkMTljLTE0NTk0Mjg3NDE5MTEubXA0?b=870&mmsid=50935422&tm=1459484148&key=282cbf1a5a0eb767c2d804f4eeffff5c&platid=2&splatid=208&playid=0&tss=no&vtype=22&cvid=8169178871&payff=0&pip=17f534203771fb3976f30dea751d1fe8&tag=mobile&sign=bcloud_151457&termid=2&pay=0&ostype=android&hwtype=un";
                //                String vrUrl = "http://www.upano.net:1935/fplus/myStream/playlist.m3u8";
                //                String vrUrl = "http://video.zhengzai.tv/video/201601131107187320.mp4";
                //                String vrUrl = "http://data.zhengzai.tv/vrvideo/playurl?videoUnique=191a07aa8f";
                //http://live.coop.gslb.letv.com/live/hls/201604013000001lt99/desc.m3u8?stream_id=201604013000001lt99&platid=10&splatid=1029
                //                String vrUrl = "http://live.coop.gslb.letv.com/live/hls/201604013000001lt99/desc.m3u8?stream_id=201604013000001lt99&platid=10&splatid=1029";
                //                Utils.playVRNetworkStream(this, vrUrl, "正在现场");
                break;
            case R.id.img_me:
                toggleMenu();
                break;
            case R.id.img_search:
                startActivity(new Intent(this, SearchActivity.class));
                break;
            case R.id.layout_myspace:

                if (UserService.getInatance().isNeedLogin(MainActivity.this)) {
                    DialogTool.createToLoginDialog(this);
                } else {
                    PreferencesUtils.saveBooleanPreferences(this, PreferencesUtils.TYPE_LIVE_DOT_FIRST, true);
                    if (!PreferencesUtils.getBooleanPreferences(this, PreferencesUtils.TYPE_CHECK_CLICK_LIVE)) {
                        PreferencesUtils.saveBooleanPreferences(this, PreferencesUtils.TYPE_CHECK_CLICK_LIVE, true);
                        mMyspaceNewText.setVisibility(View.GONE);
                    }
                    LogUtils.d("peopleState======" + peopleState);
                    switch (peopleState) {
                        case 0:  //不是主播
                            Utils.sendBroadcastToService(7, this);
                            startActivity(new Intent(this, ApplyAnchorActivity.class));
                            break;
                        case 1:  // 是主播没有发布预告
                            startActivity(new Intent(this, ChoisePublishOrLiveActivity.class));
                            break;
                        case 2://直播
                            if (UserService.getInatance().getUserBean(this) != null) {
                                LogUtils.d("chatroomId====");
                                showLoadingDialog();
                                getAnchorGetThings(UserService.getInatance().getUserBean(this).getHerald().getChatroomId());
                            }
                            break;
                    }
                }

                //                Utils.playLookForwordDemoVideo(this,"","","");
                break;
            case R.id.tv_my_zhanneixin:
                //                if (UserService.getInatance().isNeedLogin(MainActivity.this)) {
                //                    DialogTool.createToLoginDialog(this);
                //                } else
                //                    Utils.startTranscripts(this, "A20160317000000q", "56e98bbbe4b0997eecaa22a5");
                break;
            case R.id.tv_my_jilu:
                if (UserService.getInatance().isNeedLogin(MainActivity.this)) {
                    DialogTool.createToLoginDialog(this);
                } else
                    startActivity(new Intent(this, RecordActivity.class));
                break;
            case R.id.tv_my_chongzhi:
                if (UserService.getInatance().isNeedLogin(MainActivity.this))
                    DialogTool.createToLoginDialog(this);
                else
                    startActivity(new Intent(this, BuyGiftActivity.class));
                break;
            case R.id.tv_my_login:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.tv_my_regist:
                startActivity(new Intent(this, RegisterOneActivity.class));
                break;
            case R.id.im_user:
                if (UserService.getInatance().isNeedLogin(MainActivity.this)) {
                    DialogTool.createToLoginDialog(this);
                } else
                    //                    choiseImgUpdate();
                    startActivity(new Intent(this, UserHomepageActivity.class));
                break;
            case R.id.tv_my_setting:
                startActivity(new Intent(this, SetActivity.class));
                break;
            case R.id.tv_my_shoucang:
                if (UserService.getInatance().isNeedLogin(MainActivity.this)) {
                    DialogTool.createToLoginDialog(this);
                } else
                    startActivity(new Intent(this, ShouCangActivity.class));
                break;
            case R.id.tv_my_youhuiquan:
                if (UserService.getInatance().isNeedLogin(MainActivity.this)) {
                    DialogTool.createToLoginDialog(this);
                } else
                    startActivity(new Intent(this, YouHuiQuanActivity.class));
                break;
            // 日历
            case R.id.tv_my_calendor:
                int y = (int) calendarTextView.getY();
                y = calendarTextView.getTop();
                LogUtils.d("y==" + y);
                startActivity(new Intent(this, CalendarActivity.class));
                break;
            case R.id.tv_show_cost:// 消费记录
                if (UserService.getInatance().isNeedLogin(MainActivity.this)) {
                    DialogTool.createToLoginDialog(this);
                } else
                    startActivity(new Intent(this, OrderMeActivity.class));
                break;
            // 用户反馈
            case R.id.tv_user_report:
                if (UserService.getInatance().isNeedLogin(MainActivity.this)) {
                    DialogTool.createToLoginDialog(this);
                } else {
                    reportDialog = DialogTool.createUserReportDialog(MainActivity.this, new DialogReportLister() {
                        @Override
                        public void onReport(String data) {
                            String usId;
                            String contact;
                            if (UserService.getInatance().getUserBean(getBaseContext()) == null) {
                                contact = "";
                                usId = "";
                            } else {
                                contact = UserService.getInatance().getUserBean(getBaseContext()).getMobile();
                                usId = UserService.getInatance().getUserBean(getBaseContext()).getId();
                            }
                            String verName = "";
                            try {
                                verName = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }

                            //
                            RequestParams params = UrlTool.getParams(Constants.USER_ID, usId,//
                                    Constants.CONTENT, data,//
                                    "contact", contact,//
                                    Constants.TYPE, "1",//
                                    "toUserId", "",//
                                    Constants.SOURCE, Constants.ANDROID_MOBILE,//
                                    Constants.VERSION, verName + "");
                            SendActtionTool.get(UserParams.URL_USER_REPORT, null, UserAction.Action_USER_REPORT, MainActivity.this, params);
                        }
                    });
                }
                break;
            // 二维码登录
            case R.id.tv_login_qrcode:
                //                if (!UserService.getInatance().ifNeedToLogin(this)) {
                if (!UserService.getInatance().isNeedLogin(this)) {
                    Intent intent = new Intent(MainActivity.this, QRCodeActivity.class);
                    startActivity(intent);
                } else {
                    DialogTool.createToLoginDialog(this);
                }
                break;
            // 秀场
            case R.id.main_rb_left:
                if (isOpen()) {
                    toggleMenu();
                    radioGroup.check(checkId);
                    return;
                }
                setTitle(getResources().getString(R.string._show));
                fragmentLeft = fragmentLeft == null ? new ShowFragment() : fragmentLeft;
                showPlaneFragment(fragmentLeft);
                checkId = v.getId();
                break;
            // 首页
            case R.id.main_rb_center:
                setTitle(getResources().getString(R.string._live));
                fragmentCenter = fragmentCenter == null ? new LiveFragment() : fragmentCenter;
                showPlaneFragment(fragmentCenter);
                checkId = v.getId();
                break;
            // 直播
            case R.id.main_rb_right:

                setTitle(getResources().getString(R.string._now));
                fragmentRight = fragmentRight == null ? new FirstFragment() : fragmentRight;
                showPlaneFragment(fragmentRight);
                checkId = v.getId();
                break;
            default:
                break;
        }
    }

    private void getPublishUrl() {
        LogUtils.d("VIDEO_ID-----" + UserService.getInatance().getUserBean(this).getHerald().getVideoId());
        RequestParams params = UrlTool.getParams(Constants.VIDEO_ID,
                UserService.getInatance().getUserBean(this).getHerald().getVideoId(), Constants.USER_ID, UserService.getInatance().getUserBean(this).getId());
        SendActtionTool.get(Constants.URL_GET_LIVEPUBLISHINFO, null, UserAction.ACTION_LIVEPUBLISH, this, params);
    }

    /**
     * 获取主播的power等信息
     */
    private void getAnchorGetThings(String chatroomId) {
        LogUtils.d("getAnchorGetThings");
        SendActtionTool.post(UserParams.URL_GETANCHORINFO, null, UserAction.ACTION_GETANCHOR_INFO, this,
                UrlTool.getPostParams("chatroomId", chatroomId));
    }

    private ServiceConnection serviceConn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtils.d("onServiceConnected");

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtils.d("onServiceDisconnected");

        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        LogUtils.v("onSaveInstanceState");
        //        super.onSaveInstanceState(outState);
    }


    @Override
    public void setContentView(Bundle savedInstanceState) {
        //        LeCloud.init(getApplicationContext());
        LeCloudProxy.init(getApplicationContext());
        LeCloudPlayerConfig.getInstance().setDeveloperMode(LogUtils.showLog).setIsApp();

        setContentView(R.layout.activity_main);
        // CaledarRemind.calendarRm(this,"草莓音乐节", "13:45-22:45", "2015-11-28");
        DianTaiService.getInstance().setIsplaying(PreferencesUtils.getBooleanPreferences(this, PreferencesUtils.TYPE_DIANTAI_TOGLE));
        initDianTaiPlayIfplaying();
        // 启动后台Service
        intent = new Intent();
        intent.setAction("MusicServiceAction");
        intent.setPackage("com.modernsky.istv");
        bindService(intent, serviceConn, BIND_AUTO_CREATE);
        // startService(intent);
        //
        myReceive = new MyReceive();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_GETUSERINFO);
        filter.addAction(Constants.ACTION_LOGIN_CHANGE);
        registerReceiver(myReceive, filter);
        isFirst = true;
        if (!UserService.getInatance().isNeedLogin(this)) {
            PreferencesUtils.saveLongPreferences(this, PreferencesUtils.TYPE_LOGIN_IN_TIME, System.currentTimeMillis());
        }
    }

    // 初始化电台在移动网络下是否播放

    private void initDianTaiPlayIfplaying() {
        boolean shouldPlayInYiDong = PreferencesUtils.getBooleanPreferences(this, PreferencesUtils.TYPE_NET_TOGLE);
        DianTaiService.getInstance().setShouldPlayInYiDong(shouldPlayInYiDong);
    }

    @Override
    public void findViewById() {
        initView();
        initDrager();
        // 获取FragmentManager实例
        fMgr = getSupportFragmentManager();
        initFragment();
        if (measuredHeight == 0) {
            final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            radioGroup.measure(widthSpec, heightSpec);
            measuredHeight = radioGroup.getMeasuredHeight();
        }
        //        reInitView();
        getQidongcishu();
        showGuideDialog(1);
        // 检测版本

        //        initLiveText();
        SendActtionTool.get(UserParams.URL_Check_VERSION, ServiceAction.Action_User,//
                UserAction.Action_CHECK_VERSION, this);
        //yyyy-MM-dd HH:mm:ss
        getUserInfo();
        sendOpenInfo();
        shouldShowDotAboutLive();
        LogUtils.d("UMENGPUSH----" + PreferencesUtils.getLongPreferences(this, PreferencesUtils.TYPE_UMENG_PUSH));
        if (BaseApplication.uMessage != null) {
            LogUtils.d("mainactivity----uMessage != null");
            Utils.toAct(BaseApplication.uMessage, this);
            //            toAct(BaseApplication.uMessage);
        } else {
            LogUtils.d("mainactivity----uMessage == null");
        }
    }


    private void shouldShowDotAboutLive() {
        if (!PreferencesUtils.getBooleanPreferences(this, PreferencesUtils.TYPE_LIVE_DOT_FIRST)) {
            mMyspaceNewText.setVisibility(View.VISIBLE);
        } else {
            mMyspaceNewText.setVisibility(View.GONE);
        }
    }


    private void sendOpenInfo() {
        RequestParams params;
        if (UserService.getInatance().isNeedLogin(this))
            params = UrlTool.getPostParams("userIp", NetworkUtils.getWifiIpAddress(getApplicationContext()), "appType", Constants.ANDROID_MOBILE);
        else
            params = UrlTool.getPostParams("userIp", NetworkUtils.getWifiIpAddress(getApplicationContext()), "appType", Constants.ANDROID_MOBILE, Constants.USER_ID, UserService.getInatance().getUserBean(this).getId());
        SendActtionTool.post(Constants.URL_LOG_ENGINE, null, UserAction.ACTION_SEND_OPENINFO, this, params);
    }


    // 引导页
    private void showGuideDialog(int index) {
        String type = PreferencesUtils.TYPE_GUIDE[index];
        Boolean hasGuid = PreferencesUtils.getBooleanPreferences(this, type);
        //        hasGuid = false;//dialog   每次都显示dialog 调试用
        if (!hasGuid) {
            DialogTool.createGuideDialog(MainActivity.this, index, false, new DialogTool.DialogGuideListener() {
                @Override
                public void onGuide(int index) {
                    switch (index) {
                        case 1:
                            findViewById(R.id.img_me).performClick();
                            break;
                        default:
                            break;
                    }
                }
            });
            PreferencesUtils.saveBooleanPreferences(this, type, true);
        }
    }

    // 获取启动次数
    private void getQidongcishu() {
        int num = PreferencesUtils.getIntPreferences(this, PreferencesUtils.TYPE_HUOQU_ACTIVITY);
        LogUtils.d("num=====" + num);
        if (num == 5) {
            // 返回值等于5 进行评分提示
            titleView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    PingFenshowDialog();// 执行弹窗
                }
            }, 2000);
        }
        //        else {
        //            // 不为5的话不操作
        //        }
        PreferencesUtils.saveIntPreferences(this, PreferencesUtils.TYPE_HUOQU_ACTIVITY, ++num);
    }

    private void PingFenshowDialog() {
        // 自定义评分提示dialog
        DialogTool.createPingFenDialog(MainActivity.this, new DialogLister() {
            public void onCountinue() {
                // TODO Auto-generated method stub
                try {
                    Intent intent = getIntent(MainActivity.this);
                    MainActivity.this.startActivity(intent);
                    // Utils.toast(MainActivity.this, "执行去评分操作");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            public void onCancelListener() {
                // TODO Auto-generated method stub
                // Utils.toast(MainActivity.this, "不执行");
            }
        }).show();
    }

    public static Intent getIntent(Context paramContext) {
        StringBuilder localStringBuilder = new StringBuilder().append("market://details?id=");
        String str = paramContext.getPackageName();
        localStringBuilder.append(str);
        Uri localUri = Uri.parse(localStringBuilder.toString());
        return new Intent("android.intent.action.VIEW", localUri);
    }

    private DrageLayout mDrageLayout;
    private RelativeLayout mDrageView;
    RelativeLayout mDragButtomView;
    private boolean isFirst;
    private TextView calendarTextView;

    private void initDrager() {
        mDrageLayout = (DrageLayout) findViewById(R.id.drageLayout);
        mDrageView = (RelativeLayout) findViewById(R.id.draglayoutView);
        mDragButtomView = (RelativeLayout) findViewById(R.id.layoutButtom_drag);
        mDrageLayout.setDefultBottom(0);
        mDrageLayout.setView(mDrageView, mDragButtomView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reInitView();
        if (!isFirst) {
            mDrageLayout.initDrageLayoutPosition();
        }
        isFirst = false;
        findViewById(R.id.tv_login_qrcode).setVisibility(View.VISIBLE);
        //        if (BaseApplication.uMessage != null) {
        //            LogUtils.d("onResume----uMessage != null");
        //            Utils.toAct(BaseApplication.uMessage,this);
        ////            toAct(BaseApplication.uMessage);
        //        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        //        if (BaseApplication.uMessage != null) {
        //            LogUtils.d("onPause----uMessage != null");
        ////            Utils.toAct(BaseApplication.uMessage,this);
        ////            toAct(BaseApplication.uMessage);
        //            BaseApplication.uMessage=null;
        //        }
    }

    /**
     * 给我要直播的条目赋值 （我要直播和）
     */
    private void initLiveText() {
        if (UserService.getInatance().isNeedLogin(this)) {
            peopleState = 0;
            liveText.setText("我要直播");
            imgMySpace.setImageResource(R.drawable.icon_want_selector);

        } else {
            //            if (UserService.getInatance().getUserBean(this).getRank()==null){
            //                return;
            //            }onclioncli
            if (UserService.getInatance().getUserBean(this).getStatus() == 1) {//普通
                peopleState = 0;
                liveText.setText("我要直播");
                tv_dengji.setBackgroundResource(R.drawable.icon_levelbg_person);
                imgMySpace.setImageResource(R.drawable.icon_want_selector);
            } else if (UserService.getInatance().getUserBean(this).getStatus() == 6) {// 主播
                //                LogUtils.d("userId======"+UserService.getInatance().getUserBean(this).getId());
                tv_dengji.setBackgroundResource(R.drawable.icon_levelbg);
                //                LogUtils.d("UserService.getInatance().getUserBean(this).getHerald()----"+UserService.getInatance().getUserBean(this).getHerald());
                if (UserService.getInatance().getUserBean(this).getHerald() == null) {//没有预告
                    peopleState = 1;
                    liveText.setText("发布直播");
                    imgMySpace.setImageResource(R.drawable.icon_start_selector);
                } else {
                    peopleState = 2;
                    liveText.setText("开始直播");
                    imgMySpace.setImageResource(R.drawable.icon_start_selector);

                    long endTime = UserService.getInatance().getUserBean(this).getHerald().getEndTime();
                    if (endTime < System.currentTimeMillis()) {
                        peopleState = 1;
                        liveText.setText("发布直播");
                        imgMySpace.setImageResource(R.drawable.icon_start_selector);
                    }


                }
            }
        }

    }

    //
    private void reInitView() {
        boolean needLogin = UserService.getInatance().isNeedLogin(this);
        initLiveText();
        if (needLogin) {
            ll_login_regist.setVisibility(View.VISIBLE);
            ll_my_rongyu.setVisibility(View.GONE);
            tv_my_rongyu.setVisibility(View.GONE);
            tv_dengji.setVisibility(View.GONE);
            tv_my_name.setVisibility(View.GONE);
            tv_my_name.setText("");
            headimg.setImageDrawable(null);
            userBean = null;
            headimg.setSideSweepAngle(0);
        } else {
            ll_login_regist.setVisibility(View.GONE);
            ll_my_rongyu.setVisibility(View.VISIBLE);
            tv_my_rongyu.setVisibility(View.VISIBLE);
            tv_dengji.setVisibility(View.VISIBLE);
            tv_my_name.setVisibility(View.VISIBLE);
            userBean = UserService.getInatance().getUserBean(this);
            tv_my_name.setText(userBean.getUserName());
            String faceUrl = userBean.getFaceUrl();
            if (!TextUtils.isEmpty(faceUrl)) {
                BitmapTool.getInstance().initAdapterUitl(this).display(headimg, faceUrl);
            }

            UserEntity.BadgeEntity badge = userBean.getBadge();
            if (badge != null) {
                tv_lable_name.setText(badge.getName());
            }

            UserEntity.RankEntity rank = userBean.getRank();
            if (rank == null) {
                return;
            }
            tv_dengji.setText(rank.getRank());
            headimg.setSideAngleColor(R.color.default_blue_color);
            headimg.setSideSweepAngle((userBean.getExper() - rank.getMinValue()) * 360 / (rank.getMaxValue() - rank.getMinValue()));
            //            headimg.setSideSweepAngle(270);
            if (userBean.getStatus() == 6) {
                tv_my_rongyu.setText("人气值: " + userBean.getExper() + "/" + rank.getMaxValue());
            } else {
                tv_my_rongyu.setText("经验值: " + userBean.getExper() + "/" + rank.getMaxValue());
            }
        }
    }

    /**
     * 获取个人信息
     */
    private void getUserInfo() {
        RequestParams params = null;
        if (UserService.getInatance().getUserBean(this) != null) {
            params = UrlTool.getPostParams(Constants.USER_ID,
                    UserService.getInatance().getUserBean(this).getId() + "");

        } else {
            return;
        }
        SendActtionTool.post(UserParams.URL_GET_ONE,
                ServiceAction.Action_User, UserAction.Action_GetPeopleInfo,
                this, params);

    }


    private void initView() {
        findViewById(R.id.layout_myspace).setOnClickListener(this);
        imgMySpace = (ImageView) findViewById(R.id.img_left_myspace);
        mMyspaceNewText = findViewById(R.id.new_myspace);
        if (!PreferencesUtils.getBooleanPreferences(this, PreferencesUtils.TYPE_CHECK_CLICK_LIVE))
            mMyspaceNewText.setVisibility(View.VISIBLE);
        liveText = (TextView) findViewById(R.id.text_myspace);
        findViewById(R.id.img_me).setOnClickListener(this);
        tv_dengji = (TextView) findViewById(R.id.tv_dengji);
        tv_dengji.setOnClickListener(this);
        tv_my_rongyu = (TextView) findViewById(R.id.tv_my_rongyu);
        tv_lable_name = (TextView) findViewById(R.id.tv_lable_name);
        tv_my_name = (TextView) findViewById(R.id.tv_my_name);
        headimg = (RoundAngleImageView) findViewById(R.id.im_user);
        ll_my_rongyu = findViewById(R.id.ll_my_rongyu);
        ll_login_regist = findViewById(R.id.ll_login_regist);
        findViewById(R.id.img_search).setOnClickListener(this);
        //        findViewById(R.id.img_live).setOnClickListener(this);
        findViewById(R.id.tv_my_setting).setOnClickListener(this);
        findViewById(R.id.tv_my_chongzhi).setOnClickListener(this);
        findViewById(R.id.tv_my_youhuiquan).setOnClickListener(this);
        findViewById(R.id.tv_my_jilu).setOnClickListener(this);
        findViewById(R.id.tv_my_zhanneixin).setOnClickListener(this);
        // findViewById(R.id.tv_my_order).setOnClickListener(this);
        findViewById(R.id.tv_my_rank).setOnClickListener(this);
        findViewById(R.id.tv_my_shoucang).setOnClickListener(this);
        findViewById(R.id.im_user).setOnClickListener(this);
        calendarTextView = (TextView) findViewById(R.id.tv_my_calendor);
        calendarTextView.setOnClickListener(this);
        findViewById(R.id.tv_my_login).setOnClickListener(this);
        findViewById(R.id.tv_my_regist).setOnClickListener(this);
        findViewById(R.id.tv_user_report).setOnClickListener(this);
        findViewById(R.id.tv_login_qrcode).setOnClickListener(this);
        // findViewById(R.id.tv_make_record).setOnClickListener(this);
        findViewById(R.id.tv_show_cost).setOnClickListener(this);
        titleView = (Button) findViewById(R.id.tv_title);
        //
        mLeftMenu = (SlidingMenu) findViewById(R.id.id_menu);
        titleView.setOnClickListener(this);
        titleView.postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.include_left_menu).setVisibility(View.VISIBLE);
            }
        }, 1000);
        // 底部导航 点击事件
        mLeftButton = (RadioButton) findViewById(R.id.main_rb_left);
        // findViewById(R.id.main_rb_left).setOnClickListener(this);
        mLeftButton.setOnClickListener(this);
        findViewById(R.id.main_rb_center).setOnClickListener(this);
        findViewById(R.id.main_rb_right).setOnClickListener(this);
        radioGroup = getView(R.id.bottomGroup);
    }

    public void setTitle(String title) {
        LogUtils.d("title==+" + title);
        if (titleView != null) {
            titleView.setText(title);
        }
    }

    //    public void updateView(int id) {
    //        if (indexView != null) {
    //            indexView.setImageResource(R.color.white);
    //            indexView = null;
    //        }
    //        indexView = (ImageView) findViewById(id);
    //        indexView.setImageResource(R.color.default_color);
    //    }

    public void toggleMenu() {
        mLeftMenu.toggle();
    }

    /**
     * 2015年1月16日
     *
     * @return 展示当前的Fragment
     */
    private Fragment showPlaneFragment(Fragment tagFragment) {
        if (currentFragment.equals(tagFragment)) {
            return tagFragment;
        }
        if (fragmetns.contains(tagFragment)) {
            getFragmentTransaction(false).hide(currentFragment).show(tagFragment).commitAllowingStateLoss();
        } else {
            getFragmentTransaction(false).hide(currentFragment).add(R.id.fragmentRoot, tagFragment, tagFragment.getClass().getName()).commitAllowingStateLoss();
            fragmetns.add(tagFragment);
        }
        currentFragment = tagFragment;
        return tagFragment;
    }

    /**
     * 初始化首个Fragment
     */
    private void initFragment() {
        fragmentCenter = new LiveFragment();
        setTitle(getResources().getString(R.string._live));
        currentFragment = fragmentCenter;
        fragmetns.add(currentFragment);
        getFragmentTransaction(true).add(R.id.fragmentRoot, fragmentCenter, fragmentCenter.getClass().getName()).commit();
    }

    /**
     * 获取Fragment事务处理
     */
    private FragmentTransaction getFragmentTransaction(boolean isFirst) {
        FragmentTransaction transaction = fMgr.beginTransaction();
        if (!isFirst) {
            transaction.setCustomAnimations(R.anim.push_right_in, R.anim.push_left_out, R.anim.push_left_in, R.anim.push_right_out);
        }
        return transaction;
    }

    public void showRadioGroup(boolean show) {
        if (isShow == show)
            return;
        if (show) {
            anim = ObjectAnimator.ofFloat(radioGroup, "translationY", measuredHeight, 0f);
        } else {
            anim = ObjectAnimator.ofFloat(radioGroup, "translationY", 0f, measuredHeight);
        }
        anim.setTarget(radioGroup);
        anim.setDuration(300);
        if (!anim.isRunning()) {
            anim.start();
            isShow = !isShow;
        }
    }

    //    /**
    //     * 从back stack弹出所有的fragment
    //     */
    //    public static void popAllFragmentsExceptTheBottomOne() {
    //        for (int i = 0, count = fMgr.getBackStackEntryCount(); i < count; i++) {
    //            fMgr.popBackStack();
    //        }
    //    }

    // 点击返回按钮
    @Override
    public void onBackPressed() {
        if (mLeftMenu.isOpen()) {
            mLeftMenu.closeMenu();
            return;
        }
        if (fMgr.getBackStackEntryCount() <= 1) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                // finish();
                DavikActivityManager.getScreenManager().exitApp(BaseActivity.class);
                finish();
            }
        } else

        {
            super.onBackPressed();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int arg1, Intent data) {
        LogUtils.t("onActivityResult---", requestCode + "," + arg1);
        super.onActivityResult(requestCode, arg1, data);
        switch (requestCode) {
            case PHOTO_REQUEST_GALLERY:
                if (data != null) {
                    // 得到图片的全路径
                    Uri uri = data.getData();
                    String path = Utils.getAbsoluteImagePath(this, uri);
                    tempFile = new File(path);
                    crop(uri);
                }
                break;
            case PHOTO_REQUEST_CAMERA:
                if (hasSdcard()) {
                    tempFile = new File(Environment.getExternalStorageDirectory(), PHOTO_FILE_NAME);
                    crop(Uri.fromFile(tempFile));
                } else {
                    Toast.makeText(this, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
                }
                break;
            case PHOTO_REQUEST_CUT:
                try {
                    bitmap = data.getParcelableExtra("data");
                    headimg.setImageBitmap(bitmap);
                    tempFile = new File(Environment.getExternalStorageDirectory(), PHOTO_FILE_NAME);
                    if (FileUtils.saveBitmap(tempFile, bitmap)) {
                        // uploadImg("File", tempFile, user.getUserId());
                        updateUserIcon(tempFile);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * 更换选中的新头像
     */
    private void updateUserIcon(File file) {
        showLoadingDialog("正在上传图片...");
        String uri = file.getAbsolutePath();
        LogUtils.t("uri--uri", uri);
        GeneralTool.uploadFile(uri, new SaveCallback() {
            @Override
            public void onProgress(String arg0, int arg1, int arg2) {
            }

            @Override
            public void onFailure(String arg0, OSSException arg1) {
                arg1.printStackTrace();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        dismissDialog();
                        Utils.toast(MainActivity.this, "头像上传失败");
                    }
                });
            }

            // 上传成功
            @Override
            public void onSuccess(String arg0) {
                userFaceUrl = UserParams.USER_URL + arg0;
                LogUtils.d("userFaceUrl==" + userFaceUrl);
                SendActtionTool.post(UserParams.URL_USER_UPDATE, null, UserAction.Action_Update_Face_Url, MainActivity.this,
                        UrlTool.getParams(UserParams.KEY, UserParams.FACE_URL, UserParams.VALUE, userFaceUrl, Constants.USER_ID, userBean.getId()));
            }
        });
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        super.onSuccess(service, action, value);
        switch ((UserAction) action) {
            case Action_Update_Face_Url:
                Utils.toast(getApplicationContext(), "头像上传成功");
                userBean.setFaceUrl(userFaceUrl);
                UserService.getInatance().setUserBean(userBean, this);
                BitmapTool.getInstance().initAdapterUitl(this).display(headimg, userFaceUrl);
                break;
            case Action_USER_REPORT:
                Utils.toast(getApplicationContext(), "反馈成功");
                if (reportDialog.isShowing()) {
                    reportDialog.dismiss();
                }
                break;
            case Action_CHECK_VERSION:
                JSONObject object = (JSONObject) value;
                try {
                    JSONObject data = object.getJSONObject("data");
                    JSONObject sensitive = data.getJSONObject("sensitive");
                    codeVersion = sensitive.getString("version");
                    if (!codeVersion.equals(PreferencesUtils.getPreferences(this, PreferencesUtils.TYPE_Check_Verson))) {
                        String url = sensitive.getString("url");
                        SendActtionTool.post(url, ServiceAction.Action_User, UserAction.Action_CHECK_CODE_VERSION, this);
                    }
                    JSONObject object2 = data.getJSONObject("android");
                    String version = object2.getString("version");
                    String url = object2.getString("url");
                    String versioninfo = "";
                    if (object2.has("msg")) {
                        versioninfo = object2.getString("msg");
                    }
                    LogUtils.t("版本更新", version + "_" + BaseApplication.mVersionName);
                    if (!version.equals(BaseApplication.mVersionName)) {
                        LogUtils.t("版本更新", "开始更新");
                        initNotification(url, versioninfo, version);
                        //                        DialogTool.createCheckDialog(this, url, versioninfo, version);
                    } else {
                        LogUtils.t("版本更新", "不用更新");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    LogUtils.t("版本更新", e.toString());
                }
                LogUtils.t("版本更新", "更新数据");
                break;
            case Action_CHECK_CODE_VERSION:// 敏感词库更新
                JSONArray jsonArray = null;
                try {
                    jsonArray = ((JSONObject) value).getJSONArray("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (jsonArray != null) {
                    PreferencesUtils.saveCheckString(this, jsonArray.toString());
                }
                if (!TextUtils.isEmpty(PreferencesUtils.readCheckString(this))) {
                    PreferencesUtils.savePreferences(this, PreferencesUtils.TYPE_Check_Verson, codeVersion);
                }
                break;
            case Action_GetPeopleInfo:
                String datas;
                try {
                    datas = ((JSONObject) value).getJSONObject(Constants.USER_ENTITY).toString();
                    UserEntity user = JsonUtils.parse(datas, UserEntity.class);
                    UserService.getInatance().setUserBean(user, this);
                    reInitView();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case ACTION_GETANCHOR_INFO:
                try {
                    anchorChatroomInfo = JSON.parseObject(((JSONObject) value).getString("data"), AnchorChatroomInfo.class);
                    if (anchorChatroomInfo.getIsCan() == 1) {
                        getPublishUrl();
                    } else {
                        dismissDialog();
                        Utils.toast(this, "直播未开始");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            case ACTION_LIVEPUBLISH://获取推流的信息
                LogUtils.d("ACTION_LIVEPUBLISH---data---=" + value.toString());
                try {
                    String activityId = ((JSONObject) value).getJSONObject("data").getString("activityId");
                    List<PublishUrlInfo> list = JSON.parseArray(((JSONObject) value).getJSONObject("data").getString("pushUrlList"), PublishUrlInfo.class);
                    if (list != null && list.size() > 0) {
                        String publishUrl = list.get(0).getPushUrl();

                        if (!TextUtils.isEmpty(publishUrl.trim()) && !TextUtils.isEmpty(publishUrl.trim()) && !TextUtils.isEmpty(publishUrl.trim())) {
                            Utils.sendBroadcastToService(7, this);
                            Intent intent = new Intent(this, LivePublishActivity.class);
                            intent.putExtra("anchorInfo", anchorChatroomInfo);
                            intent.putExtra("publishUrl", publishUrl);
                            intent.putExtra("activityId", activityId);
                            startActivity(intent);
                        }
                        LogUtils.d(" LivePublisher.startPublish---publishUrl===" + publishUrl);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //                goToChatRoom("56d960bde4b06c3f2c92d01b");
                break;
            default:
                break;
        }
    }

    private RemoteViews view = null;
    private Notification notification;
    private NotificationManager manager = null;
    private Intent notifiintent = null;
    private PendingIntent pIntent = null;//更新显示
    WeakHandler handler = new WeakHandler(this) {
        @Override
        public void conventHandleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    LogUtils.d("msg.arg1====" + msg.arg1);
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
                case 1:
                    break;
                case 2:
                    break;
            }
        }
    };

    public void initNotification(String url, String versioninfo, String version) {
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

        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //         notifiintent = new Intent();
        //        notifiintent.setAction(Intent.ACTION_VIEW);
        //        notifiintent.setData(Uri.parse("http://www.baidu.com"));
        notifiintent = new Intent(MainActivity.this, NotificationService.class);
        pIntent = PendingIntent.getService(MainActivity.this, 0, notifiintent, 0);
        DialogTool.createCheckDialog(this, url, versioninfo, version, handler);

    }


    @Override
    public void onException(ServiceAction service, Object action, Object value) {
        super.onException(service, action, value);
        switch ((UserAction) action) {
            case ACTION_GETANCHOR_INFO:
                dismissDialog();
            default:
                break;
        }
        if (value != null)
            // zwz           Utils.toast(getApplicationContext(), value.toString());
            LogUtils.d("zwz======NotFound======onException=============", value.toString());
    }

    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {
        super.onSuccess(service, action, value);
        switch ((UserAction) action) {
            case ACTION_GETANCHOR_INFO:
                dismissDialog();
            default:
                break;
        }
        if (value != null)
            //  zwz          Utils.toast(getApplicationContext(), value.toString());
            LogUtils.d("zwz======NotFound======onFaile=============", value.toString());

    }

    @Override
    public void onFinish(ServiceAction service, Object action) {
        dismissDialog();
        switch ((UserAction) action) {
            case Action_USER_REPORT:
                if (reportDialog != null && reportDialog.isShowing()) {
                    reportDialog.dismiss();
                }
            case ACTION_LIVEPUBLISH:
                dismissDialog();
                break;
            default:
                break;
        }
    }

    private void choiseImgUpdate() {
        View eatsView = View.inflate(this, R.layout.complete_choise_img, null);
        eduation_dialog = new Dialog(this, R.style.MmsDialogTheme);
        OnClickListener choiseListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.choise_img_phone:
                        PHOTO_FILE_NAME = MediaUtil.getFinalString() + ".jpg";
                        camera();
                        eduation_dialog.dismiss();
                        break;
                    case R.id.choise_img_pic:
                        gallery();
                        eduation_dialog.dismiss();
                        break;
                    case R.id.choise_img_cancle:
                        eduation_dialog.dismiss();
                        break;
                    case R.id.rl_dialog:
                        eduation_dialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        };

        eatsView.findViewById(R.id.choise_img_phone).setOnClickListener(choiseListener);
        eatsView.findViewById(R.id.rl_dialog).setOnClickListener(choiseListener);
        eatsView.findViewById(R.id.choise_img_pic).setOnClickListener(choiseListener);
        eatsView.findViewById(R.id.choise_img_cancle).setOnClickListener(choiseListener);

        eduation_dialog.setContentView(eatsView);
        eduation_dialog.setCanceledOnTouchOutside(true);
        Window dialogWindow = eduation_dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.getDecorView().setPadding(0, 0, 0, 0);

        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = eduation_dialog.getWindow().getAttributes(); // 获取对话框当前的参数值
        p.width = (d.getWidth()); // 宽度设置为屏幕的0.95
        dialogWindow.setAttributes(p);
        eduation_dialog.setCanceledOnTouchOutside(true);
        eduation_dialog.show();
    }

    /*
     * 从相册获取
     */
    public void gallery() {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    /*
     * 从相机获取
     */
    public void camera() {
        Intent intentc = new Intent("android.media.action.IMAGE_CAPTURE");
        // 判断存储卡是否可以用，可用进行存储
        if (hasSdcard()) {
            intentc.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), PHOTO_FILE_NAME)));
        }
        startActivityForResult(intentc, PHOTO_REQUEST_CAMERA);
    }

    /**
     * 剪切图片
     *
     * @param uri
     * @function:
     * @author:Jerry
     * @date:2013-12-30
     */
    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        // 图片格式
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);// true:不返回uri，false：返回uri
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }
}

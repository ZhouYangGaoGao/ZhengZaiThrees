package com.modernsky.istv.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore.MediaColumns;
import android.text.TextUtils;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.modernsky.istv.acitivity.LoginActivity;
import com.modernsky.istv.acitivity.MainActivity;
import com.modernsky.istv.acitivity.VrPlayerActivity;
import com.modernsky.istv.acitivity.LiveActivity;
import com.modernsky.istv.acitivity.LookForwardActivity;
import com.modernsky.istv.acitivity.OrderActivity;
import com.modernsky.istv.acitivity.PlayActivity;
import com.modernsky.istv.acitivity.RechargeActivity;
import com.modernsky.istv.acitivity.RechargeMbActivity;
import com.modernsky.istv.acitivity.ShowActivity;
import com.modernsky.istv.acitivity.TranscriptsActivity;
import com.modernsky.istv.acitivity.UserHomepageActivity;
import com.modernsky.istv.acitivity.WebActivity;
import com.modernsky.istv.acitivity.YinyueJieHejiActivtity;
import com.modernsky.istv.acitivity.ZhanneixinActivity;
import com.modernsky.istv.bean.FocusPictureModel;
import com.modernsky.istv.bean.RechargeBean;
import com.modernsky.istv.bean.ShowPageItemInfo;
import com.modernsky.istv.bean.UserEntity;
import com.modernsky.istv.manager.BaseApplication;
import com.modernsky.istv.service.DianTaiService;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.DialogTool;
import com.modernsky.istv.view.HorizontalListView;
import com.umeng.message.entity.UMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 工具类
 *
 * @author zxm
 */
public class Utils {
    public static Toast mToast = null;

    /**
     * Toast弹窗
     *
     * @param context
     * @param string
     */
    public static void toast(Context context, String string) {
        if (TextUtils.isEmpty(string.trim())) {
            return;
        }
        if (context == null && Looper.myLooper() != Looper.getMainLooper()) {
            return;
        }
        if (mToast == null) {
            mToast = Toast.makeText(context.getApplicationContext(), string,
                    Toast.LENGTH_SHORT);
        } else {
            mToast.setText(string);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    public static void toAct(UMessage msg, Activity context) {
        Map<String, String> extra = msg.extra;
        LogUtils.d("toAct----" + extra.toString());
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
                        Utils.startH5(context, msg.custom, albumId, url, 6);
                    } else
                        Utils.startH5(context, msg.custom, albumId, url, 6);
                    break;
                case "open_user":
                    String userId = extra.get("userId");
                    LogUtils.d("userId----" + userId);
                    Utils.OpenUserInfo(context, userId, "1");
                    break;
                case "fans_list":
                    Utils.OpenUserInfo(context, "", "2");
                    break;
                case "atten_list":
                    Utils.OpenUserInfo(context, "", "1");
                    break;
                case "open_video":
                    String videoId = extra.get("videoId");
                    if ("1".equals(extra.get("isShow"))) {
                        Utils.playLookForwordDemoVideo(context, videoId, extra.get("singerId"));
                    } else {
                        Utils.playVideo(context, videoId, "");
                    }
                    break;
                case "open_live":
                    String liveId = extra.get("videoId");
                    if ("1".equals(extra.get("isShow"))) {
                        Utils.playShow(context, liveId, extra.get("singerId"));
                    } else {
                        Utils.playLive(context, liveId);
                    }
                    break;
                case "notice_list":
                    context.startActivity(new Intent(context, ZhanneixinActivity.class));
                    break;
                case "reload_login":
                    UserService.getInatance().setUserBean(null, context);
                    context.startActivity(new Intent(context, LoginActivity.class));
                    break;
                case "open_user_left":
                    if (context instanceof MainActivity) {
                        if (!((MainActivity) context).getmLeftMenu().isOpen())
                            ((MainActivity) context).toggleMenu();
                    }
                    break;
            }
        }
        BaseApplication.uMessage = null;
    }

    /**
     * 计算gridview的高度 使用此方法 item的最外层布局必须使用LinearLayout布局
     *
     * @param listView
     * @param numColumns
     */
    @SuppressLint("NewApi")
    public static void setGridViewHeightBasedOnChildren(HorizontalListView listView,
                                                        int numColumns) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int totalWidth = 0;
        int num = listView.getCount() % numColumns;
        int rows = listView.getCount() / numColumns;
        if (num > 0) {
            rows++;
        }
        for (int i = 0; i < rows; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
            totalWidth += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
//        params.height = totalHeight
//                + (listView.getVerticalSpacing() * (rows - 1));
        params.width = totalWidth
                + (listView.getHorizontalFadingEdgeLength() * (rows - 1));
        listView.setLayoutParams(params);
    }

    public static void toast(Context applicationContext, int id) {
        if (applicationContext == null
                && Looper.myLooper() != Looper.getMainLooper()) {
            return;
        }
        if (mToast == null) {
            mToast = Toast.makeText(applicationContext, applicationContext
                    .getResources().getString(id), Toast.LENGTH_SHORT);
        } else {
            mToast.setText(applicationContext.getResources().getString(id));
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    /**
     * 播放专辑
     *
     * @param context
     * @param albumId
     * @param albumName
     */
    public static void playAlbum(Context context, String albumId,
                                 String albumName) {
        Intent intent = new Intent(context, YinyueJieHejiActivtity.class);
        intent.putExtra(Constants.ALBUM_ID, albumId);
        intent.putExtra(Constants.TITLE, albumName);
        context.startActivity(intent);
    }

    /**
     * 播放专辑
     *
     * @param context
     * @param albumId
     */
    public static void playAlbum(Context context, String albumId) {
        sendBroadcastToService(1, context);
        Intent intent = new Intent(context, PlayActivity.class);
        intent.putExtra(Constants.ALBUM_ID, albumId);
        intent.putExtra(Constants.TYPE, Constants.ALBUM_NAME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(intent);
    }

    /**
     * 播放视频
     *
     * @param context
     * @param videoId
     * @param videoName
     */
    public static void playVideo(Context context, String videoId,
                                 String videoName) {
        LogUtils.t("------playVideo-----", "videoId_" + videoId + ";videoName_"
                + videoName);

        sendBroadcastToService(1, context);
        Intent intent = new Intent(context, PlayActivity.class);
        intent.putExtra(Constants.VIDEO_ID, videoId);
        intent.putExtra(Constants.VIDEO_NAME, videoName);
        intent.putExtra(Constants.TYPE, Constants.VIDEO_NAME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(intent);
    }


    /**
     * 播放视频
     *
     * @param context
     * @param videoId
     */
    public static void playLookForwordDemoVideo(Context context, String videoId, String singerId) {
        LogUtils.t("------playLookForwordDemoVideo-----", "videoId_" + videoId);

        sendBroadcastToService(1, context);
        Intent intent = new Intent(context, LookForwardActivity.class);
        intent.putExtra(Constants.VIDEO_ID, videoId);
        intent.putExtra("singerId", singerId);
        intent.putExtra(Constants.TYPE, Constants.VIDEO_NAME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(intent);
    }

    /**
     * 从单个艺人界面进入时播放视频
     *
     * @param context
     * @param videoId
     * @param videoName
     * @param albumId
     */
    public static void playPersonDetailVideo(Context context, String videoId,
                                             String objectId, String videoName, String albumId) {
        LogUtils.t("-----------", "videoId_" + videoId + ";videoName_"
                + videoName + "objectId=" + objectId + ";albumId_" + albumId);

        sendBroadcastToService(1, context);
        Intent intent = new Intent(context, PlayActivity.class);
//        Intent intent = new Intent(context, LookForwardActivity.class);
        intent.putExtra(Constants.VIDEO_ID, videoId);
        intent.putExtra(Constants.VIDEO_NAME, videoName);
        intent.putExtra(Constants.TYPE, Constants.TYPE_ARTIST);
        intent.putExtra(Constants.OBJECT_ID, objectId);
        intent.putExtra(Constants.ALBUM_ID, albumId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(intent);
    }

    public static void playLive(Context context, String videoId) {
        if (UserService.getInatance().isNeedLogin(context)) {
            DialogTool.createToLoginDialog(context);
//            context.startActivity(new Intent(context, LoginActivity.class));
            return;
        }
        sendBroadcastToService(1, context);
        Intent intent = new Intent(context, LiveActivity.class);
        intent.putExtra(Constants.VIDEO_ID, videoId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(intent);
    }

    /**
     * 跳转个人主页回调方法
     *
     * @param typeId 1:关注信息 2:粉丝信息
     * @return
     */
    public static void OpenUserInfo(Context context, String userId, String typeId) {
        if (UserService.getInatance().isNeedLogin(context)) {
            DialogTool.createToLoginDialog(context);
            return;
        }

        Intent intent = new Intent(context, UserHomepageActivity.class);
        intent.putExtra(Constants.USER_ID, userId);
        intent.putExtra("typeId", typeId);
        LogUtils.d("userId---" + userId + "typeId---" + typeId);
        context.startActivity(intent);

    }

    /**
     * 跳转个人主页回调方法
     *
     * @param typeId     1:关注信息 2:粉丝信息 3:作品添加
     * @param addVideoId 被添加的作品id
     * @return
     */
    public static void OpenUserInfo2(Context context, String userId, String addVideoId, String typeId) {
        if (UserService.getInatance().isNeedLogin(context)) {
            DialogTool.createToLoginDialog(context);
            return;
        }
        Intent intent = new Intent(context, UserHomepageActivity.class);
        intent.putExtra(Constants.USER_ID, userId);
        intent.putExtra("typeId", typeId);
        intent.putExtra("addVideoId", addVideoId);

        context.startActivity(intent);

    }

    /**
     * 秀场直播视频 video
     *
     * @param context
     * @param videoId
     */
    public static void playShow(Context context, String videoId, String singerId) {
        LogUtils.t("playShow", "videoId=" + videoId + ",singerId=" + singerId);
        if (UserService.getInatance().isNeedLogin(context)) {
            DialogTool.createToLoginDialog(context);
        } else {
            sendBroadcastToService(1, context);
            Intent intent = new Intent(context, ShowActivity.class);
            intent.putExtra(Constants.VIDEO_ID, videoId);
            intent.putExtra(Constants.SINGER_ID, singerId);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            context.startActivity(intent);
        }
    }

    /**
     * 总体跳转
     *
     * @param model
     * @param context
     */
    public static void playMedia(FocusPictureModel model, Activity context) {
        if (model == null) {
            return;
        }
        try {
            int type = Integer.parseInt(model.getType());

            switch (type) {
                case -100:
                    break;
                // 专辑
                case 0:
                    Utils.playAlbum(context, model.getAlbumId(), model.getName());
                    break;
                // 视屏
                case 1:
                    Utils.playVideo(context, model.getVideoId(), model.getName());
                    break;
                // 广告链接
                case 2:
                    LogUtils.d("url====" + model.getUrl());
                    Uri uri = Uri.parse(model.getUrl());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);
                    break;
                // 直播详情H5页面
                case 3:
                    //活动跳转
                case 7:
                    //票务跳转
                case 8:
                    //预告详情
                case 6:
                    startH5(context, model.getName(), model.getAlbumId(), model.getUrl(), model.getPic(), type);
                    break;
                // 艺人详情页
                case 4:
                    OpenUserInfo(context, model.getUrl(), "1");
                    break;
                // 秀场直播
                case 5:
                    Utils.playShow(context, model.getVideoId(), model.getUserId());
                    break;
                default:
                    startH5(context, model.getName(), model.getAlbumId(), model.getUrl(), model.getPic(), type);
                    Utils.toast(context, "请下载最新版本");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 秀场头图跳转
     *
     * @param model
     * @param context
     */
    public static void playMediaShow(ShowPageItemInfo model, Activity context) {
        if (model == null) {
            return;
        }
        try {
            int type = Integer.parseInt(model.getType());
            switch (type) {
                case -100:
                    break;
                // 专辑
                case 0:
                    // 视屏
                case 1:
                    Utils.playLookForwordDemoVideo(context, model.getVideoId(), model.getUserId());
                    break;
                // 广告链接
                case 2:
                    LogUtils.d("url====" + model.getUrl());
//                    Uri uri = Uri.parse(model.getUrl());
                    Uri uri = Uri.parse("http://www.baidu.com");
                    Intent intent2 = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent2);
                    break;
                // H5页面
                case 3:
                    //活动跳转
                case 7:
                    //票务跳转
                case 8:
                    startH5(context, model.getName(), model.getVideoId(), model.getUrl(), type);
                    break;
                //预告详情
                case 6:
                    startH5Anchor(context, model.getName(), model.getVideoId(), model.getUserId(), model.getUrl(), model.getFaceUrl());
                    break;
                // 艺人详情页
                case 4:
                    OpenUserInfo(context, model.getUserId(), "1");
                    break;
                // 秀场直播
                case 5:
                    Utils.playShow(context, model.getVideoId(), model.getUserId());
                    break;
                default:
                    startH5Anchor(context, model.getName(), model.getVideoId(), model.getUserId(), model.getUrl(), model.getFaceUrl());
                    Utils.toast(context, "请下载最新版本");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void startH5Anchor(Activity context, String name, String videoId, String singerId, String url, String faceUrl) {
        String str;
        if (url.contains("?"))
            str = "&singerId=";
        else
            str = "?singerId=";
        url = url + str + singerId;
        Utils.startH5(context, name, videoId, url, faceUrl, 6);
    }

    /**
     * H5跳转页面
     *
     * @param context
     * @param title
     * @param albumId
     * @param url
     */
    public static void startH5(Context context, String title, String albumId, String url, String faceUrl, int type) {
        if (TextUtils.isEmpty(url)) {
            toast(context, "数据异常,请刷新后再试!");
            return;
        }
        if (UserService.getInatance().isNeedLogin(context)) {
            DialogTool.createToLoginDialog(context);
        } else {
            UserEntity bean = UserService.getInatance().getUserBean(
                    context);
            String userId = bean.getId();
            String str;
            if (url.contains("?"))
                str = "&";
            else
                str = "?";
            String url2 = url + str
                    + "albumId=" + albumId + "&userId=" + userId
                    + "&m_source=android";
            // 测试h5页面
            // String url =
            // "http://182.92.167.30:89/h5pay/detail_test.html?m_source=android"
            // + "&userId=" + userId;
            LogUtils.t("H5-url:", url2);
            Intent intent2 = new Intent(context, WebActivity.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent2.putExtra(Constants.TITLE, title);
            intent2.putExtra(Constants.TYPE, type);
            intent2.putExtra("faceUrl", faceUrl);
            intent2.putExtra(Constants.URL, url2);
            intent2.putExtra(Constants.USER_ID, userId);
            context.startActivity(intent2);
        }
    }

    /**
     * H5跳转页面
     *
     * @param context
     * @param title
     * @param albumId
     * @param url
     */
    public static void startH5(Context context, String title, String albumId, String url, int type) {
        if (TextUtils.isEmpty(url)) {
            toast(context, "数据异常,请刷新后再试!");
            return;
        }
        if (UserService.getInatance().isNeedLogin(context)) {
            DialogTool.createToLoginDialog(context);
        } else {
            UserEntity bean = UserService.getInatance().getUserBean(
                    context);
            String userId = bean.getId();
            String str;
            if (url.contains("?"))
                str = "&";
            else
                str = "?";
            String url2 = url + str
                    + "albumId=" + albumId + "&userId" + userId
                    + "&m_source=android";
            // 测试h5页面
            // String url =
            // "http://182.92.167.30:89/h5pay/detail_test.html?m_source=android"
            // + "&userId=" + userId;
            LogUtils.t("H5-url:", url2);
            Intent intent2 = new Intent(context, WebActivity.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent2.putExtra(Constants.TITLE, title);
            intent2.putExtra(Constants.TYPE, type);
            intent2.putExtra(Constants.URL, url2);
            intent2.putExtra(Constants.USER_ID, userId);
            context.startActivity(intent2);
        }
    }

    /**
     * 开启支付界面
     *
     * @param albumId
     * @param activity
     */

    public static void startPay(String albumId, Activity activity) {
        LogUtils.d("startPay: albumId==" + albumId);
        if (UserService.getInatance().isNeedLogin(activity)) {
            DialogTool.createToLoginDialog(activity);
//            activity.startActivity(new Intent(activity, LoginActivity.class));
        } else {
            String userId = UserService.getInatance().getUserBean(activity)
                    .getId();
            if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(albumId)) {
                LogUtils.t("Utils.startPay()", "UserId_" + userId + ";"
                        + "AlbumId_" + albumId);
                Intent intent = new Intent(activity, OrderActivity.class);
                intent.putExtra(Constants.ALBUM_ID, albumId);
                intent.putExtra(Constants.USER_ID, userId);
                activity.startActivityForResult(intent, 0);
            }
        }
    }

    /**
     * 购买M豆支付界面
     *
     * @param activity
     */
    public static void startBuyMB(Activity activity, RechargeBean rechargeBean) {
        if (UserService.getInatance().isNeedLogin(activity)) {
            DialogTool.createToLoginDialog(activity);
//            activity.startActivity(new Intent(activity, LoginActivity.class));
        } else {
            String userId = UserService.getInatance().getUserBean(activity)
                    .getId();
            if (!TextUtils.isEmpty(userId) && rechargeBean != null) {
                Intent intent = new Intent(activity, RechargeActivity.class);
                intent.putExtra(Constants.OUT_TRADE_NO, rechargeBean);
                intent.putExtra(Constants.USER_ID, userId);
                activity.startActivityForResult(intent, 0);
            }
        }
    }

    /**
     * 购买M豆支付界面
     *
     * @param activity
     */
    public static void startBuyMBonHorizon(Activity activity, RechargeBean rechargeBean) {
        if (UserService.getInatance().isNeedLogin(activity)) {
            DialogTool.createToLoginDialog(activity);
//            activity.startActivity(new Intent(activity, LoginActivity.class));
        } else {
            String userId = UserService.getInatance().getUserBean(activity)
                    .getId();
            if (!TextUtils.isEmpty(userId) && rechargeBean != null) {
                Intent intent = new Intent(activity, RechargeMbActivity.class);
                intent.putExtra(Constants.OUT_TRADE_NO, rechargeBean);
                intent.putExtra(Constants.USER_ID, userId);
                activity.startActivityForResult(intent, 0);
            }
        }
    }

    /**
     * 跳转成绩单界面
     *
     * @param activity
     * @param actId
     * @param chatroomId
     */
    public static void startTranscripts(Activity activity, String actId, String chatroomId) {
        Intent intent = new Intent(activity, TranscriptsActivity.class);
        intent.putExtra(Constants.ACTIVITY_ID, actId);
        intent.putExtra(Constants.CHATROOM_ID, chatroomId);
        activity.startActivity(intent);
    }


    /**
     * 过滤js代码
     *
     * @param value
     * @return
     */
    public static String filterString(String value) {
        value = value.replaceAll("<", "& lt;").replaceAll(">", "& gt;");
        value = value.replaceAll("\\(", "& #40;").replaceAll("\\)", "& #41;");
        value = value.replaceAll("'", "& #39;");
        value = value.replaceAll("eval\\((.*)\\)", "");
        value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']",
                "\"\"");
        value = value.replaceAll("script", "");
        return value;
    }

    // 取到图片的绝对路径
    public static String getAbsoluteImagePath(Context context, Uri uri) {
        Cursor cursor = null;
        String string = "";
        try {
            ContentResolver contentResolver = context.getContentResolver();
            String[] proj = {MediaColumns.DATA};
            cursor = contentResolver.query(uri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
            cursor.moveToFirst();
            string = cursor.getString(column_index);
            cursor.close();
            return string;

        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return string;
    }

    public static String getRealPath(Context mContext, Uri fileUrl) {
        String fileName = null;
        Uri filePathUri = fileUrl;
        if (fileUrl != null) {
            if (fileUrl.getScheme().compareTo("content") == 0)           //content://开头的uri
            {
                Cursor cursor = mContext.getContentResolver().query(filePathUri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
//                    int column_index = cursor.getColumnIndexOrThrow(***.***.***.DATA);
                    fileName = cursor.getString(column_index);          //取出文件路径

                    cursor.close();
                }
            } else if (filePathUri.getScheme().compareTo("file") == 0)         //file:///开头的uri
            {

                fileName = filePathUri.toString();
                fileName = filePathUri.toString().replace("file://", "");
                fileName = Uri.decode(fileName);
            }
        }
        return fileName;
    }


    /**
     * @param context
     * @param collectId
     */
    public static void playAtristAllVideo(Context context, String collectId,
                                          int index) {
        sendBroadcastToService(1, context);
        Intent intent = new Intent(context, PlayActivity.class);
        intent.putExtra(Constants.COLLECT_ID, collectId);
        intent.putExtra(Constants.INDEX, index);
        intent.putExtra(Constants.TYPE, Constants.ARTIST_ALL_VIDEO);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(intent);
    }

    /**
     * 计算微博内容的长度 1个汉字 == 两个英文字母所占的长度 标点符号区分英文和中文
     *
     * @param c 所要统计的字符序列
     * @return 返回字符序列计算的长度
     */
    public static long calculateWeiboLength(CharSequence c) {

        double len = 0;
        for (int i = 0; i < c.length(); i++) {
            int temp = c.charAt(i);
            if (temp > 0 && temp < 127) {
                len += 0.5;
            } else {
                len++;
            }
        }
        return Math.round(len);
    }

    /**
     * @param activity 通过判断设备是否有返回键、菜单键(不是虚拟键,是手机屏幕外的按键)来确定是否有navigation bar
     * @return
     */
    @SuppressLint("NewApi")
    public static boolean checkDeviceHasNavigationBar(Context activity) {

        boolean hasMenuKey = ViewConfiguration.get(activity)
                .hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap
                .deviceHasKey(KeyEvent.KEYCODE_BACK);

        if (!hasMenuKey && !hasBackKey) {
            // 做任何你需要做的,这个设备有一个导航栏
            return true;
        }
        return false;
    }

    /**
     * @param activity 获取NavigationBar的高度
     * @return
     */
    public static int getNavigationBarHeight(Activity activity) {
        if (!checkDeviceHasNavigationBar(activity)) {
            return 0;
        }
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height",
                "dimen", "android");
        // 获取NavigationBar的高度
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    /**
     * 根据名字从asset获取bitmap
     *
     * @param context
     * @param fileName
     * @return
     */

    public static Bitmap getImageFromAssetsFile(Context context, String fileName) {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    // 0 play状态 1 暂停 2 停止 3 上一首 4 下一首 5 喜欢 6删除
    public static void sendBroadcastToService(int state, Context context) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction("com.modernsky.istv.MusicService");
        intent.putExtra("control", state);
        intent.putExtra("isPlayAfter", DianTaiService.getInstance()
                .isIsplaying());
        // 向后台Service发送播放控制的广播
        context.sendBroadcast(intent);
    }

    /**
     * @param context 向 mainactivity 通知 更新userEntity
     */
    public static void sendBroadcastToMainactivityToUpdateUserinfo(Context context) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_GETUSERINFO);
        context.sendBroadcast(intent);
    }

    // 数字转化为汉字

    // k=1 直播 k=0 是 点播
    public static List<String> mRateToString(List<String> infos, int k) {
        List<String> list = new ArrayList<String>();
        if (k == 0) {
            for (int i = 0; i < infos.size(); i++) {
                list.add(Utils.getPlayDefinitionName(infos.get(i)));
            }
        } else if (k == 1) {
            for (int i = 0; i < infos.size(); i++) {
                list.add(Utils.getLiveDefinitionName(infos.get(i)));
            }
        }

        // LogUtils.d("list===="+list);
        return list;
    }

    public static String getLiveDefinitionName(String definition) {
        int parseInt = Integer.parseInt(definition);
        String name = "高清";
        switch (parseInt) {
            case 10:
                name = "流畅";
                break;
            case 13:
                name = "标清";
                break;
            case 16:
                name = "高清";
                break;
            case 19:
                name = "超清";
                break;
            case 22:
                name = "720P";
                break;
            case 25:
                name = "1080P";
                break;
            case 28:
                name = "4k";
                break;
            case 99:
                name = "原画";
                break;

            default:
                break;
        }
        return name;
    }

    /**
     * '9'=>'MP4', '21'=>'MP4_350',
     * <p/>
     * '13'=>'MP4_800',
     * <p/>
     * '22'=>'MP4_1300',
     * <p/>
     * '51'=>'MP4_720P', '52'=>'MP4_1080P3M', '28'=>'MP4_YUANHUA'
     *
     * @param definition
     * @return
     */
    public static String getPlayDefinitionName(String definition) {
        int parseInt = Integer.parseInt(definition);
        String name = "高清";
        switch (parseInt) {
            case 9:
                name = "流畅";
                break;
            case 21:
                name = "标清";
                break;
            case 13:
                name = "高清";
                break;
            case 22:
                name = "超清";
                break;
            case 51:
                name = "720P";
                break;
            case 52:
                name = "1080P";
                break;
            case 28:
                name = "4k";
                break;
            case 99:
                name = "原画";
                break;

            default:
                break;
        }
        return name;
    }

    public static String alaboToChina(int alabo) {
        String index = "";
        switch (alabo) {
            case 1:
                index = "一";
                break;
            case 2:
                index = "二";
                break;
            case 3:
                index = "三";
                break;
            case 4:
                index = "四";
                break;
            case 5:
                index = "五";
                break;
            case 6:
                index = "六";
                break;
            case 7:
                index = "七";
                break;
            case 8:
                index = "八";
                break;
            case 9:
                index = "九";
                break;
            case 10:
                index = "十";
                break;

            default:
                break;
        }

        return index;

    }

    /**
     * 设置全屏
     *
     * @param activity
     */
    public static void setFullScreen(Activity activity) {
        activity.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 取消全屏
     *
     * @param activity
     */
    public static void cancelFullScreen(Activity activity) {
        activity.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 通过测量,获取控件的高度
     *
     * @param view
     * @return
     */
    public static int getViewHeight(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        int height = view.getMeasuredHeight();//高
//        int width = view.getMeasuredWidth();//宽
        return height;
    }

    public static void showLoadingAnim(View view, AnimationDrawable anim) {
        view.setVisibility(View.VISIBLE);
        anim.start();
    }

    public static void cancaleLoadingAnim(View view, AnimationDrawable anim) {
        view.setVisibility(View.GONE);
        if (anim.isRunning())
            anim.stop();
    }

    /**
     * @param map   清晰度集合  建是  数字   value 是 汉字“高清”等
     * @param value
     * @return
     */
    public static int getKey(Map<Integer, String> map, String value) {
        for (Map.Entry entry : map.entrySet()) {
            if (value.equals(entry.getValue()))
                return (int) entry.getKey();
        }
        return -1;
    }

    /**
     * 设置文字的颜色渐变
     *
     * @param textView
     * @param startColor
     * @param endColor
     */
    public static void setTextColorPaint(TextView textView, int startColor, int endColor) {

        Shader shader = new LinearGradient(0, 0, 0, 20, endColor, startColor, Shader.TileMode.MIRROR);
        textView.getPaint().setShader(shader);
    }

    /**
     * VR直播跳转
     *
     * @param activity
     * @param path
     * @param name
     */
    public static void playVRNetworkStream(Activity activity, String path, String name) {
        if (TextUtils.isEmpty(path)) {
            toast(activity, "VR流地址为空!");
            return;
        }
        LogUtils.d("utils---path---" + path);
        LogUtils.d("utils---name---" + name);
        sendBroadcastToService(1, activity);
        Intent intent = new Intent(activity, VrPlayerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("Name", name);
        bundle.putString("Path", path);
        bundle.putString("GlassesType", "0");     //0为gearvr或者魔镜三代，1为魔镜二代
        bundle.putString("ScreenType", "true");  // true 为分屏，false为单屏，单屏陀螺仪默认禁用，双屏陀螺仪默认启动
        intent.putExtras(bundle);
        activity.startActivity(intent);
        activity.finish();
    }


    /**
     * 随机生成分享内容
     *
     * @param
     * @return
     */
    public static String getRandomContent(Context context, int strArrayId) {
        String str = "";
        String[] strs = context.getResources().getStringArray(strArrayId);
        Random random = new Random();
        int num = random.nextInt(strs.length);
        str = strs[num];
        return str;
    }
}

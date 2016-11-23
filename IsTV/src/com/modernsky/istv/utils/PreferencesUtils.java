package com.modernsky.istv.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by zxm on 2015/1/25.
 */
public class PreferencesUtils {
    private static final String APP_INFO = "IsTV";
    public static final String TYPE_CHECK_CLICK_LIVE = "TYPE_CHECK_CLICK_LIVE";//记录是否点击了我要直播
    public static String TYPE_FIRST_START = "first_start";// 是否首次启动
    public static String TYPE_FIRST_SETTING = "first_start";// 是否首次设置快捷方式
    public static String TYPE_HOSTORY_LIST = "TYPE_HOSTORY_LIST";// 搜索历史记录
    public static String TYPE_Check_String = "TYPE_Check_String";// 过滤字段
    public static String TYPE_Check_Verson = "TYPE_Check_Verson";// 过滤字段版本
    public static String TYPE_NET_TOGLE = "TYPE_NET_TOGLE";// 网络提醒开关设置
    public static String TYPE_DIANTAI_TOGLE = "TYPE_DIANTAI_TOGLE";// 电台默认开关设置
    public static String TYPE_USER_LOGIN = "TYPE_USER_LOGIN";
    public static String TYPE_PINGLUN_TOGLE = "TYPE_PINGLUN_TOGLE";// 评论提醒开关设置
    public static boolean TYPE_TANMU_SHOW = true;// 弹幕开关
    // true,false
    public static String TYPE_DEFAULT_QXD = "TYPE_DEFAULT_QXD_TOGLE";// 视频默认播放的清细度是否最优
    public static String TYPE_FIRST_FRAGMENT = "TYPE_FIRST_FRAGMENT";// 缓存首页数据源
    public static String TYPE_SHOW_FRAGMENT = "TYPE_SHOW_FRAGMENT";// 缓存秀场数据源
    public static String TYPE_LIVE_FRAGMENT = "TYPE_LIVE_FRAGMENT";// 缓存直播数据源
    public static String TYPE_RANK_ACTIVITY = "TYPE_RANK_ACTIVITY";// 缓存排行榜页面数据源
    public static String TYPE_SHOUCANG_ACTIVITY = "TYPE_SHOWCANG_ACTIVITY";// 缓存收藏页面数据源
    public static String TYPE_RECORD_ACTIVITY = "TYPE_RECORD_ACTIVITY";// 缓存记录页面数据源

    public static String TYPE_HUOQU_ACTIVITY = "TYPE_HUOQU_ACTIVITY_3.3";// 应用启动次数

    public static String TYPE_LIKE_MUSIC = "TYPE_LIKE_MUSIC";// 喜欢的本地列表未登录
    public static String TYPE_DELETE_MUSIC = "TYPE_DELETE_MUSIC";// 删除的本地列表

    public static String TYPE_MY_SPACE = "TYPE_MY_SPACE";// （个人中心里面）我的空间是否进入过
    public static String TYPE_LOGIN_IN_TIME = "TYPE_LOGIN_IN_TIME";// 记录登陆时间
    public static String TYPE_LOGIN_OUT_TIME = "TYPE_LOGIN_OUT_TIME";// 记录退出时间
    public static String TYPE_Hot_City = "TYPE_Hot_City";// 热门城市
    public static String TYPE_All_City = "TYPE_All_City";// 全部城市
    public static String TYPE_SHARE_TIME = "TYPE_SHARE_TIME";// 分享时间
    public static String TYPE_UMPUSH = "TYPE_UMPUSH";// 友盟推送消息
    public static String TYPE_LIVE_DOT_FIRST = "TYPE_LIVE_DOT_FIRST";// 友盟推送消息

    public static String TYPE_UMENG_PUSH="TYPE_UMENG_PUSH";

    /**=============================================朱洧志 start===================================*/
    public static String RANK_ACTION = "RANK_ACTION";// 排行榜数据

    /**=============================================朱洧志   end===================================*/

    // guideDialog的标记
    public static String[] TYPE_GUIDE = {"yingdao0",//
            "yingdao1",//
            "yingdao2",//
            "yingdao3",//
            "yingdao4",//
            "yingdao5",//
            "yingdao6",
            "yingdao7",
            "yingdao8"
    };
    //
    public static Context mContext;

    public static String getPreferences(Context context, String stringType) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(APP_INFO, Context.MODE_PRIVATE);
        return pref.getString(stringType, "");
    }

    public static boolean savePreferences(Context context, String stringType, String info) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(APP_INFO, Context.MODE_PRIVATE);
        return pref.edit().putString(stringType, info).commit();
    }

    public static int getIntPreferences(Context context, String stringType) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(APP_INFO, Context.MODE_PRIVATE);
        return pref.getInt(stringType, 1);
    }

    public static boolean saveIntPreferences(Context context, String stringType, int info) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(APP_INFO, Context.MODE_PRIVATE);
        return pref.edit().putInt(stringType, info).commit();
    }

    public static long getLongPreferences(Context context, String stringType) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(APP_INFO, Context.MODE_PRIVATE);
        return pref.getLong(stringType, 1);
    }

    public static boolean saveLongPreferences(Context context, String stringType, long info) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(APP_INFO, Context.MODE_PRIVATE);
        return pref.edit().putLong(stringType, info).commit();
    }

    public static Boolean getBooleanPreferences(Context context, String stringType) {
        if (context == null) {
            return false;
        }
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(APP_INFO, Context.MODE_PRIVATE);
        return pref.getBoolean(stringType, false);
    }

    public static Boolean getBooleanDefultTrue(Context context, String stringType) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(APP_INFO, Context.MODE_PRIVATE);
        return pref.getBoolean(stringType, true);
    }

    public static boolean saveBooleanPreferences(Context context, String stringType, Boolean info) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(APP_INFO, Context.MODE_PRIVATE);
        return pref.edit().putBoolean(stringType, info).commit();
    }

    public static boolean setFirstStartInfo(Context context, boolean isFirst) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(APP_INFO, Context.MODE_PRIVATE);
        return pref.edit().putBoolean(TYPE_FIRST_START, isFirst).commit();
    }

    public static boolean getFirstStartInfo(Context context) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(APP_INFO, Context.MODE_PRIVATE);
        return pref.getBoolean(TYPE_FIRST_START, false);
    }

    /**
     * 获取过滤的字段
     *
     * @return
     */
    public static List<String> getCheckString(Context mContext) {
        try {
            List<String> dataList = JSON.parseArray(FileTool.readSensetive(mContext), String.class);
            return dataList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 内部保存文件
    public static void saveCheckString(Context mContext, String text) {
        String FILE_NAME = "check.txt";
        try {
            FileOutputStream fos = mContext.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            fos.write(text.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 内部读取文件
    public static String readCheckString(Context mContext) {
        String FILE_NAME = "check.txt";
        String text = "";
        try {
            FileInputStream fis = mContext.openFileInput(FILE_NAME);
            byte[] readBytes = new byte[fis.available()];
//            while (fis.read(readBytes) != -1) {
//            }
            text = new String(readBytes);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }
}

package com.modernsky.istv.tool;

import android.content.Context;
import android.util.Log;

import com.modernsky.istv.R;
import com.modernsky.istv.utils.LogUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TimeTool {
    /**
     * @param oldTime
     * @return 根据接受的时间与当前时间匹配，返回需要的字符信息
     */
    public static String getTimeStr(Date oldTime, Context context) {

        long time1 = new Date().getTime();

        long time2 = oldTime.getTime();

        long time = (time1 - time2) / 1000;
        if (time < 60) {// 各个终端机型的本地时间系统不一致导致差值可能为负数，所以不能只判断大于零的情况
            return context.getString(R.string.just_now);
        } else if (time >= 60 && time < 3600) {
            return time / 60 + context.getString(R.string.minute_before);
        } else if (time >= 3600 && time < 3600 * 24) {
            return time / 3600 + context.getString(R.string.hour_before);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            return sdf.format(oldTime);
        }
    }

    /**
     * @param time
     * @return 00:00:00
     */
    public static String getTimeStr(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss", Locale.getDefault());
        return sdf.format(time);
    }

    /*
     * long 型转化为  String 类型
     */
    public static String getTimeStr2(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM", Locale.getDefault());
        return sdf.format(time);
    }

    /**
     * @param str
     * @return 显示客户端时间 String
     */
    public static String strToString(String str, Context context) {
        // sample：Tue May 31 17:46:55 +0800 2011
        // E：周 MMM：字符串形式的月，如果只有两个M，表示数值形式的月 Z表示时区（＋0800）
        SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy",
                Locale.CHINA);
        Date result = null;
        try {
            result = sdf.parse(str);
        } catch (Exception e) {
        }
        return getTimeStr(result, context);
    }

    /**
     * php服务器 返回的是毫秒
     * <p/>
     * 返回 时间 提示 字符窜
     **/
    public static String getTimeString(long time, Context context) {
        return getTimeStr(new Date(time), context);
    }

    /**
     * @param time yyyy-MM-dd HH:mm:ss
     * @return millenSeconds
     */
    public static long getMillTime(String time) {
        SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            return sdff.parse(time).getTime();
        } catch (ParseException e) {
            Log.e("_______errar", e.toString());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * @param time yyyy-MM-dd HH:mm:ss
     * @return millenSeconds
     */
    public static long getMillTime2(String time) {
        SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            return sdff.parse(time).getTime();
        } catch (ParseException e) {
            Log.e("_______errar", e.toString());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 当日的日期字符串形式例如：20140327
     *
     * @return
     */
    public static String getCurrentDate(long time) {
        SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sFormat.format(new Date(time));
    }
    /**
     * 当日0点的时间戳
     *
     *@return
     */
    public static long getTimesmorning(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return  cal.getTimeInMillis();
    }
    /**
     * @param time
     * @return String yyyy-MM-dd
     */
    public static String getFormaTime(long time) {
        if (time <= 0)
            return null;
        SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//		SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdff.format(new Date(time));
    }

    /**
     * @param time
     * @return String yyyy/MM/dd
     */
    public static String getFormaTime_(long time) {
        if (time <= 0)
            return null;
        SimpleDateFormat sdff = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        //		SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdff.format(new Date(time));
    }

    /**
     * @param time
     * @return String yyyy-MM-dd HH:mm:ss
     */
    public static String getFormaTime2(long time) {
        if (time <= 0)
            return null;
//		SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdff.format(new Date(time));
    }

    /**
     * @param time
     * @return String yyyy.MM.dd HH:mm
     */
    public static String getTime(long time) {
        if (time <= 0)
            return null;
        SimpleDateFormat sdff = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault());
        return sdff.format(new Date(time));
    }

    /**
     *
     */
    public static String getDayTime(long time) {
        if (time <= 0)
            return null;
        SimpleDateFormat sdff = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
        return sdff.format(new Date(time));
    }

    /**
     * @param time
     * @return 时间段  转化成   多少天   多少小时  多少分钟
     */
    public static Map<String, Integer> getTimeCount(long time) {
        Map<String, Integer> map = new HashMap<String, Integer>();


        int day = 0;
        int hour = 0;
        int minites = 0;
        int secs = 0;

        secs = ((int) (time / (1000))) % 60;
        minites = ((int) (time / (1000 * 60))) % 60;
        hour = (((int) (time / (1000 * 60))) / 60) % 24;
        day = (((int) (time / (1000 * 60))) / 60) / 24;


//		LogUtils.d("倒计时"+day+"天"+hour +"小时"+minites+"分钟");
        map.put("secs", secs);
        map.put("day", day);
        map.put("hour", hour);
        map.put("mins", minites);

//		if (day==0) {
//			if (hour==0) {
//				return minites+"分钟";
//			} else {
//				return hour +"小时"+minites+"分钟";
//			}
//		} 
//		return day+"天"+hour +"小时"+minites+"分钟";
        return map;

    }

    public static int getCountDays(String startTime, String endTime) {
        long d1 = getMillTime(startTime + " " + "00:00:00");
        long d2 = getMillTime(endTime + " " + "00:00:00");
        LogUtils.d("d1=" + d1);
        LogUtils.d("d2=" + d2);
        long diff = d2 - d1;//这样得到的差值是微秒级别
        return (int) (diff / (1000 * 60 * 60 * 24));
    }

    //
    //输入一个 年月 得到这个月的天数  "2008/02"
    public static int getDaysOfTheMonth(String args) {
        Calendar rightNow = Calendar.getInstance();
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy/MM", Locale.getDefault()); //如果写成年月日的形式的话，要写小d，如："yyyy/MM/dd"
        try {
//			rightNow.setTime(simpleDate.parse("2008/02")); //要计算你想要的月份，改变这里即可
            rightNow.setTime(simpleDate.parse(args)); //要计算你想要的月份，改变这里即可
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int days = rightNow.getActualMaximum(Calendar.DAY_OF_MONTH);
//		System.out.println("days : "+ days);
        return days;
    }

    public static String getTimeFromSec(int secs) {
        int hour = secs / 3600; //小时
        int minite = (secs / 60) % 60; //分钟
        int sec = secs % 60;
        String time;
        if (hour < 10) {
            time = "0" + hour;
        } else {
            time = "" + hour;
        }
        time += ":";
        if (minite < 10) {
            time = time + "0" + minite;
        } else {
            time = time + "" + minite;
        }
        time += ":";
        if (sec < 10) {
            time = time + "0" + sec;
        } else {
            time = time + "" + sec;
        }
        return time;
    }

}

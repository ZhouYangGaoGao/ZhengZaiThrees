package main.java.cn.aigestudio.datepicker.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;

import com.modernsky.istv.tool.TimeTool;
import com.modernsky.istv.utils.LogUtils;

import java.util.TimeZone;

public class CaledarRemind {
    private static Context context;
    // Android2.2版本以后的URL，之前的就不写了
    private static String calanderURL = "content://com.android.calendar/calendars";
    private static String calanderEventURL = "content://com.android.calendar/events";
    private static String calanderRemiderURL = "content://com.android.calendar/reminders";

    // 添加账户
    @SuppressLint("NewApi")
    public static void initCalendars(Context context, String name, String email,
                                     String uname) {
        CaledarRemind.context = (Context) context;
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(Calendars.NAME, name);

        value.put(Calendars.ACCOUNT_NAME, email);//
        value.put(Calendars.ACCOUNT_TYPE, "com.android.exchange");
        value.put(Calendars.CALENDAR_DISPLAY_NAME, uname);
        value.put(Calendars.VISIBLE, 1);
        value.put(Calendars.CALENDAR_COLOR, -9206951);
        value.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);
        value.put(Calendars.SYNC_EVENTS, 1);
        value.put(Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(Calendars.OWNER_ACCOUNT, email);
        value.put(Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri calendarUri = Calendars.CONTENT_URI;
        calendarUri = calendarUri
                .buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER,
                        "true")
                .appendQueryParameter(Calendars.ACCOUNT_NAME, email)
                .appendQueryParameter(Calendars.ACCOUNT_TYPE,
                        "com.android.exchange").build();

        context.getContentResolver().insert(calendarUri, value);

    }

    // 写入事件到系统日历中
    public static void calendarRm(Context context, String title, String hourMinis, String yearMonthDay) {
        // 获取要出入的gmail账户的id
        initCalendars(context, "zhengzaiisTV", "zhaoqigang_good@163.com", "isTv");

        String calId = "";
        Cursor userCursor = context.getContentResolver().query(
                Uri.parse(calanderURL), null, null, null, null);
        if ((userCursor == null)) {
            return;
        }
        if (userCursor.getCount() > 0) {
            userCursor.moveToLast(); // 注意：是向最后一个账户添加，开发者可以根据需要改变添加事件 的账户
            calId = userCursor.getString(userCursor.getColumnIndex("_id"));
            userCursor.close();
        } else {
            userCursor.close();
//            Toast.makeText(context, "没有账户，请先添加账户", 0).show();
            return;
        }

        ContentValues event = new ContentValues();
        event.put("title", title);// 标题
//		event.put("description", msg);// 具体信息
        // 插入账户
        event.put("calendar_id", calId);
        System.out.println("calId: " + calId);
        event.put("eventLocation", "正在现场"); // 地点
//		"today": "2015-09-19",
//	      "todaytime": "13:45-22:45",
        String[] times = hourMinis.split("-");


        long start = TimeTool.getMillTime2(yearMonthDay + " " + times[0]);
        long end = TimeTool.getMillTime2(yearMonthDay + " " + times[1]);

        event.put("dtstart", start);
        LogUtils.d("insertdtstart===" + start);
        event.put("dtend", end);
        event.put("hasAlarm", 1);

        event.put(Events.EVENT_TIMEZONE, "Asia/Beijing"); // 这个是时区，必须有，
        // 添加事件
        Uri newEvent = context.getContentResolver().insert(
                Uri.parse(calanderEventURL), event);
        // 事件提醒的设定
        if (newEvent==null) {
            return;
        }
        long id = Long.parseLong(newEvent.getLastPathSegment());
        ContentValues values = new ContentValues();
        values.put("event_id", id);
        // 提前10分钟有提醒
        values.put("minutes", 10);
        context.getContentResolver().insert(Uri.parse(calanderRemiderURL),
                values);

//		Toast.makeText(context, "插入事件成功!!!", Toast.LENGTH_LONG).show();
    }

    public static void deleteCalendar(Context context, String hourMinis, String yearMonthDay) {
        //		"today": "2015-09-19",
//	      "todaytime": "13:45-22:45",
        String[] times = hourMinis.split("-");

        long start = TimeTool.getMillTime2(yearMonthDay + " " + times[0]);
        LogUtils.d("deletedtstart===" + start);
        context.getContentResolver().delete(Uri.parse(calanderEventURL), "dtstart = ? and eventLocation = ?", new String[]{String.valueOf(start), "正在现场"});
    }
}



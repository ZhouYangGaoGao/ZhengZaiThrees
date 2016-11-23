package com.modernsky.istv.acitivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Region;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lidroid.xutils.http.RequestParams;
import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.action.CommentAction;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.bean.ResultBean;
import com.modernsky.istv.bean.ScheduleVo;
import com.modernsky.istv.bean.ShowInfoVo;
import com.modernsky.istv.service.CalendarService;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.DialogTool;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.Utils;
import com.modernsky.istv.view.PopCalendarView;
import com.modernsky.istv.view.RoundAngleImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

import main.java.cn.aigestudio.datepicker.interfaces.OnDateSelected;
import main.java.cn.aigestudio.datepicker.utils.CaledarRemind;
import main.java.cn.aigestudio.datepicker.utils.LogUtil;
import main.java.cn.aigestudio.datepicker.views.DatePicker;
import main.java.cn.aigestudio.datepicker.views.MonthView;
import main.java.cn.aigestudio.datepicker.views.TitleView.onMonthChangedListenner;
import main.java.cn.aigestudio.datepicker.views.TitleView.onTitleYearChangedListenner;

public class CalendarActivity extends BaseActivity implements
        onTitleYearChangedListenner, onMonthChangedListenner {
    public DatePicker mDatePicker;
    RoundAngleImageView mImg;
    private TextView textView;
    public PopCalendarView popView;
    String yearMonth;
    private MyReceive myReceive;

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_back_calendar:// 回退按钮
                this.finish();
                break;

            case R.id.btn_yuyue:// 预约按钮点击事件
                // sentAppoint(null, 2);
                // Toast.makeText(this, "预约事件", 0).show();
                break;

        }
    }

    @Override
    public void setContentView(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        setContentView(R.layout.activity_calendar_layout);
        myReceive = new MyReceive();
        IntentFilter filter = new IntentFilter(Constants.ACTION_LOGIN_CHANGE);
        registerReceiver(myReceive, filter);
        init();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(myReceive);
        super.onDestroy();
    }

    private void init() {
        // TODO Auto-generated method stub
        mDatePicker = (DatePicker) findViewById(R.id.dp_viewId);
        mDatePicker.setBackgroundResource(R.color.hui1f);
        mDatePicker.setOnDateSelected(new OnDateSelected() {
            @Override
            public void selected(List<String> date) {
                for (String s : date) {
                    LogUtil.v(s);
                }
            }
        });
        mDatePicker.setOnDateClicked(new MonthView.OnMonthItemClickListener() {
            @Override
            public void onMonthItemClickListener(String date,
                                                 boolean isSelected, Region region) {
                LogUtil.d("点击的时间为:" + date);
                if (CalendarService.getInstance().getMap().containsKey(date)) {
                    List<ShowInfoVo> list = CalendarService.getInstance()
                            .getMap().get(date);
                    // for (int i = 0; i < list.size(); i++) {
                    // LogUtils.d("videoId===="+list.get(i).getVideoId()+"");
                    // }

                    if (list != null && list.size() > 0) {
                        initPopView(region, date);

                    }
                }
            }
        });
        mDatePicker.setTitleViewYearChangedLitenner(this);
        mDatePicker.titleView.monthlistenner = this;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initPopView(Region region, String date) {
        popView = new PopCalendarView(this, region, date);
        popView.showAtLocation(mDatePicker.monthView, Gravity.TOP, 0, region
                .getBounds().centerY() + region.getBounds().height() * 3);
        // popView.showAtLocation(mDatePicker.monthView, Gravity.TOP, 0, region
        // .getBounds().centerY() );
        LogUtils.d("y=" + region.getBounds().centerY());
    }

    @Override
    public void findViewById() {
        // TODO Auto-generated method stub
        findViewById(R.id.btn_back_calendar).setOnClickListener(this);// 回退按键
        findViewById(R.id.btn_yuyue).setOnClickListener(this);// 预约点击事件
        textView = (TextView) findViewById(R.id.tv_year);
        textView.setText(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        String strMonth = CalendarService.getInstance().getMonth();

        if (strMonth.length() < 2) {
            yearMonth = CalendarService.getInstance().getYear() + "0"
                    + CalendarService.getInstance().getMonth();
        } else {
            yearMonth = CalendarService.getInstance().getYear()
                    + CalendarService.getInstance().getMonth();
        }

        getCalendarValData(yearMonth);
    }


    // 获取日历当前年月活动
    public void getCalendarValData(String yearMonth) {

        RequestParams params = new RequestParams();
        LogUtils.d("yearMonth" + yearMonth);
        params.addQueryStringParameter("date", yearMonth);
        String userId = "";
        if (!UserService.getInatance().isNeedLogin(this)) {
            userId = UserService.getInatance().getUserBean(this).getId();
        }
        params.addQueryStringParameter("userId", userId);

        SendActtionTool.get(Constants.URL_CALENDAR_VAL,
                ServiceAction.Action_Comment,
                CommentAction.Action_getCalendarVal, this, params);
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        // TODO Auto-generated method stub
        String jsonString = value.toString();
        LogUtils.t("jsonString== ", jsonString);
        switch ((CommentAction) action) {
            case Action_getCalendarVal:
                try {
                    ResultBean<List<ScheduleVo>> tempBean = JSON.parseObject(
                            jsonString,
                            new TypeReference<ResultBean<List<ScheduleVo>>>() {
                            });
                    if (tempBean != null) {
                        List<ScheduleVo> datas = tempBean.data;

                        for (int i = 0; i < datas.size(); i++) {
                            String date = datas.get(i).getDate();
                            // LogUtils.d("date为：" + date);
                            CalendarService.getInstance().getMap()
                                    .put(date, datas.get(i).getData());

                        }
                        // CalendarService.setMap(map);

                        LogUtils.d("执行了绘画日历事件");
                        mDatePicker.monthView.update();

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case Action_Yuyue:
//		{"data":{"isSubscribe":0},"message":"取消预约","status":1}
//		{"data":{"isSubscribe":1},"message":"预约成功","status":1}
                try {
                    String message = new JSONObject(jsonString)
                            .getString("message");
                    LogUtils.d("onSucces--message=" + message);
                    if (popView != null && popView.isShowing()) {
                        popView.dismiss();
                    }

//                    DialogTool.createAppointSuccessDialog(this, message);
                    String isSubscribe = new JSONObject(jsonString)
                            .getJSONObject("data").getString("isSubscribe");
                    if (isSubscribe.equals("0")) {
                        message="取消预约成功";
                        LogUtils.d("isSubscribe.equals(\"0\")");
                        CaledarRemind.deleteCalendar(getApplicationContext(), appointInfo.getTodaytime(), appointInfo.getToday());
                    } else if (isSubscribe.equals("1")) {
                        message="预约成功";
                        LogUtils.d("isSubscribe.equals(\"1\")");
                        CaledarRemind.calendarRm(getApplicationContext(),
                                this.appointInfo.getAlbumName(),
                                appointInfo.getTodaytime(), appointInfo.getToday());
                    }
                    DialogTool.createAppointSuccessDialog(this, message);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                break;

        }
    }

    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {
        // TODO Auto-generated method stub
        String jsonString = value.toString();
        LogUtils.d("onfial--jsonString=" + jsonString);
        switch ((CommentAction) action) {
            case Action_Yuyue:
                // try {
                // String message = new JSONObject(jsonString)
                // .getString("message");
                // LogUtils.d("onfial--message=" + message);
                // if (popView.isShowing()) {
                // popView.dismiss();
                // }
                Utils.toast(this, jsonString);

                // } catch (JSONException e) {
                // // TODO Auto-generated catch block
                // e.printStackTrace();
                // }
                break;

            default:
                break;
        }
    }

    @Override
    public void onMonthChanded(int month) {
        LogUtils.d("月份发生改变===" + month);
        CalendarService.getInstance().setMonth(month + "");
        if (month < 10) {
            yearMonth = textView.getText() + "0"
                    + CalendarService.getInstance().getMonth();
        } else {
            yearMonth = textView.getText()
                    + CalendarService.getInstance().getMonth();
        }
        LogUtils.d("---getCalendarValData()");
        getCalendarValData(yearMonth);// 月份发生改变重新访问接口
    }

    @Override
    public void onTitleYearChanded(int year) {
        textView.setText(String.valueOf(year));
        LogUtils.d("年份发生改变===" + year);
        CalendarService.getInstance().setYear(String.valueOf(year));
    }

    /**
     * 预约单天的或者通票
     *
     * @param info
     * index 0的话预约单一的 1 预约全部 2 预约全部
     */
    private ShowInfoVo appointInfo;

    public void sentAppoint(ShowInfoVo info, int index) {
        RequestParams params = new RequestParams();
        // LogUtils.d("userId" + yearMonth);
        this.appointInfo = info;
        LogUtils.d("ActivitysendAppoint--index=" + index);
//        boolean isNeed = UserService.getInatance().ifNeedToLogin(this);
        boolean isNeed = UserService.getInatance().isNeedLogin(this);

        if (!isNeed) {
            params.addQueryStringParameter("userId", UserService.getInatance()
                    .getUserBean(this).getId());
            if (index == 0) {
                params.addQueryStringParameter("type", "ONE");
                params.addQueryStringParameter("name", info.getVideoName());
                params.addQueryStringParameter("videoId", info.getVideoId());
            } else if (index == 1) {
                params.addQueryStringParameter("type", "ALL");
                params.addQueryStringParameter("name", info.getAlbumName());
                params.addQueryStringParameter("albumId", info.getAlbumId());
                LogUtils.d("params=" + "userId="
                        + UserService.getInatance().getUserBean(this).getId()
                        + "name=" + info.getAlbumName() + "albumId"
                        + info.getAlbumId());
            } else if (index == 2) {
                params.addQueryStringParameter("type", "MONTH");
                params.addQueryStringParameter("name", "预约全部内容");
            }
            SendActtionTool.get(Constants.UserParams.URL_YUYUE_ADD,
                    ServiceAction.Action_Comment, CommentAction.Action_Yuyue,
                    this, params);
        } else {
            DialogTool.createToLoginDialog(this);
        }

    }

    public class MyReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            getCalendarValData(yearMonth);
        }
    }

}

package com.modernsky.istv.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Region;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.modernsky.istv.acitivity.CalendarActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.adapter.CommonAdapter;
import com.modernsky.istv.adapter.ViewHolder;
import com.modernsky.istv.bean.ShowInfoVo;
import com.modernsky.istv.service.CalendarService;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.DialogTool;
import com.modernsky.istv.tool.TimeTool;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.Utils;

import java.util.Calendar;
import java.util.List;

/**
 * @author : Administrator
 * @describe : 从新登录
 * <p/>
 * 2013-10-23
 */
public class PopCalendarView extends PopupWindow {
    private View mCalendarView;
    private CalendarActivity mcontext;
    private ListView mList;
    private CommonAdapter adapter;
    // private List<CalendarDayPopListInfo> list;
    private List<ShowInfoVo> list;
    private Region region;
    private ImageView mTitleImage;
    private String date;

    @SuppressLint("NewApi")
    public PopCalendarView(final Activity context, Region region, final String date) {
        super(context);
        this.mcontext = (CalendarActivity) context;
        this.region = region;
        this.date = date;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mCalendarView = inflater.inflate(R.layout.pop_calendor, null);

        mTitleImage = (ImageView) mCalendarView.findViewById(R.id.img_title);
        mTitleImage.setImageResource(R.drawable.shape_zhankai_sanjiao);
        LayoutParams params = (LayoutParams) mTitleImage.getLayoutParams();
        params.setMargins(region.getBounds().centerX()
                - region.getBounds().width() / 10, 0, 0, 0);
        mTitleImage.setLayoutParams(params);
        mList = (ListView) mCalendarView.findViewById(R.id.lv_pop_calendor);
        list = CalendarService.getInstance().getMap().get(date);
        adapter = new CommonAdapter<ShowInfoVo>(mcontext, list,
                R.layout.item_pop_calendor) {
            @SuppressLint("ResourceAsColor")
            @Override
            public void convert(ViewHolder helper, ShowInfoVo item) {
                ImageView mAppoint = helper.getView(R.id.img_appoint);
                TextView mFirst = helper.getView(R.id.tv_firstday);
//				TextView mSecond = helper.getView(R.id.tv_sencondday);

                //
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                month++;
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                String today;
                if (month < 10) {
                    today = year + "-0" + month + "-" + day;
                } else {
                    today = year + "-" + month + "-" + day;
                }
                LogUtils.d("today=" + today);
                LogUtils.d("item.getToday()=" + item.getToday());
                if (TimeTool.getCountDays(today, item.getToday()) < 0) {
                    mFirst.setText("已结束");
                    mAppoint.setImageResource(R.drawable.dot_normal_greydot);
                    mFirst.setBackgroundResource(R.drawable.but_zhankai_endbut);
//					mSecond.setVisibility(View.GONE);
                } else {

                    // 设置文字   只有一天
                    if (item.getStartDate().equals(item.getEndDate())) {
                        if (item.getOpointStatus().equals("0")) {
                            mFirst.setText("未预约");
                            mAppoint.setImageResource(R.drawable.dot_normal_bluedot);
                            mFirst.setBackgroundResource(R.drawable.but_zhankai_bluebut);
                            sendAppoint(mFirst, item, 0, context, false);
                        } else {
                            mFirst.setText("已预约");
                            sendAppoint(mFirst, item, 0, context, true);
                            mAppoint.setImageResource(R.drawable.dot_normal_pinkdot);
                            mFirst.setBackgroundResource(R.drawable.but_zhankai_pinkbut);
                        }
//						mSecond.setVisibility(View.GONE);
                    } else {

                        int indexDay = TimeTool.getCountDays(item.getStartDate(),
                                item.getToday()) + 1;
                        mFirst.setText("第"
                                + Utils.alaboToChina(indexDay) + "天");


//						mSecond.setText("全"
//								+ (TimeTool.getCountDays(item.getStartDate(),
//										item.getEndDate()) + 1) + "天");


                        if (item.getOpointStatus().equals("0")) {
                            // mSecond.setVisibility(View.VISIBLE);
                            sendAppoint(mFirst, item, 0, context, false);
                            mAppoint.setImageResource(R.drawable.dot_normal_bluedot);
                            mFirst.setBackgroundResource(R.drawable.but_zhankai_bluebut);
//							mSecond.setBackgroundResource(R.drawable.but_zhankai_bluebut);

                        } else if (item.getOpointStatus().equals("1")) {
                            mAppoint.setImageResource(R.drawable.dot_normal_pinkdot);
                            mFirst.setBackgroundResource(R.drawable.but_zhankai_pinkbut);
                            sendAppoint(mFirst, item, 0, context, true);
//							mSecond.setBackgroundResource(R.drawable.but_zhankai_bluebut);
//							sendAppoint(mSecond, item, 1);
                        } else if (item.getOpointStatus().equals("2")) {
                            mFirst.setBackgroundResource(R.drawable.but_zhankai_pinkbut);
                            mAppoint.setImageResource(R.drawable.dot_normal_pinkdot);
                            sendAppoint(mFirst, item, 0, context, true);
//							mSecond.setBackgroundResource(R.drawable.but_zhankai_pinkbut);
                        } else if (item.getOpointStatus().equals("3")) {
                            mAppoint.setImageResource(R.drawable.dot_normal_greydot);
                            mFirst.setText("已结束");
                            mFirst.setBackgroundResource(R.drawable.but_zhankai_endbut);
//							mSecond.setVisibility(View.GONE);
//							mSecond.setBackgroundResource(R.drawable.but_zhankai_pinkbut);
                        }

                    }
                }
                helper.setText(R.id.tv_name, item.getAlbumName());
                helper.setText(R.id.tv_date, item.getToday());
                helper.setText(R.id.tv_time, item.getTodaytime());
            }
        };
        mList.setAdapter(adapter);
        // 设置SelectPicPopupWindow的View
        this.setContentView(mCalendarView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.FILL_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x00000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }

    /*
     * 现根据最后参数判断  当是true时  前边的参数没作用
     */
    public void sendAppoint(final View view, final ShowInfoVo info, final int index, final Context context, final boolean isHasAppint) {

        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//				if (isHasAppint) {
//					Utils.toast(context, "您已预约过该视频");
//				} else {
                if (!UserService.getInatance().isNeedLogin(context)) {
                    mcontext.sentAppoint(info, index);
                    LogUtils.d("PopViewsendAppoint--index=" + index);
                } else {
                    if (mcontext.popView.isShowing()) {
                        mcontext.popView.dismiss();
                    }
                    DialogTool.createToLoginDialog(mcontext);
//					mcontext.startActivity(new Intent(mcontext, LoginActivity.class));
                }
                view.setEnabled(false);
            }

//			}
        });
        mCalendarView.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        }, 1000);


    }

    /**
     * 展示弹窗
     *
     * @param v
     */
    public void showPopLogin(View v) {
        this.showAtLocation(v, Gravity.START | Gravity.TOP, 0, 0);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mcontext.mDatePicker.monthView.removeSelectCycle(date);
    }

}

package com.modernsky.istv.acitivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.action.CommentAction;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.bean.TimeShaftBean;
import com.modernsky.istv.bean.WeeksBean;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.TimeTool;
import com.modernsky.istv.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhouyang 总榜时间轴界面
 */
public class TimerShaftActivity extends BaseActivity {

    private ListView listView;
    private List<Integer> list;
    List<String> liststrs;
    WeeksBean weeksBean;
    TimeShaftBean timeShaftBean;
    private TextView jiBnagdan;
    private MyAdapter adapter;


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.timer_back:
//                startActivity(new Intent(TimerShaftActivity.this, DabangAboutActivity.class));
                finish();
            case R.id.timershaft_jibangdan:
                //设置跳转结果数据
                Intent mresultData = new Intent();
                mresultData.putExtra("time", liststrs.get(0));
                mresultData.putExtra("code", 3);
                mresultData.putExtra("top", "quarter1");
                TimerShaftActivity.this.setResult(RESULT_OK, mresultData);
                TimerShaftActivity.this.finish();
                break;
        }
    }

    //获取时间轴起点
//    public static String URL_TIMESHAFT="http://stat.zhengzai.tv/timeEngine";
    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_timershaft);
    }

    @Override
    public void findViewById() {
        list = new ArrayList<>();
        liststrs = new ArrayList<>();

        adapter = new MyAdapter();

        View foot = View.inflate(this, R.layout.timer_foot, null);
        jiBnagdan= (TextView) foot.findViewById(R.id.timershaft_jibangdan);
        View top = View.inflate(this, R.layout.timer_top, null);
        findViewById(R.id.timer_back).setOnClickListener(this);
        listView = (ListView) findViewById(R.id.timer_listview);
        listView.addFooterView(foot);
        listView.addHeaderView(top);
        listView.setAdapter(adapter);
        getTimeSaft();
    }


    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder vh=new ViewHolder();
//            switch (list.get(position)) {
//                case 1:
//                    convertView = View.inflate(TimerShaftActivity.this, R.layout.item_timer_small, null);
//                    break;
//                case 2:
//                    convertView = View.inflate(TimerShaftActivity.this, R.layout.item_timer_big, null);
//                    break;
//                case 3:
//                    convertView = View.inflate(TimerShaftActivity.this, R.layout.item_timer_big2, null);
//
//                    break;
//                case 4:
//                    convertView = View.inflate(TimerShaftActivity.this, R.layout.item_timer_small2, null);
//
//                    break;
//            }
            switch (position){
                case 0:
                    if (weeksBean.getData().getWeek1()==1){
                        convertView = View.inflate(TimerShaftActivity.this, R.layout.item_timer_small, null);
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //设置跳转结果数据
                                Intent mresultData = new Intent();
                                mresultData.putExtra("time", liststrs.get(position));
                                mresultData.putExtra("code", 1);
                                mresultData.putExtra("top","week1");
                                TimerShaftActivity.this.setResult(RESULT_OK, mresultData);
                                TimerShaftActivity.this.finish();
                            }
                        });
                    }else{
                        convertView = View.inflate(TimerShaftActivity.this, R.layout.item_timer_small2, null);
                    }
                    vh.name = (TextView) convertView.findViewById(R.id.item_timer_name);
                    vh.date = (TextView) convertView.findViewById(R.id.item_timer_date);
                    vh.date.setText(liststrs.get(position));
                    vh.name.setText("第一周榜单");
                    break;
                case 1:
                    if (weeksBean.getData().getWeek2()==1){
                        convertView = View.inflate(TimerShaftActivity.this, R.layout.item_timer_small, null);
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //设置跳转结果数据
                                Intent mresultData = new Intent();
                                mresultData.putExtra("time", liststrs.get(position));
                                mresultData.putExtra("code", 1);
                                mresultData.putExtra("top","week2");
                                TimerShaftActivity.this.setResult(RESULT_OK, mresultData);
                                TimerShaftActivity.this.finish();
                            }
                        });
                    }else{
                        convertView = View.inflate(TimerShaftActivity.this, R.layout.item_timer_small2, null);
                    }
                    vh.name = (TextView) convertView.findViewById(R.id.item_timer_name);
                    vh.date = (TextView) convertView.findViewById(R.id.item_timer_date);
                    vh.date.setText(liststrs.get(position));
                    vh.name.setText("第二周榜单");
                    break;
                case 2:
                    if (weeksBean.getData().getWeek3()==1){
                        convertView = View.inflate(TimerShaftActivity.this, R.layout.item_timer_small, null);
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //设置跳转结果数据
                                Intent mresultData = new Intent();
                                mresultData.putExtra("time", liststrs.get(position));
                                mresultData.putExtra("code", 1);
                                mresultData.putExtra("top","week3");
                                TimerShaftActivity.this.setResult(RESULT_OK, mresultData);
                                TimerShaftActivity.this.finish();
                            }
                        });
                    }else{
                        convertView = View.inflate(TimerShaftActivity.this, R.layout.item_timer_small2, null);
                    }
                    vh.name = (TextView) convertView.findViewById(R.id.item_timer_name);
                    vh.date = (TextView) convertView.findViewById(R.id.item_timer_date);
                    vh.date.setText(liststrs.get(position));
                    vh.name.setText("第三周榜单");
                    break;
                case 3:
                    if (weeksBean.getData().getWeek4()==1){
                        convertView = View.inflate(TimerShaftActivity.this, R.layout.item_timer_small, null);
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //设置跳转结果数据
                                Intent mresultData = new Intent();
                                mresultData.putExtra("time", liststrs.get(position));
                                mresultData.putExtra("code", 1);
                                mresultData.putExtra("top","week4");
                                TimerShaftActivity.this.setResult(RESULT_OK, mresultData);
                                TimerShaftActivity.this.finish();
                            }
                        });
                    }else{
                        convertView = View.inflate(TimerShaftActivity.this, R.layout.item_timer_small2, null);
                    }
                    vh.name = (TextView) convertView.findViewById(R.id.item_timer_name);
                    vh.date = (TextView) convertView.findViewById(R.id.item_timer_date);
                    vh.date.setText(liststrs.get(position));
                    vh.name.setText("第四周榜单");
                    break;
                case 4:
                    if (weeksBean.getData().getMonth1()==1){
                        convertView = View.inflate(TimerShaftActivity.this, R.layout.item_timer_big, null);
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //设置跳转结果数据
                                Intent mresultData = new Intent();
                                mresultData.putExtra("time", liststrs.get(position));
                                mresultData.putExtra("code", 2);
                                mresultData.putExtra("top","month1");
                                TimerShaftActivity.this.setResult(RESULT_OK, mresultData);
                                TimerShaftActivity.this.finish();
                            }
                        });
                    }else{
                        convertView = View.inflate(TimerShaftActivity.this, R.layout.item_timer_big2, null);
                    }
                    vh.name = (TextView) convertView.findViewById(R.id.item_timer_name);
                    vh.date = (TextView) convertView.findViewById(R.id.item_timer_date);
                    vh.date.setText(liststrs.get(position));
                    vh.name.setText("第一月榜单");
                    break;
                case 5:
                    if (weeksBean.getData().getWeek5()==1){
                        convertView = View.inflate(TimerShaftActivity.this, R.layout.item_timer_small, null);
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //设置跳转结果数据
                                Intent mresultData = new Intent();
                                mresultData.putExtra("time", liststrs.get(position));
                                mresultData.putExtra("code", 1);
                                mresultData.putExtra("top","week5");
                                TimerShaftActivity.this.setResult(RESULT_OK, mresultData);
                                TimerShaftActivity.this.finish();
                            }
                        });
                    }else{
                        convertView = View.inflate(TimerShaftActivity.this, R.layout.item_timer_small2, null);
                    }
                    vh.name = (TextView) convertView.findViewById(R.id.item_timer_name);
                    vh.date = (TextView) convertView.findViewById(R.id.item_timer_date);
                    vh.date.setText(liststrs.get(position));
                    vh.name.setText("第一周榜单");
                    break;
                case 6:
                    if (weeksBean.getData().getWeek6()==1){
                        convertView = View.inflate(TimerShaftActivity.this, R.layout.item_timer_small, null);
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //设置跳转结果数据
                                Intent mresultData = new Intent();
                                mresultData.putExtra("time", liststrs.get(position));
                                mresultData.putExtra("code", 1);
                                mresultData.putExtra("top","week6");
                                TimerShaftActivity.this.setResult(RESULT_OK, mresultData);
                                TimerShaftActivity.this.finish();
                            }
                        });
                    }else{
                        convertView = View.inflate(TimerShaftActivity.this, R.layout.item_timer_small2, null);
                    }
                    vh.name = (TextView) convertView.findViewById(R.id.item_timer_name);
                    vh.date = (TextView) convertView.findViewById(R.id.item_timer_date);
                    vh.date.setText(liststrs.get(position));
                    vh.name.setText("第二周榜单");
                    break;
                case 7:
                    if (weeksBean.getData().getWeek7()==1){
                        convertView = View.inflate(TimerShaftActivity.this, R.layout.item_timer_small, null);
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //设置跳转结果数据
                                Intent mresultData = new Intent();
                                mresultData.putExtra("time", liststrs.get(position));
                                mresultData.putExtra("code", 1);
                                mresultData.putExtra("top","week7");
                                TimerShaftActivity.this.setResult(RESULT_OK, mresultData);
                                TimerShaftActivity.this.finish();
                            }
                        });
                    }else{
                        convertView = View.inflate(TimerShaftActivity.this, R.layout.item_timer_small2, null);
                    }
                    vh.name = (TextView) convertView.findViewById(R.id.item_timer_name);
                    vh.date = (TextView) convertView.findViewById(R.id.item_timer_date);
                    vh.date.setText(liststrs.get(position));
                    vh.name.setText("第三周榜单");
                    break;
                case 8:
                    if (weeksBean.getData().getWeek8()==1){
                        convertView = View.inflate(TimerShaftActivity.this, R.layout.item_timer_small, null);
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //设置跳转结果数据
                                Intent mresultData = new Intent();
                                mresultData.putExtra("time", liststrs.get(position));
                                mresultData.putExtra("code", 1);
                                mresultData.putExtra("top","week8");
                                TimerShaftActivity.this.setResult(RESULT_OK, mresultData);
                                TimerShaftActivity.this.finish();
                            }
                        });
                    }else{
                        convertView = View.inflate(TimerShaftActivity.this, R.layout.item_timer_small2, null);
                    }
                    vh.name = (TextView) convertView.findViewById(R.id.item_timer_name);
                    vh.date = (TextView) convertView.findViewById(R.id.item_timer_date);
                    vh.date.setText(liststrs.get(position));
                    vh.name.setText("第四周榜单");
                    break;
                case 9:
                    if (weeksBean.getData().getMonth2()==1){
                        convertView = View.inflate(TimerShaftActivity.this, R.layout.item_timer_big, null);
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //设置跳转结果数据
                                Intent mresultData = new Intent();
                                mresultData.putExtra("time", liststrs.get(position));
                                mresultData.putExtra("code", 2);
                                mresultData.putExtra("top","month2");
                                TimerShaftActivity.this.setResult(RESULT_OK, mresultData);
                                TimerShaftActivity.this.finish();
                            }
                        });
                    }else{
                        convertView = View.inflate(TimerShaftActivity.this, R.layout.item_timer_big2, null);
                    }
                    vh.name = (TextView) convertView.findViewById(R.id.item_timer_name);
                    vh.date = (TextView) convertView.findViewById(R.id.item_timer_date);
                    vh.date.setText(liststrs.get(position));
                    vh.name.setText("第二月榜单");
                    break;
                case 10:
                    if (weeksBean.getData().getWeek9()==1){
                        convertView = View.inflate(TimerShaftActivity.this, R.layout.item_timer_small, null);
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //设置跳转结果数据
                                Intent mresultData = new Intent();
                                mresultData.putExtra("time", liststrs.get(position));
                                mresultData.putExtra("code", 1);
                                mresultData.putExtra("top","week9");
                                TimerShaftActivity.this.setResult(RESULT_OK, mresultData);
                                TimerShaftActivity.this.finish();
                            }
                        });
                    }else{
                        convertView = View.inflate(TimerShaftActivity.this, R.layout.item_timer_small2, null);
                    }
                    vh.name = (TextView) convertView.findViewById(R.id.item_timer_name);
                    vh.date = (TextView) convertView.findViewById(R.id.item_timer_date);
                    vh.date.setText(liststrs.get(position));
                    vh.name.setText("第一周榜单");
                    break;
                case 11:
                    if (weeksBean.getData().getWeek10()==1){
                        convertView = View.inflate(TimerShaftActivity.this, R.layout.item_timer_small, null);
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //设置跳转结果数据
                                Intent mresultData = new Intent();
                                mresultData.putExtra("time", liststrs.get(position));
                                mresultData.putExtra("code", 1);
                                mresultData.putExtra("top","week10");
                                TimerShaftActivity.this.setResult(RESULT_OK, mresultData);
                                TimerShaftActivity.this.finish();
                            }
                        });
                    }else{
                        convertView = View.inflate(TimerShaftActivity.this, R.layout.item_timer_small2, null);
                    }
                    vh.name = (TextView) convertView.findViewById(R.id.item_timer_name);
                    vh.date = (TextView) convertView.findViewById(R.id.item_timer_date);
                    vh.date.setText(liststrs.get(position));
                    vh.name.setText("第二周榜单");
                    break;
                case 12:
                    if (weeksBean.getData().getWeek11()==1){
                        convertView = View.inflate(TimerShaftActivity.this, R.layout.item_timer_small, null);
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //设置跳转结果数据
                                Intent mresultData = new Intent();
                                mresultData.putExtra("time", liststrs.get(position));
                                mresultData.putExtra("code", 1);
                                mresultData.putExtra("top","week11");
                                TimerShaftActivity.this.setResult(RESULT_OK, mresultData);
                                TimerShaftActivity.this.finish();
                            }
                        });
                    }else{
                        convertView = View.inflate(TimerShaftActivity.this, R.layout.item_timer_small2, null);
                    }
                    vh.name = (TextView) convertView.findViewById(R.id.item_timer_name);
                    vh.date = (TextView) convertView.findViewById(R.id.item_timer_date);
                    vh.date.setText(liststrs.get(position));
                    vh.name.setText("第三周榜单");
                    break;
                case 13:
                    if (weeksBean.getData().getWeek12()==1){
                        convertView = View.inflate(TimerShaftActivity.this, R.layout.item_timer_small, null);
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //设置跳转结果数据
                                Intent mresultData = new Intent();
                                mresultData.putExtra("time", liststrs.get(position));
                                mresultData.putExtra("code", 1);
                                mresultData.putExtra("top","week12");
                                TimerShaftActivity.this.setResult(RESULT_OK, mresultData);
                                TimerShaftActivity.this.finish();
                            }
                        });
                    }else{
                        convertView = View.inflate(TimerShaftActivity.this, R.layout.item_timer_small2, null);
                    }
                    vh.name = (TextView) convertView.findViewById(R.id.item_timer_name);
                    vh.date = (TextView) convertView.findViewById(R.id.item_timer_date);
                    vh.date.setText(liststrs.get(position));
                    vh.name.setText("第四周榜单");
                    break;
                case 14:
                    if (weeksBean.getData().getMonth3()==1){
                        convertView = View.inflate(TimerShaftActivity.this, R.layout.item_timer_big, null);
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //设置跳转结果数据
                                Intent mresultData = new Intent();
                                mresultData.putExtra("time", liststrs.get(position));
                                mresultData.putExtra("code", 2);
                                mresultData.putExtra("top","month3");
                                TimerShaftActivity.this.setResult(RESULT_OK, mresultData);
                                TimerShaftActivity.this.finish();
                            }
                        });
                    }else{
                        convertView = View.inflate(TimerShaftActivity.this, R.layout.item_timer_big2, null);
                    }
                    vh.name = (TextView) convertView.findViewById(R.id.item_timer_name);
                    vh.date = (TextView) convertView.findViewById(R.id.item_timer_date);
                    vh.date.setText(liststrs.get(position));
                    vh.name.setText("第三月榜单");
                    break;

            }


            return convertView;
        }


        class ViewHolder {
            TextView name, date;
        }
    }


    private void getTimeSaft() {

        SendActtionTool.get(Constants.URL_TIMESHAFT,
                ServiceAction.Action_Comment,
                CommentAction.ACTION_TimerShaft, this);
    }

    private void getTimeSaftIsShow() {

        SendActtionTool.get(Constants.URL_TIMESHAFT_ISSHOW,
                ServiceAction.Action_Comment,
                CommentAction.ACTION_TimerShaft_IsShow, this);
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        super.onSuccess(service, action, value);
        switch ((CommentAction) action) {
            case ACTION_TimerShaft:

                timeShaftBean = JSON.parseObject(value.toString(), TimeShaftBean.class);
                long start = timeShaftBean.getData().getTime();
                long mstart = start;
                long day = 24 * 3600 * 1000;
                String m1 = TimeTool.getFormaTime_(mstart) + "-" + TimeTool.getFormaTime_(mstart += 27 * day);
                String m2 = TimeTool.getFormaTime_(mstart += day) + "-" + TimeTool.getFormaTime_(mstart += 27 * day);
                String m3 = TimeTool.getFormaTime_(mstart += day) + "-" + TimeTool.getFormaTime_(mstart += 27 * day);
                liststrs.add(TimeTool.getFormaTime_(start) + "-" + TimeTool.getFormaTime_(start += 6 * day));
                liststrs.add(TimeTool.getFormaTime_(start += day) + "-" + TimeTool.getFormaTime_(start += 6 * day));
                liststrs.add(TimeTool.getFormaTime_(start += day) + "-" + TimeTool.getFormaTime_(start += 6 * day));
                liststrs.add(TimeTool.getFormaTime_(start += day) + "-" + TimeTool.getFormaTime_(start += 6 * day));
                liststrs.add(m1);
                liststrs.add(TimeTool.getFormaTime_(start += day) + "-" + TimeTool.getFormaTime_(start += 6 * day));
                liststrs.add(TimeTool.getFormaTime_(start += day) + "-" + TimeTool.getFormaTime_(start += 6 * day));
                liststrs.add(TimeTool.getFormaTime_(start += day) + "-" + TimeTool.getFormaTime_(start += 6 * day));
                liststrs.add(TimeTool.getFormaTime_(start += day) + "-" + TimeTool.getFormaTime_(start += 6 * day));
                liststrs.add(m2);
                liststrs.add(TimeTool.getFormaTime_(start += day) + "-" + TimeTool.getFormaTime_(start += 6 * day));
                liststrs.add(TimeTool.getFormaTime_(start += day) + "-" + TimeTool.getFormaTime_(start += 6 * day));
                liststrs.add(TimeTool.getFormaTime_(start += day) + "-" + TimeTool.getFormaTime_(start += 6 * day));
                liststrs.add(TimeTool.getFormaTime_(start += day) + "-" + TimeTool.getFormaTime_(start += 6 * day));
                liststrs.add(m3);
                list.add(1);
                list.add(1);
                list.add(1);
                list.add(1);
                list.add(2);
                list.add(4);
                list.add(4);
                list.add(4);
                list.add(4);
                list.add(3);
                list.add(4);
                list.add(4);
                list.add(4);
                list.add(4);
                list.add(3);
                getTimeSaftIsShow();
                break;
            case ACTION_TimerShaft_IsShow:
                weeksBean = JSON.parseObject(value.toString(), WeeksBean.class);
                if (weeksBean.getData().getQuarter1()==1){
                    jiBnagdan.setOnClickListener(this);
                }
                adapter.notifyDataSetChanged();
                break;
        }
    }
}

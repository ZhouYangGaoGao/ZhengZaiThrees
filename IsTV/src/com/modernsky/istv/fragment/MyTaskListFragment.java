package com.modernsky.istv.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.http.RequestParams;
import com.modernsky.istv.R;
import com.modernsky.istv.acitivity.UserHomepageActivity;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.action.UserAction;
import com.modernsky.istv.adapter.CommonAdapter;
import com.modernsky.istv.adapter.ViewHolder;
import com.modernsky.istv.bean.ResultList;
import com.modernsky.istv.bean.TaskBean;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.TimeTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.PreferencesUtils;
import com.modernsky.istv.utils.ScreenUtils;
import com.modernsky.istv.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 个人主页--任务列表栏
 *
 * @author mufaith
 * @time 16/3/5 下午2:22
 */
public class MyTaskListFragment extends BaseFragment {

    private PullToRefreshListView mListView;
    private CommonAdapter<TaskBean> mAdapter;

    private List<TaskBean> mData = new ArrayList<>();
    private TaskBean bean;

    private ListView listView;

    private boolean hasData = true; //是否还有网络数据可添加


    private int mCurrentPage;

    private String userId;


    private UserHomepageActivity userHomepageActivity;


    private ProgressBar mBar;
    private boolean isCanLoginTime = false; //是否登陆时长够5分钟
    private boolean isCanGetShare = false;//是否已经分享过

    private long loginTime; //登陆时间戳
    private long lastTime;  //已登录时间

    private long shareTime;//分享时间戳

    private String curTimeStr; //5分钟登陆时长剩余时长显示 mm:ss
    private int curProgress; //当前进度条值
    private int mCount;
    private boolean isRefresh;

    private String[] titles;
    private TextView emptyView;



    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
//                    mBar.setProgress(curProgress);
                    break;
            }
        }
    }

    Runnable r = new Runnable() {


        @Override
        public void run() {
            mCount++;
            if (mCount == 3) {
                curProgress = curProgress + 1;
                mCount = 0;
            }
            long curTime = System.currentTimeMillis();
            curTimeStr = TimeTool.getTimeStr(300000 - curTime + loginTime);
            if (curTimeStr.equals("00:00")) {
                isCanLoginTime = true;
            }
            isRefresh = true;
            mAdapter.notifyDataSetChanged();
            if (!isCanLoginTime) {
                new MyHandler().postDelayed(r, 1000);
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userHomepageActivity = (UserHomepageActivity) getActivity();
        long curTime = System.currentTimeMillis();
        loginTime = PreferencesUtils.getLongPreferences(userHomepageActivity, PreferencesUtils.TYPE_LOGIN_IN_TIME);
        lastTime = curTime - loginTime;
        if (lastTime >= 300000) {
            isCanLoginTime = true;
        } else {
            curTimeStr = TimeTool.getTimeStr(300000 - lastTime);
            float tmp = lastTime / 300000.0f;
            if (tmp < 1) {
                curProgress = 1;
            } else {
                curProgress = Math.round(tmp * 100);
            }

        }
    }


    @Override
    public View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.userId=getArguments().getString("viewId");
        View rootView = inflater.inflate(R.layout.fragment_my_task, container, false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        long curTime = System.currentTimeMillis();
        loginTime = PreferencesUtils.getLongPreferences(userHomepageActivity, PreferencesUtils.TYPE_LOGIN_IN_TIME);
        lastTime = curTime - loginTime;
        if (lastTime >= 300000) {
            isCanLoginTime = true;
        } else {
            curTimeStr = TimeTool.getTimeStr(300000 - lastTime);
            float tmp = lastTime / 300000.0f;

            curProgress = Math.round(tmp * 100);

        }
        shareTime = PreferencesUtils.getLongPreferences(userHomepageActivity, PreferencesUtils.TYPE_SHARE_TIME);
        long zeroTime = TimeTool.getTimesmorning();

        if (0 != shareTime && shareTime >= zeroTime) {
            isCanGetShare = true;
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void initView(View rootView) {

//        titles = new String[]{"每日签到 送1个Power", "在线5分钟 送1个Power", "每日分享一次 送1个Power", "每日一赞主播 送1个Power", "每日送出1000M豆 " +
//                "送1个Power"};

        titles = new String[]{"每日打开应用奖励1Power", "每日App在线5分钟奖励1Power", "每日分享一次奖励1Power", "每日为选手点赞1次奖励1Power",
                "每日送出1000M豆礼物" +
                        "奖励1Power"};

        mListView = (PullToRefreshListView) rootView.findViewById(R.id.listView);

        listView = mListView.getRefreshableView();

        mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        emptyView = (TextView) rootView.findViewById(R.id.tv_zanwu);
//        listView.setEmptyView(emptyView);
        emptyView.setOnClickListener(this);

        mAdapter = new CommonAdapter<TaskBean>(userHomepageActivity, mData, R.layout.item_task_list) {
            @Override
            public void convert(ViewHolder helper, TaskBean item) {
                int type = item.getType();
                int left = ScreenUtils.dip2px(userHomepageActivity, 15);
                int right = left;
                int top = left / 2;
                int bottom = top;
                switch (type) {
                    case 3:
                        helper.setText(R.id.tv_title, (helper.getPosition() + 1) + "、每日一签");
                        helper.setText(R.id.tv_des, titles[0]);
                        if (item.getIsDone() == 1) {
                            helper.setVisibility(R.id.tv_get, View.GONE);
                            helper.setVisibility(R.id.iv_get, View.VISIBLE);
                            helper.setVisibility(R.id.iv_caomei, View.VISIBLE);
                            ((ImageView) helper.getView(R.id.iv_get)).setImageResource(R.drawable.icon_green_get);
                            ((ImageView) helper.getView(R.id.iv_caomei)).setImageResource(R.drawable.icon_green);
                        } else {
                            helper.setVisibility(R.id.tv_get, View.VISIBLE);
                            helper.setVisibility(R.id.iv_get, View.GONE);
                            helper.setVisibility(R.id.iv_caomei, View.GONE);
                            ((TextView) helper.getView(R.id.tv_get)).setTextColor(Color.parseColor("#00e3cb"));
                            ((TextView) helper.getView(R.id.tv_get)).setBackgroundResource(R.drawable.but_green);
                            ((TextView) helper.getView(R.id.tv_get)).setPadding(left, top, right, bottom);

                            helper.setText(R.id.tv_get, "领取");
                            ((TextView) helper.getView(R.id.tv_get)).setTag(item);

                            helper.setOnClickListener(R.id.tv_get, MyTaskListFragment.this);
                        }

                        break;
                    case 4:
                        helper.setText(R.id.tv_title, (helper.getPosition() + 1) +
                                "、我是App死忠粉");
                        helper.setText(R.id.tv_des, titles[1]);
                        if (item.getIsDone() == 1) {
                            helper.setVisibility(R.id.tv_get, View.GONE);
                            helper.setVisibility(R.id.rl_progress, View.GONE);
                            helper.setVisibility(R.id.iv_get, View.VISIBLE);
                            helper.setVisibility(R.id.iv_caomei, View.VISIBLE);
                            ((ImageView) helper.getView(R.id.iv_get)).setImageResource(R.drawable.icon_qin_get);
                            ((ImageView) helper.getView(R.id.iv_caomei)).setImageResource(R.drawable.icon_qin);
                        } else {

                            helper.setVisibility(R.id.iv_get, View.GONE);
                            helper.setVisibility(R.id.iv_caomei, View.GONE);

                            if (isCanLoginTime) {
                                helper.setVisibility(R.id.tv_get, View.VISIBLE);
                                helper.setVisibility(R.id.rl_progress, View.GONE);
                                ((TextView) helper.getView(R.id.tv_get)).setTextColor(Color.parseColor("#b5e69f"));
                                ((TextView) helper.getView(R.id.tv_get)).setBackgroundResource(R.drawable.but_qin);
                                ((TextView) helper.getView(R.id.tv_get)).setPadding(left, top, right, bottom);
                                helper.setText(R.id.tv_get, "领取");
                                ((TextView) helper.getView(R.id.tv_get)).setTag(item);
                                helper.setOnClickListener(R.id.tv_get, MyTaskListFragment.this);

                            } else {
                                //实时更新时间条
                                helper.setVisibility(R.id.tv_get, View.GONE);
                                helper.setVisibility(R.id.rl_progress, View.VISIBLE);
                                helper.setText(R.id.tv_time_past, "还差" + curTimeStr);
                                ((ProgressBar) helper.getView(R.id.proBar)).setProgress(curProgress);
                                if (!isRefresh) {
                                    new MyHandler().post(r);
                                }

                            }
                        }
                        break;
                    case 5:
                        helper.setText(R.id.tv_title, (helper.getPosition() + 1) + "、好东西你有我有大家有");
                        helper.setText(R.id.tv_des, titles[2]);
                        if (item.getIsDone() == 1) {
                            helper.setVisibility(R.id.tv_get, View.GONE);
                            helper.setVisibility(R.id.iv_get, View.VISIBLE);
                            helper.setVisibility(R.id.iv_caomei, View.VISIBLE);
                            ((ImageView) helper.getView(R.id.iv_get)).setImageResource(R.drawable.icon_yellow_get);
                            ((ImageView) helper.getView(R.id.iv_caomei)).setImageResource(R.drawable.icon_yellow);
                        } else {
                            helper.setVisibility(R.id.tv_get, View.VISIBLE);
                            helper.setVisibility(R.id.iv_get, View.GONE);
                            helper.setVisibility(R.id.iv_caomei, View.GONE);
                            ((TextView) helper.getView(R.id.tv_get)).setTextColor(Color.parseColor("#ffe535"));
                            if (isCanGetShare) {
                                ((TextView) helper.getView(R.id.tv_get)).setBackgroundResource(R.drawable.but_yellow);
                                ((TextView) helper.getView(R.id.tv_get)).setPadding(left, top, right, bottom);
                                helper.setText(R.id.tv_get, "领取");
                                ((TextView) helper.getView(R.id.tv_get)).setTag(item);

                                helper.setOnClickListener(R.id.tv_get, MyTaskListFragment.this);

                            } else {
                                helper.setText(R.id.tv_get, "未完成");
                                ((TextView) helper.getView(R.id.tv_get)).setTag(item);

                                helper.setOnClickListener(R.id.tv_get, MyTaskListFragment.this);
                            }
                        }
                        break;
                    case 6:
                        helper.setText(R.id.tv_title, (helper.getPosition() + 1) + "、点赞狂魔");
                        helper.setText(R.id.tv_des, titles[3]);
                        if (item.getIsDone() == 1) {
                            helper.setVisibility(R.id.tv_get, View.GONE);
                            helper.setVisibility(R.id.iv_get, View.VISIBLE);
                            helper.setVisibility(R.id.iv_caomei, View.VISIBLE);
                            ((ImageView) helper.getView(R.id.iv_get)).setImageResource(R.drawable.icon_orange_get);
                            ((ImageView) helper.getView(R.id.iv_caomei)).setImageResource(R.drawable.icon_orange);

                        } else {
                            helper.setVisibility(R.id.tv_get, View.VISIBLE);
                            helper.setVisibility(R.id.iv_get, View.GONE);
                            helper.setVisibility(R.id.iv_caomei, View.GONE);
                            ((TextView) helper.getView(R.id.tv_get)).setTextColor(Color.parseColor("#ff8964"));
                            if (item.getIsCan() == 1) {
                                ((TextView) helper.getView(R.id.tv_get)).setBackgroundResource(R.drawable.but_orange);
                                ((TextView) helper.getView(R.id.tv_get)).setPadding(left, top, right, bottom);
                                helper.setText(R.id.tv_get, "领取");
                                ((TextView) helper.getView(R.id.tv_get)).setTag(item);

                                helper.setOnClickListener(R.id.tv_get, MyTaskListFragment.this);
                            } else {
                                helper.setText(R.id.tv_get, "未完成");
                                ((TextView) helper.getView(R.id.tv_get)).setTag(item);

                                helper.setOnClickListener(R.id.tv_get, MyTaskListFragment.this);
                            }

                        }
                        break;
                    case 7:
                        helper.setText(R.id.tv_title, (helper.getPosition() + 1) + "、穷的只剩钱");
                        helper.setText(R.id.tv_des, titles[4]);
                        if (item.getIsDone() == 1) {
                            helper.setVisibility(R.id.tv_get, View.GONE);
                            helper.setVisibility(R.id.iv_get, View.VISIBLE);
                            helper.setVisibility(R.id.iv_caomei, View.VISIBLE);
                            ((ImageView) helper.getView(R.id.iv_get)).setImageResource(R.drawable.icon_zi_get);
                            ((ImageView) helper.getView(R.id.iv_caomei)).setImageResource(R.drawable.icon_zi);

                        } else {
                            helper.setVisibility(R.id.iv_get, View.GONE);
                            helper.setVisibility(R.id.iv_caomei, View.GONE);
                            if (item.getIsCan() == 1) {
                                helper.setVisibility(R.id.tv_get, View.VISIBLE);
                                helper.setVisibility(R.id.rl_progress, View.GONE);
                                ((TextView) helper.getView(R.id.tv_get)).setTextColor(Color.parseColor("#9A529E"));
                                ((TextView) helper.getView(R.id.tv_get)).setBackgroundResource(R.drawable.but_zi);
                                ((TextView) helper.getView(R.id.tv_get)).setPadding(left, top, right, bottom);
                                helper.setText(R.id.tv_get, "领取");
                                ((TextView) helper.getView(R.id.tv_get)).setTag(item);

                                helper.setOnClickListener(R.id.tv_get, MyTaskListFragment.this);
                            } else {
                                //更新送1000 Mb任务进度条
                                helper.setVisibility(R.id.tv_get, View.GONE);
                                helper.setVisibility(R.id.rl_progress, View.VISIBLE);
//                                helper.setVisibility(R.id.tv_time_past, View.GONE);
                                int leftNum = 1000 - item.getMbCount();
                                helper.setText(R.id.tv_time_past, "还差" + String.valueOf(leftNum) + "M豆");
                                float tmp = (float) item.getMbCount() / 1000;
                                int progress = Math.round(100 * tmp);
//                                if(progress<1){
//                                    progress=1;
//                                }
                                ((ProgressBar) helper.getView(R.id.proBar)).setProgress(progress);

                            }

                        }
                        break;
                }
            }
        };
        listView.setAdapter(mAdapter);

        initPtrListListenner();
        if (mData == null || mData.size() == 0)
            mListView.setRefreshing();


    }


    private void initPtrListListenner() {

        mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mCurrentPage = 1;
                getTaskList(userId);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (hasData) {
                    getTaskList(userId);
                } else {
                    mListView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Utils.toast(userHomepageActivity, "全部加载完毕");
                            mListView.onRefreshComplete();
                        }
                    }, 500);
                }
            }
        });
    }


    public void getTaskList(String userId) {
        RequestParams params = UrlTool.getPostParams(Constants.USER_ID, userId);
        SendActtionTool.post(Constants.UserParams.URL_TASK_LIST, null, UserAction.ACTION_TASK_LIST, this, params);
    }

    public void doTaskList(String userId, int type) {
        RequestParams params = UrlTool.getPostParams(Constants.USER_ID, userId, Constants.TYPE, type + "");
        SendActtionTool.post(Constants.UserParams.URl__DO_TASK, null, UserAction.ACTION_DO_TASK, this, params);
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        super.onSuccess(service, action, value);
        switch ((UserAction) action) {
            case ACTION_TASK_LIST:
                String json = value.toString();
                ResultList<TaskBean> result = JSON.parseObject(json, new TypeReference<ResultList<TaskBean>>() {
                });

                List<TaskBean> taskList = result.data;
                if (taskList != null && taskList.size() > 0) {
                    if (mCurrentPage == 1) {
                        mData.clear();
                        mListView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    }
                    if (taskList.size() > 5) {   //保留前五个任务,之后的任务待定...
                        List<TaskBean> removes = new ArrayList<>();
                        for (int i = 5; i < taskList.size(); i++) {
                            removes.add(taskList.get(i));
                        }
                        taskList.removeAll(removes);
                    }
                    mData.addAll(taskList);
                    mAdapter.notifyDataSetChanged();
//                    if (mCurrentPage > 1) {
//                        listView.setSelection(mData.size() - 1);
//
//                    }
                } else {
                    emptyView.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
//                    if (mData.size() != 0 && mCurrentPage != 1) {
//                        hasData = false;
//                        Utils.toast(userHomepageActivity, "全部加载完毕");
//                    }
                }
                LogUtils.d("ceshi", json);
                mListView.onRefreshComplete();
                mCurrentPage++;
                break;
            case ACTION_DO_TASK:
                userHomepageActivity.updateTab(2);
                switch (bean.getType()) {
                    case 3:
                        bean.setIsDone(1);
                        mAdapter.notifyDataSetChanged();
                        break;
                    case 4:
                        bean.setIsDone(1);
                        mAdapter.notifyDataSetChanged();
                        break;
                    case 5:
                        bean.setIsDone(1);
                        mAdapter.notifyDataSetChanged();
                        break;
                    case 6:
                        bean.setIsDone(1);
                        mAdapter.notifyDataSetChanged();
                        break;
                    case 7:
                        bean.setIsDone(1);
                        mAdapter.notifyDataSetChanged();
                        break;
                }
                Utils.toast(userHomepageActivity, "领取成功");
                break;

        }
    }

    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {
        super.onFaile(service, action, value);
        switch ((UserAction) action) {
            case ACTION_DO_TASK:
                Utils.toast(userHomepageActivity, "领取失败");
                break;
            case ACTION_TASK_LIST:
                if (mData.size() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void onFinish(ServiceAction service, Object action) {
        super.onFinish(service, action);
        switch ((UserAction) action) {
            case ACTION_TASK_LIST:
                mListView.onRefreshComplete();
                break;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_get:
                bean = (TaskBean) v.getTag();
                switch (bean.getType()) {
                    case 3:
                        doTaskList(userId, 3);
                        break;
                    case 4:

                        doTaskList(userId, 4);
                        break;
                    case 5:
                        if (!isCanGetShare) { //去分享

                        } else {
                            doTaskList(userId, 5);
                        }
                        break;
                    case 6:
                        if (bean.getIsCan() == 0) { //去点赞

                        } else {
                            doTaskList(userId, 6);
                        }
                        break;
                    case 7:
                        doTaskList(userId, 7);
                        break;
                }
                break;
            case R.id.tv_zanwu:
                getTaskList(userId);
                emptyView.setVisibility(View.GONE);
                break;
        }
    }
}

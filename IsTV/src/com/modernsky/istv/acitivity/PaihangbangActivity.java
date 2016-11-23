package com.modernsky.istv.acitivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.action.UserAction;
import com.modernsky.istv.adapter.CommonAdapter;
import com.modernsky.istv.adapter.ViewHolder;
import com.modernsky.istv.bean.ResultList;
import com.modernsky.istv.bean.UserEntity;
import com.modernsky.istv.tool.BitmapTool;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.TimeTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.PreferencesUtils;
import com.modernsky.istv.utils.Utils;
import com.modernsky.istv.view.DrageLayout;
import com.modernsky.istv.view.RoundAngleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuweizhi on 2016/2/22.
 */
public class PaihangbangActivity extends BaseActivity {
    PullToRefreshListView mPtrList;
    ListView mList;
    View mRankHeadView;
    CommonAdapter<UserEntity> commonAdapter;
    List<UserEntity> userEntityList;
    //    private TextView head_lable;
    //    private TextView head_name;
    //    private TextView head_tv_lable;
    //    private RoundAngleImageView head_img_user;
    //    private TextView head_tv_renqi;

    //    @ViewInject(R.id.img_back)
    //    ImageButton img_back;

    //音乐小球
    private DrageLayout mDrageLayout;
    private RelativeLayout mDrageView;
    RelativeLayout mDragButtomView;


    /**
     * =========================================zwz 3.3.7start=========================================
     */

    // 返回键
    @ViewInject(R.id.iv_back)
    ImageView mIv_back;
    // 标题
    @ViewInject(R.id.tv_title)
    TextView mTv_title;
    // 开始日期
    @ViewInject(R.id.tv_start_date)
    TextView mTv_start_date;
    // 截止日期
    //    @ViewInject(R.id.tv_end_date)
    //    TextView mTv_end_date;
    // 时间轴button
    //    @ViewInject(R.id.iv_time_axis)
    //    ImageView mIv_time_axis;

    // 打榜没有消息
    @ViewInject(R.id.tv_zanwu)
    TextView mTv_zanwu;

    private RoundAngleImageView mRaiv_first_head;
    private TextView mTv_first_human_count;
    private TextView mTv_ranking_first_name;
    private TextView mTv_red_net_name;

    private ArrayList<Integer> mPostionImageList;
    private View mRankFootView;
    private LinearLayout mLl_champion;
    private long mExperCount;
    private LinearLayout mLl_da_bang_explain;

    /**
     * =========================================zwz   3.3.7end=========================================
     */


    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_paihangbang);
        ViewUtils.inject(this);
        initDrager();
    }

    /**
     * 音乐小球
     */
    private void initDrager() {
        mDrageLayout = (DrageLayout) findViewById(R.id.drageLayout);
        mDrageView = (RelativeLayout) findViewById(R.id.draglayoutView);
        mDragButtomView = (RelativeLayout) findViewById(R.id.layoutButtom_drag);
        mDrageLayout.setView(mDrageView, mDragButtomView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDrageLayout.initDrageLayoutPosition();
    }

    @Override
    public void findViewById() {
        initPositionImageData();
        mIv_back.setOnClickListener(this);
        mPtrList = (PullToRefreshListView) findViewById(R.id.ptr_layout_list);
        mPtrList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getDate();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
        mPtrList.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mList = mPtrList.getRefreshableView();

        // headView = getLayoutInflater().inflate(R.layout.head_paihangbang, null);

        // zwz 冠军布局
        mRankHeadView = getLayoutInflater().inflate(R.layout.item_ranking_head, null);

        // zwz 尾布局
        // 打榜说明布局
        mRankFootView = getLayoutInflater().inflate(R.layout.item_ranking_foot, null);


        // initHeadView(headView);
        // mList.addHeaderView(headView);

        // zwz 初始化头部
        initHeadView(mRankHeadView);
        // zwz 初始化尾部
        initFootView();

        mList.addHeaderView(mRankHeadView);
        mList.addFooterView(mRankFootView);

        // 1474992000000 2016年9月28日0:0:0
        long startTime = 1474992000;
        // 获取当前的时间戳
        long currentTime = System.currentTimeMillis() / 1000;
        // 一周的时间
        long weekTime = 7 * 24 * 3600;
        // 计算出一共几周,剩余多少毫秒
        long yuTime = (currentTime - startTime) % weekTime;
        long weekLastTime = currentTime - yuTime;
        long weekStartTime = weekLastTime - weekTime;

        String time = TimeTool.getFormaTime_(weekStartTime * 1000) + "-" + TimeTool.getFormaTime_(weekLastTime * 1000 - 1000);
        mTv_start_date.setText(time);


        initListView();
        getDate();

    }

    /**
     * 初始化打榜
     */
    private void initFootView() {
        // 打榜说明
        mLl_da_bang_explain = (LinearLayout) mRankFootView.findViewById(R.id.ll_da_bang_explain);
        mLl_da_bang_explain.setOnClickListener(this);
    }


    /**
     * 初始化名次图片
     */
    private void initPositionImageData() {
        mPostionImageList = new ArrayList<>();
        mPostionImageList.add(R.drawable.num_two);
        mPostionImageList.add(R.drawable.num_three);
        mPostionImageList.add(R.drawable.num_four);
        mPostionImageList.add(R.drawable.num_five);
        mPostionImageList.add(R.drawable.num_six);
        mPostionImageList.add(R.drawable.num_seven);
        mPostionImageList.add(R.drawable.num_eight);
        mPostionImageList.add(R.drawable.num_nine);
        mPostionImageList.add(R.drawable.num_ten);
    }


    /**
     * 初始化ListView布局
     */
    private void initListView() {
        if (commonAdapter == null)
            userEntityList = new ArrayList<UserEntity>();

        commonAdapter = new CommonAdapter<UserEntity>(this, userEntityList, R.layout.item_ranking_child) {
            @Override
            public void convert(ViewHolder helper, final UserEntity item) {
               /* // 头像图片地址
                helper.setImageByUrl(R.id.item_img_user, item.getFaceUrl());
                // 乐队名称
                helper.setText(R.id.item_user_name, item.getUserName());
                // 直播霸霸or直播网红
                helper.setText(R.id.item_tv_lable, item.getBadge().getName());
                // 主播等级
                helper.setText(R.id.tv_lable, item.getRank().getRank());
                // 人气数量
                helper.setText(R.id.item_tv_count, item.getExper() + "");
                // 排行榜名次
                helper.setText(R.id.item_tv_index, (helper.getPosition() + 2) + "");*/

                /* View view = helper.getView(R.id.v_line);
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                int wid = (helper.getPosition() + 1) * getWindowManager().getDefaultDisplay().getWidth() / 10;
                layoutParams.width = getWindowManager().getDefaultDisplay().getWidth() - wid;
                view.setLayoutParams(layoutParams);*/

                // 头像图片地址
                helper.setImageByUrl(R.id.raiv_other_head, item.getFaceUrl());
                // 乐队名称
                helper.setText(R.id.tv_ranking_other_name, item.getUserName());
                // 直播霸霸or直播网红
                helper.setText(R.id.tv_red_net_other_name, item.getBadge().getName());

                // 人气数量
                helper.setText(R.id.tv_other_human_count, String.valueOf(item.getExper()));

                // 排行榜名次
                helper.setImageResource(R.id.iv_rank_position, item.getPositionImage());

                // zwz排行榜人气进度条

                LogUtils.d("haha", "===================" + calcExperCount(item.getExper()) + "===========================");
                helper.setProgress(R.id.pb_other_progress, calcExperCount(item.getExper()));


                // zwz 第二和第三名字体颜色变蓝
                if (commonAdapter.getPosition() < 2) {
                    helper.setTextColor(R.id.tv_ranking_other_name, getResources().getColor(R.color.ren_qi_blue));
                    helper.setTextColor(R.id.tv_ren_qi, getResources().getColor(R.color.ren_qi_blue));
                    helper.setTextColor(R.id.tv_other_human_count, getResources().getColor(R.color.ren_qi_blue));
                }

                // 点击每一个条目跳转的界面ID
                helper.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.OpenUserInfo(PaihangbangActivity.this, item.getId(), "1");
                    }
                });

            }
        };
        mList.setAdapter(commonAdapter);
    }

    /**
     * 计算每一个人气进度条
     *
     * @return
     */
    private int calcExperCount(long itemExper) {
        double progressDouble = (double) itemExper / mExperCount;

        LogUtils.d("hehe", "===================" + progressDouble + "===========================");

        int progressInt = (int) (progressDouble * 100);

        LogUtils.d("hehe", "===================" + progressInt + "===========================");

        return progressInt;
    }

    /**
     * 获取网路数据
     */
    private void getDate() {
        //        SendActtionTool.post(Constants.UserParams.URL_ANCHOR_TOP, null, UserAction.Action_GET_ZHUBO_RANK, this);

        // zwz 更改接口
        RequestParams params = UrlTool.getParams("way", "week");
        SendActtionTool.get(Constants.UserParams.URL_ANCHOR_TOP, null, UserAction.Action_GET_ZHUBO_RANK, this, params);
    }

    /**
     * 初始化冠军布局
     *
     * @param headView
     */
    private void initHeadView(View headView) {
        //        head_lable = (TextView) headView.findViewById(R.id.tv_lable);
        //        head_name = (TextView) headView.findViewById(R.id.textView11);
        //        head_tv_lable = (TextView) headView.findViewById(R.id.textView12);
        //        head_tv_renqi = (TextView) headView.findViewById(R.id.tv_renqi);
        //        head_img_user = (RoundAngleImageView) headView.findViewById(R.id.img_user);

        // zwz
        // 冠军整体布局
        mLl_champion = (LinearLayout) mRankHeadView.findViewById(R.id.ll_champion);
        // 冠军乐队名称
        mTv_ranking_first_name = (TextView) mRankHeadView.findViewById(R.id.tv_ranking_first_name);
        // 网络名称
        mTv_red_net_name = (TextView) mRankHeadView.findViewById(R.id.tv_red_net_name);
        // 冠军人气数量
        mTv_first_human_count = (TextView) mRankHeadView.findViewById(R.id.tv_first_human_count);
        // 冠军头像
        mRaiv_first_head = (RoundAngleImageView) mRankHeadView.findViewById(R.id.raiv_first_head);

    }

    /**
     * 给冠军布局添加数据
     *
     * @param entity
     */
    private void updateHeadView(final UserEntity entity) {
        //        head_name.setText(entity.getUserName());
        //        head_lable.setText(entity.getRank().getRank());
        //        head_tv_lable.setText(entity.getBadge().getName());
        //        head_tv_renqi.setText(String.valueOf(entity.getExper()));
        //        BitmapTool.getInstance().getAdapterUitl().display(head_img_user, entity.getFaceUrl());

        // 冠军名字
        mTv_ranking_first_name.setText(entity.getUserName());
        // 冠军网络名称
        mTv_red_net_name.setText(entity.getBadge().getName());
        // 冠军人气数
        mExperCount = entity.getExper();
        mTv_first_human_count.setText(String.valueOf(mExperCount));
        // 冠军头像
        BitmapTool.getInstance().getAdapterUitl().display(mRaiv_first_head, entity.getFaceUrl());

        mLl_champion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.OpenUserInfo(PaihangbangActivity.this, entity.getId(), "1");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.ll_da_bang_explain:
                Intent intent = new Intent(PaihangbangActivity.this, DabangAboutActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_time_axis:
                Intent intentTime = new Intent(PaihangbangActivity.this, TimerShaftActivity.class);
                startActivityForResult(intentTime, 100);
                break;
        }
    }

    /**
     * 从第二个界面获取数据
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            int code = data.getIntExtra("code", 0);
            switch (code) {
                // 1 周排行
                case 1:
                    String weekTime = data.getStringExtra("time");
                    String weekXWeek = data.getStringExtra("top");
                    mTv_title.setText("周排行");
                    mTv_start_date.setText(weekTime);

                    LogUtils.d("zwz=======week", weekXWeek);

                    RequestParams paramsWeek = UrlTool.getParams("way", weekXWeek);
                    SendActtionTool.get(Constants.UserParams.URL_ANCHOR_TOP, null, UserAction.Action_GET_ANY_ZHUBO_RANK, this, paramsWeek);

                    LogUtils.d("zwz=======week", Constants.UserParams.URL_ANCHOR_TOP + paramsWeek);
                    break;

                // 2  月排行
                case 2:
                    String monthTime = data.getStringExtra("time");
                    String weekXMonth = data.getStringExtra("top");
                    mTv_title.setText("月排行");
                    mTv_start_date.setText(monthTime);

                    LogUtils.d("zwz=======month", weekXMonth);

                    RequestParams paramsMonth = UrlTool.getParams("way", weekXMonth);
                    SendActtionTool.get(Constants.UserParams.URL_ANCHOR_TOP, null, UserAction.Action_GET_ANY_ZHUBO_RANK, this, paramsMonth);

                    LogUtils.d("zwz=======month", Constants.UserParams.URL_ANCHOR_TOP + paramsMonth);
                    break;

                // 3 季排行
                case 3:
                    String quarterTime = data.getStringExtra("time");
                    String weekXQuarter = data.getStringExtra("top");
                    mTv_title.setText("季排行");
                    mTv_start_date.setText(quarterTime);

                    LogUtils.d("zwz=======quarter", weekXQuarter);

                    RequestParams paramsQuarter = UrlTool.getParams("way", weekXQuarter);
                    SendActtionTool.get(Constants.UserParams.URL_ANCHOR_TOP, null, UserAction.Action_GET_ANY_ZHUBO_RANK, this, paramsQuarter);
                    LogUtils.d("zwz=======quarter", Constants.UserParams.URL_ANCHOR_TOP + paramsQuarter);
                    break;

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        super.onSuccess(service, action, value);
        switch ((UserAction) action) {
            case Action_GET_ZHUBO_RANK:
                // 解析主播数据,映射到Javabean.
                mPtrList.onRefreshComplete();

                LogUtils.d("zwz=========time", value.toString());

                try {
                    ResultList<UserEntity> tempList = JSON.parseObject(value.toString(), new TypeReference<ResultList<UserEntity>>() {
                    }.getType());
                    List<UserEntity> tempUserList = tempList.data;

                    if (tempUserList != null && tempUserList.size() > 0) {
                        int size = tempUserList.size();
                        updateHeadView(tempUserList.get(0));
                        tempUserList.remove(0);

                        //zwz 将排行图片添加到tempUserList集合中
                        addPositionImage(tempUserList, size);

                        userEntityList.clear();
                        userEntityList.addAll(tempUserList);

                        commonAdapter.notifyDataSetChanged();
                    }

                    // 保存数据
                    savelocalFile(PreferencesUtils.RANK_ACTION, value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case Action_GET_ANY_ZHUBO_RANK:
                // 解析主播数据,映射到Javabean.
                mPtrList.onRefreshComplete();

                LogUtils.d("zwz=========any", value.toString());

                try {
                    ResultList<UserEntity> tempList = JSON.parseObject(value.toString(), new TypeReference<ResultList<UserEntity>>() {
                    }.getType());
                    List<UserEntity> tempUserList = tempList.data;

                    if (tempUserList != null && tempUserList.size() > 0) {
                        int size = tempUserList.size();
                        updateHeadView(tempUserList.get(0));
                        tempUserList.remove(0);

                        //zwz 将排行图片添加到tempUserList集合中
                        addPositionImage(tempUserList, size);

                        userEntityList.clear();
                        userEntityList.addAll(tempUserList);

                        commonAdapter.notifyDataSetChanged();
                    }

                    // 保存数据
                    savelocalFile(PreferencesUtils.RANK_ACTION, value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;


        }
    }

    @Override
    public void onException(ServiceAction service, Object action, Object value) {
        super.onException(service, action, value);
        switch ((UserAction) action) {
            case Action_GET_ZHUBO_RANK:
                // 请求网络异常,获取本地数据
                try {
                    String jsonString = PreferencesUtils.getPreferences(getApplicationContext(), PreferencesUtils.RANK_ACTION);
                    ResultList<UserEntity> tempList = JSON.parseObject(jsonString, new TypeReference<ResultList<UserEntity>>() {
                    }.getType());
                    List<UserEntity> tempUserList = tempList.data;

                    if (tempUserList != null && tempUserList.size() > 0) {
                        int size = tempUserList.size();
                        updateHeadView(tempUserList.get(0));
                        tempUserList.remove(0);

                        //zwz 将排行图片添加到tempUserList集合中
                        addPositionImage(tempUserList, size);

                        userEntityList.clear();
                        userEntityList.addAll(tempUserList);

                        commonAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mPtrList.onRefreshComplete();

                break;

            case Action_GET_ANY_ZHUBO_RANK:
                LogUtils.d("zwz===============哈哈", "======eeeee=====");
                mPtrList.setVisibility(View.GONE);
                mTv_zanwu.setVisibility(View.VISIBLE);
                return;
        }
    }

    /**
     * 将排行榜名次图片添加到tempUserList集合中
     *
     * @param userList
     */
    private void addPositionImage(List<UserEntity> userList, int size) {
        if (-1 == (size - 1)) {
            mPtrList.setVisibility(View.GONE);
            mTv_zanwu.setVisibility(View.VISIBLE);
            return;
        }
        for (int i = 0; i < userList.size(); i++) {
            userList.get(i).setPositionImage(mPostionImageList.get(i));
        }
    }


    /**
     * 将数据保存到本地
     *
     * @param action
     * @param object
     */
    public void savelocalFile(String action, Object object) {
        PreferencesUtils.savePreferences(getApplicationContext(), action,
                object.toString());
    }


}

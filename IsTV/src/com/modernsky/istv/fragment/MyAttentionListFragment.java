package com.modernsky.istv.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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
import com.modernsky.istv.bean.UserEntity;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.Utils;
import com.modernsky.istv.view.RoundAngleImageView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * 个人主页--关注列表栏
 *
 * @author mufaith
 * @time 16/3/4 下午7:40
 */


public class MyAttentionListFragment extends BaseFragment {

    private PullToRefreshListView mListView;
    private CommonAdapter<UserEntity> mAdapter;
    private UserHomepageActivity userHomepageActivity;
    private List<UserEntity> mData = new ArrayList<UserEntity>();

    private int mCurrentPage;

    private String userId;
    private ListView listView;

    private boolean hasData = true;
    private boolean isSelf;
    private UserEntity bean;
    private TextView emptyView;

//    public MyAttentionListFragment() {
//        super();
//
//    }
//
//    public MyAttentionListFragment(String userId, boolean isSelf) {
//
//        this.userId = userId;
//        this.isSelf = isSelf;
//    }


    @Override
    public View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle=getArguments();
        this.userId = bundle.getString("viewId");
        this.isSelf =  bundle.getBoolean("isSelf");
        View rootView = inflater.inflate(R.layout.fragment_my_attention, container, false);
        return rootView;
    }


    @Override
    public void initView(View rootView) {
        mListView = (PullToRefreshListView) rootView.findViewById(R.id.listView);
        userHomepageActivity = (UserHomepageActivity) getActivity();
        listView = mListView.getRefreshableView();

        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        emptyView = (TextView) rootView.findViewById(R.id.tv_zanwu);
//        listView.setEmptyView(emptyView);
        emptyView.setOnClickListener(this);

        mAdapter = new CommonAdapter<UserEntity>(userHomepageActivity, mData, R.layout.item_attention_list) {

            @Override
            public void convert(ViewHolder helper, final UserEntity item) {

                helper.setImageByUrl(R.id.im_user, item.getFaceUrl());
                if (item.getSex() == 1) {
                    helper.setImageResource(R.id.iv_gender, R.drawable.icon_man);
                } else {
                    helper.setImageResource(R.id.iv_gender, R.drawable.icon_woman);
                }
                helper.setText(R.id.tv_name, item.getUserName());
                if (item.getBadge()!=null) {
                    helper.setText(R.id.tv_type, item.getBadge().getName());
                }
                helper.setOnClickListener(R.id.im_user, MyAttentionListFragment.this);
                ((RoundAngleImageView) helper.getView(R.id.im_user)).setTag(item);

                if (isSelf) {
                    helper.setVisibility(R.id.tv_guanzhu, View.GONE);
                    if (item.getStatus() == 6) {

                        if (item.getVideo() != null) {
                            helper.setOnClickListener(R.id.ll_zhibo, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Utils.playShow(getActivity(), item.getVideo().getVideoId() + "", item.getId());
                                }
                            });
                            helper.setVisibility(R.id.ll_yugao, View.GONE);
                            helper.setVisibility(R.id.ll_zhibo, View.VISIBLE);

                        } else if (item.getHerald() != null) {
                            helper.setOnClickListener(R.id.ll_yugao, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String url = item.getHerald().getUrl();

                                    Utils.startH5(getActivity(), item.getHerald().getName(), item.getHerald()
                                            .getId()
                                            + "", url, item.getFaceUrl(), 6);
                                    LogUtils.d("ceshi", "预告名称:" + item.getHerald().getName() + ";预告id: " + item
                                            .getHerald().getId() + ";Url: " + url);
                                }
                            });
                            helper.setVisibility(R.id.ll_zhibo, View.GONE);
                            helper.setVisibility(R.id.ll_yugao, View.VISIBLE);
                        } else {
                            helper.setVisibility(R.id.ll_zhibo, View.GONE);
                            helper.setVisibility(R.id.ll_yugao, View.GONE);
                        }


//                            helper.getConvertView().setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    long curTime = System.currentTimeMillis();
//                                    long endTime = Long.parseLong(item.getHerald().getEndTime());
//                                    ((LinearLayout) v.findViewById(R.id.ll_zhibo)).setVisibility(View.VISIBLE);
//                                    if (curTime >= item.getHerald().getStartTime() && endTime > curTime) {
//                                        startActivity(new Intent(userHomepageActivity, UserHomepageActivity.class)
//                                                .putExtra("userId", item.getId()));
//                                    }
//                                }
//                            });

                    } else {
                        helper.setVisibility(R.id.ll_zhibo, View.GONE);
                        helper.setVisibility(R.id.ll_yugao, View.GONE);

                    }
                } else {
                    helper.setVisibility(R.id.ll_zhibo, View.GONE);
                    helper.setVisibility(R.id.ll_yugao, View.GONE);
                    if (item.getId().equals(UserService.getInatance().getUserBean(userHomepageActivity).getId())) {
                        helper.setVisibility(R.id.tv_guanzhu, View.GONE);

                    } else {

                        helper.setVisibility(R.id.tv_guanzhu, View.VISIBLE);
                        if (item.getIsAttention() == 0) {
                            helper.setText(R.id.tv_guanzhu, "关注");
                            ((TextView) helper.getView(R.id.tv_guanzhu)).setTextColor(Color.parseColor("#24e7a9"));
                            ((TextView) helper.getView(R.id.tv_guanzhu)).setBackgroundResource(R.drawable
                                    .but_guanzhu_hl);
                        } else {
                            helper.setText(R.id.tv_guanzhu, "已关注");
                            ((TextView) helper.getView(R.id.tv_guanzhu)).setTextColor(Color.parseColor("#808282"));
                            ((TextView) helper.getView(R.id.tv_guanzhu)).setBackgroundResource(R.drawable.but_guanzhu);
                        }
                        helper.setOnClickListener(R.id.tv_guanzhu, MyAttentionListFragment.this);
                        ((TextView) helper.getView(R.id.tv_guanzhu)).setTag(item);
                    }
                }


            }
        };
        listView.setAdapter(mAdapter);
        initPtrListListenner();
        if (mData == null || mData.size() == 0)
            mListView.setRefreshing();

    }


    private void initPtrListListenner() {

        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mCurrentPage = 1;
                getAttentionList(userId);
                userHomepageActivity.updateTab(0);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (hasData) {
                    getAttentionList(userId);
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

    public void getAttentionList(String userId) {
        RequestParams params = UrlTool.getPostParams(Constants.USER_ID, userId, Constants.UserParams.TYPE, "2", Constants
                .PAGE, mCurrentPage + "");

        SendActtionTool.post(Constants.UserParams.URL_GET_ATTENTION_FANS, null, UserAction.ACTION_GET_FOCUS_LIST,
                this, params);

    }


    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        super.onSuccess(service, action, value);
        switch ((UserAction) action) {
            case ACTION_GET_FOCUS_LIST:
                String json = value.toString();
                ResultList<UserEntity> result = JSON.parseObject(json, new TypeReference<ResultList<UserEntity>>() {
                });

                List<UserEntity> entityList = result.data;
                if (entityList != null && entityList.size() > 0) {
                    if (mCurrentPage == 1) {
                        mListView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                        mData.clear();
                    }
                    mData.addAll(entityList);
                    if (mCurrentPage > 1) {
                        listView.setSelection(mData.size() - 1);

                    }
                    mAdapter.notifyDataSetChanged();
                } else {
                    if (mData.size() != 0 && mCurrentPage != 1) {
                        hasData = false;
                        Utils.toast(userHomepageActivity, "全部加载完毕");
                    } else {
                        emptyView.setVisibility(View.VISIBLE);
                        mListView.setVisibility(View.GONE);
                    }
                }
                mListView.onRefreshComplete();
                mCurrentPage++;
                LogUtils.d("ceshi", "关注列表--" + json);
                break;
            case ACTION_USER_ATTENTION://关注-取消关注
                org.json.JSONObject jObj = (org.json.JSONObject) value;
                int isAttention = 0;
                try {
                    isAttention = jObj.getJSONObject(Constants.DATA).getInt("isAttention");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (isAttention == 1) {
                    bean.setIsAttention(1);
                    Utils.toast(userHomepageActivity, "已关注");
//                    mAdapter.notifyDataSetChanged();

                } else {
                    bean.setIsAttention(0);
                    Utils.toast(userHomepageActivity, "已取消关注");
//                    mAdapter.notifyDataSetChanged();
                }

                break;
        }

    }

    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {
        super.onFaile(service, action, value);
        switch ((UserAction) action) {
            case ACTION_GET_FOCUS_LIST:
                if(mData.size()==0&&mCurrentPage==1){
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
            case ACTION_GET_FOCUS_LIST:
                mListView.onRefreshComplete();
                break;
        }
    }
    private void changeGuanzhu(String toUserId) {
        RequestParams params = UrlTool.getPostParams(Constants.USER_ID, UserService.getInatance().getUserBean
                (userHomepageActivity).getId(), Constants.TO_USER_ID, toUserId);

        SendActtionTool.post(Constants.UserParams.URL_ADD_ATTENTION, null, UserAction.ACTION_USER_ATTENTION,
                this, params);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_guanzhu:
                bean = (UserEntity) v.getTag();
                if (bean.getIsAttention() == 0) {
                    ((TextView) v).setText("已关注");
                    ((TextView) v).setTextColor(Color.parseColor("#808282"));
                    ((TextView) v).setBackgroundResource(R.drawable.but_guanzhu);

                } else {
                    ((TextView) v).setText("关注");
                    ((TextView) v).setTextColor(Color.parseColor("#24e7a9"));
                    ((TextView) v).setBackgroundResource(R.drawable.but_guanzhu_hl);
                }
                changeGuanzhu(bean.getId());
                break;
            case R.id.im_user:
                bean = (UserEntity) v.getTag();
                startActivity(new Intent(userHomepageActivity, UserHomepageActivity.class)
                        .putExtra("userId", bean.getId()));
                break;
            case R.id.tv_zanwu:
                getAttentionList(userId);
                emptyView.setVisibility(View.GONE);
                break;

        }

    }
}

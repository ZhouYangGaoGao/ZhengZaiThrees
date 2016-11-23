package com.modernsky.istv.fragment;

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
import com.modernsky.istv.adapter.MyWorkListAdapter;
import com.modernsky.istv.bean.ResultList;
import com.modernsky.istv.bean.WorkBean;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.StringUtils;
import com.modernsky.istv.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 个人主页--作品列表栏
 *
 * @author mufaith
 * @time 16/3/5 下午12:11
 */
public class MyWorksListFragment extends BaseFragment {
    private PullToRefreshListView mListView;
    private UserHomepageActivity userHomepageActivity;
    private List<WorkBean> mData = new ArrayList<>();
    private WorkBean bean;

    private int mCurrentPage;

    private String userId;
    private String addVideoId;
    private boolean isSelf, isAnchor;

    private ListView listView;

    private boolean hasData = true;
    private MyWorkListAdapter mAdapter;

    private TextView emptyView;

//    public MyWorksListFragment() {
//        super();
//    }
//
//    public MyWorksListFragment(String userId, String addVideoId, boolean isSelf, boolean isAnchor) {
//        this.userId = userId;
//        this.isSelf = isSelf;
//        this.isAnchor = isAnchor;
//        this.addVideoId = addVideoId;
//    }

    @Override
    public View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle=getArguments();
        this.userId = bundle.getString("viewId");
        this.addVideoId = bundle.getString("addVideoId");
        this.isSelf =  bundle.getBoolean("isSelf");
        this.isAnchor=  bundle.getBoolean("isAnchor");
        View rootView = inflater.inflate(R.layout.fragment_my_works, container, false);
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

        mAdapter = new MyWorkListAdapter(mData, userHomepageActivity.getLayoutInflater(), userHomepageActivity
                .getWindowManager().getDefaultDisplay().getWidth(), isSelf, isAnchor);

        mAdapter.setOnclickListener(this);
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
                userHomepageActivity.updateTab(3);
                getWorkList(userId);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (hasData) {
                    getWorkList(userId);
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

    public void getWorkList(String userId) {
        RequestParams params = UrlTool.getPostParams(Constants.USER_ID, userId, Constants.UserParams.TERMINAL, Constants
                .MOBILE, Constants.PAGE, mCurrentPage + "");

        SendActtionTool.post(Constants.UserParams.URl_VIDEO_LIST, null, UserAction.ACTION_VIDEO_LIST,
                this, params);

    }

    @Override
    public void onStart(ServiceAction service, Object action) {
        super.onStart(service, action);
        switch ((UserAction) action) {
            case ACTION_WORKS_DEL:
                showLoadingDialog();
                break;
        }
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        super.onSuccess(service, action, value);
        switch ((UserAction) action) {
            case ACTION_VIDEO_LIST:
                String json = value.toString();
                ResultList<WorkBean> result = JSON.parseObject(json, new TypeReference<ResultList<WorkBean>>() {
                });

                List<WorkBean> workList = result.data;
                if (workList != null && workList.size() != 0) {
                    if (workList.size() < 10) {
                        hasData = false;
                    }
                    if (mCurrentPage == 1) {
                        mData.clear();
                        mListView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    }
                    mData.addAll(workList);
                    if (mCurrentPage > 1) {
                        listView.setSelection(mData.size() - 1);

                    }
                    mAdapter.notifyDataSetChanged();
                } else {
                    if (mData.size() != 0 && mCurrentPage != 1) {
                        hasData = false;
                        Utils.toast(userHomepageActivity, "全部加载完毕");
                    }else{
                        emptyView.setVisibility(View.VISIBLE);
                        mListView.setVisibility(View.GONE);
                    }
                }
                mListView.onRefreshComplete();
                mCurrentPage++;
                break;
            case ACTION_WORKS_DEL:
                String json2 = value.toString();
                userHomepageActivity.updateTab(3);
                if (StringUtils.isEmpty(addVideoId)) {
                    Utils.toast(userHomepageActivity, R.string._del_success);
                } else {
                    Utils.toast(userHomepageActivity, "删除并添加成功");
                }
                if (bean != null) {
                    if (mData.contains(bean)) {
                        mData.remove(bean);
                        listView.setAdapter(mAdapter);
                    }
                }
                LogUtils.d("ceshi", "删除返回:" + json2);
                break;
        }

    }

    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {
        super.onFaile(service, action, value);
        switch ((UserAction) action){
            case ACTION_VIDEO_LIST:
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
            case ACTION_WORKS_DEL:
                dismissDialog();
                break;
            case ACTION_VIDEO_LIST:
                mListView.onRefreshComplete();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_delete_shoucangBtn:
                bean = (WorkBean) v.getTag();
                RequestParams params = null;
                if (StringUtils.isEmpty(addVideoId)) {
                    params = UrlTool.getPostParams("delVideoId", bean.getVideoId() + ""); //仅删除
                } else {
                    params = UrlTool.getPostParams("delVideoId", bean.getVideoId() + "", "AddVideoId", addVideoId);//删除并添加作品
                }
                SendActtionTool.post(Constants.UserParams.URL_WORKS_DEL, null, UserAction.ACTION_WORKS_DEL,
                        this, params);
                break;
            case R.id.ll_left_content:
                bean = (WorkBean) v.getTag();
                long curTime = System.currentTimeMillis();
                if (bean.getVideoType() == 4) {
                    if(curTime>bean.getEndTime()){
                        return;
                    }else{
                        Utils.playShow(getActivity(), bean.getVideoId() + "", bean.getUserId());
                    }
                } else if (bean.getVideoType() == 5) {
                    String url = bean.getUrl();
                    String str;
                    if (url.contains("?"))
                        str = "&singerId=";
                    else
                        str = "?singerId=";
                    url = url + str + bean.getUserId();

                    Utils.startH5(getActivity(), bean.getAlbumName(), bean.getAlbumId() + "", url, bean.getFaceUrl(), 6);
                } else {
                    LogUtils.d("ceshi", "点播id:" + bean.getVideoId());
                    Utils.playLookForwordDemoVideo(getActivity(), bean.getVideoId() + "", bean.getUserId());
                }
                break;
            case R.id.tv_zanwu:
                getWorkList(userId);
                emptyView.setVisibility(View.GONE);
                break;
        }
    }
}

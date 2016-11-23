package com.modernsky.istv.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.http.RequestParams;
import com.modernsky.istv.R;
import com.modernsky.istv.acitivity.DormMusicActivity;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.action.UserAction;
import com.modernsky.istv.adapter.CommonAdapter;
import com.modernsky.istv.adapter.ViewHolder;
import com.modernsky.istv.bean.ResultList;
import com.modernsky.istv.bean.WorkBean;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.TimeTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zqg on 2016/2/22.
 * 宿舍音乐节列表frg
 */
public class DormMusicFragment extends BaseFragment {
    PullToRefreshListView mPtrList;
    ListView mList;
    int index;//0 是左边的fragment 1 是右边的fragment
    private String mCitys;
    CommonAdapter<WorkBean> commonAdapter;
    private List<WorkBean> workBeanList = null;
    DormMusicActivity mActivity;
    protected int mPage = 1;


    @Override
    public View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = (DormMusicActivity) getActivity();
        workBeanList = new ArrayList<WorkBean>();
        return inflater.inflate(R.layout.layout_list, container, false);
    }

    public void getContent(String cityIds, int sort, boolean update) {
        if (update) {
            mPage = 1;
        }
        LogUtils.d("getContent---mPage=="+mPage);
        RequestParams params = UrlTool.getPostParams(Constants.TYPE, (index == 0) ? "4" : "1", Constants.CITYIDS, cityIds, Constants.SORT, sort + "", Constants.PAGE, mPage + "");
        SendActtionTool.post(Constants.URL_GET_USER_VIDEO, null, UserAction.ACTION_GET_USER_VIDEO, this, params);
        if (isVisible()) {
            showLoadingDialog();
        }
    }

    @Override
    public void initView(View rootView) {
        index = getArguments().getInt(Constants.INDEX);
        mPtrList = (PullToRefreshListView) rootView.findViewById(R.id.ptr_layout_list);
        mPtrList.setMode(PullToRefreshBase.Mode.BOTH);
        mPtrList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPage = 1;
                getContent(mActivity.getmCityId(), mActivity.getmSort(), false);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getContent(mActivity.getmCityId(), mActivity.getmSort(), false);
            }
        });
        mList = mPtrList.getRefreshableView();
//        mList.setEmptyView(rootView.findViewById(R.id.tv_zanwu));
        switch (index) {
            case 0:
                break;
            case 1:
                break;
        }
        updateView();
//        getContent(mActivity.getmCityId(), mActivity.getmSort(), false);
        //
    }

    private void updateView() {
        if (commonAdapter == null) {
            commonAdapter = new CommonAdapter<WorkBean>(mActivity, workBeanList, R.layout.item_work_info) {
                @Override
                public void convert(ViewHolder helper, final WorkBean item) {
                    helper.setImageByUrl(R.id.item_img_bg, item.getVideoPic());
                    helper.setImageResource(R.id.iv_layer,R.drawable.dzz_lby_pic_zhezhao_android);
                    helper.setImageByUrl(R.id.item_img_user, item.getFaceUrl());
                    helper.setText(R.id.item_tv_name, item.getUserName());
                    helper.setText(R.id.tv_num_zan, item.getViewCount() + "");
                    helper.setText(R.id.item_tv_address, item.getLocation());
                    helper.setText(R.id.tv_title, item.getVideoName());
                    helper.setText(R.id.tv_time, TimeTool.getTime(item.getShowTime()));
                    final long nowTime = System.currentTimeMillis();
                    if (item.getVideoType() == 1) {
                        helper.setImageResource(R.id.iv_flag, R.drawable.icon_looking);
                        helper.setVisibility(R.id.imageView5, View.GONE);
                    } else {
                        helper.setVisibility(R.id.imageView5, View.VISIBLE);
                        if (nowTime < item.getShowTime()) {
                            helper.setImageResource(R.id.iv_flag, R.drawable.icon_like);
                            helper.setImageResource(R.id.imageView5, R.drawable.icon_booking);
                        } else {
                            helper.setImageResource(R.id.imageView5, R.drawable.icon_onair);
                            helper.setImageResource(R.id.iv_flag, R.drawable.icon_looking);
                        }
                    }
                    helper.setOnClickListener(R.id.item_img_user, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!TextUtils.isEmpty(item.getUserId())) {
                                Utils.OpenUserInfo(mContext, item.getUserId(), "1");
                            }
                        }
                    });
                    if (item.getRank() != null) {
                        helper.setVisibility(R.id.tv_lable, View.VISIBLE);
                        helper.setText(R.id.tv_lable, item.getRank().getRank());
                    } else {
                        helper.setVisibility(R.id.tv_lable, View.GONE);
                        helper.setText(R.id.tv_lable, "");
                    }
                    helper.getConvertView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final long clickTime = System.currentTimeMillis();
                            if (item.getVideoType() == 4) {
                                if (clickTime < item.getShowTime() && !TextUtils.isEmpty(item.getUrl())) {
                                    String url = item.getUrl();
                                    String str;
                                    if (url.contains("?"))
                                        str = "&singerId=";
                                    else
                                        str = "?singerId=";
                                    url = url + str + item.getUserId();

                                    Utils.startH5(getActivity(), item.getAlbumName(), item.getAlbumId() + "", url, item.getFaceUrl(), 6);
                                } else {
                                    Utils.playShow(getActivity(), item.getVideoId() + "", item.getUserId());
                                }
                            } else {
                                Utils.playLookForwordDemoVideo(getActivity(), item.getVideoId() + "", item.getUserId());
                            }
                        }
                    });
                }
            };
            mList.setAdapter(commonAdapter);
        } else
            commonAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        super.onSuccess(service, action, value);
        switch ((UserAction) action) {
            case ACTION_GET_USER_VIDEO:
                ResultList<WorkBean> tempBean = JSON.parseObject(value.toString(), new TypeReference<ResultList<WorkBean>>() {
                }.getType());
                List<WorkBean> data = tempBean.data;
                if (workBeanList == null)
                    workBeanList = new ArrayList<WorkBean>();
                LogUtils.d("onSuccess---mPage=="+mPage);
                if (data != null && data.size() > 0) {
                    if (mPage == 1)
                        workBeanList.clear();
                    workBeanList.addAll(data);
                    updateView();
                    mPage++;
                } else {
                    if (mPage == 1) {
                        workBeanList.clear();
                        updateView();
                    }
                    if (mActivity.getIndex() == index) {
                        if (mPage == 1) {
                            Utils.toast(mActivity, "没有符合要求的视频");
                        } else
                            Utils.toast(mActivity, "已经加载全部");
                    }
                }
                break;
        }
    }

    @Override
    public void onFinish(ServiceAction service, Object action) {
        super.onFinish(service, action);
        mPtrList.onRefreshComplete();
        dismissDialog();
    }

    public void setPage(int i) {
        mPage = i;
    }
}

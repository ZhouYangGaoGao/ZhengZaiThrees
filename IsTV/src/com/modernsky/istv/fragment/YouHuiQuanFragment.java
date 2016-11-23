/**
 *
 */
package com.modernsky.istv.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.http.RequestParams;
import com.modernsky.istv.acitivity.OrderActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.action.PayAction;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.adapter.YouHuiQuanListAdapter;
import com.modernsky.istv.bean.ResultList;
import com.modernsky.istv.bean.YouHuiQuan;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.Constants.UserParams;
import com.modernsky.istv.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author： fengqingyun2008
 * @Email： fengqingyun2008@gmail.com
 * @version：1.0
 * @创建时间：2015-4-26 下午12:18:44
 * @类说明：
 */
public class YouHuiQuanFragment extends BaseFragment {
    // -------------优惠券
    private List<YouHuiQuan> youHuiQuans;
    private YouHuiQuanListAdapter huiQuanListAdapter;
    private ListView listView;
    private String userId;
    private OrderActivity activity;
    private PullToRefreshListView mPtrList;

//    public YouHuiQuanFragment(String userId) {
//        this.userId = userId;
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                activity.onBackPressed();
                break;

            default:
                break;
        }
    }

    @SuppressLint("InflateParams")
    @Override
    public View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (OrderActivity) getActivity();
        this.userId=getArguments().getString("userId");
        return inflater.inflate(R.layout.fragment_youhuiquan, container, false);
    }

    @Override
    public void initView(View rootView) {
        rootView.findViewById(R.id.img_back).setOnClickListener(this);
        youHuiQuans = new ArrayList<YouHuiQuan>();
        mPtrList = (PullToRefreshListView) rootView.findViewById(R.id.listview_order);
        listView = mPtrList.getRefreshableView();
        huiQuanListAdapter = new YouHuiQuanListAdapter(youHuiQuans,
                getActivity());
        listView.setAdapter(huiQuanListAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                YouHuiQuan youHuiQuan = youHuiQuans.get(position);
                if (youHuiQuan.getIsUse() == 1) {
                    com.modernsky.istv.utils.Utils.toast(activity,
                            "您已经使用过此优惠券了。");
                } else {
                    activity.setYouHuiMa(youHuiQuan.getCode(), position);
                    activity.onBackPressed();
                }
            }
        });
        if (TextUtils.isEmpty(userId))
            userId = activity.getUserId();
        LogUtils.t("userId=", userId);
        getYouHuiQuan(String.valueOf(userId));
    }

    private void getYouHuiQuan(String userId) {
        RequestParams params = UrlTool.getPostParams(Constants.USER_ID, userId);

        SendActtionTool.post(UserParams.URL_YOUHUIQUAN,
                ServiceAction.Action_Pay, PayAction.Action_getYouHuiQuan, this,
                params);
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        super.onSuccess(service, action, value);
        String string = value.toString();
        switch ((PayAction) action) {
            case Action_getYouHuiQuan:
                ResultList<YouHuiQuan> resultList = JSON.parseObject(string,
                        new TypeReference<ResultList<YouHuiQuan>>() {
                        });
                List<YouHuiQuan> tempYouHuiQuans = resultList.data;
                if (tempYouHuiQuans != null && tempYouHuiQuans.size() > 0) {
                    youHuiQuans.clear();
                    youHuiQuans.addAll(tempYouHuiQuans);
                    huiQuanListAdapter.notifyDataSetChanged();
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {
        // TODO Auto-generated method stub
        super.onFaile(service, action, value);
    }

    @Override
    public void onException(ServiceAction service, Object action, Object value) {
        // TODO Auto-generated method stub
        super.onException(service, action, value);
    }

}

/**
 *
 */
package com.modernsky.istv.acitivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.action.UserAction;
import com.modernsky.istv.adapter.CommonAdapter;
import com.modernsky.istv.adapter.ViewHolder;
import com.modernsky.istv.bean.OrderBean;
import com.modernsky.istv.bean.ResultList;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.TimeTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.Constants.UserParams;
import com.modernsky.istv.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author： fengqingyun2008
 * @Email： fengqingyun2008@gmail.com
 * @version：1.0
 * @创建时间：2015-6-8 下午12:14:20
 * @类说明：
 */
public class OrderMeActivity extends BaseActivity {
    @ViewInject(R.id.ptr_layout_list)
    private PullToRefreshListView pullToRefreshListView;
    private ListView mListView;
    private CommonAdapter<OrderBean> commonAdapter;
    private List<OrderBean> datas;
    private String userId;
    @ViewInject(R.id.tv_title)
    TextView tv_title;
    @ViewInject(R.id.tv_empty)
    TextView tv_empty;
    private int mPage = 1;

    @Override
    @OnClick(R.id.img_back)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                onBackPressed();
                break;

            default:
                break;
        }
    }

    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_zhanneixin);
        ViewUtils.inject(this);
        userId = UserService.getInatance().getUserBean(this).getId();
    }

    @Override
    public void findViewById() {
        tv_title.setText("消费记录");
        mListView = pullToRefreshListView.getRefreshableView();
        mListView.setEmptyView(tv_empty);
        initListView();
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPage = 1;
                getData(userId, mPage);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getData(userId, mPage);
            }
        });
        pullToRefreshListView.setRefreshing();
    }

    private void initListView() {


        datas = new ArrayList<OrderBean>();
        commonAdapter = new CommonAdapter<OrderBean>(this, datas,
                R.layout.item_purchas_history) {

            @Override
            public void convert(ViewHolder helper, OrderBean item) {

                helper.setText(R.id.item_lefttext, TimeTool.getFormaTime2(item.getSuccessTime()) + " " + item.getBody());
                helper.setText(R.id.item_righttext, item.getTotalFee() + "元");
            }
        };
        mListView.setAdapter(commonAdapter);
    }

    /**
     * 获取订单列表
     *
     * @param userId
     */
    private void getData(String userId, int page) {
        RequestParams params = UrlTool.getPostParams(Constants.USER_ID, userId, UserParams.TERMINAL, UserParams.MOBILE, Constants.PAGE, "" + mPage);

        SendActtionTool.post(UserParams.URL_ORDERLIST,
                ServiceAction.Action_User, UserAction.Action_GET_ORDERLIST,
                this, params);
    }

    @Override
    public void onFinish(ServiceAction service, Object action) {
        super.onFinish(service, action);
        pullToRefreshListView.onRefreshComplete();
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        super.onSuccess(service, action, value);
        String string = value.toString();
        switch ((UserAction) action) {
            case Action_GET_ORDERLIST:
                ResultList<OrderBean> resultList = JSON.parseObject(string,
                        new TypeReference<ResultList<OrderBean>>() {
                        });
                List<OrderBean> tempList = resultList.data;
                if (tempList != null && tempList.size() > 0) {
                    if (mPage == 1)
                        datas.clear();
                    datas.addAll(tempList);
                    commonAdapter.notifyDataSetChanged();
                } else {
                    Utils.toast(this, "已经加载全部");
                }
                mPage++;
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

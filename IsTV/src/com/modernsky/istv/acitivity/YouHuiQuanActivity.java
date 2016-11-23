/**
 *
 */
package com.modernsky.istv.acitivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.action.PayAction;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.adapter.CommonAdapter;
import com.modernsky.istv.adapter.ViewHolder;
import com.modernsky.istv.bean.ResultList;
import com.modernsky.istv.bean.YouHuiQuan;
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
 * @创建时间：2015-6-9 下午3:22:19
 * @类说明：
 */
public class YouHuiQuanActivity extends BaseActivity {
    private List<YouHuiQuan> youHuiQuans;
    private CommonAdapter<YouHuiQuan> huiQuanListAdapter;
    private ListView listView;
    private String userId;
    private PullToRefreshListView mPtrList;
    private int page = 1;
    private final String PAGE = "page";
    private boolean shouldGetMoreData = true;

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
        setContentView(R.layout.fragment_youhuiquan);
        ViewUtils.inject(this);
        if (UserService.getInatance().getUserBean(this) != null)
            userId = UserService.getInatance().getUserBean(this).getId();
    }

    @Override
    public void findViewById() {
        youHuiQuans = new ArrayList<YouHuiQuan>();
        mPtrList = (PullToRefreshListView) findViewById(R.id.listview_order);
        initPtrListListenner();
        listView = mPtrList.getRefreshableView();
//		listView = (ListView) findViewById(R.id.listview_order);
        huiQuanListAdapter = new CommonAdapter<YouHuiQuan>(this, youHuiQuans,
                R.layout.item_order) {

            @Override
            public void convert(ViewHolder helper, YouHuiQuan item) {
                helper.setText(R.id.textView1,
                        "类 型：" + item.getTitle());
                helper.setText(R.id.textView2, "券 号：" + item.getCode());
                helper.setText(R.id.textView3,
                        "有效期：" + TimeTool.getDayTime(item.getEndTime()));
                helper.setText(R.id.textView4, item.getMoney() + "元");
            }
        };
        listView.setAdapter(huiQuanListAdapter);
        getYouHuiQuan(userId);
    }

    private void initPtrListListenner() {

        mPtrList.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mPtrList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//             mPtrList.onRefreshComplete();
                mPtrList.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPtrList.onRefreshComplete();
                    }
                }, 500);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (shouldGetMoreData) {
                    page++;
                    getYouHuiQuan(userId);
                } else {
                    mPtrList.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Utils.toast(YouHuiQuanActivity.this, "全部加载完毕");
                            mPtrList.onRefreshComplete();
                        }
                    }, 500);
                }

            }
        });
    }


    private void getYouHuiQuan(String userId) {
        RequestParams params = UrlTool.getPostParams(Constants.USER_ID, userId, PAGE, page + "");

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
//				youHuiQuans.clear();
                    youHuiQuans.addAll(tempYouHuiQuans);
                    huiQuanListAdapter.notifyDataSetChanged();
                    if (page > 1)
                        listView.setSelection(youHuiQuans.size() - 1);
                } else {
                    shouldGetMoreData = false;
                    Utils.toast(this, "全部加载完毕");
                }
                mPtrList.onRefreshComplete();

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

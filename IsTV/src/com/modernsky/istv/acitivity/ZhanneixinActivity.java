package com.modernsky.istv.acitivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.action.UserAction;
import com.modernsky.istv.adapter.NoticeAdapter;
import com.modernsky.istv.bean.NoticeBean;
import com.modernsky.istv.bean.ResultList;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zqg on 2016/2/23.
 */
public class ZhanneixinActivity extends BaseActivity {
    PullToRefreshListView mPtrList;
    ListView mList;
    @ViewInject(R.id.img_back)
    ImageButton mBacImg;
    @ViewInject(R.id.tv_title)
    TextView mTitle;
    private int mPage = 1;
    private String userId;
    private List<NoticeBean> mDate;

    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_zhanneixin);
        ViewUtils.inject(this);
    }

    @Override
    public void findViewById() {
        mTitle.setText("消息中心");
        mBacImg.setOnClickListener(this);
        mPtrList = (PullToRefreshListView) findViewById(R.id.ptr_layout_list);
        mPtrList.setMode(PullToRefreshBase.Mode.BOTH);
        mList = mPtrList.getRefreshableView();
        userId = UserService.getInatance().getUserBean(this).getId();
        View emptyView = findViewById(R.id.tv_zanwu);
        mList.setEmptyView(emptyView);
        mDate = new ArrayList<NoticeBean>();
        updateShowcang();
        initPtrListListenner();
        mPtrList.setRefreshing();
    }

    private void initPtrListListenner() {

        mPtrList.setMode(PullToRefreshBase.Mode.BOTH);
        mPtrList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPage = 1;
                getContent();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getContent();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.img_down:
                NoticeBean tempBean = (NoticeBean) v.getTag();
                if (tempBean.getIsRead() == 0) {
                    SendActtionTool.post(Constants.UserParams.URL_NOTICE_READ, null, UserAction.ACTION_NOTICE_READ, this, UrlTool.getPostParams(Constants.USER_ID, userId, Constants.ID, tempBean.getId()));
                }

                break;
            case R.id.item_delete_shoucangBtn:
                NoticeBean bean = (NoticeBean) v.getTag();
                UserAction.ACTION_NOTICE_DEL.value = bean;
                SendActtionTool.post(Constants.UserParams.URL_NOTICE_DEL, null, UserAction.ACTION_NOTICE_DEL, this, UrlTool.getPostParams(Constants.USER_ID, userId, Constants.ID, bean.getId(), Constants.TYPE, "1"));
                showLoadingDialog();

                break;
        }
    }


    private void getContent() {
        SendActtionTool.post(Constants.UserParams.URL_NOTICE_LIST, null, UserAction.ACTION_NOTICE_LIST, this, UrlTool.getPostParams(Constants.USER_ID, userId, Constants.PAGE, "" + mPage));
    }

    /**
     * 更新内容
     */
    private void updateShowcang() {
        if (mList == null) {
            mList = mPtrList.getRefreshableView();
        }
        NoticeAdapter mAdapter = new NoticeAdapter(mDate,
                getLayoutInflater(), getWindowManager().getDefaultDisplay()
                .getWidth());
        mAdapter.setOnclickListener(this);
        mList.setAdapter(mAdapter);

    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        super.onSuccess(service, action, value);
        switch ((UserAction) action) {
            case ACTION_NOTICE_DEL:
                dismissDialog();
                NoticeBean temp = (NoticeBean) ((UserAction) action).value;
                mDate.remove(temp);
                updateShowcang();
                break;

            case ACTION_NOTICE_READ:
                LogUtils.d(value.toString());
                break;
            case ACTION_NOTICE_LIST:

                try {
                    ResultList<NoticeBean> resultList = JSON.parseObject(value.toString(),
                            new TypeReference<ResultList<NoticeBean>>() {
                            });
                    List<NoticeBean> tempList = resultList.data;
                    if (tempList != null && tempList.size() > 0) {
                        if (mPage == 1)
                            mDate.clear();
                        mDate.addAll(tempList);
                        updateShowcang();
                        if (mPage > 1)
                            mList.setSelection(mDate.size() - 1);
                    } else {
                        Utils.toast(this, "全部加载完毕");
                    }
                    mPtrList.onRefreshComplete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mPage++;
                break;
        }
    }


}

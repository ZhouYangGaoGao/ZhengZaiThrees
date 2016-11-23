/**
 *
 */
package com.modernsky.istv.acitivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.action.UserAction;
import com.modernsky.istv.adapter.BofangLeaveAdapter;
import com.modernsky.istv.bean.BofangBean;
import com.modernsky.istv.bean.ResultList;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.Constants.UserParams;
import com.modernsky.istv.utils.PreferencesUtils;
import com.modernsky.istv.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author： fengqingyun2008
 * @Email： fengqingyun2008@gmail.com
 * @version：1.0
 * @创建时间：2015-6-9 下午3:22:19
 * @类说明：播放纪录
 */
public class RecordActivity extends BaseActivity {
    private List<BofangBean> dataBofang;
    private ListView listView;
    private String userId;
    @ViewInject(R.id.tv_video_name)
    private TextView tv_video_name;
    private BofangBean bean;
    private PullToRefreshListView mPtrList;
    private int page = 1;
    private final String PAGE = "page";
    private boolean shouldGetMoreData = true;

    @OnClick(R.id.img_back)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                onBackPressed();
                break;
            case R.id.item_delete:
                bean = (BofangBean) v.getTag();
                // UserAction.Action_See_Leave_DEL.value = bean;
                SendActtionTool.get(UserParams.URL_PLAY_RECORD_DEL,
                        ServiceAction.Action_User, UserAction.Action_See_Leave_DEL,
                        this, UrlTool.getParams(Constants.USER_ID, userId,
                                Constants.VIDEO_ID,
                                String.valueOf(bean.getVideoId())));
                showLoadingDialog();
                break;
            case R.id.party_left_content:
                BofangBean bean = (BofangBean) v.getTag();
                if (TextUtils.isEmpty(bean.getSingerId())) {
                    // 打开 播放器
                    Utils.playVideo(RecordActivity.this,
                            String.valueOf(bean.getVideoId()), bean.getVideoName());
                } else {
                    Utils.playLookForwordDemoVideo(RecordActivity.this,String.valueOf(bean.getVideoId()),bean.getSingerId());
                }

                break;

            default:
                break;
        }
    }

    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_youhuiquan);
        ViewUtils.inject(this);
        userId = UserService.getInatance().getUserBean(this).getId();
        tv_video_name.setText("观看记录");
    }

    @Override
    public void findViewById() {
        dataBofang = new ArrayList<BofangBean>();
        mPtrList = (PullToRefreshListView) findViewById(R.id.listview_order);
        initPtrListListenner();
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
                            Utils.toast(RecordActivity.this, "全部加载完毕");
                            mPtrList.onRefreshComplete();
                        }
                    }, 500);
                }

            }
        });
    }

    private void getYouHuiQuan(String userId) {
        // 得到播放记录视屏
        SendActtionTool.get(UserParams.URL_PLAY_RECORD,
                ServiceAction.Action_User, UserAction.Action_See_Leave, this,
                UrlTool.getParams(Constants.USER_ID, userId, PAGE, page + ""));
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        super.onSuccess(service, action, value);
        savelocalFile(value);
        String string = value.toString();
        switch ((UserAction) action) {
            case Action_See_Leave:
                ResultList<BofangBean> resultList = JSON.parseObject(string,
                        new TypeReference<ResultList<BofangBean>>() {
                        });
                List<BofangBean> tempYouHuiQuans = resultList.data;
                if (tempYouHuiQuans != null && tempYouHuiQuans.size() > 0) {
//				dataBofang.clear();
                    dataBofang.addAll(tempYouHuiQuans);
                    updateboFangleave();
                    if (page > 1)
                        listView.setSelection(dataBofang.size() - 1);
                } else {
                    shouldGetMoreData = false;
                    Utils.toast(this, "全部加载完毕");
                }
                mPtrList.onRefreshComplete();
                break;
            // 删除观看记录
            case Action_See_Leave_DEL:
                Utils.toast(getApplicationContext(), R.string._del_success);
                if (bean != null) {
                    if (dataBofang.contains(bean)) {
                        dataBofang.remove(bean);
                        updateboFangleave();
                    }
                }
                break;

            default:
                break;
        }
    }

    /**
     * 更新播放记录
     */
    private void updateboFangleave() {
        if (listView == null) {
//			listView = (ListView) findViewById(R.id.listview_order);
            listView = mPtrList.getRefreshableView();
        }
        BofangLeaveAdapter adapter = new BofangLeaveAdapter(dataBofang,
                getLayoutInflater(), getWindowManager().getDefaultDisplay()
                .getWidth());
        adapter.setOnclickListener(this);
        listView.setAdapter(adapter);
    }

    @Override
    public void onFinish(ServiceAction service, Object action) {
        super.onFinish(service, action);
        switch ((UserAction) action) {
            case Action_See_Leave_DEL:
                dismissDialog();
                break;
        }
    }

    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {
        // TODO Auto-generated method stub
        super.onFaile(service, action, value);
        JSONObject js;
        try {
            js = new JSONObject(PreferencesUtils.getPreferences(
                    RecordActivity.this,
                    PreferencesUtils.TYPE_SHOUCANG_ACTIVITY));
            String str = js.toString();
            switch ((UserAction) action) {
                case Action_See_Leave:
                    ResultList<BofangBean> resultList = JSON.parseObject(str,
                            new TypeReference<ResultList<BofangBean>>() {
                            });
                    List<BofangBean> tempYouHuiQuans = resultList.data;
                    if (tempYouHuiQuans != null && tempYouHuiQuans.size() > 0) {
                        dataBofang.clear();
                        dataBofang.addAll(tempYouHuiQuans);
                        updateboFangleave();
                    }
                    break;
                // 删除观看记录
                case Action_See_Leave_DEL:
                    Utils.toast(getApplicationContext(), R.string._del_success);
                    if (bean != null) {
                        if (dataBofang.contains(bean)) {
                            dataBofang.remove(bean);
                            updateboFangleave();
                        }
                    }
                    break;

                default:
                    break;
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void onException(ServiceAction service, Object action, Object value) {
        // TODO Auto-generated method stub
        super.onException(service, action, value);
        JSONObject js;
        try {
            js = new JSONObject(PreferencesUtils.getPreferences(
                    RecordActivity.this,
                    PreferencesUtils.TYPE_SHOUCANG_ACTIVITY));
            String str = js.toString();
            switch ((UserAction) action) {
                case Action_See_Leave:
                    ResultList<BofangBean> resultList = JSON.parseObject(str,
                            new TypeReference<ResultList<BofangBean>>() {
                            });
                    List<BofangBean> tempYouHuiQuans = resultList.data;
                    if (tempYouHuiQuans != null && tempYouHuiQuans.size() > 0) {
                        dataBofang.clear();
                        dataBofang.addAll(tempYouHuiQuans);
                        updateboFangleave();
                    }
                    break;
                // 删除观看记录
                case Action_See_Leave_DEL:
                    Utils.toast(getApplicationContext(), R.string._del_success);
                    if (bean != null) {
                        if (dataBofang.contains(bean)) {
                            dataBofang.remove(bean);
                            updateboFangleave();
                        }
                    }
                    break;

                default:
                    break;
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 首页整个页面数据缓存到sp中
     *
     * @param
     */
    public void savelocalFile(Object object) {
        PreferencesUtils.savePreferences(RecordActivity.this,
                PreferencesUtils.TYPE_SHOUCANG_ACTIVITY, object.toString());

    }
}

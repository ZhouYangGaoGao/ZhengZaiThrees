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
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.action.UserAction;
import com.modernsky.istv.adapter.CollectAdapter;
import com.modernsky.istv.bean.BofangBean;
import com.modernsky.istv.bean.ResultList;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.Constants.UserParams;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.PreferencesUtils;
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
public class ShouCangActivity extends BaseActivity {
    private List<BofangBean> dataShouCangBeans;
    private ListView listView;
    private String userId;
    @ViewInject(R.id.tv_video_name)
    private TextView tv_video_name;
    private BofangBean shoucang;
    private PullToRefreshListView mPtrList;
    private int page = 1;
    private boolean shouldGetMoreData = true;

    @OnClick(R.id.img_back)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                onBackPressed();
                break;
            case R.id.item_delete:
                shoucang = (BofangBean) v.getTag();
                // UserAction.Action_Shoucang_Del.value = shoucang;
//                SendActtionTool.post(UserParams.URL_Dell_Collect_VIDEO,
//                        ServiceAction.Action_User, UserAction.Action_Shoucang_Del,
//                        this, UrlTool.getParams(Constants.USER_ID, userId,
//                                Constants.VIDEO_ID,
//                                String.valueOf(shoucang.getVideoId())));
                SendActtionTool.post(UserParams.URL_Dell_Collect_VIDEO,
                        ServiceAction.Action_User, UserAction.Action_Shoucang_Del,
                        this, UrlTool.getPostParams(Constants.USER_ID, userId,
                                Constants.RESOURCE_ID,
                                String.valueOf(shoucang.getVideoId()), Constants.TYPE, "1"));
                LogUtils.d("userID==" + userId + "VIDEO_ID==" + shoucang.getVideoId());
                showLoadingDialog();
                break;
            case R.id.party_left_content:
                BofangBean bean = (BofangBean) v.getTag();
                // 打开 播放器
                Utils.playVideo(ShouCangActivity.this,
                        String.valueOf(bean.getVideoId()), bean.getVideoName()
                );
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
        tv_video_name.setText("收藏");
    }

    @Override
    public void findViewById() {
        dataShouCangBeans = new ArrayList<BofangBean>();
        mPtrList = (PullToRefreshListView) findViewById(R.id.listview_order);
        initPtrListListenner();
        getShouCangQuan(userId);
    }

    private void initPtrListListenner() {

        mPtrList.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mPtrList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (shouldGetMoreData) {
                    page++;
                    getShouCangQuan(userId);
                } else {
                    mPtrList.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Utils.toast(ShouCangActivity.this, "全部加载完毕");
                            mPtrList.onRefreshComplete();
                        }
                    }, 500);
                }

            }
        });
    }

    private void getShouCangQuan(String userId) {
        // 得到我的收藏视屏
        SendActtionTool.get(UserParams.URL_COLLECT_LIST,
                ServiceAction.Action_User, UserAction.Action_Shoucang, this,
                UrlTool.getParams(Constants.USER_ID, userId,
                        UserParams.TERMINAL, Constants.MOBILE, Constants.TYPE, "1", Constants.PAGE, page + ""));
    }

    /**
     * 更新收藏
     */
    private void updateShowcang() {
        if (listView == null) {
//            listView = (ListView) findViewById(R.id.listview_order);
            listView = mPtrList.getRefreshableView();
        }
        CollectAdapter adapter = new CollectAdapter(dataShouCangBeans,
                getLayoutInflater(), getWindowManager().getDefaultDisplay()
                .getWidth());
        adapter.setOnclickListener(this);
        listView.setAdapter(adapter);
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        super.onSuccess(service, action, value);
        savelocalFile(value);
        String string = value.toString();
        LogUtils.t(action.toString(), string);
        switch ((UserAction) action) {
            case Action_Shoucang:
                ResultList<BofangBean> resultList = JSON.parseObject(string,
                        new TypeReference<ResultList<BofangBean>>() {
                        });
                List<BofangBean> tempYouHuiQuans = resultList.data;
                if (tempYouHuiQuans != null && tempYouHuiQuans.size() > 0) {
//                    dataShouCangBeans.clear();
                    dataShouCangBeans.addAll(tempYouHuiQuans);
                    updateShowcang();
                    if (page > 1)
                        listView.setSelection(dataShouCangBeans.size() - 1);
                } else {
                    shouldGetMoreData = false;
                    Utils.toast(this, "全部加载完毕");
                }
                mPtrList.onRefreshComplete();
                break;
            // 删除收藏
            case Action_Shoucang_Del:
                Utils.toast(getApplicationContext(), R.string._del_success);
                if (shoucang != null) {
                    if (dataShouCangBeans.contains(shoucang)) {
                        dataShouCangBeans.remove(shoucang);
                        updateShowcang();
                        LogUtils.t("updateShowcang", "--updateShowcang");
                    }
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
        Utils.toast(this, value.toString());
//        JSONObject js;
//        try {
//            js = new JSONObject(PreferencesUtils.getPreferences(
//                    ShouCangActivity.this,
//                    PreferencesUtils.TYPE_SHOUCANG_ACTIVITY));
//            String st = js.toString();
//            switch ((UserAction) action) {
//                case Action_Shoucang:
//
//                    ResultList<BofangBean> resultList = JSON.parseObject(st,
//                            new TypeReference<ResultList<BofangBean>>() {
//                            });
//                    List<BofangBean> tempYouHuiQuans = resultList.data;
//                    if (tempYouHuiQuans != null && tempYouHuiQuans.size() > 0) {
//                        dataShouCangBeans.clear();
//                        dataShouCangBeans.addAll(tempYouHuiQuans);
//                        updateShowcang();
//                    }
//
//                    break;
//                // 删除收藏
//                case Action_Shoucang_Del:
//                    Utils.toast(getApplicationContext(), R.string._del_success);
//                    if (shoucang != null) {
//                        if (dataShouCangBeans.contains(shoucang)) {
//                            dataShouCangBeans.remove(shoucang);
//                            updateShowcang();
//                            LogUtils.t("updateShowcang", "--updateShowcang");
//                        }
//                    }
//                    break;
//                default:
//                    break;
//            }
//
//        } catch (JSONException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }

    }

    @Override
    public void onException(ServiceAction service, Object action, Object value) {
        // TODO Auto-generated method stub
        super.onException(service, action, value);
        Utils.toast(this, value.toString());
//        JSONObject js;
//        try {
//            js = new JSONObject(PreferencesUtils.getPreferences(
//                    ShouCangActivity.this,
//                    PreferencesUtils.TYPE_SHOUCANG_ACTIVITY));
//            String st = js.toString();
//            switch ((UserAction) action) {
//                case Action_Shoucang:
//
//                    ResultList<BofangBean> resultList = JSON.parseObject(st,
//                            new TypeReference<ResultList<BofangBean>>() {
//                            });
//                    List<BofangBean> tempYouHuiQuans = resultList.data;
//                    if (tempYouHuiQuans != null && tempYouHuiQuans.size() > 0) {
//                        dataShouCangBeans.clear();
//                        dataShouCangBeans.addAll(tempYouHuiQuans);
//                        updateShowcang();
//
//                    }
//
//                    break;
//                // 删除收藏
//                case Action_Shoucang_Del:
//                    Utils.toast(getApplicationContext(), R.string._del_success);
//                    if (shoucang != null) {
//                        if (dataShouCangBeans.contains(shoucang)) {
//                            dataShouCangBeans.remove(shoucang);
//                            updateShowcang();
//                            LogUtils.t("updateShowcang", "--updateShowcang");
//                        }
//                    }
//                    break;
//                default:
//                    break;
//            }
//
//        } catch (JSONException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }
    }

    @Override
    public void onFinish(ServiceAction service, Object action) {
        super.onFinish(service, action);
        switch ((UserAction) action) {
            case Action_Shoucang_Del:
                dismissDialog();
                break;
        }
    }

    /**
     * 首页整个页面数据缓存到sp中
     *
     * @param
     */
    public void savelocalFile(Object object) {
        PreferencesUtils.savePreferences(ShouCangActivity.this,
                PreferencesUtils.TYPE_SHOUCANG_ACTIVITY, object.toString());

    }
}

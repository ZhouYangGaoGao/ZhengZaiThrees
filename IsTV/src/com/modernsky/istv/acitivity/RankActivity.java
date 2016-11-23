/**
 *
 */
package com.modernsky.istv.acitivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.http.RequestParams;
import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.action.RankAction;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.adapter.CommonAdapter;
import com.modernsky.istv.adapter.ViewHolder;
import com.modernsky.istv.bean.RankBean;
import com.modernsky.istv.service.RankPageService;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.LogUtils;
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
 * @创建时间：2015-6-8 下午12:14:20
 * @类说明：
 */
public class RankActivity extends BaseActivity {
    private PullToRefreshListView pullToRefreshListView;
    private ListView listView;
    private CommonAdapter<RankBean> commonAdapter;
    private List<RankBean> datas;
    private int lastPosition;

    //	private String lastMB=0+"";
    @Override
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
        setContentView(R.layout.a_ranking);
    }

    private void getUrlData(String lastMB) {
        RequestParams params = UrlTool.getParams("showId", "1", "lastMB", lastMB);
        SendActtionTool.get(Constants.URL_RANK, null, RankAction.Action_today,
                this, params);
    }

    @Override
    public void findViewById() {
        findViewById(R.id.img_back).setOnClickListener(this);
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.listview);
        pullToRefreshListView
                .setOnRefreshListener(new OnRefreshListener<ListView>() {

                    @Override
                    public void onRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        getUrlData("0");
                    }
                });
        listView = pullToRefreshListView.getRefreshableView();
        listView.setDividerHeight(0);
        // final Animation animation = AnimationUtils.loadAnimation(this,
        // R.anim.slide_bottom_to_top);
        datas = new ArrayList<RankBean>();
        commonAdapter = new CommonAdapter<RankBean>(this, datas,
                R.layout.item_rank) {

            @Override
            public void convert(ViewHolder helper, final RankBean item) {
                helper.setImageByUrl(R.id.imageView1, item.getStandardPic());
                helper.setText(R.id.indexView,
                        String.valueOf(datas.indexOf(item) + 1));
                helper.setText(R.id.tv_video_name, item.getVideoName());
                helper.setText(R.id.tv_songer, item.getStarringName());
                helper.setText(R.id.textView2, "人气：" + item.getViewCount());
                helper.getView(R.id.imageView1).setOnClickListener(
                        new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Utils.playVideo(RankActivity.this,
                                        String.valueOf(item.getVideoId()),
                                        item.getVideoName());
                            }
                        });
                int position = datas.indexOf(item);
                // if (position > lastPosition) {// 这里就是动画的应用
                Animation animation = AnimationUtils.loadAnimation(mContext,
                        (position > lastPosition) ? R.anim.slide_bottom_to_top
                                : R.anim.slide_top_to_bottom);
                helper.getConvertView().startAnimation(animation);
                // }
                lastPosition = position;

            }

        };
        listView.setAdapter(commonAdapter);
        // 检测缓存
        if (!RankPageService.getInstance().isHaveDate()) {
            getUrlData("0");
            // 填充数据
        } else {
            updateTodayData();
        }

    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        LogUtils.t(action.toString(), value.toString());
        switch ((RankAction) action) {
            case Action_today:
                RankPageService.getInstance().setToday(
                        analyData((JSONObject) value));
                updateTodayData();
                savelocalFile(value);
                break;
            default:
                break;
        }
    }

    @Override
    public void onException(ServiceAction service, Object action, Object value) {
        super.onException(service, action, value);
        switch ((RankAction) action) {
            case Action_today:
                try {
                    JSONObject js = new JSONObject(PreferencesUtils.getPreferences(
                            RankActivity.this,
                            PreferencesUtils.TYPE_RANK_ACTIVITY));
                    RankPageService.getInstance().setToday(
                            analyData(js));
                    updateTodayData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

        }
    }

    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {
        super.onFaile(service, action, value);
        switch ((RankAction) action) {
            case Action_today:
                try {
                    JSONObject js = new JSONObject(PreferencesUtils.getPreferences(
                            RankActivity.this,
                            PreferencesUtils.TYPE_RANK_ACTIVITY));
                    RankPageService.getInstance().setToday(
                            analyData(js));
                    updateTodayData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

        }
    }

    @Override
    public void onFinish(ServiceAction service, Object action) {
        super.onFinish(service, action);
        pullToRefreshListView.onRefreshComplete();
    }

    /**
     * 首页整个页面数据缓存到sp中
     */
    public void savelocalFile(Object object) {
        PreferencesUtils.savePreferences(RankActivity.this, PreferencesUtils.TYPE_RANK_ACTIVITY,
                object.toString());

    }

    /**
     *
     */
    private void updateTodayData() {
        List<RankBean> temBeans = RankPageService.getInstance().getToday();
        if (temBeans != null && temBeans.size() > 0) {
            LogUtils.t("temBeans", temBeans.size() + "");
            datas.clear();
            datas.addAll(temBeans);
            commonAdapter.notifyDataSetChanged();
        }
    }

    /**
     * @param obj
     * @return 解析数据
     */
    private List<RankBean> analyData(JSONObject obj) {
        List<RankBean> datas = null;
        try {
            String arr = obj.getString(Constants.DATA);
            datas = JSON.parseArray(arr, RankBean.class);
            return datas;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return datas;
    }


}

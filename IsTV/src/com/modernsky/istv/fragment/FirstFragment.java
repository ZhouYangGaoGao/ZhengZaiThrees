/**
 *
 */
package com.modernsky.istv.fragment;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.http.RequestParams;
import com.modernsky.istv.R;
import com.modernsky.istv.acitivity.MainActivity;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.adapter.CommonAdapter;
import com.modernsky.istv.adapter.ViewHolder;
import com.modernsky.istv.bean.Data;
import com.modernsky.istv.bean.FocusPictureModel;
import com.modernsky.istv.bean.UserEntity;
import com.modernsky.istv.service.FirstPageService;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.TimeTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.PreferencesUtils;
import com.modernsky.istv.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author： fengqingyun2008
 * @Email： fengqingyun2008@gmail.com
 * @version：1.0
 * @创建时间：2015-6-11 下午6:41:14
 * @类说明：
 */
public class FirstFragment extends BaseFragment implements
        OnScrollListener {

//    private Map<Integer, View> mTimeItemMap;
    private SparseArray<View> mTimeItemSArray;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    goInWindow(mTimeListView);
                    break;
                case 1:
                    backWindow(mTimeListView);
                    break;
                case 2:// xianshi时间点
                    showTimePoint();
                    break;
                case 3:// yincang时间点
                    hideTimePoint();
                default:
                    break;
            }
        }
    };

    private ListView listview;
    private CommonAdapter<FocusPictureModel> commonAdapter;
    private List<FocusPictureModel> datas;
    private PullToRefreshListView pullToRefreshListView;
    private int lastPosition;

    private MainActivity mainActivity;
    private String codeVersion;

    private CommonAdapter<String> mTimeAdapter;
    private ListView mTimeListView;
    private List<String> mTimeList;
    private Map<String, Integer> mTimeMap;
//    private SparseArray<Integer> mTimeMap;
    private int index = -1;
    private View leftBacgrundView;
    private boolean timePointIsShow = true;

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

    }

    @Override
    public View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mTimeMap == null) {
            mTimeMap = new HashMap<String,Integer>();
        }
//        if (mTimeItemMap == null) {
//            mTimeItemMap = new HashMap<Integer, View>();
//        }
        if (mTimeItemSArray == null) {
            mTimeItemSArray = new SparseArray<View>();
        }
        mainActivity = (MainActivity) getActivity();
        return inflater.inflate(R.layout.fragment_first, container, false);

    }

    /*
     * 给timeList初始化
     */
    private void initTimeList(View rootView) {
        leftBacgrundView = rootView.findViewById(R.id.bacground);
        leftBacgrundView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return true;
            }
        });

        if (mTimeListView == null) {
            mTimeListView = (ListView) rootView.findViewById(R.id.timeList);
        }
        mTimeList = new ArrayList<String>();
        if (mTimeAdapter == null) {
            mTimeAdapter = new CommonAdapter<String>(getActivity(), mTimeList,
                    R.layout.item_time) {

                @Override
                public void convert(ViewHolder helper, String item) {
                    helper.setText(R.id.text_item_timeline, item);
                }
            };
        } else {
            mTimeAdapter.notifyDataSetChanged();
        }
        mTimeListView.setAdapter(mTimeAdapter);
        mTimeListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    final int arg2, long arg3) {
                if (!mainActivity.isOpen()) {
                    index = mTimeMap.get(mTimeList.get(arg2));
                    leftBacgrundView.setVisibility(View.GONE);
                    handler.removeMessages(0);
                    handler.removeMessages(1);
                    handler.sendEmptyMessage(1);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            updateData();
                            if (!timePointIsShow) {
                                handler.removeMessages(2);
                                handler.removeMessages(3);
                                handler.sendEmptyMessageDelayed(2, 300);
                            }
                            listview.smoothScrollToPositionFromTop(
                                    mTimeMap.get(mTimeList.get(arg2)) + 1, 0, 300);
                        }
                    }, 300);
                }


            }
        });

    }

    private void showTimeList() {
        if (timePointIsShow) {
            handler.removeMessages(2);
            handler.removeMessages(3);
            handler.sendEmptyMessage(3);
        }

        handler.removeMessages(0);
        handler.removeMessages(1);
        if (timeListIsShow) {
        } else {
            handler.sendEmptyMessage(0);
        }
        handler.sendEmptyMessageDelayed(1, 3000);
    }

    @Override
    public void initView(View rootView) {
        pullToRefreshListView = (PullToRefreshListView) rootView
                .findViewById(R.id.listview);
        pullToRefreshListView
                .setOnRefreshListener(new OnRefreshListener<ListView>() {

                    @Override
                    public void onRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        getUrlData();
                    }
                });
        listview = pullToRefreshListView.getRefreshableView();
        initTimeList(rootView);
        datas = new ArrayList<FocusPictureModel>();
        commonAdapter = new CommonAdapter<FocusPictureModel>(getActivity(),
                datas, R.layout.item_center) {

            @Override
            public void convert(ViewHolder helper, final FocusPictureModel item) {

                helper.setImageByUrl(R.id.imageView1, item.getPic());
                ImageView conner = helper.getView(R.id.item_rotate_textview);
                String category = item.getCategory();
                conner.setVisibility(View.VISIBLE);
                int type = Integer.parseInt(category);
                switch (type) {
                    case 0:
                        conner.setVisibility(View.GONE);
                        break;
                    case 1:
                        conner.setBackgroundResource(R.drawable.icon_conner_live);
                        break;
                    case 2:
                        conner.setBackgroundResource(R.drawable.icon_conner_rebo);
                        break;
                    case 3:
                        conner.setBackgroundResource(R.drawable.icon_conner_only);
                        break;
                    case 4:
                        conner.setBackgroundResource(R.drawable.icon_conner_review);
                        break;
                    default:
                        conner.setVisibility(View.GONE);
                        break;
                }

                helper.getView(R.id.imageView1).setOnClickListener(
                        new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                if (mainActivity.isOpen()) {
                                    mainActivity.toggleMenu();
                                    return;
                                }
                                Utils.playMedia(item, getActivity());
                            }
                        });
                int position = helper.getPosition();
                // if (position > lastPosition) {// 这里就是动画的应用
//                Animation animation = AnimationUtils.loadAnimation(mContext,
//                        (position > lastPosition) ? R.anim.slide_bottom_to_top
//                                : R.anim.slide_top_to_bottom);
//                helper.getConvertView().startAnimation(animation);
                // }
                lastPosition = position;
                // 时间的显示

                helper.setText(R.id.text_item_time,
                        TimeTool.getTimeStr2(item.getShowtime()));

                RelativeLayout timeView = helper.getView(R.id.rl_timeLine);
                timeView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if (!mainActivity.isOpen()) {
                            showTimeList();
                        }
                    }
                });

                if (mTimeMap.containsKey(TimeTool.getTimeStr2(item
                        .getShowtime()))
                        && mTimeMap
                        .get(TimeTool.getTimeStr2(item.getShowtime())) == position) {
//                    mTimeItemMap.put(position, timeView);
                    mTimeItemSArray.put(position, timeView);
                    if (timePointIsShow) {
                        timeView.setVisibility(View.VISIBLE);
                    } else {
                        timeView.setVisibility(View.INVISIBLE);
                    }
                } else {
                    timeView.setVisibility(View.INVISIBLE);
                }

            }

        };
        listview.setAdapter(commonAdapter);
        listview.setOnScrollListener(this);
        // 检测缓存
        if (!FirstPageService.getInstance().isHaveDujiaDate()) {
            pullToRefreshListView.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    pullToRefreshListView.setRefreshing();
                    getUrlData();
                }
            }, 300);
            // 填充数据
        } else {
            updateData();
        }

        handler.sendEmptyMessageDelayed(3, 3000);
    }

    /**
     * 获取网络数据
     */
    private void getUrlData() {
        UserEntity bean = UserService.getInatance().getUserBean(getActivity());
        RequestParams params;
        if (bean != null) {
            params = UrlTool.getParams(Constants.TYPE,
                    Constants.TYPE_FIRSTPAGE, Constants.USER_ID, bean.getId());
            LogUtils.t("FirsttPageFragment.getUrlData()_获取首页数据", bean.getId());
        } else {
            params = UrlTool
                    .getParams(Constants.TYPE, Constants.TYPE_FIRSTPAGE);
        }
        SendActtionTool.get(Constants.URL_HOMEPAGE,
                ServiceAction.Action_FirstPage, null, this, params);
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        JSONObject obj = (JSONObject) value;
        switch (service) {
            // 当前页面的数据
            case Action_FirstPage:
                updatePageDate(obj);
                // 缓存当前fragment页面所有数据
                savelocalFile(obj);
                break;
            // 用户请求
            case Action_User:
//                updateYuyue(action, value);
                break;
            default:
                break;
        }
    }

    private void updateTimeList(List<FocusPictureModel> list) {
        mTimeList.clear();
        mTimeMap.clear();
        for (int i = 0; i < list.size(); i++) {
            String time = TimeTool.getTimeStr2(list.get(i).getShowtime());
            if (!mTimeList.contains(time)) {
                mTimeList.add(time);
                mTimeMap.put(time, i);
            }
        }
        mTimeAdapter.notifyDataSetChanged();
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {
        // TODO Auto-generated method stub
        super.onFaile(service, action, value);
        switch (service) {
            case Action_FirstPage:
                try {
                    JSONObject js = new JSONObject(PreferencesUtils.getPreferences(
                            FirstFragment.this.getActivity()
                                    .getApplicationContext(),
                            PreferencesUtils.TYPE_FIRST_FRAGMENT));
                    updatePageDate(js);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;

        }
    }

    @Override
    public void onException(ServiceAction service, Object action, Object value) {
        // TODO Auto-generated method stub
        super.onException(service, action, value);
        switch (service) {
            case Action_FirstPage:
                try {
                    JSONObject js = new JSONObject(PreferencesUtils.getPreferences(
                            FirstFragment.this.getActivity()
                                    .getApplicationContext(),
                            PreferencesUtils.TYPE_FIRST_FRAGMENT));
                    updatePageDate(js);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;

        }
    }

//    /**
//     * @param action
//     * @param value  预约信息解析
//     */
//    private void updateYuyue(Object action, Object value) {
//        // Data data = FirstPageService.getInstance().getYuyueDatas();
//        switch ((UserAction) action) {
//
//            case Action_CHECK_CODE_VERSION:// 敏感词库更新
//                PreferencesUtils.saveCheckString(mainActivity, value.toString());
//                if (!TextUtils.isEmpty(PreferencesUtils
//                        .readCheckString(mainActivity))) {
//                    PreferencesUtils.savePreferences(mainActivity,
//                            PreferencesUtils.TYPE_Check_Verson, codeVersion);
//                }
//
//                break;
//            default:
//                break;
//        }
//    }

    @Override
    public void onFinish(ServiceAction service, Object action) {
        switch (service) {
            // 当前页面的数据
            case Action_FirstPage:
                pullToRefreshListView.onRefreshComplete();
                break;

            default:
                break;
        }
    }

    /**
     * @param object 界面数据 json
     *               <p/>
     *               解析当前界面的 所有数据
     */
    private void updatePageDate(JSONObject object) {
        try {
            JSONArray array = object.getJSONArray(Constants.DATA);
            List<Data> datas = JSON.parseArray(array.toString(), Data.class);

            Data data = null;

            for (int i = 0; i < datas.size(); i++) {
                int key = -1;
                data = datas.get(i);
                new File(object.toString());
                key = data.getSubscribe();
                List<FocusPictureModel> dataList = data.getData();
                switch (key) {
                    // 广告寻展
                    case 0:
                        // FirstPageService.getInstance().setAdvertisementsDatas(data);
                        break;
                    // 独家 热播
                    case 2:
//					Collections.sort(dataList, new DownComparatorUser());
                        updateTimeList(dataList);
                        FirstPageService.getInstance().setDujiaData(data);
                        break;
                    default:
                        break;
                }
            }
            updateData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 首页整个页面数据缓存到sp中
     *
     * @param
     */
    public void savelocalFile(JSONObject object) {
        PreferencesUtils.savePreferences(FirstFragment.this.getActivity()
                        .getApplicationContext(), PreferencesUtils.TYPE_FIRST_FRAGMENT,
                object.toString());

    }

    /**
     *
     */
    private void updateData() {
        // 独家
        initDujia();
    }

    /**
     *
     */
    private void initDujia() {

        Data data = FirstPageService.getInstance().getDujiaData();
        if (data != null) {
            List<FocusPictureModel> tempPictureModels = data.getData();
//			Collections.sort(tempPictureModels, new DownComparatorUser());
            if (tempPictureModels != null && tempPictureModels.size() > 0) {
                datas.clear();
                datas.addAll(tempPictureModels);
                commonAdapter.notifyDataSetChanged();
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case SCROLL_STATE_TOUCH_SCROLL:
                break;
            case SCROLL_STATE_FLING:
                ((MainActivity) getActivity()).showRadioGroup(false);
                break;
            case SCROLL_STATE_IDLE:
                handler.removeMessages(2);
                handler.removeMessages(3);
                if (!timePointIsShow) {
                    // 不是正在显示 就不再让让显示
                    handler.sendEmptyMessage(2);
                } else {
                    // 3秒后
                    handler.sendEmptyMessageDelayed(3, 3000);
                }

                ((MainActivity) getActivity()).showRadioGroup(true);
                break;

            default:
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

    }

    private void showTimePoint() {
        for (int i = 0; i < datas.size(); i++) {
//            if (mTimeItemMap.containsKey(i)) {
//                mTimeItemMap.get(i).setVisibility(View.VISIBLE);
//                goInWindow(mTimeItemMap.get(i));
//            }
            if (mTimeItemSArray.get(i)!=null) {
                mTimeItemSArray.get(i).setVisibility(View.VISIBLE);
                goInWindow(mTimeItemSArray.get(i));
            }
        }
        handler.removeMessages(3);
        handler.sendEmptyMessageDelayed(3, 3000);

    }

    private void hideTimePoint() {
        for (int i = 0; i < datas.size(); i++) {
            if (mTimeItemSArray.get(i)!=null) {
                mTimeItemSArray.get(i).setVisibility(View.VISIBLE);
                backWindow(mTimeItemSArray.get(i));
            }
        }
    }

    private boolean timeListIsShow = false;

    public void backWindow(final View view) {

        LogUtils.d("backButtom");
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(view, "translationX", 0,
                        -view.getWidth()),
                ObjectAnimator.ofFloat(view, "alpha", 1, 0f));
        // 动画周期为500ms

        set.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator animation) {
//                if (mTimeItemMap.containsValue(view)) {
//                    timePointIsShow = false;
//                } else if (mTimeListView == view) {
//                    timeListIsShow = false;
//                    leftBacgrundView.setVisibility(View.GONE);
//                }
                if (mTimeItemSArray.indexOfValue(view)>=0) {
                    timePointIsShow = false;
                } else if (mTimeListView == view) {
                    timeListIsShow = false;
                    leftBacgrundView.setVisibility(View.GONE);
                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {

                if (mTimeItemSArray.indexOfValue(view)>=0) {
                    timePointIsShow = false;
                } else if (mTimeListView == view) {
                    timeListIsShow = false;
                    leftBacgrundView.setVisibility(View.GONE);
                }
            }
        });
        set.setDuration( 500).start();

    }

    public void goInWindow(final View view) {

        LogUtils.d("backButtom");
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(view, "translationX",
                -view.getWidth(), 0), ObjectAnimator.ofFloat(view, "alpha", 0,
                1f));
        // 动画周期为500ms
        view.setVisibility(View.VISIBLE);
        if (mTimeListView == view) {
            leftBacgrundView.setVisibility(View.VISIBLE);
        }
        set.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mTimeItemSArray.indexOfValue(view)>=0) {
                    timePointIsShow = true;
                } else if (mTimeListView == view) {
                    timeListIsShow = true;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (mTimeItemSArray.indexOfValue(view)>=0) {
                    timePointIsShow = true;
                } else if (mTimeListView == view) {
                    timeListIsShow = true;
                }
            }
        });
        set.setDuration( 500).start();
    }

}

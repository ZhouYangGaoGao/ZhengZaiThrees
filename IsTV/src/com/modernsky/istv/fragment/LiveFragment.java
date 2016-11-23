package com.modernsky.istv.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.http.RequestParams;
import com.modernsky.istv.R;
import com.modernsky.istv.acitivity.MainActivity;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.action.YinyuejieAction;
import com.modernsky.istv.adapter.CommonAdapter;
import com.modernsky.istv.adapter.ViewHolder;
import com.modernsky.istv.bean.Data;
import com.modernsky.istv.bean.FocusPictureModel;
import com.modernsky.istv.service.ZhiboPageService;
import com.modernsky.istv.tool.BitmapTool;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.TimeTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.PreferencesUtils;
import com.modernsky.istv.utils.Utils;
import com.modernsky.istv.view.ChildViewPager;
import com.modernsky.istv.view.ChildViewPager.OnSingleTouchListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author rendy 直播界面
 */
public class LiveFragment extends BaseFragment implements OnPageChangeListener,
        OnScrollListener {
    private ViewPager viewPager;
    // 装点点的ImageView数组
    private ImageView[] tips;
    // 装ImageView数组
    private View[] mViews;
    private boolean isScrolled;
    //    private Map<Integer, View> mTimeItemMap;
    private int currentTag;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 4:
                    updateShowText();
                    break;
                default:
                    break;
            }
        }

    };
    private ViewGroup group;
    private Timer timer;
    private ListView mListView;
    private PullToRefreshListView mPullToRefreshListView;
    private CommonAdapter<FocusPictureModel> commonAdapter;
    private MainActivity mainActivity;
    private List<FocusPictureModel> models;
    private View headView;


    private SparseArray<ImageView> imgMap;
    private SparseArray<TextView> mShowDayMap;
    private SparseArray<TextView> mShowHourMap;
    private SparseArray<TextView> mShowMiniteMap;
    private SparseArray<TextView> mShowSecMap;


    @Override
    public View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // mShowTimeTextList = new ArrayList<TextView>();
        imgMap = new SparseArray<ImageView>();
        mShowDayMap = new SparseArray<TextView>();
        mShowHourMap = new SparseArray<TextView>();
        mShowMiniteMap = new SparseArray<TextView>();
        mShowSecMap = new SparseArray<TextView>();
        mainActivity = (MainActivity) getActivity();
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    @Override
    public void initView(View rootView) {
//        if (mTimeItemMap == null) {
//            mTimeItemMap = new HashMap<Integer, View>();
//        }
        initCountTime();
        // handler.sendEmptyMessage(4);
        mPullToRefreshListView = (PullToRefreshListView) rootView
                .findViewById(R.id.listview);
        // 下拉监听函数
        mPullToRefreshListView
                .setOnRefreshListener(new OnRefreshListener<ListView>() {
                    @Override
                    public void onRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        getUrlData();
                        // 执行刷新函数
                    }

                });
        initListView();
        // 检测缓存
        if (!ZhiboPageService.getInstance().isHaveDate()) {
//            mPullToRefreshListView.setRefreshing();
            mPullToRefreshListView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPullToRefreshListView.setRefreshing();
                }
            }, 300);
            // getUrlData();
            // 填充数据
        } else {
            updateData();
        }
    }


    private void updateData() {
        // 初始化 巡展 Øß
        initAvdViewPage(headView);
        Data data = ZhiboPageService.getInstance().getDujiaData();
        if (data != null) {
            List<FocusPictureModel> tempPictureModels = data.getData();
//			Collections.sort(tempPictureModels, new UpComparatorUser());
            if (tempPictureModels != null && tempPictureModels.size() > 0) {
                models.clear();
                models.addAll(tempPictureModels);
                commonAdapter.notifyDataSetChanged();
            }
        }
    }

    public void updateShowText() {

        for (int i = 0; i < models.size(); i++) {
            ImageView img = null;
            if (imgMap.get(i) != null) {
                img = imgMap.get(i);
            }
//            if (imgMap.containsKey(i)) {
//                img = imgMap.get(i);
//            }
            TextView text_day = null, text_hour = null, text_mini = null, text_sec = null;
            if (mShowDayMap.get(i) != null) {
                text_day = mShowDayMap.get(i);
            }

            if (mShowHourMap.get(i) != null) {
                text_hour = mShowHourMap.get(i);
            }

            if (mShowMiniteMap.get(i) != null) {
                text_mini = mShowMiniteMap.get(i);
            }
            if (mShowSecMap.get(i) != null) {
                text_sec = mShowSecMap.get(i);
            }
            if (System.currentTimeMillis() >= models.get(i).getShowtime()) {
                if (img != null) {
                    img.setVisibility(View.VISIBLE);
                    img.setImageResource(R.drawable.liveing_button);
                    if (text_day != null) {
                        ((View) text_day.getParent().getParent()).setVisibility(View.INVISIBLE);
                    }
                }
            } else {
                Map<String, Integer> map = TimeTool.getTimeCount(models.get(i)
                        .getShowtime() - System.currentTimeMillis());

                if (text_day != null && text_hour != null && text_mini != null && text_sec != null) {

                    text_day.setText(add0IflengthIs1("" + map.get("day")));

                    text_hour.setText(add0IflengthIs1("" + map.get("hour")));

                    text_mini.setText(add0IflengthIs1("" + map.get("mins")));
                    text_sec.setText(add0IflengthIs1("" + map.get("secs")));
                }
                // LogUtils.d("map=" + map);
            }
        }
    }

    private String add0IflengthIs1(String str) {
        while (str.length() < 2) {
            str = "0" + str;
        }
        return str;
    }

    @Override
    public void onClick(View v) {

    }

    private void getUrlData() {
        LogUtils.t("getUrlData", "getUrlData");
        RequestParams params = UrlTool.getParams(Constants.TYPE,
                Constants.TYPE_ZhiBo);
        SendActtionTool.get(Constants.URL_HOMEPAGE,
                ServiceAction.Action_YinyueJie, YinyuejieAction.Action_List,
                this, params);
    }

    private boolean timePointIsShow = true;

    /**
     * 初始化 列表信息
     */
    private void initListView() {
        if (mListView == null) {
            mListView = mPullToRefreshListView.getRefreshableView();
            mListView.setOnScrollListener(this);
        }
        if (models == null) {
            models = new ArrayList<FocusPictureModel>();
        }
        if (commonAdapter == null) {
            commonAdapter = new CommonAdapter<FocusPictureModel>(getActivity(),
                    models, R.layout.item_livefragment) {

                private int lastPosition;

                @Override
                public void convert(ViewHolder helper,
                                    final FocusPictureModel item) {
                    int position = models.indexOf(item);
                    // LogUtils.d("position=" + position);

                    LinearLayout layout = helper.getView(R.id.timeLauout);
                    TextView tv_day = helper.getView(R.id.up1);
                    TextView tv_hour = helper.getView(R.id.up2);
                    TextView tv_minite = helper.getView(R.id.up3);
                    TextView tv_sec = helper.getView(R.id.up4);
                    ImageView tv_count = helper.getView(R.id.tv_time_count);


                    if ("2".equals(item.getType())) {
                        ViewGroup.LayoutParams params = helper.getConvertView().getLayoutParams();
//                        int height = params.height;
                        int width = params.width;
                        int picWidth = item.getWidth();
                        int picHeight = item.getHeight();
                        if (picWidth > 0)
                            params.height = picHeight * width / picWidth;
                        helper.getConvertView().setLayoutParams(params);
                        helper.setVisibility(R.id.buttomLayout, View.GONE);
                    } else if ("3".equals(item.getType())) {

                        if (imgMap.size() < 5 || (imgMap.get(position) != null)) {
                            layout.setVisibility(View.VISIBLE);
                            tv_count.setVisibility(View.VISIBLE);
                            imgMap.put(position, tv_count);
                            mShowDayMap.put(position, tv_day);
                            mShowHourMap.put(position, tv_hour);
                            mShowMiniteMap.put(position, tv_minite);
                            mShowSecMap.put(position, tv_sec);

                            if (System.currentTimeMillis() >= item.getShowtime()) {
                                tv_count.setImageResource(R.drawable.liveing_button);
                                layout.setVisibility(View.INVISIBLE);
                            } else {
                                layout.setVisibility(View.VISIBLE);
                                tv_count.setVisibility(View.INVISIBLE);
                                Map<String, Integer> map = TimeTool.getTimeCount(item
                                        .getShowtime() - System.currentTimeMillis());
                                // LogUtils.d("map=" + map);
                                tv_day.setText(String.valueOf(map.get("day")));
                                tv_hour.setText(String.valueOf(map.get("hour")));
                                tv_minite.setText(String.valueOf(map.get("mins")));
                                tv_sec.setText(String.valueOf(map.get("secs")));

                            }
                        }
                    }


                    helper.setText(R.id.text_item_time,
                            TimeTool.getTimeStr2(item.getShowtime()));


                    helper.setText(R.id.tv_video_name, item.getName());
                    helper.setText(R.id.textView2,
                            TimeTool.getFormaTime(item.getShowtime()));
                    helper.setImageByUrl(R.id.imageView1, item.getPic());
                    helper.getConvertView().

                            setOnClickListener(
                                    new OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            if (mainActivity.isOpen()) {
                                                mainActivity.toggleMenu();
                                                return;
                                            }
                                            if (item.getType().equals("-100")) {
                                                return;
                                            }

                                            Utils.playMedia(item, getActivity());
                                        }
                                    }

                            );

                    // 这里就是动画的应用
//                    Animation animation = AnimationUtils
//                            .loadAnimation(
//                                    mContext,
//                                    (position > lastPosition) ? R.anim.slide_bottom_to_top
//                                            : R.anim.slide_top_to_bottom);
//                    helper.getConvertView().
//
//                            startAnimation(animation);
//
//                    lastPosition = position;

                    // 左上角时间的操作
//                    helper.setText(R.id.text_item_time,
//                            TimeTool.getTimeStr2(item.getShowtime()));
//
                    RelativeLayout timeView = helper.getView(R.id.rl_timeline);
                    timeView.setVisibility(View.GONE);

                }
            };
            headView = LayoutInflater.from(getActivity()).inflate(R.layout.head_viewpage, null);
            mListView.addHeaderView(headView);
            mListView.setAdapter(commonAdapter);
        } else

        {
            commonAdapter.notifyDataSetChanged();
        }

    }

    private void initCountTime() {
        // if (System.currentTimeMillis() >= time) {
        // textview.setText("已过期");
        // } else {
        // textview.setText("倒计时:"
        // + TimeTool.getTimeCount(time - System.currentTimeMillis()));

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(4);
            }
        }, 0, 1000);
        // }
    }

    /**
     * @param rootView 初始化 巡展 广告
     */
    private void initAvdViewPage(View rootView) {
        if (group == null) {
            group = (ViewGroup) rootView.findViewById(R.id.viewGroup);
            viewPager = (ChildViewPager) rootView.findViewById(R.id.viewPager);
        }
        Data data = ZhiboPageService.getInstance().getAdvertisementsDatas();
        if (data == null) {
            group.setVisibility(View.GONE);
            return;
        }

        List<FocusPictureModel> datas = data.getData();
        if (data == null || datas == null || datas.size() == 0) {
            group.setVisibility(View.GONE);
            return;
        } else {
            group.setVisibility(View.VISIBLE);
        }
        int size = datas.size();
        // 将点点加入到ViewGroup中
        tips = new ImageView[size];
        group.removeAllViews();
        // 将图片装载到数组中
        for (int i = 0; i < size; i++) {

            ImageView imageView = new ImageView(getActivity());
            tips[i] = imageView;
            if (i == 0) {
                tips[i].setBackgroundResource(R.drawable.icon_page_counter_hl);
            } else {
                tips[i].setBackgroundResource(R.drawable.icon_page_counter);
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    new ViewGroup.LayoutParams(
                            android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                            android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

            // layoutParams.leftMargin = 5;
            // layoutParams.rightMargin = 5;
            layoutParams.weight = 1;

            if (size == 1)
                imageView.setVisibility(View.GONE);
            group.addView(imageView, layoutParams);
        }
        if (tips.length == 1) {
            group.setVisibility(View.GONE);
        }
        // 将图片装载到数组中
        mViews = new View[datas.size()];
        for (int i = 0; i < mViews.length; i++) {
            View view = getActivity().getLayoutInflater().inflate(
                    R.layout.item_viewpager, null);
            view.setFocusable(false);
            TextView text1 = (TextView) view.findViewById(R.id.tv_video_name);
            TextView text2 = (TextView) view.findViewById(R.id.textView2);
            BitmapTool.getInstance().initAdapterUitl(getActivity())
                    .display(view.findViewById(R.id.img_viewpager),
                            datas.get(i).getPic());
            text1.setText(datas.get(i).getName());
            text2.setText(TimeTool.getFormaTime(datas.get(i).getShowtime()));
            mViews[i] = view;
        }
        // 设置Adapter
        viewPager.setAdapter(new MyPagerAdapter());
        ((ChildViewPager) viewPager).setOnSingleTouchListener(listener);
        // 设置监听，主要是设置点点的背景
        viewPager.setOnPageChangeListener(this);
        // 设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动
        if (mViews.length > 1)
            viewPager.setCurrentItem(mViews.length * 500);
        else
            viewPager.setCurrentItem(0);


        if (timer == null && mViews.length > 1) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            currentTag = viewPager.getCurrentItem() + 1;
                            viewPager.setCurrentItem(currentTag);
                        }
                    });

                }
            }, Constants.LUNBO_TIME, Constants.LUNBO_TIME);
        }

    }

    // View pager的点击事件
    OnSingleTouchListener listener = new OnSingleTouchListener() {
        @Override
        public void onSingleTouch() {
            if (mainActivity.isOpen()) {
                mainActivity.toggleMenu();
                return;
            }
            List<FocusPictureModel> data = ZhiboPageService.getInstance()
                    .getAdvertisementsDatas().getData();
            int positon = viewPager.getCurrentItem() % data.size();
            FocusPictureModel model = data.get(positon);
            Utils.playMedia(model, getActivity());
        }
    };

    // viewpager适配器
    class MyPagerAdapter extends PagerAdapter {
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View currentFocus = getActivity().getCurrentFocus();
            if (currentFocus != null) {
                currentFocus.clearFocus();
            }
            if (mViews != null && mViews.length > 0) {
                if (mViews.length == 1) {
                    container.addView(mViews[position]);
                    return mViews[0];
                }
                position = position % mViews.length;
                container.addView(mViews[position], 0);
                return mViews[position];
            }
            return null;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (mViews != null && mViews.length > 1) {
                position = position % mViews.length;
                View view=mViews[position];
                if (container.findFocus()==view) {
                    container.clearChildFocus(view);
                }
//                if (view== getActivity().getCurrentFocus()) {
//                }
                container.removeView(view);
            }
        }

        @Override
        public int getCount() {
            if (mViews.length == 1)
                return mViews.length;
            else
                return Integer.MAX_VALUE;
        }


        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        switch (arg0) {
            // 手势滑动
            case 1:
                isScrolled = false;
                break;
            // 界面切换
            case 2:
                isScrolled = true;
                break;
            // 滑动结束
            case 0:
               /* // 当前为最后一张，此时从右向左滑，则切换到第一张
                if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1
                        && !isScrolled) {
                    viewPager.setCurrentItem(0, false);
                }
                // 当前为第一张，此时从左向右滑，则切换到最后一张
                else if (viewPager.getCurrentItem() == 0 && !isScrolled) {
                    viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 1,
                            false);
                }*/
                break;

            default:
                break;
        }
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    /**
     * @param object 解析列表界面
     */
    private void updateList(JSONObject object) {
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
//				Collections.sort(dataList, new UpComparatorUser());
                switch (key) {
                    // 广告寻展
                    case 0:
                        // ZhiboPageService
                        ZhiboPageService.getInstance().setAdvertisementsDatas(data);
                        break;
                    // 独家 热播
                    case 2:
//                        updateTimeList(dataList);
                        models.clear();
                        models.addAll(dataList);
                        data.setData(dataList);
                        ZhiboPageService.getInstance().setDujiaData((Data) data);
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

    @Override
    public void onPageSelected(int arg0) {
        // 设置选中的tip的背景
        for (int i = 0; i < tips.length; i++) {
            if (i == arg0 % mViews.length) {
                tips[i].setBackgroundResource(R.drawable.icon_page_counter_hl);
            } else {
                tips[i].setBackgroundResource(R.drawable.icon_page_counter);
            }
        }
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        super.onSuccess(service, action, value);
        switch ((YinyuejieAction) action) {
            case Action_List:
                updateList((JSONObject) value);
                savelocalFile(value);
                break;
            default:
                break;
        }
    }


    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {
        super.onFaile(service, action, value);
        switch ((YinyuejieAction) action) {
            case Action_List:
                try {
                    JSONObject js = new JSONObject(
                            PreferencesUtils.getPreferences(LiveFragment.this
                                            .getActivity().getApplicationContext(),
                                    PreferencesUtils.TYPE_LIVE_FRAGMENT));
                    updateList(js);
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
        switch ((YinyuejieAction) action) {
            case Action_List:
                try {
                    JSONObject js = new JSONObject(
                            PreferencesUtils.getPreferences(LiveFragment.this
                                            .getActivity().getApplicationContext(),
                                    PreferencesUtils.TYPE_LIVE_FRAGMENT));
                    updateList(js);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;

        }
    }

    @Override
    public void onFinish(ServiceAction service, Object action) {
        super.onFinish(service, action);
        mPullToRefreshListView.onRefreshComplete();
    }

    // 首页整个页面数据缓存到sp中
    public void savelocalFile(Object object) {
        PreferencesUtils.savePreferences(LiveFragment.this.getActivity()
                        .getApplicationContext(), PreferencesUtils.TYPE_LIVE_FRAGMENT,
                object.toString());

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case SCROLL_STATE_TOUCH_SCROLL:
                ((MainActivity) getActivity()).showRadioGroup(false);
//                View currentFocus = getActivity().getCurrentFocus();
//                if (currentFocus != null) {
//                    currentFocus.clearFocus();
//                }
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

}

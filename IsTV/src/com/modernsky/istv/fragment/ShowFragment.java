package com.modernsky.istv.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lidroid.xutils.http.RequestParams;
import com.modernsky.istv.R;
import com.modernsky.istv.acitivity.DormMusicActivity;
import com.modernsky.istv.acitivity.MainActivity;
import com.modernsky.istv.acitivity.PaihangbangActivity;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.action.YinyuejieAction;
import com.modernsky.istv.adapter.CommonAdapter;
import com.modernsky.istv.adapter.ViewHolder;
import com.modernsky.istv.bean.ShowAnchorItemInfo;
import com.modernsky.istv.bean.ShowPageItemInfo;
import com.modernsky.istv.tool.BitmapTool;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.PreferencesUtils;
import com.modernsky.istv.utils.Utils;
import com.modernsky.istv.view.ChildViewPager;
import com.modernsky.istv.view.HorizontalListView;
import com.modernsky.istv.view.RoundAngleImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * @author： fengqingyun2008
 * @Email： fengqingyun2008@gmail.com
 * @version：1.0
 * @创建时间：2015-5-29 下午5:39:00
 * @类说明：秀场列表
 */
public class ShowFragment extends BaseFragment implements OnPageChangeListener {
    private MainActivity mainActivity;

    private ViewPager viewPager;
    /*
     * 装点点的ImageView数组
     */
    private ImageView[] tips;
    /*
     * 装ImageView数组
     */
    private View[] mViews;
    /*
     * 图片资源
     */
    // private int[] imgIdArray;
    private int currentTag;
    private ViewGroup group;
    private Timer timer;

    //    private List<ShowPageInfo> showPageInfos;
    //        1 头图 2 轮播 3 热门主播 4 排行榜 5 宿舍大作战
    private List<ShowPageItemInfo> hideDatas;
    private List<ShowPageItemInfo> lunboDatas;
    private List<ShowAnchorItemInfo> hotAnchors;
    private List<ShowPageItemInfo> phbDatas;
    private List<ShowPageItemInfo> dorms;
    // View pager的点击事件
    ChildViewPager.OnSingleTouchListener listener = new ChildViewPager.OnSingleTouchListener() {
        @Override
        public void onSingleTouch() {
            if (mainActivity.isOpen()) {
                mainActivity.toggleMenu();
                return;
            }
            int positon = viewPager.getCurrentItem() % hideDatas.size();
            ShowPageItemInfo showPageItemInfo = hideDatas.get(positon);
            Utils.playMediaShow(showPageItemInfo, getActivity());
        }
    };
    private ImageView dzzImg;
    private ImageView dormImg;
    private PullToRefreshScrollView scollview;
    private View layout_hlv_top, layout_paihangbang, layout_hotanchor, layout_dorm;

    @Override
    public View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();
        return inflater.inflate(R.layout.fragment_show_right, container, false);
    }

    private View rootView;

    @Override
    public void initView(View rootView) {
        this.rootView = rootView;
        scollview = (PullToRefreshScrollView) rootView.findViewById(R.id.scollview);
        layout_hlv_top = rootView.findViewById(R.id.rl_hlv_top);
        layout_paihangbang = rootView.findViewById(R.id.layout_paihangbang);
        layout_hotanchor = rootView.findViewById(R.id.layout_hotanchor);
        layout_dorm = rootView.findViewById(R.id.layout_dorm);
        initDataList();
        initListenner();
        initHeadView();
        getUrlData();
    }

    private void initListenner() {
        scollview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                LogUtils.d("setOnRefreshListener");
                getUrlData();
            }
        });
        scollview.setOnScrollListener(new PullToRefreshScrollView.OnScrollListener() {
            @Override
            public void onScrollStart() {
//                LogUtils.d("onScrollStart");
                ((MainActivity) getActivity()).showRadioGroup(false);
            }

            @Override
            public void onScrollFinish() {
//                LogUtils.d("onScrollFinish");
                ((MainActivity) getActivity()).showRadioGroup(true);
            }
        });
    }

    private void initDataList() {
        hideDatas = new ArrayList<ShowPageItemInfo>();
        lunboDatas = new ArrayList<ShowPageItemInfo>();
        hotAnchors = new ArrayList<ShowAnchorItemInfo>();
        phbDatas = new ArrayList<ShowPageItemInfo>();
        dorms = new ArrayList<ShowPageItemInfo>();
    }

    private void updateData() {
        initAnchorList();
        initHorTopListData();
        initPHBAndDorm();
        initViewPage(rootView);
    }

    private void initPHBAndDorm() {
        LogUtils.d("initPHBAndDorm---排行榜和宿舍");
        if (phbDatas != null && phbDatas.size() > 0) {
            BitmapTool.getInstance().getAdapterUitl().display(dzzImg, phbDatas.get(0).getPic());
            layout_paihangbang.setVisibility(View.VISIBLE);
        } else
            layout_paihangbang.setVisibility(View.GONE);
        if (dorms != null && dorms.size() > 0) {
            BitmapTool.getInstance().getAdapterUitl().display(dormImg, dorms.get(0).getPic());
            layout_dorm.setVisibility(View.VISIBLE);
        } else
            layout_dorm.setVisibility(View.GONE);
    }

    CommonAdapter<ShowAnchorItemInfo> hlva;
    private HorizontalListView hlv;
    private HorizontalListView topHlvList;
    private CommonAdapter<ShowPageItemInfo> hlvTopAdapter;

    private void initHorTopListData() {
        LogUtils.d("initAnchorList---轮播");
        if (lunboDatas != null && lunboDatas.size() > 0) {
            layout_hlv_top.setVisibility(View.VISIBLE);
        } else
            layout_hlv_top.setVisibility(View.GONE);


        if (hlvTopAdapter == null) {
            hlvTopAdapter = new CommonAdapter<ShowPageItemInfo>(mainActivity, lunboDatas, R.layout.item_hlv_showfragment) {
                @Override
                public void convert(ViewHolder helper, ShowPageItemInfo item) {
                    helper.setImageByUrl(R.id.img, item.getPic());
                }
            };
            topHlvList.setAdapter(hlvTopAdapter);
        } else {
            hlvTopAdapter.notifyDataSetChanged();
        }

    }

    private void initAnchorList() {
        LogUtils.d("initAnchorList---主播列表");
        if (hotAnchors != null && hotAnchors.size() > 0) {
            layout_hotanchor.setVisibility(View.VISIBLE);
        } else
            layout_hotanchor.setVisibility(View.GONE);

        if (hlva == null) {
            hlva = new CommonAdapter<ShowAnchorItemInfo>(this.mainActivity, hotAnchors, R.layout.layoyt_anchor) {
                @Override
                public void convert(ViewHolder helper, final ShowAnchorItemInfo item) {
//                    img_anchor_stat
                    ImageView liveImg = helper.getView(R.id.img_anchor_state);
                    if (item.getIslive().equals("0")) {
                        liveImg.setVisibility(View.GONE);
                    } else {
                        liveImg.setVisibility(View.VISIBLE);
                    }
                    RoundAngleImageView imageview = helper.getView(R.id.img_anchor_pic);
                    helper.setText(R.id.tv_lv_anchor, item.getRank().getRank() + "");
                    helper.setText(R.id.tv_num_anchor, item.getRemarks());
                    helper.setText(R.id.tv_name_anchor, item.getUserName());
                    BitmapTool.getInstance().getAdapterUitl().display(imageview, item.getFaceUrl());
                }
            };
            hlv.setAdapter(hlva);
        } else {
            hlva.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_dzz_phb:
                startActivity(new Intent(mainActivity, PaihangbangActivity.class));
                break;
            case R.id.img_dorm:
                startActivity(new Intent(mainActivity, DormMusicActivity.class));
                break;
        }

    }

    private void getUrlData() {
        LogUtils.t("getUrlData", "getUrlData");
        RequestParams params = UrlTool.getParams(Constants.TYPE,
                Constants.TYPE_SHOW_LIST);
        SendActtionTool.get(Constants.URL_HOMEPAGE,
                ServiceAction.Action_YinyueJie, YinyuejieAction.Action_List,
                this, params);
    }


    private void initHeadView() {
        dzzImg = (ImageView) rootView.findViewById(R.id.img_dzz_phb);
        dormImg = (ImageView) rootView.findViewById(R.id.img_dorm);
        dzzImg.setOnClickListener(this);
        dormImg.setOnClickListener(this);
        hlv = (HorizontalListView) rootView.findViewById(R.id.anchorList);
        topHlvList = (HorizontalListView) rootView.findViewById(R.id.hlv_top);
        hlv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Utils.OpenUserInfo(mainActivity, hotAnchors.get(position).getUserId(), "1");
            }
        });
        topHlvList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShowPageItemInfo showPageItemInfo = lunboDatas.get(position);
                Utils.startH5(mainActivity, showPageItemInfo.getName(), showPageItemInfo.getVideoId(), showPageItemInfo.getUrl(), 9);
            }
        });
    }

    /**
     * @param rootView 初始化巡展
     */
    private void initViewPage(View rootView) {
        LogUtils.d("initViewPage--头图");
        if (group == null) {
            group = (ViewGroup) rootView.findViewById(R.id.viewGroup);
            viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        }

        if (hideDatas == null
                || hideDatas.size() == 0) {
            viewPager.setVisibility(View.GONE);
            group.setVisibility(View.GONE);
            return;
        } else {
            viewPager.setVisibility(View.VISIBLE);
            group.setVisibility(View.VISIBLE);
        }
        viewPager.setFocusable(true);
        // 将点点加入到ViewGroup中
        tips = new ImageView[hideDatas.size()];
        group.removeAllViews();
        // 将图片装载到数组中
        int size = hideDatas.size();
        for (int i = 0; i < size; i++) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setLayoutParams(new LayoutParams(10, 10));
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
            layoutParams.weight = 1;
            if (size == 1)
                imageView.setVisibility(View.GONE);
            group.addView(imageView, layoutParams);
        }

        // 将图片装载到数组中
        mViews = new ImageView[hideDatas.size()];
        for (int i = 0; i < mViews.length; i++) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setScaleType(ScaleType.FIT_XY);
//            imageView.setOnClickListener(listener);
            mViews[i] = imageView;
            BitmapTool.getInstance().initAdapterUitl(getActivity())
                    .display(imageView, hideDatas.get(i).getPic());
        }
        ((ChildViewPager) viewPager).setOnSingleTouchListener(listener);
        // 设置Adapter
        viewPager.setAdapter(new MyPagerAdapter());
        // 设置监听，主要是设置点点的背景
        viewPager.setOnPageChangeListener(this);
        // 设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动
        if (mViews != null && mViews.length > 1)
            viewPager.setCurrentItem(mViews.length * 500);
        else
            viewPager.setCurrentItem(0);

        if (timer == null && mViews.length > 1) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    viewPager.post(new Runnable() {

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

    // viewpager适配器
    class MyPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
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
                container.removeView(mViews[position]);
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
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int arg0) {
        // TODO Auto-generated method stub
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
//                updateList((JSONObject) value);
                initJsonData((JSONObject) value);
                scollview.onRefreshComplete();
                savelocalFile(value);
                break;
            default:
                break;
        }
    }

    private void initJsonData(JSONObject value) {
//        1 头图 2 轮播 3 热门主播 4 排行榜 5 宿舍大作战
        try {
            JSONArray arrays = value.getJSONArray("data");
            JSONObject object;
            for (int i = 0; i < arrays.length(); i++) {
                object = (JSONObject) arrays.get(i);
                String group = object.getString("group");
                String isdisplay = object.getString("isdisplay");
                if ("1".equals(isdisplay))
                switch (group) {
                    case "1":
                        List<ShowPageItemInfo> tempHideDatas = JSON.parseArray(object.getString("data"), ShowPageItemInfo.class);
                        if (tempHideDatas != null && tempHideDatas.size() > 0) {
                            hideDatas.clear();
                            hideDatas.addAll(tempHideDatas);
                        }
                        LogUtils.d("initJsonData---hideDatas.size()==" + hideDatas.size());
                        break;
                    case "2":
                        lunboDatas = JSON.parseArray(object.getString("data"), ShowPageItemInfo.class);
                        LogUtils.d("initJsonData---lunboDatas.size()==" + lunboDatas.size());
                        break;
                    case "3":
                        hotAnchors = JSON.parseArray(object.getString("data"), ShowAnchorItemInfo.class);
                        LogUtils.d("initJsonData---hotAnchors.size()==" + hotAnchors.size());
                        break;
                    case "4":
                        phbDatas = JSON.parseArray(object.getString("data"), ShowPageItemInfo.class);
                        break;
                    case "5":
                        dorms = JSON.parseArray(object.getString("data"), ShowPageItemInfo.class);
                        break;
                }
            }
            updateData();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {
        super.onFaile(service, action, value);
        switch ((YinyuejieAction) action) {
            case Action_List:
                scollview.onRefreshComplete();
                try {
                    JSONObject js = new JSONObject(
                            PreferencesUtils.getPreferences(ShowFragment.this
                                            .getActivity().getApplicationContext(),
                                    PreferencesUtils.TYPE_SHOW_FRAGMENT));
//                    updateList(js);

                    initJsonData(js);
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
                scollview.onRefreshComplete();
                try {
                    JSONObject js = new JSONObject(
                            PreferencesUtils.getPreferences(ShowFragment.this
                                            .getActivity().getApplicationContext(),
                                    PreferencesUtils.TYPE_SHOW_FRAGMENT));
//                    updateList(js);
                    initJsonData(js);
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
//        mPullToRefreshListView.onRefreshComplete();
    }

    /**
     * 首页整个页面数据缓存到sp中
     *
     * @param
     */
    public void savelocalFile(Object object) {
        PreferencesUtils.savePreferences(ShowFragment.this.getActivity()
                        .getApplicationContext(), PreferencesUtils.TYPE_SHOW_FRAGMENT,
                object.toString());
    }


}

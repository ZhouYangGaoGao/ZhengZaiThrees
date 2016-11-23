package com.modernsky.istv.acitivity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lidroid.xutils.http.RequestParams;
import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.action.CommentAction;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.adapter.CommonAdapter;
import com.modernsky.istv.adapter.ViewHolder;
import com.modernsky.istv.bean.Album;
import com.modernsky.istv.bean.HotKey;
import com.modernsky.istv.bean.HotSearch;
import com.modernsky.istv.bean.Result;
import com.modernsky.istv.bean.ResultBean;
import com.modernsky.istv.bean.SearchData;
import com.modernsky.istv.bean.SearchTypeBean;
import com.modernsky.istv.bean.Video;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.PreferencesUtils;
import com.modernsky.istv.utils.Utils;
import com.modernsky.istv.view.DrageLayout;
import com.modernsky.istv.view.FlowLayout;
import com.modernsky.istv.view.NoScroListView;
import com.modernsky.istv.window.TitlePop;
import com.modernsky.istv.window.TitlePop.OnItemOnClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author 搜索界面
 */
public class SearchActivity extends BaseActivity {
    // 搜索
    private InputMethodManager mInputMethodManager;
    private EditText mSearchView;
    private Button mSearch_cancel;
    // private LinearLayout mSearchText;
    private int mIv_width;
    private boolean mSearch_cancel_isVisible;
    private ImageView mClearButton;
    private View head_hotspot = null;
    // 搜索结果
    protected NoScroListView mAlbumListView;
    protected NoScroListView mVideoListView;
    private List<Album> albums;
    private List<Video> videos;
    protected CharSequence mFilterString;
    // 历史记录
    private List<String> historys;
    private ListView mListViewHistory;
    private CommonAdapter<String> commonAdapter;
    private CommonAdapter<Video> videoListAdapter;
    private CommonAdapter<Album> albumListAdapter;
    // 热门标签
    private List<HotSearch> hotSearchs;
    private ListView searchHotListView;
    private ListView emptyList;
    private CommonAdapter<HotSearch> searchHotAdapter;
    private View footView;
    private TextView sp_fenlei;// 分类
    private List<SearchTypeBean> data_list;
    //    private CommonAdapter<SearchTypeBean> arr_adapter;
    private TitlePop titlePopup = null;
    // Search
    private int channel = -10;

    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search);
        initDrager();
    }

    private DrageLayout mDrageLayout;
    private RelativeLayout mDrageView;
    RelativeLayout mDragButtomView;

    private void initDrager() {
        mDrageLayout = (DrageLayout) findViewById(R.id.drageLayout);
        mDrageView = (RelativeLayout) findViewById(R.id.draglayoutView);
        mDragButtomView = (RelativeLayout) findViewById(R.id.layoutButtom_drag);
        mDrageLayout.setView(mDrageView, mDragButtomView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDrageLayout.initDrageLayoutPosition();
    }

    @Override
    public void findViewById() {
        initSpinner();
        initSearchView();
        initHotspot();
        initHistory();
        initList();
        initEmptyList();
        if (!UserService.getInatance().isNeedLogin(this)) {
            if (UserService.getInatance().getUserBean(this).getHerald() != null) {
                LogUtils.d("videoId=====" + UserService.getInatance().getUserBean(this).getHerald().getVideoId());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_hot_detail:
                break;
            case R.id.tv_fenlei:
                titlePopup.show(v);
                break;
            default:
                break;
        }
    }

    //
    private void initList() {
        mAlbumListView = (NoScroListView) findViewById(R.id.lv_album);
        mVideoListView = (NoScroListView) findViewById(R.id.lv_video);
        albums = new ArrayList<Album>();
        videos = new ArrayList<Video>();
        //
        albumListAdapter = new CommonAdapter<Album>(this, albums, R.layout.item_search_album) {
            @Override
            public void convert(ViewHolder helper, Album item) {
                ImageView conner = helper.getView(R.id.item_rotate_textview);
                helper.setImageByUrl(R.id.imageView1, item.getStandardPic());
                conner.setVisibility(View.GONE);
            }
        };
        mAlbumListView.setAdapter(albumListAdapter);
        mAlbumListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 播放专辑
                Album album = albums.get(position);
                Utils.playAlbum(SearchActivity.this, album.getAlbumId());
            }
        });
        //
        videoListAdapter = new CommonAdapter<Video>(this, videos, R.layout.item_listview_search_ablumlist) {
            @Override
            public void convert(ViewHolder helper, Video item) {
                helper.setText(R.id.tv_video_name, item.getName());
                helper.setImageByUrl(R.id.imageView1, item.getStandardPic());
            }
        };
        mVideoListView.setAdapter(videoListAdapter);
        mVideoListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 播放专辑
                Video video = videos.get(position);
                Utils.playVideo(SearchActivity.this, video.getVideoId(), video.getName());
            }
        });
    }

    // 初始化分类
    private void initSpinner() {
        data_list = new ArrayList<SearchTypeBean>();
        sp_fenlei = (TextView) findViewById(R.id.tv_fenlei);
        sp_fenlei.setOnClickListener(this);
        titlePopup = new TitlePop(getActivity());
        titlePopup.setItemOnClickListener(new OnItemOnClickListener() {
            @Override
            public void onItemClick(SearchTypeBean item, int position) {
                sp_fenlei.setText(item.getName());
                channel = (Integer) item.getChannelId();
//                mSearchView.setText("");
                if (!TextUtils.isEmpty(mSearchView.getText().toString())) {
                    search(mSearchView.getText().toString());
                }
            }
        });
        getTypeSearchData();
    }

    /**
     * 初始化搜索控件
     */
    private void initSearchView() {
        mSearchView = (EditText) findViewById(R.id.searchview);
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
        mSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkCancle();
                mAlbumListView.setVisibility(View.GONE);
                mVideoListView.setVisibility(View.GONE);
            }
        });
        mSearchView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mSearchView.isFocused()) {
                    mListViewHistory.setVisibility(View.VISIBLE);
                    searchHotListView.setVisibility(View.GONE);
                    // TODO
                    emptyList.setVisibility(View.GONE);
                    mAlbumListView.setVisibility(View.GONE);
                    mVideoListView.setVisibility(View.GONE);
                }
                return false;
            }
        });
        mSearchView.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    String query = mSearchView.getText().toString().trim();
                    mInputMethodManager.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
                    search(query);
                    return true;
                }
                return false;
            }
        });
        mSearch_cancel = (Button) findViewById(R.id.search_cancel);
        mSearch_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSearch_cancel.getText().toString().equals("取消")) {
                    finish();
                    return;
                }
                // updateView(mSearch_cancel);
                // if (!mSearch_cancel_isVisible) {
                //
                // return;
                // }
                mSearch_cancel_isVisible = false;
                mSearchView.setText("");
                // getAnimatorList(false).start();
                //
                mListViewHistory.setVisibility(View.GONE);
                mAlbumListView.setVisibility(View.GONE);
                mVideoListView.setVisibility(View.GONE);
                searchHotListView.removeHeaderView(head_hotspot);
                searchHotListView.setVisibility(View.VISIBLE);
                searchHotAdapter.notifyDataSetChanged();
            }
        });
        checkCancle();
    }

    // 初始化热门标签
    private void initHotspot() {
        searchHotListView = (ListView) findViewById(R.id.listview_search_hot);
        hotSearchs = new ArrayList<HotSearch>();
        searchHotAdapter = new CommonAdapter<HotSearch>(this, hotSearchs, R.layout.item_search_hot) {
            @Override
            public void convert(ViewHolder helper, HotSearch item) {
                helper.setText(R.id.tv_hot_detail, item.getName());
                FlowLayout mFlowLayout = helper.getView(R.id.flowgroup);
                List<HotKey> data = item.getData();
                mFlowLayout.removeAllViews();
                for (int i = 0; i < data.size(); i++) {
                    final TextView tv = (TextView) mInflater.inflate(R.layout.tv, mFlowLayout, false);
                    tv.setText(data.get(i).getName());
                    tv.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            searchHotListView.setVisibility(View.GONE);
                            String str = tv.getText().toString().trim();
                            mSearchView.setText(str);
                            mSearchView.setSelection(mSearchView.getText().length());
                            search(str);
                        }
                    });
                    mFlowLayout.addView(tv);
                }
            }
        };
        searchHotListView.setAdapter(searchHotAdapter);
        getHotSearchData();
    }

    // 初始化热门标签
    private void initEmptyList() {
        head_hotspot = LayoutInflater.from(getBaseContext()).inflate(R.layout.head_hotspot, null);
        emptyList = (ListView) findViewById(R.id.listview_empty);
        emptyList.addHeaderView(head_hotspot);
        emptyList.setAdapter(searchHotAdapter);
        emptyList.setVisibility(View.GONE);
        getHotSearchData();
    }

    /**
     * 初始化历史记录
     */
    private void initHistory() {
        String str = PreferencesUtils.getPreferences(this, PreferencesUtils.TYPE_HOSTORY_LIST);
        if (historys == null) {
            historys = new ArrayList<String>();
        }
        if (!TextUtils.isEmpty(str)) {
            historys = JSON.parseArray(str, String.class);
        }
        mListViewHistory = (ListView) findViewById(R.id.listview_history);
        commonAdapter = new CommonAdapter<String>(this, historys, R.layout.item_history) {
            @Override
            public void convert(ViewHolder helper, String item) {
                helper.setText(R.id.tv_name, item);
            }
        };
        footView = getLayoutInflater().inflate(R.layout.tv_single, null);
        if (commonAdapter.getCount() > 0) {
            mListViewHistory.addFooterView(footView);
        }
        mListViewHistory.setAdapter(commonAdapter);
        mListViewHistory.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < historys.size()) {
                    mSearchView.setText(historys.get(position));
                    search(historys.get(position));
                } else {
                    historys.clear();
                    mListViewHistory.removeFooterView(footView);
                    saveHistorys(historys);
                    commonAdapter.notifyDataSetChanged();
                    Utils.toast(getApplicationContext(), "已经清空历史记录");
                }
            }
        });
    }

    private void checkCancle() {
        if (mSearchView.getText().toString().length() == 0) {
            mSearch_cancel.setText("取消");
            return;
        }
        mSearch_cancel.setText("清空");
    }

    // 动画
    private AnimatorSet getAnimatorList(boolean isEditMode) {
        AnimatorSet set = new AnimatorSet();
        Animator[] animatorArray = new Animator[2];
        if (isEditMode) {
            animatorArray[1] = visibleView(mSearch_cancel, true);
        } else {
            animatorArray[1] = visibleView(mSearch_cancel, false);
        }
        set.play(animatorArray[1]);
        return set;
    }

    // 出现取消按钮
    private Animator visibleView(final View target, final boolean visible) {
        // 平移动画
        ValueAnimator animator = (ValueAnimator) target.getTag(R.id.search_cancel);
        if (animator != null) {
            animator.cancel();
        }
        if (visible) {
            animator = ValueAnimator.ofInt(-mIv_width, 0);
        } else {
            animator = ValueAnimator.ofInt(0, -mIv_width);
        }
        target.setTag(R.id.search_cancel, animator);
        ValueAnimator.setFrameDelay(1000 / 60);
        final LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) target.getLayoutParams();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();
                params.rightMargin = value;
                target.getParent().requestLayout();
            }

        });
        return animator;
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        super.onSuccess(service, action, value);
        dismissDialog();
        String jsonString = value.toString();
        LogUtils.t("jsonString== ", jsonString);
        switch ((CommentAction) action) {
            case Action_Serach_Keywords:
                Result<Album, Video> temResult = JSON.parseObject(jsonString, new TypeReference<Result<Album, Video>>() {
                });
                if (temResult == null) {
                    return;
                }
                SearchData data = temResult.data;
                if (data == null) {
                    showSearchResult(false);
                    return;
                }
                boolean empty = data.getAlbumList().size() == 0 && data.getVideoList().size() == 0;
                if (empty) {
                    showSearchResult(false);
                    return;
                }
                showSearchResult(true);
                List<Album> albumList = data.getAlbumList();
                if (albumList == null) {
                } else {
                    albums.clear();
                    albums.addAll(albumList);
                    albumListAdapter.notifyDataSetChanged();
                }
                List<Video> videoList = data.getVideoList();
                if (videoList == null) {
                } else {
                    videos.clear();
                    videos.addAll(videoList);
                    videoListAdapter.notifyDataSetChanged();
                }
                break;
            case Aciton_getSearchHot:
                try {
                    ResultBean<List<HotSearch>> tempBean = JSON.parseObject(jsonString, new TypeReference<ResultBean<List<HotSearch>>>() {
                    });
                    if (tempBean != null) {
                        List<HotSearch> data1 = tempBean.data;
                        if (data1 != null && data1.size() > 0) {
                            hotSearchs.clear();
                            hotSearchs.add(data1.get(0));
                            hotSearchs.add(data1.get(1));
                            // hotSearchs.addAll(data1);
                            searchHotAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            // 搜索分类数据填充
            case Action_getSearchType:
                try {
                    ResultBean<List<SearchTypeBean>> tempBean = JSON.parseObject(jsonString, new TypeReference<ResultBean<List<SearchTypeBean>>>() {
                    });
                    if (tempBean != null) {
                        List<SearchTypeBean> data1 = tempBean.data;
                        if (data1 != null && data1.size() > 0) {
                            data_list.clear();
                            data_list.addAll(data1);
                            extraMenuInit();
                            LogUtils.d("data != null && data.size() > 0");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    //
    private void showSearchResult(boolean haveResult) {
        if (haveResult) {
            searchHotListView.setVisibility(View.GONE);
            mListViewHistory.setVisibility(View.GONE);
            emptyList.setVisibility(View.GONE);
            mAlbumListView.setVisibility(View.VISIBLE);
            mVideoListView.setVisibility(View.VISIBLE);
            searchHotAdapter.notifyDataSetChanged();
            return;
        }
        // showEmpty
        mListViewHistory.setVisibility(View.GONE);
        searchHotListView.setVisibility(View.GONE);
        emptyList.setVisibility(View.VISIBLE);
        mAlbumListView.setVisibility(View.GONE);
        mVideoListView.setVisibility(View.GONE);
        searchHotAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {
        super.onFaile(service, action, value);
        dismissDialog();
        switch ((CommentAction) action) {
            case Action_Serach_Keywords:
                showSearchResult(false);
        }
    }

    @Override
    public void onException(ServiceAction service, Object action, Object value) {
        super.onException(service, action, value);
        dismissDialog();
        switch ((CommentAction) action) {
            case Action_Serach_Keywords:
                showSearchResult(false);
        }
    }

    private void search(String keyword) {
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("keywords", keyword);
        // params.addQueryStringParameter("terminal", "3");
        if (channel != -10) {
            params.addQueryStringParameter("channel", channel + "");
        }
        //
        SendActtionTool.get(Constants.URL_SEARCH_KEYWORDS, ServiceAction.Action_Comment, CommentAction.Action_Serach_Keywords, this, params);
        //
        if (keyword.length() == 0) {
            return;
        }
        if (historys.size() == 0) {
            mListViewHistory.addFooterView(footView);
        }
        if (historys.size() > 5) {
            historys.remove(0);
        }
        if (historys.contains(keyword)) {
            historys.remove(keyword);
        }
        // 重要 倒叙
        Collections.reverse(historys);
        historys.add(keyword);
        Collections.reverse(historys);
        mListViewHistory.setVisibility(View.GONE);
        saveHistorys(historys);
        commonAdapter.notifyDataSetChanged();
        showLoadingDialog();
    }

    // 保存历史记录到sharePreferences
    private void saveHistorys(List<String> list) {
        String str = JSON.toJSONString(list);
        PreferencesUtils.savePreferences(getApplicationContext(), PreferencesUtils.TYPE_HOSTORY_LIST, str);
    }

    @Override
    public void onStart(ServiceAction service, Object action) {
    }

    @Override
    public void onFinish(ServiceAction service, Object action) {
    }

    // 附加功能菜单
    private void extraMenuInit() {
        titlePopup.addList((ArrayList<SearchTypeBean>) data_list);
    }

    // 获取分类标签数据
    private void getTypeSearchData() {
        SendActtionTool.get(Constants.URL_SEARCH_TYPE, ServiceAction.Action_Comment, CommentAction.Action_getSearchType, this);
    }

    // 获取热门标签数据
    private void getHotSearchData() {
        SendActtionTool.get(Constants.URL_SEARCH_HOT, ServiceAction.Action_Comment, CommentAction.Aciton_getSearchHot, this);
    }

    public Context getActivity() {
        return getBaseContext();
    }
}

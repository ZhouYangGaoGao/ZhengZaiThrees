package com.modernsky.istv.acitivity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.aliyun.mbaas.oss.callback.SaveCallback;
import com.aliyun.mbaas.oss.model.OSSException;
import com.lidroid.xutils.http.RequestParams;
import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.action.UserAction;
import com.modernsky.istv.adapter.HomePageViewPagerAdapter;
import com.modernsky.istv.bean.UserEntity;
import com.modernsky.istv.fragment.MyAttentionListFragment;
import com.modernsky.istv.fragment.MyFansListFragment;
import com.modernsky.istv.fragment.MyTaskListFragment;
import com.modernsky.istv.fragment.MyWorksListFragment;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.BitmapTool;
import com.modernsky.istv.tool.DialogTool;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.FileUtils;
import com.modernsky.istv.utils.GeneralTool;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.MediaUtil;
import com.modernsky.istv.utils.PreferencesUtils;
import com.modernsky.istv.utils.StringUtils;
import com.modernsky.istv.utils.Utils;
import com.modernsky.istv.view.DrageLayout;
import com.modernsky.istv.view.PopThreeShare;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 个人主页
 *
 * @author mufaith
 * @time 16/3/4 上午11:27
 */
public class UserHomepageActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    private RelativeLayout ll_root;
    private ViewPager viewPager_up, viewPager_down;
    private MyAttentionListFragment attentionListFragment;//关注
    private MyFansListFragment fansListFragment;//粉丝
    private MyWorksListFragment worksListFragment;//作品
    private MyTaskListFragment taskListFragment;//任务
    private HomePageViewPagerAdapter adapter;
    // 登录状态改变广播
    private MyReceive myReceive;
    private UserEntity mUserEntity;


    private View view_info, view_introduce;
    private TextView tv_nikeName, tv_grade, tv_user_type, tv_jifen, tv_user_address, tv_rank_value, tv_bean_value,
            tv_power_value, tv_ding_value, tv_zan, tv_introduce, tv_bean_key;


    private ImageView iv_user, iv_male, iv_female;
    private ImageView imgBtn_money, imgBtn_news;

    private ProgressBar mBar, mBar_2;
    private LinearLayout ll_progressbar;


    private LinearLayout ll_edit, ll_address, ll_rank, ll_bean;

    private List<View> viewList = new ArrayList<>();
    private ImageButton imgBtn_back;
    private ImageView iv_share;
    private ImageView[] dots;
    private int currentIndex;

    private boolean isAnchor = false;//是否主播
    private boolean isSelf = false;//是否登陆用户
    public String viewId; // 传入的查看用户id
    private String addVideoId;//传入的添加作品id
    public String typeId = "0";//传入的信息类型

    private boolean isRefresh = false; //是否是刷新状态
    private int pageIndex;//刷新页面序号
    private TextView img_new_letter;

    private List<String> titles;
    private List<String> nums;

    private TabLayout tl;
    private static final String POSITIION = "position";
    private PopThreeShare mPopThreeShare;


    /**
     * 头像上传处理部分
     */
    private static final int PHOTO_REQUEST_CAMERA = 101;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 201;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 301;// 结果`
    Dialog eduation_dialog;

    /* 头像名称 */
    private static String PHOTO_FILE_NAME = "temp_photo.jpg";
    private File tempFile;
    private Bitmap bitmap;
    protected String userFaceUrl;

    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_user_homepage);

        myReceive = new MyReceive();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_USERBEAN_CHANGE);
        registerReceiver(myReceive, filter);
        initDrager();
    }

    //音乐小球
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

        imgBtn_back = (ImageButton) findViewById(R.id.imgBtn_back);
        iv_share = (ImageView) findViewById(R.id.imgBtn_set_share);
        tv_nikeName = (TextView) findViewById(R.id.tv_nickName);
        iv_user = (ImageView) findViewById(R.id.iv_user);

        ll_root = (RelativeLayout) findViewById(R.id.ll_root);


        verifyUserIdentity();
        initListener();
    }

    //发起网络请求
//    private void getIfHasNewletter() {
//        SendActtionTool.post(Constants.UserParams.URL_GET_ONE, null, UserAction.ACTION_GET_LETTERINFO, this, UrlTool
//                .getPostParams(Constants.USER_ID, viewId, Constants
//                        .UserParams.TERMINAL, "MOBILE"));
//    }

    //获取未读消息数量
    private void getUnReadMsgCount() {
        SendActtionTool.post(Constants.UserParams.URL_GET_UNREAND_COUNT, null, UserAction.ACTION_GET_UNREAND_COUNT, this, UrlTool
                .getPostParams(Constants.USER_ID, viewId));
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(myReceive);
        super.onDestroy();
    }

    //viewId, isSelf
    private MyFansListFragment initMyfans() {
        Bundle bundle = new Bundle();
        bundle.putString("viewId", viewId);
        bundle.putBoolean("isSelf", isSelf);
        MyFansListFragment fragment = new MyFansListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    //viewId, isSelf
    private MyAttentionListFragment initMyAttention() {
        Bundle bundle = new Bundle();
        bundle.putString("viewId", viewId);
        bundle.putBoolean("isSelf", isSelf);
        MyAttentionListFragment fragment = new MyAttentionListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    //    viewId, "", isSelf, isAnchor
    private MyWorksListFragment initMyWrks() {
        Bundle bundle = new Bundle();
        bundle.putString("viewId", viewId);
        bundle.putBoolean("isSelf", isSelf);
        bundle.putBoolean("isAnchor", isAnchor);
        bundle.putString("addVideoId", "");
        MyWorksListFragment fragment = new MyWorksListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private MyTaskListFragment initMyTask() {
        Bundle bundle = new Bundle();
        bundle.putString("viewId", viewId);
        MyTaskListFragment fragment = new MyTaskListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private void initViewPagers() {

        viewPager_up = (ViewPager) findViewById(R.id.viewpager_up);
        viewPager_down = (ViewPager) findViewById(R.id.viewpager_down);

//        AdapterPagerFragment adapter = new AdapterPagerFragment(getSupportFragmentManager());,
        adapter = new HomePageViewPagerAdapter(getSupportFragmentManager(), this);


        attentionListFragment = (attentionListFragment == null) ? initMyAttention() :
                attentionListFragment;
        fansListFragment = (fansListFragment == null) ? initMyfans() : fansListFragment;
        worksListFragment = (worksListFragment == null) ? initMyWrks() :
                worksListFragment;
        taskListFragment = (taskListFragment == null) ? initMyTask() :
                taskListFragment;

        adapter.addFragment(attentionListFragment);
        adapter.addFragment(fansListFragment);

        if (isAnchor) {
            adapter.addFragment(worksListFragment);
        }
        if (isSelf) {
            adapter.addFragment(taskListFragment);
        }
        viewPager_down.setAdapter(adapter);

        tl = (TabLayout) findViewById(R.id.tabs);
        tl.setupWithViewPager(viewPager_down);

        titles = new ArrayList<>();
        titles.add("关注");
        titles.add("粉丝");
        if (isAnchor) {
            titles.add("作品");
        }
        if (isSelf) {
            titles.add("任务");
        }
        nums = new ArrayList<>();
        nums.add(mUserEntity.getAttentionCount() + "");
        nums.add(mUserEntity.getFansCount() + "");
        nums.add(mUserEntity.getVideoCount() + "");

        for (int i = 0; i < tl.getTabCount(); i++) {
            TabLayout.Tab tabAt = tl.getTabAt(i);
            tabAt.setCustomView(adapter.getTabView(i, titles, nums, typeId, isSelf, isAnchor));

        }
        adapter.notifyDataSetChanged();

        tl.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ((TextView) tab.getCustomView().findViewById(R.id.tv_title)).setTextColor(Color.parseColor("#ffffff"));
                ((TextView) tab.getCustomView().findViewById(R.id.tv_num)).setTextColor(Color.parseColor("#ffffff"));
                viewPager_down.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                ((TextView) tab.getCustomView().findViewById(R.id.tv_title)).setTextColor(Color.parseColor("#9a9a9a"));
                ((TextView) tab.getCustomView().findViewById(R.id.tv_num)).setTextColor(Color.parseColor("#555555"));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        // google的bug，2为滑动完毕，0为初始化，不一样的
        viewPager_down.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tl) {
            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == 2) {
                    state = 0;
                }
                super.onPageScrollStateChanged(state);
            }
        });
        if (adapter.getCount()>0) {
            viewPager_down.setCurrentItem(0);
        }
        switch (Integer.parseInt(typeId)) {
            case 0:
                if (adapter.getCount()>0) {
                    viewPager_down.setCurrentItem(0);
                }
                break;
            case 1:
                if (adapter.getCount()>0) {
                    viewPager_down.setCurrentItem(0);
                }
                break;
            case 2:
                if (adapter.getCount()>1) {
                    viewPager_down.setCurrentItem(1);
                }
                break;
            case 3:
                if (adapter.getCount()>2) {
                    viewPager_down.setCurrentItem(2);
                }
                break;
        }

        LayoutInflater inflater = LayoutInflater.from(this);

        view_info = inflater.inflate(R.layout.user_header_info, null);
        view_introduce = inflater.inflate(R.layout.include_user_intro, null);

        img_new_letter = (TextView) view_info.findViewById(R.id.im_new_badger);
        tv_grade = (TextView) view_info.findViewById(R.id.tv_grade);
        tv_user_type = (TextView) view_info.findViewById(R.id.tv_user_type);
        tv_jifen = (TextView) view_info.findViewById(R.id.tv_jifen);
        tv_user_address = (TextView) view_info.findViewById(R.id.tv_user_address);
        tv_rank_value = (TextView) view_info.findViewById(R.id.tv_rank_value);
        tv_bean_value = (TextView) view_info.findViewById(R.id.tv_bean_value);
        tv_power_value = (TextView) view_info.findViewById(R.id.tv_power_value);
        tv_ding_value = (TextView) view_info.findViewById(R.id.tv_ding_value);
        tv_bean_key = (TextView) view_info.findViewById(R.id.tv_power_key);
        tv_zan = (TextView) view_info.findViewById(R.id.tv_zan);
        mBar = (ProgressBar) view_info.findViewById(R.id.proBar);
        mBar_2 = (ProgressBar) view_info.findViewById(R.id.proBar_2);
        ll_progressbar = (LinearLayout) view_info.findViewById(R.id.ll_progressbar);

        imgBtn_money = (ImageView) view_info.findViewById(R.id.imgBtn_money);
        imgBtn_news = (ImageView) view_info.findViewById(R.id.imgBtn_news);
        ll_edit = (LinearLayout) view_info.findViewById(R.id.ll_edit);
        imgBtn_money.setOnClickListener(UserHomepageActivity.this);
        imgBtn_news.setOnClickListener(UserHomepageActivity.this);
        ll_edit.setOnClickListener(UserHomepageActivity.this);


        ll_address = (LinearLayout) view_info.findViewById(R.id.ll_address);
        ll_rank = (LinearLayout) view_info.findViewById(R.id.ll_rank);
        ll_bean = (LinearLayout) view_info.findViewById(R.id.ll_bean);


        iv_male = (ImageView) view_info.findViewById(R.id.iv_male);
        iv_female = (ImageView) view_info.findViewById(R.id.iv_female);

        tv_introduce = (TextView) view_introduce.findViewById(R.id.tv_introduction);


        viewList.add(view_info);
        viewList.add(view_introduce);
        viewPager_up.setAdapter(new MyViewPagerAdapter(viewList));

        viewPager_up.setOnPageChangeListener(this);
        viewPager_up.setOnClickListener(this);
        initDots();
        if (isSelf)
            getUnReadMsgCount();

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt(POSITIION, tl.getSelectedTabPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (viewPager_down != null)
            viewPager_down = (ViewPager) findViewById(R.id.viewpager_down);
        if (adapter.getCount()>savedInstanceState.getInt(POSITIION)) {
            viewPager_down.setCurrentItem(savedInstanceState.getInt(POSITIION));
        }
//        viewPager_down.setCurrentItem(savedInstanceState.getInt(POSITIION));
    }

    private void initListener() {

        imgBtn_back.setOnClickListener(this);
        iv_share.setOnClickListener(this);
//        view_info.setOnClickListener(this);


    }

    //核验用户身份
    private void verifyUserIdentity() {
        String userId = UserService.getInatance().getUserBean(this).getId();
        viewId = StringUtils.isEmpty(getIntent().getStringExtra(Constants.USER_ID)) ? userId : getIntent()
                .getStringExtra(Constants.USER_ID);
        typeId = TextUtils.isEmpty(getIntent().getStringExtra("typeId")) ? "0" : getIntent().getStringExtra("typeId");
        addVideoId = StringUtils.isEmpty(getIntent().getStringExtra("addVideoId")) ? "" : getIntent().getStringExtra
                ("addVideoId");
        if (viewId.equals(userId)) {
            isSelf = true;

            if (UserService.getInatance().getUserBean(this).getStatus() == 6) {
                showGuideDialog(0);
            } else {
                showGuideDialog(2);
            }
        }
        getContent();
    }


    //发起网络请求
    private void getContent() {
        SendActtionTool.post(Constants.UserParams.URL_GET_ONE, null, UserAction.ACTION_GET_USERENTITY, this, UrlTool
                .getPostParams(Constants.USER_ID, viewId, Constants
                        .UserParams.TERMINAL, "MOBILE"));
    }

    private void setContent() {
        tv_nikeName.setText(mUserEntity.getUserName());
        BitmapTool.getInstance().getAdapterUitl().display(iv_user, mUserEntity.getFaceUrl());
        if (isAnchor) {
            iv_share.setVisibility(View.VISIBLE);
        } else {
            iv_share.setVisibility(View.GONE);
        }

        tv_grade.setText(mUserEntity.getRank().getRank());
        if (mUserEntity.getStatus() == 6) {
            tv_grade.setBackgroundResource(R.drawable.icon_levelbg);
        } else {
            tv_grade.setBackgroundResource(R.drawable.icon_levelbg_person);

        }
        if (TextUtils.isEmpty(mUserEntity.getSign())) {
            tv_introduce.setText("这个人很懒，什么都没有留下……");
        } else {
            tv_introduce.setText(mUserEntity.getSign());
        }
        if (!StringUtils.isEmpty(mUserEntity.getBadge().getName())) {

            tv_user_type.setText(mUserEntity.getBadge().getName());
        }
        if (mUserEntity.getSex() == 0) {

            iv_female.setImageDrawable(getResources().getDrawable(R.drawable.icon_woman));
            iv_male.setImageDrawable(getResources().getDrawable(R.drawable.icon_woman));
        } else if (mUserEntity.getSex() == 1) {
            iv_female.setImageDrawable(getResources().getDrawable(R.drawable.icon_man));
            iv_female.setImageDrawable(getResources().getDrawable(R.drawable.icon_man));
        } else {
            iv_female.setImageDrawable(getResources().getDrawable(R.drawable.icon_secret_sex));
            iv_male.setImageDrawable(getResources().getDrawable(R.drawable.icon_secret_sex));
        }
//        if (!StringUtils.isEmpty(mUserEntity.getBadge().getImgUrl())) {
//
//            BitmapTool.getInstance().getAdapterUitl()
//                    .display(iv_crown, mUserEntity.getBadge().getImgUrl());
//        }
        if (isSelf) {
            ll_progressbar.setVisibility(View.VISIBLE);
            float tmp = 0;
            if (mUserEntity.getRank() != null)
                tmp = (float) (mUserEntity.getExper() - mUserEntity.getRank().getMinValue()) / (mUserEntity.getRank()
                        .getMaxValue() - mUserEntity.getRank().getMinValue());
            int value = (int) (tmp * 100);
            if (isAnchor) {
                mBar.setVisibility(View.GONE);
                mBar_2.setVisibility(View.VISIBLE);
                mBar_2.setProgress(value);
            } else {
                mBar.setVisibility(View.VISIBLE);
                mBar_2.setVisibility(View.GONE);
                mBar.setProgress(value);
            }
        } else {
            ll_progressbar.setVisibility(View.GONE);
        }

        tv_jifen.setText(mUserEntity.getExper() + "/" + mUserEntity.getRank().getMaxValue());

        if (!StringUtils.isEmpty(mUserEntity.getAddress())) {

            tv_user_address.setText(mUserEntity.getAddress());
        }
        if (!StringUtils.isEmpty(String.valueOf(mUserEntity.getRanking()))) {
            showValue(tv_rank_value, mUserEntity.getRanking());//排名
        }
        if (!StringUtils.isEmpty(String.valueOf(mUserEntity.getStrawCount()))) {
            showValue(tv_bean_value, mUserEntity.getStrawCount());//Power数量

        }

        if (!StringUtils.isEmpty(String.valueOf(mUserEntity.getMbCount()))) {
            showValue(tv_power_value, mUserEntity.getMbCount()); //MB数量
        }
        if (!StringUtils.isEmpty(String.valueOf(mUserEntity.getPraiseCount()))) {
            showValue(tv_ding_value, mUserEntity.getPraiseCount());//赞数量
        }


        if (isSelf) {
            imgBtn_money.setImageResource(R.drawable.icon_xiaofeijilu);
            ll_edit.setVisibility(View.VISIBLE);

        } else {
            if (mUserEntity.getIsAttention() == 0) {
                imgBtn_money.setImageResource(R.drawable.icon_hearts_nor);
            } else {
                imgBtn_money.setImageResource(R.drawable.icon_hearts_hl);
            }
            imgBtn_news.setVisibility(View.GONE);
            ll_edit.setVisibility(View.GONE);
            if (isAnchor) {
                tv_bean_key.setText("人气值");
                Long tmp = mUserEntity.getExper();
                showValue(tv_power_value, tmp.intValue());
            } else {
                ll_bean.setVisibility(View.GONE);
            }
        }

        if (isAnchor) {
            iv_female.setVisibility(View.GONE);
            ll_address.setVisibility(View.VISIBLE);
            ll_rank.setVisibility(View.VISIBLE);
            tv_zan.setText("收到赞");

        } else {
            iv_female.setVisibility(View.VISIBLE);
            ll_address.setVisibility(View.GONE);
            ll_rank.setVisibility(View.GONE);
            tv_zan.setText("送出赞");
        }
    }

    /**
     * 数值显示(根据大小改单位显示) 小数点后保留两位
     *
     * @param
     * @return
     */
    private void showValue(TextView view, int value) {
        float result;
        if (value < 10000) {
            view.setText(value + "");
        } else if (value > 10000) {
            result = (float) value / 10000;
//            DecimalFormat df = new DecimalFormat("0.00");//保留两位小数
//            String str = df.format(result);

            BigDecimal b = new BigDecimal(result);
            result = b.setScale(2, BigDecimal.ROUND_FLOOR).floatValue();

            view.setText(result + "w");
        }
    }

    /**
     * @param pageIndex 0:关注页面数显 1:粉丝页面刷新 2:任务列表页刷新
     * @return
     */
    public void updateTab(int pageIndex) {
        this.pageIndex = pageIndex;
        isRefresh = true;
        getContent();
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        super.onSuccess(service, action, value);
        switch ((UserAction) action) {
            case ACTION_GET_USERENTITY:
                JSONObject jObj = (JSONObject) value;
                String json = null;
                try {
                    json = jObj.getJSONObject(Constants.USER_ENTITY).toString();

                    mUserEntity = JSON.parseObject(json, UserEntity.class);

                } catch (JSONException e) {
                    e.printStackTrace();

                }
                if (mUserEntity != null) {
                    if (mUserEntity.getStatus() == 6) {
                        isAnchor = true;
                    }
                    if (isSelf) {
                        UserService.getInatance().setUserBean(mUserEntity, this);
                    }
                    if (isRefresh) {
                        switch (pageIndex) {
                            case 0:
                                adapter.getTextView().setText(String.valueOf(mUserEntity.getAttentionCount()));

                                break;
                            case 1:
                                adapter.getTextView2().setText(String.valueOf(mUserEntity.getFansCount()));

                                break;
                            case 2:
                                tv_bean_value.setText(String.valueOf(mUserEntity.getStrawCount()));
                                break;
                            case 3:
                                adapter.getTextView3().setText(String.valueOf(mUserEntity.getVideoCount()));
                                break;
                        }
                    } else {
                        initViewPagers();
                        setContent();
                    }
                }

                LogUtils.d("ceshi", json);
                break;
            case Action_Update_Face_Url:
                Utils.toast(getApplicationContext(), "头像上传成功");
                mUserEntity.setFaceUrl(userFaceUrl);
                UserService.getInatance().setUserBean(mUserEntity, this);
                BitmapTool.getInstance().initAdapterUitl(this).display(iv_user, userFaceUrl);
                break;
            case ACTION_USER_ATTENTION:
                JSONObject jObj2 = (JSONObject) value;
                int isAttention = 0;
                try {

                    isAttention = jObj2.getJSONObject(Constants.DATA).getInt("isAttention");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (isAttention == 1) {
                    mUserEntity.setIsAttention(1);
                    Utils.toast(this, "已关注");
                    imgBtn_money.setImageResource(R.drawable.icon_hearts_hl);
                } else {
                    mUserEntity.setIsAttention(0);
                    Utils.toast(this, "已取消关注");
                    imgBtn_money.setImageResource(R.drawable.icon_hearts_nor);
                }
                break;
            case ACTION_GET_UNREAND_COUNT://未读站内信数量
                if (img_new_letter == null) {
                    return;
                }
                JSONObject jObj3 = (JSONObject) value;
                try {
                    if (jObj3.getInt("data") > 0) {
                        img_new_letter.setVisibility(View.VISIBLE);
                        img_new_letter.setText(jObj3.getString("data"));
                    } else
                        img_new_letter.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isSelf)
            getUnReadMsgCount();
    }

    @Override
    public void onStart(ServiceAction service, Object action) {
        super.onStart(service, action);
        switch ((UserAction) action) {
            case ACTION_GET_USERENTITY:
                if (!isRefresh) {
                    showLoadingDialog();
                }
                break;
        }
    }

    @Override
    public void onFinish(ServiceAction service, Object action) {
        super.onFinish(service, action);
        switch ((UserAction) action) {
            case ACTION_GET_USERENTITY:
                dismissDialog();
                break;
            case Action_Update_Face_Url:
                dismissDialog();
                break;
        }

    }

    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {
        super.onFaile(service, action, value);
        switch ((UserAction) action) {
            case ACTION_GET_USERENTITY:
                com.modernsky.istv.utils.Utils.toast(this, "数据加载失败");
                LogUtils.d("个人主页数据加载失败");
                break;
        }
    }

    private void initDots() {
        LinearLayout ll_dots = (LinearLayout) findViewById(R.id.ll_dots);
        dots = new ImageView[viewList.size()];

        for (int i = 0; i < viewList.size(); i++) {
            dots[i] = (ImageView) ll_dots.getChildAt(i);
            dots[i].setEnabled(true);
        }
        currentIndex = 0;
        dots[currentIndex].setEnabled(false);//设为选中状态

    }

    private void setCurrentDot(int position) {
        if (position < 0 || position > viewList.size() - 1
                || currentIndex == position) {
            return;
        }
        dots[position].setEnabled(false);//选中该项
        dots[currentIndex].setEnabled(true);

        currentIndex = position;

    }

    // 当当前页面被滑动时调用
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    // 当新的页面被选中时调用
    @Override
    public void onPageSelected(int position) {
        setCurrentDot(position);
    }

    // 当滑动状态改变时调用
    @Override
    public void onPageScrollStateChanged(int state) {

    }


    public class MyViewPagerAdapter extends PagerAdapter {
        private List<View> mViewList;

        public MyViewPagerAdapter(List<View> viewList) {
            mViewList = viewList;
        }

        @Override
        public int getCount() {
            if (mViewList != null) {
                return mViewList.size();
            }
            return 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            ((ViewPager) container).removeView(mViewList.remove(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ((ViewPager) container).addView(mViewList.get(position), 0);//添加页卡
            mViewList.get(position).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isSelf) {
                        choiseImgUpdate();
                    }
                }
            });
//            if (position == 0) {
//                imgBtn_money = (ImageView) container.findViewById(R.id.imgBtn_money);
//                imgBtn_news = (ImageView) container.findViewById(R.id.imgBtn_news);
//                ll_edit = (LinearLayout) container.findViewById(R.id.ll_edit);
//                imgBtn_money.setOnClickListener(UserHomepageActivity.this);
//                imgBtn_news.setOnClickListener(UserHomepageActivity.this);
//                ll_edit.setOnClickListener(UserHomepageActivity.this);
//                if (isSelf) {
//                    imgBtn_money.setImageDrawable(getResources().getDrawable(R.drawable.icon_hearts_selcector));
//                    ll_edit.setVisibility(View.VISIBLE);
//                } else {
//                    ll_edit.setVisibility(View.GONE);
//                }
//            }
            return mViewList.get(position);

        }

    }

    /**
     * 随机生成分享内容
     *
     * @param
     * @return
     */
    private String getRandomContent() {

        String[] strs = null;
        if (isSelf) {
            strs = getResources().getStringArray(R.array.shareContentForSelf);
        } else {
            strs = getResources().getStringArray(R.array.shareContent);

        }
        Random random = new Random();
        int num = random.nextInt(3);

        return strs[num];
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.imgBtn_back://页面返回
                this.finish();
                break;
            case R.id.imgBtn_set_share://页面分享
                String url_debug = "http://t.zhengzai.tv/pages/per_homepage" +
                        ".html?singerId=" + viewId + "&if_share=1";
                String url_zhengshi = "http://wap.zhengzai.tv/pages/per_homepage.html?singerId=" + viewId +
                        "&if_share=1";

                String url = LogUtils.debug ? url_debug : url_zhengshi;

                String imgUrl = (mUserEntity != null) ? mUserEntity.getFaceUrl() : "";
                String title = "";
                mPopThreeShare = new PopThreeShare(this);
                mPopThreeShare.setShareUrlForAnchor(title, getRandomContent(), url, imgUrl, "");
                mPopThreeShare.showBototomPop();
                break;
            case R.id.imgBtn_money:
                if (isSelf) {
                    startActivity(new Intent(this, OrderMeActivity.class));//消费记录页面
                } else {
                    //关注/取消关注
                    RequestParams params = UrlTool.getPostParams(Constants.USER_ID, UserService.getInatance().getUserBean
                            (this).getId(), Constants
                            .TO_USER_ID, viewId);

                    SendActtionTool.post(Constants.UserParams.URL_ADD_ATTENTION, null, UserAction.ACTION_USER_ATTENTION,
                            this, params);

                    if (mUserEntity.getIsAttention() == 0) {
                        imgBtn_money.setImageResource(R.drawable.icon_hearts_hl);
                    } else {
                        imgBtn_money.setImageResource(R.drawable.icon_hearts_nor);
                    }
                }
                break;
            case R.id.imgBtn_news://站内信
                startActivity(new Intent(this, ZhanneixinActivity.class).putExtra(Constants.USER_ID, viewId));
                break;
            case R.id.ll_edit://编辑入口

                startActivity(new Intent(this, ClienUserActivity.class));

                break;

            default:
                break;
        }

    }

    private void choiseImgUpdate() {
        View eatsView = View.inflate(this, R.layout.complete_choise_img, null);
        eduation_dialog = new Dialog(this, R.style.MmsDialogTheme);
        View.OnClickListener choiseListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.choise_img_phone:
                        PHOTO_FILE_NAME = MediaUtil.getFinalString() + ".jpg";
                        camera();
                        eduation_dialog.dismiss();
                        break;
                    case R.id.choise_img_pic:
                        gallery();
                        eduation_dialog.dismiss();
                        break;
                    case R.id.choise_img_cancle:
                        eduation_dialog.dismiss();
                        break;
                    case R.id.rl_dialog:
                        eduation_dialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        };

        eatsView.findViewById(R.id.choise_img_phone).setOnClickListener(choiseListener);
        eatsView.findViewById(R.id.rl_dialog).setOnClickListener(choiseListener);
        eatsView.findViewById(R.id.choise_img_pic).setOnClickListener(choiseListener);
        eatsView.findViewById(R.id.choise_img_cancle).setOnClickListener(choiseListener);

        eduation_dialog.setContentView(eatsView);
        eduation_dialog.setCanceledOnTouchOutside(true);
        Window dialogWindow = eduation_dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.getDecorView().setPadding(0, 0, 0, 0);

        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = eduation_dialog.getWindow().getAttributes(); // 获取对话框当前的参数值
        p.width = (d.getWidth()); // 宽度设置为屏幕的0.95
        dialogWindow.setAttributes(p);
        eduation_dialog.setCanceledOnTouchOutside(true);
        eduation_dialog.show();
    }

    /*
    * 从相册获取
    */
    public void gallery() {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    /*
     * 从相机获取
     */
    public void camera() {
        Intent intentc = new Intent("android.media.action.IMAGE_CAPTURE");
        // 判断存储卡是否可以用，可用进行存储
        if (hasSdcard()) {
            intentc.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory()
                    , PHOTO_FILE_NAME)));
        }
        startActivityForResult(intentc, PHOTO_REQUEST_CAMERA);
    }

    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && mPopThreeShare != null) {
            mPopThreeShare.setSinaWeibo(requestCode, resultCode, data);
        }
        switch (requestCode) {
            case PHOTO_REQUEST_GALLERY:
                if (data != null) {
                    // 得到图片的全路径
                    Uri uri = data.getData();
                    String path = Utils.getAbsoluteImagePath(this, uri);
                    tempFile = new File(path);
                    crop(uri);
                }
                break;
            case PHOTO_REQUEST_CAMERA:
                if (hasSdcard()) {
                    tempFile = new File(Environment.getExternalStorageDirectory(), PHOTO_FILE_NAME);
                    crop(Uri.fromFile(tempFile));
                } else {
                    Toast.makeText(this, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
                }
                break;
            case PHOTO_REQUEST_CUT:
                try {
                    bitmap = data.getParcelableExtra("data");
                    iv_user.setImageBitmap(bitmap);
                    tempFile = new File(Environment.getExternalStorageDirectory(), PHOTO_FILE_NAME);
                    if (FileUtils.saveBitmap(tempFile, bitmap)) {
                        // uploadImg("File", tempFile, user.getUserId());
                        updateUserIcon(tempFile);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

        }
    }

    /**
     * 更换选中的新头像
     */
    private void updateUserIcon(File file) {
        if (file.length() > 1048576 * 10) {
            Utils.toast(this, "图片过大，请选取10M以下的图片");
            return;
        }
        showLoadingDialog("正在上传图片");
        String uri = file.getAbsolutePath();
        LogUtils.t("uri--uri", uri);
        GeneralTool.uploadFile(uri, new SaveCallback() {
            @Override
            public void onProgress(String arg0, int arg1, int arg2) {
            }

            @Override
            public void onFailure(String arg0, OSSException arg1) {
                arg1.printStackTrace();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        dismissDialog();
                        Utils.toast(UserHomepageActivity.this, "头像上传失败");
                    }
                });
            }

            // 上传成功
            @Override
            public void onSuccess(String arg0) {
                userFaceUrl = Constants.UserParams.USER_URL + arg0;
                LogUtils.d("userFaceUrl==" + userFaceUrl);
                SendActtionTool.post(Constants.UserParams.URL_USER_UPDATE, null, UserAction.Action_Update_Face_Url,
                        UserHomepageActivity.this,
                        UrlTool.getPostParams(Constants.UserParams.KEY, Constants.UserParams.FACE_URL, Constants
                                .UserParams.VALUE, userFaceUrl, Constants.USER_ID, mUserEntity.getId()));
            }
        });
    }

    /**
     * 剪切图片
     *
     * @param uri
     * @function:
     * @author:Jerry
     * @date:2013-12-30
     */
    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        // 图片格式
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);// true:不返回uri，false：返回uri
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    // 引导页
    private void showGuideDialog(int index) {
        String type = PreferencesUtils.TYPE_GUIDE[index];
        Boolean hasGuid = PreferencesUtils.getBooleanPreferences(this, type);
//        hasGuid = false;//dialog   每次都显示dialog 调试用
        if (!hasGuid) {
            DialogTool.createGuideDialog(UserHomepageActivity.this, index, false, new DialogTool.DialogGuideListener() {
                @Override
                public void onGuide(int index) {
                    switch (index) {
                        case 1:
//                            findViewById(R.id.img_me).performClick();
                            break;
                        case 0:
                            if (adapter.getCount()>3) {
                                viewPager_down.setCurrentItem(3);
                            }
                            break;
                        case 2:
                            if (adapter.getCount()>2) {
                                viewPager_down.setCurrentItem(2);
                            }
                            break;
                        default:
                            break;
                    }
                }
            });
            PreferencesUtils.saveBooleanPreferences(this, type, true);
        }
    }

    public class MyReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Constants.ACTION_USERBEAN_CHANGE:
                    mUserEntity = UserService.getInatance().getUserBean(UserHomepageActivity.this);
                    setContent();
                    break;
            }
        }
    }
}

package com.modernsky.istv.acitivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.adapter.AdapterPagerFragment;
import com.modernsky.istv.fragment.DormMusicFragment;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.view.DrageLayout;
import com.modernsky.istv.view.PopChooseCityView;

import java.util.ArrayList;
import java.util.List;

import main.java.cn.aigestudio.datepicker.utils.LogUtil;

/**
 * Created by zqg on 2016/2/22.
 * 宿舍音乐节列表页面
 */
public class DormMusicActivity extends BaseActivity implements ViewPager.OnPageChangeListener {
    ViewPager mViewPager;
    AdapterPagerFragment adapter;
    View leftLineView, rightLineView;
    List<Fragment> fragments;
    Button mTitle;
    ImageView mLeftBackImg, mRightImg;
    private PopChooseCityView popChooseCityView;
    private String mCityId = "";
    private int mSort = 1;
    private DormMusicFragment fragment1;
    private DormMusicFragment fragment2;
    private View topView;
    private ImageView iv_arrow;
    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_pailianshi);
        initDrager();
    }

    @Override
    public void findViewById() {
        topView=findViewById(R.id.topView);
        fragments = new ArrayList<Fragment>();
        mTitle = (Button) findViewById(R.id.tv_title);
        mTitle.setText("宿舍音乐会");
        mLeftBackImg = (ImageView) findViewById(R.id.img_me);
        mLeftBackImg.setImageResource(R.drawable.icon_back);
        mLeftBackImg.setOnClickListener(this);
        mRightImg = (ImageView) findViewById(R.id.img_search);
        mRightImg.setOnClickListener(this);
        findViewById(R.id.text_left_pailianshi).setOnClickListener(this);
        findViewById(R.id.img_down).setOnClickListener(this);
        findViewById(R.id.text_right_pailianshi).setOnClickListener(this);
        leftLineView = findViewById(R.id.line_below_left);
        rightLineView = findViewById(R.id.line_below_right);
        mViewPager = (ViewPager) findViewById(R.id.viewPager_pailianshi);
        mViewPager.addOnPageChangeListener(this);
        adapter = new AdapterPagerFragment(getSupportFragmentManager());
        initFragmentList();
        mViewPager.setAdapter(adapter);
        iv_arrow= (ImageView) findViewById(R.id.img_down);
        popChooseCityView = new PopChooseCityView(this);
        popChooseCityView.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                iv_arrow.setImageResource(R.drawable.icon_arrow_down);
            }
        });
        popChooseCityView.setmCityListener(new PopChooseCityView.ChooseCityListener() {
            @Override
            public void onCityChoose(String city) {
                mCityId = city;
                updateFragement();
            }

            @Override
            public void onSortChoose(int sort) {
                mSort = sort;
                updateFragement();
            }
        });
        initTitle(0);
    }

    public int getIndex() {
        return mViewPager.getCurrentItem();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateFragement();
    }


    private void updateFragement() {
        if (mViewPager.getCurrentItem() == 0) {
            fragment1.getContent(mCityId, mSort, true);
        } else {
            fragment2.getContent(mCityId, mSort, true);
        }
    }

    private void initFragmentList() {
        fragment1 = new DormMusicFragment();
        fragment2 = new DormMusicFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putInt(Constants.INDEX, 0);
        Bundle bundle2 = new Bundle();
        bundle2.putInt(Constants.INDEX, 1);
        fragment1.setArguments(bundle1);
        fragment2.setArguments(bundle2);
        fragments.add(fragment1);
        fragments.add(fragment2);
        adapter.setFragments(fragments);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_left_pailianshi:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.text_right_pailianshi:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.img_me:
                this.finish();
                break;
            case R.id.img_search:
                startActivity(new Intent(this, SearchActivity.class));
                break;
            case R.id.img_down:
                LogUtils.d("img_down--------");
//                    popChooseCityView.showPopLogin(v);
                    popChooseCityView.showPopLogin(topView);
                    ((ImageView)v).setImageResource(R.drawable.icon_arrow_up);
                break;
            default:
                break;
        }
    }

        @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        initTitle(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void initTitle(int num) {
        LogUtil.d("initTitle__" + num);
        switch (num) {
            case 0:
                leftLineView.setVisibility(View.VISIBLE);
                rightLineView.setVisibility(View.GONE);
                break;
            case 1:
                leftLineView.setVisibility(View.GONE);
                rightLineView.setVisibility(View.VISIBLE);
                break;
        }
        if (popChooseCityView != null)
            popChooseCityView.clearFlag();
//        if (!TextUtils.isEmpty(mCityId) || mSort != 1) {
//
//        }
        mCityId = "";
        mSort = 1;
        updateFragement();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;
        switch (requestCode) {
            case 100:
                String cityName = data.getStringExtra("cityName");
                String cityId = data.getStringExtra("cityId");
                LogUtils.d(cityName + "------" + cityId);
                mCityId = cityId;
                if (popChooseCityView != null) {
                    popChooseCityView.setmCityText(cityName);
                }
//                updateFragement();
                break;
        }
    }

    public String getmCityId() {
        return mCityId;
    }

    public int getmSort() {
        return mSort;
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


}

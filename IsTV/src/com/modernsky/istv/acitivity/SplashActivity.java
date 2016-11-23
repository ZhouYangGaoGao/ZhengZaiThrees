package com.modernsky.istv.acitivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.PreferencesUtils;

import java.util.ArrayList;

/**
 * Created by xqp on 2015/6/29.
 */
public class SplashActivity extends BaseActivity {
    private ViewPager viewPager = null;
    private ArrayList<View> viewContainter = null;
    private int mPosition;
    private int selectedPos;


    @Override
    public void onClick(View v) {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    @Override
    public void setContentView(Bundle savedInstanceState) {

        setContentView(R.layout.activity_splash);
        // num 应用启动次数
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        int num = PreferencesUtils.getIntPreferences(getApplicationContext(),
                PreferencesUtils.TYPE_HUOQU_ACTIVITY);
        if (num > 1) {
            startActivity(new Intent(this, LogoActivity.class));
            overridePendingTransition(R.anim.enter, R.anim.exit);
            finish();
        }

    }

    @Override
    public void findViewById() {
        RelativeLayout rl_rootview = (RelativeLayout) findViewById(R.id.rl_rootview);
        rl_rootview.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        viewContainter = new ArrayList<View>();
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        ImageView imageView1 = new ImageView(this);
        ImageView imageView2 = new ImageView(this);
        ImageView imageView3 = new ImageView(this);
        ImageView imageView4 = new ImageView(this);
        imageView1.setImageResource(R.drawable.welcome_1);
        imageView2.setImageResource(R.drawable.welcome_2);
        imageView3.setImageResource(R.drawable.welcome_3);
        imageView4.setImageResource(R.drawable.welcome_4);
        viewContainter.add(imageView1);
        viewContainter.add(imageView2);
        viewContainter.add(imageView3);
        viewContainter.add(imageView4);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mPosition = position;
            }

            @Override
            public void onPageSelected(int position) {
                LogUtils.t("onPageSelected ", "" + position);
                selectedPos = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                LogUtils.t("onPageScrollStateChanged ", "" + state);
                if (state == 0 && mPosition == 3 && selectedPos == 3) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            }
        });


        viewPager.setAdapter(new PagerAdapter() {

            //viewpager中的组件数量
            @Override
            public int getCount() {
                return viewContainter.size();
            }

            //滑动切换的时候销毁当前的组件
            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                ((ViewPager) container).removeView(viewContainter.get(position));
            }

            //每次滑动的时候生成的组件
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ((ViewPager) container).addView(viewContainter.get(position));
                return viewContainter.get(position);
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getItemPosition(Object object) {
                return super.getItemPosition(object);
            }

        });


    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 按下的如果是BACK，同时没有重复
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            return true;
        }
        return false;
    }
}
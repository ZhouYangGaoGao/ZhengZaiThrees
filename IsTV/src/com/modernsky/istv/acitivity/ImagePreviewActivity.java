package com.modernsky.istv.acitivity;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lidroid.xutils.BitmapUtils;
import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.tool.BitmapTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.view.ZoomImageView;

public class ImagePreviewActivity extends BaseActivity {

    private ViewPager mViewPager;
    private ImageView[] mImageViews;
    private int intExtra;
    private ArrayList<String> strList = new ArrayList<String>();
    private BitmapUtils bitmapUtils;

    @Override
    public void onClick(View v) {

    }

    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_image_preview);
        intExtra = getIntent().getIntExtra(Constants.POSOTION, 0);
        strList = getIntent().getStringArrayListExtra(Constants.DATA);
        bitmapUtils = BitmapTool.getInstance().initAdapterUitl(this);
        mImageViews = new ImageView[strList.size()];
        for (int i = 0; i < mImageViews.length; i++) {
            ZoomImageView imageView = new ZoomImageView(getApplicationContext());
            mImageViews[i] = imageView;
            bitmapUtils.display(imageView, strList.get(i));
        }
    }

    @Override
    public void findViewById() {
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);

        mViewPager.setAdapter(new PagerAdapter() {

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(mImageViews[position], 0);
                return mImageViews[position];
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                container.removeView(mImageViews[position]);
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                return strList.size();
            }
        });
        mViewPager.setCurrentItem(intExtra);

    }
}

package com.modernsky.istv.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.widget.TextView;

import com.modernsky.istv.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mufaith
 */
public class HomePageViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments = new ArrayList<>();
    private List<String> titles = new ArrayList<>();
    private Context context;

    private TextView tv_guanzhu;
    private TextView tv_fans;
    private TextView tv_works;


    public HomePageViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public void addFragment(Fragment f, String title) {
        fragments.add(f);
        titles.add(title);
    }

    public void addFragment(Fragment f) {
        fragments.add(f);
    }

    public void addFragments(List<Fragment> f) {
        fragments.addAll(f);
    }

    public void setFragments(List<Fragment> fragments) {

        this.fragments = fragments;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }


    /**
     * @param titles:tab文字,
     * @param nums:         tab 数字
     * @param typeId        : 新关注或者新粉丝信息代号
     * @return
     */


    public View getTabView(int position, List<String> titles, List<String> nums, String typeId, boolean isSelf,
                           boolean isAnchor) {

        View view = View.inflate(context, R.layout.item_tabview, null);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        TextView tv_num = (TextView) view.findViewById(R.id.tv_num);
        tv_title.setText(titles.get(position));
        if (position > 2) {
            tv_num.setVisibility(View.GONE);
        }
        if (position <= 2) {
            switch (position) {
                case 0:
                    tv_guanzhu = tv_num;
                    tv_title.setTextColor(Color.parseColor("#ffffff"));
                    tv_num.setTextColor(Color.parseColor("#ffffff"));
                    break;
                case 1:
                    tv_fans = tv_num;
                    break;
                case 2:
                    if (isAnchor) {
                        tv_works = tv_num;
                        tv_num.setVisibility(View.VISIBLE);
                    }else{
                        tv_num.setVisibility(View.GONE);
                    }
                    break;
            }

            tv_num.setText(nums.get(position));
//            switch (type) {
//                case 0:
//                    dot_tip.setVisibility(View.GONE);
//                    break;
//                case 1:
//                    if (position == 0) {
//                        dot_tip.setVisibility(View.VISIBLE);
//                    }
//                    break;
//                case 2:
//                    if (position == 1) {
//                        dot_tip.setVisibility(View.VISIBLE);
//                    }
//                    break;
//            }
        }
        return view;
    }

    public TextView getTextView() {
        return tv_guanzhu;
    }

    public TextView getTextView2() {
        return tv_fans;
    }

    public TextView getTextView3() {
        return tv_works;
    }
}

package com.modernsky.istv.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class AdapterPagerFragment extends FragmentPagerAdapter {
    private List<String> titles = new ArrayList<String>();

    public void setFragments(List<Fragment> fragments) {
        this.fragments = fragments;
    }

    private List<Fragment> fragments = new ArrayList<Fragment>();

    public AdapterPagerFragment(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }


    public void addFragment(Fragment f) {
        fragments.add(f);
    }

    public void addFragments(List<Fragment> f) {
        fragments.addAll(f);
    }

    public void addFragment(Fragment f, String title) {
        fragments.add(f);
        titles.add(title);
    }

}

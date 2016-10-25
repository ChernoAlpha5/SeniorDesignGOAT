package com.example.andrew.tabtest;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Andrew on 10/25/2016.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {
    ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    ArrayList<String> tabTitles = new ArrayList<String>();

    public void addFragments(Fragment fragment, String title){
        this.fragments.add(fragment);
        this.tabTitles.add(title);
    }
    public ViewPagerAdapter(FragmentManager fm){
        super(fm);

    }
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public CharSequence getPageTitle(int position){
        return tabTitles.get(position);
    }
}

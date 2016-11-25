package com.adafruit.bluefruit.le.connect.app.graphData;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewGroupCompat;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Andrew Wong on 10/25/2016.
 */
// code for refreshing fragment found from:
// http://semycolon.blogspot.in/2014/12/refresh-pageviewer-fragment-everytime.html

public class ViewPagerAdapter extends FragmentPagerAdapter {
    ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    ArrayList<String> tabTitles = new ArrayList<String>();
    private Map<Integer,String> mFragmentTags = new HashMap<Integer,String>(); //define a hash map to store the Tags of the Fragments
    private FragmentManager mFragmentManage;
    private Context mContext;

/*    public ViewPagerAdapter(FragmentManager fm, Context context){
        super(fm);
        mFragmentManage = fm;
        mContext = context;
    }*/

    public void addFragments(Fragment fragment, String title){
        this.fragments.add(fragment);
        this.tabTitles.add(title);
    }
    public ViewPagerAdapter(FragmentManager fm){
        super(fm);
        mFragmentManage = fm;

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
    //override the instantiateItem method to save the tag of the Fragment in the hashmap along with the position.
    public Object instantiateItem(ViewGroup container, int position) {
        Object obj = super.instantiateItem(container, position);
        if (obj instanceof Fragment) {
            // record the fragment tag here.
            Fragment f = (Fragment) obj;
            String tag = f.getTag();
            mFragmentTags.put(position, tag);
        }
        return obj;
    }

   //method returns tag of a previously created Fragment based on the position
    public Fragment getFragment(int position) {
        String tag = mFragmentTags.get(position);
        if (tag == null)
            return null;

        return mFragmentManage.findFragmentByTag(tag);
    }

    public CharSequence getPageTitle(int position){
        return tabTitles.get(position);
    }
}

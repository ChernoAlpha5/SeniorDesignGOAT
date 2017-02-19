package andrew.bluno2.Graph;

/**
 * Created by Andrew on 2/18/2017.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

/* A {@link FragmentPagerAdapter} that returns a fragment corresponding to
        * one of the sections/tabs/pages.
        */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    //private static WeakReference<Activity> mActivityRef;
    private RespirationFrag respFrag ;
    private RespirationFrag hydrFrag; //TODO: CREATE A HYDRATION FRAGMENT
    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // Do NOT try to save references to the Fragments in getItem(),
        // because getItem() is not always called. If the Fragment
        // was already created then it will be retrieved from the FragmentManger
        // and not here (i.e. getItem() won't be called again).

        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        //return PlaceholderFragment.newInstance(position + 1);
        //return RespirationFrag.newInstance(position + 1);
        switch (position) {
            case 0:
                return new RespirationFrag();  //create new respiration fragment
            case 1:
                return new RespirationFrag();   //TODO: CREATE A HYDRATION FRAGMENT
            default:
                // This should never happen. Always account for each position above
                return null;
        }
    }
    // Here we can finally safely save a reference to the created
    // Fragment, no matter where it came from (either getItem() or
    // FragmentManger). Simply save the returned Fragment from
    // super.instantiateItem() into an appropriate reference depending
    // on the ViewPager position.
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
        // save the appropriate reference depending on position
        switch (position) {
            case 0:
                respFrag = (RespirationFrag) createdFragment;
                break;
            case 1:
                hydrFrag = (RespirationFrag) createdFragment;
                break;
        }
        return createdFragment;
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "SECTION 1";
            case 1:
                return "SECTION 2";
                /*case 2:
                    return "SECTION 3";*/
        }
        return null;
    }

}
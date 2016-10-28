package my.hackathon.jalanrayaku.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import my.hackathon.jalanrayaku.fragment.TabActivity;
import my.hackathon.jalanrayaku.fragment.TabMap;
import my.hackathon.jalanrayaku.fragment.TabStatus;

/**
 * Created by MuhammadIqbal on 25/10/2016.
 */

public class MainPageAdapter extends FragmentPagerAdapter {

    private int mNumOfTabs;

    public MainPageAdapter(FragmentManager fm, int numTabs) {
        super(fm);
        mNumOfTabs = numTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TabStatus();
            case 1:
                return new TabMap();
            case 2:
                return new TabActivity();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}

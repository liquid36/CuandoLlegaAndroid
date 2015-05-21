package com.samsoft.cuandollega.objects;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.samsoft.cuandollega.Fragments.calleList;
import com.samsoft.cuandollega.Fragments.controlerSelector;
import com.samsoft.cuandollega.Fragments.favoriteList;

/**
 * Created by sam on 19/05/15.
 */

public class MainTabAdapter extends FragmentPagerAdapter {

    public MainTabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Top Rated fragment activity
                return new favoriteList();
            case 1:
                // Games fragment activity
                return new controlerSelector();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 2;
    }

}

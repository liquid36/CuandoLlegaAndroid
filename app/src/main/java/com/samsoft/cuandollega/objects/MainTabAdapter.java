package com.samsoft.cuandollega.objects;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.samsoft.cuandollega.Fragments.calleList;
import com.samsoft.cuandollega.Fragments.controlerSelector;
import com.samsoft.cuandollega.Fragments.favoriteList;
import com.samsoft.cuandollega.Fragments.mapControler;

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
                return new controlerSelector();
            case 1:
                return new favoriteList();
            case 2:
                return new mapControler();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

}

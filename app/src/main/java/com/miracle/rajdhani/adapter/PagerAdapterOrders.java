package com.miracle.rajdhani.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.miracle.rajdhani.fragments.PastOrdersFragment;
import com.miracle.rajdhani.fragments.UpcomingOrdersFragment;


public class PagerAdapterOrders extends FragmentStatePagerAdapter {
    int totalTabs;

    public PagerAdapterOrders(FragmentManager fm, int tabCount) {
        super(fm);
        this.totalTabs = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PastOrdersFragment();

            case 1:
                return new UpcomingOrdersFragment();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}

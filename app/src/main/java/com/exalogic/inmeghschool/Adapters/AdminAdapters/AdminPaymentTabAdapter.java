package com.exalogic.inmeghschool.Adapters.AdminAdapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.exalogic.inmeghschool.Fragment.AdminFeeHistoryFragment;
import com.exalogic.inmeghschool.Fragment.DefaulterFragment;

/**
 * Created by Exalogic on 21-Jul-17.
 */

public class AdminPaymentTabAdapter extends FragmentPagerAdapter {
    private final int tabCount;

    public AdminPaymentTabAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new DefaulterFragment();
            case 1:
                return new AdminFeeHistoryFragment();

        }
        return null;
    }

    @Override
    public int getCount() {
        return this.tabCount;
    }
}



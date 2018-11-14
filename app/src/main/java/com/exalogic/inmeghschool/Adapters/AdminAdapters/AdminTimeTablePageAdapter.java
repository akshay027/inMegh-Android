package com.exalogic.inmeghschool.Adapters.AdminAdapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.exalogic.inmeghschool.Fragment.AdminClassTimeTableFragment;
import com.exalogic.inmeghschool.Fragment.AdminTeacherTimeTableFragment;

/**
 * Created by Exalogic on 10/4/2016.
 */
public class AdminTimeTablePageAdapter extends FragmentPagerAdapter {
    private final int tabCount;

    public AdminTimeTablePageAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AdminClassTimeTableFragment();
            case 1:
                return new AdminTeacherTimeTableFragment();

        }
        return null;
    }

    @Override
    public int getCount() {
        return this.tabCount;
    }
}



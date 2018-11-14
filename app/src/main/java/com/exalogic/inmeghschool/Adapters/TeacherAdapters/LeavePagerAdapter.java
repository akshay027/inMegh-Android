package com.exalogic.inmeghschool.Adapters.TeacherAdapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.exalogic.inmeghschool.Fragment.MyLeavesFragment;
import com.exalogic.inmeghschool.Fragment.StudentLeavesFragment;

/**
 * Created by Nikhil on 18-06-2016..
 */
public class LeavePagerAdapter extends FragmentPagerAdapter {

    private final int tabCount;

    public LeavePagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MyLeavesFragment();
            case 1:
                return new StudentLeavesFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return this.tabCount;
    }
}

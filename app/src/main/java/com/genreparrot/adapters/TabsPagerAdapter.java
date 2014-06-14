package com.genreparrot.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.genreparrot.fragments.MediaFragment;
import com.genreparrot.fragments.ScheduleFragment;
import com.genreparrot.fragments.StartFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter {
    private Context context;

    public TabsPagerAdapter(FragmentManager fm, Context _context) {
        super(fm);
        this.context = _context;
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                return new StartFragment();
            case 1:
                return new MediaFragment();
            case 2:
                return new ScheduleFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }

}
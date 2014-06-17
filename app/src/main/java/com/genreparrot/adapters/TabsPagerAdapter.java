package com.genreparrot.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.genreparrot.fragments.MediaFragment;
import com.genreparrot.fragments.ScheduleFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter {
    private Context context;

    Fragment media = new MediaFragment();
    Fragment schedule = new ScheduleFragment();

    public TabsPagerAdapter(FragmentManager fm, Context _context) {
        super(fm);
        this.context = _context;
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                return media;
            case 1:
                return schedule;
        }
        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 2;
    }

}
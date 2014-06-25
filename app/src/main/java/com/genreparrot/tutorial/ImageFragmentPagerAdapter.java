package com.genreparrot.tutorial;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.genreparrot.tutorial.ImageViewFragment;

import java.util.ArrayList;

public class ImageFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Integer> itemData;

    public ImageFragmentPagerAdapter(FragmentManager fm,
                                     ArrayList<Integer> itemData) {
        super(fm);
        this.itemData = itemData;
    }
    @Override
    public int getCount() {
        return itemData.size();
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
    @Override
    public Fragment getItem(int position) {
        ImageViewFragment f = ImageViewFragment.newInstance();
        f.setImageList(itemData.get(position));
        return f;
    }
}
package com.genreparrot.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.genreparrot.adapters.AssetsAdapter;
import com.genreparrot.adapters.StoreGridAdapter;
import com.genreparrot.app.R;

import java.util.ArrayList;

public class MediaFragment extends Fragment
{
    // holds pointer to the root layout element of this fragment
    private View root;

    public MediaFragment(){

    }

    @Override
    public void onResume(){
        ArrayList<String> arr = new ArrayList<String>();
        Log.d("debug", "Media onResume");
        arr = new AssetsAdapter().getAssetsList(getActivity().getBaseContext(), "packages");

        StoreGridAdapter ad = new StoreGridAdapter(getActivity(), arr);
        GridView grid = (GridView) root.findViewById(R.id.gridStore);

        grid.setAdapter(ad);
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        root = inflater.inflate(R.layout.fragment_media, container, false);
        return root;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }
}
package com.genreparrot.fragments;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.genreparrot.adapters.MediaPlayerAdapter;
import com.genreparrot.app.R;

public class StartFragment extends Fragment
{


    public StartFragment(){

    }

    private int mInterval = 10000; // 5 seconds by default, can be changed later
    private Handler mHandler;

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            updateStatus(); //this function can change value of mInterval.
            mHandler.postDelayed(mStatusChecker, mInterval);
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    public void updateStatus(){
        Button btnStart = (Button) getView().findViewById(R.id.btnStartTraining);
        Button btnCancel = (Button) getView().findViewById(R.id.btnCancelAllSchedules);

        boolean alarmUp = (PendingIntent.getBroadcast(getActivity(), (int) MediaPlayerAdapter.REPEAT_EVERYDAY_BROADCAST_ID,
                new Intent(getActivity(), MediaPlayerAdapter.class),
                PendingIntent.FLAG_NO_CREATE) != null);

        MediaPlayerAdapter mpa = MediaPlayerAdapter.getInstance();

        // The schedule is at work either if the EverydayAlarm is up or if MediaPlayerAdapter
        // has some items on its list.
        if (alarmUp || mpa.list.size()>0) {
            Log.d("debug","mpa.list.size(): "+ mpa.list.size());
            Log.d("debug","alarmUp:"+ alarmUp);
            btnCancel.setVisibility(View.VISIBLE);
            btnStart.setVisibility(View.GONE);
            Log.d("debug", "schedules are at work");
        }
        else {
            btnCancel.setVisibility(View.GONE);
            btnStart.setVisibility(View.VISIBLE);
            Log.d("debug", "schedules are idle");
        }
    }
    public void onActivityCreated (Bundle savedInstanceState)
    {
        mHandler = new Handler();

        startRepeatingTask();

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_start, container, false);
    }

    @Override
    public void onStop(){
        stopRepeatingTask();
        super.onStop();
    }
}
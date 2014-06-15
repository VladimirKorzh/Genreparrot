package com.genreparrot.app;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.genreparrot.adapters.MediaPlayerAdapter;
import com.genreparrot.adapters.TabsPagerAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import util.IabHelper;
import util.IabResult;

public class MainActivity extends FragmentActivity implements
        ActionBar.TabListener {

    private ViewPager viewPager;
    private ActionBar actionBar;
    IabHelper mHelper;

    static final String ACTIVE_TAB = "activeTab";

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putInt(ACTIVE_TAB, viewPager.getCurrentItem());

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        actionBar.setSelectedNavigationItem(savedInstanceState.getInt(ACTIVE_TAB));
        viewPager.setCurrentItem(savedInstanceState.getInt(ACTIVE_TAB));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





//        // Initiate In-App billing TODO Secute Public Key
/*      To keep your public key safe from malicious users and hackers,
        do not embed it in any code as a literal string. Instead, construct the string
        at runtime from pieces or use bit manipulation (for example, XOR with some other string)
        to hide the actual key. The key itself is not secret information, but you
        do not want to make it easy for a hacker or malicious user to replace the public key with another key.*/

        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiZ9RhHkwfm9K851Q86pWuWfdBoorVQkI+DCdSquJB6uMt+FzeDgD3fdCzjAvzRzP9NW9zlVgoh7CZhzgv+9EYaRHfaOkX1dBRBvGOyo3wu6q1r56s9yLoDLT2GeAJuHXVFtOQqsPOK0ME8Af5UWtYhf3YIkBLmOX4eFDCGuwTKNRJ5IarAwDVs+ZPhgZeDyTBGF+xEStqYg/htQKbYh924LRYmMRmIOVt9Jw/j5nPKqGVFpS7qD/fZOzl3FeD+wp0D7XBC/zsBR/ex9CxaWrtJ/xMb9ktQyP1cc7Pzob7TnjVa/ggKSJsOdjXsCwe4gbssJ/Pr4TM2pWp+eMO33RDwIDAQAB";
        // compute your public key and store it in base64EncodedPublicKey
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.d("debug", "Problem setting up In-app Billing: " + result);
                }
                else {
                    Log.d("debug", " Hooray, IAB is fully set up!");
                }
            }
        });

        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayShowHomeEnabled(false);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        TabsPagerAdapter mAdapter = new TabsPagerAdapter(getSupportFragmentManager(), this);

        viewPager.setAdapter(mAdapter);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        String[] tabs = {getString(R.string.ScheduleTab),
                         getString(R.string.LibraryTab)};

        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                                               .setTabListener(this));
        }

        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        // Allow user to select files from mediastore
        //Intent pickAudioIntent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.INTERNAL_CONTENT_URI);
        //startActivityForResult(pickAudioIntent, 0);



    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }


    public void btnCreateNewScheduleClick(View v){
        Intent intent = new Intent(this, CreateEditSchedule.class);
        Bundle b = new Bundle();
        b.putInt("scheduleID", -1); //Your id
        intent.putExtras(b); //Put your id to your next Intent
        startActivity(intent);
        overridePendingTransition( R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }


    public void btnStartTrainingClick(View v){

        final CharSequence[] items = {getString(R.string.StartModeRepeatEveryday), getString(R.string.StartModeTodayOnly)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.StartModeDialogTitle));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();

                // get the alarm manager reference
                AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                // create the intent for the scheduled task
                Intent intent = new Intent(getBaseContext(), MediaPlayerAdapter.class);

                Time t = new Time();
                t.setToNow();
                t.normalize(true);
                t.hour = 0;
                t.minute = 0;
                t.second = 1;

                if (item == 0) {
                    // set the alarm to be repeated every day at 0:0:1
                    PendingIntent pi = PendingIntent.getBroadcast(getBaseContext(), (int) MediaPlayerAdapter.REPEAT_EVERYDAY_BROADCAST_ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    am.setRepeating(AlarmManager.RTC_WAKEUP, t.toMillis(true), TimeUnit.DAYS.toMillis(1), pi);
                    MediaPlayerAdapter mpa = MediaPlayerAdapter.getInstance();
                    mpa.list.add(MediaPlayerAdapter.REPEAT_EVERYDAY_BROADCAST_ID);
                }
                if (item == 1) {
                    // fire alarm only once for today
                    PendingIntent pi = PendingIntent.getBroadcast(getBaseContext(), (int) MediaPlayerAdapter.REPEAT_EVERYDAY_BROADCAST_ID, intent, PendingIntent.FLAG_ONE_SHOT);
                    am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi);
                }

//                // Update UI
//                Button btnStart = (Button) findViewById(R.id.btnStartTraining);
//                Button btnCancel = (Button) findViewById(R.id.btnCancelAllSchedules);
//                btnCancel.setVisibility(View.VISIBLE);
//                btnStart.setVisibility(View.GONE);
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void btnCancelAllSchedulesClick(View v){
        MediaPlayerAdapter mpa = MediaPlayerAdapter.getInstance();
        mpa.CancelAllSchedules(getBaseContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Releases Google In App Billing references
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }



}
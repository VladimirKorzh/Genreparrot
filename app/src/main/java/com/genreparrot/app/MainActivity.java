package com.genreparrot.app;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.genreparrot.adapters.AppData;
import com.genreparrot.adapters.TabsPagerAdapter;
import com.genreparrot.fragments.MediaFragment;

import util.IabHelper;
import util.IabResult;
import util.Inventory;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {
    static final String TAG = "MainActivity";

    private ViewPager viewPager;
    private android.support.v7.app.ActionBar actionBar;
    public TabsPagerAdapter tabsPagerAdapter;

    AppData appData = AppData.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        AppData.myLog(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (appData.iabHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!appData.iabHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            AppData.myLog(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    // We're being destroyed. It's important to dispose of the helper here!
    @Override
    public void onDestroy() {
        super.onDestroy();

        // very important:
        AppData.myLog(TAG, "Destroying helper.");
        if (appData.iabHelper != null) {
            appData.iabHelper.dispose();
            appData.iabHelper = null;
        }
    }


    @Override
    public void onTabSelected(Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

    }


    // Display a simple message to the user
    public void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        AppData.myLog(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }


    public void btnCreateNewScheduleClick(View v){
        Intent intent = new Intent(this, CreateEditScheduleActivity.class);
        Bundle b = new Bundle();
        b.putInt("scheduleID", -1);
        intent.putExtras(b);
        startActivity(intent);
        overridePendingTransition( R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    public void setupUI(){
        // Set default volume control buttons reaction
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);

        actionBar = getSupportActionBar();
        // Disable unnecessary elements
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);


        // Create tabs adapter that will handle our fragments
        tabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager(), this);

        // set everything up
        viewPager.setAdapter(tabsPagerAdapter);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set the amount of pages to hold in memory
        viewPager.setOffscreenPageLimit(2);

        // Adding Tabs
        String[] tabs = {getString(R.string.LibraryTab), getString(R.string.ScheduleTab)};

        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }

        // swiping the viewpager make respective tab selected
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

        // Query owned packages
        AppData.getInstance().iabHelper.queryInventoryAsync(queryOwnedPackagesListener);

    }





//
//    // Listener that's called when we finish querying the items and subscriptions we own
//    IabHelper.QueryInventoryFinishedListener queryAvailablePackagesListener = new IabHelper.QueryInventoryFinishedListener() {
//        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
//            AppData.myLog(TAG, "Query store inventory finished.");
//
//            // Have we been disposed of in the meantime? If so, quit.
//            if (iabHelper == null) return;
//
//            // Is it a failure?
//            if (result.isFailure()) {
//                alert(getString(R.string.msgErrFailedQueryInventory));
//                return;
//            }
//            else {
//                AppData.myLog(TAG, "Query store inventory was successful.");
//            }
//
//            AppData.myLog(TAG, "Now initiating owned items query");
//            iabHelper.queryInventoryAsync(queryOwnedPackagesListener);
//        }
//    };

    IabHelper.QueryInventoryFinishedListener queryOwnedPackagesListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {

            if (result.isFailure()) {
                alert(getString(R.string.msgErrOwnedQueryFailed));
            }
            else {
                AppData.myLog(TAG, "Owned items query successful.");

                for (String sku : AppData.getInstance().packages_loaded.keySet()){
                    if (inventory.hasPurchase(sku)){
                        AppData.getInstance().packages_loaded.get(sku).owned = true;
                        AppData.myLog(TAG, "User owns: " + sku);
                    }
                    else{
                        AppData.myLog(TAG, "User doesn't own "+ sku);
                    }
                }
                // reload the list using latest values
                ((MediaFragment) tabsPagerAdapter.getItem(0)).RefreshGrid();
            }
        }
    };












}
package com.genreparrot.app;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.genreparrot.adapters.AssetsHelper;
import com.genreparrot.adapters.LoadingDialog;
import com.genreparrot.adapters.MediaPlayerAdapter;
import com.genreparrot.adapters.SoundPackage;
import com.genreparrot.adapters.TabsPagerAdapter;
import com.genreparrot.fragments.ScheduleFragment;

import util.IabHelper;
import util.IabResult;
import util.Inventory;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    // TAG is used in debug output
    static final String TAG = "MainActivity";

    // Global references to action bar and viewPager
    private ViewPager viewPager;
    private ActionBar actionBar;
    public TabsPagerAdapter mAdapter;

    LoadingDialog l;
    AssetsHelper a;

    // The helper object
    public IabHelper mHelper;


    public IabHelper getIabHelper(){
        return mHelper;
    }

    public void initiateAppStore() {
        // Applications public key
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiZ9RhHkwfm9K851Q86pWuWfdBoorVQkI+DCdSquJB6uMt+FzeDgD3fdCzjAvzRzP9NW9zlVgoh7CZhzgv+9EYaRHfaOkX1dBRBvGOyo3wu6q1r56s9yLoDLT2GeAJuHXVFtOQqsPOK0ME8Af5UWtYhf3YIkBLmOX4eFDCGuwTKNRJ5IarAwDVs+ZPhgZeDyTBGF+xEStqYg/htQKbYh924LRYmMRmIOVt9Jw/j5nPKqGVFpS7qD/fZOzl3FeD+wp0D7XBC/zsBR/ex9CxaWrtJ/xMb9ktQyP1cc7Pzob7TnjVa/ggKSJsOdjXsCwe4gbssJ/Pr4TM2pWp+eMO33RDwIDAQAB";

        // Create the helper, passing it our context and the public key to verify signatures with
        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should TODO set this to false).
        mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    alert(getString(R.string.msgErrBillingSetup) + result);
                    LoadingDialog.loading.hide();
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful.");
                l.changeMsg(getString(R.string.msgFetchingAvailablePackages));
                mHelper.queryInventoryAsync(false, AssetsHelper.getInstance().packages_found, queryAvailablePackagesListener);

            }
        });
    }


    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener queryAvailablePackagesListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query store inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                alert(getString(R.string.msgErrFailedQueryInventory));
                LoadingDialog.loading.hide();
                return;
            }
            else {
                Log.d(TAG, "Query store inventory was successful.");
            }

            Log.d(TAG, "Now initiating owned items query");
            l.changeMsg(getString(R.string.msgGettingListOfOwnedProducts));
            mHelper.queryInventoryAsync(queryOwnedPackagesListener);
        }
    };

    IabHelper.QueryInventoryFinishedListener queryOwnedPackagesListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {

            if (result.isFailure()) {
                alert(getString(R.string.msgErrOwnedQueryFailed));
                LoadingDialog.loading.hide();
            }
            else {
                Log.d(TAG, "Owned items query successful.");
                for (String sku : AssetsHelper.getInstance().packages_found){
                    if (inventory.hasPurchase(sku)){
                        AssetsHelper.getInstance().packages_loaded.get(sku).owned = true;
                        Log.d(TAG, "User owns: "+sku);
                    }
                }
                LoadingDialog.loading.hide();
            }
        }
    };




    private class LoadApplicationFlow extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            publishProgress(getString(R.string.msgLoadingPackages));
            a = new AssetsHelper(getApplication());


            // Get the list of packages in the current distribution
            a.packages_found = AssetsHelper.getSoundPackagesNames(getApplication());

            // Load all the packages that are found
            for (String pkg : a.packages_found){
                SoundPackage sp = new SoundPackage(getApplication(), AssetsHelper.getPkg_path(pkg));
                publishProgress(getString(R.string.msgJustLoadedPackage)+ sp.props.getProperty("caption"));
                a.packages_loaded.put(pkg, sp);
                Log.d(TAG, "Package loaded: "+ pkg);
            }

            // Load google In-App Store library and connect
            publishProgress(getString(R.string.msgInitiatingAppStore));
            initiateAppStore();

            return "Success";
        }

        @Override
        protected void onPostExecute(String res) {
            setupUI();
            // make sure we start the flow every run.
            Intent intent = new Intent(getApplication(), MediaPlayerAdapter.class);
            boolean alarmUp = (PendingIntent.getBroadcast(getApplication(), (int) MediaPlayerAdapter.REPEAT_EVERYDAY_BROADCAST_ID,
                    intent, PendingIntent.FLAG_NO_CREATE) != null);

            if (alarmUp)
            {
                Log.d(TAG, "Alarm is already active");
            }
            else {
                Log.d(TAG,"Alarm wasn't set. Setting it now.");
                ((ScheduleFragment) mAdapter.getItem(1)).ad.actionStartTraining();
            }
        }

        @Override
        protected void onPreExecute() {
            l.changeMsg("Initializing...");
        }

        @Override
        protected void onProgressUpdate(String... values) {
            runOnUiThread(l.changeMsg(values[0]));
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // creating loading dialog
        l = new LoadingDialog(this);
        new LoadApplicationFlow().execute("");
    }



    // TODO Figure out if this is needed
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    // We're being destroyed. It's important to dispose of the helper here!
    @Override
    public void onDestroy() {
        super.onDestroy();

        // very important:
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
        l.loading.dismiss();
    }


    public void setupUI(){
        // Set default volume control buttons reaction
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);

        actionBar = getActionBar();
        // Disable unnecessary elements
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);


        // Create tabs adapter that will handle our fragments
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager(), this);

        // set everything up
        viewPager.setAdapter(mAdapter);
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
    }



    // Display a simple message to the user
    public void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
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
        b.putInt("scheduleID", -1);
        intent.putExtras(b);
        startActivity(intent);
        overridePendingTransition( R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }
}
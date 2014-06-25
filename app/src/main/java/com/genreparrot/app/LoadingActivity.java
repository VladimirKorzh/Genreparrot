package com.genreparrot.app;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.genreparrot.adapters.AppData;
import com.genreparrot.adapters.LoadingDialog;
import com.genreparrot.adapters.MediaPlayerAdapter;
import com.genreparrot.adapters.SoundPackage;
import com.genreparrot.database.ScheduleDAO;
import com.genreparrot.tutorial.TutorialActivity;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import util.IabHelper;
import util.IabResult;

public class LoadingActivity extends Activity {

    static final String TAG = "Loading";

    AppData appData;
    Properties settings;
    LoadingDialog loadingDialog;
    SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        appData = new AppData(getApplication());
        settings = new Properties();
        loadingDialog = new LoadingDialog(this);
        prefs = getSharedPreferences("com.genreparrot", MODE_PRIVATE);

        // Load ASYNC
        LoadingTask lt = new LoadingTask();
        lt.execute();
    }

    public void initiateAppStore() {
        // Applications public key
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiZ9RhHkwfm9K851Q86pWuWfdBoorVQkI+DCdSquJB6uMt+FzeDgD3fdCzjAvzRzP9NW9zlVgoh7CZhzgv+9EYaRHfaOkX1dBRBvGOyo3wu6q1r56s9yLoDLT2GeAJuHXVFtOQqsPOK0ME8Af5UWtYhf3YIkBLmOX4eFDCGuwTKNRJ5IarAwDVs+ZPhgZeDyTBGF+xEStqYg/htQKbYh924LRYmMRmIOVt9Jw/j5nPKqGVFpS7qD/fZOzl3FeD+wp0D7XBC/zsBR/ex9CxaWrtJ/xMb9ktQyP1cc7Pzob7TnjVa/ggKSJsOdjXsCwe4gbssJ/Pr4TM2pWp+eMO33RDwIDAQAB";

        // Create the helper, passing it our context and the public key to verify signatures with
        AppData.myLog(TAG, "Creating IAB helper.");
        appData.iabHelper = new IabHelper(this, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should
        appData.iabHelper.enableDebugLogging(AppData.APPSTORE_DEBUGGING);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        AppData.myLog(TAG, "Starting setup.");
        appData.iabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                AppData.myLog(TAG, "Setup finished.");

                // Have we been disposed of in the meantime? If so, quit.
                if (appData.iabHelper == null) return;

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    AppData.myLog(TAG, getString(R.string.msgErrBillingSetup));
                }
                else {
                    // IAB is fully set up. Now, let's get an inventory of stuff we own.
                    AppData.myLog(TAG, "Setup successful.");
                }
            }
        });
    }

    public void ShowTutorialIfNotSeen(){
        if(prefs.getBoolean("tutorial", true)) {
            //prefs.edit().putBoolean("tutorial", false).commit();

            Intent i=new Intent(this,TutorialActivity.class);
            startActivity(i);
        }
    }

    private class LoadingTask extends AsyncTask<Void, String, String> {
        protected String doInBackground(Void... pkgs) {
            AssetManager assetManager = getApplication().getAssets();

            LoadDefaultDatabaseIfFirstRun();

            publishProgress(getString(R.string.msgInitiatingAppStore));
            if (!AppData.EMULATOR_BUILD) initiateAppStore();

            publishProgress(getString(R.string.msgResettingAlarm));
            resetEverydayAlarm();

            publishProgress(getString(R.string.msgReadingSettings));
            try {
                settings.loadFromXML(assetManager.open("settings.xml"));
            } catch (IOException e) {
                Log.e("ERROR", "Failed to load app settings");
            }

            publishProgress(getString(R.string.msgLoadingPackages));

            // get the included files record
            String packagesList = settings.getProperty("packages", null);

            // get the individual file names
            List<String> individualPackages = AppData.getInstance().splitNtrim(packagesList);

            // load individual packages
            for (String pkg : individualPackages) {
                appData.packages_loaded.put(pkg, new SoundPackage(getApplication(), pkg));
                publishProgress(getString(R.string.msgJustLoadedPackage)+" "+ pkg);
            }

            return "Done";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            runOnUiThread(loadingDialog.changeMsg(values[0]));
        }

        protected void onPostExecute(String str) {
            AppData.myLog(TAG, "Packages loaded");
            LoadingDone();
        }
    }

    public void resetEverydayAlarm(){
        // make sure we start the alarm every run.
        Intent intent = new Intent(getApplication(), MediaPlayerAdapter.class);
        boolean alarmUp = (PendingIntent.getBroadcast(getApplication(), (int) AppData.REPEAT_EVERYDAY_BROADCAST_ID,
                intent, PendingIntent.FLAG_NO_CREATE) != null);

        if (alarmUp)
        {
            AppData.myLog(TAG, "Alarm is already active");
        }
        else {
            AppData.myLog(TAG, "Alarm wasn't set. Setting it now.");
            appData.RestartAutomaticTimer();
        }
    }

    // Function that sets the default schedules in the database
    public void LoadDefaultDatabaseIfFirstRun(){
        if (prefs.getBoolean("firstrun", true)) {
            prefs.edit().putBoolean("firstrun", false).commit();

            ScheduleDAO SchDao = new ScheduleDAO(getApplicationContext());
            SchDao.open();

            SchDao.createSchedule(
                    "packages/pkg_basic/pora_podkrepitsa.mp3", //file
                    13, // volume
                    Long.parseLong("1403235038000"), // start
                    Long.parseLong("1403236838000"), // end
                    15, //repspersession
                    8, // repsinterval
                    5, //sessioninterval
                    5, // attractortimes
                    0, // state,
                    String.valueOf(AppData.ATTRACTORFILE_DEFAULT)
            );

            SchDao.createSchedule(
                    "packages/pkg_basic/svobodu_popugayam.mp3", //file
                    11, // volume
                    Long.parseLong("1403247623000"), // start
                    Long.parseLong("1403253023000"), // end
                    15, //repspersession
                    8, // repsinterval
                    10, //sessioninterval
                    3, // attractortimes
                    0, // state
                    String.valueOf(AppData.ATTRACTORFILE_DEFAULT)
            );

            SchDao.createSchedule(
                    "packages/pkg_basic/dumat_menwe_nado.mp3", //file
                    13, // volume
                    Long.parseLong("1403265632000"), // start
                    Long.parseLong("1403268332000"), // end
                    15, //repspersession
                    8, // repsinterval
                    5, //sessioninterval
                    3, // attractortimes
                    0, // state
                    String.valueOf(AppData.ATTRACTORFILE_DEFAULT)
            );

            SchDao.createSchedule(
                    "packages/pkg_basic/sova_otkrivay.mp3", //file
                    13, // volume
                    Long.parseLong("1403272850000"), // start
                    Long.parseLong("1403280050000"), // end
                    15, //repspersession
                    8, // repsinterval
                    10, //sessioninterval
                    4, // attractortimes
                    0, // state
                    String.valueOf(AppData.ATTRACTORFILE_DEFAULT)
            );

            SchDao.close();
        }
    }

    public void LoadingDone()
    {
        Intent i=new Intent(this,MainActivity.class);
        startActivity(i);
        finish();
        LoadingDialog.loading.dismiss();
        //TODO ShowTutorialIfNotSeen();
    }
}
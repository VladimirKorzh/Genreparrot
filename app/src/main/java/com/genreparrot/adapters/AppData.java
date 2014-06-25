package com.genreparrot.adapters;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import util.IabHelper;

public class AppData {
    private static AppData instance = null;

    private static final String  TAG = "AppData";

    public static final boolean  EMULATOR_BUILD = false;
    private static final boolean ENABLE_DEBUG_LOG = false;
    public static final boolean  APPSTORE_DEBUGGING = false;

    public static final int      RC_REQUEST = 22812;
    public static final long     REPEAT_EVERYDAY_BROADCAST_ID=9999;
    private static final long    ONE_DAY_IN_MILLIS = 86400000;

    public static final int    REPSPERSESSION_DEFAULT = 15;
    public static final int    REPSINTERVAL_DEFAULT = 8;
    public static final int    SESSIONINTERVAL_DEFAULT = 10;
    public static final int    ATTRACTORTIMES_DEFAULT = 3;
    public static final String ATTRACTORFILE_DEFAULT = "attactor/chik.wav";

    public static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiZ9RhHkwfm9K851Q86pWuWfdBoorVQkI+DCdSquJB6uMt+FzeDgD3fdCzjAvzRzP9NW9zlVgoh7CZhzgv+9EYaRHfaOkX1dBRBvGOyo3wu6q1r56s9yLoDLT2GeAJuHXVFtOQqsPOK0ME8Af5UWtYhf3YIkBLmOX4eFDCGuwTKNRJ5IarAwDVs+ZPhgZeDyTBGF+xEStqYg/htQKbYh924LRYmMRmIOVt9Jw/j5nPKqGVFpS7qD/fZOzl3FeD+wp0D7XBC/zsBR/ex9CxaWrtJ/xMb9ktQyP1cc7Pzob7TnjVa/ggKSJsOdjXsCwe4gbssJ/Pr4TM2pWp+eMO33RDwIDAQAB";
    public static final byte[] SALT = new byte[] {
            -46, 12, 30, -123, -103, -57, 55, -64, 51, 88, -95,
            -28, 77, -117, -36, -113, -13, 32, -64, 11
    };

    private Context context = null;
    public IabHelper iabHelper = null;
    public HashMap<String, SoundPackage> packages_loaded = new HashMap<String, SoundPackage>();

    public AppData(Context c){
        this.context = c;
        instance = this;
    }

    public static AppData getInstance() {
        return instance;
    }

    public void RestartAutomaticTimer(){
        // get the alarm manager reference
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // create the intent for the scheduled task
        Intent intent = new Intent(context, MediaPlayerAdapter.class);

        Time t = new Time();
        t.setToNow();
        t.normalize(true);
        t.hour = 0;
        t.minute = 0;
        t.second = 1;

        // set the alarm to be repeated every day at 0:0:1
        PendingIntent pi = PendingIntent.getBroadcast(context, (int) REPEAT_EVERYDAY_BROADCAST_ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        am.setRepeating(AlarmManager.RTC_WAKEUP, t.toMillis(true), ONE_DAY_IN_MILLIS, pi);
    }

    public List<String> splitNtrim(String str){
        List<String> result = new ArrayList<String>();
        for (String r :  Arrays.asList(str.split(","))){
            result.add(r.trim());
        }
        return result;
    }

    // Display a simple message to the user
    public void alert(Context c, String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(c);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        AppData.myLog(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }







    public static void myLog(String tag, String msg){
        if (ENABLE_DEBUG_LOG) Log.d(tag, msg);
    }

    public String getFilepathFromFileAlias(String fileAlias){
        for (SoundPackage sp : packages_loaded.values()){
            if (sp.files.containsValue(fileAlias)){
                return sp.files.inverse().get(fileAlias);
            }
        }
        AppData.myLog(TAG, "Input: " + fileAlias);
        AppData.myLog(TAG, "Error finding corresponding filepath. returning what we've got.");
        return fileAlias;
    }

    public String getAliasFromFilepath(String filepath){
        for (SoundPackage sp : packages_loaded.values()){
            if (sp.files.containsKey(filepath)){
                return sp.files.get(filepath);
            }
        }
        AppData.myLog(TAG, "Input: " + filepath);
        AppData.myLog(TAG, "Error finding corresponding alias. returning what we've got.");
        AppData.myLog(TAG, "Probably: " + getRealPathFromURI(Uri.parse(filepath)));
        return getRealPathFromURI(Uri.parse(filepath));
    }

    public String getRealPathFromURI(Uri contentUri)
    {
        String[] proj = { MediaStore.Audio.Media.DATA };
        Cursor cursor =  context.getContentResolver().query(contentUri, proj, null, null, null); //Since manageQuery is deprecated
        int column_index = 0;
        String res = "";
        if (cursor != null) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            cursor.moveToFirst();
            res = cursor.getString(column_index);
            cursor.close();
        }

        return res;
    }

}

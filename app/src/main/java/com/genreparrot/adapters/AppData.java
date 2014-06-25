package com.genreparrot.adapters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.Log;

import java.io.IOException;
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

    public static final int REPSPERSESSION_DEFAULT = 15;
    public static final int REPSINTERVAL_DEFAULT = 8;
    public static final int SESSIONINTERVAL_DEFAULT = 10;
    public static final int ATTRACTORTIMES_DEFAULT = 3;
    public static final String ATTRACTORFILE_DEFAULT = "attactor/chik.wav";


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
//
//    // returns all the sound packages names
//    public static ArrayList<String> getSoundPackagesNames(Context c){
//        String[] pkgs = null;
//        ArrayList<String> folders = new ArrayList<String>();
//
//        try {
//            pkgs = c.getAssets().list(pkg_path);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        if (pkgs != null){
//            for (String p : pkgs){
//                folders.add(p);
//            }
//        }
//
//        return folders;
//    }

    // returns full path to package from its name
//    public static String getPkg_path(String pkg){
//        return pkg_path+"/"+pkg;
//    }

    // Overloaded method for getting the default exclusion list
    public static ArrayList<String> getAssetsList(Context context, String path) {
        ArrayList<String> except = new ArrayList<String>();
        except.add(".txt");
        except.add(".png");
        except.add(".xml");
        return getAssetsList(context, path, except);
    }

    // Returns the full-path list of items in the specified Asset folder
    // except the ones with specified extensions
    public static ArrayList<String> getAssetsList(Context context, String path, ArrayList<String> except){
        ArrayList<String> files = new ArrayList<String>();
        String folder = path;
        String filePath = null;

        try {
            for (String filename : context.getAssets().list(folder)){
                boolean flag = false;
                AppData.myLog("debug", "AppData found file: " + filename);
                for (String exc : except) {
                    if (filename.endsWith(exc)) {
                        flag = true;
                        break;
                    }
                }
                if (flag == false) {
                    filePath = folder + "/" + filename;
                    files.add(filePath);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }

}

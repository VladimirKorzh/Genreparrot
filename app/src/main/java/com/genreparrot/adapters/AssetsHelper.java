package com.genreparrot.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class AssetsHelper {
    private static AssetsHelper instance = null;

    public static final boolean EMULATOR_BUILD = false;
    public static final boolean ENABLE_DEBUG_LOG = true;


    final static String pkg_path = "packages";
    final static String TAG = "AssetsHelper";
    private Context context;

    public ArrayList<String> packages_found;
    public HashMap<String, SoundPackage> packages_loaded = new HashMap<String, SoundPackage>();

    public AssetsHelper(Context c){
        this.context = c;
        instance = this;
    }

    public static AssetsHelper getInstance() {
        return instance;
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
        AssetsHelper.myLog(TAG, "Input: " + fileAlias);
        AssetsHelper.myLog(TAG, "Error finding corresponding filepath. returning what we've got.");
        return fileAlias;
    }

    public String getAliasFromFilepath(String filepath){
        for (SoundPackage sp : packages_loaded.values()){
            if (sp.files.containsKey(filepath)){
                return sp.files.get(filepath);
            }
        }
        AssetsHelper.myLog(TAG, "Input: " + filepath);
        AssetsHelper.myLog(TAG, "Error finding corresponding alias. returning what we've got.");
        AssetsHelper.myLog(TAG, "Probably: " + getRealPathFromURI(Uri.parse(filepath)));
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

    // returns all the sound packages names
    public static ArrayList<String> getSoundPackagesNames(Context c){
        String[] pkgs = null;
        ArrayList<String> folders = new ArrayList<String>();

        try {
            pkgs = c.getAssets().list(pkg_path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (pkgs != null){
            for (String p : pkgs){
                folders.add(p);
            }
        }

        return folders;
    }

    // returns full path to package from its name
    public static String getPkg_path(String pkg){
        return pkg_path+"/"+pkg;
    }

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
                AssetsHelper.myLog("debug", "AssetsHelper found file: " + filename);
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

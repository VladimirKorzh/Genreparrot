package com.genreparrot.adapters;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class SoundPackage {
    final static String pkg_path = "packages";

    public Properties props = new Properties();
    public String path = null;
    public String SKU = null;
    public Bitmap image = null;

    public BiMap<String, String> files = HashBiMap.create();
    public boolean owned = false;

    public SoundPackage(Context context, String packageName) {
        AssetManager am = context.getAssets();

        // store own location
        path = pkg_path+"/"+packageName;

        // get Google App Store SKU
        SKU = packageName;

        // make basic package owned by default
        if (SKU.equals("pkg_basic")) owned = true;

        // load package information
        try {
            props.loadFromXML(am.open(path + "/properties.xml"));
        } catch (IOException e) {
            Log.e("ERROR", "Package missing properties: " + path);
        }

        // get the included files record
        String filesString = props.getProperty("filelist", null);

        // get the individual file names
        if (filesString != null) {
            List<String> fileList = AppData.getInstance().splitNtrim(filesString);

            for (String file : fileList) {
                // get readable aliases of files and store them
                String fullPath = path+"/"+file+".mp3";
                files.put(fullPath, props.getProperty(file, fullPath));
            }
        }

        // load bitmap image of package
        try {
            image = BitmapFactory.decodeStream(am.open(path + "/header.png"));
        } catch (IOException e) {
            Log.e("ERROR", "Package missing header picture: " + path);
        }
    }
}
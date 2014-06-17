package com.genreparrot.adapters;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class SoundPackage {
    public String SKU = null;
    public Bitmap image = null;
    public String path = null;
    public Properties props = new Properties();
    public BiMap<String,String> files = HashBiMap.create();

    public boolean owned = false;

    private String getFilename(String file){
        String name = file.replace(path+"/", "");
        name = name.substring(0, name.length() - 4);
        return name;
    }

    public SoundPackage(Context context, String packagePath){
        AssetManager am = context.getAssets();
        path = packagePath;
        SKU = packagePath.replace(AssetsHelper.pkg_path+"/","");

        // make basic package owned by default
        if (SKU.equals("pkg_basic")) owned = true;

        ArrayList<String> fileList = AssetsHelper.getAssetsList(context, path);
        try {
            image = BitmapFactory.decodeStream(am.open(path + "/header.png"));
        } catch (IOException e) {
            Log.e("ERROR","Package missing header picture: " + path);
        }
        try {
            props.loadFromXML(am.open(path + "/properties.xml"));

            for (String file : fileList){
                files.put(file, props.getProperty(getFilename(file), file));
            }


        } catch (IOException e) {
            Log.e("ERROR","Package missing properties: " + path);
        }
    }
}

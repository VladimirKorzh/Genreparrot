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
    public Bitmap image = null;
    public String path = null;
    public Properties props = new Properties();
    public BiMap<String,String> files = HashBiMap.create();

    private String getTag(String file){
        String tag = file.replace(path+"/", "");
        tag = tag.substring(0, tag.length() - 4);
        return tag;
    }

    public SoundPackage(Context context, String packagePath){
        AssetManager am = context.getAssets();
        path = packagePath;
        ArrayList<String> fileList = AssetsAdapter.getAssetsList(context, path);
        try {
            image = BitmapFactory.decodeStream(am.open(path + "/header.png"));
        } catch (IOException e) {
            Log.e("ERROR","Package missing header picture: " + path);
        }
        try {
            props.loadFromXML(am.open(path + "/properties.xml"));

            for (String file : fileList){
                files.put(file, props.getProperty(getTag(file), file));
            }


        } catch (IOException e) {
            Log.e("ERROR","Package missing properties: " + path);
        }


    }
}

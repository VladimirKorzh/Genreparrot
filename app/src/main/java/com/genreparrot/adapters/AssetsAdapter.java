package com.genreparrot.adapters;

import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by futurewife on 15.06.14.
 */
public class AssetsAdapter {

    // Returns the full-path list of items in the specified Asset folder
    public static ArrayList<String> getAssetsList(Context context, String path){
        ArrayList<String> files = new ArrayList<String>();
        String folder = path;
        String filePath = null;

        try {
            for (String filename : context.getAssets().list(folder)){
                filePath = folder+"/"+filename;

                files.add(filePath);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }

}

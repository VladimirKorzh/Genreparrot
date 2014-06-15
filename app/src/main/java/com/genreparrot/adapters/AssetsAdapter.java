package com.genreparrot.adapters;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
                Log.d("debug", "AssetsAdapter found file: " + filePath);
                files.add(filePath);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }

    public static String readTxtFile(Context context, String filepath){
        StringBuilder contents = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(filepath), "UTF-8"));

            // do reading, usually loop until end of file reading
            String mLine = reader.readLine();
            while (mLine != null) {
                mLine = reader.readLine();
                contents.append(mLine);
            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return contents.toString();
    }
}

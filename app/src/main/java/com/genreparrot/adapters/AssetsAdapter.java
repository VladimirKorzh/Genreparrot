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
                Log.d("debug", "AssetsAdapter found file: " + filename);
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

    public static String readTxtFile(Context context, String filepath){
        StringBuilder contents = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(filepath), "UTF-8"));

            // do reading, usually loop until end of file reading
            String mLine = reader.readLine();
            while (mLine != null) {
                contents.append(mLine);
                mLine = reader.readLine();
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

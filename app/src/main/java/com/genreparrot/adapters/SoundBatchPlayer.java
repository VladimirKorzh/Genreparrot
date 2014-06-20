package com.genreparrot.adapters;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import java.util.Stack;

public class SoundBatchPlayer implements MediaPlayer.OnCompletionListener{

    private static SoundBatchPlayer instance = null;
	private MediaPlayer mp = null;
    private Context lastContext = null;
    private Stack playlist = new Stack();

    protected SoundBatchPlayer() {
        mp = new MediaPlayer();
        mp.setOnCompletionListener(this);
    }

    public static SoundBatchPlayer getInstance() {
        if(instance == null) {
            instance = new SoundBatchPlayer();
        }
        return instance;
    }

    public void playSingleFile(Context context, String filepath) {
        clearPlaylist();
        stopPlaying();
        playFile(context, filepath);
    }

    public void clearPlaylist(){
        AssetsHelper.myLog("debug","Clear Playlist");
        if (!playlist.empty()) playlist.clear();
    }

    public void stopPlaying() {
        AssetsHelper.myLog("debug","Stop Playing");
        mp.stop();
        mp.reset();
    }

    private void playFile(Context context, String filename) {
        try {
            lastContext = context;

            mp.setVolume(1f, 1f);
            if (filename.contains("storage/")) {
                mp.setDataSource(filename);
            }
            else {
                AssetFileDescriptor afd = lastContext.getAssets().openFd(filename);
                mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            }
            mp.prepare();
            mp.start();
            AssetsHelper.myLog("debug","Playing file: "+filename);
        } catch (Exception e) {
            AssetsHelper.myLog("ERROR", "FILE NOT FOUND: "+filename);
        }
    }

    public void playPlaylist(Context context, Stack files) {
      lastContext = context;
      AssetsHelper.myLog("debug", "playing playlist");
      playlist = files;
      playFile(lastContext, (String)files.pop());
    }

    public void onCompletion(MediaPlayer arg0) {
      stopPlaying();
      if (!playlist.empty()) {
    	  playFile(lastContext, (String)playlist.pop());
      }
    }
}

package com.genreparrot.adapters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.format.Time;
import android.util.Log;

import com.genreparrot.database.Schedule;
import com.genreparrot.database.ScheduleDAO;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class MediaPlayerAdapter extends BroadcastReceiver {

    public static final long REPEAT_EVERYDAY_BROADCAST_ID=9999;

    private static MediaPlayerAdapter instance = null;
    public static List<Long> list = new ArrayList<Long>();

    private static String fAttention = "attactor/chik.wav";

    public static MediaPlayerAdapter getInstance() {
        if(instance == null) {
            instance = new MediaPlayerAdapter();
        }
        return instance;
    }

    public static void debugTime(String name, Time t){
        AssetsHelper.myLog("debug", "Time: "+name+" "+t.hour+":"+t.minute+":"+t.second);
    }

    public void Schedule(Context context, Schedule s){

        AssetsHelper.myLog("debug", "---- Schedule function ----"+s.getId());

        // get the current system time information
        Time currentTime = new Time();
        currentTime.setToNow();
        debugTime("current time:",currentTime);

        // Print packages.basic debug info about the schedule
        s.print();

        // check if schedule has start time set in the past and modify it to the current time
        // in order to prevent the system from invoking all the previous alarms all at once.
        Time start = Schedule.timeMillisToObject(s.getStarttime());
        Time end   = Schedule.timeMillisToObject(s.getEndtime());
        if (currentTime.after(start) && currentTime.before(end)) {
            AssetsHelper.myLog("debug", "-> Now is after start but before end. Scheduling to start right now.");

            s.setStarttime(currentTime.toMillis(true));
            this.Schedule(context, s);
            return;
        }

        // Check if the alarm is already past its due time. In that case do not execute anything.
        if (currentTime.after(end)){
            AssetsHelper.myLog("debug", "-> Now is after end do not even bother.");
            return;
        }


        // get the alarm manager reference
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        // create the intent for the scheduled task
        Intent intent = new Intent(context, MediaPlayerAdapter.class);
        intent.putExtra("schedule", s);
        PendingIntent pi = PendingIntent.getBroadcast(context, (int) s.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // set the alarm
        am.setRepeating(AlarmManager.RTC_WAKEUP, start.toMillis(true), s.getRepsinterval() * 1000, pi);

        // Save the alarm id for future cancellation
        list.add(s.getId());
    }

    @Override
	public void onReceive(Context context, Intent intent) {

        // Prevent the phone from going into sleep mode
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
		//Acquire the lock
		wl.acquire();

        // get the parcelable schedule from intent
        Bundle b = intent.getExtras();
        Schedule s = b.getParcelable("schedule");

        // In case if the parcelable value of schedule hasn't been passed
        // we are dealing with everyday-repeating case.
        if (s == null) {
            ScheduleDAO SchDao = new ScheduleDAO(context);
            SchDao.open();

            List<Schedule> lstSch = SchDao.getAllSchedules();
            MediaPlayerAdapter mpa = new MediaPlayerAdapter();
            mpa.CancelAllSchedules(context);

            for (Schedule cur : lstSch) {
                if (cur.getState() == 1)
                    mpa.Schedule(context, new Schedule(cur));
            }

            SchDao.close();
            return;
        }


        AssetsHelper.myLog("debug", "---- OnReceive ----"+s.getId());

        // get the current system time information
        Time currentTime = new Time();
        currentTime.setToNow();
        debugTime("current time:",currentTime);

        // Print packages.basic debug info about the schedule
        s.print();

        //  Schedule time is up. Stop it from repeating.
        Time end   = Schedule.timeMillisToObject(s.getEndtime());
        if (currentTime.after(end)) {
            AssetsHelper.myLog("debug", "Schedule End Time Reached Cancelling schedule");
            CancelSchedule(context, s.getId());
            return;
        }

        // Session is over. Reschedule
        Time sessionEnd = Schedule.timeMillisToObject(s.getStarttime());
        sessionEnd.second += (s.getRepspersession() * s.getRepsinterval())-1;
        sessionEnd.normalize(true);

        Time nextStart = new Time();
        nextStart.setToNow();
        nextStart.minute += s.getSessioninterval();
        nextStart.normalize(true);

        if (currentTime.after(sessionEnd)){
            CancelSchedule(context, s.getId());
            s.setStarttime(nextStart.toMillis(true));
            debugTime("Session of reps is over. Next scheduled at: ", nextStart);
            this.Schedule(context, s);
            return;
        }

        // Set the desired output volume
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, s.getVolume(), 0);

        // initiate our player and set up the playlist
		SoundBatchPlayer sb = new SoundBatchPlayer();
		Stack files = new Stack();
		files.push(s.getFilename());

        // Check if we have played the attractor sound enough times already
        Time sessionAttractorEnd = new Time();
        sessionAttractorEnd.set(Schedule.timeMillisToObject(s.getStarttime()));
        sessionAttractorEnd.second += (s.getAttractor()*s.getRepsinterval())-1;
        sessionAttractorEnd.normalize(true);
        if (currentTime.before(sessionAttractorEnd)) {
            files.push(fAttention);
        }
		sb.playPlaylist(context, files);
		
		//Release the lock
		wl.release();
	}

	public void CancelAllSchedules(Context context)
	{
        if (!list.isEmpty()) {
            AssetsHelper.myLog("debug", "activeIds: " + list.size());

            Iterator<Long> it = list.iterator();
            while(it.hasNext()){
                Long id  = it.next();
                Intent intent = new Intent(context, MediaPlayerAdapter.class);
                PendingIntent sender = PendingIntent.getBroadcast(context, id.intValue(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.cancel(sender);
                sender.cancel();
                AssetsHelper.myLog("debug","removing schedule with id: "+id);
                it.remove();
            }

        }
        else {
            AssetsHelper.myLog("debug","activeIds is empty");
        }
	}

    public void CancelSchedule(Context context, Long id){
        Intent intent = new Intent(context, MediaPlayerAdapter.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, id.intValue(), intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
        AssetsHelper.myLog("debug", "scheduled alarm cancelled "+ id);
        list.remove(id);
    }
}
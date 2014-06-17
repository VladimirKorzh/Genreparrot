package com.genreparrot.adapters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.genreparrot.app.R;
import com.genreparrot.database.Schedule;
import com.genreparrot.database.ScheduleDAO;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ScheduleListAdapter extends ArrayAdapter<Schedule>{
    private final Context context;
    private final ArrayList<Schedule> values;

    public ScheduleListAdapter(Context context, ArrayList<Schedule> values) {
        super(context, R.layout.list_single_schedule_layout, values);
        this.context = context;
        this.values = values;
    }


    public void actionStartTraining(){
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
        PendingIntent pi = PendingIntent.getBroadcast(context, (int) MediaPlayerAdapter.REPEAT_EVERYDAY_BROADCAST_ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        am.setRepeating(AlarmManager.RTC_WAKEUP, t.toMillis(true), TimeUnit.DAYS.toMillis(1), pi);
        //MediaPlayerAdapter.list.add(MediaPlayerAdapter.REPEAT_EVERYDAY_BROADCAST_ID);
        //Toast.makeText(context, "Schedule changed successfully", Toast.LENGTH_LONG).show();
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_single_schedule_layout, parent, false);

        TextView starttime = (TextView) rowView.findViewById(R.id.txtStartTime);
        TextView endtime   = (TextView) rowView.findViewById(R.id.txtEndTime);
        TextView filename  = (TextView) rowView.findViewById(R.id.txtFilename);
        TextView txtPos    = (TextView) rowView.findViewById(R.id.txtPos);
        Switch   sw        = (Switch)   rowView.findViewById(R.id.switchOnOff);

        starttime.setText(String.valueOf(Schedule.timeMillisToString(values.get(position).getStarttime())));
        endtime.setText(String.valueOf(Schedule.timeMillisToString(values.get(position).getEndtime())));

        String alias = AssetsHelper.getInstance().getAliasFromFilepath(String.valueOf(values.get(position).getFilename()));
        filename.setText(alias);

        sw.setChecked(values.get(position).getState() == 1);

        txtPos.setText(String.valueOf(values.get(position).getId()));

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Check for overlaps in schedule
                boolean overlap = false;
                Schedule s1 = values.get(position);
                Time start_s1 = Schedule.timeMillisToObject(s1.getStarttime());
                Time end_s1 = Schedule.timeMillisToObject(s1.getEndtime());
                for(Schedule s2 : values){

                    if (s2.getState() == 0) continue;
                    if (s1 == s2) continue;

                    Time start_s2 = Schedule.timeMillisToObject(s2.getStarttime());
                    Time end_s2 = Schedule.timeMillisToObject(s2.getEndtime());

                    MediaPlayerAdapter.debugTime(start_s1.toString(),start_s1);
                    MediaPlayerAdapter.debugTime(end_s1.toString(),end_s1);
                    MediaPlayerAdapter.debugTime(start_s2.toString(),start_s2);
                    MediaPlayerAdapter.debugTime(end_s2.toString(),end_s2);

                    if ( start_s1.after(start_s2)){
                        // second goes first
                        if ( !end_s2.before(start_s1)) {
                            overlap = true;
                            break;
                        }
                    }
                    else{
                        // first goes first
                        if ( !end_s1.before(start_s2)){
                            overlap = true;
                            break;
                        }
                    }
                }

                LinearLayout layout = null;
                Switch sw = null;
                switch (view.getId()){
                    case R.id.lstScheduleItem:
                        layout = (LinearLayout) view;
                        sw = (Switch) view.findViewById(R.id.switchOnOff);
                        sw.toggle();
                        break;
                    case R.id.switchOnOff:
                        layout = (LinearLayout) view.getParent();
                        sw = (Switch) view;
                        break;
                }


                if (overlap){
                    Toast.makeText(getContext(), context.getString(R.string.msgErrScheduleOverlap), 1000).show();
                    sw.toggle();
                    return;
                }

                if (layout != null && sw != null) {
                    TextView txtPos = (TextView) layout.findViewById(R.id.txtPos);
                    Log.d("debug", "clicked " + txtPos.getText());

                    ScheduleDAO SchDao = new ScheduleDAO(getContext().getApplicationContext());
                    SchDao.open();
                    SchDao.toggleSchedule(Integer.parseInt(txtPos.getText().toString()), sw.isChecked()? 1 : 0);
                    SchDao.close();
                    values.get(position).setState(sw.isChecked() ? 1 : 0);
                }

                actionStartTraining();
            }
        };
        rowView.setOnClickListener(listener);
        sw.setOnClickListener(listener);
        return rowView;
    }
}
package com.genreparrot.adapters;

import android.content.Context;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.genreparrot.app.R;
import com.genreparrot.database.Schedule;
import com.genreparrot.database.ScheduleDAO;

import org.jraf.android.backport.switchwidget.Switch;

import java.util.ArrayList;

public class ScheduleListAdapter extends ArrayAdapter<Schedule>{
    private final Context context;
    private final ArrayList<Schedule> values;

    public ScheduleListAdapter(Context context, ArrayList<Schedule> values) {
        super(context, R.layout.list_single_schedule_layout, values);
        this.context = context;
        this.values = values;
    }



    public void SubmitChangesToDb(LinearLayout layout, Switch sw, Integer position){
        if (layout != null && sw != null) {
            TextView txtPos = (TextView) layout.findViewById(R.id.txtItemId);
            AppData.myLog("debug", "clicked " + txtPos.getText());

            ScheduleDAO SchDao = new ScheduleDAO(getContext().getApplicationContext());
            SchDao.open();
            SchDao.toggleSchedule(Integer.parseInt(txtPos.getText().toString()), sw.isChecked()? 1 : 0);
            SchDao.close();
            values.get(position).setState(sw.isChecked() ? 1 : 0);
        }
    }

    public boolean CheckScheduleOverlap(Switch sw, Integer position){
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

        if (overlap){
            Toast.makeText(getContext(), context.getString(R.string.msgErrScheduleOverlap), 1000).show();
            sw.toggle();
            // There is an overlap
            return true;
        }
        else{
            // There are no overlaps, we are good
            return false;
        }
    }

    public void ScheduleStateHasChanged(View view, Integer position){
        // Find important items on the screen depending on what was pressed
        LinearLayout layout = null;
        org.jraf.android.backport.switchwidget.Switch sw = null;
        switch (view.getId()){
            case R.id.lstScheduleItem:
                layout = (LinearLayout) view;
                sw = (org.jraf.android.backport.switchwidget.Switch) view.findViewById(R.id.switchOnOff);
                sw.toggle();
                break;
            case R.id.switchOnOff:
                layout = (LinearLayout) view.getParent();
                sw = (org.jraf.android.backport.switchwidget.Switch) view;
                break;
        }

        // Check if there is an overlap
        if (!CheckScheduleOverlap(sw, position)){
            SubmitChangesToDb(layout, sw, position);
            AppData.getInstance().RestartAutomaticTimer();
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_single_schedule_layout, parent, false);

        TextView starttime = (TextView) rowView.findViewById(R.id.txtStartTime);
        TextView endtime   = (TextView) rowView.findViewById(R.id.txtEndTime);
        TextView filename  = (TextView) rowView.findViewById(R.id.txtFilename);
        TextView txtItemID    = (TextView) rowView.findViewById(R.id.txtItemId);
        final org.jraf.android.backport.switchwidget.Switch sw = (org.jraf.android.backport.switchwidget.Switch) rowView.findViewById(R.id.switchOnOff);

        starttime.setText(String.valueOf(Schedule.timeMillisToString(values.get(position).getStarttime())));
        endtime.setText(String.valueOf(Schedule.timeMillisToString(values.get(position).getEndtime())));

        String alias = AppData.getInstance().getAliasFromFilepath(String.valueOf(values.get(position).getFilename()));
        filename.setText(alias);

        sw.setChecked(values.get(position).getState() == 1);

        txtItemID.setText(String.valueOf(values.get(position).getId()));

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppData.myLog("list adapter", "sw state changed by click");
                sw.toggle();
            }
        };

        rowView.setOnClickListener(listener);
        sw.setOnClickListener(listener);

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be true if the switch is in the On position
                AppData.myLog("list adapter", "sw state changed by swipe");
                ScheduleStateHasChanged(buttonView, position);
            }
        });
        return rowView;
    }
}
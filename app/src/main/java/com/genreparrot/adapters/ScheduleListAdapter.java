package com.genreparrot.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.genreparrot.app.R;
import com.genreparrot.database.Schedule;
import com.genreparrot.database.ScheduleDAO;

public class ScheduleListAdapter extends ArrayAdapter<Schedule>{
    private final Context context;
    private final Schedule[] values;

    public ScheduleListAdapter(Context context, Schedule[] values) {
        super(context, R.layout.list_single_schedule_layout, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_single_schedule_layout, parent, false);

        TextView starttime = (TextView) rowView.findViewById(R.id.txtStartTime);
        TextView endtime = (TextView) rowView.findViewById(R.id.txtEndTime);
        TextView filename = (TextView) rowView.findViewById(R.id.txtFilename);
        TextView txtPos   = (TextView) rowView.findViewById(R.id.txtPos);
        Switch sw = (Switch) rowView.findViewById(R.id.switchOnOff);

        starttime.setText(String.valueOf(Schedule.timeMillisToString(values[position].getStarttime())));
        endtime.setText(String.valueOf(Schedule.timeMillisToString(values[position].getEndtime())));
        filename.setText(String.valueOf(values[position].getFilename()));

        sw.setChecked(values[position].getState() == 1);


        Log.d("debug", "Schedule state "+values[position].getState());
        txtPos.setText(String.valueOf(values[position].getId()));

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                if (layout != null && sw != null) {
                    TextView txtPos = (TextView) layout.findViewById(R.id.txtPos);
                    Log.d("debug", "clicked " + txtPos.getText());

                    ScheduleDAO SchDao = new ScheduleDAO(context);
                    SchDao.open();
                    SchDao.toggleSchedule(Integer.parseInt(txtPos.getText().toString()), sw.isChecked()? 1 : 0);
                    SchDao.close();
                }
            }
        };


        sw.setOnClickListener(listener);
        rowView.setOnClickListener(listener);
        return rowView;
    }




}
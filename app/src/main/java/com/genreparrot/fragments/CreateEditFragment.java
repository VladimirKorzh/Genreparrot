package com.genreparrot.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.genreparrot.adapters.AppData;
import com.genreparrot.app.R;
import com.genreparrot.database.Schedule;
import com.genreparrot.database.ScheduleDAO;

public class CreateEditFragment extends Fragment {

    public CreateEditFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            ScheduleDAO SchDao = new ScheduleDAO(getActivity());
            View rootView = inflater.inflate(R.layout.fragment_create_edit_alarm, container, false);

            TextView filename = (TextView) rootView.findViewById(R.id.txtTrainingSound);
            SeekBar volume = (SeekBar) rootView.findViewById(R.id.seekVolumeMusic);
            TextView starttime = (TextView) rootView.findViewById(R.id.txtStartTime);
            TextView endtime = (TextView) rootView.findViewById(R.id.txtEndTime);
            TextView repspersession = (TextView) rootView.findViewById(R.id.txtRepsPerSession);
            TextView repsinterval = (TextView) rootView.findViewById(R.id.txtRepsInterval);
            TextView sessioninterval = (TextView) rootView.findViewById(R.id.txtSessionInterval);
            TextView attractortimes = (TextView) rootView.findViewById(R.id.txtAttractorSoundTimes);
            TextView attractorsound = (TextView) rootView.findViewById(R.id.txtAttractorSound);

            Bundle b = getActivity().getIntent().getExtras();
            int scheduleID = b.getInt("scheduleID");
            SchDao.open();
            Schedule sch = SchDao.getSchedule(scheduleID);
            if (sch == null) scheduleID = -1;
            if (scheduleID == -1){
                // new schedule -> load default values
                repspersession.setText(String.valueOf(AppData.REPSPERSESSION_DEFAULT));
                repsinterval.setText(String.valueOf(AppData.REPSINTERVAL_DEFAULT));
                sessioninterval.setText(String.valueOf(AppData.SESSIONINTERVAL_DEFAULT));
                attractortimes.setText(String.valueOf(AppData.ATTRACTORTIMES_DEFAULT));
                attractorsound.setText(String.valueOf(AppData.ATTRACTORFILE_DEFAULT));
            }
            else {
                // load it from db
                String alias = AppData.getInstance().getAliasFromFilepath(sch.getFilename());
                filename.setText(alias);
                volume.setProgress(sch.getVolume());

                starttime.setText(Schedule.timeMillisToString(sch.getStarttime()));
                endtime.setText(Schedule.timeMillisToString(sch.getEndtime()));

                repspersession.setText(String.valueOf(sch.getRepspersession()));
                repsinterval.setText(String.valueOf(sch.getRepsinterval()));
                sessioninterval.setText(String.valueOf(sch.getSessioninterval()));
                attractortimes.setText(String.valueOf(sch.getAttractorTimes()));
                attractorsound.setText(String.valueOf(sch.getAttractorFile()));
            }
            SchDao.close();
            return rootView;
        }
}

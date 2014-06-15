package com.genreparrot.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.genreparrot.adapters.ScheduleListAdapter;
import com.genreparrot.app.CreateEditSchedule;
import com.genreparrot.app.R;
import com.genreparrot.database.Schedule;
import com.genreparrot.database.ScheduleDAO;

import java.util.List;

public class ScheduleFragment extends Fragment
{
    private ScheduleListAdapter ad;
    private List<Schedule> lstSch;
    private View root;


    public ScheduleFragment(){
    }

    private void getList(){
        ScheduleDAO SchDao = new ScheduleDAO(getActivity());
        SchDao.open();

        List<Schedule> lstSch = SchDao.getAllSchedules();
        Schedule[] arr = lstSch.toArray(new Schedule[lstSch.size()]);
        ScheduleListAdapter ad = new ScheduleListAdapter(getActivity(), arr);
        ListView lst = (ListView) root.findViewById(R.id.lstSchedules);

        lst.setAdapter(ad);
    }

    @Override
    public void onResume(){
        getList();
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        root = inflater.inflate(R.layout.fragment_schedule, container, false);
        registerForContextMenu((ListView) root.findViewById(R.id.lstSchedules));
        return root;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater m = getActivity().getMenuInflater();
        m.inflate(R.menu.schedule_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Log.d("debug", "Clicked item pos: " + String.valueOf(info.id));

        ListView lst = (ListView) root.findViewById(R.id.lstSchedules);
        ScheduleListAdapter ad = (ScheduleListAdapter) lst.getAdapter();
        Schedule s = ad.getItem((int) info.id);

        switch(item.getItemId()){
            case R.id.schedule_delete_item:
                ScheduleDAO scheduleDAO = new ScheduleDAO(getActivity());
                scheduleDAO.open();
                scheduleDAO.deleteSchedule(s);
                scheduleDAO.close();
                getList();
                return true;
            case R.id.schedule_edit_item:
                Intent intent = new Intent(getActivity(), CreateEditSchedule.class);
                Bundle b = new Bundle();
                b.putInt("scheduleID", (int) s.getId());
                intent.putExtras(b);
                startActivity(intent);
                getActivity().overridePendingTransition( R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                return true;
        }
        return super.onContextItemSelected(item);
    }
}
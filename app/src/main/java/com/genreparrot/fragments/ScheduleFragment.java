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

import java.util.ArrayList;
import java.util.List;

public class ScheduleFragment extends Fragment
{
    public ScheduleListAdapter ad;
    private View root;

    public ScheduleFragment(){
    }

    public void getList(){
        ScheduleDAO SchDao = new ScheduleDAO(getActivity());
        SchDao.open();

        List<Schedule> lstSch = SchDao.getAllSchedules();
        ad.clear();
        for (Schedule s : lstSch){
            ad.add(s);
        }
        ad.notifyDataSetChanged();
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
        ListView lst = (ListView) root.findViewById(R.id.lstSchedules);

        ad = new ScheduleListAdapter(getActivity(), new ArrayList<Schedule>());
        lst.setAdapter(ad);
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
        ScheduleDAO scheduleDAO = new ScheduleDAO(getActivity());
        scheduleDAO.open();
        switch(item.getItemId()){
            case R.id.schedule_delete_item:
                scheduleDAO.deleteSchedule(s);

                getList();
                ad.actionStartTraining();
                break;
            case R.id.schedule_edit_item:
                scheduleDAO.toggleSchedule((int) s.getId(),0);
                ad.actionStartTraining();

                Intent intent = new Intent(getActivity(), CreateEditSchedule.class);
                Bundle b = new Bundle();
                b.putInt("scheduleID", (int) s.getId());
                intent.putExtras(b);
                startActivity(intent);
                getActivity().overridePendingTransition( R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                break;
        }

        scheduleDAO.close();
        return super.onContextItemSelected(item);
    }
}
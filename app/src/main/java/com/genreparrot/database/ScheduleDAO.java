package com.genreparrot.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.genreparrot.adapters.AppData;

import java.util.ArrayList;
import java.util.List;

public class ScheduleDAO {

    // Database fields
    private SQLiteDatabase database;
    private SqliteHelper dbHelper;
    private String[] allColumns = { SqliteHelper.COLUMN_ID,
                                    SqliteHelper.COLUMN_FILENAME,
                                    SqliteHelper.COLUMN_VOLUME,
                                    SqliteHelper.COLUMN_STARTTIME,
                                    SqliteHelper.COLUMN_ENDTIME,
                                    SqliteHelper.COLUMN_REPSPERSESSION,
                                    SqliteHelper.COLUMN_REPSINTERVAL,
                                    SqliteHelper.COLUMN_SESSIONINTERVAL,
                                    SqliteHelper.COLUMN_ATTRACTORTIMES,
                                    SqliteHelper.COLUMN_STATE,
                                    SqliteHelper.COLUMN_ATTRACTORFILE};

    public ScheduleDAO(Context context) {
        dbHelper = new SqliteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }


    public boolean updateSchedule(int id, String filename, int volume, long starttime, long endtime,
                                    int repspersession, int repsinterval, int sessioninterval, int attractortimes,
                                   int state, String attractorFile) {
        ContentValues values = new ContentValues();

        values.put(SqliteHelper.COLUMN_FILENAME, filename);
        values.put(SqliteHelper.COLUMN_VOLUME, volume);
        values.put(SqliteHelper.COLUMN_STARTTIME, starttime);
        values.put(SqliteHelper.COLUMN_ENDTIME, endtime);
        values.put(SqliteHelper.COLUMN_REPSINTERVAL, repsinterval);
        values.put(SqliteHelper.COLUMN_REPSPERSESSION, repspersession);
        values.put(SqliteHelper.COLUMN_SESSIONINTERVAL, sessioninterval);
        values.put(SqliteHelper.COLUMN_ATTRACTORTIMES, attractortimes);
        values.put(SqliteHelper.COLUMN_STATE, state);
        values.put(SqliteHelper.COLUMN_ATTRACTORFILE, attractorFile);
        int i = database.update(SqliteHelper.TABLE_SCHEDULES, values, SqliteHelper.COLUMN_ID + "="+id,null);
        return i>0;
    }

    public boolean toggleSchedule(int id, int state){
        AppData.myLog("debug", "toggleSchedule: " + id + " " + state);
        ContentValues values = new ContentValues();
        values.put(SqliteHelper.COLUMN_STATE, state);
        int i = database.update(SqliteHelper.TABLE_SCHEDULES, values, SqliteHelper.COLUMN_ID + "="+id,null);
        AppData.myLog("debug", "result: " + i);
        return i>0;
    }


    public Schedule createSchedule(String filename, int volume, long starttime, long endtime,
                                   int repspersession, int repsinterval, int sessioninterval, int attractortimes,
                                   int state, String attractorFile) {
        ContentValues values = new ContentValues();

        values.put(SqliteHelper.COLUMN_FILENAME, filename);
        values.put(SqliteHelper.COLUMN_VOLUME, volume);
        values.put(SqliteHelper.COLUMN_STARTTIME, starttime);
        values.put(SqliteHelper.COLUMN_ENDTIME, endtime);
        values.put(SqliteHelper.COLUMN_REPSINTERVAL, repsinterval);
        values.put(SqliteHelper.COLUMN_REPSPERSESSION, repspersession);
        values.put(SqliteHelper.COLUMN_SESSIONINTERVAL, sessioninterval);
        values.put(SqliteHelper.COLUMN_ATTRACTORTIMES, attractortimes);
        values.put(SqliteHelper.COLUMN_STATE, state);
        values.put(SqliteHelper.COLUMN_ATTRACTORFILE, attractorFile);

        AppData.myLog("DATABASE", values.toString());

        long insertId = database.insert(SqliteHelper.TABLE_SCHEDULES, null, values);

        Cursor cursor = database.query(SqliteHelper.TABLE_SCHEDULES,
                allColumns, SqliteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);

        if (!(cursor.moveToFirst()) || cursor.getCount() ==0){
            AppData.myLog("debug", "empty cursor");
        }
        cursor.moveToFirst();
        Schedule newSchedule = cursorToSchedule(cursor);
        cursor.close();
        return newSchedule;
    }

    public void deleteSchedule(Schedule schedule) {
        long id = schedule.getId();
        System.out.println("Schedule deleted with id: " + id);
        database.delete(SqliteHelper.TABLE_SCHEDULES, SqliteHelper.COLUMN_ID + " = " + id, null);
    }

    public Schedule getSchedule(int id){
        Cursor cursor = database.query(SqliteHelper.TABLE_SCHEDULES,
                                        allColumns, SqliteHelper.COLUMN_ID+"="+id,
                                        null, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount()!=0) {
            return cursorToSchedule(cursor);
        }
        else
            return null;
    }

    public List<Schedule> getAllSchedules() {
        List<Schedule> schedules = new ArrayList<Schedule>();

        Cursor cursor = database.query(SqliteHelper.TABLE_SCHEDULES,
                                        allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Schedule schedule = cursorToSchedule(cursor);
            schedules.add(schedule);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return schedules;
    }

    private Schedule cursorToSchedule(Cursor cursor) {
        Schedule schedule = new Schedule();

        schedule.setId(cursor.getLong(0));
        schedule.setFilename(cursor.getString(1));
        schedule.setVolume(cursor.getInt(2));
        schedule.setStarttime(cursor.getLong(3));
        schedule.setEndtime(cursor.getLong(4));
        schedule.setRepspersession(cursor.getInt(5));
        schedule.setRepsinterval(cursor.getInt(6));
        schedule.setSessioninterval(cursor.getInt(7));
        schedule.setAttractorTimes(cursor.getInt(8));
        schedule.setState(cursor.getInt(9));
        schedule.setAttractorFile(cursor.getString(10));

        return schedule;
    }
}
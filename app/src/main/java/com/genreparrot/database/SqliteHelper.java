package com.genreparrot.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SqliteHelper extends SQLiteOpenHelper {

    public static final String TABLE_SCHEDULES = "schedules";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_FILENAME = "filename";
    public static final String COLUMN_VOLUME = "volume";
    public static final String COLUMN_STARTTIME = "starttime";
    public static final String COLUMN_ENDTIME = "endtime";
    public static final String COLUMN_REPSPERSESSION = "repspersession";
    public static final String COLUMN_REPSINTERVAL = "repsinterval";
    public static final String COLUMN_SESSIONINTERVAL = "sessioninterval";
    public static final String COLUMN_ATTRACTORTIMES = "attractorTimes";
    public static final String COLUMN_STATE = "state";
    public static final String COLUMN_ATTRACTORFILE = "attractorFile";

    private static final String DATABASE_NAME = "schedules.db";
    private static final int DATABASE_VERSION = 7;


    public SqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table " + TABLE_SCHEDULES +"(" +
            COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_FILENAME  + " text not null, " +
            COLUMN_VOLUME + " integer not null, " +
            COLUMN_STARTTIME + " INT8 not null, " +
            COLUMN_ENDTIME + " INT8 not null, " +
            COLUMN_REPSPERSESSION + " integer not null, " +
            COLUMN_REPSINTERVAL + " integer not null, " +
            COLUMN_SESSIONINTERVAL + " integer not null, " +
            COLUMN_ATTRACTORTIMES + " integer not null, "+
            COLUMN_STATE + " integer, " +
            COLUMN_ATTRACTORFILE + " text not null " +
            ");";




    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SqliteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data"
        );
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULES);
        onCreate(db);
    }

}

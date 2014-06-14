package com.genreparrot.database;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.Time;
import android.util.Log;

public class Schedule implements Parcelable {
    private long id;
    private String filename;
    private int volume;

    private long starttime;
    private long endtime;

    private int repspersession;
    private int repsinterval;
    private int sessioninterval;
    private int attractor;
    private int state;

    public Schedule() {

    }


    public Schedule(Schedule n) {
        this.setId(n.getId());
        this.setFilename(n.getFilename());
        this.setVolume(n.getVolume());
        this.setStarttime(n.getStarttime());
        this.setEndtime(n.getEndtime());
        this.setRepspersession(n.getRepspersession());
        this.setRepsinterval(n.getRepsinterval());
        this.setSessioninterval(n.getSessioninterval());
        this.setAttractor(n.getAttractor());
        this.setState(n.getState());
    }

    public static long timeStringToMillis(String value) {
        String[] parts = value.split(":");
        int hours = Integer.valueOf(parts[0]);
        int minutes = Integer.valueOf(parts[1]);

        Time time = new Time();
        time.setToNow();

        time.hour = hours;
        time.minute = minutes;
        return time.toMillis(true);
    }

    public static String timeMillisToString(long value) {
        Time time = new Time();
        time.setToNow();
        time.set(value);
        return String.format("%d:%02d", time.hour, time.minute);
    }

    public static Time timeMillisToObject(long value){
        Time t = new Time();
        t.setToNow();
        t.set(value);
        return t;
    }

    public void print() {
        Time t;
        Log.d("debug", "Id: "+ this.getId());
        Log.d("debug", "File: "+ this.getFilename());
        t = timeMillisToObject(this.getStarttime());
        Log.d("debug", "Start Time: " + t.hour + ":" + t.minute + ":" + t.second);
        t = timeMillisToObject(this.getEndtime());
        Log.d("debug", "End Time: " + t.hour + ":" + t.minute + ":" + t.second);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getStarttime() {
        return starttime;
    }

    public void setStarttime(long starttime) {
        this.starttime = starttime;
    }

    public long getEndtime() {
        return endtime;
    }

    public void setEndtime(long endtime) {
        this.endtime = endtime;
    }

    public int getRepspersession() {
        return repspersession;
    }

    public void setRepspersession(int repspersession) {
        this.repspersession = repspersession;
    }

    public int getRepsinterval() {
        return repsinterval;
    }

    public void setRepsinterval(int repsinterval) {
        this.repsinterval = repsinterval;
    }

    public int getSessioninterval() {
        return sessioninterval;
    }

    public void setSessioninterval(int sessioninterval) {
        this.sessioninterval = sessioninterval;
    }

    public int getAttractor() {
        return attractor;
    }

    public void setAttractor(int attractor) {
        this.attractor = attractor;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(filename);
        parcel.writeInt(volume);

        parcel.writeLong(starttime);
        parcel.writeLong(endtime);

        parcel.writeInt(repspersession);
        parcel.writeInt(repsinterval);
        parcel.writeInt(sessioninterval);
        parcel.writeInt(attractor);
        parcel.writeInt(state);
    }

    public Schedule(Parcel pc) {
        id = pc.readLong();
        filename = pc.readString();
        volume = pc.readInt();

        starttime = pc.readLong();
        endtime = pc.readLong();

        repspersession = pc.readInt();
        repsinterval = pc.readInt();
        sessioninterval = pc.readInt();
        attractor = pc.readInt();
        state = pc.readInt();
    }

    public static final Parcelable.Creator<Schedule> CREATOR = new Creator<Schedule>() {

        public Schedule createFromParcel(Parcel source) {

            return new Schedule(source);
        }

        public Schedule[] newArray(int size) {
            return new Schedule[size];
        }
    };
}
package org.tin.oop2_capstone;


public class ActivityLog {
    private String activity;
    private String timeStart;
    private int duration;

    public ActivityLog(String activity, String timeStart, int duration) {
        this.activity = activity;
        this.timeStart = timeStart;
        this.duration = duration;
    }

    public String getActivity() {
        return activity;
    }

    public String getTimeStart(){
        return timeStart;
    }

    public int getDuration() {
        return duration;
    }
}

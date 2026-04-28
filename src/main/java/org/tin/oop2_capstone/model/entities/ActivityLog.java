package org.tin.oop2_capstone.model.entities;

// todo:  change this to hold the Activity class instead of strings

public class ActivityLog {
    private Activity activity;
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

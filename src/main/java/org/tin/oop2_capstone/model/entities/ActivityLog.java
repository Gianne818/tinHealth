package org.tin.oop2_capstone.model.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds all activities of the user.
 * Data will come from logRepository, as it returns all Activities of the user
 * Data can also come from adding new activity. In this case, add to db via ActivityRepository
 */

public class ActivityLog {
    private List<Activity> activities;

    public ActivityLog(){
        this.activities = new ArrayList<>();
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void addActivity(Activity activity) {
        if(activity!=null) {
            activities.add(activity);
        }
    }
    public  void setActivities(List<Activity> activities) {
        this.activities = activities;
    }
}

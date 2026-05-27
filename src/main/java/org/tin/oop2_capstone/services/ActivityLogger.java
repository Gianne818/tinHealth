package org.tin.oop2_capstone.services;

import org.tin.oop2_capstone.database.repositories.ActivityRepository;
import org.tin.oop2_capstone.database.repositories.MealRepository;
import org.tin.oop2_capstone.database.repositories.UserRepository;
import org.tin.oop2_capstone.model.entities.Activity;
import org.tin.oop2_capstone.model.entities.ActivityLog;
import org.tin.oop2_capstone.model.entities.MealLog;
import org.tin.oop2_capstone.model.observer.ActivityLogObserver;
import org.tin.oop2_capstone.model.observer.MealLogObserver;

import java.util.ArrayList;
import java.util.List;

public class ActivityLogger extends Logger<ActivityLogObserver> {
    private static ActivityLogger instance;
    private ActivityLog activityLog;
    private final List<ActivityLogObserver> observers;
    private ActivityRepository activityRepository;
    private int userID;

    private Activity activityToAdd;

    private ActivityLogger() {
        this.activityLog = new ActivityLog();
        this.observers = new ArrayList<>();
        this.userID = SessionManager.getInstance().getCurrentUser().getUid();
        this.activityRepository = DependencyService.getActivityRepository();
    }

    public static synchronized ActivityLogger getInstance() {
        if (instance == null) {
            instance = new ActivityLogger();
        }
        return instance;
    }

    @Override
    public boolean isValid() {
        return activityLog != null;
    }

    public boolean addActivity(Activity activity){
        this.activityToAdd = activity;
        return logData();
    }

    @Override
    public boolean saveToDB() {
        if(activityRepository.addActivity(activityToAdd, userID)){
            activityToAdd = null;
            return true;
        }
        return false;
    }

    @Override
    public void notifyObservers() {
        for (ActivityLogObserver activityLogObserver : observers) {
            activityLogObserver.onActivityLogChanged();
        }
    }

    public boolean deleteActivityLog(int activityId){
        if(activityRepository.deleteActivity(activityId)){
            notifyObservers();
            return true;
        }
        return false;
    }

    public void addObserver(ActivityLogObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(ActivityLogObserver observer) {
        observers.remove(observer);
    }

    public ActivityLog getActivityLog() {
        return activityLog;
    }

    public void setActivityLog(ActivityLog activityLog) {
        this.activityLog = activityLog;
    }

}

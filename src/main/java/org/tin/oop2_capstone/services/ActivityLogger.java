package org.tin.oop2_capstone.services;

import org.tin.oop2_capstone.model.entities.ActivityLog;
import org.tin.oop2_capstone.model.observer.LogObserver;

import java.util.ArrayList;
import java.util.List;

public class ActivityLogger extends Logger {
    private static ActivityLogger instance;
    private ActivityLog activityLog;
    private final List<LogObserver> observers;

    private ActivityLogger() {
        this.activityLog = new ActivityLog();
        this.observers = new ArrayList<>();
    }

    public static synchronized ActivityLogger getInstance() {
        if (instance == null) {
            instance = new ActivityLogger();
        }
        return instance;
    }

    @Override
    public boolean isValid() {
        return activityLog != null && !activityLog.getActivities().isEmpty();
    }

    @Override
    public void saveToDB() {
        // wala pay database
    }

    @Override
    public void notifyObservers() {
        for (LogObserver observer : observers) {
            // log observer wala pay methods sooo...
            // observer.notify(activityLog);
        }
    }

    public void addObserver(LogObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(LogObserver observer) {
        observers.remove(observer);
    }

    public ActivityLog getActivityLog() {
        return activityLog;
    }

    public void setActivityLog(ActivityLog activityLog) {
        this.activityLog = activityLog;
    }
}

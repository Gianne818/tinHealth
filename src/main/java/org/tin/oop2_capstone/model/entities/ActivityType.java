package org.tin.oop2_capstone.model.entities;

public class ActivityType {
    private int activityTypeID;
    private String name;
    private final String defaultUnit = "mins";
    private double metValue;

    public ActivityType(int activityTypeID, String name, double metValue) {
        this.activityTypeID = activityTypeID;
        this.name = name;
        this.metValue = metValue;
    }

    public int getActivityTypeID() {
        return activityTypeID;
    }

    public ActivityType setActivityTypeID(int activityTypeID) {
        this.activityTypeID = activityTypeID;
        return this;
    }

    public String getName() {
        return name;
    }

    public ActivityType setName(String name) {
        this.name = name;
        return this;
    }

    public String getDefaultUnit() {
        return defaultUnit;
    }

    public double getMetValue() {
        return metValue;
    }

    public ActivityType setMetValue(double metValue) {
        this.metValue = metValue;
        return this;
    }
}

package org.tin.oop2_capstone.model.entities;

import java.time.LocalDateTime;

public class Activity {
    private ActivityType activityType;
    private LocalDateTime logDateTime;
    private String unit;
    private double quantity;
    private double calories;
    private int activityId;

    // todo: remove calorie from parameter and calculate based on intensity and user info
    // todo: remove String name parameter on constructor and field and replace with activityType.
    // todo: when db is implemented, we just getActivityType().getName() to display in UI.

    private String name;

    // This no-arg constructor is important because:
    // Required for reflection and frameworks (like ORMs or serializers) to instantiate the object dynamically.
    // And to be used also in ActivityLogController: Activity newActivity = new Activity();
    public Activity() {}

    public Activity(ActivityType activityType, LocalDateTime logTime, String unit, double quantity, double calories) {
        this.activityType = activityType;
        this.logDateTime = logTime;
        this.unit = unit;
        this.quantity = quantity;
        this.calories = calories;
    }

    // Setters
    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public void setLogDateTime(LocalDateTime logDateTime) {
        this.logDateTime = logDateTime;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getters
    public int getActivityId() {
        return activityId;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getLogDateTime() {
        return logDateTime;
    }

    public String getUnit() {
        return unit;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getCalories() {
        return calories;
    }
}

package org.tin.oop2_capstone.model.entities;

import java.time.LocalDateTime;

public class Activity {
    private ActivityType activityType;
    private LocalDateTime logDateTime;
    private String unit;
    private double quantity;
    private double calories;

    // todo: remove calorie from parameter and calculate based on intensity and user info
    // todo: remove String name parameter on constructor and field and replace with activityType.
    // todo: when db is implemented, we just getActivityType().getName() to display in UI.

    private String name;
    public Activity(ActivityType activityType, LocalDateTime logTime, String unit, double quantity, double calories) {
        this.activityType = activityType;
        this.logDateTime = logTime;
        this.unit = unit;
        this.quantity = quantity;
        this.calories = calories;
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

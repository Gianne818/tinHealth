package org.tin.oop2_capstone.model.entities;

import java.time.LocalTime;

public class Activity {
    private String name;
    private LocalTime logTime;
    private String unit;
    private double quantity;
    private double calories;

    public Activity(String name, LocalTime logTime, String unit, double quantity, double calories) {
        this.name = name;
        this.logTime = logTime;
        this.unit = unit;
        this.quantity = quantity;
        this.calories = calories;
    }


    public String getName() {
        return name;
    }

    public LocalTime getLogTime() {
        return logTime;
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

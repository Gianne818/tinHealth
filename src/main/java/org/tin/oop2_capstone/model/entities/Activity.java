package org.tin.oop2_capstone.model.entities;

import java.time.LocalDateTime;

public class Activity {
    private String name;
    private LocalDateTime logDateTime;
    private String unit;
    private double quantity;
    private double calories;
    private String intensity;

    //todo: remove calorie from parameter and calculate based on intensity and user info
    public Activity(String name, LocalDateTime logTime, String unit, double quantity, double calories, String intensity) {
        this.name = name;
        this.logDateTime = logTime;
        this.unit = unit;
        this.quantity = quantity;
        this.calories = calories;
        this.intensity = intensity;
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

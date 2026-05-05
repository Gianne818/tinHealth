package org.tin.oop2_capstone.model.entities;

public class ActivityType {
    private String name;
    private final String defaultUnit = "minutess";
    private double metValue;

    public ActivityType(String name, double metValue) {
        this.name = name;
        this.metValue = metValue;
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

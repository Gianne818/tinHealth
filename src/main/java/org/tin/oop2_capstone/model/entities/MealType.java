package org.tin.oop2_capstone.model.entities;

import java.time.LocalTime;

public enum MealType {
    BREAKFAST("Breakfast", 6, 9),
    LUNCH("Lunch", 12, 14),
    DINNER("Dinner", 18, 21),
    SNACK("Snack", 0, 23);

    private final String label;
    private final int defaultTimeStart;
    private final int defaultTimeEnd;


    MealType(String label, int defaultTimeStart, int defaultTimeEnd){
        this.label = label;
        this.defaultTimeStart = defaultTimeStart;
        this.defaultTimeEnd = defaultTimeEnd;
    }

    public String getDefaultTimeRange(){
        return LocalTime.of(defaultTimeStart, 0) + " - " + LocalTime.of(defaultTimeEnd, 0);
    }

    public LocalTime getDefaultTimeEnd() {
        return LocalTime.of(defaultTimeEnd, 0);
    }

    public LocalTime getDefaultTimeStart() {
        return LocalTime.of(defaultTimeStart, 0);
    }

    @Override
    public String toString() {
        return label;
    }

    public boolean isWithinRange(LocalTime time) {
        if (this == SNACK) return true;
        LocalTime start = getDefaultTimeStart();
        LocalTime end = LocalTime.of(defaultTimeEnd, 59); // Covers up to the end of the hour
        return !time.isBefore(start) && !time.isAfter(end);
    }


}



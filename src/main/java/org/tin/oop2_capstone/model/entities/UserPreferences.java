package org.tin.oop2_capstone.model.entities;

import java.io.Serializable;

public class UserPreferences implements Serializable {
    /** implements serializable to be added for future .ser files **/

    private static final long serialVersionUID = 1L;

    private boolean enableExercisePrompts;
    private int     exerciseIntensity;
    private int     promptFrequencyHours;
    private String  theme;
    private double  dailyCalorieGoal;
    private int weeklyActivityReps;

    public UserPreferences() {}

    public UserPreferences(boolean enableExercisePrompts, int exerciseIntensity, int promptFrequencyHours, String theme, double dailyCalorieGoal, int weeklyActivityReps) {
        this.enableExercisePrompts = enableExercisePrompts;
        this.exerciseIntensity = exerciseIntensity;
        this.promptFrequencyHours = promptFrequencyHours;
        this.theme = theme;
        this.dailyCalorieGoal = dailyCalorieGoal;
        this.weeklyActivityReps = weeklyActivityReps;
    }

    public boolean isEnableExercisePrompts() {
        return enableExercisePrompts;
    }

    public int getExerciseIntensity() {
        return exerciseIntensity;
    }
    public int getPromptFrequencyHours() {
        return promptFrequencyHours;
    }
    public String getTheme() {
        return theme;
    }
    public double getDailyCalorieGoal() {
        return dailyCalorieGoal;
    }
    public int getWeeklyActivityReps() {
        return weeklyActivityReps;
    }

    public void setEnableExercisePrompts(boolean enableExercisePrompts) {
        this.enableExercisePrompts = enableExercisePrompts;
    }

    public void setExerciseIntensity(int exerciseIntensity) {
        this.exerciseIntensity = exerciseIntensity;
    }
    public void setPromptFrequencyHours(int promptFrequencyHours) {
        this.promptFrequencyHours = promptFrequencyHours;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public void setDailyCalorieGoal(double dailyCalorieGoal) {
        this.dailyCalorieGoal = dailyCalorieGoal;
    }
    public void setWeeklyActivityReps(int weeklyActivityReps) {
        this.weeklyActivityReps = weeklyActivityReps;
    }
}

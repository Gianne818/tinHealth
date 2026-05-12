package org.tin.oop2_capstone.model.entities;

import java.io.Serializable;

public class UserPreferences implements Serializable {
    /** implements serializable to be added for future .ser files **/

    private static final long serialVersionUID = 1L;

    private boolean enableExercisePrompts;
    private int     exerciseIntensity;
    private int     promptFrequencyMinutes;
    private String  theme;
    private double  dailyCalorieGoalIn;
    private double dailyCalorieGoalOut;
    private boolean exerciseReminder;

    private boolean achievementNotifications;
    private int weeklyActivityReps;

    public UserPreferences() {}

    public UserPreferences(boolean enableExercisePrompts, int exerciseIntensity, int promptFrequencyMinutes, String theme, double In, int weeklyActivityReps) {
        this.enableExercisePrompts = enableExercisePrompts;
        this.exerciseIntensity = exerciseIntensity;
        this.promptFrequencyMinutes = promptFrequencyMinutes;
        this.theme = theme;
        this.dailyCalorieGoalIn = dailyCalorieGoalIn;
        this.weeklyActivityReps = weeklyActivityReps;
    }

    public boolean isEnableExercisePrompts() {
        return enableExercisePrompts;
    }

    public int getExerciseIntensity() {
        return exerciseIntensity;
    }
    public int getPromptFrequencyMinutes() {
        return promptFrequencyMinutes;
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
        this.promptFrequencyMinutes = promptFrequencyMinutes;
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

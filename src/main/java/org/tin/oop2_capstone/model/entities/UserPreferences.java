package org.tin.oop2_capstone.model.entities;

import java.io.Serializable;

public class UserPreferences implements Serializable {
    /** implements serializable to be added for future .ser files **/

    private int userPrefID;
    private String goalType;
    private double targetWeightKG;
    private boolean enableExercisePrompts;
    private int exerciseIntensity;
    private boolean exerciseReminders;
    private boolean mealReminders;
    private boolean achievementNotifications;
    private int promptFrequencyMins;
    private String theme;
    private double dailyCalorieIn;
    private double dailyCalorieOut;
    private int weeklyActivityReps;

    public UserPreferences() {}

    public UserPreferences(int userPrefID, String goalType, double targetWeightKG, boolean enableExercisePrompts, int exerciseIntensity, boolean exerciseReminders, boolean mealReminders, boolean achievementNotifications, int promptFrequencyMins, String theme, double dailyCalorieIn, double dailyCalorieOut) {
        this.userPrefID = userPrefID;
        this.goalType = goalType;
        this.targetWeightKG = targetWeightKG;
        this.enableExercisePrompts = enableExercisePrompts;
        this.exerciseIntensity = exerciseIntensity;
        this.exerciseReminders = exerciseReminders;
        this.mealReminders = mealReminders;
        this.achievementNotifications = achievementNotifications;
        this.promptFrequencyMins = promptFrequencyMins;
        this.theme = theme;
        this.dailyCalorieIn = dailyCalorieIn;
        this.dailyCalorieOut = dailyCalorieOut;
    }

    public int getUserPrefID() {
        return userPrefID;
    }

    public void setUserPrefID(int userPrefID) {
        this.userPrefID = userPrefID;
    }

    public String getGoalType() {
        return goalType;
    }

    public void setGoalType(String goalType) {
        this.goalType = goalType;
    }

    public double getTargetWeightKG() {
        return targetWeightKG;
    }

    public void setTargetWeightKG(double targetWeightKG) {
        this.targetWeightKG = targetWeightKG;
    }

    public boolean isEnableExercisePrompts() {
        return enableExercisePrompts;
    }

    public void setEnableExercisePrompts(boolean enableExercisePrompts) {
        this.enableExercisePrompts = enableExercisePrompts;
    }

    public int getExerciseIntensity() {
        return exerciseIntensity;
    }

    public void setExerciseIntensity(int exerciseIntensity) {
        this.exerciseIntensity = exerciseIntensity;
    }

    public boolean isExerciseReminders() {
        return exerciseReminders;
    }

    public void setExerciseReminders(boolean exerciseReminders) {
        this.exerciseReminders = exerciseReminders;
    }

    public boolean isMealReminders() {
        return mealReminders;
    }

    public void setMealReminders(boolean mealReminders) {
        this.mealReminders = mealReminders;
    }

    public boolean isAchievementNotifications() {
        return achievementNotifications;
    }

    public void setAchievementNotifications(boolean achievementNotifications) {
        this.achievementNotifications = achievementNotifications;
    }

    public int getPromptFrequencyMins() {
        return promptFrequencyMins;
    }

    public void setPromptFrequencyMins(int promptFrequencyMins) {
        this.promptFrequencyMins = promptFrequencyMins;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public double getDailyCalorieIn() {
        return dailyCalorieIn;
    }

    public void setDailyCalorieIn(double dailyCalorieIn) {
        this.dailyCalorieIn = dailyCalorieIn;
    }

    public double getDailyCalorieOut() {
        return dailyCalorieOut;
    }

    public void setDailyCalorieOut(double dailyCalorieOut) {
        this.dailyCalorieOut = dailyCalorieOut;
    }

    public int getWeeklyActivityReps() {
        return weeklyActivityReps;
    }

}

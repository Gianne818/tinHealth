package org.tin.oop2_capstone.model.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public void setWeeklyActivityReps(int weeklyActivityReps) {
        this.weeklyActivityReps = weeklyActivityReps;
    }

    public NutritionDetails getTargetMacros(User user) {
        double calories = dailyCalorieIn;
        String goal = goalType.toLowerCase();
        double proteinRatio, carbRatio, fatRatio, sugarPct;

        switch (goal) {
            case "lose weight":
                proteinRatio = 0.35; carbRatio = 0.35; fatRatio = 0.30;
                sugarPct = 0.05;
                break;
            case "gain muscle":
                proteinRatio = 0.40; carbRatio = 0.40; fatRatio = 0.20;
                sugarPct = 0.10;
                break;
            case "gain weight":
                proteinRatio = 0.25; carbRatio = 0.50; fatRatio = 0.25;
                sugarPct = 0.10;
                break;
            default: //maintain
                proteinRatio = 0.30; carbRatio = 0.40; fatRatio = 0.30;
                sugarPct = 0.10;
        }

        double proteinFloorPerKg;
        switch (goal) {
            case "gain muscle":
                proteinFloorPerKg = 1.8;
                break;
            case "gain weight":
                proteinFloorPerKg = 1.4;
                break;
            default: //maintain or lose weight
                proteinFloorPerKg = 1.2;
        }

        double proteinG = Math.max( (dailyCalorieIn * proteinRatio) / 4.0, user.getWeightKg() * proteinFloorPerKg);
        double carbG = (dailyCalorieIn * carbRatio) / 4.0;
        double fatG  = (dailyCalorieIn * fatRatio)  / 9.0;
        double sugarG = (dailyCalorieIn * sugarPct) / 4.0;
        double fiberG = (dailyCalorieIn / 1000.0) * 14.0;
        double sodiumMg = 2300;
        double cholesterolMg = 300;

        //round 2 decimal places
        calories = Math.round(calories * 100.0) / 100.0;
        proteinG = Math.round(proteinG * 100.0) / 100.0;
        carbG = Math.round(carbG * 100.0) / 100.0;
        fatG = Math.round(fatG * 100.0) / 100.0;
        sugarG = Math.round(sugarG * 100.0) / 100.0;
        fiberG = Math.round(fiberG * 100.0) / 100.0;


        return new NutritionDetails(calories, proteinG, fatG, carbG, cholesterolMg, sodiumMg, sugarG, fiberG);
    }

//    debugging shtuff
//    public String toString() {
//        return "UserPreferences{" +
//                "userPrefID=" + userPrefID +
//                ", goalType='" + goalType + '\'' +
//                ", targetWeightKG=" + targetWeightKG +
//                ", enableExercisePrompts=" + enableExercisePrompts +
//                ", exerciseIntensity=" + exerciseIntensity +
//                ", exerciseReminders=" + exerciseReminders +
//                ", mealReminders=" + mealReminders +
//                ", achievementNotifications=" + achievementNotifications +
//                ", promptFrequencyMins=" + promptFrequencyMins +
//                ", theme='" + theme + '\'' +
//                ", dailyCalorieIn=" + dailyCalorieIn +
//                ", dailyCalorieOut=" + dailyCalorieOut +
//                ", weeklyActivityReps=" + weeklyActivityReps +
//                '}';
//    }

}

package org.tin.oop2_capstone.services;

import org.tin.oop2_capstone.model.entities.User;

public class ExerciseDifficultyService {

    /**
     * This class should calculate the difficulty and hence the intensities of each exercise based user details like BMI and stuff
     * However user can override it with their own preferences, so perhaps just have some setters for this
     */

    public static final int EASY     = 0;
    public static final int MODERATE = 1;
    public static final int HARD     = 2;

    private static final double MULTIPLIER_EASY     = 0.75;
    private static final double MULTIPLIER_MODERATE = 1.00;
    private static final double MULTIPLIER_HARD     = 1.50;

    private static final double BMI_UNDERWEIGHT_MAX = 18.5;
    private static final double BMI_OVERWEIGHT_MIN  = 25.0;

    private int currentDiff;
    private boolean manualSet;

    public ExerciseDifficultyService() {
        this.currentDiff = MODERATE;
        this.manualSet = false;
    }

    public void calculateDifficulty(User user) {
        if (user == null || user.getHeightM() <= 0){
            return;
        }
        if (manualSet){
            return;
        }
        double bmi = computeBMI(user.getWeightKg(), user.getHeightM());
        currentDiff = diffFromBMI(bmi);
    }

    public void setDifficulty(int difficulty) {
        this.currentDiff = difficulty;
        this.manualSet = true;
    }

    public void clearOverride() {
        this.manualSet = false;
    }

    public int getDifficulty() {
        return currentDiff;
    }

    public int adjustReps(int baseReps) {
        return (int) Math.max(1, Math.round(baseReps* getMultiplierFor(currentDiff)));
    }

    private double computeBMI(double weightKg, double heightM) {
        return weightKg / (heightM * heightM);
    }

    private int diffFromBMI(double bmi) {
        if (bmi < BMI_UNDERWEIGHT_MAX){
            return EASY;
        }
        if (bmi < BMI_OVERWEIGHT_MIN){
            return MODERATE;
        }
        return HARD;
    }

    private double getMultiplierFor(int d) {
        if (d == EASY){
            return MULTIPLIER_EASY;
        }
        if (d == HARD){
            return MULTIPLIER_HARD;
        }
        return MULTIPLIER_MODERATE;
    }
}

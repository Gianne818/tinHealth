package org.tin.oop2_capstone.services;

import org.tin.oop2_capstone.model.entities.ActivityType;
import org.tin.oop2_capstone.model.entities.User;
import org.tin.oop2_capstone.model.entities.UserPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExerciseDifficultyService {

    /**
     * This class should calculate the difficulty and hence the intensities of each exercise based user details like BMI and stuff
     * However user can override it with their own preferences, so perhaps just have some setters for this
     */

    public final Map<String, Double> exerciseDifficulty;

    private static final double BMI_UNDERWEIGHT_MAX = 18.5;
    private static final double BMI_NORMAL_MAX = 25.0;
    private static final double BMI_OVERWEIGHT_MAX = 30.0;
    private static final int AGE_YOUNG = 25;
    private static final int AGE_ADULT = 45;
    private static final int AGE_SENIOR = 65;
    private static final double MET_LIGHT= 3.0;
    private static final double MET_MODERATE = 6.0;
    private static final double MET_HIGH = 9.0;


    private String currentDiff;
    private boolean manualSet;


    public ExerciseDifficultyService() {
        this.currentDiff = "MODERATE";
        this.manualSet = false;
        exerciseDifficulty = Map.of(
            "SEDENTARY", 1.2,
            "LIGHT", 1.375,
            "MODERATE", 1.55,
            "HARD", 1.725,
            "EXTREME", 1.9
        );
    }

    public void calculateDifficulty(User user) {
        if (user == null || user.getHeightCm() <= 0) {
            return;
        }

        if (manualSet) {
            return;
        }
        double bmi = computeBMI(user.getWeightKg(), user.getHeightCm());
        int age = user.getAge();

        currentDiff = determineBasedOnBMIAndAge(bmi, age);
    }

    public void setDifficulty(String difficulty) {
        if (exerciseDifficulty.containsKey(difficulty)) {
            this.currentDiff = difficulty;
            this.manualSet = true;
        }
    }

    public void clearOverride() {
        this.manualSet = false;
    }

    public String getDifficulty() {
        return currentDiff;
    }

    /* removed multiplier depending on exerciseDiff */
    public double adjustMins(double baseMins, int exerciseIntensity) {
        double multiplier = 1.0;
        switch(exerciseIntensity){
            case 1:
                multiplier = 1.0;
                break;
            case 2:
                multiplier = 1.3;
                break;
            case 3:
                multiplier = 1.6;
                break;
            case 4:
                multiplier = 2.0;
                break;
            case 5:
                multiplier = 2.5;
                break;
            case 6:
                multiplier = 3.0;
                break;
            case 7:
                multiplier = 3.5;
                break;
            case 8:
                multiplier = 4.0;
                break;
            case 9:
                multiplier = 4.5;
                break;
            case 10:
                multiplier = 5.0;
                break;
        }

        return baseMins * multiplier;
    }

    public int getExercisePromptFrequencyMins() {
        return switch (currentDiff) {
            case "SEDENTARY" -> 480;
            case "LIGHT" -> 360;
            case "MODERATE" -> 240;
            case "HARD" -> 180;
            case "EXTREME" -> 120;
            default -> 240;
        };
    }

    public double getCalorieGoalMultiplier() {
        return exerciseDifficulty.get(currentDiff);
    }


    public List<ActivityType> getAppropriateActivityTypes(List<ActivityType> allActivityTypes) {
        return switch (currentDiff) {
            case "SEDENTARY" -> filterActivitiesByMET(allActivityTypes, 0, MET_LIGHT);
            case "LIGHT" -> filterActivitiesByMET(allActivityTypes, MET_LIGHT, MET_MODERATE);
            case "MODERATE" -> filterActivitiesByMET(allActivityTypes, MET_LIGHT, MET_HIGH);
            case "HARD" -> filterActivitiesByMET(allActivityTypes, MET_MODERATE, MET_HIGH);
            case "EXTREME" -> filterActivitiesByMET(allActivityTypes, MET_HIGH, Double.MAX_VALUE);
            default -> allActivityTypes;
        };
    }

    /* mga helper methods for some calculations */
    private double computeBMI(double weightKg, double heightCm) {
        double heightM = heightCm / 100.0;
        return weightKg / (heightM * heightM);
    }

    private String determineBasedOnBMIAndAge(double bmi, int age) {
        String bmiCategory = getBMICategory(bmi);
        String ageCategory = getAgeCategory(age);

        //enhanced switch statement daw
        int bmiScore = switch (bmiCategory) {
            case "SEDENTARY" -> 1;
            case "LIGHT" -> 2;
            case "MODERATE" -> 3;
            case "HARD" -> 4;
            case "EXTREME" -> 5;
            default -> 3;
        };

        int ageScore = switch (ageCategory) {
            case "SEDENTARY" -> 1;
            case "LIGHT" -> 2;
            case "MODERATE" -> 3;
            case "HARD" -> 4;
            case "EXTREME" -> 5;
            default -> 3;
        };

        int averageScore = (bmiScore + ageScore) / 2;

        return switch (averageScore) {
            case 1 -> "SEDENTARY";
            case 2 -> "LIGHT";
            case 3 -> "MODERATE";
            case 4 -> "HARD";
            case 5 -> "EXTREME";
            default -> "MODERATE";
        };

    }

    private String getBMICategory(double bmi) {
        if (bmi < BMI_UNDERWEIGHT_MAX) {
            return "LIGHT";
        }
        if (bmi < BMI_NORMAL_MAX) {
            return "MODERATE";
        }
        if (bmi < BMI_OVERWEIGHT_MAX) {
            return "HARD";
        }
        return "SEDENTARY";
    }

    private String getAgeCategory(int age) {
        if (age < AGE_YOUNG) {
            return "EXTREME";
        }
        if (age < AGE_ADULT) {
            return "HARD";
        }
        if (age < AGE_SENIOR) {
            return "MODERATE";
        }
        return "LIGHT";
    }


    private List<ActivityType> filterActivitiesByMET(List<ActivityType> activities, double minMET, double maxMET) {
        List<ActivityType> result = new ArrayList<>();

        for (ActivityType a : activities) {
            if (a.getMetValue() >= minMET && a.getMetValue() < maxMET) {
                result.add(a);
            }
        }

        return result;
    }
}

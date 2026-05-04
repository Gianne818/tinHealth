package org.tin.oop2_capstone.database.repositories;

import org.tin.oop2_capstone.model.entities.UserPreferences;
import org.tin.oop2_capstone.services.SessionManager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class SettingsRepository {

    /* This checks if the user inputs (the user preferences) in the settings tab are valid. This also
    stores them in the database if everything is valid.
     */

    private static final int EXERCISE_INTENSITY_MIN = 1;
    private static final int EXERCISE_INTENSITY_MAX = 10;

    private static final int PROMPT_FREQUENCY_HOURS_MIN = 1;
    private static final int PROMPT_FREQUENCY_HOURS_MAX = 168;

    private static final double DAILY_CALORIE_GOAL_MIN = 500.0;
    private static final double DAILY_CALORIE_GOAL_MAX = 10000.0;

    private static final int WEEKLY_ACTIVITY_REPS_MIN = 0;
    private static final int WEEKLY_ACTIVITY_REPS_MAX = 1000;

    private static final String THEME_LIGHT = "light";
    private static final String THEME_DARK = "dark";

    public SettingsRepository() {
    }

    public boolean isExerciseIntensityValid(int intensity) {
        if(intensity >= EXERCISE_INTENSITY_MIN && intensity <= EXERCISE_INTENSITY_MAX) {
            return true;
        }
        return false;
    }

    public boolean isPromptFrequencyHoursValid(int hours) {
        return hours >= PROMPT_FREQUENCY_HOURS_MIN && hours <= PROMPT_FREQUENCY_HOURS_MAX;
    }

    public boolean isThemeValid(String theme) {
        if (theme == null) {
            return false;
        }
        return theme.equalsIgnoreCase(THEME_LIGHT) || theme.equalsIgnoreCase(THEME_DARK);
    }

    public boolean isDailyCalorieGoalValid(double goal) {
        return goal >= DAILY_CALORIE_GOAL_MIN && goal <= DAILY_CALORIE_GOAL_MAX;
    }

    public boolean isWeeklyActivityRepsValid(int reps) {
        return reps >= WEEKLY_ACTIVITY_REPS_MIN && reps <= WEEKLY_ACTIVITY_REPS_MAX;
    }

    public boolean areUserPreferencesValid(UserPreferences preferences) {
        if (preferences == null) {
            return false;
        }

        boolean isExerciseIntensityValid = isExerciseIntensityValid(preferences.getExerciseIntensity());
        boolean isPromptFrequencyValid = isPromptFrequencyHoursValid(preferences.getPromptFrequencyHours());
        boolean isThemeValid = isThemeValid(preferences.getTheme());
        boolean isDailyCalorieGoalValid = isDailyCalorieGoalValid(preferences.getDailyCalorieGoal());
        boolean isWeeklyActivityRepsValid = isWeeklyActivityRepsValid(preferences.getWeeklyActivityReps());

        return isExerciseIntensityValid
                && isPromptFrequencyValid
                && isThemeValid
                && isDailyCalorieGoalValid
                && isWeeklyActivityRepsValid;
    }

    public boolean save(UserPreferences preferences) {
        if (!areUserPreferencesValid(preferences)) {
            return false;
        }

        storeUserPreferences(preferences);
        return true;
    }
    public UserPreferences load() {
        return SessionManager.getInstance().getCurrentUserPrefs();
    }

    private void storeUserPreferences(UserPreferences preferences) {
        SessionManager.getInstance().setCurrentUserPrefs(preferences);
    }
}

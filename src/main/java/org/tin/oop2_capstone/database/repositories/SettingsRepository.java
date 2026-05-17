package org.tin.oop2_capstone.database.repositories;

import org.tin.oop2_capstone.database.RetrieveData;
import org.tin.oop2_capstone.database.UpdateData;
import org.tin.oop2_capstone.model.entities.UserPreferences;
import org.tin.oop2_capstone.services.SessionManager;

public class SettingsRepository {

    /* This checks if the user inputs (the user preferences) in the settings tab are valid. This also
    stores them in the database if everything is valid.
     */
    public static volatile SettingsRepository instance;

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

    private SettingsRepository(){
        System.out.println("SettingsRepository is initialized for the first time.");
    }

    public static SettingsRepository getInstance(){
        if(instance == null){
            synchronized (SettingsRepository.class){
                if(instance == null){
                    return instance = new SettingsRepository();
                }
            }
        }
        return instance;
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

        boolean isPromptFrequencyValid = isPromptFrequencyHoursValid(preferences.getPromptFrequencyMins());
        boolean isThemeValid = isThemeValid(preferences.getTheme());
        boolean isDailyCalorieGoalInValid = isDailyCalorieGoalValid(preferences.getDailyCalorieOut());
        boolean isDailyCalorieGoalOutValid = isDailyCalorieGoalValid(preferences.getDailyCalorieOut());

        return isPromptFrequencyValid
                && isThemeValid
                && isDailyCalorieGoalInValid
                && isDailyCalorieGoalOutValid;
    }

    public boolean save(UserPreferences preferences) {
        if (!areUserPreferencesValid(preferences)) {
            return false;
        }

        storeUserPreferences(preferences);
        return true;
    }

    //Overload save for SettingsController compatibility
    public boolean save(UserPreferences preferences, int userId) {
        if (!areUserPreferencesValid(preferences)) {
            return false;
        }

        boolean dbSuccess = UpdateData.updateUserPreferences(userId, preferences);
        if (!dbSuccess) {
            return false;
        }

        // Sync session so every other controller sees the new values immediately
        SessionManager.getInstance().setCurrentUserPrefs(preferences);
        return true;
    }

    public UserPreferences load() {
        return SessionManager.getInstance().getCurrentUserPrefs();
    }

    private void storeUserPreferences(UserPreferences preferences) {
        SessionManager.getInstance().setCurrentUserPrefs(preferences);
    }
}

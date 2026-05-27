package org.tin.oop2_capstone.database.repositories;

import org.tin.oop2_capstone.database.RetrieveData;
import org.tin.oop2_capstone.database.UpdateData;
import org.tin.oop2_capstone.database.data_sources.UserPrefDataSource;
import org.tin.oop2_capstone.model.entities.Activity;
import org.tin.oop2_capstone.model.entities.ActivityType;
import org.tin.oop2_capstone.model.entities.UserPreferences;
import org.tin.oop2_capstone.services.SessionManager;

import java.util.List;

public class UserPrefRepository {

    /* This checks if the user inputs (the user preferences) in the settings tab are valid. This also
    stores them in the database if everything is valid.
     */

    private UserPreferences userPreferences;
    private final UserPrefDataSource userPrefDataSource;

    public UserPrefRepository(UserPrefDataSource userPrefDataSource){
        this.userPrefDataSource = userPrefDataSource;
    }

    //Overload save for SettingsController compatibility
    public boolean save(UserPreferences preferences, int userId) {
        return userPrefDataSource.save(preferences, userId);
    }

    public UserPreferences load() {
        return SessionManager.getInstance().getCurrentUserPrefs();
    }

    public int getDailyCalorieInGoal(int userId) {
        return userPrefDataSource.fetchUserDailyCalorieInGoal(userId);
    }


    public int getPromptFrequency(int userId){
        return userPrefDataSource.fetchUserPromptFrequency(userId);
    }

    /* im just adding this */
    public int getExerciseIntensity(int userId){
        return userPrefDataSource.fetchUserPreferences(userId).getExerciseIntensity();
    }

    public void fetchUserPrefs(int userId){
        this.userPreferences = userPrefDataSource.fetchUserPreferences(userId);
    }

    public UserPreferences getUserPreferences() {
        return userPreferences;
    }


}

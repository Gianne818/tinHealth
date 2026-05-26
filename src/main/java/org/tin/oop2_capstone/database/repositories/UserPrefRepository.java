package org.tin.oop2_capstone.database.repositories;

import org.tin.oop2_capstone.database.RetrieveData;
import org.tin.oop2_capstone.database.UpdateData;
import org.tin.oop2_capstone.database.data_sources.UserPrefDataSource;
import org.tin.oop2_capstone.model.entities.UserPreferences;
import org.tin.oop2_capstone.services.SessionManager;

public class UserPrefRepository {

    /* This checks if the user inputs (the user preferences) in the settings tab are valid. This also
    stores them in the database if everything is valid.
     */

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
}

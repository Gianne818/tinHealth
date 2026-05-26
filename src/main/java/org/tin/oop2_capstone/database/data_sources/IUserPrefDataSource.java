package org.tin.oop2_capstone.database.data_sources;

import org.tin.oop2_capstone.model.entities.UserPreferences;

public interface IUserPrefDataSource {
    boolean save(UserPreferences userPreferences, int userId);
    int fetchUserDailyCalorieInGoal(int userId);
    int fetchUserPromptFrequency(int userId);

}

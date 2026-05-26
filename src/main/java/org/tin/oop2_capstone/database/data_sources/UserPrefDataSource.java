package org.tin.oop2_capstone.database.data_sources;

import org.tin.oop2_capstone.database.RetrieveData;
import org.tin.oop2_capstone.database.UpdateData;
import org.tin.oop2_capstone.model.entities.UserPreferences;
import org.tin.oop2_capstone.services.SessionManager;

import java.util.Set;

public class UserPrefDataSource implements IUserPrefDataSource{
    @Override
    public boolean save(UserPreferences userPreferences, int userId) {
        if(UpdateData.updateUserPreferences(userId, userPreferences)){
            SessionManager.getInstance().setCurrentUserPrefs(userPreferences);
            return true;
        }
        return false;
    }

    @Override
    public int fetchUserDailyCalorieInGoal(int userId) {
        return RetrieveData.fetchUserDailyCalorieInGoal(userId);
    }

    @Override
    public int fetchUserPromptFrequency(int userId) {
        return RetrieveData.fetchUserPromptFrequency(userId);
    }
}

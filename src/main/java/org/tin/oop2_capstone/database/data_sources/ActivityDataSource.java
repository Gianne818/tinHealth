package org.tin.oop2_capstone.database.data_sources;

import org.tin.oop2_capstone.database.InsertData;
import org.tin.oop2_capstone.database.RetrieveData;
import org.tin.oop2_capstone.model.entities.Activity;
import org.tin.oop2_capstone.model.entities.ActivityType;
import org.tin.oop2_capstone.model.entities.NutritionDetails;

import java.util.List;
import java.util.Map;

public class ActivityDataSource implements IActivityDataSource{
    @Override
    public List<Activity> fetchUserActivities(int userId) {
        return RetrieveData.fetchUserActivities(userId);
    }

    @Override
    public NutritionDetails getWeeklyNutrients(int userId) {
        return  RetrieveData.fetchUserWeeklyNutrients(userId);
    }

    @Override
    public Map<String, Double[]> getWeeklyCalories(int userId) {
        return RetrieveData.fetchUserWeeklyCalories(userId);
    }

    @Override
    public double getTodayCaloriesOut(int userId) {
        return RetrieveData.fetchUserTodayCaloriesOut(userId);
    }

    @Override
    public int getCurrentStreak(int userId) {
        return RetrieveData.fetchUserCurrentStreak(userId);
    }

    @Override
    public int getTodayActivitiesCount(int userId) {
        return RetrieveData.fetchUserTodayActivities(userId).size();
    }

    @Override
    public List<Activity> getTodayActivities(int userId){ return RetrieveData.fetchUserTodayActivities(userId);}

    @Override
    public boolean insertActivity(int userId, Activity activity) {
        return InsertData.insertActivity(userId, activity.getActivityId(), activity.getQuantity(), activity.getCalories());
    }

    @Override
    public int getTotalActivitiesCount(int userId) {
        return RetrieveData.fetchUserTotalActivities(userId);
    }

    @Override
    public int getWeeklyWorkoutCount(int userId) {
        return RetrieveData.fetchUserWeeklyWorkout(userId);
    }

    @Override
    public double getUserCurrentUserWeight(int userId) {
        return RetrieveData.fetchUserLatestWeight(userId);
    }

    @Override
    public List<ActivityType> fetchActivityTypes() {
        return RetrieveData.fetchActivityTypes();
    }

}

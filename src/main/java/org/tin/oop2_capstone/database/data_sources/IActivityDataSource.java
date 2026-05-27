package org.tin.oop2_capstone.database.data_sources;

import org.tin.oop2_capstone.model.entities.Activity;
import org.tin.oop2_capstone.model.entities.ActivityType;
import org.tin.oop2_capstone.model.entities.NutritionDetails;

import java.util.List;
import java.util.Map;

public interface IActivityDataSource {
    List<Activity> fetchUserActivities(int userId);
    NutritionDetails getWeeklyNutrients(int userId);
    Map<String, Double[]> getWeeklyCalories(int userId);
    double getTodayCaloriesOut(int userId);
    int getCurrentStreak(int userId);
    int getTodayActivitiesCount(int userId);
    List<Activity> getTodayActivities(int userId);
    boolean insertActivity(int userId, Activity activity);
    boolean deleteActivity(int activityId);
    double getUserCurrentUserWeight(int userId);
    int getWeeklyWorkoutCount(int userId);
    int getTotalActivitiesCount(int userId);
    List<ActivityType> fetchActivityTypes();
}

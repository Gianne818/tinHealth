package org.tin.oop2_capstone.database.repositories;

import org.tin.oop2_capstone.database.data_sources.ActivityDataSource;
import org.tin.oop2_capstone.model.entities.Activity;
import org.tin.oop2_capstone.model.entities.NutritionDetails;

import java.util.*;

public class ActivityRepository {
    /**
     * Validate before inserting into database the activity.
     */

    private List<Activity> userActivities;
    private final ActivityDataSource activityDataSource;

    public ActivityRepository(ActivityDataSource dataSource) {
        this.activityDataSource = dataSource;
    }

    public void fetchInitialActivityData(int userId) {
        this.userActivities = activityDataSource.fetchUserActivities(userId);
    }

    public List<Activity> getUserActivities() {
        return userActivities;
    }

    public NutritionDetails getWeeklyNutrients(int userId) {
        return activityDataSource.getWeeklyNutrients(userId);
    }

    public Map<String, Double[]> getWeeklyCalories(int userId) {
        return activityDataSource.getWeeklyCalories(userId);
    }

    public double getTodayCaloriesOut(int userId) {
        return activityDataSource.getTodayCaloriesOut(userId);
    }

    public int getCurrentStreak(int userId) {
        return activityDataSource.getCurrentStreak(userId);
    }

    public int getTodayActivitiesCount(int userId) {
        return activityDataSource.getTodayActivitiesCount(userId);
    }

    public boolean addActivity(Activity activity, int userId) {
        if (activityDataSource.insertActivity(userId, activity)) {
            if (userActivities != null) {
                userActivities.add(0, activity);
            } else {
                userActivities = new ArrayList<>();
                userActivities.add(activity);
            }
            return true;
        }
        return false;
    }

    public double getUserCurrentWeight(int userId) {
        return activityDataSource.getUserCurrentUserWeight(userId);
    }

}

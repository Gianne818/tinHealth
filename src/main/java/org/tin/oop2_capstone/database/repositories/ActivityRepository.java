package org.tin.oop2_capstone.database.repositories;

import org.tin.oop2_capstone.database.RetrieveData;
import org.tin.oop2_capstone.model.entities.Activity;
import org.tin.oop2_capstone.model.entities.ActivityType;
import org.tin.oop2_capstone.model.entities.NutritionDetails;
import java.util.*;


public class ActivityRepository {
    /**
     * Validate before inserting into database the activity.
     */

    private static volatile ActivityRepository instance;

    private int weeklyWorkoutCount;
    private int totalActivitiesCount;
    private int currentStreak;
    private int todayCaloriesOut;
    private int todayActivitiesCount;
    private NutritionDetails weeklyNutrients;
    private Map<String, Double[]> weeklyCalories;
    private List<Activity> todayActivities;
    private List<ActivityType> activityTypes;
    private List<Activity> userActivities;

    private ActivityRepository(){
        System.out.println("ActivityRepository is initialized for the first time.");
    }

    public static ActivityRepository getInstance(){
        if(instance == null) {
            synchronized (ActivityRepository.class) {
                if (instance == null) {
                    instance = new ActivityRepository();
                }
            }
        }
        return instance;
    }

    public void fetchInitialActivityData(int userId){
        this.weeklyWorkoutCount = RetrieveData.fetchUserWeeklyWorkout(userId);
        this.totalActivitiesCount = RetrieveData.fetchUserTotalActivities(userId);
        this.currentStreak = RetrieveData.fetchUserCurrentStreak(userId);
        this.todayCaloriesOut = RetrieveData.fetchUserTodayCaloriesOut(userId);
        this.todayActivitiesCount =  RetrieveData.fetchUserTodayActivities(userId).size();
        this.weeklyNutrients = RetrieveData.fetchUserWeeklyNutrients(userId);
        this.weeklyCalories = RetrieveData.fetchUserWeeklyCalories(userId);
        this.todayActivities = RetrieveData.fetchUserTodayActivities(userId);
        this.activityTypes = RetrieveData.fetchActivityTypes();
        this.userActivities = RetrieveData.fetchUserActivities(userId);
    }

    public int getWeeklyWorkoutCount() {
        return weeklyWorkoutCount;
    }

    public int getTotalActivitiesCount() {
        return totalActivitiesCount;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public int getTodayCaloriesOut() {
        return todayCaloriesOut;
    }

    public int getTodayActivitiesCount() {
        return todayActivitiesCount;
    }

    public NutritionDetails getWeeklyNutrients() {
        return weeklyNutrients;
    }

    public Map<String, Double[]> getWeeklyCalories() {
        return weeklyCalories;
    }

    public List<Activity> getTodayActivities() {
        return todayActivities;
    }

    public List<ActivityType> getActivityTypes() {
        return activityTypes;
    }

    public List<Activity> getUserActivities() {
        return userActivities;
    }

    //    public List<Map<String, Object>> getTodayFoodLogs(int userId) {
//        List<Map<String, Object>> logs = new ArrayList<>();
//
//        String query = """
//        SELECT m.meal_type, c.name, m.serving_size, m.serving_units,
//               nd.calories * m.serving_size AS total_calories,
//               TIME_FORMAT(m.log_timestamp, '%h:%i %p') AS log_time
//        FROM Meals m
//        JOIN Consumables c ON m.consumable_id = c.consumable_id
//        LEFT JOIN NutritionalDetails nd ON c.nutri_id = nd.nutri_id
//        WHERE m.user_id = ? AND DATE(m.log_timestamp) = CURDATE()
//        ORDER BY m.log_timestamp DESC
//        LIMIT 5
//        """;
//
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//            stmt.setInt(1, userId);
//            ResultSet rs = stmt.executeQuery();
//
//            while (rs.next()) {
//                Map<String, Object> log = new HashMap<>();
//                log.put("meal_type", rs.getString("meal_type"));
//                log.put("name", rs.getString("name"));
//                log.put("total_calories", rs.getInt("total_calories"));
//                log.put("log_time", rs.getString("log_time"));
//                logs.add(log);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return logs;
//    }
}

package org.tin.oop2_capstone.database.repositories;

import org.tin.oop2_capstone.database.RetrieveData;
import org.tin.oop2_capstone.model.entities.Activity;
import org.tin.oop2_capstone.model.entities.NutritionDetails;
import java.util.*;


public class ActivityRepository {
    /**
     * Validate before inserting into database the activity.
     */
    public static int getWeeklyWorkoutCount(int userId) {
        return RetrieveData.fetchUserWeeklyWorkout(userId);
    }

    public static int getTotalActivitiesCount(int userId) {
        return RetrieveData.fetchUserTotalActivities(userId);
    }

    public static int getCurrentStreak(int userId) {
        return RetrieveData.fetchUserCurrentStreak(userId);
    }

    public static int getTodayCaloriesOut(int userId) {
        return RetrieveData.fetchUserTodayCaloriesOut(userId);
    }


    public static NutritionDetails getWeeklyNutrients(int userId) {
        return RetrieveData.fetchUserWeeklyNutrients(userId);
    }

    public static Map<String, Double[]> getWeeklyCalories(int userId) {
        return RetrieveData.fetchUserWeeklyCalories(userId);
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

    public static List<Activity> getTodayActivities(int userId) {
       return RetrieveData.fetchUserTodayActivities(userId);
    }

    public static int getTodayActivitiesCount(int userId) {
        return  RetrieveData.fetchUserTodayActivities(userId).size();
    }
}

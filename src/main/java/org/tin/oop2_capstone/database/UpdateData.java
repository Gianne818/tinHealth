package org.tin.oop2_capstone.database;

import org.tin.oop2_capstone.model.entities.NutritionDetails;
import org.tin.oop2_capstone.model.entities.UserPreferences;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateData {


    public static void updateFoodNutrition(String foodName, NutritionDetails nutrition) {
        String query = """
            UPDATE NutritionalDetails nd
            JOIN Consumables c ON c.nutri_id = nd.nutri_id
            SET nd.calories     = ?,
                nd.protein      = ?,
                nd.fats         = ?,
                nd.carbs        = ?,
                nd.cholesterol  = ?,
                nd.sodium       = ?,
                nd.sugar        = ?,
                nd.fiber        = ?
            WHERE c.name = ?
            AND c.type   = 'food'
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setDouble(1, nutrition.getCalories());
            stmt.setDouble(2, nutrition.getProtein());
            stmt.setDouble(3, nutrition.getFat());
            stmt.setDouble(4, nutrition.getCarbs());
            stmt.setDouble(5, nutrition.getCholesterol());
            stmt.setDouble(6, nutrition.getSodium());
            stmt.setDouble(7, nutrition.getSugar());
            stmt.setDouble(8, nutrition.getFiber());
            stmt.setString(9, foodName);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void markAsSynced(String foodName) {
        String query = """
            UPDATE Consumables
            SET is_pending = TRUE
            WHERE name = ?
            AND type = 'food'
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, foodName);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public static boolean updateUserPreferences(int userId, UserPreferences prefs) {
        String query = """
            UPDATE UserPrefs SET
                enable_exercise_prompts = ?,
                prompt_freq = ?,
                theme = ?,
                exercise_reminders = ?,
                meal_reminders = ?,
                achievement_notifications = ?,
                target_weight_kg = ?,
                daily_calorie_in = ?,
                daily_calorie_out = ?,
                weekly_activity_goal = ?,
                exercise_intensity = ?
            WHERE user_id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setBoolean(1, prefs.isEnableExercisePrompts());
            stmt.setInt(2, prefs.getPromptFrequencyMins());
            stmt.setString(3, prefs.getTheme());
            stmt.setBoolean(4, prefs.isExerciseReminders());
            stmt.setBoolean(5, prefs.isMealReminders());
            stmt.setBoolean(6, prefs.isAchievementNotifications());
            stmt.setDouble(7, prefs.getTargetWeightKG());
            stmt.setDouble(8, prefs.getDailyCalorieIn());
            stmt.setDouble(9, prefs.getDailyCalorieOut());
            stmt.setInt(10, prefs.getWeeklyActivityReps());
            stmt.setInt(11, prefs.getExerciseIntensity());
            stmt.setInt(12, userId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1;

        } catch (SQLException e) {
            System.out.println("Error updating user preferences: " + e.getMessage());
            return false;
        }
    }

}

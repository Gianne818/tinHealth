package org.tin.oop2_capstone.database.repositories;

import org.tin.oop2_capstone.database.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;


public class ActivityRepository {
    /**
     * Validate before inserting into database the activity.
     */
    public int getWeeklyWorkoutCount(int userId) {
        String query = """
            SELECT COUNT(*) AS workout_count
            FROM Activities
            WHERE user_id = ?
            AND YEARWEEK(log_timestamp, 1) = YEARWEEK(CURDATE(), 1)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("workout_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalActivitiesCount(int userId) {
        String query = "SELECT COUNT(*) AS total_count FROM Activities WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getCurrentStreak(int userId) {
        String query = """
            SELECT DISTINCT DATE(log_timestamp) AS activity_date
            FROM Activities
            WHERE user_id = ?
            ORDER BY activity_date DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            Set<LocalDate> uniqueDates = new LinkedHashSet<>();
            while (rs.next()) {
                uniqueDates.add(rs.getDate("activity_date").toLocalDate());
            }

            int streak = 0;
            if (!uniqueDates.isEmpty()) {
                LocalDate today = LocalDate.now();
                LocalDate expected = today;

                if (uniqueDates.contains(today)) {
                    for (LocalDate date : uniqueDates) {
                        if (date.equals(expected)) {
                            streak++;
                            expected = expected.minusDays(1);
                        } else if (date.isBefore(expected)) {
                            break;
                        }
                    }
                }
            }
            return streak;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getDailyCaloriesOut(int userId) {
        String query = """
        SELECT COALESCE(SUM(calories), 0) AS total_calories
        FROM Activities
        WHERE user_id = ?
        AND DATE(log_timestamp) = CURDATE()
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return (int) rs.getDouble("total_calories");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static class MacroData {
        public double protein;
        public double carbs;
        public double fats;

        public MacroData(double protein, double carbs, double fats) {
            this.protein = protein;
            this.carbs = carbs;
            this.fats = fats;
        }
    }

    public MacroData getWeeklyMacros(int userId) {
        String query = """
        SELECT 
            COALESCE(SUM(nd.protein * m.serving_size), 0) AS total_protein,
            COALESCE(SUM(nd.carbs * m.serving_size), 0) AS total_carbs,
            COALESCE(SUM(nd.fats * m.serving_size), 0) AS total_fats
        FROM Meals m
        JOIN Consumables c ON m.consumable_id = c.consumable_id
        LEFT JOIN NutritionalDetails nd ON c.nutri_id = nd.nutri_id
        WHERE m.user_id = ?
        AND YEARWEEK(m.log_timestamp, 1) = YEARWEEK(CURDATE(), 1)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new MacroData (
                        rs.getDouble("total_protein"),
                        rs.getDouble("total_carbs"),
                        rs.getDouble("total_fats")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, Double[]> getWeeklyCalories(int userId) {
        Map<String, Double[]> weeklyData = new HashMap<>();

        String query = """
        SELECT 
            DAYOFWEEK(m.log_timestamp) AS day,
            COALESCE(SUM(nd.calories * m.serving_size), 0) AS calories_in,
            COALESCE((SELECT SUM(calories) FROM Activities WHERE user_id = ? AND DAYOFWEEK(log_timestamp) = day), 0) AS calories_out
        FROM Meals m
        JOIN Consumables c ON m.consumable_id = c.consumable_id
        LEFT JOIN NutritionalDetails nd ON c.nutri_id = nd.nutri_id
        WHERE m.user_id = ? AND YEARWEEK(m.log_timestamp, 1) = YEARWEEK(CURDATE(), 1)
        GROUP BY DAYOFWEEK(m.log_timestamp)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int day = rs.getInt("day");
                double caloriesIn = rs.getDouble("calories_in");
                double caloriesOut = rs.getDouble("calories_out");

                String dayName = getDayName(day);
                weeklyData.put(dayName, new Double[]{caloriesIn, caloriesOut});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Fill missing days with zeros
        String[] allDays = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (String day : allDays) {
            weeklyData.putIfAbsent(day, new Double[]{0.0, 0.0});
        }

        return weeklyData;
    }

    private String getDayName(int dayOfWeek) {
        switch (dayOfWeek) {
            case 2: return "Mon";
            case 3: return "Tue";
            case 4: return "Wed";
            case 5: return "Thu";
            case 6: return "Fri";
            case 7: return "Sat";
            case 1: return "Sun";
            default: return "";
        }
    }
}

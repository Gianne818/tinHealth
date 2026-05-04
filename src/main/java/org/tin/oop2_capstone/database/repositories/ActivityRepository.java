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
}

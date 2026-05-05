package org.tin.oop2_capstone.database.repositories;

import org.tin.oop2_capstone.database.DatabaseConnection;
import java.sql.*;

public class UserPrefRepository {

    public int getDailyCalorieInGoal(int userId) {
        String query = "SELECT daily_calorie_in FROM UserPrefs WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("daily_calorie_in");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 2000;
    }
}
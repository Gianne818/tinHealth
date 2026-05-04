package org.tin.oop2_capstone.database.repositories;

import org.tin.oop2_capstone.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MealRepository {
    /**
     * Validate before inserting into database.
     * sometimes, the API returns a null, or maybe the user will input bad values
     */
    public double getDailyCalories(int userId) {
        String query = """
        SELECT COALESCE(SUM(
            CASE 
                WHEN c.type = 'food' THEN nd.calories * m.serving_size
                WHEN c.type = 'foodcombo' THEN (
                    SELECT COALESCE(SUM(nd2.calories * ci.quantity), 0)
                    FROM ComboItems ci
                    JOIN Consumables c2 ON ci.consumable_id = c2.consumable_id
                    LEFT JOIN NutritionalDetails nd2 ON c2.nutri_id = nd2.nutri_id
                    WHERE ci.combo_id = c.consumable_id
                ) * m.serving_size
                ELSE 0
            END
        ), 0) AS total_calories
        FROM Meals m
        JOIN Consumables c ON m.consumable_id = c.consumable_id
        LEFT JOIN NutritionalDetails nd ON c.nutri_id = nd.nutri_id
        WHERE m.user_id = ? AND DATE(m.log_timestamp) = CURDATE()
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total_calories");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
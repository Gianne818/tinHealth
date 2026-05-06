package org.tin.oop2_capstone.database;

import org.tin.oop2_capstone.model.entities.NutritionDetails;

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
            SET is_synced = TRUE
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


}

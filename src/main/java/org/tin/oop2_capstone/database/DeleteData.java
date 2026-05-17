package org.tin.oop2_capstone.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeleteData {

    public static boolean deleteActivity(int activityId) {
        String sql = "DELETE FROM Activities WHERE act_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, activityId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean deleteMeal(int mealId) {
        String getConsumableSql = "SELECT consumable_id FROM Meals WHERE meal_id = ?";
        String deleteMealSql = "DELETE FROM Meals WHERE meal_id = ?";
        String deleteConsumableSql = "DELETE FROM Consumables WHERE consumable_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            int consumableId = -1;

            // 1. Get the consumable ID before deleting the meal record
            try (PreparedStatement pstmtGet = conn.prepareStatement(getConsumableSql)) {
                pstmtGet.setInt(1, mealId);
                try (ResultSet rs = pstmtGet.executeQuery()) {
                    if (rs.next()) {
                        consumableId = rs.getInt("consumable_id");
                    }
                }
            }

            // 2. Delete the Meal record
            try (PreparedStatement pstmtMeal = conn.prepareStatement(deleteMealSql)) {
                pstmtMeal.setInt(1, mealId);
                int rowsAffected = pstmtMeal.executeUpdate();
                System.out.println("Rows affected by delete meal: " + rowsAffected);

                if (rowsAffected == 0) return false;
            }

            // 3. Clean up the Consumable record if no other meals are referencing it
            if (consumableId != -1) {
                try (PreparedStatement pstmtCons = conn.prepareStatement(deleteConsumableSql)) {
                    pstmtCons.setInt(1, consumableId);
                    pstmtCons.executeUpdate();
                } catch (SQLException e) {
                    System.out.println("Consumable shared by other logs; kept in database.");
                }
            }

            return true;

        } catch (SQLException e) {
            System.out.println("SQL ERROR IN DELETE MEAL:");
            e.printStackTrace();
        }
        return false;
    }


}
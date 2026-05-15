package org.tin.oop2_capstone.database;

import org.tin.oop2_capstone.model.entities.Consumable;
import org.tin.oop2_capstone.model.entities.Food;
import org.tin.oop2_capstone.model.entities.Meal;
import org.tin.oop2_capstone.model.entities.NutritionDetails;

import java.sql.*;
import java.time.LocalDateTime;

public class InsertData {

    /**
     * Insert a meal record into the database
     * @param userId The user ID
     * @param meal The meal object to insert
     * @return true if insertion was successful, false otherwise
     */
    public static boolean insertMeal(int userId, Meal meal) {
        String insertMealSQL = "INSERT INTO Meals (user_id, consumable_id, meal_type, serving_size, serving_units, log_timestamp) VALUES (?, ?, ?, ?, ?, ?)";

        // First, ensure the consumable exists
        int consumableId = insertOrGetConsumable(meal.getConsumable());
        if (consumableId == -1) {
            System.err.println("Failed to get consumable ID");
            return false;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertMealSQL, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, consumableId);
            pstmt.setString(3, meal.getMealType().toString());
            pstmt.setDouble(4, meal.getQuantity());
            pstmt.setString(5, meal.getUnit());
            pstmt.setTimestamp(6, Timestamp.valueOf(meal.getLogDateTime()));

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Meal inserted successfully");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error inserting meal: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Insert a new consumable (food or combo) into the database
     * @param consumable The consumable object to insert
     * @return The generated consumable_id, or -1 if insertion failed
     */
    public static int insertConsumable(Consumable consumable) {
        // For Food type, insert nutritional details
        int nutriId = -1;
        NutritionDetails nutrition = consumable.getNutrition();

        if (nutrition != null) {
            nutriId = insertNutritionalDetails(nutrition);
            if (nutriId == -1) {
                return -1;
            }
        }

        String insertConsumableSQL = "INSERT INTO Consumables (name, type, is_pending, nutri_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertConsumableSQL, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, consumable.getName());

            // Determine the type based on the class
            String type = consumable instanceof Food ? "food" : "foodcombo";
            pstmt.setString(2, type);
            pstmt.setBoolean(3, consumable.isPending());

            if (nutriId != -1) {
                pstmt.setInt(4, nutriId);
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error inserting consumable: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Insert nutritional details into the database
     * @param nutrition The nutrition object to insert
     * @return The generated nutri_id, or -1 if insertion failed
     */
    public static int insertNutritionalDetails(NutritionDetails nutrition) {
        if (nutrition == null) {
            return -1;
        }

        String insertNutritionSQL = "INSERT INTO NutritionalDetails (sodium, carbs, sugar, fiber, calories, cholesterol, protein, fats) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertNutritionSQL, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setObject(1, nutrition.getSodium(), Types.DECIMAL);
            pstmt.setObject(2, nutrition.getCarbs(), Types.DECIMAL);
            pstmt.setObject(3, nutrition.getSugar(), Types.DECIMAL);
            pstmt.setObject(4, nutrition.getFiber(), Types.DECIMAL);
            pstmt.setObject(5, nutrition.getCalories(), Types.DECIMAL);
            pstmt.setObject(6, nutrition.getCholesterol(), Types.DECIMAL);
            pstmt.setObject(7, nutrition.getProtein(), Types.DECIMAL);
            pstmt.setObject(8, nutrition.getFat(), Types.DECIMAL);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error inserting nutritional details: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Check if a consumable exists and return its ID, or insert it if it doesn't exist
     * @param consumable The consumable to check/insert
     * @return The consumable ID
     */
    public static int insertOrGetConsumable(Consumable consumable) {
        // First try to get existing consumable by name and type
        String type = consumable instanceof Food ? "food" : "foodcombo";
        String selectSQL = "SELECT consumable_id FROM Consumables WHERE name = ? AND type = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {

            pstmt.setString(1, consumable.getName());
            pstmt.setString(2, type);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int existingId = rs.getInt("consumable_id");
                System.out.println("Found existing consumable: " + consumable.getName() + " with ID: " + existingId);
                return existingId;
            }

        } catch (SQLException e) {
            System.err.println("Error checking existing consumable: " + e.getMessage());
        }

        // If not found, insert new consumable
        System.out.println("Inserting new consumable: " + consumable.getName());
        return insertConsumable(consumable);
    }


}
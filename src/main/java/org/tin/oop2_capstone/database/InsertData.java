package org.tin.oop2_capstone.database;

import org.tin.oop2_capstone.model.entities.Consumable;
import org.tin.oop2_capstone.model.entities.Food;
import org.tin.oop2_capstone.model.entities.Meal;
import org.tin.oop2_capstone.model.entities.NutritionDetails;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.NULL;

public class InsertData {


    public static boolean insertMeal(int userId, Meal meal) {
        String insertMealSQL = "INSERT INTO Meals (user_id, consumable_id, meal_type, serving_size, serving_units, log_timestamp) VALUES (?, ?, ?, ?, ?, ?)";

        int consumableId = insertOrGetConsumable(meal.getConsumable());
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertMealSQL, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, consumableId);
            pstmt.setString(3, meal.getMealType().toString());
            pstmt.setDouble(4, meal.getQuantity());
            pstmt.setString(5, meal.getUnit());
            pstmt.setTimestamp(6, Timestamp.valueOf(meal.getLogDateTime()));

            int affectedRows = pstmt.executeUpdate();

           return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int insertConsumable(Consumable consumable) {

        String insertConsumableSQL = "INSERT INTO Consumables (name, type, is_pending, nutri_id) VALUES (?, ?, ?, ?)";
        int generatedConsumableId = -1;
        String type = consumable instanceof Food ? "food" : "foodcombo";
        int nutriId = -1;
        if(type.equals("food")){
            nutriId = insertNutritionalDetails(consumable.getNutrition());
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertConsumableSQL, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, consumable.getName());
            pstmt.setString(2, type);
            pstmt.setBoolean(3, consumable.isPending());

            if(type.equals("foodcombo") || nutriId == -1){
                pstmt.setNull(4, NULL);
            } else {
                pstmt.setInt(4, nutriId);
            }

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet resultSet = pstmt.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        generatedConsumableId = resultSet.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }

        if (type.equals("foodcombo")) {
            List<Integer> foodIds = new ArrayList<>();
            List<Food> foods = consumable.getConsumables();

            for (Food f : foods) {
                foodIds.add(insertOrGetConsumable(f));
            }

            insertIntoComboItems(generatedConsumableId, foodIds);
        }
        return generatedConsumableId;
    }

    public static void insertIntoComboItems(int comboId, List<Integer> foodIds){
        String sql = "INSERT INTO ComboItems(combo_id, consumable_id, quantity) VALUES(?, ?, ?)";

        try(Connection conn = DatabaseConnection.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(sql)){

            for(int id : foodIds){
                preparedStatement.setInt(1, comboId);
                preparedStatement.setInt(2, id);
                preparedStatement.setDouble(3, 1.0);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

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
            e.printStackTrace();
        }
        return -1;
    }

    public static int insertOrGetConsumable(Consumable consumable) {
        String type = consumable instanceof Food ? "food" : "foodcombo";
        String selectSQL = "SELECT consumable_id FROM Consumables WHERE name = ? AND type = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {

            pstmt.setString(1, consumable.getName());
            pstmt.setString(2, type);

            try(ResultSet rs = pstmt.executeQuery()){
                if (rs.next()) {
                    int existingId = rs.getInt("consumable_id");
                    return existingId;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return insertConsumable(consumable);
    }


}
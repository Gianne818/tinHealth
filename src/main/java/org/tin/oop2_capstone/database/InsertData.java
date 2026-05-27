package org.tin.oop2_capstone.database;

import org.tin.oop2_capstone.model.entities.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.NULL;

public class InsertData {


    public static boolean insertMeal(int userId, Meal meal) {
        String insertMealSQL = "INSERT INTO Meals (user_id, consumable_id, meal_type, log_date, time) VALUES (?, ?, ?, ?, ?)";

        int consumableId = insertOrGetConsumable(meal.getConsumable());
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertMealSQL, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, consumableId);
            pstmt.setString(3, meal.getMealType().toString());
            pstmt.setDate(4, java.sql.Date.valueOf(meal.getLogDate())); // Today's date
            pstmt.setString(5, meal.getTime()); // The raw text "12:00 - 14:00"

            return pstmt.executeUpdate() > 0;

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
        String sql = "INSERT INTO ComboItems(combo_id, consumable_id) VALUES(?, ?)";

        try(Connection conn = DatabaseConnection.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(sql)){

            for(int id : foodIds){
                preparedStatement.setInt(1, comboId);
                preparedStatement.setInt(2, id);
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


        public static boolean insertActivity(int userId, int activityTypeId, double quantity, double calories) {
        String insertActivitySQL = "INSERT INTO Activities (user_id, activity_type_id, quantity, calories, time, log_date) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertActivitySQL)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, activityTypeId);
            pstmt.setDouble(3, quantity);
            pstmt.setDouble(4, calories);
            pstmt.setString(5, LocalTime.now().toString());
            pstmt.setDate(6, Date.valueOf(LocalDate.now()));

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static int insertUser(User user){
        if(user == null){
            return -1;
        }
        String sql = "INSERT INTO users (fullname, username, email, password_hash, age, date_of_birth, gender, weight_kg, height_cm, activity_level) VALUES " +
                            "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int insertedRows = 0;
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

            pstmt.setString(1, user.getFullname());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPasswordHashed());
            pstmt.setInt(5, user.getAge());
            pstmt.setObject(6, user.getDateOfBirth());
            pstmt.setString(7, user.getIsMale() ? "Male" : "Female");
            pstmt.setDouble(8, user.getWeightKg());
            pstmt.setDouble(9, user.getHeightCm());
            pstmt.setString(10, user.getActivityLevel());

            insertedRows = pstmt.executeUpdate();
            System.out.println(insertedRows + " row/s inserted in users table");
            if(insertedRows > 0){
                try(ResultSet rs = pstmt.getGeneratedKeys()){
                    if(rs.next()){
                        return rs.getInt(1); // Use column index for portability
                    }
                }
            }
        } catch(SQLException e){
            e.printStackTrace();
        } finally {
            System.out.println(insertedRows + " row/s inserted in users table");
        }
        return -1;
    }

    public static int insertUserPref(UserPreferences userPref, int user_id){
        if(userPref == null){
            return -1;
        }
        int insertedRows = 0;

        String sql = "INSERT INTO userprefs (user_id, goal, target_weight_kg, enable_exercise_prompts, prompt_freq, theme, exercise_reminders, meal_reminders, achievement_notifications, daily_calorie_in, daily_calorie_out, weekly_activity_goal, exercise_intensity) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

            pstmt.setInt(1, user_id);
            pstmt.setString(2, userPref.getGoalType());
            pstmt.setDouble(3, userPref.getTargetWeightKG());
            pstmt.setBoolean(4, userPref.isEnableExercisePrompts());
            pstmt.setInt(5, userPref.getPromptFrequencyMins());
            pstmt.setString(6, userPref.getTheme());
            pstmt.setBoolean(7, userPref.isExerciseReminders());
            pstmt.setBoolean(8, userPref.isMealReminders());
            pstmt.setBoolean(9, userPref.isAchievementNotifications());
            pstmt.setDouble(10, userPref.getDailyCalorieIn());
            pstmt.setDouble(11, userPref.getDailyCalorieOut());
            pstmt.setInt(12, userPref.getWeeklyActivityReps()); // weekly_activity_goal
            pstmt.setInt(13, userPref.getExerciseIntensity());  // exercise_intensity

            insertedRows = pstmt.executeUpdate();
            System.out.println(insertedRows + " row/s inserted in users table");
            if(insertedRows > 0){
                try(ResultSet rs = pstmt.getGeneratedKeys()){
                    if(rs.next()){
                        return rs.getInt(1); // Use column index for portability
                    }
                }
            }
        } catch(SQLException e){
            e.printStackTrace();
        } finally {
            System.out.println(insertedRows + " row/s inserted in userprefs table");
        }
        return -1;
    }
}
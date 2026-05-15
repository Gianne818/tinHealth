package org.tin.oop2_capstone.database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.tin.oop2_capstone.model.entities.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class RetrieveData {

    // ----------- Activity Retrieve Operations -----------

    public static List<Activity> fetchUserActivities(int userId){
        List<Activity> activities = new ArrayList<>();
        String query = """
            SELECT at.met_value, at.name, a.quantity, a.calories, a.log_timestamp
            FROM Activities a
            JOIN ActivityTypes at ON a.activity_type_id = at.activity_type_id
            WHERE a.user_id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                activities.add(new Activity(new ActivityType(rs.getString("name"), rs.getDouble("met_value")),
                        rs.getTimestamp("log_timestamp").toLocalDateTime(),
                        "minutes",
                        rs.getDouble("quantity"),
                        rs.getDouble("calories")
                ));
            }

            return activities;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return activities;
    }

    public static int fetchUserWeeklyWorkout(int userId){
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

    public static int fetchUserTotalActivities(int userId){
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

    public static int fetchUserCurrentStreak(int userId){
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

    public static int fetchUserTodayCaloriesOut(int userId){
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

    public static NutritionDetails fetchUserWeeklyNutrients(int userId){
        String query = """
        SELECT 
            COALESCE(SUM(nd.calories * m.serving_size), 0) AS total_calories,
            COALESCE(SUM(nd.protein * m.serving_size), 0) AS total_protein,
            COALESCE(SUM(nd.fats * m.serving_size), 0) AS total_fat,
            COALESCE(SUM(nd.carbs * m.serving_size), 0) AS total_carbs,
            COALESCE(SUM(nd.cholesterol * m.serving_size), 0) AS total_cholesterol,
            COALESCE(SUM(nd.sodium * m.serving_size), 0) AS total_sodium,
            COALESCE(SUM(nd.sugar * m.serving_size), 0) AS total_sugar,
            COALESCE(SUM(nd.fiber * m.serving_size), 0) AS total_fiber
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
                return new NutritionDetails(
                        rs.getDouble("total_calories"),
                        rs.getDouble("total_protein"),
                        rs.getDouble("total_fat"),
                        rs.getDouble("total_carbs"),
                        rs.getDouble("total_cholesterol"),
                        rs.getDouble("total_sodium"),
                        rs.getDouble("total_sugar"),
                        rs.getDouble("total_fiber")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, Double[]> fetchUserWeeklyCalories(int userId){
        Map<String, Double[]> weeklyData = new HashMap<>();
        String[] days = {"", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

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


                String dayName = days[day];
                weeklyData.put(dayName, new Double[]{caloriesIn, caloriesOut});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Fill missing days with zeros
        for (String day : days) {
            weeklyData.putIfAbsent(day, new Double[]{0.0, 0.0});
        }

        return weeklyData;
    }

    public static ObservableList<ActivityType> fetchActivityTypes(){
        String query = "SELECT * FROM ActivityTypes";
        ObservableList<ActivityType> activityTypes = FXCollections.observableArrayList();
        try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                activityTypes.add(new ActivityType(rs.getString("name"),
                        rs.getDouble("met_value")
                ));
            }
        }

        catch (SQLException e){
            e.printStackTrace();
        }
        return activityTypes;
    }

    public static List<Activity> fetchUserTodayActivities(int userId){
        List<Activity> activities = new ArrayList<>();
        String query = """
        SELECT at.met_value, at.name, a.quantity, a.calories, a.log_timestamp
        FROM Activities a
        JOIN ActivityTypes at ON a.activity_type_id = at.activity_type_id
        WHERE a.user_id = ? AND DATE(a.log_timestamp) = CURDATE()
        ORDER BY a.log_timestamp DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                activities.add(new Activity(new ActivityType(rs.getString("name"), rs.getDouble("met_value")),
                        rs.getTimestamp("log_timestamp").toLocalDateTime(),
                        "minutes",
                        rs.getDouble("quantity"),
                        rs.getDouble("calories")

                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return activities;
    }


    // ----------- Meal Retrieve Operations -----------

    public static double fetchUserTodayCaloriesIn(int userId){
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

    //test retrieve
    public static List<Meal> fetchUserMeals(int userId) {
        List<Meal> meals = new ArrayList<>();
        String query = """
            SELECT m.meal_type, c.name, m.serving_size, m.serving_units, m.log_timestamp,
                   nd.calories, nd.protein, nd.fats, nd.carbs, nd.cholesterol, nd.sodium, nd.sugar, nd.fiber
            FROM Meals m
            JOIN Consumables c ON m.consumable_id = c.consumable_id
            LEFT JOIN NutritionalDetails nd ON c.nutri_id = nd.nutri_id
            WHERE m.user_id = ?
            ORDER BY m.log_timestamp DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                NutritionDetails nd = new NutritionDetails(
                        rs.getDouble("calories"), rs.getDouble("protein"), rs.getDouble("fats"),
                        rs.getDouble("carbs"), rs.getDouble("cholesterol"), rs.getDouble("sodium"),
                        rs.getDouble("sugar"), rs.getDouble("fiber")
                );

                Food food = new Food(rs.getString("name"), nd, false);

                org.tin.oop2_capstone.model.entities.MealType type =
                        org.tin.oop2_capstone.model.entities.MealType.valueOf(rs.getString("meal_type").toUpperCase());

                meals.add(new Meal(
                        type, food, rs.getTimestamp("log_timestamp").toLocalDateTime(),
                        rs.getDouble("serving_size"), rs.getString("serving_units")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return meals;
    }


    // ----------- User and User Prefs Retrieve Operations -----------

    public static User fetchUser(String username, String password){
        String query = "SELECT * FROM Users WHERE username = ? AND password_hash = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                User user = new User();
                user.setUid(rs.getInt("user_id"));
                user.setFullname(rs.getString("fullname"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password_hash"));
                user.setAge(rs.getInt("age"));
                user.setMale("Male".equals(rs.getString("gender")));
                user.setWeightKg(rs.getDouble("weight_kg"));
                user.setHeightCm(rs.getDouble("height_cm"));
                user.setActivityLevel(rs.getString("activity_level"));

                return user;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int fetchUserPromptFrequency(int userId){
        String query = "SELECT prompt_freq FROM UserPrefs WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("prompt_freq");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 60; // default 60 minutes
    }

    public static int fetchUserDailyCalorieInGoal(int userId){
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

    public static List<Food> getPendingFoodsFromDB() {
        List<Food> pending = new ArrayList<>();
        String query = """
            SELECT c.consumable_id, c.name
            FROM Consumables c
            WHERE c.type = 'food'
            AND c.is_synced = FALSE
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                pending.add(new Food(
                        rs.getString("name"),
                        null,
                        true
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pending;
    }


}

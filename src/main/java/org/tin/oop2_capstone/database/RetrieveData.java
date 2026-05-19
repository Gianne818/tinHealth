package org.tin.oop2_capstone.database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.tin.oop2_capstone.model.entities.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class RetrieveData {

    // ----------- Activity Retrieve Operations -----------

    public static List<Activity> fetchUserActivities(int userId) {
        List<Activity> activities = new ArrayList<>();
        String query = """
                SELECT at.activity_type_id, at.met_value, at.name, a.quantity, a.calories, a.log_date, a.time
                FROM Activities a
                JOIN ActivityTypes at ON a.activity_type_id = at.activity_type_id
                WHERE a.user_id = ?
                ORDER BY a.log_date DESC, a.time DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LocalDate logDate = rs.getDate("log_date").toLocalDate();
                String timeStr = rs.getString("time");
                LocalTime time = LocalTime.MIDNIGHT;

                try {
                    if (timeStr != null && !timeStr.isEmpty()) {
                        time = LocalTime.parse(timeStr);
                    }
                } catch (Exception e) {
                    // Fallback to midnight if time parsing fails on unexpected ranges
                }

                activities.add(new Activity(
                        new ActivityType(rs.getInt("activity_type_id"), rs.getString("name"), rs.getDouble("met_value")),
                        LocalDateTime.of(logDate, time),
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

    public static int fetchUserWeeklyWorkout(int userId) {
        String query = """
                SELECT COUNT(*) AS workout_count
                FROM Activities
                WHERE user_id = ?
                AND YEARWEEK(log_date, 1) = YEARWEEK(CURDATE(), 1)
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

    public static int fetchUserTotalActivities(int userId) {
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

    public static int fetchUserCurrentStreak(int userId) {
        String query = """
                SELECT DISTINCT log_date AS activity_date
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

    public static int fetchUserTodayCaloriesOut(int userId) {
        String query = """
            SELECT COALESCE(SUM(calories), 0) AS total_calories
            FROM Activities
            WHERE user_id = ?
            AND log_date = CURDATE()
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

    public static NutritionDetails fetchUserWeeklyNutrients(int userId) {
        String query = """
            SELECT 
                COALESCE(SUM(nd.calories), 0) AS total_calories,
                COALESCE(SUM(nd.protein), 0) AS total_protein,
                COALESCE(SUM(nd.fats), 0) AS total_fat,
                COALESCE(SUM(nd.carbs), 0) AS total_carbs,
                COALESCE(SUM(nd.cholesterol), 0) AS total_cholesterol,
                COALESCE(SUM(nd.sodium), 0) AS total_sodium,
                COALESCE(SUM(nd.sugar), 0) AS total_sugar,
                COALESCE(SUM(nd.fiber), 0) AS total_fiber
            FROM Meals m
            JOIN Consumables c ON m.consumable_id = c.consumable_id
            LEFT JOIN NutritionalDetails nd ON c.nutri_id = nd.nutri_id
            WHERE m.user_id = ?
            AND YEARWEEK(m.log_date, 1) = YEARWEEK(CURDATE(), 1)
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

    public static Map<String, Double[]> fetchUserWeeklyCalories(int userId) {
        Map<String, Double[]> weeklyData = new HashMap<>();
        String[] days = {"", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

        // Initialize all days with 0.0
        for (String day : days) {
            if (!day.isEmpty()) weeklyData.put(day, new Double[]{0.0, 0.0});
        }

        // 1. Fetch Calories IN (Includes foodcombo logic)
        String inQuery = """
            SELECT DAYOFWEEK(m.log_date) AS day,
                   SUM(CASE 
                       WHEN c.type = 'food' THEN nd.calories
                       WHEN c.type = 'foodcombo' THEN (SELECT COALESCE(SUM(nd2.calories), 0) FROM ComboItems ci JOIN Consumables c2 ON ci.consumable_id = c2.consumable_id LEFT JOIN NutritionalDetails nd2 ON c2.nutri_id = nd2.nutri_id WHERE ci.combo_id = c.consumable_id)
                       ELSE 0 END) AS calories_in
            FROM Meals m JOIN Consumables c ON m.consumable_id = c.consumable_id LEFT JOIN NutritionalDetails nd ON c.nutri_id = nd.nutri_id
            WHERE m.user_id = ? AND YEARWEEK(m.log_date, 1) = YEARWEEK(CURDATE(), 1)
            GROUP BY DAYOFWEEK(m.log_date)
            """;

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(inQuery)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                weeklyData.get(days[rs.getInt("day")])[0] = rs.getDouble("calories_in");
            }
        } catch (SQLException e) { e.printStackTrace(); }

        // 2. Fetch Calories OUT (Independent of Meals)
        String outQuery = """
            SELECT DAYOFWEEK(log_date) AS day, SUM(calories) AS calories_out
            FROM Activities
            WHERE user_id = ? AND YEARWEEK(log_date, 1) = YEARWEEK(CURDATE(), 1)
            GROUP BY DAYOFWEEK(log_date)
            """;

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(outQuery)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                weeklyData.get(days[rs.getInt("day")])[1] = rs.getDouble("calories_out");
            }
        } catch (SQLException e) { e.printStackTrace(); }

        return weeklyData;
    }

    public static ObservableList<ActivityType> fetchActivityTypes() {
        String query = "SELECT * FROM ActivityTypes";
        ObservableList<ActivityType> activityTypes = FXCollections.observableArrayList();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                activityTypes.add(new ActivityType(
                        rs.getInt("activity_type_id"),
                        rs.getString("name"),
                        rs.getDouble("met_value")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return activityTypes;
    }

    public static List<Activity> fetchUserTodayActivities(int userId) {
        List<Activity> activities = new ArrayList<>();
        String query = """
            SELECT at.activity_type_id, at.met_value, at.name, a.quantity, a.calories, a.log_date, a.time
            FROM Activities a
            JOIN ActivityTypes at ON a.activity_type_id = at.activity_type_id
            WHERE a.user_id = ? AND a.log_date = CURDATE()
            ORDER BY a.time DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LocalDate logDate = rs.getDate("log_date").toLocalDate();
                String timeStr = rs.getString("time");
                LocalTime time = LocalTime.MIDNIGHT;

                try {
                    if (timeStr != null && !timeStr.isEmpty()) {
                        time = LocalTime.parse(timeStr);
                    }
                } catch (Exception e) {}

                activities.add(new Activity(
                        new ActivityType(rs.getInt("activity_type_id"), rs.getString("name"), rs.getDouble("met_value")),
                        LocalDateTime.of(logDate, time),
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

    public static double fetchUserTodayCaloriesIn(int userId) {
        String query = """
            SELECT COALESCE(SUM(
                CASE 
                    WHEN c.type = 'food' THEN nd.calories
                    WHEN c.type = 'foodcombo' THEN (
                        SELECT COALESCE(SUM(nd2.calories), 0)
                        FROM ComboItems ci
                        JOIN Consumables c2 ON ci.consumable_id = c2.consumable_id
                        LEFT JOIN NutritionalDetails nd2 ON c2.nutri_id = nd2.nutri_id
                        WHERE ci.combo_id = c.consumable_id
                    )
                    ELSE 0
                END
            ), 0) AS total_calories
            FROM Meals m
            JOIN Consumables c ON m.consumable_id = c.consumable_id
            LEFT JOIN NutritionalDetails nd ON c.nutri_id = nd.nutri_id
            WHERE m.user_id = ? AND m.log_date = CURDATE()
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

    public static List<Meal> fetchUserMeals(int userId) {
        List<Meal> meals = new ArrayList<>();
        String query = """
        SELECT m.meal_type, c.name, c.type, m.log_date, m.time,
               CASE 
                   WHEN c.type = 'food' THEN nd.calories
                   WHEN c.type = 'foodcombo' THEN (
                       SELECT COALESCE(SUM(nd2.calories), 0)
                       FROM ComboItems ci
                       JOIN Consumables c2 ON ci.consumable_id = c2.consumable_id
                       LEFT JOIN NutritionalDetails nd2 ON c2.nutri_id = nd2.nutri_id
                       WHERE ci.combo_id = c.consumable_id
                   )
                   ELSE 0
               END AS calories,
               CASE WHEN c.type = 'food' THEN nd.protein ELSE 0 END AS protein,
               CASE WHEN c.type = 'food' THEN nd.fats ELSE 0 END AS fats,
               CASE WHEN c.type = 'food' THEN nd.carbs ELSE 0 END AS carbs,
               CASE WHEN c.type = 'food' THEN nd.cholesterol ELSE 0 END AS cholesterol,
               CASE WHEN c.type = 'food' THEN nd.sodium ELSE 0 END AS sodium,
               CASE WHEN c.type = 'food' THEN nd.sugar ELSE 0 END AS sugar,
               CASE WHEN c.type = 'food' THEN nd.fiber ELSE 0 END AS fiber
        FROM Meals m
        JOIN Consumables c ON m.consumable_id = c.consumable_id
        LEFT JOIN NutritionalDetails nd ON c.nutri_id = nd.nutri_id
        WHERE m.user_id = ?
        ORDER BY m.log_date DESC, m.time DESC
    """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                NutritionDetails nd = new NutritionDetails(
                        rs.getDouble("calories"), rs.getDouble("protein"), rs.getDouble("fats"),
                        rs.getDouble("carbs"), rs.getDouble("cholesterol"), rs.getDouble("sodium"),
                        rs.getDouble("sugar"), rs.getDouble("fiber")
                );

                Food food = new Food(rs.getString("name"), nd, false);
                MealType type = MealType.valueOf(rs.getString("meal_type").toUpperCase());

                meals.add(new Meal(
                        type, food, rs.getDate("log_date").toLocalDate(), rs.getString("time")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return meals;
    }

    public static List<Meal> fetchUserMealsToday(int userId) {
        List<Meal> mealsToday = new ArrayList<>();
        String query = """
            SELECT m.meal_type, c.name, c.type, m.log_date, m.time,
                   CASE 
                       WHEN c.type = 'food' THEN nd.calories
                       WHEN c.type = 'foodcombo' THEN (
                           SELECT COALESCE(SUM(nd2.calories), 0)
                           FROM ComboItems ci
                           JOIN Consumables c2 ON ci.consumable_id = c2.consumable_id
                           LEFT JOIN NutritionalDetails nd2 ON c2.nutri_id = nd2.nutri_id
                           WHERE ci.combo_id = c.consumable_id
                       )
                       ELSE 0
                   END AS calories,
                   CASE WHEN c.type = 'food' THEN nd.protein ELSE 0 END AS protein,
                   CASE WHEN c.type = 'food' THEN nd.fats ELSE 0 END AS fats,
                   CASE WHEN c.type = 'food' THEN nd.carbs ELSE 0 END AS carbs,
                   CASE WHEN c.type = 'food' THEN nd.cholesterol ELSE 0 END AS cholesterol,
                   CASE WHEN c.type = 'food' THEN nd.sodium ELSE 0 END AS sodium,
                   CASE WHEN c.type = 'food' THEN nd.sugar ELSE 0 END AS sugar,
                   CASE WHEN c.type = 'food' THEN nd.fiber ELSE 0 END AS fiber
            FROM Meals m
            JOIN Consumables c ON m.consumable_id = c.consumable_id
            LEFT JOIN NutritionalDetails nd ON c.nutri_id = nd.nutri_id
            WHERE m.user_id = ? AND m.log_date = CURDATE()
            ORDER BY m.time DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                NutritionDetails nd = new NutritionDetails(
                        rs.getDouble("calories"), rs.getDouble("protein"), rs.getDouble("fats"),
                        rs.getDouble("carbs"), rs.getDouble("cholesterol"), rs.getDouble("sodium"),
                        rs.getDouble("sugar"), rs.getDouble("fiber")
                );

                Food food = new Food(rs.getString("name"), nd, false);
                MealType type = MealType.valueOf(rs.getString("meal_type").toUpperCase());

                mealsToday.add(new Meal(
                        type, food, rs.getDate("log_date").toLocalDate(), rs.getString("time")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mealsToday;
    }

    public static List<Meal> fetchWeeklyUserMeals(int userId) {
        List<Meal> mealsThisWeek = new ArrayList<>();
        String query = """
                SELECT m.meal_type, c.name, c.type, m.log_date, m.time,
                       CASE 
                           WHEN c.type = 'food' THEN nd.calories
                           WHEN c.type = 'foodcombo' THEN (
                               SELECT COALESCE(SUM(nd2.calories), 0)
                               FROM ComboItems ci
                               JOIN Consumables c2 ON ci.consumable_id = c2.consumable_id
                               LEFT JOIN NutritionalDetails nd2 ON c2.nutri_id = nd2.nutri_id
                               WHERE ci.combo_id = c.consumable_id
                           )
                           ELSE 0
                       END AS calories,
                       CASE WHEN c.type = 'food' THEN nd.protein ELSE 0 END AS protein,
                       CASE WHEN c.type = 'food' THEN nd.fats ELSE 0 END AS fats,
                       CASE WHEN c.type = 'food' THEN nd.carbs ELSE 0 END AS carbs,
                       CASE WHEN c.type = 'food' THEN nd.cholesterol ELSE 0 END AS cholesterol,
                       CASE WHEN c.type = 'food' THEN nd.sodium ELSE 0 END AS sodium,
                       CASE WHEN c.type = 'food' THEN nd.sugar ELSE 0 END AS sugar,
                       CASE WHEN c.type = 'food' THEN nd.fiber ELSE 0 END AS fiber
                FROM Meals m
                JOIN Consumables c ON m.consumable_id = c.consumable_id
                LEFT JOIN NutritionalDetails nd ON c.nutri_id = nd.nutri_id
                WHERE m.user_id = ?
                AND YEARWEEK(m.log_date, 1) = YEARWEEK(CURDATE(), 1)
                ORDER BY m.log_date DESC, m.time DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                NutritionDetails nd = new NutritionDetails(
                        rs.getDouble("calories"), rs.getDouble("protein"), rs.getDouble("fats"),
                        rs.getDouble("carbs"), rs.getDouble("cholesterol"), rs.getDouble("sodium"),
                        rs.getDouble("sugar"), rs.getDouble("fiber")
                );

                Food food = new Food(rs.getString("name"), nd, false);
                MealType type = MealType.valueOf(rs.getString("meal_type").toUpperCase());

                mealsThisWeek.add(new Meal(
                        type, food, rs.getDate("log_date").toLocalDate(), rs.getString("time")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mealsThisWeek;
    }

    // ----------- User and User Prefs Retrieve Operations -----------

    public static User fetchUser(String username, String password) {
        String query = "SELECT * FROM Users WHERE username = ? AND password_hash = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
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
                user.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
                user.setActivityLevel(rs.getString("activity_level"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static double fetchUserLatestWeight(int userId) {
        String query = "SELECT weight_kg FROM WeightHistories WHERE user_id = ? ORDER BY log_date DESC LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("weight_kg");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public static int fetchUserPromptFrequency(int userId) {
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

    public static int fetchUserDailyCalorieInGoal(int userId) {
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
                AND c.is_pending = TRUE
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                pending.add(new Food(rs.getString("name"), null, true));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pending;
    }

    public static UserPreferences fetchUserPreferences(int userId) {
        String query = "SELECT * FROM UserPrefs WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                UserPreferences prefs = new UserPreferences();
                prefs.setUserPrefID(rs.getInt("userpref_id"));
                prefs.setGoalType(rs.getString("goal"));
                prefs.setTargetWeightKG(rs.getDouble("target_weight_kg"));
                prefs.setEnableExercisePrompts(rs.getBoolean("enable_exercise_prompts"));

                // Column now exists in DB, it is safe to set
                prefs.setExerciseIntensity(rs.getInt("exercise_intensity"));

                prefs.setExerciseReminders(rs.getBoolean("exercise_reminders"));
                prefs.setMealReminders(rs.getBoolean("meal_reminders"));
                prefs.setAchievementNotifications(rs.getBoolean("achievement_notifications"));
                prefs.setPromptFrequencyMins(rs.getInt("prompt_freq"));
                prefs.setTheme(rs.getString("theme"));
                prefs.setDailyCalorieIn(rs.getDouble("daily_calorie_in"));
                prefs.setDailyCalorieOut(rs.getDouble("daily_calorie_out"));

                return prefs;
            }
        } catch (SQLException e) {
            System.out.println("Error while fetching user preferences: " + e.getMessage());
        }
        return null;
    }

    public static boolean checkUsername(String username) {
        String query = "SELECT 1 FROM Users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            // If rs.next() is true, it means a record was found (username is taken)
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return true on exception to prevent bypassing validation via database errors
        return true;
    }

    /**
     * Checks if an email already exists in the database.
     * @param email The email to check.
     * @return true if the email exists, false otherwise.
     */
    public static boolean checkEmail(String email) {
        String query = "SELECT 1 FROM Users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            // If rs.next() is true, it means a record was found (email is taken)
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return true on exception to prevent bypassing validation via database errors
        return true;
    }
}
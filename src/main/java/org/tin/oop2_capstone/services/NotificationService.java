package org.tin.oop2_capstone.services;

import org.tin.oop2_capstone.model.entities.NutritionDetails;
import org.tin.oop2_capstone.model.entities.UserPreferences;
import org.tin.oop2_capstone.model.entities.User;

import java.util.ArrayList;
import java.util.List;

public class NotificationService {

    public record Notification(String type, String title, String message) {}

    public static List<Notification> generateNotifications(
            User user,
            UserPreferences prefs,
            NutritionDetails todayNutrition,   // today's actual intake
            double caloriesOut,                // today's burned
            int weeklyWorkouts                 // this week's activity count
    ) {
        List<Notification> notifs = new ArrayList<>();
        NutritionDetails targets = prefs.getTargetMacros(user);

        // 1. Nutrient goals exceeded
        if (prefs.isAchievementNotifications()) {
            if (todayNutrition.getCalories() >= targets.getCalories()) {
                notifs.add(new Notification("goal",
                        "Calorie Intake Goal Reached",
                        String.format("You've hit your daily calorie goal of %.0f kcal. Watch your intake!", targets.getCalories())));
            }
            if (todayNutrition.getProtein() > targets.getProtein()) {
                notifs.add(new Notification("goal",
                        "Protein Limit Exceeded",
                        String.format("You've consumed %.1fg of protein, exceeding your %.1fg target.", todayNutrition.getProtein(), targets.getProtein())));
            }
            if (todayNutrition.getCarbs() > targets.getCarbs()) {
                notifs.add(new Notification("goal",
                        "Carbs Limit Exceeded",
                        String.format("You've consumed %.1fg of carbs, exceeding your %.1fg target.", todayNutrition.getCarbs(), targets.getCarbs())));
            }
            if (todayNutrition.getFat() > targets.getFat()) {
                notifs.add(new Notification("goal",
                        "Fat Limit Exceeded",
                        String.format("You've consumed %.1fg of fat, exceeding your %.1fg target.", todayNutrition.getFat(), targets.getFat())));
            }
            if (todayNutrition.getSugar() > targets.getSugar()) {
                notifs.add(new Notification("goal",
                        "Sugar Limit Exceeded",
                        String.format("You've consumed %.1fg of sugar, exceeding your %.1fg limit.", todayNutrition.getSugar(), targets.getSugar())));
            }

            // 2. Calorie burned goal reached
            if (caloriesOut >= prefs.getDailyCalorieOut()) {
                notifs.add(new Notification("achievement",
                        "Calorie Burn Goal Reached!",
                        String.format("You've burned %.0f kcal today. Your daily goal of %.0f kcal is complete!", caloriesOut, prefs.getDailyCalorieOut())));
            }

            // 3. Weekly activity goal reached
            if (weeklyWorkouts >= prefs.getWeeklyActivityReps()) {
                notifs.add(new Notification("achievement",
                        "Weekly Activity Goal Achieved!",
                        String.format("You've completed %d workouts this week, reaching your goal of %d. Great job!", weeklyWorkouts, prefs.getWeeklyActivityReps())));
            }
        }

        // 4. Activity reminder — no activity today
        if (prefs.isExerciseReminders() && caloriesOut == 0) {
            notifs.add(new Notification("reminder",
                    "Time to Move!",
                    "You haven't logged any activity today. Get moving to hit your calorie burn goal!"));
        }

        return notifs;
    }
}

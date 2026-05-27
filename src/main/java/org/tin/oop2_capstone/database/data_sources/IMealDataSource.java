package org.tin.oop2_capstone.database.data_sources;

import org.tin.oop2_capstone.model.entities.Meal;

import java.util.List;

public interface IMealDataSource {
    List<Meal> fetchUserMeals(int userId);
    double fetchUserTodayCalories(int userId);
    boolean insertMeal(int userId, Meal meal);
    List<Meal> fetchUserWeeklyMeals(int userId);
    List<Meal> fetchUserMealsToday(int userId);
}

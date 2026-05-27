package org.tin.oop2_capstone.database.data_sources;

import org.tin.oop2_capstone.database.InsertData;
import org.tin.oop2_capstone.database.RetrieveData;
import org.tin.oop2_capstone.model.entities.Meal;

import java.util.List;

public class MealDataSource implements IMealDataSource{
    @Override
    public List<Meal> fetchUserMeals(int userId) {
        return RetrieveData.fetchUserMeals(userId);
    }

    @Override
    public double fetchUserTodayCalories(int userId) {
        return RetrieveData.fetchUserTodayCaloriesIn(userId);
    }

    @Override
    public boolean insertMeal(int userId, Meal meal) {
        return InsertData.insertMeal(userId, meal);
    }

    @Override
    public List<Meal> fetchUserWeeklyMeals(int userId){
        return RetrieveData.fetchWeeklyUserMeals(userId);
    }

    @Override
    public List<Meal> fetchUserMealsToday(int userId){
        return RetrieveData.fetchUserMealsToday(userId);
    }
}

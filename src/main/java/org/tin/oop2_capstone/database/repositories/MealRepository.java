package org.tin.oop2_capstone.database.repositories;

import org.tin.oop2_capstone.database.InsertData;
import org.tin.oop2_capstone.database.RetrieveData;
import org.tin.oop2_capstone.database.data_sources.MealDataSource;
import org.tin.oop2_capstone.model.entities.Food;
import org.tin.oop2_capstone.model.entities.FoodCombo;
import org.tin.oop2_capstone.model.entities.Meal;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MealRepository {
    /**
     * Validate before inserting into database.
     * sometimes, the API returns a null, or maybe the user will input bad values
     */

    private List<Meal> userMeals;
    private List<Meal> userMealsToday;
    private  List<Meal> weeklyUserMeals;
    private final MealDataSource mealDataSource;

    public MealRepository(MealDataSource mealDataSource){
        this.mealDataSource = mealDataSource;
    }


    public void fetchInitialMealData(int userId) {
        userMealsToday = mealDataSource.fetchUserMealsToday(userId);
        weeklyUserMeals = mealDataSource.fetchUserWeeklyMeals(userId);
        userMeals = mealDataSource.fetchUserMeals(userId);
    }

    public List<Meal> getUserMeals() {
        return userMeals;
    }

    public List<Meal> getWeeklyUserMeals() {
        return weeklyUserMeals;
    }

    public double getTodayCaloriesIn(int userId) {
        return mealDataSource.fetchUserTodayCalories(userId);
    }

    public List<Meal> getUserMealsToday() {
        return userMealsToday;
    }

    public boolean addMeal(Meal meal, int userId) {
        if(mealDataSource.insertMeal(userId, meal)){
            if (userMeals != null) {
                userMealsToday.addFirst(meal);
                userMeals.add(0, meal); // Add to beginning of list
            } else {
                userMeals = new ArrayList<>();
                userMealsToday = new ArrayList<>();
                userMealsToday.add(meal);
                userMeals.add(meal);
            }
            return true;
        }
        return false;
    }

    public boolean deleteMeal(int mealId){
        userMeals.removeIf(meal -> meal.getMealId() == mealId);
        userMealsToday.removeIf(meal -> meal.getMealId() == mealId);
        weeklyUserMeals.removeIf(meal -> meal.getMealId() == mealId);
        return mealDataSource.deleteMeal(mealId);
    }
}
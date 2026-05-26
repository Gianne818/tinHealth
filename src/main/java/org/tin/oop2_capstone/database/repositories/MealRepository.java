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
    private final MealDataSource mealDataSource;

    public MealRepository(MealDataSource mealDataSource){
        this.mealDataSource = mealDataSource;
    }


    public void fetchInitialMealData(int userId) {
        mealDataSource.fetchUserTodayCalories(userId);
    }

    public List<Meal> getUserMeals() {
        return userMeals;
    }

    public double getTodayCaloriesIn(int userId) {
        return mealDataSource.fetchUserTodayCalories(userId);
    }

    public boolean addMeal(Meal meal, int userId) {
        if(mealDataSource.insertMeal(userId, meal)){
            if (userMeals != null) {
                userMeals.add(0, meal); // Add to beginning of list
            } else {
                userMeals = new ArrayList<>();
                userMeals.add(meal);
            }
            return true;
        }
        return false;
    }
}
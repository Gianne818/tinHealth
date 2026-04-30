package org.tin.oop2_capstone.model.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FoodLog {
    private List<Meal> meals;
    private LocalDate date;


    public FoodLog(LocalDate date){
        this.date = date;
        this.meals = new ArrayList<>();
    }

    public List<Meal> getMeals() {
        return meals;
    }

    public LocalDate getDate() {
        return date;
    }

    public void addMeal(Meal meal){
        if(meal != null) meals.add(meal);
    }

    public void setFoods(List<Meal> meals) {
        this.meals = meals;
    }
}

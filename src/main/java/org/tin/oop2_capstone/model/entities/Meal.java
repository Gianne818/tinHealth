package org.tin.oop2_capstone.model.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

/* todo: logTime should be calculated in our controller.
    example, If user inputs food and current time==12PM, then breakfast is selected, we default to time to 6-9 AM
    else we can do a current time
 */

public class Meal {
    private int mealId;
    private MealType mealType;
    private Consumable consumable;
    private LocalDate logDate;
    private String time;
    private NutritionDetails nutritionDetails;

    public Meal(MealType mealType, Consumable consumable, LocalDate logDate, String time) {
        this.mealType = mealType;
        this.consumable = consumable;
        this.logDate = logDate;
        this.time = time;
        this.nutritionDetails = calculateTotal(consumable);
    }

    private NutritionDetails calculateTotal(Consumable consumable){
        return consumable.getNutrition();
    }

    public MealType getMealType() { return mealType; }
    public Consumable getConsumable() { return consumable; }
    public LocalDate getLogDate() { return logDate; }
    public String getTime() { return time; }
    public NutritionDetails getNutritionDetails() { return nutritionDetails; }
    public int getMealId() { return mealId; }
    public void setMealId(int mealId){ this.mealId = mealId; }
}
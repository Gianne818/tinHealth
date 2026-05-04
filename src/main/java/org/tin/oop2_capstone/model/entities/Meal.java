package org.tin.oop2_capstone.model.entities;

import java.time.LocalDateTime;

/* todo: logTime should be calculated in our controller.
    example, If user inputs food and current time==12PM, then breakfast is selected, we default to time to 6-9 AM, but they can override this
 */

public class Meal {
    private MealType mealType;
    private Consumable consumable;
    private LocalDateTime logDateTime;
    private NutritionDetails nutritionDetails;
    private double quantity;
    private String unit;

    public Meal(MealType mealType, Consumable consumable, LocalDateTime logTime, double quantity, String unit) {
        this.mealType = mealType;
        this.consumable = consumable;
        this.logDateTime = logTime;
        this.nutritionDetails = calculateTotal(consumable);
        this.quantity = quantity;
        this.unit = unit;
    }
     private NutritionDetails calculateTotal(Consumable consumable){
        return consumable.getNutrition();
     }

    public String getUnit() {
        return unit;
    }

    public double getQuantity() {
        return quantity;
    }

    public MealType getMealType() {
        return mealType;
    }

    public Consumable getConsumable() {
        return consumable;
    }

    public LocalDateTime getLogDateTime() {
        return logDateTime;
    }

    public NutritionDetails getNutritionDetails() {
        return nutritionDetails;
    }
}

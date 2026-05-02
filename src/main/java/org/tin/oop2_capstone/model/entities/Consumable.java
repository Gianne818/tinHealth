package org.tin.oop2_capstone.model.entities;

public abstract class Consumable {
    /**
     * This is our composite pattern. Whether it be a Food or a FoodCombo, in our Meal, we just do getNutrition, or in SyncMonitor, just isPending.
     */
    private String name;


    public Consumable(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract NutritionDetails getNutrition();
    public abstract boolean isPending();
}

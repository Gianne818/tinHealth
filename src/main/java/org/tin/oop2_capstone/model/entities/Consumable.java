package org.tin.oop2_capstone.model.entities;

import java.util.List;

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

    public void setName(String name) {
        this.name = name;
    }

    public abstract List<Food> getConsumables();

    public abstract NutritionDetails getNutrition();
    public abstract boolean isPending();
}

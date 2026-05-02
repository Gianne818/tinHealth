package org.tin.oop2_capstone.services;

// todo: let this thing delegate the work of other classes, like call NutritionApiAdapter, Food and ACtivity Logger, etc

import org.tin.oop2_capstone.api.NutritionApiAdapter;
import org.tin.oop2_capstone.model.entities.Food;

public class NutritionFacade {

    private final NutritionApiAdapter nutritionApiAdapter;
    // todo: wer can i find DailyLogManager

    public NutritionFacade() {
        this.nutritionApiAdapter = new NutritionApiAdapter();
    }

    public Food searchFood(String query) {
        return nutritionApiAdapter.fetchFood(query);
    }
}

package org.tin.oop2_capstone.api;

//todo: his job will be like... call FoodAPI to get food data, then call FoodParser to parse the json, then return the food object to the controller

import org.tin.oop2_capstone.model.entities.Food;
import org.tin.oop2_capstone.services.FoodParser;

public class NutritionApiAdapter {

    public Food fetchFood(String query) {
        if (query == null || query.isBlank()){
            return null;
        }
        String json = FoodAPI.getFoodData(query);
        if (json == null){
            return null;
        }
        return FoodParser.parseFood(json);
    }
}

package org.tin.oop2_capstone;

import org.tin.oop2_capstone.model.entities.Food;
import org.tin.oop2_capstone.api.FoodAPI;
import org.tin.oop2_capstone.model.entities.FoodLog;
import org.tin.oop2_capstone.services.FoodParser;

public class Main {
    public static void main(String[] args) {

        FoodLog foodLog = new FoodLog();

        String[] queries = {"fish", "beef", "banana", "apple"};

        for (String query : queries) {
            String json = FoodAPI.getFoodData(query);
            Food food = FoodParser.parseFood(json);

            if (food != null) {
                foodLog.addFood(food);
            }
        }

        for (Food food : foodLog.getFoods()) {
            System.out.println(food);
            System.out.println();
        }
    }
}
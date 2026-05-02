package org.tin.oop2_capstone;

import org.tin.oop2_capstone.model.entities.Food;
import org.tin.oop2_capstone.api.FoodAPI;
import org.tin.oop2_capstone.model.entities.FoodLog;
import org.tin.oop2_capstone.model.entities.Meal;
import org.tin.oop2_capstone.services.FoodParser;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        List<Food> foods = new ArrayList<>();
        String[] queries = {"slice+of+bread", "beef", "banana", "apple"};

        for (String query : queries) {
            String json = FoodAPI.getFoodData(query);
            Food food = FoodParser.parseFood(json);


            if (food != null) {
                foods.add(food);
            }
        }

        for (Food food : foods) {
            System.out.println(food);
            System.out.println();
        }
    }
}
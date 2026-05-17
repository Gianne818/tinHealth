package org.tin.oop2_capstone;

import org.tin.oop2_capstone.model.entities.Food;
import org.tin.oop2_capstone.api.FoodAPI;
import org.tin.oop2_capstone.services.FoodParser;
import org.tin.oop2_capstone.services.SearchInterpreter;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        List<Food> foods = new ArrayList<>();
        String[] queries = {
                "white bread",
                "banana",
                "apple",
                "beef steak",
                "hotdog with rice",
                "croissant",
                "baguette",
                "oreo"
        };

        for (String query : queries) {

            // STEP 1: interpret input first
            List<String> interpreted = SearchInterpreter.interpret(query);

            // if no split detected, treat as single food
            if (interpreted.isEmpty()) {
                interpreted.add(query);
            }

            // STEP 2: process each food
            for (String foodQuery : interpreted) {

                String cleanedQuery = foodQuery.trim();
                String json = FoodAPI.getFoodData(cleanedQuery);
                Food food = FoodParser.parseFood(json, cleanedQuery);
                if (food != null) {
                    foods.add(food);
                }
            }
        }


        for (Food food : foods) {
            System.out.println(food);
            System.out.println();
        }
    }
}
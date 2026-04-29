package org.tin.oop2_capstone.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.tin.oop2_capstone.model.entities.Food;
import org.tin.oop2_capstone.model.entities.FoodLog;
import org.tin.oop2_capstone.model.entities.NutritionDetails;

public class FoodParser {



    public static Food parseFood(String json) {


        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
        JsonArray foods = obj.getAsJsonArray("foods");
        if (foods == null || foods.isEmpty()) return null;

        JsonObject firstFood = foods.get(0).getAsJsonObject();
        String name = firstFood.get("description").getAsString();
        JsonArray nutrients = firstFood.getAsJsonArray("foodNutrients");

        double calories = 0, protein = 0, fat = 0, carbs = 0;
        double cholesterol = 0, sodium = 0, sugar = 0, fiber = 0;

        if (nutrients != null) {
            for (int i = 0; i < nutrients.size(); i++) {
                JsonObject nutrient = nutrients.get(i).getAsJsonObject();

                String nutrientName = nutrient.get("nutrientName")
                        .getAsString().toLowerCase();

                double value = nutrient.get("value").getAsDouble();

                switch (nutrientName) {
                    case "energy":
                        calories = value;
                        break;
                    case "protein":
                        protein = value;
                        break;
                    case "total lipid (fat)":
                        fat = value;
                        break;
                    case "carbohydrate, by difference":
                        carbs = value;
                        break;
                    case "cholesterol":
                        cholesterol = value;
                        break;
                    case "sodium, na":
                        sodium = value;
                        break;
                    case "sugars, total including nlea":
                        sugar = value;
                        break;
                    case "fiber, total dietary":
                        fiber = value;
                        break;
                }
            }
        }

       return new Food(name, new NutritionDetails(calories, protein, fat, carbs,
                cholesterol, sodium, sugar, fiber));

    }

}
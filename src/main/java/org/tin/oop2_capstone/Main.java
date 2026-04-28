package org.tin.oop2_capstone;

import org.tin.oop2_capstone.model.entities.Food;
import org.tin.oop2_capstone.api.FoodAPI;
import org.tin.oop2_capstone.services.FoodParser;

public class Main {
    public static void main(String[] args) {
        String json = FoodAPI.getFoodData("banana");
        Food food = FoodParser.parseFood(json);
        System.out.println(food);
    }
}
package org.tin.oop2_capstone;

import org.tin.oop2_capstone.classes.Food;
import org.tin.oop2_capstone.utils.FoodAPI;
import org.tin.oop2_capstone.utils.FoodParser;

public class Main {
    public static void main(String[] args) {
        String json = FoodAPI.getFoodData("banana");
        Food food = FoodParser.parseFood(json);
        System.out.println(food);
    }
}
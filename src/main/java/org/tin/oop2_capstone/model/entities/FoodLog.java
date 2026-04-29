package org.tin.oop2_capstone.model.entities;

import java.util.ArrayList;
import java.util.List;



public class FoodLog {
    private List<Food> foods;

    public FoodLog(){
        this.foods = new ArrayList<>();
    }

    public List<Food> getFoods() {
        return foods;
    }

    public void addFood(Food food){
        if(food != null) foods.add(food);
    }

    public void setFoods(List<Food> foods) {
        this.foods = foods;
    }
}

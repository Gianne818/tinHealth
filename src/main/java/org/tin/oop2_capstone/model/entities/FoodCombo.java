package org.tin.oop2_capstone.model.entities;

import java.util.List;

/**
 * FoodCombo will be for composite foods, in order for us to query them separately
 * If the user inputs Oreos with Milk, we get the Nutrients for both separately.
 * It will be stored as foods[0] = Orea, foods[1] = Milk
 */

public class FoodCombo extends Consumable {
    // name will be the raw user input, like "Burger with Cheese"

    private List<Food> foods;
    private NutritionDetails nutrition;


    public FoodCombo(String name, List<Food> foods) {
        super(name);
        this.foods = foods;
        this.nutrition = getNutrition();
    }

    @Override
    public NutritionDetails getNutrition() {
        double calories = 0, protein = 0, fat = 0, carbs = 0, cholesterol = 0, sodium = 0, sugar = 0, fiber = 0;
        for(Food f : foods){
            calories += f.getNutrition().getCalories();
            protein += f.getNutrition().getProtein();
            fat += f.getNutrition().getFat();
            carbs += f.getNutrition().getCarbs();
            cholesterol += f.getNutrition().getCholesterol();
            sodium += f.getNutrition().getSodium();
            sugar += f.getNutrition().getSugar();
            fiber += f.getNutrition().getFiber();
            // todo: for other nutritions stuff. Create new nutrition object and return it.
            System.out.println(f);
        }
        return new NutritionDetails(calories, protein, fat, carbs, cholesterol, sodium, sugar, fiber);
    }

    @Override
    public boolean isPending() {
        for(Food f : foods){
            if(f.isPending()){
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Food> getConsumables() {
        return foods;
    }

    @Override
    public String getName() {
        return super.getName();
    }
}

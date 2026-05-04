package org.tin.oop2_capstone.model.entities;

/**
 * FoodCombo will be for composite foods, in order for us to query them separately
 * If the user inputs Oreos with Milk, we get the Nutrients for both separately.
 * It will be stored as foods[0] = Orea, foods[1] = Milk
 */

public class FoodCombo extends Consumable {
    // name will be the raw user input, like "Burger with Cheese"

    private Food[] foods;
    private NutritionDetails nutrition;


    public FoodCombo(String name, Food[] foods) {
        super(name);
        this.foods = foods;
        this.nutrition = getNutrition();
    }

    @Override
    public NutritionDetails getNutrition() {
        for(Food f : foods){
            double calories = f.getNutrition().getCalories();
            // todo: for other nutritions stuff. Create new nutrition object and return it.
        }
        return null;
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
    public String getName() {
        return super.getName();
    }
}

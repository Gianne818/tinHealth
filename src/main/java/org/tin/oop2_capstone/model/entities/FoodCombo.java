package org.tin.oop2_capstone.model.entities;

public class FoodCombo extends Consumable {
    // name will be the raw user input, like "2 hotdogs, 1 rice"

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

package org.tin.oop2_capstone.model.entities;

public class Food extends Consumable{
    private NutritionDetails nutrition;
    private boolean isPending;


    public Food(String name, NutritionDetails nutrition, boolean isPending){
        super(name);
        this.nutrition = nutrition;
        this.isPending = isPending;
    }

    public void setPending(boolean pending) {
        isPending = pending;
    }

    public String getName() {
        return super.getName();
    }


    @Override
    public NutritionDetails getNutrition() {
        return nutrition; }

    @Override
    public boolean isPending() {
        return isPending;
    }

    // removed getters for each individual nutrition to avoid making Food a godclass
    // if we add or removed new nutritions, we'd need to also change this Food class, redundant and violates SRP

    @Override
    public String toString() {
        return  "name='" + super.getName() + '\'' +
                ", \ncalories=" + nutrition.getCalories() +
                ", \nprotein=" + nutrition.getProtein() +
                ", \nfat=" + nutrition.getFat() +
                ", \ncarbs=" + nutrition.getCarbs() +
                ", \ncholesterol=" + nutrition.getCholesterol() +
                ", \nsodium=" + nutrition.getSodium() +
                ", \nsugar=" + nutrition.getSugar() +
                ", \nfiber=" + nutrition.getFiber();
    }
}
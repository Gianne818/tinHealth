package org.tin.oop2_capstone.model.entities;

public class Food {

    private String name;
    private NutritionDetails nutrition;


    public Food(String name, NutritionDetails nutrition){
        this.name = name;
        this.nutrition = nutrition;
    }

    public String getName() { return name; }
    public double getCalories() { return nutrition.getCalories(); }
    public double getProtein() { return nutrition.getProtein(); }
    public double getFat() { return nutrition.getFat(); }
    public double getCarbohydrates() { return nutrition.getCarbs(); }
    public double getCholesterol() { return nutrition.getCholesterol(); }
    public double getSodium() { return nutrition.getSodium(); }
    public double getSugar() { return nutrition.getSugar(); }
    public double getFiber() { return nutrition.getFiber(); }

    @Override
    public String toString() {
        return  "name='" + name + '\'' +
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
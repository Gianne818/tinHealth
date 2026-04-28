package org.tin.oop2_capstone.model.entities;

public class Food {

    private String name;
    private double calories;
    private double protein;
    private double fat;
    private double carbohydrates;
    private double cholesterol;
    private double sodium;
    private double sugar;
    private double fiber;


    public Food(String name, double calories, double protein, double fat,
                double carbohydrates, double cholesterol, double sodium,
                double sugar, double fiber) {
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbohydrates = carbohydrates;
        this.cholesterol = cholesterol;
        this.sodium = sodium;
        this.sugar = sugar;
        this.fiber = fiber;
    }


    public String getName() { return name; }
    public double getCalories() { return calories; }
    public double getProtein() { return protein; }
    public double getFat() { return fat; }
    public double getCarbohydrates() { return carbohydrates; }
    public double getCholesterol() { return cholesterol; }
    public double getSodium() { return sodium; }
    public double getSugar() { return sugar; }
    public double getFiber() { return fiber; }

//    public void setName(String name) { this.name = name; }
//    public void setCalories(double calories) { this.calories = calories; }
//    public void setProtein(double protein) { this.protein = protein; }
//    public void setFat(double fat) { this.fat = fat; }
//    public void setCarbohydrates(double carbohydrates) { this.carbohydrates = carbohydrates; }
//    public void setCholesterol(double cholesterol) { this.cholesterol = cholesterol; }
//    public void setSodium(double sodium) { this.sodium = sodium; }
//    public void setSugar(double sugar) { this.sugar = sugar; }
//    public void setFiber(double fiber) { this.fiber = fiber; }

    @Override
    public String toString() {
        return  "name='" + name + '\'' +
                ", \ncalories=" + calories +
                ", \nprotein=" + protein +
                ", \nfat=" + fat +
                ", \ncarbs=" + carbohydrates +
                ", \ncholesterol=" + cholesterol +
                ", \nsodium=" + sodium +
                ", \nsugar=" + sugar +
                ", \nfiber=" + fiber;
    }
}
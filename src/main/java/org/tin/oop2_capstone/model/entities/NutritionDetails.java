package org.tin.oop2_capstone.model.entities;

public class NutritionDetails {
    private double calories;
    private double protein;
    private double fat;
    private double carbs;
    private double cholesterol;
    private double sodium;
    private double sugar;
    private double fiber;

    public NutritionDetails(double calories, double protein, double fat,
                            double carbs, double cholesterol, double sodium,
                            double sugar, double fiber) {
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
        this.cholesterol = cholesterol;
        this.sodium = sodium;
        this.sugar = sugar;
        this.fiber = fiber;
    }

    public double getCalories() { return calories; }
    public double getProtein() { return protein; }
    public double getFat() { return fat; }
    public double getCarbs() { return carbs; }
    public double getCholesterol() { return cholesterol; }
    public double getSodium() { return sodium; }
    public double getSugar() { return sugar; }
    public double getFiber() { return fiber; }
}

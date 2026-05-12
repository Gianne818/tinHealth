package org.tin.oop2_capstone.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.tin.oop2_capstone.database.repositories.MealRepository;
import org.tin.oop2_capstone.model.entities.Meal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.tin.oop2_capstone.database.repositories.MealRepository.getInstance;
import static org.tin.oop2_capstone.database.repositories.MealRepository.instance;

public class ToolTipController {
    @FXML Label dayLabel;
    @FXML Label calInLabel;
    @FXML Label calOutLabel;
    @FXML Label proteinLabel;
    @FXML Label fatLabel;
    @FXML Label cholesterolLabel;
    @FXML Label sodiumLabel;
    @FXML Label sugarLabel;
    @FXML Label fiberLabel;

    List<String> macroNames= List.of(new String[]{"protein", "fat", "cholesterol", "sodium", "sugar", "fiber"});

    List<Map<String, String>> macros = new ArrayList<>();

    public void setData(String day, Number in, Number out){
        if(day == "Mon") day = "Monday";
        else if(day == "Tue") day = "Tuesday";
        else if(day == "Wed") day = "Wednesday";
        else if(day == "Thu") day = "Thursday";
        else if(day == "Fri") day = "Friday";
        else if(day == "Sat") day = "Saturday";
        else day = "Sunday";

        dayLabel.setText(day);
        calInLabel.setText(in + " kcal");
        calOutLabel.setText(out + " kcal");

        //test
        List<Meal> userMeals = instance.getUserMeals();
        Double protein = 0.0, fat = 0.0, cholesterol = 0.0, sodium = 0.0, sugar = 0.0, fiber  = 0.0;
            for(Meal m : userMeals){
                protein += m.getNutritionDetails().get("protein");
                fat += m.getNutritionDetails().get("fat");
                cholesterol += m.getNutritionDetails().get("cholesterol");
                sodium += m.getNutritionDetails().get("sodium");
                sugar += m.getNutritionDetails().get("sugar");
                fiber += m.getNutritionDetails().get("fiber");
            }
            List<Double> macroValues= new ArrayList<>();
            macroValues.add(protein);
            macroValues.add(fat);
            macroValues.add(cholesterol);
            macroValues.add(sodium);
            macroValues.add(sugar);
            macroValues.add(fiber);
            for(int i =0; i<7; i++){
                Map<String, String> tmp = new HashMap<>();
                tmp.put(macroNames.get(i), macroValues.get(i)+"");
                macros.add(tmp);
            }

        }
    }

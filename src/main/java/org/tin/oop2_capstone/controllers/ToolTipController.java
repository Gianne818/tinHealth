package org.tin.oop2_capstone.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.tin.oop2_capstone.database.RetrieveData;
import org.tin.oop2_capstone.database.repositories.MealRepository;
import org.tin.oop2_capstone.model.entities.Meal;
import org.tin.oop2_capstone.model.entities.NutritionDetails;
import org.tin.oop2_capstone.services.SessionManager;

import java.util.List;

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

        //Macros Display
        List<Meal> userMeals = RetrieveData.fetchUserMealsToday(SessionManager.getInstance().getCurrentUser().getUid());
        double protein = 0.0, fat = 0.0, cholesterol = 0.0, sodium = 0.0, sugar = 0.0, fiber = 0.0;

        for (Meal m : userMeals) {
            NutritionDetails nd = m.getNutritionDetails();
            if (nd != null) {
                protein += nd.getProtein();
                fat += nd.getFat();
                cholesterol += nd.getCholesterol();
                sodium += nd.getSodium();
                sugar += nd.getSugar();
                fiber += nd.getFiber();
            }
        }

        proteinLabel.setText(String.format("%.1f g", protein));
        fatLabel.setText(String.format("%.1f g", fat));
        cholesterolLabel.setText(String.format("%.1f mg", cholesterol));
        sodiumLabel.setText(String.format("%.1f mg", sodium));
        sugarLabel.setText(String.format("%.1f g", sugar));
        fiberLabel.setText(String.format("%.1f g", fiber));

        Label[] macroLabels = {proteinLabel, fatLabel, cholesterolLabel, sodiumLabel, sugarLabel, fiberLabel};

        for (Label label : macroLabels) {
            javafx.scene.Node parentContainer = label.getParent(); //Basically gets the entire GridPane the label is in
            if (label.getText().startsWith("0.0")) { //Checks if value is 0
                parentContainer.setVisible(false);
                parentContainer.setManaged(false);
            } else {
                parentContainer.setVisible(true);
                parentContainer.setManaged(true);
            }
        }

        }


    }

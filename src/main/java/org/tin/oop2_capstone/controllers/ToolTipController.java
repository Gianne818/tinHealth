package org.tin.oop2_capstone.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

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

    public void setData(String day, double cals, double protein, double carbs,
                        double fat, double cholesterol, double sodium,
                        double sugar, double fiber) {
        setDayLabel(day);
        calInLabel.setText(String.format("%.0f kcal", cals));
        calOutLabel.setText("--");

        proteinLabel.setText(String.format("%.1f g", protein));
        fatLabel.setText(String.format("%.1f g", fat));
        cholesterolLabel.setText(String.format("%.1f mg", cholesterol));
        sodiumLabel.setText(String.format("%.1f mg", sodium));
        sugarLabel.setText(String.format("%.1f g", sugar));
        fiberLabel.setText(String.format("%.1f g", fiber));

        Label[] macroLabels = {proteinLabel, fatLabel, cholesterolLabel,
                sodiumLabel, sugarLabel, fiberLabel};
        for (Label label : macroLabels) {
            javafx.scene.Node parent = label.getParent();
            boolean isEmpty = label.getText().startsWith("0.0");
            parent.setVisible(!isEmpty);
            parent.setManaged(!isEmpty);
        }
    }

    //Overloaded method version for setData for exclusive dashboard controller use
    public void setData(String day, Number in, Number out) {
        setDayLabel(day);
        calInLabel.setText(in + " kcal");
        calOutLabel.setText(out + " kcal");

        // Hide all macro rows — dashboard tooltip doesn't show them
        Label[] macroLabels = {proteinLabel, fatLabel, cholesterolLabel,
                sodiumLabel, sugarLabel, fiberLabel};
        for (Label label : macroLabels) {
            javafx.scene.Node parent = label.getParent();
            parent.setVisible(false);
            parent.setManaged(false);
        }
    }

    //Helper for setData(Dashboard)
    private void setDayLabel(String day) {
        switch (day) {
            case "Mon": dayLabel.setText("Monday");    break;
            case "Tue": dayLabel.setText("Tuesday");   break;
            case "Wed": dayLabel.setText("Wednesday"); break;
            case "Thu": dayLabel.setText("Thursday");  break;
            case "Fri": dayLabel.setText("Friday");    break;
            case "Sat": dayLabel.setText("Saturday");  break;
            default:    dayLabel.setText("Sunday");    break;
        }
    }


    }

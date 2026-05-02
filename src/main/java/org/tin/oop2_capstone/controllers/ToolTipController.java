package org.tin.oop2_capstone.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ToolTipController {
    @FXML Label dayLabel;
    @FXML Label calInLabel;
    @FXML Label calOutLabel;
    @FXML Label macroLabel;
    @FXML Label macroPercentageLabel;

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
    }

    public void setMacroData(String name, double percentage) {
        macroLabel.setText(name);
        macroPercentageLabel.setText(String.format("%.1f%%", percentage));
    }
}
